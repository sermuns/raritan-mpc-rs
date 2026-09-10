/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.X509TrustManager;
import javax.sound.sampled.Mixer;
import nn.pp.audiocore.AudioAdapter;
import nn.pp.audiocore.AudioCore;
import nn.pp.audiocore.AudioDevice;
import nn.pp.audiocore.AudioEventListener;
import nn.pp.audiocore.AudioException;
import nn.pp.audiocore.AudioFormat;
import nn.pp.audiocore.impl.ListenerLists;
import nn.pp.audiocore.impl.audio.DeviceEnumerator;
import nn.pp.audiocore.impl.rap.RapHandler;
import nn.pp.core.NotificationListener;
import nn.pp.core.T;
import nn.pp.core.impl.DeviceConnector;

public class AudioCoreImpl
implements AudioCore {
    protected Logger logger;
    private AudioDevice playbackDevice;
    private AudioDevice captureDevice;
    private AudioAdapter adapter = new AudioAdapter(){

        @Override
        public void audioConnected(boolean bl) {
            if (bl) {
                DeviceEnumerator.addConnection(new AudioDevice[]{AudioCoreImpl.this.playbackDevice, AudioCoreImpl.this.captureDevice}, AudioCoreImpl.this);
            }
        }

        @Override
        public void disconnected(Exception exception) {
            AudioCoreImpl.this.disconnect();
        }
    };
    private X509TrustManager trustManager;
    private DeviceConnector connector;
    private boolean connected = false;
    private RapHandler rapHandler;
    ListenerLists listeners = new ListenerLists();

    public AudioCoreImpl(Logger logger) {
        if (logger == null) {
            logger = Logger.getLogger("Audio");
            logger.setUseParentHandlers(false);
            logger.addHandler(new ConsoleHandler());
            logger.setLevel(Level.SEVERE);
        }
        this.logger = logger;
        this.addAudioEventListener(this.adapter);
        this.addNotificationListener(this.adapter);
    }

    @Override
    public void dispose() {
        if (this.rapHandler != null) {
            this.rapHandler = null;
        }
        this.removeAudioEventListener(this.adapter);
        this.removeNotificationListener(this.adapter);
    }

    @Override
    public void setX509TrustManager(X509TrustManager x509TrustManager) {
        this.trustManager = x509TrustManager;
    }

    @Override
    public void connectAudioWithUserLogin(String string, int n, boolean bl, String string2, int n2, int n3, AudioDevice audioDevice, AudioFormat audioFormat, AudioDevice audioDevice2, AudioFormat audioFormat2, String string3, String string4) throws IOException, AudioException {
        this.connectToHost(string, n, bl, string2, n2, n3, audioDevice, audioFormat, audioDevice2, audioFormat2, string3, string4, null, null, null, null);
    }

    @Override
    public void connectAudioWithRdmSession(String string, int n, boolean bl, String string2, int n2, int n3, AudioDevice audioDevice, AudioFormat audioFormat, AudioDevice audioDevice2, AudioFormat audioFormat2, String string3, String string4, String string5) throws IOException, AudioException {
        this.connectToHost(string, n, bl, string2, n2, n3, audioDevice, audioFormat, audioDevice2, audioFormat2, null, null, null, string3, string4, string5);
    }

    @Override
    public void connectAudioWithEricKey(String string, int n, boolean bl, String string2, int n2, int n3, AudioDevice audioDevice, AudioFormat audioFormat, AudioDevice audioDevice2, AudioFormat audioFormat2, String string3) throws IOException, AudioException {
        this.connectToHost(string, n, bl, string2, n2, n3, audioDevice, audioFormat, audioDevice2, audioFormat2, null, null, string3, null, null, null);
    }

    private void connectToHost(String string, int n, boolean bl, String string2, int n2, int n3, AudioDevice audioDevice, AudioFormat audioFormat, AudioDevice audioDevice2, AudioFormat audioFormat2, String string3, String string4, String string5, String string6, String string7, String string8) throws IOException, AudioException {
        if (this.connected) {
            throw new AudioException(T._("Already connected"));
        }
        this.playbackDevice = audioDevice;
        this.captureDevice = audioDevice2;
        try {
            this.connector = new DeviceConnector(this.logger, this.trustManager);
            this.connector.connect(string, n, bl);
            if (!bl && string7 != null && string8 != null) {
                this.connector.writeCCSGproxyModePrefix(string7);
                if (string8.equals("yes")) {
                    System.out.println("CC-Proxy[Audio] mode: SSL enabled .. \n");
                    this.connector.connectSSLWithSocket(string, n);
                } else {
                    System.out.println("CC-Proxy[Audio] mode: plaintext .. \n");
                }
            }
            Mixer.Info info = audioDevice != null ? audioDevice.getMixer() : null;
            Mixer.Info info2 = audioDevice2 != null ? audioDevice2.getMixer() : null;
            this.rapHandler = RapHandler.loadRapHandler(this.connector, this.logger, this.listeners, info, audioFormat, info2, audioFormat2, n2, n3);
            this.rapHandler.connect(string2, string3, string4, string5, string6);
            this.connected = true;
            this.logger.log(Level.INFO, T._("Audio capable device connected"));
        }
        catch (AudioException audioException) {
            this.logger.log(Level.SEVERE, T._("Could not establish Audio session"), audioException);
            this.disconnect();
            throw audioException;
        }
        catch (IOException iOException) {
            this.logger.log(Level.SEVERE, T._("Could not establish Audio session"), iOException);
            this.disconnect();
            throw iOException;
        }
    }

    @Override
    public void disconnect() {
        DeviceEnumerator.removeConnection(this);
        if (this.rapHandler != null) {
            this.rapHandler.close();
            this.connector.disconnect();
            this.rapHandler.dispose();
            this.rapHandler = null;
            this.connector = null;
        }
        this.connected = false;
    }

    @Override
    public void setCaptureBufferSizeMs(int n) throws IOException {
        if (this.rapHandler != null) {
            this.rapHandler.setCaptureBlockSize(n);
        }
    }

    @Override
    public int getCaptureBufferSizeMs() {
        if (this.rapHandler != null) {
            return this.rapHandler.getCaptureBlockSize();
        }
        return 120;
    }

    @Override
    public void setPlaybackBufferSizeMs(int n) throws IOException {
        if (this.rapHandler != null) {
            this.rapHandler.setPlaybackBlockSize(n);
        }
    }

    @Override
    public int getPlaybackBufferSizeMs() {
        if (this.rapHandler != null) {
            return this.rapHandler.getPlaybackBlockSize();
        }
        return 120;
    }

    @Override
    public void addNotificationListener(NotificationListener notificationListener) {
        this.listeners.notificationListenerList.addListener(notificationListener);
    }

    @Override
    public void removeNotificationListener(NotificationListener notificationListener) {
        this.listeners.notificationListenerList.removeListener(notificationListener);
    }

    @Override
    public void addAudioEventListener(AudioEventListener audioEventListener) {
        this.listeners.audioEventListenerList.addListener(audioEventListener);
    }

    @Override
    public void removeAudioEventListener(AudioEventListener audioEventListener) {
        this.listeners.audioEventListenerList.removeListener(audioEventListener);
    }
}

