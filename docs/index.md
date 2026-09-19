# Raritan MPC (Rust)

A Rust rewrite of the Raritan MPC Java client for Raritan KVM switches
(Dominion KX2 family), with an `egui` GUI and a headless CLI.

## Status

- ✅ Port enumeration over RDM — works.
- ✅ Live video (RFB remote framebuffer) — works, verified headlessly
  against a real switch (1024×768 RGB565, LRLE + Raw decoding; see
  [Verification](verification.md)).
- ⚠️ The legacy TR video-stream grant (cmd 55) is intentionally bypassed:
  the switch never answers it (see [RDM](rdm.md#the-tr-video-stream-grant)).

## Crates

| Crate | Role |
|---|---|
| `raritan-rdm` | RDM control channel: auth, database queries, event referral session, TR grant diagnostics |
| `raritan-rfb` | RFB 1.29 video channel: handshake, message pump, framebuffer decode (Raw/LRLE), update requests |
| `raritan-common` | Shared CSC framing, legacy TLS, XML/RC4 helpers, ports/timeouts |
| `raritan-session` | End-to-end video flow shared by CLI and GUI (RDM → RFB → capture, plus the event session) |
| `raritan-cli` | Headless CLI: list ports, capture video to PPM, dump inventory XML |
| `raritan-mpc` | `egui` GUI (default workspace member) |

## Quickstart

```bash
# List KVM ports
cargo run -p raritan-cli -- <HOST>

# Capture 60 framebuffer updates from a port, write /tmp/opencode/frame.ppm
cargo run -p raritan-cli -- <HOST> --video "Rack 10" --frames 60

# GUI
cargo run -p raritan-mpc
```

Credentials default to `admin` / `admin` (`--user`, `--password` override).
Logging defaults to `raritan_mpc=info,raritan_rdm=info,raritan_rfb=info,raritan_session=info,raritan_common=info`
(override with `RUST_LOG`).

## Reading guide

- [Flows](flows.md) — the end-to-end connection sequences.
- [RFB protocol](rfb.md) — byte-level reference for the video channel.
- [RDM protocol](rdm.md) — control channel, auth, events, and the grant story.
- [Verification](verification.md) — captures, TLS decryption, replay tests.
- [Development](development.md) — builds, tests, capture recipes.
