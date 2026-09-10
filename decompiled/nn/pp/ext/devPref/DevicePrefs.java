/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext.devPref;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import nn.pp.rccore.RCCore;

public class DevicePrefs {
    public static final String ROOT_NODE = "/RaritanDevices";
    protected static final String FILE_NAME = System.getProperty("user.home") != null ? System.getProperty("user.home") + System.getProperty("file.separator") + "DeviceTree.xml" : "DeviceTree.xml";
    protected static final String G2_CONNECTION_SPEED_KEY = "g2ConnectionSpeed";
    protected static final String G2_COLOR_DEPTH_KEY = "g2ColorDepth";
    protected static final String G2_SMOOTHING_KEY = "g2Smoothing";
    public static final String AUTO_MOUNT_SMARTCARDREADER_NAME = "autoMountSmartCardReaderName";
    public static final String CONNECT_PLAYBACK = "connectPlaybackDevice";
    public static final String PLAYBACK_DEVICE = "playbackDevice";
    public static final String PLAYBACK_FORMAT = "playbackFormat";
    public static final String PLAYBACK_BUFSIZE = "playbackBufferSize";
    public static final String CONNECT_CAPTURE = "connectCaptureDevice";
    public static final String CAPTURE_DEVICE = "captureDevice";
    public static final String CAPTURE_FORMAT = "captureFormat";
    public static final String CAPTURE_BUFSIZE = "captureBufferSize";
    protected Preferences rootPrefs = Preferences.userRoot().node("/" + companyNameNoSpace + "Devices");
    protected RCCore.ColorDepth g2ColorDepth = null;
    protected RCCore.Compression g2ConnectionSpeed = null;
    protected RCCore.Smoothing g2Smoothing = null;
    private String cardReaderName;
    private static String companyNameNoSpace = "Raritan";
    private boolean isConnectPlaybackDevice = true;
    private String playbackDevice = null;
    private String playbackFormat = null;
    private int playbackBufferSize = -1;
    private boolean isConnectCaptureDevice = false;
    private String captureDevice = null;
    private String captureFormat = null;
    private int captureBufferSize = -1;

