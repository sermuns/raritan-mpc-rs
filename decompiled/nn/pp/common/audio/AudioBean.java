/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.audio;

import java.awt.Window;
import nn.pp.audiocore.AudioDevice;
import nn.pp.audiocore.AudioFormat;
import nn.pp.common.audio.AudioErrorsAndMessageHandler;
import nn.pp.core.INotificationEvent;

public class AudioBean {
    private final String host;
    private final int port;
    private final boolean sslMode;
    private final String username;
    private final String password;
    private final String rdmSession;
    private final String proxyConnectionId;
    private final String proxyUseSSL;
    private final String ericKey;
    private int rfbSessionId;
    private int msindex = -1;
    private String portId;
    private AudioDevice playbackDevice;
    private AudioDevice captureDevice;
    private AudioFormat playbackFormat;
    private AudioFormat captureFormat;
    private final AudioErrorsAndMessageHandler audioErrorsAndMessageHandler;

    public AudioBean(String string, int n, boolean bl, String string2, String string3, String string4, String string5, String string6, String string7, AudioErrorsAndMessageHandler audioErrorsAndMessageHandler) {
        this.host = string;
        this.port = n;
        this.sslMode = bl;
        this.username = string2;
        this.password = string3;
        this.rdmSession = string4;
        this.proxyConnectionId = string5;
        this.proxyUseSSL = string6;
        this.ericKey = string7;
        this.audioErrorsAndMessageHandler = audioErrorsAndMessageHandler;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public boolean isSslMode() {
        return this.sslMode;
    }

    public String getPortId() {
        return this.portId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRdmSession() {
        return this.rdmSession;
    }

    public String getProxyConnectionId() {
        return this.proxyConnectionId;
    }

    public String getProxyUseSSL() {
        return this.proxyUseSSL;
    }

    public String getEricKey() {
        return this.ericKey;
    }

    public int getRfbSessionId() {
        return this.rfbSessionId;
    }

    public int getMsindex() {
        return this.msindex;
    }

    public void setRfbSessionId(int n) {
        this.rfbSessionId = n;
    }

    public void setMsindex(int n) {
        this.msindex = n;
    }

    public void setPortId(String string) {
        this.portId = string;
    }

    public void setCaptureDevice(AudioDevice audioDevice) {
        this.captureDevice = audioDevice;
    }

    public AudioDevice getCaptureDevice() {
        return this.captureDevice;
    }

    public void setPlaybackDevice(AudioDevice audioDevice) {
        this.playbackDevice = audioDevice;
    }

    public AudioDevice getPlaybackDevice() {
        return this.playbackDevice;
    }

    public void setCaptureFormat(AudioFormat audioFormat) {
        this.captureFormat = audioFormat;
    }

    public AudioFormat getCaptureFormat() {
        return this.captureFormat;
    }

    public void setPlaybackFormat(AudioFormat audioFormat) {
        this.playbackFormat = audioFormat;
    }

    public AudioFormat getPlaybackFormat() {
        return this.playbackFormat;
    }

    public void notificationReceived(Window window, INotificationEvent iNotificationEvent, String string) {
        this.audioErrorsAndMessageHandler.notificationReceived(window, iNotificationEvent, string);
    }

    public void errorMessage(Window window, String string, String string2) {
        this.audioErrorsAndMessageHandler.errorMessage(window, string, string2);
    }
}

