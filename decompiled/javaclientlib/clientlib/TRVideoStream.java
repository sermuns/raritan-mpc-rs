/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

import com.util.kbd.KeyHIDValue;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.Arrays;
import javaclientlib.clientlib.HIDKeyboard;
import javaclientlib.clientlib.IKvmData;
import javaclientlib.clientlib.Keyboard;
import javaclientlib.clientlib.PS2Keyboard;
import javaclientlib.clientlib.TRConnection;
import javaclientlib.common.CCT;
import javaclientlib.common.org.lzo.Int;
import javaclientlib.common.org.lzo.Lzo1xDecompressor;
import javaclientlib.tr.BitMapInfo;
import javaclientlib.tr.Cell;
import javaclientlib.tr.Constants;
import javaclientlib.tr.TRCCT;
import javaclientlib.tr.TRCMD_CONNECT_VIDEO_STREAM_DATA;
import javaclientlib.tr.TRCMD_GET_COMP_PARAMS_DATA;
import javaclientlib.tr.TRCMD_GET_TARGET_INTERFACE_DATA;
import javaclientlib.tr.TRCMD_GET_VIDEO_PARAMS_DATA;
import javaclientlib.tr.TRCMD_KB_DATA;
import javaclientlib.tr.TRCMD_MOUSE_DATA;
import javaclientlib.tr.TRCMD_RAW_MOUSE_DATA;
import javaclientlib.tr.TRCMD_REFRESH_DATA;
import javaclientlib.tr.TRCMD_SET_COMP_PARAMS_DATA;
import javaclientlib.tr.TRCMD_SET_TARGET_INTERFACE_DATA;
import javaclientlib.tr.TRCMD_SET_VIDEO_PARAMS_DATA;
import javaclientlib.tr.TRCMD_STOP_VIDEO_STREAM_DATA;
import javaclientlib.tr.TRCMD_SYNC_MOUSE_DATA;
import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRLIB_COMM;
import javaclientlib.tr.TRLIB_REFERRAL_COMM;
import javaclientlib.tr.TRLIB_UPDATEINFO;
import javaclientlib.tr.TRLIB_USERINFO;
import javaclientlib.tr.TRRECT;
import javaclientlib.tr.TRRSP_BITPLANE_DATA;
import javaclientlib.tr.TRRSP_CACHE_DATA;
import javaclientlib.tr.TRRSP_CELL_DATA;
import javaclientlib.tr.TRRSP_CONNECTED_DATA;
import javaclientlib.tr.TRRSP_KB_STATUS_DATA;
import javaclientlib.tr.TRRSP_NEW_VIDEO_MODE_DATA;
import javaclientlib.tr.TRRSP_NOTIFY_DATA;
import javaclientlib.tr.TRRSP_RECEIVE_SERIAL_DATA;
import javaclientlib.tr.TRRSP_TARGET_PARAMS_DATA;
import javaclientlib.tr.TRRSP_VIDEO_MARKER_DATA;
import javaclientlib.tr.TRRSP_VIDEO_PARAMS_DATA;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;
import javaclientlib.tr.TRSRVR_SERIAL_PARAMS;
import javaclientlib.tr.TRSRVR_TARGET_PARAMS;
import javaclientlib.tr.TRSRVR_VIDEO_PARAMS;
import javaclientlib.utils.RRCGeneralException;
import javaclientlib.utils.RRCLogger;

