/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.ui.pan;

import java.awt.Component;
import java.awt.event.ComponentEvent;

public class PanningEvent
extends ComponentEvent {
    private int direction;

    public PanningEvent(Component component, int n) {
        super(component, 0);
        this.direction = n;
    }

    public int getDirection() {
        return this.direction;
    }
}

