/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.util.StringUtils;
import com.raritan.rrc.util.modem.ModemConnector;
import com.raritan.tools.util.CryptoException;
import com.raritan.tools.util.StringCryptoUtil;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import javaclientlib.tr.Constants;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;
import javaclientlib.utils.RRCLogger;
import nn.pp.ext.devPref.DevicePrefs;
import nn.pp.ext.pref.ApplicationPreferences;

public class DevicePreferences
extends DevicePrefs {
    private static final String DESCRIPTION_KEY = "description";
    private static final String CONNECTION_TYPE_KEY = "connectionType";
    private static final String PRODUCT_TYPE_KEY = "productType";
    private static final String FIND_BY_KEY = "findBy";
    private static final String IP_ADDRESS_KEY = "ipAddress";
    private static final String SERVER_NAME_KEY = "serverName";
    private static final String DNS_NAME_KEY = "dnsName";
    private static final String PORT_KEY = "port";
    private static final String HTTPS_PORT_KEY = "httpsPort";
    private static final String PHONE_KEY = "phone";
    private static final String MODEM_KEY = "modem";
    private static final String CONNECTION_SPEED_KEY = "connectionSpeed";
    private static final String COLOR_DEPTH_KEY = "colorDepth";
    private static final String PROGRESSIVE_UPDATE_KEY = "progressiveUpdate";
    private static final String FLOW_CONTROL_KEY = "flowControl";
    private static final String SMOOTHING_KEY = "smoothing";
    private static final String FRAMES_PER_SECOND = "framesPerSecond";
    private static final String SECURITY_KEY = "privateKey";
    private static final String REMEMBER_KEY = "remember";
    private static final String USER_NAME_KEY = "userName";
    private static final String PASSWORD_KEY = "password";
    private static final String DEVICENAME_KEY = "deviceName";
    private String description = "";
    private int connectionType = 2;
    private String productType = "";
    private int findBy = 0;
    private String ip = "0.0.0.0";
    private String name = "";
    private String dnsName = "";
    private int port = Integer.parseInt(com.raritan.tools.util.Constants.NETWORKCONFIG_DEFAULT_PORT);
    private int httpsPort = new ApplicationPreferences().getDefaultHttpsPort();
    private String phone = "";
    private String modem = "";
    private int connectionSpeed = 0;
    private int colorDepth = 0;
    private boolean progressiveUpdate = false;
    private boolean flowControl = false;
    private int smoothing = 0;
    private int framesPerSecond = 0;
    private String privateKey = "";
    private String confirmKey = "";
    private int rememberOption = 2;
    private String userName = "";
    private String password = "";
    private ModemConnector modemConnector = null;
    private String deviceName = "";
    private static final String unassignedDeviceName = new String();

    public DevicePreferences() {
        this.rootPrefs = Preferences.userRoot().node("/" + Constants.COMPANY_NAME_NOSPACE + "Devices");
    }

    public String getNodeName() {
        switch (this.getConnectionType()) {
            case 2: {
                switch (this.getFindBy()) {
                    case 0: {
                        return this.getIp();
                    }
                    case 1: {
                        return this.getName();
                    }
                    case 2: {
                        return this.getDnsName();
                    }
                }
                return null;
            }
            case 1: {
                return this.getPhone();
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void exportPreferences() {
        Preferences preferences = Preferences.userRoot().node("/" + Constants.COMPANY_NAME_NOSPACE + "Devices");
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(DevicePreferences.getOrCreateXmlFile(FILE_NAME)));
            preferences.exportSubtree(outputStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
        }
        catch (IOException iOException) {
        }
        catch (BackingStoreException backingStoreException) {
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ByteArrayOutputStream CCExportPrefs() {
        Preferences preferences = Preferences.userRoot().node("VideoSetting");
        preferences.putInt(CONNECTION_SPEED_KEY, this.connectionSpeed);
        preferences.putInt(COLOR_DEPTH_KEY, this.colorDepth);
        preferences.putInt(PROGRESSIVE_UPDATE_KEY, this.progressiveUpdate ? 1 : 0);
        preferences.putInt(FLOW_CONTROL_KEY, this.flowControl ? 1 : 0);
        preferences.putInt(SMOOTHING_KEY, this.smoothing);
        preferences.putInt(FRAMES_PER_SECOND, this.framesPerSecond);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            preferences.exportSubtree(byteArrayOutputStream);
        }
        catch (IOException iOException) {}
        catch (BackingStoreException backingStoreException) {}
        finally {
            return byteArrayOutputStream;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void exportPreferences(String string) {
        Preferences preferences = this.rootPrefs.node(string);
        this.storeToPreferences(preferences);
        preferences.put(DESCRIPTION_KEY, this.description);
        preferences.putInt(CONNECTION_TYPE_KEY, this.connectionType);
        preferences.put(PRODUCT_TYPE_KEY, this.productType);
        preferences.putInt(FIND_BY_KEY, this.findBy);
        preferences.put(IP_ADDRESS_KEY, this.ip);
        if (this.isPersistDeviceName()) {
            preferences.put(DEVICENAME_KEY, this.getDeviceName());
        }
        preferences.put(SERVER_NAME_KEY, this.getName());
        preferences.put(DNS_NAME_KEY, this.dnsName);
        preferences.putInt(PORT_KEY, this.port);
        preferences.putInt(HTTPS_PORT_KEY, this.httpsPort);
        preferences.put(PHONE_KEY, this.phone);
        preferences.put(MODEM_KEY, this.modem);
        preferences.putInt(CONNECTION_SPEED_KEY, this.connectionSpeed);
        preferences.putInt(COLOR_DEPTH_KEY, this.colorDepth);
        preferences.putInt(PROGRESSIVE_UPDATE_KEY, this.progressiveUpdate ? 1 : 0);
        preferences.putInt(FLOW_CONTROL_KEY, this.flowControl ? 1 : 0);
        preferences.putInt(SMOOTHING_KEY, this.smoothing);
        preferences.putInt(FRAMES_PER_SECOND, this.framesPerSecond);
        if (StringUtils.notNullOrEmpty(this.privateKey)) {
            try {
                preferences.put(SECURITY_KEY, StringCryptoUtil.encrypt(this.privateKey, "l2Ge0FQQwlu2yGSF+Jh/nZdhntBUEMJb"));
            }
            catch (CryptoException cryptoException) {
                RRCLogger.logException(cryptoException);
            }
            catch (IOException iOException) {
                RRCLogger.logException(iOException);
            }
        } else {
            preferences.put(SECURITY_KEY, "");
        }
        preferences.put(USER_NAME_KEY, this.userName);
        if (StringUtils.notNullOrEmpty(this.password)) {
            try {
                preferences.put(PASSWORD_KEY, StringCryptoUtil.encrypt(this.password, "l2Ge0FQQwlu2yGSF+Jh/nZdhntBUEMJb"));
            }
            catch (CryptoException cryptoException) {
                RRCLogger.logException(cryptoException);
            }
            catch (IOException iOException) {
                RRCLogger.logException(iOException);
            }
        } else {
            preferences.put(PASSWORD_KEY, "");
        }
        preferences.putInt(REMEMBER_KEY, this.rememberOption);
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(DevicePreferences.getOrCreateXmlFile(FILE_NAME)));
            this.rootPrefs.exportSubtree(outputStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
        }
        catch (IOException iOException) {
        }
        catch (BackingStoreException backingStoreException) {
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

    public void importPreferences(String string, String string2) {
        ByteArrayInputStream byteArrayInputStream = null;
        byteArrayInputStream = new ByteArrayInputStream(string2.getBytes());
        this.importPreferences(string, byteArrayInputStream, Preferences.userRoot());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void importPreferences(String string, InputStream inputStream, Preferences preferences) {
        Preferences preferences2 = null;
        try {
            Preferences.importPreferences(inputStream);
            preferences2 = preferences.node(string);
            this.description = preferences2.get(DESCRIPTION_KEY, "");
            this.connectionType = preferences2.getInt(CONNECTION_TYPE_KEY, 2);
            this.productType = preferences2.get(PRODUCT_TYPE_KEY, "");
            this.findBy = preferences2.getInt(FIND_BY_KEY, 0);
            this.ip = preferences2.get(IP_ADDRESS_KEY, "0.0.0.0");
            this.name = preferences2.get(SERVER_NAME_KEY, "");
            this.dnsName = preferences2.get(DNS_NAME_KEY, "");
            this.port = preferences2.getInt(PORT_KEY, Integer.parseInt(com.raritan.tools.util.Constants.NETWORKCONFIG_DEFAULT_PORT));
            this.httpsPort = preferences2.getInt(HTTPS_PORT_KEY, new ApplicationPreferences().getDefaultHttpsPort());
            this.phone = preferences2.get(PHONE_KEY, "0");
            this.modem = preferences2.get(MODEM_KEY, "");
            this.connectionSpeed = preferences2.getInt(CONNECTION_SPEED_KEY, 0);
            this.colorDepth = preferences2.getInt(COLOR_DEPTH_KEY, 0);
            String string2 = preferences2.get(PROGRESSIVE_UPDATE_KEY, "false");
            this.progressiveUpdate = string2.equals("1") || string2.equalsIgnoreCase("true");
            string2 = preferences2.get(FLOW_CONTROL_KEY, "false");
            this.flowControl = string2.equals("1") || string2.equalsIgnoreCase("true");
            this.smoothing = preferences2.getInt(SMOOTHING_KEY, 0);
            this.framesPerSecond = preferences2.getInt(FRAMES_PER_SECOND, 0);
            this.privateKey = preferences2.get(SECURITY_KEY, "");
            if (StringUtils.notNullOrEmpty(this.privateKey)) {
                try {
                    this.privateKey = StringCryptoUtil.decrypt(this.privateKey, "l2Ge0FQQwlu2yGSF+Jh/nZdhntBUEMJb");
                }
                catch (CryptoException cryptoException) {
                    RRCLogger.logException(cryptoException);
                }
                catch (IOException iOException) {
                    RRCLogger.logException(iOException);
                }
            }
            this.userName = preferences2.get(USER_NAME_KEY, "");
            this.password = preferences2.get(PASSWORD_KEY, "");
            if (StringUtils.notNullOrEmpty(this.password)) {
                try {
                    this.password = StringCryptoUtil.decrypt(this.password, "l2Ge0FQQwlu2yGSF+Jh/nZdhntBUEMJb");
                }
                catch (CryptoException cryptoException) {
                    RRCLogger.logException(cryptoException);
                }
                catch (IOException iOException) {
                    RRCLogger.logException(iOException);
                }
            }
            this.rememberOption = preferences2.getInt(REMEMBER_KEY, 0);
            this.setDeviceName(preferences2.get(DEVICENAME_KEY, unassignedDeviceName));
        }
        catch (InvalidPreferencesFormatException invalidPreferencesFormatException) {
            DevicePreferences.exportPreferences();
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

    public static void deleteNode(String string) {
        Preferences preferences = Preferences.userRoot().node("/" + Constants.COMPANY_NAME_NOSPACE + "Devices");
        try {
            preferences.node(string).removeNode();
            DevicePreferences.exportPreferences();
        }
        catch (BackingStoreException backingStoreException) {
            // empty catch block
        }
    }

    public static String[] returnNodes() {
        DevicePreferences.importPreferences();
        Preferences preferences = Preferences.userRoot().node("/" + Constants.COMPANY_NAME_NOSPACE + "Devices");
        String[] stringArray = null;
        try {
            stringArray = preferences.childrenNames();
        }
        catch (BackingStoreException backingStoreException) {
            // empty catch block
        }
        return stringArray;
    }

    public static DevicePreferences getNode(String string) {
        DevicePreferences.importPreferences();
        Preferences preferences = Preferences.userRoot().node("/" + Constants.COMPANY_NAME_NOSPACE + "Devices");
        try {
            if (preferences.nodeExists(string)) {
                DevicePreferences devicePreferences = new DevicePreferences();
                devicePreferences.importPreferences(string);
                return devicePreferences;
            }
        }
        catch (BackingStoreException backingStoreException) {
            // empty catch block
        }
        return null;
    }

    protected static File getOrCreateXmlFile(String string) {
        DevicePreferences.setComapnyName(Constants.COMPANY_NAME_NOSPACE);
        return DevicePrefs.getOrCreateXmlFile(string);
    }

    public TRSRVR_COMP_PARAMS getCompParams() {
        TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = new TRSRVR_COMP_PARAMS();
        int n = 0;
        n = this.progressiveUpdate ? (n |= 0x8000) : (n &= 0xFFFF7FFF);
        n |= 0x18;
        n = this.flowControl ? (n |= 0x80) : (n &= 0xFFFFFF7F);
        tRSRVR_COMP_PARAMS.setFlags(n);
        tRSRVR_COMP_PARAMS.setCCT((short)this.colorDepth);
        tRSRVR_COMP_PARAMS.setCacheDepth((short)0);
        tRSRVR_COMP_PARAMS.setCompressMode(2);
        tRSRVR_COMP_PARAMS.setSpeed(this.connectionSpeed);
        tRSRVR_COMP_PARAMS.setMinFrameTime(200 / (this.framesPerSecond + 1));
        tRSRVR_COMP_PARAMS.setMaxFrameTime(0);
        tRSRVR_COMP_PARAMS.setSmoothing(this.smoothing);
        return tRSRVR_COMP_PARAMS;
    }

    public int getColorDepth() {
        return this.colorDepth;
    }

    public int getConnectionSpeed() {
        return this.connectionSpeed;
    }

    public int getConnectionType() {
        return this.connectionType;
    }

    public String getProductType() {
        return this.productType;
    }

    public void setProductType(String string) {
        this.productType = string;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDnsName() {
        return this.dnsName;
    }

    public int getFindBy() {
        return this.findBy;
    }

    public boolean isFlowControl() {
        return this.flowControl;
    }

    public String getIp() {
        if ("".equals(this.ip)) {
            return "0.0.0.0";
        }
        return this.ip;
    }

    public InetAddress getInetAddess() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(this.getIp());
        }
        catch (UnknownHostException unknownHostException) {
            unknownHostException.printStackTrace();
        }
        return inetAddress;
    }

    public String getKey() {
        return this.privateKey;
    }

    public String getModem() {
        return this.modem;
    }

    public String getName() {
        if (this.isDeviceNameInStore()) {
            return this.getDeviceName();
        }
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public int getPort() {
        return this.port;
    }

    public int getHttpsPort() {
        return this.httpsPort;
    }

    public boolean isProgressiveUpdate() {
        return this.progressiveUpdate;
    }

    public int getSmoothing() {
        return this.smoothing;
    }

    public void setColorDepth(int n) {
        this.colorDepth = n;
    }

    public void setConnectionSpeed(int n) {
        this.connectionSpeed = n;
    }

    public void setConnectionType(int n) {
        this.connectionType = n;
    }

    public void setDescription(String string) {
        this.description = string;
    }

    public void setDnsName(String string) {
        this.dnsName = string;
    }

    public void setFindBy(int n) {
        this.findBy = n;
    }

    public void setFlowControl(boolean bl) {
        this.flowControl = bl;
    }

    public void setIp(String string) {
        this.ip = null == string || "".equals(string) ? "0.0.0.0" : string;
    }

    public void setKey(String string) {
        this.privateKey = string;
    }

    public void setModem(String string) {
        this.modem = string;
    }

    public void setName(String string) {
        this.setDeviceName(string);
    }

    public void setPhone(String string) {
        this.phone = string;
    }

    public void setPort(int n) {
        this.port = n;
    }

    public void setHttpsPort(int n) {
        this.httpsPort = n;
    }

    public void setProgressiveUpdate(boolean bl) {
        this.progressiveUpdate = bl;
    }

    public void setSmoothing(int n) {
        this.smoothing = n;
    }

    public String getConfirmKey() {
        return this.confirmKey;
    }

    public int getFramesPerSecond() {
        return this.framesPerSecond;
    }

    public void setConfirmKey(String string) {
        this.confirmKey = string;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String string) {
        this.password = string;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String string) {
        this.userName = string;
    }

    public int getRememberOption() {
        return this.rememberOption;
    }

    public void setRememberOption(int n) {
        this.rememberOption = n;
    }

    public void setFramesPerSecond(int n) {
        this.framesPerSecond = n;
    }

    public void setModemConnector(ModemConnector modemConnector) {
        this.modemConnector = modemConnector;
    }

    public ModemConnector getModemConnector() {
        return this.modemConnector;
    }

    public synchronized String getDeviceName() {
        return this.deviceName;
    }

    public synchronized void setDeviceName(String string) {
        this.deviceName = string;
    }

    public boolean isDeviceNameInStore() {
        return this.getDeviceName() != unassignedDeviceName;
    }

    private boolean isPersistDeviceName() {
        return this.getDeviceName() != unassignedDeviceName;
    }
}

