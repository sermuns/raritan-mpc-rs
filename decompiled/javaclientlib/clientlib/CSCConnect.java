/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  sun.misc.BASE64Decoder
 *  sun.misc.BASE64Encoder
 */
package javaclientlib.clientlib;

import java.io.DataInputStream;
import java.io.FilterOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import javaclientlib.tr.Constants;
import javaclientlib.tr.TRRSP_ID_DATA;
import javaclientlib.utils.RC4Cipher;
import javaclientlib.utils.RRCLogger;
import javaclientlib.utils.RRCUtil;
import javaclientlib.utils.SecureSocket;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CSCConnect {
    private int iConnected = 0;
    private int iIsSSL = 0;
    private int iAuthenticated = 0;
    private Socket objCSCSocket = null;
    private boolean boolNeedSSL = false;
    private DataInputStream objDataInputStream = null;
    private FilterOutputStream objFilterOutputStream;
    private SecureSocket objSSLSocket;
    private static int MAX_CSC_INFO = 1024;
    private static byte[] CSC_Discover = new byte[]{0, 0, 0, 20, 60, 67, 83, 67, 95, 68, 105, 115, 99, 111, 118, 101, 114, 47, 62, 0};
    private static int CSC_Dis_Len = 20;
    private static byte[] byNull = new byte[]{0};

    protected void finalize() {
        this.disconnect();
    }

    public int connect(int n, short s, byte[] byArray) {
        try {
            this.iConnected = 0;
            this.iIsSSL = 0;
            InetAddress inetAddress = null;
            inetAddress = InetAddress.getByAddress(RRCUtil.getBytesForInt(n));
            if (s == 0) {
                s = (short)Constants.TR_PORT_BASE;
            }
            this.objCSCSocket = new Socket(inetAddress, (int)s);
            if (this.objCSCSocket == null) {
                return -2;
            }
            this.objCSCSocket.setTcpNoDelay(true);
            this.createStreams();
            this.iConnected = 1;
            return 0;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return -2;
        }
    }

    public byte[] GetCSCInfo() {
        String string = new String("<CSC_Ack/>");
        byte[] byArray = new byte[200];
        byte[] byArray2 = new byte[4];
        boolean bl = false;
        int n = this.read(byArray, 0, 4);
        if (n < 0) {
            return null;
        }
        int n2 = this.getIntForBytes(byArray, 0);
        if (n2 > 200) {
            return null;
        }
        n = this.read(byArray, 0, n2);
        n2 = string.length() + 4 + 1;
        n = this.write(RRCUtil.getBytesForInt(n2), 4);
        n = this.write(string.getBytes(), string.length());
        n = this.write(byNull, 1);
        if (n < 0) {
            return null;
        }
        n = this.read(byArray2, 0, 4);
        if (n < 0) {
            return null;
        }
        n2 = this.getIntForBytes(byArray2, 0);
        byArray2 = new byte[n2 -= 4];
        if (byArray2 == null) {
            return null;
        }
        n = this.read(byArray2, 0, n2);
        if (n < 0) {
            return null;
        }
        return byArray2;
    }

    public int startNewCSCSession(String string, String string2, String string3, String string4, String string5) {
        String string6 = new String("<CSC_Start_Session ProtocolID=\"" + string + "\"/>");
        byte[] byArray = new byte[200];
        int n = string6.length() + 1 + 4;
        int n2 = this.write(RRCUtil.getBytesForInt(n), 4);
        n2 = this.write(string6.getBytes(), string6.length());
        n2 = this.write(byNull, 1);
        if (n2 < 0) {
            return n2;
        }
        if (string4.equals("SSL")) {
            n2 = this.startSSL();
            if (n2 != 0) {
                return n2;
            }
            this.createStreams();
        } else {
            this.boolNeedSSL = false;
        }
        if (string5.equals("CSC")) {
            string6 = new String("<CSC_Auth UserName=\"" + string2 + "\" Password=\"" + string3 + "\"/>");
            n = string6.length() + 1 + 4;
            n2 = this.write(RRCUtil.getBytesForInt(n), 4);
            n2 = this.write(string6.getBytes(), string6.length());
            n2 = this.write(byNull, 1);
            if (n2 < 0) {
                return n2;
            }
            n2 = this.read(byArray, 0, 4);
            n = this.getIntForBytes(byArray, 0);
            if ((n = n - 4 - 1) < 200) {
                n2 = this.read(byArray, 0, n);
            }
            String string7 = new String(byArray, 0, n);
            this.read(byArray, 0, 1);
            if (string7.equals("<CSC_Pass/>")) {
                this.iAuthenticated = 1;
                return 0;
            }
            this.disconnect();
            return -1;
        }
        this.iAuthenticated = 1;
        return 0;
    }

    public int startReferralCSCSession(String string, String string2, String string3, String string4) {
        int n;
        String string5 = new String("<CSC_Start_Session ProtocolID=\"" + string + "\" SessionID=\"" + string2 + "\"/>");
        byte[] byArray = new byte[200];
        int n2 = string5.length() + 1 + 4;
        this.write(RRCUtil.getBytesForInt(n2), 4);
        this.write(string5.getBytes(), n2 - 5);
        this.write(byNull, 1);
        if (string4.equals("SSL")) {
            n = this.startSSL();
            if (n != 0) {
                return n;
            }
            this.createStreams();
        } else {
            this.boolNeedSSL = false;
        }
        n = this.CSC_Test(string3.getBytes());
        return n;
    }

    private int CSC_Test(byte[] byArray) {
        try {
            int n;
            byte[] byArray2 = null;
            byte[] byArray3 = null;
            byte[] byArray4 = null;
            byte[] byArray5 = null;
            byte[] byArray6 = null;
            Object var9_7 = null;
            String string = "1234567890";
            byte[] byArray7 = null;
            byte[] byArray8 = null;
            byte[] byArray9 = null;
            byArray6 = new BASE64Decoder().decodeBuffer(new String(byArray));
            byArray7 = this.readMessage(0);
            if (byArray7 == null) {
                return -10;
            }
            byArray8 = this.getAttributeValue(new String(byArray7), "ClearText");
            byArray7 = null;
            if (byArray8 == null) {
                return -1;
            }
            byArray2 = new BASE64Decoder().decodeBuffer(new String(byArray8));
            int n2 = byArray2.length;
            RC4Cipher rC4Cipher = new RC4Cipher(byArray6);
            rC4Cipher.encrypt(byArray2, 0, byArray2, 0, n2);
            rC4Cipher = null;
            byArray4 = new BASE64Encoder().encode(byArray2).getBytes();
            n2 = (int)System.currentTimeMillis();
            byte[] byArray10 = string.getBytes();
            byArray3 = new byte[byArray10.length];
            for (n = 0; n < byArray10.length; ++n) {
                byArray3[n] = (byte)(byArray10[n] ^ (char)n2);
                n2 >>= 3;
                n2 = (int)((long)n2 ^ System.currentTimeMillis());
            }
            byArray5 = new BASE64Encoder().encode(byArray3).getBytes();
            n2 = byArray5.length;
            String string2 = "<CSC_Test2 Encrypted=\"" + new String(byArray4) + "\" ClearText=\"" + new String(byArray5) + "\"/>";
            n2 = this.writeMessage(string2.getBytes());
            if (n2 < 0) {
                return n2;
            }
            byArray7 = this.readMessage(0);
            if (byArray7 == null) {
                return -10;
            }
            byArray9 = this.getAttributeValue(new String(byArray7), "Encrypted");
            byArray7 = null;
            if (byArray9 == null) {
                return -1;
            }
            byArray2 = new BASE64Decoder().decodeBuffer(new String(byArray9));
            rC4Cipher = new RC4Cipher(byArray6);
            rC4Cipher.decrypt(byArray2, 0, byArray2, 0, byArray2.length);
            if (byArray2.length != byArray10.length) {
                return -1;
            }
            for (n = 0; n < byArray10.length; ++n) {
                if (byArray3[n] == byArray2[n]) continue;
                return -1;
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return -1;
        }
        return 0;
    }

    public void disconnect() {
        if (this.iConnected != 0) {
            this.objCSCSocket = null;
            this.iConnected = 0;
            this.iIsSSL = 0;
            this.iAuthenticated = 0;
        }
    }

    private int startSSL() {
        try {
            this.boolNeedSSL = true;
            String string = this.objCSCSocket.getInetAddress().getHostAddress();
            this.objSSLSocket = new SecureSocket(this.objCSCSocket, string, Constants.TR_PORT_BASE, false);
            this.objSSLSocket.connect();
            return 0;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return -4;
        }
    }

    public int writeMessage(byte[] byArray) {
        int n = 0;
        int n2 = 4 + byArray.length + 1;
        n = this.write(RRCUtil.getBytesForInt(n2), 4);
        if (n < 0) {
            return n;
        }
        n = this.write(byArray, n2 - 4 - 1);
        if (n < 0) {
            return n;
        }
        n = this.write(byNull, 1);
        if (n < 0) {
            return n;
        }
        return n;
    }

    public byte[] readMessage(int n) {
        byte[] byArray = new byte[4];
        int n2 = this.read(byArray, 0, 4);
        if (n2 < 0) {
            return null;
        }
        int n3 = this.getIntForBytes(byArray, 0);
        if (n > 0 && n3 > n) {
            return null;
        }
        if (n3 < 9) {
            return null;
        }
        byte[] byArray2 = new byte[n3 - 4];
        if (byArray2 == null) {
            return null;
        }
        n2 = this.read(byArray2, 0, n3 - 4);
        if (n2 < 0) {
            return null;
        }
        return byArray2;
    }

    public int write(byte[] byArray, int n) {
        int n2 = 0;
        boolean bl = true;
        if (this.iConnected != 0) {
            try {
                this.objFilterOutputStream.write(byArray, 0, n);
            }
            catch (Exception exception) {
                RRCLogger.logException(exception);
                bl = false;
            }
            if (!bl) {
                this.iConnected = 0;
                n2 = -20;
            }
        } else {
            n2 = -8;
        }
        return n2;
    }

    public int read(byte[] byArray, int n, int n2) {
        boolean bl = true;
        int n3 = 0;
        int n4 = 0;
        if (this.iConnected != 0) {
            try {
                n4 = this.objDataInputStream.read(byArray, n, n2);
            }
            catch (Exception exception) {
                RRCLogger.logException(exception);
                bl = false;
            }
            if (n4 < 0 || !bl) {
                this.iConnected = 0;
                n3 = -20;
            }
        } else {
            bl = false;
            n3 = -10;
        }
        return n3;
    }

    private int getIntForBytes(byte[] byArray, int n) {
        return ((byArray[n + 0] & 0xFF) << 24) + ((byArray[n + 1] & 0xFF) << 16) + ((byArray[n + 2] & 0xFF) << 8) + ((byArray[n + 3] & 0xFF) << 0);
    }

    private void createStreams() {
        try {
            if (this.boolNeedSSL) {
                this.objDataInputStream = new DataInputStream(this.objSSLSocket.getSecureInputStream());
                this.objFilterOutputStream = new FilterOutputStream(this.objSSLSocket.getSecureOutputStream());
            } else {
                this.objDataInputStream = new DataInputStream(this.objCSCSocket.getInputStream());
                this.objFilterOutputStream = new FilterOutputStream(this.objCSCSocket.getOutputStream());
            }
        }
        catch (Exception exception) {
            this.objDataInputStream = null;
            this.objFilterOutputStream = null;
            RRCLogger.logException(exception);
        }
    }

    public byte[] Query_CSC_Info(int n, short s, int n2) {
        TRRSP_ID_DATA tRRSP_ID_DATA = new TRRSP_ID_DATA();
        InetAddress inetAddress = null;
        try {
            if (s == 0) {
                s = (short)Constants.TR_PORT_BASE;
            }
            DatagramSocket datagramSocket = new DatagramSocket();
            DatagramPacket datagramPacket = new DatagramPacket(CSC_Discover, CSC_Dis_Len);
            datagramPacket.setPort(s);
            if (n2 == 0) {
                n2 = 10;
            }
            n2 = n2 * 1000 + (int)System.currentTimeMillis();
            do {
                if (inetAddress == null) {
                    byte[] byArray = RRCUtil.getBytesForInt(n);
                    inetAddress = InetAddress.getByAddress(byArray);
                    datagramPacket.setAddress(inetAddress);
                }
                datagramSocket.send(datagramPacket);
                datagramSocket.setSoTimeout(1000);
                int n3 = datagramPacket.getLength();
                byte[] byArray = new byte[2048];
                DatagramPacket datagramPacket2 = new DatagramPacket(byArray, byArray.length);
                datagramSocket.receive(datagramPacket2);
                String string = new String(byArray);
                if (n3 < 11 && (n3 > 2048 || n3 > datagramPacket2.getLength() || !string.equals("0x00")) || string.indexOf("CSC_Info") == 0) continue;
                for (int i = 0; i < n3 - 4; ++i) {
                    byArray[i] = byArray[i + 4];
                }
                datagramSocket.disconnect();
                return byArray;
            } while ((long)n2 >= System.currentTimeMillis());
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return null;
    }

    public String getNodeString(String string, String string2) {
        if (string == null) {
            return null;
        }
        int n = string.indexOf("<" + string2 + ">");
        int n2 = string.indexOf("</" + string2 + ">");
        if (n != -1 && n2 != -1) {
            return string.substring(n + string2.length() + 2, n2);
        }
        return null;
    }

    public byte[] getAttributeValue(String string, String string2) {
        String string3 = " " + string2 + "=\"";
        int n = -1;
        if (string == null) {
            return null;
        }
        n = string.indexOf(string3);
        if (n == -1 && (n = string.indexOf(string3 = " " + string2 + "= \"")) == -1 && (n = string.indexOf(string3 = " " + string2 + " =\"")) == -1) {
            string3 = " " + string2 + " = \"";
            n = string.indexOf(string3);
        }
        if (n == -1) {
            return null;
        }
        int n2 = n + string3.length();
        int n3 = string.indexOf("\"", n2);
        int n4 = n + string3.length();
        if (n3 != -1) {
            String string4 = string.substring(n2, n3);
            if (string4.charAt(string4.length() - 1) == '\n') {
                string4 = string4.substring(0, string4.length() - 1);
            }
            if (string4 != null) {
                return string4.getBytes();
            }
        }
        return null;
    }

    public static void main(String[] stringArray) {
        try {
            int n = -1;
            InetAddress inetAddress = InetAddress.getByName("172.16.35.103");
            byte[] byArray = null;
            CSCConnect cSCConnect = new CSCConnect();
            n = cSCConnect.connect(inetAddress.hashCode(), (short)5000, "Dummy".getBytes());
            if (n < 0) {
                System.exit(1);
            }
            byArray = cSCConnect.GetCSCInfo();
            n = cSCConnect.startNewCSCSession("RDM", "admin", Constants.DEFAULT_PASSWORD, "SSL", "CSC");
            if (n < 0) {
                System.exit(1);
            }
            String string = "<Session><GetSessionID/></Session>";
            cSCConnect.writeMessage(string.getBytes());
            byte[] byArray2 = cSCConnect.readMessage(0);
            string = new String(byArray2);
            String string2 = cSCConnect.getNodeString(string, "SessionID");
            String string3 = cSCConnect.getNodeString(string, "SessionKey");
            string3 = string3.substring(0, string3.length() - 1);
            CSCConnect cSCConnect2 = new CSCConnect();
            n = cSCConnect2.connect(inetAddress.hashCode(), (short)5000, "Dummy".getBytes());
            if (n < 0) {
                System.exit(1);
            }
            byArray = cSCConnect2.GetCSCInfo();
            n = cSCConnect2.startReferralCSCSession("RDMEvent", string2, string3, "SSL");
            if (n < 0) {
                System.exit(1);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

