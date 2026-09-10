/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.rccore.impl.RCCoreImpl;
import nn.pp.rccore.impl.RemoteConsoleRendererGraphical;
import nn.pp.rccore.impl.RemoteConsoleRendererGraphicalVolatileImage;

public class RCCoreImplGraphical
extends RCCoreImpl {
    private boolean useVolatileImage;

    public RCCoreImplGraphical(Logger logger, boolean bl) {
        super(logger);
        this.useVolatileImage = bl;
    }

    @Override
    protected void loadRenderer() {
        this.renderer = this.useVolatileImage ? new RemoteConsoleRendererGraphicalVolatileImage(this.logger, this.listeners) : new RemoteConsoleRendererGraphical(this.logger, this.listeners);
        this.logger.log(Level.INFO, T._("Using for rendering:") + " " + this.renderer.toString());
    }
}

