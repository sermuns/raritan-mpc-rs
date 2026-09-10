/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ms.security.PermissionID
 *  com.ms.security.PolicyEngine
 */
package amp.powerboard.clientapi.net;

import amp.powerboard.clientapi.command.CCommandHandler;
import amp.powerboard.clientapi.common.exception.CInvalidUserException;
import amp.powerboard.clientapi.common.exception.CMaxUserExceededException;
import amp.powerboard.clientapi.common.exception.CUserAlreadyLoggedException;
import amp.powerboard.clientapi.net.CMsgInputStream;
import amp.powerboard.clientapi.net.CMsgOutputStream;
import amp.powerboard.clientapi.security.CCryptoKey;
import amp.powerboard.clientapi.security.CMd5;
import amp.powerboard.clientapi.security.RC4CryptoKey;
import com.ms.security.PermissionID;
import com.ms.security.PolicyEngine;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class CSecureSocket {
    private byte[] encryptKey;
    private byte[] decryptKey;
    public byte[] mbox_admin_cmd;
    public byte[] mbox_admin_con;
    public byte[] mbox_admin_recmd;
    RC4CryptoKey inCmKey;
    RC4CryptoKey outCmKey;
    private Socket socket;
    private InputStream secureInput;
    private OutputStream secureOutput;
    private String login;

    public CSecureSocket(int n, byte by, InetAddress inetAddress, int n2, String string, int n3, byte[] byArray, String string2, CCryptoKey cCryptoKey, CCryptoKey cCryptoKey2, int n4, String string3, String string4, String string5, byte[] byArray2, CCommandHandler cCommandHandler) throws IOException, CUserAlreadyLoggedException, CInvalidUserException, CMaxUserExceededException {
        block63: {
            byte[] byArray3;
            OutputStream outputStream;
            InputStream inputStream;
            block64: {
                block62: {
                    this.encryptKey = null;
                    this.decryptKey = null;
                    this.mbox_admin_cmd = null;
                    this.mbox_admin_con = null;
                    this.mbox_admin_recmd = null;
                    this.inCmKey = null;
                    this.outCmKey = null;
                    this.login = string;
                    this.socket = new Socket(inetAddress, n2);
                    inputStream = this.socket.getInputStream();
                    outputStream = this.socket.getOutputStream();
                    byte[] byArray4 = new byte[string2.length()];
                    string2.getBytes(0, string2.length(), byArray4, 0);
                    CMd5 cMd5 = new CMd5();
                    cMd5.update(byArray4);
                    byArray3 = cMd5.digest();
                    if (!string3.equals("HP")) break block62;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                    dataOutputStream.writeBytes(this.login);
                    dataOutputStream.write(byArray3);
                    dataOutputStream.write(byArray);
                    dataOutputStream.writeInt(by);
                    CMd5 cMd52 = new CMd5();
                    cMd52.update(byteArrayOutputStream.toByteArray());
                    byArray2 = cMd52.digest();
                    CMsgOutputStream cMsgOutputStream = new CMsgOutputStream(outputStream, 2000);
                    cMsgOutputStream.writeShort((short)this.login.length());
                    cMsgOutputStream.writeBytes(this.login);
                    cMsgOutputStream.writeInt(n3);
                    cMsgOutputStream.writeInt(by);
                    cMsgOutputStream.writeShort((short)16);
                    cMsgOutputStream.write(byArray2);
                    cMsgOutputStream.close();
                    CMsgInputStream cMsgInputStream = new CMsgInputStream(inputStream);
                    block4 : switch (cMsgInputStream.opCode) {
                        case 2000: {
                            int n5 = cMsgInputStream.readInt();
                            switch (n5) {
                                case 0: {
                                    break block4;
                                }
                                case 8: {
                                    throw new CUserAlreadyLoggedException();
                                }
                                case 9: {
                                    throw new CInvalidUserException();
                                }
                                case 10: {
                                    throw new CMaxUserExceededException();
                                }
                            }
                            System.out.println("Authentication Error: " + n5);
                            break;
                        }
                        default: {
                            System.out.println("Invalid Opcode: " + cMsgInputStream.opCode);
                        }
                    }
                    try {
                        cMsgInputStream.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    this.secureInput = cCryptoKey.newInputStream(inputStream);
                    this.secureOutput = cCryptoKey2.newOutputStream(outputStream);
                    if (cCryptoKey == cCryptoKey2) break block63;
                    cCryptoKey.StartEncryption();
                    cCryptoKey2.StartEncryption();
                    break block63;
                }
                if (!string3.equals("DC") && (!string3.equals("CONSOLE_MANAGER") || !string4.equals("DC"))) break block64;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                dataOutputStream.writeBytes(this.login);
                dataOutputStream.write(byArray3);
                dataOutputStream.write(byArray);
                dataOutputStream.writeInt(by);
                CMd5 cMd5 = new CMd5();
                cMd5.update(byteArrayOutputStream.toByteArray());
                byArray2 = cMd5.digest();
                CMsgOutputStream cMsgOutputStream = new CMsgOutputStream(outputStream, 2000);
                cMsgOutputStream.writeShort((short)this.login.length());
                cMsgOutputStream.writeBytes(this.login);
                cMsgOutputStream.writeInt(n3);
                cMsgOutputStream.writeInt(by);
                cMsgOutputStream.writeShort((short)16);
                cMsgOutputStream.write(byArray2);
                if (string3.equals("CONSOLE_MANAGER") && string4.equals("DC")) {
                    cMsgOutputStream.writeInt(n4);
                }
                cMsgOutputStream.close();
                CMsgInputStream cMsgInputStream = new CMsgInputStream(inputStream);
                block13 : switch (cMsgInputStream.opCode) {
                    case 2000: {
                        int n6 = cMsgInputStream.readInt();
                        switch (n6) {
                            case 0: {
                                break block13;
                            }
                            case 8: {
                                throw new CUserAlreadyLoggedException();
                            }
                            case 9: {
                                throw new CInvalidUserException();
                            }
                            case 10: {
                                throw new CMaxUserExceededException();
                            }
                        }
                        System.out.println("Authentication Error: " + n6);
                        break;
                    }
                    default: {
                        System.out.println("Invalid Opcode: " + cMsgInputStream.opCode);
                    }
                }
                try {
                    cMsgInputStream.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                this.secureInput = cCryptoKey.newInputStream(inputStream);
                this.secureOutput = cCryptoKey2.newOutputStream(outputStream);
                if (cCryptoKey == cCryptoKey2) break block63;
                cCryptoKey.StartEncryption();
                cCryptoKey2.StartEncryption();
                break block63;
            }
            if (string3.equals("COMMAND_CENTER")) {
                this.generateCustomKeys();
                RC4CryptoKey rC4CryptoKey = (RC4CryptoKey)cCryptoKey2;
                String string6 = new String(byArray);
                ByteArrayOutputStream byteArrayOutputStream = this.getEncryptedPacket(cCryptoKey2, n3, byArray, by, string5);
                byte[] byArray5 = byteArrayOutputStream.toByteArray();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeInt(byArray5.length - 1);
                dataOutputStream.write(byArray5, 0, byArray5.length);
                dataOutputStream.flush();
                CMsgInputStream cMsgInputStream = new CMsgInputStream(inputStream);
                block22 : switch (cMsgInputStream.opCode) {
                    case 2000: {
                        int n7 = cMsgInputStream.readInt();
                        switch (n7) {
                            case 0: {
                                break block22;
                            }
                            case 8: {
                                throw new CUserAlreadyLoggedException();
                            }
                            case 9: {
                                throw new CInvalidUserException();
                            }
                            case 10: {
                                throw new CMaxUserExceededException();
                            }
                        }
                        System.out.println("Authentication Error: " + n7);
                        break;
                    }
                    default: {
                        System.out.println("Invalid Opcode: " + cMsgInputStream.opCode);
                    }
                }
                try {
                    cMsgInputStream.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                this.secureInput = this.inCmKey.newInputStream(inputStream);
                this.secureOutput = this.outCmKey.newOutputStream(outputStream);
                this.inCmKey.StartEncryption();
                this.outCmKey.StartEncryption();
            } else {
                int n8;
                CMsgInputStream cMsgInputStream;
                CMsgOutputStream cMsgOutputStream;
                this.secureInput = cCryptoKey.newInputStream(inputStream);
                this.secureOutput = cCryptoKey2.newOutputStream(outputStream);
                if (cCryptoKey != cCryptoKey2) {
                    cCryptoKey.StartEncryption();
                    cCryptoKey2.StartEncryption();
                }
                int n9 = 8;
                if (string3.equals("CONSOLE_MANAGER")) {
                    n9 = 12;
                }
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeInt(n9);
                dataOutputStream.writeInt(n3);
                dataOutputStream.writeInt(by);
                if (string3.equals("CONSOLE_MANAGER")) {
                    dataOutputStream.writeInt(n4);
                }
                int n10 = 0;
                dataOutputStream.writeByte(n10);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                int n11 = dataInputStream.readInt();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream2 = new DataOutputStream(byteArrayOutputStream);
                if (string3.indexOf("_APP") != -1) {
                    if (by == 1) {
                        cMsgOutputStream = new CMsgOutputStream(this.secureOutput, 2001);
                        cMsgOutputStream.writeInt(n4);
                        n10 = 0;
                        cMsgOutputStream.writeByte(n10);
                        cMsgOutputStream.close();
                        cMsgInputStream = new CMsgInputStream(this.secureInput);
                        switch (cMsgInputStream.opCode) {
                            case 2001: {
                                n8 = cMsgInputStream.readInt();
                                switch (n8) {
                                    case 0: {
                                        int n12 = cMsgInputStream.readInt();
                                        int n13 = cMsgInputStream.readInt();
                                        int n14 = cMsgInputStream.readInt();
                                        short s = cMsgInputStream.readShort();
                                        cCommandHandler.mboxLoginName = this.login = cMsgInputStream.readBytes(s);
                                        s = cMsgInputStream.readShort();
                                        byte[] byArray6 = new byte[s];
                                        int n15 = cMsgInputStream.read(byArray6, 0, s);
                                        int n16 = s / 3;
                                        byArray2 = new byte[n16];
                                        System.arraycopy(byArray6, 0, byArray2, 0, n16);
                                        cCommandHandler.mbox_mp_con = new byte[n16];
                                        System.arraycopy(byArray6, n16, cCommandHandler.mbox_mp_con, 0, n16);
                                        cCommandHandler.mbox_mp_recmd = new byte[n16];
                                        System.arraycopy(byArray6, 2 * n16, cCommandHandler.mbox_mp_recmd, 0, n16);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    dataOutputStream2.writeBytes(this.login);
                    dataOutputStream2.write(byArray3);
                    dataOutputStream2.write(byArray);
                    dataOutputStream2.writeInt(by);
                    CMd5 cMd5 = new CMd5();
                    cMd5.update(byteArrayOutputStream.toByteArray());
                    byArray2 = cMd5.digest();
                    if (by == 1 || by == 3) {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        dataOutputStream2 = new DataOutputStream(byteArrayOutputStream);
                        dataOutputStream2.writeBytes(this.login);
                        dataOutputStream2.write(byArray3);
                        dataOutputStream2.write(byArray);
                        dataOutputStream2.writeInt(1);
                        cMd5 = new CMd5();
                        cMd5.update(byteArrayOutputStream.toByteArray());
                        this.mbox_admin_cmd = cMd5.digest();
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        dataOutputStream2 = new DataOutputStream(byteArrayOutputStream);
                        dataOutputStream2.writeBytes(this.login);
                        dataOutputStream2.write(byArray3);
                        dataOutputStream2.write(byArray);
                        dataOutputStream2.writeInt(2);
                        cMd5 = new CMd5();
                        cMd5.update(byteArrayOutputStream.toByteArray());
                        this.mbox_admin_con = cMd5.digest();
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        dataOutputStream2 = new DataOutputStream(byteArrayOutputStream);
                        dataOutputStream2.writeBytes(this.login);
                        dataOutputStream2.write(byArray3);
                        dataOutputStream2.write(byArray);
                        dataOutputStream2.writeInt(3);
                        cMd5 = new CMd5();
                        cMd5.update(byteArrayOutputStream.toByteArray());
                        this.mbox_admin_recmd = cMd5.digest();
                    }
                }
                cMsgOutputStream = new CMsgOutputStream(this.secureOutput, 2000);
                cMsgOutputStream.writeShort((short)this.login.length());
                cMsgOutputStream.writeBytes(this.login);
                cMsgOutputStream.writeShort((short)16);
                cMsgOutputStream.write(byArray2);
                cMsgOutputStream.writeInt(n);
                if (string3.equals("CEREBUS_X16+") || string3.equals("CEREBUS_X32+")) {
                    cMsgOutputStream.writeShort((short)string2.length());
                    cMsgOutputStream.writeBytes(string2);
                }
                if (string3.equals("CONSOLE_MANAGER")) {
                    cMsgOutputStream.writeInt(n4);
                }
                cMsgOutputStream.close();
                cMsgInputStream = new CMsgInputStream(this.secureInput);
                block37 : switch (cMsgInputStream.opCode) {
                    case 2000: {
                        n8 = cMsgInputStream.readInt();
                        switch (n8) {
                            case 0: {
                                break block37;
                            }
                            case 8: {
                                throw new CUserAlreadyLoggedException();
                            }
                            case 9: {
                                throw new CInvalidUserException();
                            }
                            case 10: {
                                throw new CMaxUserExceededException();
                            }
                        }
                        System.out.println("Authentication Error: " + n8);
                        break;
                    }
                    default: {
                        System.out.println("Invalid Opcode: " + cMsgInputStream.opCode);
                    }
                }
                try {
                    cMsgInputStream.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    public CSecureSocket(InetAddress inetAddress, int n, int n2, int n3, int n4, byte by, String string) throws IOException, CInvalidUserException {
        this.encryptKey = null;
        this.decryptKey = null;
        this.mbox_admin_cmd = null;
        this.mbox_admin_con = null;
        this.mbox_admin_recmd = null;
        this.inCmKey = null;
        this.outCmKey = null;
        try {
            if (Class.forName("com.ms.security.PolicyEngine") != null) {
                PolicyEngine.assertPermission((PermissionID)PermissionID.NETIO);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.socket = new Socket(inetAddress, n);
        InputStream inputStream = this.socket.getInputStream();
        OutputStream outputStream = this.socket.getOutputStream();
        this.secureInput = inputStream;
        this.secureOutput = outputStream;
        int n5 = 16;
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(n5);
        dataOutputStream.writeInt(n2);
        dataOutputStream.writeInt(n3);
        dataOutputStream.writeInt(n4);
        dataOutputStream.writeInt(by);
        int n6 = 0;
        dataOutputStream.writeByte(n6);
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int n7 = dataInputStream.readInt();
        if (n7 != 0) {
            throw new CInvalidUserException();
        }
    }

    public void close() throws IOException {
        this.socket.close();
    }

    public InetAddress getInetAddress() throws IOException {
        return this.socket.getInetAddress();
    }

    public InetAddress getLocalAddress() throws IOException {
        return this.socket.getLocalAddress();
    }

    public InputStream getInputStream() throws IOException {
        return this.secureInput;
    }

    public int getLocalPort() throws IOException {
        return this.socket.getLocalPort();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.secureOutput;
    }

    public int getPort() throws IOException {
        return this.socket.getPort();
    }

    public String toString() {
        return this.socket.toString() + ":" + this.login;
    }

    private ByteArrayOutputStream getEncryptedPacket(CCryptoKey cCryptoKey, int n, byte[] byArray, int n2, String string) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(n);
        this.secureOutput = cCryptoKey.newOutputStream(byteArrayOutputStream);
        cCryptoKey.StartEncryption();
        dataOutputStream = new DataOutputStream(this.secureOutput);
        dataOutputStream.write(byArray);
        byte[] byArray2 = string.getBytes();
        int n3 = byArray2.length;
        dataOutputStream.writeShort(n3);
        dataOutputStream.write(byArray2, 0, byArray2.length);
        dataOutputStream.writeInt(n2);
        dataOutputStream.write(this.decryptKey, 0, this.decryptKey.length);
        dataOutputStream.write(this.encryptKey, 0, this.encryptKey.length);
        dataOutputStream.writeByte(0);
        return byteArrayOutputStream;
    }

    private void generateCustomKeys() {
        Random random = new Random();
        this.encryptKey = new byte[16];
        random.nextBytes(this.encryptKey);
        this.decryptKey = new byte[16];
        random.nextBytes(this.decryptKey);
        this.inCmKey = new RC4CryptoKey(this.decryptKey);
        this.outCmKey = new RC4CryptoKey(this.encryptKey);
    }
}

