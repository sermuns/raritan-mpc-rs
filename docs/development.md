# Development

## Build & test

```bash
cargo build --workspace --offline   # offline works once deps are cached
cargo test --workspace --offline
cargo clippy --workspace --offline
```

`CARGO_TARGET_DIR` is set to `~/.cache/cargo` in this environment, so
binaries land in `~/.cache/cargo/debug/`, not `./target`.

## CLI reference

```bash
# list ports (index, name, type, id, status)
raritan-cli HOST [--user U] [--password P]

# headless video capture (PPM)
raritan-cli HOST --video <index|id|name-substring> [--frames N] [--out F]

# inventory XML dump (top-level + child devices + capabilities)
raritan-cli HOST --dump-xml FILE

# TR liveness probe on an unauthenticated channel (diagnostic)
raritan-cli HOST --ping-pre-auth
```

## Capturing traffic

```bash
dumpcap -i skuldafn -f "host <switch-host>" -w /tmp/opencode/ours.pcapng
SSLKEYLOGFILE=/tmp/opencode/ourskeys.log raritan-cli ...
```

- Interface here is `skuldafn` (a tunnel); adjust per machine.
- `SSLKEYLOGFILE` is honored by `raritan-rdm` TLS setup for Wireshark
  decryption of our own sessions.
- For Java captures, the key extraction used previously was
  `extract-tls-secrets` as a javaagent; keep capture windows tight
  around the click of interest.

## Reading captures

- RFB video is plaintext TCP on 443: `tshark -z follow,tcp,hex,<stream>`.
- RDM on 5000 is TLS 1.0: force dissection with
  `-d tcp.port==5000,tls`, reassemble via `tcp.payload`, decrypt per
  [Verification](verification.md).
- Default log filter already gives per-crate info; override with
  `RUST_LOG` (note: setting it *replaces* the default filter entirely).

## Repo map

- `raritan-common/src/` — shared CSC framing (`csc`), legacy TLS 1.0
  (`tls`), XML helpers (`xml`), RC4/base64 (`crypto`), ports/timeouts
  (`net`), big-endian readers (`io`).
- `raritan-rfb/src/` — `proto` (message types), `creds`, `framebuffer`
  (Raw decode), `lrle` (tile decoder), `input` (key events + Eric
  table), `transport` (TCP/TLS setup),
  `handshake`, `pump` (steady-state messages); `RfbStream` itself in
  `stream.rs`.
- `raritan-rdm/src/` — `client.rs` (`RdmClient`), `handshake` (CSC
  pre-TLS + auth), `event` (`CSC_Test2`), `tr` (legacy binary TR grant
  diagnostics), `model` (port parsing); `protocol` re-exports common.
- `raritan-session/src/lib.rs` — end-to-end video flow (RDM login →
  RFB → event session → frame capture) shared by CLI and GUI;
  `control.rs` is the GUI's single long-lived RDM connection (port
  enumeration, video credentials, keepalive) served over a channel.
- `raritan-cli/src/main.rs` — list/capture/dump commands.
- `raritan-mpc/src/main.rs` — egui app; video worker calls
  `raritan-session::establish_video`.
- `decompiled/` — CFR output of the Java client; the protocol reference
  behind `docs/rfb.md` and `docs/rdm.md`.
