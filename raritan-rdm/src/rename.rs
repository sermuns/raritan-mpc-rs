//! Port rename via the `WebUI` edit form.
//!
//! The Java client has no rename feature and the RDM channel has no `Set`
//! verb (only `<Database><Get>`/`<Count>` appear in the decompiled
//! client), so renames go through the same HTTPS form the `WebUI` posts:
//! `POST /port_edit.asp` with the new name in `FV_0_portedit` (maxlength
//! 32). Verified live against `192.168.42.10`: full-form replay and the
//! minimal field set below both rename, confirmed by re-reading the port
//! list over RDM.
//!
//! Only standard KVM ports (RDM `Type="VM"` ↔ `WebUI` subtype `Dual-VM`)
//! are supported: other subtypes (blade chassis, KVM switch, power) own
//! extra form sections whose defaults we do not reconstruct, so posting a
//! rename for them could clobber data. Success is verified by re-reading
//! the port over RDM — never from the POST response, which the switch
//! may truncate with an abrupt TLS close.

use crate::{Port, RdmClient};
use eyre::{OptionExt, bail};
use raritan_web::{WebSession, form_urlencode};
use tracing::warn;

/// `FV_0_portedit` maxlength in the `WebUI` form.
pub const MAX_PORT_NAME_LEN: usize = 32;

/// Builds the minimal `port_edit.asp` field set for a standard KVM port.
/// Field names/values mirror the browser capture byte-for-byte except
/// `FV_0_portedit`.
pub fn rename_fields(port: &Port, new_name: &str) -> eyre::Result<Vec<(String, String)>> {
    if new_name.is_empty() {
        bail!("new port name must not be empty");
    }
    if new_name.chars().count() > MAX_PORT_NAME_LEN {
        bail!("new port name exceeds {MAX_PORT_NAME_LEN} characters");
    }
    if port.r#type.as_deref() != Some("VM") {
        bail!(
            "renaming ports of type {:?} is not supported (only VM/Dual-VM verified)",
            port.r#type
        );
    }
    let number = port
        .index
        .map(|index| index.saturating_add(1).to_string())
        .ok_or_eyre("port has no @index; cannot address its edit form")?;
    Ok(vec![
        ("standard_kvm_port".to_owned(), "kvm".to_owned()),
        ("FV_0_portedit".to_owned(), new_name.to_owned()),
        ("_port_id".to_owned(), port.id.clone()),
        ("_port_subtype".to_owned(), "Dual-VM".to_owned()),
        ("_port_number".to_owned(), number),
        ("_port_connection".to_owned(), String::new()),
        ("_port_type".to_owned(), "kvm".to_owned()),
        ("_usb_profiles".to_owned(), "0,".to_owned()),
        ("action_apply".to_owned(), "OK".to_owned()),
        (
            "__templates__".to_owned(),
            " portedit applytootherports applytootherportsres".to_owned(),
        ),
    ])
}

/// Renames `port` to `new_name`: `WebUI` login, form POST, then RDM
/// re-read to confirm the switch applied it.
pub fn rename_port(
    host: &str,
    user: &str,
    password: &str,
    port: &Port,
    new_name: &str,
) -> eyre::Result<()> {
    let fields = rename_fields(port, new_name)?;
    let body = form_urlencode(&fields);
    let mut web = WebSession::new(host);
    web.login(user, password)?;
    // Best-effort response: the switch often aborts the TLS session
    // instead of closing cleanly after applying the POST, so a broken
    // response proves nothing — the outcome is checked below over RDM.
    if let Err(error) = web.post_raw("/port_edit.asp", &body) {
        warn!(
            ?error,
            "rename POST response unreadable; verifying over RDM"
        );
    }
    // The switch serialises TLS handshakes and refuses connections under
    // load, so the verification login gets its own retries (kept here,
    // not in `RdmClient::connect`, to leave video timing untouched).
    let mut attempt = 0;
    loop {
        attempt += 1;
        match verify_rename(host, user, password, port, new_name) {
            Ok(()) => return Ok(()),
            Err(error) if attempt < 3 => {
                warn!(?error, attempt, "rename verification failed; retrying");
                std::thread::sleep(std::time::Duration::from_secs(15));
            }
            Err(error) => return Err(error),
        }
    }
}

/// Re-reads the port list over RDM and confirms the new name stuck.
fn verify_rename(
    host: &str,
    user: &str,
    password: &str,
    port: &Port,
    new_name: &str,
) -> eyre::Result<()> {
    let mut client = RdmClient::connect(host, user, password)?;
    let renamed = client
        .enumerate_ports()?
        .into_iter()
        .find(|candidate| candidate.id == port.id)
        .ok_or_eyre(format!("port {} vanished after rename", port.id))?;
    if renamed.name.as_deref() == Some(new_name) {
        Ok(())
    } else {
        bail!(
            "rename not applied: port {} still named {:?}",
            port.id,
            renamed.name
        );
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    fn vm_port() -> Port {
        Port {
            id: "P_000d5d065096_6".to_owned(),
            class: Some("KVM".to_owned()),
            r#type: Some("VM".to_owned()),
            index: Some(6),
            status: Some(1),
            stat_available: Some(0),
            connection: None,
            device_id: None,
            name: Some("Auriel".to_owned()),
        }
    }

    #[test]
    fn builds_browser_equivalent_fields() {
        let fields = rename_fields(&vm_port(), "Auriel1").unwrap();
        let body = form_urlencode(&fields);
        assert!(body.contains("FV_0_portedit=Auriel1"));
        assert!(body.contains("_port_id=P_000d5d065096_6"));
        assert!(body.contains("_port_subtype=Dual-VM"));
        assert!(body.contains("_port_number=7"));
        assert!(body.contains("standard_kvm_port=kvm"));
        assert!(body.contains("action_apply=OK"));
    }

    #[test]
    fn rejects_bad_names_and_types() {
        assert!(rename_fields(&vm_port(), "").is_err());
        assert!(rename_fields(&vm_port(), &"x".repeat(33)).is_err());
        assert!(rename_fields(&vm_port(), &"x".repeat(32)).is_ok());
        let mut down = vm_port();
        down.r#type = Some("Not Available".to_owned());
        assert!(rename_fields(&down, "Auriel1").is_err());
        let mut no_index = vm_port();
        no_index.index = None;
        assert!(rename_fields(&no_index, "Auriel1").is_err());
    }
}
