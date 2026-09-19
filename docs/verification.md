# Verification

How each layer was proven against ground truth (Java client +
packet captures). Capture files themselves are **not** committed;
paths below are where they lived during development.

## RFB handshake replay (offline, hermetic)

`raritan-rfb` unit test `handshake_matches_java_client_bytes` feeds a
minimal server sequence (version, caps `0x13`, empty challenge, auth-ok,
one conn-param, `admin` welcome, server-init, 128 RGB565 1024×768)
through `RfbStream::handshake` and asserts **every client byte** equals
the Java client's (`e-RIC` hello, login, `[33][len][session+NUL]`,
`[07,0,0,4]`, associated tag, KVM switch, `[0x1080,0,0,0]` encodings,
RGB565 pixel format, full update request, and the session-init tail).

`pump_answers_ping_and_decodes_lrle_rect` replays a real captured ping
plus a real 16×16 LRLE rect: asserts the `[149,…]` reply bytes and that
all 256 pixels decode (opaque alpha).

`pipelined_header_body_split_matches_single_read` locks the worker's
pipelining contract: `poll_incoming` exposes the update header while the
body is unread, the next update request is written in between, and the
split read decodes exactly what the single read does.

## Pcap replay (needs capture files)

With `/tmp/truth.pcapng` (Java, Rack 10) a throwaway harness fed the
reassembled server stream into `handshake()`:

- our 203 handshake bytes = exact prefix of the Java client's bytes;
- negotiated 1024×768 RGB565;
- steady-state `read_message` walks server commands, OSD, USB profiles,
  bandwidth and ping traffic and decodes real updates (this caught the
  OSD double-length, ack-format (19 B), rect-alignment-pad, and LRLE
  tile bugs).

Reassembly recipe (both directions, dedupe by relative `tcp.seq`):

```bash
tshark -r CAP -Y "tcp.stream==N && tcp.len>0" \
  -T fields -e frame.number -e tcp.srcport -e tcp.seq -e tcp.len -e tcp.payload
```

Note: use `tcp.payload`, not `data` (the latter is empty when Wireshark
dissects the segment as TLS). Force the dissector on port 5000 with
`-d tcp.port==5000,tls`.

## Decrypting Java's RDM sessions

Port-5000 TLS (1.0, `TLS_RSA_WITH_AES_256_CBC_SHA`) with a `SSLKEYLOGFILE`
keylog. Wireshark 4.7.3 would not apply the log, so decryption was done
manually in Python (`hashlib` + `cryptography`):

1. Reassemble each direction (above).
2. Parse TLS records; take `client_random`/`server_random` from the
   Hello bodies; match `CLIENT_RANDOM` in the keylog → master secret.
3. Sanity-check via Finished: `PRF(master, "client finished",
   MD5(transcript)+SHA1(transcript))[0:12]` must equal the verify data.
4. Key expansion: `PRF(master, "key expansion", server_random +
   client_random, 136)` → client/server MAC (20 B), keys (32 B), IVs.
5. CBC chain per direction, first IV = last Finished ciphertext block;
   strip `pad+1`, verify `HMAC-SHA1(seq + type + 0301 + len + msg)`.

Gotchas that cost real time: strip `pad_value + 1` bytes (the length
byte counts); app-data sequence numbers start at 1 (Finished consumed
0); record boundaries are authoritative — a "shifted plaintext" means
your IV chain skipped a record, not a cipher problem.

This produced `jmain_c_plain.bin` (1219 B: auth, GetSessionID, group
query, inventory, capabilities, device/port-group queries, Notify
Subscribe — and **no cmd 55**), which settled the grant question.

## Live headless capture

```bash
cargo run -p raritan-cli -- HOST --video "Rack 10" --frames 60 \
  --out /tmp/opencode/frame.ppm
```

Decodes 1024×768 updates to a PPM (viewable after conversion, e.g. with
Pillow). A Windows lock screen decoded pixel-perfect on the first
successful run. Target flakiness is real: expect `No video from target
server` / `video=unstable` / calibration OSDs even in Java captures.

## Steady-state pipelining (live probe)

A throwaway harness drove the exact worker path against a real switch
(`poll header → request → read body`, Rack 10, 1024×768): 6 consecutive
updates, including 115–235 KB bodies, decoded with the stream staying
aligned — mid-update requests don't confuse the switch. Same probe in
release mode: per-update client CPU ≈ 1.5 ms total (decode + clone +
convert) on small idle-screen updates, so decode throughput is not the
bottleneck; input pickup while idle is bounded by the 5 ms poll
(~3 ms avg on loopback vs ~45 ms avg at 100 ms).
