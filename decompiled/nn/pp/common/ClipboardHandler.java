/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardHandler
implements ClipboardOwner {
    private static Clipboard clipboard;
    private static ClipboardHandler cbhInstance;

    private ClipboardHandler() {
    }

    public static ClipboardHandler getInstance() {
        if (cbhInstance == null) {
            cbhInstance = new ClipboardHandler();
        }
        return cbhInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void copyToClipboard(String string) {
        ClipboardHandler clipboardHandler = cbhInstance;
        synchronized (clipboardHandler) {
            if (clipboard == null) {
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            }
            clipboard.setContents(new StringSelection(string), cbhInstance);
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}

