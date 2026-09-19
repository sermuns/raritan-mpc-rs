# Connection flows

All traffic goes to the switch on two TCP ports: **5000** (RDM control,
TLS) and **443** (video: plaintext RFB; short TLS sessions for misc
device access). No other ports are involved (verified by capture).

## Port enumeration (port 5000)

1. TCP connect `:5000`.
2. Read CSC greeting (`<CSC .../>` frame: `u32-be` length + NUL-terminated XML).
3. Send `<CSC_Ack/>`, read `<CSC_Info .../>` (device model, protocols).
4. Send `<CSC_Start_Session ProtocolID="RDM"/>`, upgrade to TLS 1.0
   (`DEFAULT:@SECLEVEL=0`, no cert verification).
5. Send `<CSC_Auth UserName=".." Password=".."/>`, expect `<CSC_Pass/>`.
6. `<Session><GetSessionID/></Session>` → `SessionID` + `SessionKey`
   (used later as the RFB credential pair `"id":"key"`).
7. `<Database><Get>...` queries for `/System/Device[@Type='IP-Reach']/Port`
   and each connected child device → KVM port list.

Implemented in `raritan-rdm/src/client.rs` (`RdmClient::connect`,
`enumerate_ports`) with framing in `protocol.rs` (`read_frame`/`write_frame`).

## Video (port 443, plaintext RFB)

No TR grant is used (see [RDM](rdm.md#the-tr-video-stream-grant)). The flow is:

1. RDM session (auth) → session credentials, fetched once per
   connection. The GUI keeps one such connection for its whole run
   (`raritan_session::ControlLink`, a thread owning the `RdmClient`):
   port enumeration, video credentials and the keepalive all go over it,
   so the switch sees one login per run instead of one per port refresh
   and one per video connect. The CLI logs in per run
   (`establish_video`).
2. TCP connect `:443`, raw RFB 1.29 handshake (see [RFB](rfb.md)):
   version → RDM-session auth → welcome → client-init + associated tag +
   KVM-switch to the port id → framebuffer format → encodings/pixel
   format → session-init tail. Then `spawn_event_session(session_id,
   session_key)` on a background thread: second `:5000` connection,
   `<CSC_Start_Session ProtocolID="RDMEvent" SessionID="..."/>`, TLS,
   RC4 `CSC_Test2` handshake, held open and drained (mirrors the Java
   client's always-on event loop). It is off the critical path because
   its TLS handshake costs ~2 s on the switch and video runs without it.
   Every stage polls a cancel flag so a superseded connect stops early;
   each stage is a TLS 1.0 handshake the switch serialises, so two
   connects at once roughly double each other's time.
3. Steady state: read server messages (updates, pings, OSD, commands…),
   apply framebuffer updates, send incremental full-area update requests.
   The RDM connection stays open for the whole session, with keepalives
   like the Java client: an RFB ping request every 20 s (`PingTimer`,
   `RfbPinger`) and a `GET_DEVICE_ID` database query on the RDM channel
   after 29 s idle (`TRKeepAliveThread` / event loop, in `ControlLink`).
   Without these the switch reaps the session: the `RDMEvent` socket
   closes first, then the RFB stream. A video reconnect asks the control
   thread for credentials again; it verifies the connection with a round
   trip first and logs in afresh if that fails.

## What the Java client does differently (and why it doesn't matter)

- It also opens the `RDMEvent` session — we replicate that.
- It sends `<Notify><Subscribe>...` on the main RDM session — we don't
  (video works without it; frames are driven by RFB update requests).
- It sends the TR `Connect` (cmd 55) grant request — the switch never
  answers ours (nor NACKs it), and video works without it, so we skip it.
  Details and evidence: [RDM](rdm.md#the-tr-video-stream-grant).
