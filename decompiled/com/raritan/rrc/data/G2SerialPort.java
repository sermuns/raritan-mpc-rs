/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.swing.JApplet
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.DummyHostNameVerifier;
import com.raritan.rrc.data.DummyTrustManager1;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.Stream;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.RSCView;
import com.raritan.tools.util.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import javaclientlib.utils.RRCGeneralException;
import javaclientlib.utils.RRCLogger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.swing.JApplet;

public class G2SerialPort
extends Port {
    private RSCView rscView;

    @Override
    public Stream getStream() {
        return null;
    }

    @Override
    public DeviceView getView() {
        return this.rscView;
    }

    public void setView(RSCView rSCView) {
        this.rscView = rSCView;
    }

    @Override
    public void connect() {
        try {
            this.downloadRSCJar();
            this.setConnected(true);
            this.setState("CONNECTED");
            this.firePropertyChange("DEVICE_PORT_VIEW_ADD", null, null);
        }
        catch (KeyManagementException keyManagementException) {
            RRCLogger.logException(keyManagementException);
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            RRCLogger.logException(noSuchAlgorithmException);
        }
        catch (RRCGeneralException rRCGeneralException) {
            RRCLogger.logException(rRCGeneralException);
        }
        catch (IOException iOException) {
            RRCLogger.logException(iOException);
        }
    }

    public void downloadRSCJar() throws RRCGeneralException, KeyManagementException, NoSuchAlgorithmException, IOException {
        if (this.device.getRscFileHandle() == null) {
            this.deleteRSCJar();
            File file = null;
            try {
                JarEntry jarEntry;
                SSLContext sSLContext = SSLContext.getInstance("SSLv3");
                TrustManager[] trustManagerArray = new TrustManager[]{new DummyTrustManager1()};
                sSLContext.init(null, trustManagerArray, null);
                SSLSocketFactory sSLSocketFactory = sSLContext.getSocketFactory();
                HttpsURLConnection.setDefaultSSLSocketFactory(sSLSocketFactory);
                HttpsURLConnection.setDefaultHostnameVerifier(new DummyHostNameVerifier());
                DeviceConnector deviceConnector = this.device.getDeviceConnector();
                String string = "https://" + Util.getURLCompatibleIP(deviceConnector.getInetAddress(), this.device.getHttpsPort()) + "/RSC.jar";
                URL uRL = new URL(string);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)uRL.openConnection();
                file = new File(this.getTempDirPath() + File.separator + this.getRSCJarName());
                JarInputStream jarInputStream = new JarInputStream(httpsURLConnection.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                JarOutputStream jarOutputStream = new JarOutputStream(fileOutputStream);
                byte[] byArray = new byte[1024];
                block4: while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                    jarOutputStream.putNextEntry(jarEntry);
                    while (true) {
                        int n;
                        if ((n = jarInputStream.read(byArray, 0, byArray.length)) <= 0) {
                            jarInputStream.closeEntry();
                            jarOutputStream.closeEntry();
                            continue block4;
                        }
                        jarOutputStream.write(byArray, 0, n);
                    }
                }
                jarOutputStream.flush();
                jarInputStream.close();
                jarOutputStream.close();
            }
            catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                RRCLogger.logException(noSuchAlgorithmException);
            }
            catch (KeyManagementException keyManagementException) {
                RRCLogger.logException(keyManagementException);
            }
            catch (IOException iOException) {
                RRCLogger.logException(iOException);
            }
            RRCLogger.log(300, 4, "RSC Jar path " + file.getCanonicalPath());
            this.device.setRscFileHandle(file);
        } else {
            RRCLogger.log(300, 4, this.getDevice().getRscFileHandle().getCanonicalPath() + " already exists");
        }
    }

    private boolean deleteRSCJar() throws IOException {
        File file = new File(this.getTempDirPath() + File.pathSeparator + this.getRSCJarName());
        boolean bl = true;
        if (file.exists()) {
            bl = file.delete();
            RRCLogger.log(300, 4, "Deleting file " + file.getCanonicalPath() + " : " + bl);
        }
        return bl;
    }

    private String getTempDirPath() throws IOException {
        File file = File.createTempFile("rsc", ".tmp");
        RRCLogger.log(300, 4, "Temp dir path " + file.getParentFile().getCanonicalPath());
        String string = file.getParentFile().getCanonicalPath();
        file.delete();
        return string;
    }

    public String getRSCJarName() {
        DeviceConnector deviceConnector = this.getDevice().getDeviceConnector();
        String string = deviceConnector.getInetAddress().getHostAddress();
        String string2 = string.replace(':', '.');
        return "rsc_" + string2 + ".jar";
    }

    @Override
    public void disconnect() {
        if (!this.isConnected()) {
            return;
        }
        this.setConnected(false);
        this.setActive(false);
        this.setState("AVAILABLE");
        JApplet jApplet = this.rscView.getApplet();
        try {
            Method method = jApplet.getClass().getMethod("disconnect", null);
            method.invoke((Object)jApplet, null);
        }
        catch (SecurityException securityException) {
            RRCLogger.logException(securityException);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            RRCLogger.logException(noSuchMethodException);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            RRCLogger.logException(illegalArgumentException);
        }
        catch (IllegalAccessException illegalAccessException) {
            RRCLogger.logException(illegalAccessException);
        }
        catch (InvocationTargetException invocationTargetException) {
            RRCLogger.logException(invocationTargetException);
        }
        super.disconnect();
        this.firePropertyChange("DEVICE_PORT_VIEW_REMOVE", null, null);
        this.setView(null);
        this.setDeviceView(null);
    }
}

