/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.rccore.impl.RCCoreImpl;
import nn.pp.rccore.impl.RemoteConsoleRenderer;

public class RCCoreGenericImpl
extends RCCoreImpl {
    private final RemoteConsoleRenderer userSpecifiedRenderer;

    public RCCoreGenericImpl(Logger logger, RemoteConsoleRenderer remoteConsoleRenderer) {
        super(logger);
        this.userSpecifiedRenderer = remoteConsoleRenderer;
    }

    @Override
    protected void loadRenderer() {
        this.renderer = this.userSpecifiedRenderer;
        this.logger.log(Level.INFO, T._("Using for rendering:") + " " + this.renderer.toString());
    }
}

