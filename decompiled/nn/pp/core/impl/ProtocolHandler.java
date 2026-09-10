/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core.impl;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.core.impl.DeviceConnector;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;

public abstract class ProtocolHandler<E extends Exception>
extends Thread {
    protected DeviceConnector connector;
    protected MonitoringDataInputStream is;
    protected MonitoringDataOutputStream os;
    protected Logger logger;
    protected String host;
    protected String username;
    protected String password;
    protected String httpSessionID;
    protected String rdmSessionID;
    protected String portID;
    protected boolean shouldRun = false;
    protected boolean connected = false;
    protected boolean inInitialHandshake = false;
    private MessageDigest digest;
    protected static final int socketTimeoutInHandshake = 60000;
    protected int versionMajor;
    protected int versionMinor;

    protected static String getVersionString(int n, int n2, char c) {
        String string = "" + n;
        if (n < 10) {
            string = "0" + string;
        }
        String string2 = "" + n2;
        if (n2 < 10) {
            string2 = "0" + string2;
        }
        return new String(string + c + string2);
    }

    protected abstract String getProtocolName();

    protected abstract void loadPdus();

    protected abstract void processInitialHandshake(int var1) throws IOException, E;

    protected abstract void negotiateProtocolVersion() throws IOException, E;

    protected abstract void processProtocol() throws IOException, E;

    protected abstract void disconnected(Exception var1);

    protected abstract E loadException(String var1);

    public void init(DeviceConnector deviceConnector, Logger logger) throws IOException {
        this.connector = deviceConnector;
        this.is = deviceConnector.getInputStream();
        this.os = deviceConnector.getOutputStream();
        this.host = deviceConnector.getHost();
        this.logger = logger;
        this.loadPdus();
    }

    public void connect(String string, String string2, String string3, String string4, String string5) throws IOException, E {
        this.portID = string;
        this.username = string2;
        this.password = string3;
        this.httpSessionID = string4;
        this.rdmSessionID = string5;
        this.enterInitialHandshake();
        if (this.versionMajor == -1 || this.versionMinor == -1) {
            this.negotiateProtocolVersion();
        }
        this.shouldRun = true;
        this.start();
        this.connected = true;
    }

    public void close() {
        this.shouldRun = false;
    }

    public void dispose() {
        this.shouldRun = false;
        try {
            this.join(1000L);
            if (this.isAlive()) {
                this.logger.log(Level.WARNING, T._("Protocol thread still alive, killing forcefully!"));
                this.stop();
            }
            if (this.connector != null) {
                this.connector = null;
            }
            if (this.is != null) {
                this.is = null;
            }
            if (this.os != null) {
                this.os = null;
            }
            if (this.digest != null) {
                this.digest = null;
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    protected void enterInitialHandshake() throws IOException {
        this.inInitialHandshake = true;
        this.connector.setSocketTimeout(60000);
        this.connector.setTcpNoDelay(true);
    }

    protected void initialHandshakeFinished() throws IOException {
        this.inInitialHandshake = false;
        this.connector.setSocketTimeout(0);
    }

    @Override
    public void run() {
        block2: {
            this.logger.log(Level.INFO, MessageFormat.format(T._("{0} handler thread started."), this.getProtocolName()));
            try {
                this.processProtocol();
            }
            catch (Exception exception) {
                if (!this.shouldRun) break block2;
                this.logger.log(Level.SEVERE, MessageFormat.format(T._("Error occured in {0} protocol handling"), this.getProtocolName()), exception);
                this.disconnected(exception);
            }
        }
        this.logger.log(Level.INFO, MessageFormat.format(T._("{0} handler thread finished."), this.getProtocolName()));
    }

    protected int readServerMessageType() throws IOException {
        return this.is.readUnsignedByte();
    }

    public String binToHex(byte[] byArray) {
        String string = "";
        for (int i = 0; i < byArray.length; ++i) {
            int n = byArray[i] & 0xFF;
            if (n <= 15) {
                string = string + "0";
            }
            string = string + Integer.toHexString(n).toUpperCase();
        }
        return string;
    }

    public String getChallengeResponse(byte[] byArray, String string) throws IOException, E {
        try {
            this.digest = MessageDigest.getInstance(string);
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw this.loadException(T._("Could not find " + string + " algorithm."));
        }
        this.digest.update(byArray);
        this.digest.update(this.httpSessionID.getBytes("ISO-8859-1"), 0, this.httpSessionID.length());
        byte[] byArray2 = this.digest.digest();
        String string2 = this.binToHex(byArray2);
        return string2;
    }
}

