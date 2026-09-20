# Local install for raritan-mpc: release binary via cargo, plus the
# desktop entry and icons. Everything lands in $HOME (binary via
# ~/.cargo/bin); run `update-desktop-database`/`gtk-update-icon-cache`
# afterwards so the menu entry and icon show up without a logout.

default:
    @just --list

# Install the binary and the desktop entry + icons.
install:
    #!/usr/bin/env bash
    set -euo pipefail
    cargo install --force --path raritan-mpc
    command -v raritan-mpc >/dev/null || { echo "raritan-mpc not on PATH" >&2; exit 1; }
    mkdir -p \
        "$HOME/.local/share/applications" \
        "$HOME/.local/share/icons/hicolor/scalable/apps" \
        "$HOME/.local/share/icons/hicolor/128x128/apps"
    cp raritan-mpc.desktop "$HOME/.local/share/applications/"
    cp media/logo.svg "$HOME/.local/share/icons/hicolor/scalable/apps/raritan-mpc.svg"
    cp media/icon-128.png "$HOME/.local/share/icons/hicolor/128x128/apps/raritan-mpc.png"
    update-desktop-database "$HOME/.local/share/applications"
    gtk-update-icon-cache -f -t "$HOME/.local/share/icons/hicolor" >/dev/null
    echo "installed: $(command -v raritan-mpc)"

push:
    git push
    git push --tags

watch:
    watchexec --exts rs --restart -- cargo run
