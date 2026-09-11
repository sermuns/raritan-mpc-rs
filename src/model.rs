use eyre::Result;
use quick_xml::de::from_str;
use serde::Deserialize;

#[derive(Debug, Deserialize, PartialEq)]
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
    #[serde(rename = "@Status", default)]
    pub status: Option<i32>,
    #[serde(rename = "@StatAvailable", default)]
    pub stat_available: Option<i32>,
    #[serde(rename = "@Connection", default)]
    pub connection: Option<String>,
    pub name: Option<String>,
}

pub(crate) fn parse_ports(xml: &str) -> Result<Vec<Port>> {
    Ok(from_str::<PortDocument>(xml)?.into_ports())
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

#[derive(Debug, Deserialize)]
pub(crate) struct SessionResponse {
    #[serde(rename = "GetSessionID")]
    pub get_session_id: SessionData,
}

#[derive(Debug, Deserialize)]
pub(crate) struct SessionData {
    #[serde(rename = "SessionID", default)]
    pub session_id: Option<String>,
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
}
