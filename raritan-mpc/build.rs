//! Exposes the git commit to the UI via `VERGEN_GIT_*` env vars.
//!
//! Best-effort: outside a git checkout the variables stay unset and the
//! UI falls back to placeholders (`fail_on_error` defaults to off).

use vergen_gitcl::{Emitter, Gitcl};

fn main() {
    let _ = Emitter::default()
        .add_instructions(&Gitcl::all_git())
        .and_then(|emitter| emitter.emit());
}
