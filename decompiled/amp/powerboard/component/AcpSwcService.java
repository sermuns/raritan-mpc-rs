/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.component;

import amp.powerboard.clientapi.services.CSWCService;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Panel;
import java.util.Vector;

public class AcpSwcService
extends CSWCService {
    private Vector vectorMB = new Vector();

    public void addInstance(Dialog dialog) {
        this.vectorMB.addElement(dialog);
    }

    public void removeInstance(Dialog dialog) {
        if (!this.vectorMB.isEmpty()) {
            this.vectorMB.removeElement(dialog);
        }
    }

    public void removeAllInstances() {
        if (!this.vectorMB.isEmpty()) {
            int n = 0;
            while (n < this.vectorMB.size()) {
                Dialog dialog = (Dialog)this.vectorMB.elementAt(n);
                dialog.dispose();
                ++n;
            }
        }
    }

    public static int adjustedFontSize(int n) {
        int n2 = n;
        Panel panel = new Panel();
        int n3 = panel.getFontMetrics(new Font("Dialog", 0, n2)).getAscent();
        int n4 = panel.getFontMetrics(new Font("Dialog", 0, n2)).getHeight();
        while (n4 > n + 4) {
            n4 = panel.getFontMetrics(new Font("Helvetica", 0, n2 -= 2)).getHeight();
        }
        return n2;
    }
}

