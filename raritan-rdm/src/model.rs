use eyre::Result;
use quick_xml::de::from_str;
use serde::Deserialize;

#[derive(Debug, Clone, Deserialize, PartialEq)]
#[serde(rename_all = "PascalCase")]
pub struct Port {
    #[serde(rename = "@id")]
    pub id: String,
    #[serde(rename = "@Class", default)]
    pub class: Option<String>,
    #[serde(rename = "@Type", default)]
    pub r#type: Option<String>,
    #[serde(rename = "@index", default)]
    pub index: Option<i32>,
    /// Switch status (0 = down, 1 = up, 2 = busy); the real state combines
    /// this with `@StatAvailable` — see [`Port::is_busy`].
    #[serde(rename = "@Status", default)]
    pub status: Option<i32>,
    #[serde(rename = "@StatAvailable", default)]
    pub stat_available: Option<i32>,
    #[serde(rename = "@Connection", default)]
    pub connection: Option<String>,
    #[serde(rename = "DeviceID", default)]
    pub device_id: Option<String>,
    pub name: Option<String>,
}

pub(crate) fn parse_ports(xml: &str) -> Result<Vec<Port>> {
    Ok(from_str::<PortDocument>(xml)?.into_ports())
}

impl Port {
    /// Physical port number as printed on the switch (1-indexed).
    /// The wire `@index` is 0-indexed, so the UI shows `index + 1`.
    pub fn display_index(&self) -> String {
        self.index
            .map_or_else(|| "?".to_owned(), |value| (value.saturating_add(1)).to_string())
    }
    /// Ports worth listing: available (1) or busy (2). Unavailable (0)
    /// and unknown statuses stay hidden, like the Java client.
    pub fn is_listed(&self) -> bool {
        matches!(self.status, Some(1 | 2))
    }

    /// Busy (status 2): connected elsewhere, shown with a marker.
    pub fn is_busy(&self) -> bool {
        self.busy_tooltip().is_some()
    }
    /// Occupied marker, mirroring Java's availability states, which combine
    /// `@Status` with `@StatAvailable`. `None` when free.
    pub fn busy_tooltip(&self) -> Option<&'static str> {
        match self.stat_available {
            Some(1 | 2) => Some("Someone else is connected to this port"),
            Some(4) => Some("All channels are in use"),
            // No StatAvailable: raw status stands alone (legacy path).
            _ if self.status == Some(2) => Some("Someone else is connected to this port"),
            _ => None,
        }
    }
}

#[derive(Debug, Default, Deserialize)]
#[serde(rename_all = "PascalCase")]
struct PortDocument {
    #[serde(default)]
    port: Vec<Port>,
    #[serde(default)]
    device: Vec<PortDocument>,
    #[serde(default)]
    get: Vec<PortDocument>,
    #[serde(default)]
    data: Vec<PortDocument>,
    #[serde(default)]
    database: Vec<PortDocument>,
    #[serde(default)]
    response: Vec<PortDocument>,
}

impl PortDocument {
    fn into_ports(self) -> Vec<Port> {
        let mut ports = self.port;
        for child in self
            .device
            .into_iter()
            .chain(self.get)
            .chain(self.data)
            .chain(self.database)
            .chain(self.response)
        {
            ports.extend(child.into_ports());
        }
        ports
    }
}

/// Switch identity from `<CSC_Info>`. All fields optional: firmware varies,
/// and a missing field must never break the connection.
#[derive(Debug, Clone, Default, Deserialize, PartialEq)]
#[serde(rename_all = "PascalCase")]
pub struct SwitchInfo {
    #[serde(rename = "@Type", default)]
    pub device_type: Option<String>,
    #[serde(rename = "@id", default)]
    pub device_id: Option<String>,
    #[serde(rename = "@Model", default)]
    pub model: Option<String>,
    #[serde(rename = "@Version", default)]
    pub version: Option<String>,
    #[serde(default)]
    pub name: Option<String>,
    #[serde(default)]
    pub hostname: Option<String>,
    #[serde(rename = "IPAddress", default)]
    pub ip_address: Option<String>,
}

#[derive(Debug, Default, Deserialize)]
#[serde(rename_all = "PascalCase")]
struct CscInfo {
    #[serde(default)]
    device: Option<SwitchInfo>,
}