    protected static void setComapnyName(String string) {
        companyNameNoSpace = string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static File getOrCreateXmlFile(String string) {
        File file = new File(string);
        BufferedWriter bufferedWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
                bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                bufferedWriter.write("\n\n");
                bufferedWriter.write("<!DOCTYPE preferences SYSTEM 'http://java.sun.com/dtd/preferences.dtd'>");
                bufferedWriter.write("\n\n");
                bufferedWriter.write("<preferences EXTERNAL_XML_VERSION=\"1.0\">");
                bufferedWriter.write("<root type=\"user\">");
                bufferedWriter.write("<map /><node name=\"" + companyNameNoSpace + "Devices\"><map />");
                bufferedWriter.write("</node></root></preferences>");
                bufferedWriter.flush();
            }
        }
        catch (IOException iOException) {
        }
        finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                    bufferedWriter = null;
                }
                catch (IOException iOException) {}
            }
        }
        return file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void exportPreferences(String string) {
        Preferences preferences = this.rootPrefs.node(string);
        this.storeToPreferences(preferences);
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(DevicePrefs.getOrCreateXmlFile(FILE_NAME)));
            this.rootPrefs.exportSubtree(outputStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
        }
        catch (IOException iOException) {
        }
        catch (BackingStoreException backingStoreException) {
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream = null;
                }
                catch (IOException iOException) {}
            }
        }
    }

    protected void storeToPreferences(Preferences preferences) {
        String string;
        if (this.g2ConnectionSpeed == null) {
            preferences.remove(G2_CONNECTION_SPEED_KEY);
        } else {
            preferences.put(G2_CONNECTION_SPEED_KEY, this.g2ConnectionSpeed.toString());
        }
        if (this.g2Smoothing != null) {
            preferences.put(G2_SMOOTHING_KEY, this.g2Smoothing.toString());
        }
        if (this.g2ColorDepth != null) {
            preferences.put(G2_COLOR_DEPTH_KEY, this.g2ColorDepth.toString());
        }
        if ((string = this.getCardReaderName()) != null) {
            assert (string.length() < 8192);
            preferences.put(AUTO_MOUNT_SMARTCARDREADER_NAME, string);
        } else {
            preferences.remove(AUTO_MOUNT_SMARTCARDREADER_NAME);
        }
        preferences.putBoolean(CONNECT_PLAYBACK, this.isConnectPlaybackDevice());
        if (this.getPlaybackDevice() != null) {
            preferences.put(PLAYBACK_DEVICE, this.getPlaybackDevice());
        } else {
            preferences.remove(PLAYBACK_DEVICE);
        }
        if (this.getPlaybackFormat() != null) {
            preferences.put(PLAYBACK_FORMAT, this.getPlaybackFormat());
        } else {
            preferences.remove(PLAYBACK_FORMAT);
        }
        preferences.putInt(PLAYBACK_BUFSIZE, this.getPlaybackBufferSize());
        preferences.putBoolean(CONNECT_CAPTURE, this.isConnectCaptureDevice());
        if (this.getCaptureDevice() != null) {
            preferences.put(CAPTURE_DEVICE, this.getCaptureDevice());
        } else {
            preferences.remove(CAPTURE_DEVICE);
        }
        if (this.getCaptureFormat() != null) {
            preferences.put(CAPTURE_FORMAT, this.getCaptureFormat());
        } else {
            preferences.remove(CAPTURE_FORMAT);
        }
        preferences.putInt(CAPTURE_BUFSIZE, this.getCaptureBufferSize());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void importPreferences() {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(DevicePrefs.getOrCreateXmlFile(FILE_NAME)));
            Preferences.importPreferences(inputStream);
        }
        catch (InvalidPreferencesFormatException invalidPreferencesFormatException) {
        }
        catch (IOException iOException) {
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                }
                catch (IOException iOException) {}
            }
        }
    }

    public void importPreferences(String string) {
        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(DevicePrefs.getOrCreateXmlFile(FILE_NAME)));
            this.importPreferences(string, bufferedInputStream, this.rootPrefs);
        }
        catch (IOException iOException) {
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void importPreferences(String string, InputStream inputStream, Preferences preferences) {
        Preferences preferences2 = null;
        try {
            Preferences.importPreferences(inputStream);
            preferences2 = preferences.node(string);
        }
        catch (InvalidPreferencesFormatException invalidPreferencesFormatException) {
        }
        catch (IOException iOException) {
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                }
                catch (IOException iOException) {}
            }
        }
        if (preferences2 != null) {
            this.loadValues(preferences2);
        }
    }

    protected void loadValues(Preferences preferences) {
        String string = null;
        string = preferences.get(G2_CONNECTION_SPEED_KEY, null);
        if (string != null) {
            for (Enum enum_ : RCCore.Compression.values()) {
                if (!string.equalsIgnoreCase(enum_.toString())) continue;
                this.g2ConnectionSpeed = enum_;
                break;
            }
        }
        if ((string = preferences.get(G2_COLOR_DEPTH_KEY, null)) != null) {
            for (Enum enum_ : RCCore.ColorDepth.values()) {
                if (!string.equalsIgnoreCase(enum_.toString())) continue;
                this.g2ColorDepth = enum_;
                break;
            }
        }
        if ((string = preferences.get(G2_SMOOTHING_KEY, null)) != null) {
            for (Enum enum_ : RCCore.Smoothing.values()) {
                if (!string.equalsIgnoreCase(enum_.toString())) continue;
                this.g2Smoothing = enum_;
                break;
            }
        }
        this.setCardReaderName(preferences.get(AUTO_MOUNT_SMARTCARDREADER_NAME, null));
        this.setConnectPlaybackDevice(preferences.getBoolean(CONNECT_PLAYBACK, true));
        this.setPlaybackDevice(preferences.get(PLAYBACK_DEVICE, null));
        this.setPlaybackFormat(preferences.get(PLAYBACK_FORMAT, null));
        this.setPlaybackBufferSize(preferences.getInt(PLAYBACK_BUFSIZE, -1));
        this.setConnectCaptureDevice(preferences.getBoolean(CONNECT_CAPTURE, false));
        this.setCaptureDevice(preferences.get(CAPTURE_DEVICE, null));
        this.setCaptureFormat(preferences.get(CAPTURE_FORMAT, null));
        this.setCaptureBufferSize(preferences.getInt(CAPTURE_BUFSIZE, -1));
    }

    public static DevicePrefs getNode(String string) {
        DevicePrefs.importPreferences();
        Preferences preferences = Preferences.userRoot().node("/" + companyNameNoSpace + "Devices");
        try {
            if (preferences.nodeExists(string)) {
                DevicePrefs devicePrefs = new DevicePrefs();
                devicePrefs.importPreferences(string);
                return devicePrefs;
            }
        }
        catch (BackingStoreException backingStoreException) {
            // empty catch block
        }
        return null;
    }

    public RCCore.ColorDepth getG2ColorDepth() {
        return this.g2ColorDepth;
    }

    public RCCore.Compression getG2ConnectionSpeed() {
        return this.g2ConnectionSpeed;
    }

    public RCCore.Smoothing getG2Smoothing() {
        return this.g2Smoothing;
    }

    public void setG2ColorDepth(RCCore.ColorDepth colorDepth) {
        this.g2ColorDepth = colorDepth;
    }

    public void setG2ConnectionSpeed(RCCore.Compression compression) {
        this.g2ConnectionSpeed = compression;
    }

    public void setG2Smoothing(RCCore.Smoothing smoothing) {
        this.g2Smoothing = smoothing;
    }

    public String getCardReaderName() {
        return this.cardReaderName;
    }

    public void setCardReaderName(String string) {
        this.cardReaderName = string;
    }

    public boolean isConnectPlaybackDevice() {
        return this.isConnectPlaybackDevice;
    }

    public void setConnectPlaybackDevice(boolean bl) {
        this.isConnectPlaybackDevice = bl;
    }

    public String getPlaybackDevice() {
        return this.playbackDevice;
    }

    public void setPlaybackDevice(String string) {
        this.playbackDevice = string;
    }

    public String getPlaybackFormat() {
        return this.playbackFormat;
    }

    public void setPlaybackFormat(String string) {
        this.playbackFormat = string;
    }

    public int getPlaybackBufferSize() {
        return this.playbackBufferSize;
    }

    public void setPlaybackBufferSize(int n) {
        this.playbackBufferSize = n;
    }

    public boolean isConnectCaptureDevice() {
        return this.isConnectCaptureDevice;
    }

    public void setConnectCaptureDevice(boolean bl) {
        this.isConnectCaptureDevice = bl;
    }

    public String getCaptureDevice() {
        return this.captureDevice;
    }

    public void setCaptureDevice(String string) {
        this.captureDevice = string;
    }

    public String getCaptureFormat() {
        return this.captureFormat;
    }

    public void setCaptureFormat(String string) {
        this.captureFormat = string;
    }

    public int getCaptureBufferSize() {
        return this.captureBufferSize;
    }

    public void setCaptureBufferSize(int n) {
        this.captureBufferSize = n;
    }
}

