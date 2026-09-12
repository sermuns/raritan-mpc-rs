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

1. Fresh RDM session (auth only) → `open_event_session(session_id,
   session_key)`: second `:5000` connection, `<CSC_Start_Session
   ProtocolID="RDMEvent" SessionID="..."/>`, TLS, RC4 `CSC_Test2`
   handshake. Held open, drained in the background (mirrors the Java
   client's always-on event loop).
2. TCP connect `:443`, raw RFB 1.29 handshake (see [RFB](rfb.md)):
   version → RDM-session auth → welcome → client-init + associated tag +
   KVM-switch to the port id → framebuffer format → encodings/pixel
   format → session-init tail.
3. Steady state: read server messages (updates, pings, OSD, commands…),
   apply framebuffer updates, send incremental full-area update requests.

## What the Java client does differently (and why it doesn't matter)

- It also opens the `RDMEvent` session — we replicate that.
- It sends `<Notify><Subscribe>...` on the main RDM session — we don't
  (video works without it; frames are driven by RFB update requests).
- It sends the TR `Connect` (cmd 55) grant request — the switch never
  answers ours (nor NACKs it), and video works without it, so we skip it.
  Details and evidence: [RDM](rdm.md#the-tr-video-stream-grant).