/// Parses `<CSC_Info>`; unparseable payloads yield an empty struct, never an error.
pub(crate) fn parse_switch_info(xml: &str) -> SwitchInfo {
    from_str::<CscInfo>(xml)
        .map(|info| info.device.unwrap_or_default())
        .unwrap_or_default()
}

#[derive(Debug, Deserialize)]
pub(crate) struct SessionResponse {
    #[serde(rename = "GetSessionID")]
    pub get_session_id: SessionData,
}

#[derive(Debug, Deserialize)]
// Field names follow the XML (`SessionID` vs `SessionID` element).
#[expect(clippy::struct_field_names)]
pub(crate) struct SessionData {
    #[serde(rename = "@SessionID", default)]
    pub session_id: Option<String>,
    #[serde(rename = "SessionID", default)]
    pub session_id_element: Option<String>,
    #[serde(rename = "@SessionKey", default)]
    pub session_key: Option<String>,
    #[serde(rename = "SessionKey", default)]
    pub session_key_element: Option<String>,
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn parses_nested_port_document() {
        let ports = parse_ports(
            r#"<Database><Get><Data><Device id="D_1"><Port id="P_1" Class="KVM" Type="VM" index="2" Status="1" StatAvailable="1" Connection="D_1"><Name>Rack &amp; 1</Name></Port></Device></Data></Get></Database>"#,
        )
        .unwrap();
        assert_eq!(ports[0].name.as_deref(), Some("Rack & 1"));
        assert_eq!(ports[0].connection.as_deref(), Some("D_1"));
    }

    #[test]
    fn parses_csc_info_device() {
        let info = parse_switch_info(
            r#"<CSC_Info><Device Type="Dominion_KX2" id="DKX2_1" Model="DKX2-464" Version="2.7.0.5.2183" ProductCode="HKF"><Name>raritan01</Name><Hostname></Hostname><IPAddress>192.0.2.10</IPAddress></Device></CSC_Info>"#,
        );
        assert_eq!(info.device_type.as_deref(), Some("Dominion_KX2"));
        assert_eq!(info.model.as_deref(), Some("DKX2-464"));
        assert_eq!(info.version.as_deref(), Some("2.7.0.5.2183"));
        assert_eq!(info.name.as_deref(), Some("raritan01"));
        assert_eq!(info.ip_address.as_deref(), Some("192.0.2.10"));
    }

    #[test]
    fn garbage_csc_info_yields_empty() {
        assert_eq!(parse_switch_info(""), SwitchInfo::default());
        assert_eq!(parse_switch_info("<CSC_Info/>"), SwitchInfo::default());
    }

    #[test]
    fn busy_ports_are_listed_and_flagged() {
        let ports = parse_ports(
            r#"<Database><Get><Data><Device id="D_1"><Port id="P_5" Class="KVM" index="5" Status="1" StatAvailable="0"><Name>IDLE-BOX</Name></Port><Port id="P_7" Class="KVM" index="7" Status="1" StatAvailable="2"><Name>LAILA-W14</Name></Port><Port id="P_8" Class="KVM" index="8" Status="1" StatAvailable="1"><Name>SHARED-BOX</Name></Port><Port id="P_9" Class="KVM" index="9" Status="1" StatAvailable="4"><Name>FULL-BOX</Name></Port><Port id="P_10" Class="KVM" index="10" Status="1" StatAvailable="3"><Name>DOWN-BOX</Name></Port><Port id="P_11" Class="KVM" index="11" Status="0" StatAvailable="2"><Name>DEAD-BOX</Name></Port><Port id="P_12" Class="KVM" index="12" Status="2"><Name>LEGACY-BOX</Name></Port></Device></Data></Get></Database>"#,
        )
        .unwrap();
        let listed: Vec<&str> = ports
            .iter()
            .filter(|port| port.is_listed())
            .map(|port| port.id.as_str())
            .collect();
        assert_eq!(listed, ["P_5", "P_7", "P_8", "P_9", "P_10", "P_12"]);
        let laila = ports.iter().find(|port| port.id == "P_7").unwrap();
        assert_eq!(laila.index, Some(7));
        assert!(laila.is_busy());
        assert_eq!(
            laila.busy_tooltip(),
            Some("Someone else is connected to this port")
        );
        assert!(!ports[0].is_busy());
        assert_eq!(ports[0].busy_tooltip(), None);
        // Down ports stay hidden even with a busy-looking StatAvailable.
        assert!(
            !ports
                .iter()
                .find(|port| port.id == "P_11")
                .unwrap()
                .is_listed()
        );
    }
}
