/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.net.ssl.SSLContext
 *  com.sun.net.ssl.TrustManager
 */
package javaclientlib.utils;

import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javaclientlib.utils.DummyTrustManager;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import sun.security.jgss.SunProvider;

public class SecureSocket {
    private int iPort;
    private String sHostName;
    private Socket objSocket;
    private SSLSocket objSSLSocket;
    private boolean useTLS = false;

    public SecureSocket() {
    }

    public SecureSocket(Socket socket, String string, int n, boolean bl) {
        this.objSocket = socket;
        this.sHostName = string;
        this.iPort = n;
        this.useTLS = bl;
    }

    public void setSocket(Socket socket) {
        this.objSocket = socket;
    }

    public void setHostName(String string) {
        this.sHostName = string;
    }

    public void setPort(int n) {
        this.iPort = n;
    }

    public void connect() throws NoSuchAlgorithmException, KeyManagementException, IOException {
        Security.addProvider(new SunProvider());
        SSLContext sSLContext = this.useTLS ? SSLContext.getInstance((String)"TLSv1") : SSLContext.getInstance((String)"SSLv3");
        TrustManager[] trustManagerArray = new TrustManager[]{new DummyTrustManager()};
        sSLContext.init(null, trustManagerArray, null);
        SSLSocketFactory sSLSocketFactory = sSLContext.getSocketFactory();
        this.objSSLSocket = (SSLSocket)sSLSocketFactory.createSocket(this.objSocket, this.sHostName, this.iPort, false);
        if (this.useTLS) {
            this.objSSLSocket.setEnabledProtocols(new String[]{"TLSv1", "SSLv3"});
        } else {
            this.objSSLSocket.setEnabledProtocols(new String[]{"SSLv3"});
        }
        this.objSSLSocket.startHandshake();
    }

    public InputStream getSecureInputStream() throws IOException {
        return this.objSSLSocket.getInputStream();
    }

    public OutputStream getSecureOutputStream() throws IOException {
        return this.objSSLSocket.getOutputStream();
    }
}

