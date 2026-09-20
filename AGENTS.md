# AGENTS.md — instructions for LLM agents working in this repo

This is a Rust rewrite of Raritan's Java KVM client ("Multi-Platform Client").
Rust code is ~99% LLM-generated; `README.md` + logo are the human parts.
Commits containing LLM output are prefixed `(slop)` — use that prefix.

## Commands

```bash
cargo build --workspace --offline
cargo test --workspace --offline
cargo clippy --workspace --all-targets --offline
cargo run -p raritan-cli -- <HOST>                        # list ports
cargo run -p raritan-cli -- <HOST> --video "Rack 10" --frames 1 --out /tmp/f.ppm
cargo run -p raritan-mpc -- --host <HOST>                 # GUI
```

`CARGO_TARGET_DIR` is `~/.cache/cargo` here. Default creds `admin`/`admin`.
Lab switch for live tests: `192.168.42.10` (test video target: `Rack 10`).
`RUST_LOG=info` gives per-stage timings (`RDM connect done`, `RFB handshake
done`, `first framebuffer update received`).

## Architecture (essentials; depth in `docs/`)

- Two TCP ports: **5000** RDM control (CSC framing + TLS 1.0,
  `DEFAULT:@SECLEVEL=0`, no cert verify — the switch speaks nothing newer)
  and **443** RFB video (plaintext, RFB 1.29).
- `raritan-common`: CSC framing (`csc.rs`), TLS (`tls.rs`), timeouts
  (`net.rs`). `raritan-rdm`: login, port DB, event session, TR diagnostics.
  `raritan-rfb`: handshake, pump (`pump.rs`), Raw/LRLE decode.
  `raritan-session`: `establish_video` (CLI: own login) / `connect_video`
  (GUI: on shared `ControlLink` login) / `find_port` / `capture_frames`.
- GUI video worker (`run_pump`, `raritan-mpc/src/main.rs`) is
  single-threaded: drain input → 5 ms `wait_for_message` peek → drain
  again → `poll_incoming`; the next update request goes out at the update
  **header**, before body transfer/decode (mirrors Java). Frame queue to
  egui is `sync_channel(2)` with drop-on-full; GUI repaints on frame
  arrival. Retries 4× with 1/2/4 s backoff; superseded connects cancel
  between stages (parallel TLS handshakes serialize on the switch and
  double each other's time).

## Gotchas (all verified against captures/decompiled client — do not "fix")

- No TR video-stream grant (cmd 55): the switch never answers; video flows
  over RFB without it. TR helpers are diagnostics only.
- RFB handshake bytes are frozen by test
  (`handshake_matches_java_client_bytes`): 3 full update requests + tail.
  Key press = Eric code | 0x8000 (verified; Java's boolean is inverted in
  name only — pressed sets the high bit).
- Wire port `@index` is 0-indexed; UI shows 1-indexed
  (`Port::display_index`). `find_port` accepts both + id/name.
- Busy = `@Status` combined with `@StatAvailable`, not status alone.
- Perf claims require `--release` (debug decode/convert is ~20× slower)
  and live-switch measurement; loopback input-pickup harness pattern from
  git history is the template.
- No Notify Subscribe is sent (works without it). Event session
  (`RDMEvent` + RC4 `CSC_Test2`) is held for parity, off the critical path.

## Docs map

- `docs/flows.md` — connection sequences + worker loop.
- `docs/rfb.md` — byte-level video reference + message catalog.
- `docs/rdm.md` — control channel, sessions, TR-grant evidence.
- `docs/verification.md` — captures, TLS decryption, replay tests.
- `docs/development.md` — repo map, capture recipes.