public class TRVideoStream
implements IKvmData,
Keyboard {
    private Keyboard keyboard = null;
    long minUpdateTime = 50L;
    private TRConnection objConn;
    private TRVideoStream objVSNext;
    private boolean boolObjectGood;
    private byte byDeviceID;
    private boolean boolConnected;
    private boolean boolWaitVideoMode;
    private TRCCT objCCT;
    private int iKBDataCount;
    private BitMapInfo[] objDIB;
    private boolean boolCacheEnabled;
    private boolean boolSixteenBit;
    private byte[] byDIBBytes;
    private int[] iPixelData;
    private int[] iPaletteTemp;
    private BufferedImage bufferedImage = null;
    private boolean boolProgressiveUpdate;
    private byte[] byCache0;
    private byte[] byCache1;
    private byte[] byCache2;
    private int iCacheDepth;
    protected TRRSP_NEW_VIDEO_MODE_DATA objTRRSPNewVideoModeData;
    private Cell[] objCellInfo;
    private byte[] byKBBuffer;
    private byte[] byBitPlaneBuffer;
    private boolean mylog = true;
    private int iConst1 = 31744;
    private int iConst2 = 992;
    private int iConst3 = 31;
    private int Const4 = -1;
    private int Const5 = -1;
    private int Const6 = -1;
    private short[] pMaskMap;
    private short[] p16BitConvertTable;
    private int icc = 0;
    short[] progressiveMaskMap = new short[]{31744, 992, 31, 15360, 480, 15, 7168, 224, 7, 3072, 96, 3, 1024, 32, 1};
    short[] RGB16MaskMap = new short[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 0};
    short[] RGB12MaskMap = new short[]{3, 4, 8, 16, 96, 128, 256, 512, 3072, 4096, 8192, 16384, 0, 0, 0, 0};
    public static final int DECOMP_BUFFER_SIZE = 131072;
    private static int[] iMaskMap;

    public void setMinFrameUpdateTime(long l) {
        this.minUpdateTime = l;
    }

    public TRVideoStream(TRConnection tRConnection, int n) {
        this.objConn = tRConnection;
        this.boolObjectGood = true;
        this.objVSNext = null;
        this.byDeviceID = 0;
        this.boolConnected = false;
        this.boolWaitVideoMode = true;
        this.objCCT = null;
        this.iKBDataCount = 0;
        this.byKBBuffer = new byte[16];
        this.objDIB = new BitMapInfo[16];
        iMaskMap = new int[16];
        this.boolCacheEnabled = false;
        this.boolSixteenBit = false;
        this.byDIBBytes = null;
        this.boolProgressiveUpdate = false;
        this.byBitPlaneBuffer = null;
        this.p16BitConvertTable = null;
        this.byCache0 = null;
        this.byCache1 = null;
        this.byCache2 = null;
        this.pMaskMap = new short[16];
        this.iCacheDepth = 0;
        this.objTRRSPNewVideoModeData = null;
        this.objCellInfo = new Cell[1024];
        for (int i = 0; i < this.objCellInfo.length; ++i) {
            this.objCellInfo[i] = new Cell();
        }
        this.objTRRSPNewVideoModeData = new TRRSP_NEW_VIDEO_MODE_DATA();
        tRConnection.addVideoStream(this);
        this.keyboard = (tRConnection.getServerID().getOptions() & 4) > 0 ? new HIDKeyboard(this) : new PS2Keyboard(this);
        KeyHIDValue.setHIDMap(n);
    }

    protected void finalize() {
        if (this.boolConnected) {
            this.stopVideoStream();
        }
        if (this.objConn != null) {
            this.objConn.removeVideoStream(this);
        }
        this.objTRRSPNewVideoModeData = null;
    }

    public void setDeviceID(byte by) {
        this.byDeviceID = by;
    }

    public void setConnected(boolean bl) {
        this.boolConnected = bl;
    }

    public boolean isConnected() {
        return this.boolConnected;
    }

    public void setVideoStreamDevice(byte by) {
        this.objConn.setVideoStreamDevice(this, by);
    }

    public boolean connectVideoStream(String string, String string2, TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS, int n) {
        try {
            TRCMD_CONNECT_VIDEO_STREAM_DATA tRCMD_CONNECT_VIDEO_STREAM_DATA = new TRCMD_CONNECT_VIDEO_STREAM_DATA(4108);
            boolean bl = false;
            String string3 = null;
            string3 = n == 0 ? "<Connect><Portal>" + string + "</Portal><Target>" + string2 + "</Target></Connect>" : "<Connect ForceConnection=\"1\"><Portal>" + string + "</Portal><Target>" + string2 + "</Target></Connect>";
            tRCMD_CONNECT_VIDEO_STREAM_DATA.setCommand((byte)55);
            System.arraycopy(string3.getBytes(), 0, tRCMD_CONNECT_VIDEO_STREAM_DATA.toByteArray(), 44, string3.getBytes().length);
            tRCMD_CONNECT_VIDEO_STREAM_DATA.setCmdLength((short)(44 + string3.getBytes().length));
            tRCMD_CONNECT_VIDEO_STREAM_DATA.setCompParams(tRSRVR_COMP_PARAMS);
            bl = this.objConn.sendTRCmdExx(tRCMD_CONNECT_VIDEO_STREAM_DATA, true, null, 37, true, 20000);
            bl = this.objConn.processResponse(bl, tRCMD_CONNECT_VIDEO_STREAM_DATA);
            if (bl) {
                TRRSP_CONNECTED_DATA tRRSP_CONNECTED_DATA = new TRRSP_CONNECTED_DATA(tRCMD_CONNECT_VIDEO_STREAM_DATA.toByteArray());
                this.byDeviceID = tRRSP_CONNECTED_DATA.getDeviceID();
                this.objConn.setVideoStreamDevice(this, this.byDeviceID);
                this.boolConnected = true;
            }
            this.objConn.syncTRCmd();
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean switchVideoStream(String string, String string2) {
        try {
            TRCOMMAND tRCOMMAND = new TRCOMMAND(4108);
            boolean bl = false;
            String string3 = "<Connect><Portal>" + string + "</Portal><Target>" + string2 + "</Target></Connect>";
            this.boolWaitVideoMode = true;
            tRCOMMAND.setCommand((byte)57);
            System.arraycopy(string3.getBytes(), 0, tRCOMMAND.toByteArray(), 4, string3.getBytes().length);
            tRCOMMAND.setCmdLength((short)(4 + string3.getBytes().length));
            bl = this.objConn.sendTRCmdEx(tRCOMMAND, true, null, 2);
            bl = this.objConn.processResponse(bl, tRCOMMAND);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean stopVideoStream() {
        try {
            boolean bl = false;
            this.boolWaitVideoMode = true;
            this.boolConnected = false;
            TRCMD_STOP_VIDEO_STREAM_DATA tRCMD_STOP_VIDEO_STREAM_DATA = new TRCMD_STOP_VIDEO_STREAM_DATA(4108);
            tRCMD_STOP_VIDEO_STREAM_DATA.setCommand((byte)10);
            tRCMD_STOP_VIDEO_STREAM_DATA.setCmdLength((short)5);
            tRCMD_STOP_VIDEO_STREAM_DATA.setDeviceID(this.byDeviceID);
            bl = this.objConn.sendTRCmdEx(tRCMD_STOP_VIDEO_STREAM_DATA, true, null, 2, 20000);
            this.objConn.setVideoStreamDevice(null, this.byDeviceID);
            bl = this.objConn.processResponse(bl, tRCMD_STOP_VIDEO_STREAM_DATA);
            if (this.objConn != null) {
                this.objConn.removeVideoStream(this);
            }
            this.objTRRSPNewVideoModeData = null;
            this.bufferedImage = null;
            this.iPixelData = null;
            this.iPaletteTemp = null;
            this.freeDecompressionResources();
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean setVideoParams(TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS) {
        try {
            boolean bl = true;
            TRCMD_SET_VIDEO_PARAMS_DATA tRCMD_SET_VIDEO_PARAMS_DATA = new TRCMD_SET_VIDEO_PARAMS_DATA(4108);
            tRCMD_SET_VIDEO_PARAMS_DATA.setCommand((byte)45);
            tRCMD_SET_VIDEO_PARAMS_DATA.setCmdLength((short)89);
            tRCMD_SET_VIDEO_PARAMS_DATA.setDeviceID(this.byDeviceID);
            tRCMD_SET_VIDEO_PARAMS_DATA.setVideoParams(tRSRVR_VIDEO_PARAMS);
            bl = this.objConn.sendTRCmdEx(tRCMD_SET_VIDEO_PARAMS_DATA, true, null, 2);
            bl = this.objConn.processResponse(bl, tRCMD_SET_VIDEO_PARAMS_DATA);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean getVideoParams(TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS) {
        try {
            boolean bl = true;
            TRCMD_GET_VIDEO_PARAMS_DATA tRCMD_GET_VIDEO_PARAMS_DATA = new TRCMD_GET_VIDEO_PARAMS_DATA(4108);
            tRCMD_GET_VIDEO_PARAMS_DATA.setCommand((byte)11);
            tRCMD_GET_VIDEO_PARAMS_DATA.setCmdLength((short)5);
            tRCMD_GET_VIDEO_PARAMS_DATA.setDeviceID(this.byDeviceID);
            bl = this.objConn.sendTRCmdEx(tRCMD_GET_VIDEO_PARAMS_DATA, true, null, 29);
            if (bl) {
                TRRSP_VIDEO_PARAMS_DATA tRRSP_VIDEO_PARAMS_DATA = new TRRSP_VIDEO_PARAMS_DATA(tRCMD_GET_VIDEO_PARAMS_DATA.toByteArray());
                tRSRVR_VIDEO_PARAMS.setVideoParams(tRRSP_VIDEO_PARAMS_DATA.getParams());
            }
            bl = this.objConn.processResponse(bl, tRCMD_GET_VIDEO_PARAMS_DATA);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean setTargetParams(byte[] byArray, int n) {
        try {
            boolean bl = true;
            TRCMD_SET_TARGET_INTERFACE_DATA tRCMD_SET_TARGET_INTERFACE_DATA = new TRCMD_SET_TARGET_INTERFACE_DATA();
            tRCMD_SET_TARGET_INTERFACE_DATA.setCommand((byte)60);
            tRCMD_SET_TARGET_INTERFACE_DATA.setCmdLength((short)73);
            tRCMD_SET_TARGET_INTERFACE_DATA.setDeviceID(this.byDeviceID);
            if (byArray == null || byArray.length == 0) {
                return false;
            }
            tRCMD_SET_TARGET_INTERFACE_DATA.setIType(byArray);
            tRCMD_SET_TARGET_INTERFACE_DATA.setISettings(n);
            if (this.objConn.getServerID().getProtocolVersion() < 22) {
                return false;
            }
            bl = this.objConn.sendTRCmdEx(tRCMD_SET_TARGET_INTERFACE_DATA, true, null, 2);
            bl = this.objConn.processResponse(bl, tRCMD_SET_TARGET_INTERFACE_DATA);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean getTargetParams(TRSRVR_TARGET_PARAMS tRSRVR_TARGET_PARAMS) {
        try {
            boolean bl = true;
            TRCMD_GET_TARGET_INTERFACE_DATA tRCMD_GET_TARGET_INTERFACE_DATA = new TRCMD_GET_TARGET_INTERFACE_DATA();
            tRCMD_GET_TARGET_INTERFACE_DATA.setCommand((byte)59);
            tRCMD_GET_TARGET_INTERFACE_DATA.setCmdLength((short)145);
            tRCMD_GET_TARGET_INTERFACE_DATA.setDeviceID(this.byDeviceID);
            tRCMD_GET_TARGET_INTERFACE_DATA.setTargetParams(tRSRVR_TARGET_PARAMS);
            if (this.objConn.getServerID().getProtocolVersion() < 22) {
                return false;
            }
            bl = this.objConn.sendTRCmdEx(tRCMD_GET_TARGET_INTERFACE_DATA, true, null, 47);
            if (bl) {
                TRRSP_TARGET_PARAMS_DATA tRRSP_TARGET_PARAMS_DATA = new TRRSP_TARGET_PARAMS_DATA(tRCMD_GET_TARGET_INTERFACE_DATA.toByteArray());
                tRSRVR_TARGET_PARAMS.setTargetParams(tRRSP_TARGET_PARAMS_DATA.getParams());
            }
            bl = this.objConn.processResponse(bl, tRCMD_GET_TARGET_INTERFACE_DATA);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean setCompParams(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) {
        try {
            boolean bl = false;
            TRCMD_SET_COMP_PARAMS_DATA tRCMD_SET_COMP_PARAMS_DATA = new TRCMD_SET_COMP_PARAMS_DATA(4108);
            tRCMD_SET_COMP_PARAMS_DATA.setCommand((byte)46);
            tRCMD_SET_COMP_PARAMS_DATA.setCmdLength((short)45);
            tRCMD_SET_COMP_PARAMS_DATA.setDeviceID(this.byDeviceID);
            tRSRVR_COMP_PARAMS.setCompressMode(2);
            tRCMD_SET_COMP_PARAMS_DATA.setParams(tRSRVR_COMP_PARAMS);
            bl = this.objConn.sendTRCmdEx(tRCMD_SET_COMP_PARAMS_DATA, true, null, 2);
            bl = this.objConn.processResponse(bl, tRCMD_SET_COMP_PARAMS_DATA);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean getCompParams(TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS, boolean bl) {
        try {
            boolean bl2 = true;
            TRCMD_GET_COMP_PARAMS_DATA tRCMD_GET_COMP_PARAMS_DATA = new TRCMD_GET_COMP_PARAMS_DATA(4108);
            tRCMD_GET_COMP_PARAMS_DATA.setCommand((byte)13);
            tRCMD_GET_COMP_PARAMS_DATA.setCmdLength((short)6);
            tRCMD_GET_COMP_PARAMS_DATA.setDeviceID(this.byDeviceID);
            tRCMD_GET_COMP_PARAMS_DATA.setPVSettings((byte)(bl ? 1 : 0));
            bl2 = this.objConn.sendTRCmdEx(tRCMD_GET_COMP_PARAMS_DATA, true, null, 11);
            if (bl2) {
                TRRSP_VIDEO_PARAMS_DATA tRRSP_VIDEO_PARAMS_DATA = new TRRSP_VIDEO_PARAMS_DATA(tRCMD_GET_COMP_PARAMS_DATA.toByteArray());
                tRSRVR_VIDEO_PARAMS.setVideoParams(tRRSP_VIDEO_PARAMS_DATA.getParams());
            }
            bl2 = this.objConn.processResponse(bl2, tRCMD_GET_COMP_PARAMS_DATA);
            return bl2;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean refreshVideo(int n, boolean bl) {
        try {
            boolean bl2 = true;
            TRCMD_REFRESH_DATA tRCMD_REFRESH_DATA = new TRCMD_REFRESH_DATA();
            tRCMD_REFRESH_DATA.setCommand((byte)47);
            tRCMD_REFRESH_DATA.setCmdLength((short)10);
            tRCMD_REFRESH_DATA.setDeviceID(this.byDeviceID);
            tRCMD_REFRESH_DATA.setFlags(n);
            tRCMD_REFRESH_DATA.setAutoSense(bl);
            bl2 = this.objConn.sendTRCmd(tRCMD_REFRESH_DATA);
            return bl2;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean sendKeyData(int n, byte[] byArray) {
        try {
            byte[] byArray2 = new byte[21];
            boolean bl = false;
            TRCMD_KB_DATA tRCMD_KB_DATA = new TRCMD_KB_DATA(byArray2);
            tRCMD_KB_DATA.setCommand((byte)17);
            tRCMD_KB_DATA.setCmdLength((short)(5 + n));
            tRCMD_KB_DATA.setDeviceID(this.byDeviceID);
            System.arraycopy(byArray, 0, tRCMD_KB_DATA.toByteArray(), 5, n);
            bl = this.objConn.sendTRCmd(tRCMD_KB_DATA);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean sendMouseData(byte by, short s, short s2, short s3) {
        try {
            boolean bl = false;
            TRCMD_MOUSE_DATA tRCMD_MOUSE_DATA = new TRCMD_MOUSE_DATA();
            tRCMD_MOUSE_DATA.setCommand((byte)18);
            tRCMD_MOUSE_DATA.setCmdLength((short)10);
            tRCMD_MOUSE_DATA.setDeviceID(this.byDeviceID);
            tRCMD_MOUSE_DATA.setFunction(by);
            switch (by) {
                case 6: 
                case 7: {
                    tRCMD_MOUSE_DATA.setX(s);
                    break;
                }
                case 8: {
                    tRCMD_MOUSE_DATA.setX(s3);
                    break;
                }
                default: {
                    tRCMD_MOUSE_DATA.setX(s);
                }
            }
            tRCMD_MOUSE_DATA.setY(s2);
            bl = this.objConn.sendTRCmd(tRCMD_MOUSE_DATA);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean sendRawMouseData(int n, byte[] byArray) {
        try {
            boolean bl = false;
            if (n != 4) {
                TRConnection.setLastError(1);
                return false;
            }
            byte[] byArray2 = new byte[9];
            TRCMD_RAW_MOUSE_DATA tRCMD_RAW_MOUSE_DATA = new TRCMD_RAW_MOUSE_DATA(byArray2);
            tRCMD_RAW_MOUSE_DATA.setCommand((byte)19);
            tRCMD_RAW_MOUSE_DATA.setCmdLength((short)(5 + n));
            tRCMD_RAW_MOUSE_DATA.setDeviceID(this.byDeviceID);
            System.arraycopy(byArray, 0, tRCMD_RAW_MOUSE_DATA.toByteArray(), 5, n);
            bl = this.objConn.sendTRCmd(tRCMD_RAW_MOUSE_DATA);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean syncMouse(int n) {
        try {
            boolean bl = false;
            TRCMD_SYNC_MOUSE_DATA tRCMD_SYNC_MOUSE_DATA = new TRCMD_SYNC_MOUSE_DATA();
            tRCMD_SYNC_MOUSE_DATA.setCommand((byte)49);
            tRCMD_SYNC_MOUSE_DATA.setCmdLength((short)9);
            tRCMD_SYNC_MOUSE_DATA.setSyncFlags(n);
            tRCMD_SYNC_MOUSE_DATA.setDeviceID(this.byDeviceID);
            bl = this.objConn.sendTRCmd(tRCMD_SYNC_MOUSE_DATA);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean TRRSP_Notify(TRCOMMAND tRCOMMAND) {
        TRRSP_NOTIFY_DATA tRRSP_NOTIFY_DATA = new TRRSP_NOTIFY_DATA(tRCOMMAND.toByteArray());
        this.notify(tRRSP_NOTIFY_DATA.getEvent(), tRRSP_NOTIFY_DATA.getParam());
        return true;
    }

    public void targetParamsChanged(TRSRVR_TARGET_PARAMS tRSRVR_TARGET_PARAMS) {
    }

    public boolean TRRSP_New_Video_Mode(TRCOMMAND tRCOMMAND) {
        DataBufferInt dataBufferInt = null;
        try {
            int n;
            if (this.Const4 == -1) {
                this.iConst1 = 0x7C000000;
                this.Const4 = 0;
                while ((this.iConst1 >>> this.Const4 & 0xFFFFFF00) != 0) {
                    ++this.Const4;
                }
            }
            if (this.Const5 == -1) {
                this.iConst2 = 0x3E00000;
                this.Const5 = 0;
                while ((this.iConst2 >>> this.Const5 & 0xFFFFFF00) != 0) {
                    ++this.Const5;
                }
            }
            if (this.Const6 == -1) {
                this.iConst3 = 0x1F0000;
                this.Const6 = 0;
                while ((this.iConst3 >>> this.Const6 & 0xFFFFFF00) != 0) {
                    ++this.Const6;
                }
            }
            boolean bl = false;
            TRRSP_NEW_VIDEO_MODE_DATA tRRSP_NEW_VIDEO_MODE_DATA = new TRRSP_NEW_VIDEO_MODE_DATA(tRCOMMAND.toByteArray());
            if ((tRRSP_NEW_VIDEO_MODE_DATA.getFlags() & 2) != 0 && !this.boolWaitVideoMode) {
                return true;
            }
            this.objTRRSPNewVideoModeData.setNewVideoMode(tRRSP_NEW_VIDEO_MODE_DATA);
            this.objCCT = CCT.getCCT(this.objTRRSPNewVideoModeData.getCCT());
            this.freeDecompressionResources();
            if (this.objCCT == null) {
                this.objConn.dbNotify(1016, 39);
                return false;
            }
            this.boolProgressiveUpdate = (this.objTRRSPNewVideoModeData.getCompParams().getFlags() & 0x8000) != 0;
            if (this.objCCT.getBitsPerPixel() > 8) {
                this.pMaskMap = this.boolProgressiveUpdate ? this.progressiveMaskMap : (this.objCCT.getBitsPerPixel() == 12 ? this.RGB12MaskMap : this.RGB16MaskMap);
            }
            if (!(bl = this.allocateDecompressionResources())) {
                this.objConn.dbNotify(1021, 40);
                return false;
            }
            for (n = 0; n < 1024; ++n) {
                this.objCellInfo[n].setRefresh(false);
                this.objCellInfo[n].setCodePlane((short)0);
                this.objCellInfo[n].setCacheHead((short)0);
                this.objCellInfo[n].setCache(0, 0);
                this.objCellInfo[n].setCache(1, 0);
                this.objCellInfo[n].setCache(2, 0);
            }
            for (n = 0; n < this.objCCT.getCCTFieldCount(); ++n) {
                this.objDIB[n].getBMIHeader().setBIWidth(this.objTRRSPNewVideoModeData.getLogicalHSize());
                this.objDIB[n].getBMIHeader().setBIHeight(-this.objTRRSPNewVideoModeData.getLogicalVSize());
            }
            this.boolWaitVideoMode = false;
            this.bufferedImage = new BufferedImage(this.objTRRSPNewVideoModeData.getLogicalHSize(), this.objTRRSPNewVideoModeData.getLogicalVSize(), 2);
            dataBufferInt = (DataBufferInt)this.bufferedImage.getRaster().getDataBuffer();
            this.iPixelData = dataBufferInt.getData();
            this.newVideoModeNotify(this.objTRRSPNewVideoModeData);
            return true;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean TRRSP_Cache(TRCOMMAND tRCOMMAND) {
        boolean bl = true;
        TRRSP_CACHE_DATA tRRSP_CACHE_DATA = new TRRSP_CACHE_DATA(tRCOMMAND.toByteArray());
        if (this.boolWaitVideoMode) {
            return true;
        }
        int n = (this.objTRRSPNewVideoModeData.getLogicalHSize() + this.objTRRSPNewVideoModeData.getCellHSize() - 1) / this.objTRRSPNewVideoModeData.getCellHSize();
        int n2 = (this.objTRRSPNewVideoModeData.getLogicalVSize() + this.objTRRSPNewVideoModeData.getCellVSize() - 1) / this.objTRRSPNewVideoModeData.getCellVSize();
        short s = this.objTRRSPNewVideoModeData.getLogicalHSize();
        int n3 = 0;
        int n4 = -64;
        int n5 = 6;
        for (int i = tRRSP_CACHE_DATA.getStart(); i < tRRSP_CACHE_DATA.getStart() + tRRSP_CACHE_DATA.getCount(); ++i) {
            TRLIB_UPDATEINFO tRLIB_UPDATEINFO = new TRLIB_UPDATEINFO();
            tRLIB_UPDATEINFO.setLastField(true);
            tRLIB_UPDATEINFO.setFrameBuffer(this.byDIBBytes);
            tRLIB_UPDATEINFO.setBitMapInfo(this.objDIB[this.objCCT.getCCTFieldCount() - 1]);
            if ((tRRSP_CACHE_DATA.getCell()[n3] & n4) == 1) {
                int n6 = ((tRRSP_CACHE_DATA.getCell()[n3] & n4) >> n5) - 1;
                byte[] byArray = n6 == 0 ? this.byCache0 : (n6 == 1 ? this.byCache1 : this.byCache2);
                int n7 = i / n;
                int n8 = i % n;
                tRLIB_UPDATEINFO.getRect().setLeft((short)(n8 * this.objTRRSPNewVideoModeData.getCellHSize()));
                tRLIB_UPDATEINFO.getRect().setRight((short)(tRLIB_UPDATEINFO.getRect().getLeft() + this.objTRRSPNewVideoModeData.getCellHSize() - 1));
                tRLIB_UPDATEINFO.getRect().setTop((short)(n7 * this.objTRRSPNewVideoModeData.getCellVSize()));
                tRLIB_UPDATEINFO.getRect().setBottom((short)(tRLIB_UPDATEINFO.getRect().getTop() + this.objTRRSPNewVideoModeData.getCellVSize() - 1));
                for (int j = tRLIB_UPDATEINFO.getRect().getTop(); j <= tRLIB_UPDATEINFO.getRect().getBottom(); ++j) {
                    int n9 = j * s + tRLIB_UPDATEINFO.getRect().getLeft();
                    int n10 = j * s + tRLIB_UPDATEINFO.getRect().getLeft();
                    for (int k = tRLIB_UPDATEINFO.getRect().getLeft(); k <= tRLIB_UPDATEINFO.getRect().getRight(); ++k) {
                        this.byDIBBytes[n10++] = byArray[n9++];
                    }
                }
                if (tRLIB_UPDATEINFO.getRect().getRight() >= this.objTRRSPNewVideoModeData.getActualHSize()) {
                    tRLIB_UPDATEINFO.getRect().setRight((short)(this.objTRRSPNewVideoModeData.getActualHSize() - 1));
                }
                if (tRLIB_UPDATEINFO.getRect().getBottom() >= this.objTRRSPNewVideoModeData.getActualVSize()) {
                    tRLIB_UPDATEINFO.getRect().setBottom((short)(this.objTRRSPNewVideoModeData.getActualVSize() - 1));
                }
                this.updateNotify(tRLIB_UPDATEINFO);
            }
            n4 = (byte)(n4 >> 2);
            n5 -= 2;
            if (n4 != 0) continue;
            ++n3;
            n4 = -64;
            n5 = 6;
        }
        return bl;
    }

    public boolean TRRSP_Cell_Data(TRCOMMAND tRCOMMAND) {
        try {
            boolean bl = false;
            Rectangle rectangle = null;
            TRRECT tRRECT = new TRRECT();
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            TRRSP_CELL_DATA tRRSP_CELL_DATA = new TRRSP_CELL_DATA(tRCOMMAND.toByteArray());
            int n4 = tRRSP_CELL_DATA.getCmdLength() - 8;
            int n5 = tRRSP_CELL_DATA.getCellCount();
            byte by = tRRSP_CELL_DATA.getFlags();
            bl = this.objConn.read(tRCOMMAND.toByteArray(), 8, n4);
            if (!bl) {
                return false;
            }
            if (this.boolWaitVideoMode) {
                return true;
            }
            Int intVal = new Int(131072);
            if ((by & 1) != 0) {
                int n6 = 0;
                Lzo1xDecompressor lzo1xDecompressor = new Lzo1xDecompressor();
                n6 = lzo1xDecompressor.decompress(tRCOMMAND.toByteArray(), 8, n4, this.byBitPlaneBuffer, 0, intVal);
                if (n6 != 0) {
                    return false;
                }
            }
            int n7 = (this.objTRRSPNewVideoModeData.getLogicalHSize() + this.objTRRSPNewVideoModeData.getCellHSize() - 1) / this.objTRRSPNewVideoModeData.getCellHSize();
            int n8 = (this.objTRRSPNewVideoModeData.getLogicalVSize() + this.objTRRSPNewVideoModeData.getCellVSize() - 1) / this.objTRRSPNewVideoModeData.getCellVSize();
            int n9 = this.boolSixteenBit ? 2 : 1;
            short s = this.objTRRSPNewVideoModeData.getLogicalHSize();
            for (int i = 0; i < n5; ++i) {
                int n10;
                int n11;
                TRLIB_UPDATEINFO tRLIB_UPDATEINFO = new TRLIB_UPDATEINFO();
                tRLIB_UPDATEINFO.setLastField(true);
                short s2 = (short)(((this.byBitPlaneBuffer[n2 + 2] & 0xFF) << 8) + ((this.byBitPlaneBuffer[n2 + 3] & 0xFF) << 0));
                n2 += 8;
                if (s2 > this.objTRRSPNewVideoModeData.getCellCount()) break;
                int n12 = s2 / n7;
                int n13 = s2 % n7;
                int n14 = n13 * this.objTRRSPNewVideoModeData.getCellHSize();
                int n15 = n14 + this.objTRRSPNewVideoModeData.getCellHSize() - 1;
                int n16 = n12 * this.objTRRSPNewVideoModeData.getCellVSize();
                int n17 = n16 + this.objTRRSPNewVideoModeData.getCellVSize() - 1;
                if (this.boolSixteenBit) {
                    int n18;
                    int n19;
                    if (this.p16BitConvertTable == null) {
                        for (n11 = n16; n11 <= n17; ++n11) {
                            n = (n11 * s + n14) * n9;
                            n10 = (n15 - n14 + 1) * n9 / 32;
                            n19 = n14;
                            while (n10-- != 0) {
                                n3 = n11 * s + n19;
                                for (int j = 0; j < 16; ++j) {
                                    n18 = (this.byBitPlaneBuffer[n2++] & 0xFF) << 8 | this.byBitPlaneBuffer[n2++] & 0xFF;
                                    this.iPixelData[n3++] = 0xFF000000 | (n18 & 0x7C00) << 9 | (n18 & 0x3E0) << 6 | (n18 & 0x1F) << 3;
                                }
                                n19 += 16;
                            }
                        }
                    } else {
                        for (n11 = n16; n11 <= n17; ++n11) {
                            n = (n11 * s + n14) * n9;
                            n10 = (n15 - n14 + 1) / 16 * n9;
                            n19 = n14;
                            while (n10-- != 0) {
                                n3 = n11 * s + n19;
                                for (int j = 0; j < 8; ++j) {
                                    n18 = (this.byBitPlaneBuffer[n2++] & 0xFF) << 8 | this.byBitPlaneBuffer[n2++] & 0xFF;
                                    this.iPixelData[n3++] = 0xFF000000 | (n18 & 0xF00) << 12 | (n18 & 0xF0) << 8 | (n18 & 0xF) << 4;
                                }
                                n19 += 8;
                            }
                        }
                    }
                } else {
                    for (n11 = n16; n11 <= n17; ++n11) {
                        n = (n11 * s + n14) * n9;
                        for (n10 = n15 - n14 + 1; n10 > 0; --n10) {
                            this.iPixelData[n++] = this.iPaletteTemp[this.byBitPlaneBuffer[n2++] & 0xFF];
                        }
                    }
                }
                if (n15 >= this.objTRRSPNewVideoModeData.getActualHSize()) {
                    n15 = this.objTRRSPNewVideoModeData.getActualHSize() - 1;
                }
                if (n17 >= this.objTRRSPNewVideoModeData.getActualVSize()) {
                    n17 = this.objTRRSPNewVideoModeData.getActualVSize() - 1;
                }
                rectangle = rectangle == null ? new Rectangle(n14, n16, n15 - n14 + 1, n17 - n16 + 1) : rectangle.union(new Rectangle(n14, n16, n15 - n14 + 1, n17 - n16 + 1));
                tRLIB_UPDATEINFO.setRectangle(rectangle);
                tRLIB_UPDATEINFO.setImage(this.bufferedImage);
                this.updateNotify(tRLIB_UPDATEINFO);
                rectangle = null;
            }
            return true;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean TRRSP_Bitplane(TRCOMMAND tRCOMMAND) {
        try {
            boolean bl = true;
            TRRECT tRRECT = new TRRECT();
            TRLIB_UPDATEINFO tRLIB_UPDATEINFO = new TRLIB_UPDATEINFO();
            TRLIB_UPDATEINFO tRLIB_UPDATEINFO2 = new TRLIB_UPDATEINFO();
            TRRSP_BITPLANE_DATA tRRSP_BITPLANE_DATA = new TRRSP_BITPLANE_DATA(tRCOMMAND.toByteArray());
            TRRECT tRRECT2 = tRRSP_BITPLANE_DATA.getArea();
            tRRECT.setBottom(tRRECT2.getBottom());
            tRRECT.setLeft(tRRECT2.getLeft());
            tRRECT.setRight(tRRECT2.getRight());
            tRRECT.setTop(tRRECT2.getTop());
            byte by = tRRSP_BITPLANE_DATA.getPlane();
            int n = tRRSP_BITPLANE_DATA.getCmdLength() - 14;
            bl = this.objConn.read(tRRSP_BITPLANE_DATA.toByteArray(), 0, n);
            if (this.boolWaitVideoMode) {
                return false;
            }
            if (bl) {
                this.merge(tRRECT, tRRSP_BITPLANE_DATA.toByteArray(), by, true);
                for (int i = 0; i < this.objCCT.getCCTFieldCount(); ++i) {
                    if (by == this.objCCT.getCCTField()[i].getBitCount() - 1) {
                        tRLIB_UPDATEINFO.setRect(tRRECT);
                        tRLIB_UPDATEINFO.setBitMapInfo(this.objDIB[i]);
                        tRLIB_UPDATEINFO.setFrameBuffer(this.byDIBBytes);
                        tRLIB_UPDATEINFO.setImage(this.bufferedImage);
                        tRLIB_UPDATEINFO.setLastField(i == this.objCCT.getCCTFieldCount() - 1);
                        tRLIB_UPDATEINFO2 = tRLIB_UPDATEINFO;
                        if (tRLIB_UPDATEINFO.getRect().getRight() >= this.objTRRSPNewVideoModeData.getActualHSize()) {
                            tRLIB_UPDATEINFO.getRect().setRight((short)(this.objTRRSPNewVideoModeData.getActualHSize() - 1));
                        }
                        if (tRLIB_UPDATEINFO.getRect().getBottom() >= this.objTRRSPNewVideoModeData.getActualVSize()) {
                            tRLIB_UPDATEINFO.getRect().setBottom((short)(this.objTRRSPNewVideoModeData.getActualVSize() - 1));
                        }
                        if (this.boolProgressiveUpdate || tRLIB_UPDATEINFO.isLastField()) {
                            this.updateNotify(tRLIB_UPDATEINFO);
                        }
                        if (!tRLIB_UPDATEINFO.isLastField()) break;
                        this.updateCache(tRLIB_UPDATEINFO);
                        break;
                    }
                    if (by < this.objCCT.getCCTField()[i].getBitCount()) break;
                    by = (byte)(by - this.objCCT.getCCTField()[i].getBitCount());
                }
            }
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean TRRSP_Packed(TRCOMMAND tRCOMMAND) {
        return false;
    }

    public boolean TRRSP_Video_Marker(TRCOMMAND tRCOMMAND) {
        try {
            short s = 0;
            boolean bl = true;
            TRRSP_VIDEO_MARKER_DATA tRRSP_VIDEO_MARKER_DATA = new TRRSP_VIDEO_MARKER_DATA(tRCOMMAND.toByteArray());
            s = tRRSP_VIDEO_MARKER_DATA.getCount();
            tRRSP_VIDEO_MARKER_DATA.setCommand((byte)48);
            tRRSP_VIDEO_MARKER_DATA.setCmdLength((short)7);
            tRRSP_VIDEO_MARKER_DATA.setCount(s);
            bl = this.objConn.sendTRCmd(tRCOMMAND);
            return bl;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public boolean TRRSP_KB_Status(TRCOMMAND tRCOMMAND) {
        TRRSP_KB_STATUS_DATA tRRSP_KB_STATUS_DATA = new TRRSP_KB_STATUS_DATA(tRCOMMAND.toByteArray());
        this.setLEDState((tRRSP_KB_STATUS_DATA.getStatus() & 0x10) != 0, (tRRSP_KB_STATUS_DATA.getStatus() & 0x20) != 0, (tRRSP_KB_STATUS_DATA.getStatus() & 0x40) != 0);
        this.setScanCodeSet((tRRSP_KB_STATUS_DATA.getStatus() & 0xC) >> 2);
        this.setTargetType((tRRSP_KB_STATUS_DATA.getStatus() & 0xF00) != 0);
        if (tRRSP_KB_STATUS_DATA.getCount() != 0) {
            this.setScanCode3Data(tRRSP_KB_STATUS_DATA.getData(), tRRSP_KB_STATUS_DATA.getCount());
        }
        this.notify(1031, (tRRSP_KB_STATUS_DATA.getStatus() & 0x70) >> 4);
        return true;
    }

    public boolean allocateDecompressionResources() {
        boolean bl = false;
        this.boolSixteenBit = this.objCCT.getBitsPerPixel() > 8;
        this.byBitPlaneBuffer = new byte[131072];
        if (this.objCCT.getBitsPerPixel() == 12) {
            this.p16BitConvertTable = new short[4096];
            Arrays.fill(this.p16BitConvertTable, (short)0);
            for (int i = 0; i < 4096; ++i) {
                this.p16BitConvertTable[i] = (short)((i & 0xF00) << 3 | ((i & 0x100) != 0 ? 1024 : 0) | (i & 0xF0) << 2 | ((i & 0x10) != 0 ? 32 : 0) | (i & 0xF) << 1 | ((i & 1) != 0 ? 1 : 0));
            }
        }
        int n = this.objTRRSPNewVideoModeData.getLogicalHSize() * this.objTRRSPNewVideoModeData.getLogicalVSize();
        if (this.boolSixteenBit) {
            n *= 2;
        }
        this.byDIBBytes = new byte[n];
        Arrays.fill(this.byDIBBytes, (byte)0);
        if (this.boolCacheEnabled) {
            this.byCache0 = new byte[n];
            this.byCache1 = new byte[n];
            this.byCache2 = new byte[n];
        }
        if (this.buildDIBs()) {
            bl = true;
        }
        return bl;
    }

    public void freeDecompressionResources() {
        this.byBitPlaneBuffer = null;
        this.byDIBBytes = null;
        this.byCache0 = null;
        this.byCache1 = null;
        this.byCache2 = null;
        this.p16BitConvertTable = null;
        for (int i = 0; i < this.objDIB.length; ++i) {
            this.objDIB[i] = null;
        }
    }

    public boolean buildDIBs() {
        boolean bl = false;
        int n = this.objCCT.getBitsPerPixel() <= 8 ? 1024 : 131072;
        byte[] byArray = new byte[n * this.objCCT.getCCTFieldCount()];
        this.iPaletteTemp = new int[n * this.objCCT.getCCTFieldCount() / 4];
        BitMapInfo bitMapInfo = new BitMapInfo();
        bitMapInfo.setColors(byArray);
        if (byArray != null) {
            for (int i = 0; i < this.objCCT.getCCTFieldCount(); ++i) {
                int n2;
                this.objDIB[i] = bitMapInfo;
                this.objDIB[i].getBMIHeader().setBISize(40);
                this.objDIB[i].getBMIHeader().setBIWidth(1280);
                this.objDIB[i].getBMIHeader().setBIHeight(-1024);
                this.objDIB[i].getBMIHeader().setBIPlanes((short)1);
                if (this.objCCT.getBitsPerPixel() <= 8) {
                    this.objDIB[i].getBMIHeader().setBIBitCount((short)8);
                } else {
                    this.objDIB[i].getBMIHeader().setBIBitCount((short)16);
                }
                this.objDIB[i].getBMIHeader().setBICompression(0);
                this.objDIB[i].getBMIHeader().setBISizeImage(0);
                this.objDIB[i].getBMIHeader().setBIXPelsPerMeter(3780);
                this.objDIB[i].getBMIHeader().setBIYPelsPerMeter(3780);
                this.objDIB[i].getBMIHeader().setBIClrUsed(this.objCCT.getColors());
                this.objDIB[i].getBMIHeader().setBIClrImportant(0);
                if (this.objCCT.getBitsPerPixel() > 8) continue;
                for (n2 = 0; n2 < this.objCCT.getColors(); ++n2) {
                    int n3 = this.objCCT.getRGBCode(n2).getCode() * 4;
                    byArray[n3 + 0] = this.objCCT.getRGBCode(n2).getRGBTriple().getRGBTBlue();
                    byArray[n3 + 1] = this.objCCT.getRGBCode(n2).getRGBTriple().getRGBTGreen();
                    byArray[n3 + 2] = this.objCCT.getRGBCode(n2).getRGBTriple().getRGBTRed();
                    byArray[n3 + 3] = 0;
                }
                n2 = 0;
                for (int j = 0; j < this.iPaletteTemp.length; ++j) {
                    int n4 = byArray[n2++] & 0xFF;
                    int n5 = byArray[n2++] & 0xFF;
                    int n6 = byArray[n2++] & 0xFF;
                    int n7 = byArray[n2++] & 0xFF;
                    this.iPaletteTemp[j] = ((n7 * 256 + n6) * 256 + n5) * 256 + n4 | 0xFF000000;
                }
            }
            bl = true;
        } else {
            this.objConn.dbNotify(1021, 44);
        }
        return bl;
    }

    public void merge(TRRECT tRRECT, byte[] byArray, int n, boolean bl) {
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = tRRECT.getLeft();
        short s = tRRECT.getRight();
        int n8 = tRRECT.getTop();
        short s2 = tRRECT.getBottom();
        short s3 = this.objTRRSPNewVideoModeData.getLogicalHSize();
        byte by = (byte)(1 << n);
        byte by2 = ~by;
        if (!this.boolSixteenBit) {
            if (!bl) {
                for (n3 = n8; n3 < s2 + 1; ++n3) {
                    n4 = n3 * s3 + n7;
                    n2 = n7;
                    while (n2 < s + 1) {
                        byte by3 = byArray[n6];
                        if ((by3 & 0x80) != 0) {
                            this.byDIBBytes[n4] = by;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            this.byDIBBytes[n4] = 0;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by3 & 0x40) != 0) {
                            this.byDIBBytes[n4] = by;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            this.byDIBBytes[n4] = 0;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by3 & 0x20) != 0) {
                            this.byDIBBytes[n4] = by;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            this.byDIBBytes[n4] = 0;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by3 & 0x10) != 0) {
                            this.byDIBBytes[n4] = by;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            this.byDIBBytes[n4] = 0;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by3 & 8) != 0) {
                            this.byDIBBytes[n4] = by;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            this.byDIBBytes[n4] = 0;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by3 & 4) != 0) {
                            this.byDIBBytes[n4] = by;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            this.byDIBBytes[n4] = 0;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by3 & 2) != 0) {
                            this.byDIBBytes[n4] = by;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            this.byDIBBytes[n4] = 0;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by3 & 1) != 0) {
                            this.byDIBBytes[n4] = by;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            this.byDIBBytes[n4] = 0;
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        n2 += 8;
                        ++n6;
                    }
                }
            } else {
                for (n3 = n8; n3 < s2 + 1; ++n3) {
                    n4 = n3 * s3 + n7;
                    n2 = n7;
                    while (n2 < s + 1) {
                        byte by4 = byArray[n6];
                        if ((by4 & 0x80) != 0) {
                            int n9 = n4;
                            this.byDIBBytes[n9] = (byte)(this.byDIBBytes[n9] | by);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            int n10 = n4;
                            this.byDIBBytes[n10] = (byte)(this.byDIBBytes[n10] & by2);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by4 & 0x40) != 0) {
                            int n11 = n4;
                            this.byDIBBytes[n11] = (byte)(this.byDIBBytes[n11] | by);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            int n12 = n4;
                            this.byDIBBytes[n12] = (byte)(this.byDIBBytes[n12] & by2);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by4 & 0x20) != 0) {
                            int n13 = n4;
                            this.byDIBBytes[n13] = (byte)(this.byDIBBytes[n13] | by);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            int n14 = n4;
                            this.byDIBBytes[n14] = (byte)(this.byDIBBytes[n14] & by2);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by4 & 0x10) != 0) {
                            int n15 = n4;
                            this.byDIBBytes[n15] = (byte)(this.byDIBBytes[n15] | by);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            int n16 = n4;
                            this.byDIBBytes[n16] = (byte)(this.byDIBBytes[n16] & by2);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by4 & 8) != 0) {
                            int n17 = n4;
                            this.byDIBBytes[n17] = (byte)(this.byDIBBytes[n17] | by);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            int n18 = n4;
                            this.byDIBBytes[n18] = (byte)(this.byDIBBytes[n18] & by2);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by4 & 4) != 0) {
                            int n19 = n4;
                            this.byDIBBytes[n19] = (byte)(this.byDIBBytes[n19] | by);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            int n20 = n4;
                            this.byDIBBytes[n20] = (byte)(this.byDIBBytes[n20] & by2);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by4 & 2) != 0) {
                            int n21 = n4;
                            this.byDIBBytes[n21] = (byte)(this.byDIBBytes[n21] | by);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            int n22 = n4;
                            this.byDIBBytes[n22] = (byte)(this.byDIBBytes[n22] & by2);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        if ((by4 & 1) != 0) {
                            int n23 = n4;
                            this.byDIBBytes[n23] = (byte)(this.byDIBBytes[n23] | by);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        } else {
                            int n24 = n4;
                            this.byDIBBytes[n24] = (byte)(this.byDIBBytes[n24] & by2);
                            this.iPixelData[n4] = this.iPaletteTemp[this.byDIBBytes[n4] & 0xFF];
                            ++n4;
                        }
                        n2 += 8;
                        ++n6;
                    }
                }
            }
        } else {
            byte[] byArray2 = new byte[2];
            byte[] byArray3 = new byte[2];
            short s4 = this.pMaskMap[n];
            short s5 = ~s4;
            byArray2[1] = (byte)(s4 >>> 0 & 0xFF);
            byArray2[0] = (byte)(s4 >>> 8 & 0xFF);
            byArray3[1] = (byte)(s5 >>> 0 & 0xFF);
            byArray3[0] = (byte)(s5 >>> 8 & 0xFF);
            for (n3 = n8; n3 <= s2; ++n3) {
                n4 = 2 * (n3 * s3 + n7);
                n2 = n7;
                while (n2 < s + 1) {
                    n5 = n3 * s3 + n2;
                    byte by5 = byArray[n6];
                    short s6 = (short)(byArray[n6] & 0xFF);
                    if ((s6 & 0x80) != 0) {
                        int n25 = n4++;
                        this.byDIBBytes[n25] = (byte)(this.byDIBBytes[n25] | byArray2[1]);
                        int n26 = n4++;
                        this.byDIBBytes[n26] = (byte)(this.byDIBBytes[n26] | byArray2[0]);
                    } else {
                        int n27 = n4++;
                        this.byDIBBytes[n27] = (byte)(this.byDIBBytes[n27] & byArray3[1]);
                        int n28 = n4++;
                        this.byDIBBytes[n28] = (byte)(this.byDIBBytes[n28] & byArray3[0]);
                    }
                    if ((s6 & 0x40) != 0) {
                        int n29 = n4++;
                        this.byDIBBytes[n29] = (byte)(this.byDIBBytes[n29] | byArray2[1]);
                        int n30 = n4++;
                        this.byDIBBytes[n30] = (byte)(this.byDIBBytes[n30] | byArray2[0]);
                    } else {
                        int n31 = n4++;
                        this.byDIBBytes[n31] = (byte)(this.byDIBBytes[n31] & byArray3[1]);
                        int n32 = n4++;
                        this.byDIBBytes[n32] = (byte)(this.byDIBBytes[n32] & byArray3[0]);
                    }
                    if ((s6 & 0x20) != 0) {
                        int n33 = n4++;
                        this.byDIBBytes[n33] = (byte)(this.byDIBBytes[n33] | byArray2[1]);
                        int n34 = n4++;
                        this.byDIBBytes[n34] = (byte)(this.byDIBBytes[n34] | byArray2[0]);
                    } else {
                        int n35 = n4++;
                        this.byDIBBytes[n35] = (byte)(this.byDIBBytes[n35] & byArray3[1]);
                        int n36 = n4++;
                        this.byDIBBytes[n36] = (byte)(this.byDIBBytes[n36] & byArray3[0]);
                    }
                    if ((s6 & 0x10) != 0) {
                        int n37 = n4++;
                        this.byDIBBytes[n37] = (byte)(this.byDIBBytes[n37] | byArray2[1]);
                        int n38 = n4++;
                        this.byDIBBytes[n38] = (byte)(this.byDIBBytes[n38] | byArray2[0]);
                    } else {
                        int n39 = n4++;
                        this.byDIBBytes[n39] = (byte)(this.byDIBBytes[n39] & byArray3[1]);
                        int n40 = n4++;
                        this.byDIBBytes[n40] = (byte)(this.byDIBBytes[n40] & byArray3[0]);
                    }
                    if ((s6 & 8) != 0) {
                        int n41 = n4++;
                        this.byDIBBytes[n41] = (byte)(this.byDIBBytes[n41] | byArray2[1]);
                        int n42 = n4++;
                        this.byDIBBytes[n42] = (byte)(this.byDIBBytes[n42] | byArray2[0]);
                    } else {
                        int n43 = n4++;
                        this.byDIBBytes[n43] = (byte)(this.byDIBBytes[n43] & byArray3[1]);
                        int n44 = n4++;
                        this.byDIBBytes[n44] = (byte)(this.byDIBBytes[n44] & byArray3[0]);
                    }
                    if ((s6 & 4) != 0) {
                        int n45 = n4++;
                        this.byDIBBytes[n45] = (byte)(this.byDIBBytes[n45] | byArray2[1]);
                        int n46 = n4++;
                        this.byDIBBytes[n46] = (byte)(this.byDIBBytes[n46] | byArray2[0]);
                    } else {
                        int n47 = n4++;
                        this.byDIBBytes[n47] = (byte)(this.byDIBBytes[n47] & byArray3[1]);
                        int n48 = n4++;
                        this.byDIBBytes[n48] = (byte)(this.byDIBBytes[n48] & byArray3[0]);
                    }
                    if ((s6 & 2) != 0) {
                        int n49 = n4++;
                        this.byDIBBytes[n49] = (byte)(this.byDIBBytes[n49] | byArray2[1]);
                        int n50 = n4++;
                        this.byDIBBytes[n50] = (byte)(this.byDIBBytes[n50] | byArray2[0]);
                    } else {
                        int n51 = n4++;
                        this.byDIBBytes[n51] = (byte)(this.byDIBBytes[n51] & byArray3[1]);
                        int n52 = n4++;
                        this.byDIBBytes[n52] = (byte)(this.byDIBBytes[n52] & byArray3[0]);
                    }
                    if ((s6 & 1) != 0) {
                        int n53 = n4++;
                        this.byDIBBytes[n53] = (byte)(this.byDIBBytes[n53] | byArray2[1]);
                        int n54 = n4++;
                        this.byDIBBytes[n54] = (byte)(this.byDIBBytes[n54] | byArray2[0]);
                    } else {
                        int n55 = n4++;
                        this.byDIBBytes[n55] = (byte)(this.byDIBBytes[n55] & byArray3[1]);
                        int n56 = n4++;
                        this.byDIBBytes[n56] = (byte)(this.byDIBBytes[n56] & byArray3[0]);
                    }
                    int n57 = n4 - 16;
                    for (int i = 0; i < 8; ++i) {
                        int n58 = (this.byDIBBytes[n57 + 1] & 0xFF) * 256 + (this.byDIBBytes[n57] & 0xFF);
                        int n59 = n58 << 16 & 0xFFFF0000;
                        this.iPixelData[n5++] = (n59 & this.iConst1) >>> this.Const4 << 16 & 0xFF0000 | (n59 & this.iConst2) >>> this.Const5 << 8 & 0xFF00 | (n59 & this.iConst3) >>> this.Const6 & 0xFF | 0xFF000000;
                        n57 += 2;
                    }
                    n2 += 8;
                    ++n6;
                }
            }
        }
    }

    public void updateCache(TRLIB_UPDATEINFO tRLIB_UPDATEINFO) {
        TRRECT tRRECT = new TRRECT();
        boolean bl = true;
        if (!this.boolCacheEnabled) {
            return;
        }
        int n = (this.objTRRSPNewVideoModeData.getLogicalHSize() + this.objTRRSPNewVideoModeData.getCellHSize() - 1) / this.objTRRSPNewVideoModeData.getCellHSize();
        int n2 = (this.objTRRSPNewVideoModeData.getLogicalVSize() + this.objTRRSPNewVideoModeData.getCellVSize() - 1) / this.objTRRSPNewVideoModeData.getCellVSize();
        short s = this.objTRRSPNewVideoModeData.getLogicalHSize();
        for (int i = tRLIB_UPDATEINFO.getRect().getTop(); i <= tRLIB_UPDATEINFO.getRect().getBottom(); i += this.objTRRSPNewVideoModeData.getCellVSize()) {
            for (int j = tRLIB_UPDATEINFO.getRect().getLeft(); j <= tRLIB_UPDATEINFO.getRect().getRight(); j += this.objTRRSPNewVideoModeData.getCellHSize()) {
                int n3 = i / this.objTRRSPNewVideoModeData.getCellVSize();
                int n4 = j / this.objTRRSPNewVideoModeData.getCellHSize();
                Cell cell = this.objCellInfo[n3 * n + n4];
                short s2 = cell.getCacheHead();
                cell.setCacheHead((short)(s2 + 1));
                if (cell.getCacheHead() == 3) {
                    cell.setCacheHead((short)0);
                }
                byte[] byArray = s2 == 0 ? this.byCache0 : (s2 == 1 ? this.byCache1 : this.byCache2);
                tRRECT.setLeft((short)(n4 * this.objTRRSPNewVideoModeData.getCellHSize()));
                tRRECT.setRight((short)(tRRECT.getLeft() + this.objTRRSPNewVideoModeData.getCellHSize() - 1));
                tRRECT.setTop((short)(n3 * this.objTRRSPNewVideoModeData.getCellVSize()));
                tRRECT.setBottom((short)(tRRECT.getTop() + this.objTRRSPNewVideoModeData.getCellVSize() - 1));
                for (int k = tRRECT.getTop(); k <= tRRECT.getBottom(); ++k) {
                    int n5 = k * s + tRRECT.getLeft();
                    int n6 = k * s + tRRECT.getLeft();
                    for (int i2 = tRRECT.getLeft(); i2 <= tRRECT.getRight(); ++i2) {
                        byArray[n6++] = this.byDIBBytes[n5++];
                    }
                }
                if (s2 == 0) {
                    this.byCache0 = byArray;
                    continue;
                }
                if (s2 == 1) {
                    this.byCache1 = byArray;
                    continue;
                }
                this.byCache2 = byArray;
            }
        }
    }

    public byte getDeviceID() {
        return this.byDeviceID;
    }

    @Override
    public void notify(int n, int n2) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(200, 2, "NOTIFY  Event=" + n + " Param=" + n2);
        }
    }

    @Override
    public void updateNotify(TRLIB_UPDATEINFO tRLIB_UPDATEINFO) {
    }

    @Override
    public void newVideoModeNotify(TRRSP_NEW_VIDEO_MODE_DATA tRRSP_NEW_VIDEO_MODE_DATA) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(200, 2, "newVideoModeNotify=" + tRRSP_NEW_VIDEO_MODE_DATA);
        }
    }

    public void setNext(TRVideoStream tRVideoStream) {
        this.objVSNext = tRVideoStream;
    }

    public TRVideoStream getNext() {
        return this.objVSNext;
    }

    private int Endian(int n) {
        return n << 24 | (n & 0xFF00) << 8 | (n & 0xFF0000) >> 8 | n >> 24;
    }

    public static void main(String[] stringArray) {
        TRLIB_COMM tRLIB_COMM = new TRLIB_COMM();
        TRLIB_USERINFO tRLIB_USERINFO = new TRLIB_USERINFO();
        TRLIB_REFERRAL_COMM tRLIB_REFERRAL_COMM = new TRLIB_REFERRAL_COMM();
        TRSRVR_SERIAL_PARAMS tRSRVR_SERIAL_PARAMS = new TRSRVR_SERIAL_PARAMS();
        boolean bl = false;
        TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS = new TRSRVR_COMP_PARAMS();
        int[] nArray = new int[]{0, 0, 0};
        tRSRVR_COMP_PARAMS.setFlags(56);
        tRSRVR_COMP_PARAMS.setCCT((short)10);
        tRSRVR_COMP_PARAMS.setCacheDepth((short)0);
        tRSRVR_COMP_PARAMS.setCompressMode(0);
        tRSRVR_COMP_PARAMS.setSpeed(0);
        tRSRVR_COMP_PARAMS.setMinFrameTime(0);
        tRSRVR_COMP_PARAMS.setMaxFrameTime(0);
        tRSRVR_COMP_PARAMS.setSmoothing(6);
        tRSRVR_COMP_PARAMS.setReserved(nArray);
        TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS = new TRSRVR_VIDEO_PARAMS();
        TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS2 = new TRSRVR_VIDEO_PARAMS();
        int[] nArray2 = new int[]{0, 0, 0};
        byte[] byArray = new byte[]{0, 0, 0};
        tRSRVR_VIDEO_PARAMS.setAdType(0);
        tRSRVR_VIDEO_PARAMS.setSettings(1023);
        tRSRVR_VIDEO_PARAMS.setFlags(1241188);
        tRSRVR_VIDEO_PARAMS.setAutoSense(0);
        tRSRVR_VIDEO_PARAMS.setNoiseFilter(4);
        tRSRVR_VIDEO_PARAMS.setRedGain(128);
        tRSRVR_VIDEO_PARAMS.setRedOffset(32);
        tRSRVR_VIDEO_PARAMS.setGreenGain(128);
        tRSRVR_VIDEO_PARAMS.setGreenOffset(32);
        tRSRVR_VIDEO_PARAMS.setBlueGain(128);
        tRSRVR_VIDEO_PARAMS.setBlueOffset(32);
        tRSRVR_VIDEO_PARAMS.setPllDivider(1343);
        tRSRVR_VIDEO_PARAMS.setPllOffset(20);
        tRSRVR_VIDEO_PARAMS.setVoltage(8237536);
        tRSRVR_VIDEO_PARAMS.setCurrent(1240484);
        tRSRVR_VIDEO_PARAMS.setReserved1(nArray2);
        tRSRVR_VIDEO_PARAMS.setReserved2(byArray);
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
            TRVideoStream tRVideoStream = new TRVideoStream(tRConnection, 0);
            bl3 = tRVideoStream.connectVideoStream("//*[@id=FG0]", "//*[@id=S0T1]", tRSRVR_COMP_PARAMS, 0);
            Thread.sleep(500L);
            System.out.print("\n--------------------------------" + bl3);
            bl3 = tRVideoStream.stopVideoStream();
            System.out.print("\nDisconnect Connection");
            System.out.println("--------------------------------");
            tRConnection.disConnect();
            System.out.println("**************+Disconnect Successful **********");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public boolean TRRSP_Compressed(TRCOMMAND tRCOMMAND) throws RRCGeneralException {
        try {
            throw new RRCGeneralException("TRRSP_Compressed: JBIG Compression NOT Supported in this release.");
        }
        catch (RRCGeneralException rRCGeneralException) {
            RRCLogger.logException(rRCGeneralException);
            throw rRCGeneralException;
        }
    }

    public static void saveToFile(int[] nArray, String string) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(string);
            byte[] byArray = new byte[nArray.length * 4];
            int n = 0;
            for (int i = 0; i < nArray.length; ++i) {
                byArray[n + 3] = (byte)(nArray[i] >>> 0 & 0xFF);
                byArray[n + 2] = (byte)(nArray[i] >>> 8 & 0xFF);
                byArray[n + 1] = (byte)(nArray[i] >>> 16 & 0xFF);
                byArray[n + 0] = (byte)(nArray[i] >>> 24 & 0xFF);
                ++n;
            }
            fileOutputStream.write(byArray);
            fileOutputStream.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void saveToFile(byte[] byArray, String string) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(string);
            fileOutputStream.write(byArray);
            fileOutputStream.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void saveToFile(byte[] byArray, int n, int n2, String string) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(string);
            fileOutputStream.write(byArray, n, n2);
            fileOutputStream.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public boolean setKeyState(short s, boolean bl, boolean bl2) {
        return this.keyboard.setKeyState(s, bl, bl2);
    }

    @Override
    public boolean getKeyState(short s) {
        return this.keyboard.getKeyState(s);
    }

    @Override
    public boolean releaseAllPressedKeys() {
        return this.keyboard.releaseAllPressedKeys();
    }

    @Override
    public boolean releaseKeyIfPressed(short s) {
        return this.keyboard.releaseKeyIfPressed(s);
    }

    @Override
    public boolean toogleKeyState(short s) {
        return this.keyboard.toogleKeyState(s);
    }

    @Override
    public boolean outputKeyData(byte[] byArray, int n, int n2) {
        return this.keyboard.outputKeyData(byArray, n, n2);
    }

    @Override
    public boolean flushKeyData() {
        return this.keyboard.flushKeyData();
    }

    @Override
    public void setLEDState(boolean bl, boolean bl2, boolean bl3) {
        this.keyboard.setLEDState(bl, bl2, bl3);
    }

    @Override
    public boolean getScrollLockStatus() {
        return this.keyboard.getScrollLockStatus();
    }

    @Override
    public boolean getNumLockStatus() {
        return this.keyboard.getNumLockStatus();
    }

    @Override
    public boolean getCapsLockStatus() {
        return this.keyboard.getCapsLockStatus();
    }

    @Override
    public void setScanCode3Data(byte[] byArray, int n) {
        this.keyboard.setScanCode3Data(byArray, n);
    }

    @Override
    public void setScanCodeSet(int n) {
        this.keyboard.setScanCodeSet(n);
    }

    @Override
    public void setTargetType(boolean bl) {
        this.keyboard.setTargetType(bl);
    }

    @Override
    public boolean isTargetType() {
        return this.keyboard.isTargetType();
    }
}

