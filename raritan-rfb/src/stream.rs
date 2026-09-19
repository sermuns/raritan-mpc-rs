//! The `RfbStream` type itself; constructors in [`crate::transport`],
//! handshake in [`crate::handshake`], steady-state pump in [`crate::pump`].

use crate::framebuffer::FramebufferUpdate;
use std::collections::VecDeque;

pub struct RfbStream<S> {
    pub(crate) stream: S,
    pub(crate) framebuffer_size: Option<(u16, u16)>,
    pub(crate) pending_updates: VecDeque<FramebufferUpdate>,
}
