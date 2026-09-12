# RDM control channel

Reference: decompiled `javaclientlib.clientlib` (`TRConnection`,
`CSCConnect`, `TRVideoStream`) plus decrypted captures. Implementation:
`raritan-rdm/src/client.rs`, framing in `protocol.rs`.

## Framing and auth (port 5000)

- Plaintext CSC handshake: read greeting (`<CSC .../>`), send
  `<CSC_Ack/>`, read `<CSC_Info .../>` (device model, protocols),
  send `<CSC_Start_Session ProtocolID="RDM"/>`.
- Upgrade to TLS 1.0 (`DEFAULT:@SECLEVEL=0`, no verification).
- `<CSC_Auth UserName=".." Password=".."/>` → `<CSC_Pass/>`.
- CSC frame format (both directions, also inside TLS):
  `[len:u32-be][XML][0x00]`, length includes all 5+ bytes
  (`read_frame`/`write_frame`).
- `SSLKEYLOGFILE` is honored for Wireshark captures (see
  [Verification](verification.md)).

## Database and sessions

- `<Session><GetSessionID/></Session>` → `SessionID` + `SessionKey`.
  The pair (`"id":"key"`) is the RFB credential
  (`RfbCredentials::rdm_session`); same construction as the Java
  `RFBProfile.rdmSession`.
- `<Database><Get><Select>…` queries enumerate devices/ports. The
  top-level IP-Reach query returns the `Admin` and `*_FG_0` ports; each
  `Connection="D_…"` device is queried for its KVM ports (id, Class,
  Type, index, Status, Name, …).
- `open_event_session(session_id, session_key)`: second `:5000`
  connection, `<CSC_Start_Session ProtocolID="RDMEvent"
  SessionID="…"/>`, TLS, then the RC4 `CSC_Test2` dance from
  `CSCConnect.CSC_Test` (key = session key, probe string
  `"1234567890"` time-XORed). Held open with a background drain,
  mirroring Java's always-on event loop.

## Notify subscription

Java sends on the main session after enumerating:

```xml
<Notify><Subscribe><ID>*</ID><Events>*</Events><Time/><SerialNo/>
<SubscriptionID/><NodeID/><SendData/></Subscribe></Notify>
```

We currently skip this (video works without it); kept here for fidelity
work later.

## The TR video-stream grant

Java's legacy path (`TRVideoStream.connectVideoStream`) sends binary TR
command 55 (`[len:u16][55][pkt][40 B comp params][XML]`, expects
`VIDEO_CONNECTED = 37`). Our byte-identical request (verified field by
field against `TRCOMMAND`/`TRCMD_CONNECT_VIDEO_STREAM_DATA`, same
cipher, same sizes on the wire) is met with **total silence**: no grant,
no NACK, on fresh or reused sessions, all ports, all portal/target
variants, with or without force, with the event session up, waiting up
to 150 s.

Decisive evidence from a decrypted Java capture (`jmain_*` in
[Verification](verification.md)): the Java client's main session
**never sends cmd 55 at all** — it ends with the Notify Subscribe —
yet video flows over RFB. The grant is therefore unnecessary for RFB
video on this firmware, and both the GUI worker and the CLI go straight
from RDM credentials to the RFB channel. The grant helpers
(`connect_video_stream`, `probe_ping`) remain in the library for future
debugging.
