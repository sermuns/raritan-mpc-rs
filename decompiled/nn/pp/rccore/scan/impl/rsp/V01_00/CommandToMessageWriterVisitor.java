/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.scan.impl.rsp.V01_00;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.Util;
import nn.pp.core.impl.MonitoringDataOutputStream;
import nn.pp.rccore.scan.ScanCoreException;
import nn.pp.rccore.scan.impl.commands.AuthHttpIDChallResponse;
import nn.pp.rccore.scan.impl.commands.AuthHttpIDGetChallenge;
import nn.pp.rccore.scan.impl.commands.AuthNamePassRequest;
import nn.pp.rccore.scan.impl.commands.AuthRDMRequest;
import nn.pp.rccore.scan.impl.commands.CommandVisitorV01_00;
import nn.pp.rccore.scan.impl.commands.PongResponse;
import nn.pp.rccore.scan.impl.commands.QuitMessageFromClient;
import nn.pp.rccore.scan.impl.commands.ScanSessionRequest;

public class CommandToMessageWriterVisitor
implements CommandVisitorV01_00 {
    private MonitoringDataOutputStream mos;
    private static final Logger LOGGER = Logger.getLogger(CommandToMessageWriterVisitor.class.getName());

    @Override
    public void handleAuthNamePassRequest(AuthNamePassRequest authNamePassRequest) throws IOException, ScanCoreException {
        byte[] byArray;
        byte[] byArray2;
        LOGGER.log(Level.INFO, "Writing command on Socket " + authNamePassRequest);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(0);
        monitoringDataOutputStream.write(0);
        try {
            byArray2 = authNamePassRequest.getUserName().getBytes("ISO-8859-1");
            byArray = authNamePassRequest.getPassword().getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new ScanCoreException(unsupportedEncodingException);
        }
        monitoringDataOutputStream.writeShort(byArray2.length);
        monitoringDataOutputStream.writeShort(byArray.length);
        monitoringDataOutputStream.write(byArray2);
        monitoringDataOutputStream.write(byArray);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleAuthRDMRequest(AuthRDMRequest authRDMRequest) throws IOException, ScanCoreException {
        byte[] byArray;
        LOGGER.log(Level.INFO, "Writing command on Socket " + authRDMRequest);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(7);
        String string = authRDMRequest.getRdmID() + '\u0000';
        try {
            byArray = string.getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new ScanCoreException(unsupportedEncodingException);
        }
        monitoringDataOutputStream.write((byte)byArray.length);
        monitoringDataOutputStream.write(byArray);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleHttpIDChallengeResponse(AuthHttpIDChallResponse authHttpIDChallResponse) throws IOException, ScanCoreException {
        byte[] byArray;
        LOGGER.log(Level.INFO, "Writing command on Socket " + authHttpIDChallResponse);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        try {
            byArray = Util.getHash(authHttpIDChallResponse.getChallenge(), new byte[][]{authHttpIDChallResponse.getHttpSessionID().getBytes("ISO-8859-1")});
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new ScanCoreException(noSuchAlgorithmException);
        }
        byte[] byArray2 = Util.binToHex(byArray).getBytes("ISO-8859-1");
        if (byArray2.length != 64 && byArray2.length != 32) {
            throw new ScanCoreException("Response has invalid length");
        }
        byte[] byArray3 = new byte[byArray2.length + 9];
        System.arraycopy("RSP RESP=".getBytes("ISO-8859-1"), 0, byArray3, 0, 9);
        System.arraycopy(byArray2, 0, byArray3, 9, byArray2.length);
        monitoringDataOutputStream.write(byArray3);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleHttpIDGetChallenge(AuthHttpIDGetChallenge authHttpIDGetChallenge) throws IOException, ScanCoreException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + authHttpIDGetChallenge);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(6);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleScanSessionRequest(ScanSessionRequest scanSessionRequest) throws IOException, ScanCoreException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + scanSessionRequest);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(1);
        String[] stringArray = scanSessionRequest.getPortIds();
        monitoringDataOutputStream.write((byte)stringArray.length);
        byte[][] byArrayArray = new byte[stringArray.length][];
        for (int i = 0; i < stringArray.length; ++i) {
            String string = stringArray[i];
            try {
                byArrayArray[i] = string.getBytes("ISO-8859-1");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                throw new ScanCoreException(unsupportedEncodingException);
            }
            monitoringDataOutputStream.write((byte)byArrayArray[i].length);
        }
        for (byte[] byArray : byArrayArray) {
            monitoringDataOutputStream.write(byArray);
        }
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handlePongResponse(PongResponse pongResponse) throws IOException, ScanCoreException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + pongResponse);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(5);
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    @Override
    public void handleQuitMessageFromClient(QuitMessageFromClient quitMessageFromClient) throws IOException, ScanCoreException {
        LOGGER.log(Level.INFO, "Writing command on Socket " + quitMessageFromClient);
        MonitoringDataOutputStream monitoringDataOutputStream = this.getMos();
        monitoringDataOutputStream.write(3);
        monitoringDataOutputStream.write(0);
        monitoringDataOutputStream.writeShort(0);
        monitoringDataOutputStream.writeInt(quitMessageFromClient.getReason());
        monitoringDataOutputStream.flush();
        monitoringDataOutputStream = null;
    }

    synchronized void setOs(MonitoringDataOutputStream monitoringDataOutputStream) {
        this.mos = monitoringDataOutputStream;
    }

    private synchronized MonitoringDataOutputStream getMos() {
        return this.mos;
    }
}

