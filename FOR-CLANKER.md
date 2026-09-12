We are rewriting Raritan MPC, a Java client for Raritan KVM switches. The new client is written in Rust, using `egui` for GUI.

Our first goal was to enumerate all ports, this works!

Next goal is to connect to one port and start receiving video frames (RFB, remote frame buffer). This does not work yet, something is going wrong with server handshake and server never sends the framebuf frames.

I have decompiled the java client at ./decompiled/ and have ability to capture traffic using `dumpcap` and have sslkeylogfile, so you can decrypt TLS.
