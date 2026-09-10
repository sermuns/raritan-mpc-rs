/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.core.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import nn.pp.core.T;
import nn.pp.core.Util;
import nn.pp.core.impl.IPAddressUtil;
import nn.pp.core.impl.MonitoringDataInputStream;
import nn.pp.core.impl.MonitoringDataOutputStream;

public class DeviceConnector {
    private static final int CONNECT_TIMEOUT = 15000;
    private Logger logger;
    private String sockerr = "";
    private X509TrustManager trustManager;
    private Socket socket;
    protected MonitoringDataInputStream inputStream;
    protected MonitoringDataOutputStream outputStream;
    protected Object streamMtx = new Object();
    private String host;
    private X509TrustManager trustAll = new X509TrustManager(){

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509CertificateArray, String string) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509CertificateArray, String string) {
        }
    };
    private static HashMap<String, InetSocketAddress> addressCache = new HashMap();

    private static InetAddress getInetAddress(String string) throws UnknownHostException {
        if (string.charAt(0) == '[') {
            if (string.length() > 2 && string.charAt(string.length() - 1) == ']') {
                string = string.substring(1, string.length() - 1);
            } else {
                throw new UnknownHostException(string + ": invalid IPv6 address");
            }
        }
        if (Character.digit(string.charAt(0), 16) != -1 || string.charAt(0) == ':') {
            byte[] byArray = null;
            byArray = IPAddressUtil.textToNumericFormatV4(string);
            if (byArray == null) {
                byArray = IPAddressUtil.textToNumericFormatV6(string);
            }
            if (byArray != null) {
                return InetAddress.getByAddress("", byArray);
            }
        }
        return InetAddress.getByName(string);
    }

    private static synchronized InetSocketAddress getInetSocketAddress(String string, int n) throws UnknownHostException {
        String string2 = string + ":" + n;
        if (addressCache.containsKey(string2)) {
            return addressCache.get(string2);
        }
        InetSocketAddress inetSocketAddress = new InetSocketAddress(DeviceConnector.getInetAddress(string), n);
        addressCache.put(string2, inetSocketAddress);
        return inetSocketAddress;
    }

    public DeviceConnector(Logger logger, X509TrustManager x509TrustManager) {
        this.logger = logger;
    }

    public String getHost() {
        return this.host;
    }

    private Socket connect(String string, int n) {
        String string2 = "" + string + ':' + n;
        this.logger.log(Level.INFO, MessageFormat.format(T._("Trying connection to {0}"), string2));
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(DeviceConnector.getInetSocketAddress(string, n), 15000);
            this.logger.log(Level.INFO, MessageFormat.format(T._("Successfully connected to {0}"), string2));
        }
        catch (Exception exception) {
            this.logger.log(Level.SEVERE, MessageFormat.format(T._("Connection to {0} FAILED"), string2), exception);
            this.sockerr = exception.getMessage();
        }
        return socket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Socket connectSSLWithSocket(String string, int n) {
        String string2 = "" + string + ':' + n;
        this.logger.log(Level.INFO, MessageFormat.format(T._("trying SSL connection to {0}"), string2));
        SSLSocket sSLSocket = null;
        if (this.trustManager == null) {
            this.trustManager = this.trustAll;
        }
        TrustManager[] trustManagerArray = new TrustManager[]{this.trustManager};
        try {
            SSLContext sSLContext = SSLContext.getInstance("TLSv1");
            sSLContext.init(null, trustManagerArray, null);
            SSLSocketFactory sSLSocketFactory = sSLContext.getSocketFactory();
            sSLSocket = (SSLSocket)sSLSocketFactory.createSocket(this.socket, string, n, true);
            this.socket = sSLSocket;
            Object object = this.streamMtx;
            synchronized (object) {
                this.outputStream = null;
                this.inputStream = null;
            }
            this.logger.log(Level.INFO, MessageFormat.format(T._("SSL connected successfully to {0}"), string2));
            this.logger.log(Level.FINE, T._("SSL running handshake..."));
            sSLSocket.startHandshake();
            this.logger.log(Level.FINE, T._("SSL handshake successful :-)"));
            this.logger.log(Level.FINER, MessageFormat.format(T._("SSL {0}"), sSLSocket.getSession()));
        }
        catch (Exception exception) {
            this.logger.log(Level.SEVERE, MessageFormat.format(T._("SSL connection to {0} FAILED"), string2), exception);
            this.sockerr = exception.getMessage();
            sSLSocket = null;
        }
        return sSLSocket;
    }

    private Socket connectSSL(String string, int n) {
        String string2 = "" + string + ':' + n;
        this.logger.log(Level.INFO, MessageFormat.format(T._("trying SSL connection to {0}"), string2));
        SSLSocket sSLSocket = null;
        if (this.trustManager == null) {
            this.trustManager = this.trustAll;
        }
        TrustManager[] trustManagerArray = new TrustManager[]{this.trustManager};
        try {
            SSLContext sSLContext = SSLContext.getInstance("TLSv1");
            sSLContext.init(null, trustManagerArray, null);
            SSLSocketFactory sSLSocketFactory = sSLContext.getSocketFactory();
            sSLSocket = (SSLSocket)sSLSocketFactory.createSocket();
            sSLSocket.connect(DeviceConnector.getInetSocketAddress(string, n), 15000);
            this.logger.log(Level.INFO, MessageFormat.format(T._("SSL connected successfully to {0}"), string2));
            this.logger.log(Level.FINE, T._("SSL running handshake..."));
            sSLSocket.startHandshake();
            this.logger.log(Level.FINE, T._("SSL handshake successful :-)"));
            this.logger.log(Level.FINER, MessageFormat.format(T._("SSL {0}"), sSLSocket.getSession()));
        }
        catch (Exception exception) {
            this.logger.log(Level.SEVERE, MessageFormat.format(T._("SSL connection to {0} FAILED"), string2), exception);
            this.sockerr = exception.getMessage();
            sSLSocket = null;
        }
        return sSLSocket;
    }

    public void connect(String string, int n, boolean bl) throws IOException {
        string = Util.getURLCompatibleIP(string);
        this.socket = bl ? this.connectSSL(string, n) : this.connect(string, n);
        if (this.socket == null) {
            throw new IOException(MessageFormat.format(T._("Cannot connect to device: {0}"), this.sockerr));
        }
        this.host = string;
    }

    public void disconnect() {
        if (this.socket != null) {
            try {
                this.socket.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MonitoringDataInputStream getInputStream() throws IOException {
        Object object = this.streamMtx;
        synchronized (object) {
            if (this.inputStream == null) {
                this.inputStream = new MonitoringDataInputStream(new BufferedInputStream(this.socket.getInputStream(), 32768));
            }
            return this.inputStream;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MonitoringDataOutputStream getOutputStream() throws IOException {
        Object object = this.streamMtx;
        synchronized (object) {
            if (this.outputStream == null) {
                this.outputStream = new MonitoringDataOutputStream(this.socket.getOutputStream());
            }
            return this.outputStream;
        }
    }

    public void setSocketTimeout(int n) throws IOException {
        this.socket.setSoTimeout(n);
    }

    public void setTcpNoDelay(boolean bl) throws IOException {
        this.socket.setTcpNoDelay(bl);
    }

    public void writeCCSGproxyModePrefix(String string) throws IOException {
        String string2 = "<CSC_Connect ConnectionID=\"" + string + "\"/>";
        byte[] byArray = string2.getBytes();
        int n = 4 + byArray.length + 1;
        MonitoringDataOutputStream monitoringDataOutputStream = this.getOutputStream();
        monitoringDataOutputStream.writeInt(n);
        monitoringDataOutputStream.write(byArray, 0, byArray.length);
        monitoringDataOutputStream.writeByte(0);
        monitoringDataOutputStream.flush();
    }
}

