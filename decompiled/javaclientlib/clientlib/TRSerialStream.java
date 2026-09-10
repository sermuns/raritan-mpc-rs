/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

import java.net.InetAddress;
import javaclientlib.clientlib.ISerialStream;
import javaclientlib.clientlib.TRConnection;
import javaclientlib.tr.Constants;
import javaclientlib.tr.TRBASECOMMAND;
import javaclientlib.tr.TRCMD_GET_SERIAL_PARAMS_DATA;
import javaclientlib.tr.TRCMD_SEND_SERIAL_DATA;
import javaclientlib.tr.TRCMD_STOP_SERIAL_STREAM_DATA;
import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRLIB_COMM;
import javaclientlib.tr.TRLIB_REFERRAL_COMM;
import javaclientlib.tr.TRLIB_USERINFO;
import javaclientlib.tr.TRRSP_CONNECTED_DATA;
import javaclientlib.tr.TRRSP_RECEIVE_SERIAL_DATA;
import javaclientlib.tr.TRRSP_SERIAL_PARAMS_DATA;
import javaclientlib.tr.TRSRVR_SERIAL_PARAMS;
import javaclientlib.utils.RRCLogger;

public class TRSerialStream
implements ISerialStream {
    private TRSerialStream objSerialStreamNext = null;
    private boolean boolObjectGood = true;
    private byte byDeviceID = 0;
    private boolean boolConnected = false;
    private TRConnection objConnection;

    public TRSerialStream(TRConnection tRConnection) {
        if (tRConnection != null) {
            this.objConnection = tRConnection;
            tRConnection.addSerialStream(this);
        }
    }

    protected void finalize() throws Exception {
        if (this.boolConnected) {
            this.stopSerialStream();
        }
        if (this.objConnection != null) {
            this.objConnection.removeSerialStream(this);
        }
    }

    public boolean connectSerialStream(String string, String string2) {
        boolean bl = false;
        try {
            String string3 = "<Connect><Portal>" + string + "</Portal><Target>" + string2 + "</Target></Connect>";
            TRRSP_CONNECTED_DATA tRRSP_CONNECTED_DATA = new TRRSP_CONNECTED_DATA(4108);
            tRRSP_CONNECTED_DATA.setCommand((byte)56);
            System.arraycopy(string3.getBytes(), 0, tRRSP_CONNECTED_DATA.toByteArray(), 4, string3.getBytes().length);
            tRRSP_CONNECTED_DATA.setCmdLength((short)(4 + string3.getBytes().length));
            bl = this.objConnection.sendTRCmdExx(tRRSP_CONNECTED_DATA, true, null, 38, true, 0);
            bl = this.objConnection.processResponse(bl, tRRSP_CONNECTED_DATA);
            if (bl) {
                this.byDeviceID = tRRSP_CONNECTED_DATA.getDeviceID();
                this.log("The Got Device Id " + tRRSP_CONNECTED_DATA.getDeviceID());
                this.objConnection.setSerialStreamDevice(this, this.byDeviceID);
                this.boolConnected = true;
            }
            this.objConnection.syncTRCmd();
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean stopSerialStream() throws Exception {
        boolean bl = false;
        try {
            TRCMD_STOP_SERIAL_STREAM_DATA tRCMD_STOP_SERIAL_STREAM_DATA = new TRCMD_STOP_SERIAL_STREAM_DATA();
            tRCMD_STOP_SERIAL_STREAM_DATA.setCommand((byte)23);
            tRCMD_STOP_SERIAL_STREAM_DATA.setCmdLength((short)5);
            tRCMD_STOP_SERIAL_STREAM_DATA.setDeviceID(this.byDeviceID);
            this.boolConnected = false;
            bl = this.objConnection.sendTRCmdEx(tRCMD_STOP_SERIAL_STREAM_DATA, true, null, 2);
            this.objConnection.setSerialStreamDevice(null, this.byDeviceID);
            if (this.objConnection != null) {
                this.objConnection.removeSerialStream(this);
            }
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean setSerialStream(TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS) throws Exception {
        boolean bl = false;
        try {
            TRCOMMAND tRCOMMAND = new TRCOMMAND(4108);
            if (tRSRVR_SERIAL_PARAMS == null && RRCLogger.logEnabled) {
                RRCLogger.log(200, 256, "Invalid objTRSrvrSerialParams object");
            }
            tRCOMMAND.setCommand((byte)25);
            tRCOMMAND.setCmdLength((short)53);
            tRCOMMAND.setByte(this.byDeviceID, 52);
            System.arraycopy(tRSRVR_SERIAL_PARAMS.getDataBytes(), 0, tRCOMMAND.toByteArray(), 4, 16);
            bl = this.objConnection.sendTRCmdEx(tRCOMMAND, true, null, 2);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean getSerialStream(TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS) throws Exception {
        try {
            boolean bl = false;
            TRCMD_GET_SERIAL_PARAMS_DATA tRCMD_GET_SERIAL_PARAMS_DATA = new TRCMD_GET_SERIAL_PARAMS_DATA(4108);
            if (tRSRVR_SERIAL_PARAMS == null && RRCLogger.logEnabled) {
                RRCLogger.log(200, 256, "Invalid objTRSrvrSerialParams object");
            }
            tRCMD_GET_SERIAL_PARAMS_DATA.setCommand((byte)26);
            tRCMD_GET_SERIAL_PARAMS_DATA.setCmdLength((short)5);
            tRCMD_GET_SERIAL_PARAMS_DATA.setDeviceID(this.byDeviceID);
            bl = this.objConnection.sendTRCmdEx(tRCMD_GET_SERIAL_PARAMS_DATA, true, null, 19);
            if (bl) {
                TRRSP_SERIAL_PARAMS_DATA tRRSP_SERIAL_PARAMS_DATA = null;
                tRRSP_SERIAL_PARAMS_DATA = new TRRSP_SERIAL_PARAMS_DATA(tRCMD_GET_SERIAL_PARAMS_DATA.toByteArray());
                tRSRVR_SERIAL_PARAMS.setParams(tRRSP_SERIAL_PARAMS_DATA.getParams());
            }
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    @Override
    public boolean serialOut(int n, byte[] byArray) throws Exception {
        try {
            boolean bl = false;
            TRCMD_SEND_SERIAL_DATA tRCMD_SEND_SERIAL_DATA = new TRCMD_SEND_SERIAL_DATA(4108);
            tRCMD_SEND_SERIAL_DATA.setCommand((byte)24);
            if (n > 4103) {
                n = 4103;
            }
            tRCMD_SEND_SERIAL_DATA.setCmdLength((short)(5 + n));
            tRCMD_SEND_SERIAL_DATA.setDeviceID(this.byDeviceID);
            tRCMD_SEND_SERIAL_DATA.setPktData(byArray);
            bl = this.objConnection.sendTRCmd(tRCMD_SEND_SERIAL_DATA);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    @Override
    public void serialIn(int n, byte[] byArray) {
    }

    public boolean TRRSP_Receive_Serial(TRBASECOMMAND tRBASECOMMAND) {
        try {
            boolean bl = false;
            TRRSP_RECEIVE_SERIAL_DATA tRRSP_RECEIVE_SERIAL_DATA = new TRRSP_RECEIVE_SERIAL_DATA(tRBASECOMMAND.toByteArray());
            if (tRRSP_RECEIVE_SERIAL_DATA == null && RRCLogger.logEnabled) {
                RRCLogger.log(200, 256, "Invalid objTRRSPReceiveSerialData object");
            }
            short s = (short)(tRRSP_RECEIVE_SERIAL_DATA.getCmdLength() - 7);
            byte[] byArray = new byte[s];
            bl = this.objConnection.read(byArray, 0, s);
            this.serialIn(s, byArray);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public byte getDeviceID() {
        return this.byDeviceID;
    }

    public boolean sendSunBreak() throws Exception {
        try {
            boolean bl = false;
            TRCOMMAND tRCOMMAND = new TRCOMMAND();
            tRCOMMAND.setCommand((byte)58);
            tRCOMMAND.setCmdLength((short)4);
            bl = this.objConnection.sendTRCmd(tRCOMMAND);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    private void log(String string) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 256, "Video Stream::" + string);
        }
    }

    public static void main(String[] stringArray) {
        TRLIB_COMM tRLIB_COMM = new TRLIB_COMM();
        TRLIB_USERINFO tRLIB_USERINFO = new TRLIB_USERINFO();
        TRLIB_REFERRAL_COMM tRLIB_REFERRAL_COMM = new TRLIB_REFERRAL_COMM();
        TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS = new TRSRVR_SERIAL_PARAMS();
        boolean bl = false;
        TRRSP_RECEIVE_SERIAL_DATA tRRSP_RECEIVE_SERIAL_DATA = new TRRSP_RECEIVE_SERIAL_DATA();
        short s = 0;
        boolean bl2 = false;
        tRRSP_RECEIVE_SERIAL_DATA.setFlags(s);
        tRRSP_RECEIVE_SERIAL_DATA.setDeviceID((byte)(bl2 ? 1 : 0));
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName("10.0.0.240");
            tRLIB_COMM.setIpAddress(inetAddress.hashCode());
            tRLIB_COMM.setConnType(2);
            tRLIB_COMM.setServerName("IP Reach".getBytes());
            tRLIB_COMM.setDnsName("IP Reach".getBytes());
            tRLIB_COMM.setIpPort(5000);
            tRLIB_COMM.setFindBy(0);
            tRLIB_USERINFO.setName("admin".getBytes());
            tRLIB_USERINFO.setPassword(Constants.DEFAULT_PASSWORD.getBytes());
            tRLIB_REFERRAL_COMM.setVersion(1);
            TRConnection tRConnection = new TRConnection();
            boolean bl3 = tRConnection.connect(tRLIB_COMM, tRLIB_USERINFO, null, null);
            while (!tRConnection.getAuthenticated()) {
                Thread.sleep(100L);
            }
            TRSerialStream tRSerialStream = new TRSerialStream(tRConnection);
            Thread.sleep(200L);
            tRSerialStream.stopSerialStream();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

