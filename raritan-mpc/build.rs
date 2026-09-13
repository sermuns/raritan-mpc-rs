//! Exposes the git commit to the UI via `VERGEN_GIT_*` env vars.
//!
//! Hand-rolled instead of vergen-gitcl (which drags ~30 crates, incl. a
//! second syn, just to shell out to git): all we need is `rev-parse HEAD`
//! plus a dirty check. Outside a git checkout the variables stay unset
//! and the UI falls back to placeholders.

use std::path::PathBuf;

fn git(args: &[&str]) -> Option<String> {
    let output = std::process::Command::new("git").args(args).output().ok()?;
    if !output.status.success() {
        return None;
    }
    Some(String::from_utf8(output.stdout).ok()?.trim().to_owned())
}

/// Path of the `.git/HEAD` file, following worktree `gitdir:` pointers.
fn head_file() -> Option<PathBuf> {
    let manifest = PathBuf::from(std::env::var("CARGO_MANIFEST_DIR").ok()?);
    let dotgit = manifest.join("../.git");
    if dotgit.join("HEAD").is_file() {
        return Some(dotgit.join("HEAD"));
    }
    let content = std::fs::read_to_string(&dotgit).ok()?;
    let dir = content.strip_prefix("gitdir:")?.trim();
    Some(PathBuf::from(dir).join("HEAD"))
}

fn main() {
    // Re-run when the commit changes: HEAD moves on branch switch, the
    // branch ref moves on new commits. (Package files trigger rebuilds
    // anyway; this covers metadata-only commits.)
    if let Some(head) = head_file() {
        println!("cargo:rerun-if-changed={}", head.display());
        if let Some(ref_path) = std::fs::read_to_string(&head)
            .ok()
            .as_deref()
            .and_then(|content| content.strip_prefix("ref:"))
            .map(str::trim)
            && let Some(parent) = head.parent()
        {
            println!("cargo:rerun-if-changed={}", parent.join(ref_path).display());
        }
    }
    if let Some(sha) = git(&["rev-parse", "HEAD"]) {
        println!("cargo:rustc-env=VERGEN_GIT_SHA={sha}");
        // Untracked files don't change the binary, so they don't count.
        let dirty =
            git(&["status", "--porcelain", "--untracked-files=no"]).is_some_and(|s| !s.is_empty());
        println!("cargo:rustc-env=VERGEN_GIT_DIRTY={dirty}");
    }
}
