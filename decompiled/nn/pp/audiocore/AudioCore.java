/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore;

import java.io.IOException;
import javax.net.ssl.X509TrustManager;
import nn.pp.audiocore.AudioDevice;
import nn.pp.audiocore.AudioEventListener;
import nn.pp.audiocore.AudioException;
import nn.pp.audiocore.AudioFormat;
import nn.pp.core.NotificationListener;

public interface AudioCore {
    public static final int DEFAULT_MIC_BLK_SIZE = 120;
    public static final int DEFAULT_SPK_BLK_SIZE = 120;
    public static final int MAX_MIC_BLK_SIZE = 400;
    public static final int MAX_SPK_BLK_SIZE = 400;
    public static final int BLK_SIZE_MULTIPLY = 40;

    public void dispose();

    public void setX509TrustManager(X509TrustManager var1);

    public void connectAudioWithUserLogin(String var1, int var2, boolean var3, String var4, int var5, int var6, AudioDevice var7, AudioFormat var8, AudioDevice var9, AudioFormat var10, String var11, String var12) throws IOException, AudioException;

    public void connectAudioWithRdmSession(String var1, int var2, boolean var3, String var4, int var5, int var6, AudioDevice var7, AudioFormat var8, AudioDevice var9, AudioFormat var10, String var11, String var12, String var13) throws IOException, AudioException;

    public void connectAudioWithEricKey(String var1, int var2, boolean var3, String var4, int var5, int var6, AudioDevice var7, AudioFormat var8, AudioDevice var9, AudioFormat var10, String var11) throws IOException, AudioException;

    public void disconnect();

    public void setCaptureBufferSizeMs(int var1) throws IOException;

    public int getCaptureBufferSizeMs();

    public void setPlaybackBufferSizeMs(int var1) throws IOException;

    public int getPlaybackBufferSizeMs();

    public void addNotificationListener(NotificationListener var1);

    public void removeNotificationListener(NotificationListener var1);

    public void addAudioEventListener(AudioEventListener var1);

    public void removeAudioEventListener(AudioEventListener var1);
}

