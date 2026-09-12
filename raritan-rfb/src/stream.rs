//! The `RfbStream` type itself (fields + handshake entry imports).
//!
//! Constructors live in [`crate::transport`], the handshake in
//! [`crate::handshake`], and the steady-state pump in [`crate::pump`].

use crate::framebuffer::FramebufferUpdate;
use std::collections::VecDeque;

pub struct RfbStream<S> {
    pub(crate) stream: S,
    pub(crate) framebuffer_size: Option<(u16, u16)>,
    pub(crate) pending_updates: VecDeque<FramebufferUpdate>,
}
