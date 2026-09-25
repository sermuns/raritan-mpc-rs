# WebUI channel (port 443) and port rename

The Java client has no rename feature, and the RDM channel (port 5000)
has no `Set` verb — the decompiled client only ever sends
`<Database><Get>` and `<Database><Count>`. Renames therefore go through
the WebUI HTTPS form, reverse-engineered from a Firefox capture
(`port_edit.asp` POST with `FV_0_portedit=Auriel`; decrypt with
`SSLKEYLOGFILE`, see [Verification](verification.md)).

Implementation: `WebSession` in `raritan-common/src/web.rs` (TLS 1.0,
`DEFAULT:@SECLEVEL=0`, no cert verify — same as RDM), field mapping and
verification in `raritan-rdm/src/rename.rs`, test CLI in `raritan-cli`
(`--rename <PORT> --name <NEW>`).

## Flow

1. `POST /auth.asp` (`login`, `password`, `is_dotnet=0`,
   `is_standalone_client=0`, `action_login=Login`) → 302 with
   `Set-Cookie: pp_session_id=…`. A 200 means the login page was shown
   again (bad credentials).
2. `POST /port_edit.asp` (`application/x-www-form-urlencoded`) with the
   minimal field set below; success answers 200 with a
   `redirectToTable()` page (redirect to `port_configuration.asp`).
3. Re-read the port list over RDM and confirm the name stuck. This is
   the only success signal used.

## Field mapping (`rename_fields`)

| Form field          | Value                                              |
| ------------------- | -------------------------------------------------- |
| `standard_kvm_port` | `kvm`                                              |
| `FV_0_portedit`     | new name (form `maxlength` is 32)                  |
| `_port_id`          | RDM port `id` (e.g. `P_000d5d065096_6`)            |
| `_port_subtype`     | `Dual-VM` (RDM `Type="VM"`)                        |
| `_port_number`      | display index (`@index + 1`; `…_6` ↔ 7)            |
| `_port_connection`  | empty                                              |
| `_port_type`        | `kvm`                                              |
| `_usb_profiles`     | `0,`                                               |
| `action_apply`      | `OK`                                               |
| `__templates__`     | ` portedit applytootherports applytootherportsres` |

The browser posts ~130 fields (blade-chassis / KVM-switch sections
included), but the minimal set above renames identically when verified
live — the extra sections are type-inapplicable for `kvm` ports. Only
RDM `Type="VM"` ports are supported; other subtypes own extra form
sections whose defaults we do not reconstruct, so renaming them could
clobber data (rejected with an error).

## Quirks (all observed live)

- The switch applies the POST but usually aborts the TLS session instead
  of closing cleanly (`UnexpectedEof` on read). The POST response is
  therefore best-effort; success is verified over RDM.
- The switch serialises TLS handshakes and refuses connections under
  load (`Connection refused` on 443 and, rarely, 5000). `WebSession`
  reconnects with 10/20/30 s backoff; the rename verification login
  retries 3× at 15 s. Space WebUI requests seconds apart.
- The current name is NOT in the `GET port_edit.asp` HTML — the form's
  `FV_0_portedit` starts empty and is filled from sidebar applet data
  (`initPortData` → `top.container_sidebar.ports.getPortDataById`), which
  is why the POST carries the new name unconditionally and no GET is
  needed before it.
