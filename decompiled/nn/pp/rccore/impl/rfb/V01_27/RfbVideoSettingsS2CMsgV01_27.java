/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_27;

import java.io.IOException;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.ProtocolMessage;
import nn.pp.rccore.VideoSettings;

public class RfbVideoSettingsS2CMsgV01_27
extends ProtocolMessage {
    public VideoSettings videoSettings;
    private static final int clockMax = 4096;
    private static final int clockRange = 500;

    public void read(MonitoringDataInputStream monitoringDataInputStream, boolean bl, boolean bl2, boolean bl3, boolean bl4) throws IOException {
        if (!bl) {
            monitoringDataInputStream.readUnsignedByte();
        }
        this.videoSettings = new VideoSettings();
        monitoringDataInputStream.readUnsignedByte();
        this.videoSettings.getBrightnessRed().setValue(monitoringDataInputStream.readUnsignedByte());
        this.videoSettings.getBrightnessGreen().setValue(monitoringDataInputStream.readUnsignedByte());
        this.videoSettings.getBrightnessBlue().setValue(monitoringDataInputStream.readUnsignedByte());
        this.videoSettings.getContrastRed().setValue(monitoringDataInputStream.readUnsignedByte());
        this.videoSettings.getContrastGreen().setValue(monitoringDataInputStream.readUnsignedByte());
        this.videoSettings.getContrastBlue().setValue(monitoringDataInputStream.readUnsignedByte());
        this.videoSettings.getAutoColorCalibration().setEnabled(monitoringDataInputStream.readUnsignedByte() != 0);
        this.videoSettings.getAutoAutoAdjust().setEnabled(monitoringDataInputStream.readUnsignedByte() != 0);
        this.videoSettings.getClock().setValue(monitoringDataInputStream.readUnsignedShort());
        this.videoSettings.getPhase().setValue(monitoringDataInputStream.readUnsignedShort());
        this.videoSettings.getOffsetX().setValue(monitoringDataInputStream.readUnsignedShort());
        this.videoSettings.getOffsetY().setValue(monitoringDataInputStream.readUnsignedShort());
        this.videoSettings.getResolutionX().setValue(monitoringDataInputStream.readUnsignedShort());
        this.videoSettings.getResolutionY().setValue(monitoringDataInputStream.readUnsignedShort());
        this.videoSettings.getRefreshRate().setValue(monitoringDataInputStream.readUnsignedShort());
        this.videoSettings.getNoiseFilter().setValue(monitoringDataInputStream.readUnsignedShort());
        this.videoSettings.getOffsetY().setMaxValue(monitoringDataInputStream.readUnsignedShort());
        int n = this.videoSettings.getResolutionX().getValue() + 2;
        int n2 = this.videoSettings.getClock().getValue() + 500;
        if (n2 > 4096) {
            n2 = 4096;
        }
        this.videoSettings.getClock().setMinValue(n);
        this.videoSettings.getClock().setMaxValue(n2);
        this.videoSettings.getResolutionX().setSupported(true);
        this.videoSettings.getResolutionY().setSupported(true);
        this.videoSettings.getRefreshRate().setSupported(true);
        this.videoSettings.getSaveSettings().setSupported(true);
        this.videoSettings.getCancelSettings().setSupported(true);
        if (!bl3 && !bl4) {
            return;
        }
        if (!bl2) {
            this.videoSettings.getBrightnessRed().setSupported(true);
            this.videoSettings.getBrightnessGreen().setSupported(true);
            this.videoSettings.getBrightnessBlue().setSupported(true);
            this.videoSettings.getContrastRed().setSupported(true);
            this.videoSettings.getContrastGreen().setSupported(true);
            this.videoSettings.getContrastBlue().setSupported(true);
        }
        if (!bl4) {
            return;
        }
        if (!bl2) {
            this.videoSettings.getClock().setSupported(true);
            this.videoSettings.getPhase().setSupported(true);
            this.videoSettings.getAutoColorCalibration().setSupported(true);
            this.videoSettings.getAutoAutoAdjust().setSupported(true);
            this.videoSettings.getNoiseFilter().setSupported(true);
        }
        this.videoSettings.getOffsetX().setSupported(true);
        this.videoSettings.getOffsetY().setSupported(true);
        this.videoSettings.getResetAllModes().setSupported(true);
        this.videoSettings.getResetThisMode().setSupported(true);
    }
}

