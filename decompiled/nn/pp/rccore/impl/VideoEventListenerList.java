/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.Vector;
import nn.pp.core.impl.ListenerList;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.VideoEventListener;
import nn.pp.rccore.VideoSettings;
import nn.pp.rccore.impl.VideoEventListenerAction;

public class VideoEventListenerList
extends ListenerList<VideoEventListener> {
    public void fireEncodingChangeAllowed(final boolean bl) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).encodingChangeAllowed(bl);
            }
        }, 1);
    }

    public void fireEncodingAutoSupportedChanged(final boolean bl) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).encodingAutoSupportedChanged(bl);
            }
        }, 2);
    }

    public void fireSupportedEncodingPredefinesChanged(List<RCCore.Predefine> list) {
        final Vector<RCCore.Predefine> vector = new Vector<RCCore.Predefine>(list);
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).supportedEncodingPredefinesChanged(vector);
            }
        }, 2);
    }

    public void fireSupportedEncodingColorDepthsChanged(List<RCCore.ColorDepth> list) {
        final Vector<RCCore.ColorDepth> vector = new Vector<RCCore.ColorDepth>(list);
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).supportedEncodingColorDepthsChanged(vector);
            }
        }, 2);
    }

    public void fireSupportedEncodingCompressionsChanged(List<RCCore.Compression> list) {
        final Vector<RCCore.Compression> vector = new Vector<RCCore.Compression>(list);
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).supportedEncodingCompressionsChanged(vector);
            }
        }, 2);
    }

    public void fireEncodingLossySupportedChanged(final boolean bl) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).encodingLossySupportedChanged(bl);
            }
        }, 2);
    }

    public void fireEncodingAutoChanged(final boolean bl) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).encodingAutoChanged(bl);
            }
        }, 4);
    }

    public void fireEncodingColorDepthChanged(final RCCore.ColorDepth colorDepth) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).encodingColorDepthChanged(colorDepth);
            }
        }, 4);
    }

    public void fireEncodingCompressionChanged(final RCCore.Compression compression) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).encodingCompressionChanged(compression);
            }
        }, 4);
    }

    public void fireEncodingLossyChanged(final boolean bl) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).encodingLossyChanged(bl);
            }
        }, 4);
    }

    public void fireVideoSettingsSupportChanged(final boolean bl) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).videoSettingsSupportChanged(bl);
            }
        }, 8);
    }

    public void fireVideoAutoAdjustSupportChanged(final boolean bl) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).videoAutoAdjustSupportChanged(bl);
            }
        }, 8);
    }

    public void fireColorCalibrationSupportChanged(final boolean bl) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).colorCalibrationSupportChanged(bl);
            }
        }, 8);
    }

    public void fireVideoRefreshSupportChanged(final boolean bl) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).videoRefreshSupportChanged(bl);
            }
        }, 8);
    }

    public void fireVideoSettingsUpdated(VideoSettings videoSettings) {
        final VideoSettings videoSettings2 = new VideoSettings(videoSettings);
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).videoSettingsUpdated(videoSettings2);
            }
        }, 8);
    }

    public void fireResolutionChanged(Dimension dimension) {
        final Dimension dimension2 = new Dimension(dimension);
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).resolutionChanged(dimension2);
            }
        }, 16);
    }

    public void fireOsdMessageReceived(final String string, final boolean bl) {
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).osdMessageReceived(string, bl);
            }
        }, 32);
    }

    public void fireVideoDataUpdated(int n, int n2, int n3, int n4) {
        final Rectangle rectangle = new Rectangle(n, n2, n3, n4);
        this.fire(new VideoEventListenerAction(){

            @Override
            public void run() {
                ((VideoEventListener)this.listener).videoDataUpdated(rectangle);
            }
        }, 64);
    }
}

