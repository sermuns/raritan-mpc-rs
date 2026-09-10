/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.awt.FlowLayout;
import javax.swing.JPanel;

public class ScrollableFlowPanel
extends JPanel {
    private static final int VGAP = 6;
    private static final int HGAP = 6;

    public ScrollableFlowPanel() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(6);
        flowLayout.setVgap(6);
        flowLayout.setAlignment(3);
        this.setLayout(flowLayout);
    }
}

