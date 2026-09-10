/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  sun.misc.BASE64Decoder
 *  sun.misc.BASE64Encoder
 */
package javaclientlib.clientlib;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javaclientlib.clientlib.ITRConnection;
import javaclientlib.clientlib.RSPDEF;
import javaclientlib.clientlib.TRKeepAliveThread;
import javaclientlib.clientlib.TRSerialStream;
import javaclientlib.clientlib.TRVideoStream;
import javaclientlib.clientlib.Util;
import javaclientlib.common.RadiusPacket;
import javaclientlib.tr.Constants;
import javaclientlib.tr.RADIUS_ATTRIB_HEADER;
import javaclientlib.tr.RADIUS_ATTRIB_STRING;
import javaclientlib.tr.RADIUS_PACKET;
import javaclientlib.tr.TRBASECOMMAND;
import javaclientlib.tr.TRCMDWAIT;
import javaclientlib.tr.TRCMD_ACCESS_REQUEST_DATA;
import javaclientlib.tr.TRCMD_CHANGE_PASSWORD_DATA;
import javaclientlib.tr.TRCMD_CLIENT_CHALLENGE_DATA;
import javaclientlib.tr.TRCMD_CLIENT_RESPONSE_DATA;
import javaclientlib.tr.TRCMD_DATABASE_REQUEST_DATA;
import javaclientlib.tr.TRCMD_ENUM_VIDEO_DEVICES_DATA;
import javaclientlib.tr.TRCMD_HANDSHAKE_ACK_DATA;
import javaclientlib.tr.TRCMD_IDENTIFY_REMOTE_DATA;
import javaclientlib.tr.TRCMD_SET_DATA_ITEM_DATA;
import javaclientlib.tr.TRCOMMAND;
import javaclientlib.tr.TRLIB_COMM;
import javaclientlib.tr.TRLIB_REFERRAL_COMM;
import javaclientlib.tr.TRLIB_USERINFO;
import javaclientlib.tr.TRRSP_ACCESS_RESPONSE_DATA;
import javaclientlib.tr.TRRSP_DATABASE_RESPONSE_DATA;
import javaclientlib.tr.TRRSP_KB_STATUS_DATA;
import javaclientlib.tr.TRRSP_NACK_DATA;
import javaclientlib.tr.TRRSP_RDM_EVENT_DATA;
import javaclientlib.tr.TRRSP_SERVER_CHALLENGE_DATA;
import javaclientlib.tr.TRRSP_SERVER_RESPONSE_DATA;
import javaclientlib.tr.TRRSP_TARGET_PARAMS_DATA;
import javaclientlib.tr.TRRSP_VIDEO_MARKER_DATA;
import javaclientlib.tr.TRSRVR_CHANNEL_DATA;
import javaclientlib.tr.TRSRVR_SERVER_ID;
import javaclientlib.tr.TRSRVR_TARGET_PARAMS;
import javaclientlib.utils.MD5;
import javaclientlib.utils.Monitor;
import javaclientlib.utils.RC4Cipher;
import javaclientlib.utils.RRCLogger;
import javaclientlib.utils.RRCUtil;
import javaclientlib.utils.SecureSocket;
import javaclientlib.utils.XMLParser;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class TRConnection
extends Thread
implements ITRConnection {
    protected boolean updateActive = false;
    private int cnt = 0;
    private static XMLParser xmlParser;
    public long lConnectTime;
    private final int DO_NOTHING = 0;
    private final int DONT_READ = 1;
    private final int READ_EXPECTED = 2;
    private final int READ_AVAILABLE = 3;
    private final int READ_UPTO = 4;
    private final int TRLIB_ANY_RESPONSE = 0;
    private final int TR_MAX_VIDEO_STREAM = 256;
    private final int TR_MAX_SERIAL_STREAM = 32;
    private int responseTotalSize = 0;
    private TRCOMMAND objTRBaseCommand;
    private Vector objVSList;
    private Vector objSSList;
    private TRSerialStream objSSAdmin = null;
    private Hashtable objCmdWaitHashtable;
    private Monitor objSyncMonitor;
    private TRRSP_ACCESS_RESPONSE_DATA accessResponse = null;
    private TRKeepAliveThread objKeepAliveThrd;
    private TRLIB_COMM objComm = new TRLIB_COMM();
    public TRLIB_USERINFO objUserInfo;
    private TRLIB_REFERRAL_COMM objReferral = new TRLIB_REFERRAL_COMM();
    private TRSRVR_SERVER_ID objSvrID = null;
    private TRVideoStream[] objVSArray;
    private TRSerialStream[] objSSArray;
    private RSPDEF[] objRSPDEFList;
    private Object objCriticalSection;
    private Object objMutex;
    private Object objWriteCR;
    private Socket objReadSock = null;
    private Socket objWriteSock = null;
    private Socket objEventSock = null;
    private SecureSocket objEventSSLSocket = null;
    private DataInputStream objBufferedInputStream = null;
    private DataOutputStream objFilterOutputStream = null;
    private DataInputStream objBufferedInputEventStream = null;
    private FilterOutputStream objFilterOutputEventStream = null;
    private Socket objRFPSock = null;
    private SecureSocket objRFPSSLSocket = null;
    private DataInputStream objBufferedInputRFPStream = null;
    private FilterOutputStream objFilterOutputRFPStream = null;
    private SecureSocket objReadSSLSocket = null;
    private SecureSocket objWriteSSLSocket = new SecureSocket();
    private byte[] byRand1;
    private byte[] byRand2;
    private byte[] byRand3;
    private byte[] byRand4;
    private byte byChapID;
    private byte[] byChapData;
    private byte[] RC4Key;
    private byte byPktID = 1;
    private boolean boolSingleTCPPortProtocol = false;
    private boolean boolHaveLoginData = false;
    private boolean boolCommResolved = false;
    private boolean boolConnected = false;
    private boolean boolSocketOK;
    private boolean boolAuthenticated = false;
    private boolean boolPassWordChangeInprocess = false;
    private boolean boolDisconnecting = false;
    private boolean boolNeedSSL = false;
    private boolean boolNeedRC4 = false;
    private boolean boolOrderlyShutdown = false;
    private boolean boolObjectGood = false;
    private boolean boolIsAdministrator = false;
    private String sessionId = null;
    private String sessionKey = null;
    private long lTimeLastMsg;
    private long lLastSeed;
    private long lTimeLastMsgSent;
    private long lPermissions = 0L;
    private int iReadCount;
    protected int iDataIn;
    protected int iDataOut;
    private RC4Cipher readRC4;
    private RC4Cipher writeRC4;
    public static Vector objConnVectorList;
    private static int iLastError;
    private String productVersion;
    private String deviceType;
    private boolean isKx2Device = false;
    private boolean isKX101G2Device = false;
    private boolean isKX101G1Device = false;
    private static byte[] byNull;
    private int iConnected;
    private int rfpConnected;
    private static String EVENT;
    private static String RFP;
    private static final String GET_DEVICE_ID = "<Database><Get><Select>/System/Device</Select><Nodes> Device </Nodes><SubNodes> Name SerialNo @id</SubNodes></Get></Database>";
    private boolean pingDevice = true;
    private boolean gdMode = false;
    private boolean useTLS = false;

    public static String long2IPString(long l) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(l >> 24 & 0xFFL);
        stringBuffer.append('.');
        stringBuffer.append(l >> 16 & 0xFFL);
        stringBuffer.append('.');
        stringBuffer.append(l >> 8 & 0xFFL);
        stringBuffer.append('.');
        stringBuffer.append(l & 0xFFL);
        return stringBuffer.toString();
    }

    public static long bytes2LongIP(byte[] byArray) {
        long l = byArray[0] << 24;
        l += (long)(byArray[1] << 16 & 0xFF0000);
        l += (long)(byArray[2] << 8 & 0xFF00);
        return l += (long)(byArray[3] & 0xFF);
    }

    public TRConnection() {
        this.objVSArray = new TRVideoStream[256];
        this.objSSArray = new TRSerialStream[32];
        this.objVSList = new Vector();
        this.objSSList = new Vector();
        this.objCmdWaitHashtable = new Hashtable();
        this.objSyncMonitor = new Monitor();
        this.objCriticalSection = new Object();
        this.objWriteCR = new Object();
        this.lTimeLastMsg = System.currentTimeMillis();
        byte[] byArray = new byte[65535];
        this.objTRBaseCommand = new TRCOMMAND(byArray);
        this.byRand1 = new byte[64];
        this.byRand2 = new byte[64];
        this.byRand3 = new byte[64];
        this.byRand4 = new byte[64];
        this.iReadCount = 0;
        this.iDataIn = 0;
        this.iDataOut = 0;
        this.populateRSPDEF();
        this.boolObjectGood = true;
        objConnVectorList = new Vector();
        objConnVectorList.addElement(this);
    }

    protected void finalize() {
        if (this.boolObjectGood) {
            objConnVectorList.removeElement(this);
        }
        this.objVSArray = null;
        this.objSSArray = null;
        this.objCmdWaitHashtable = null;
        this.objSyncMonitor = null;
        this.objCriticalSection = null;
        this.objWriteCR = null;
        this.objComm = null;
        this.objReferral = null;
        this.objWriteSSLSocket = null;
        this.objVSList = null;
        this.objSSList = null;
        this.objSSAdmin = null;
        this.accessResponse = null;
        Object var1_1 = null;
        this.objTRBaseCommand = null;
        this.byRand1 = null;
        this.byRand2 = null;
        this.byRand3 = null;
        this.byRand4 = null;
        objConnVectorList = null;
    }

    private void populateRSPDEF() {
        this.objRSPDEFList = new RSPDEF[48];
        for (int i = 0; i < 48; ++i) {
            this.objRSPDEFList[i] = new RSPDEF();
        }
        this.objRSPDEFList[0].populateRSDEF("nop", 2, 4, null, null);
        this.objRSPDEFList[1].populateRSDEF("nop", 0, 0, null, null);
        this.objRSPDEFList[2].populateRSDEF("response", 2, 4, null, null);
        this.objRSPDEFList[3].populateRSDEF("nack", 2, 8, null, null);
        this.objRSPDEFList[43].populateRSDEF("TRRSP_Notify", 3, 4100, null, null);
        this.objRSPDEFList[5].populateRSDEF("pong", 2, 4, null, null);
        this.objRSPDEFList[6].populateRSDEF("response", 2, 78, null, null);
        this.objRSPDEFList[7].populateRSDEF("TRRSP_Server_Challenge", 2, 136, null, null);
        this.objRSPDEFList[28].populateRSDEF("TRRSP_Server_Response", 2, 41, null, null);
        this.objRSPDEFList[9].populateRSDEF("TRRSP_Access_Response", 3, 4100, null, null);
        this.objRSPDEFList[29].populateRSDEF("response", 2, 89, null, null);
        this.objRSPDEFList[47].populateRSDEF("targetParamsChanged", 2, 145, null, null);
        this.objRSPDEFList[11].populateRSDEF("response", 2, 46, null, null);
        this.objRSPDEFList[39].populateRSDEF("TRRSP_New_Video_Mode", 2, 65, null, null);
        this.objRSPDEFList[40].populateRSDEF("TRRSP_Cache", 3, 265, null, null);
        this.objRSPDEFList[41].populateRSDEF("TRRSP_Compressed", 4, 15, null, null);
        this.objRSPDEFList[45].populateRSDEF("TRRSP_Cell_Data", 4, 8, null, null);
        this.objRSPDEFList[42].populateRSDEF("TRRSP_Bitplane", 4, 14, null, null);
        this.objRSPDEFList[16].populateRSDEF("TRRSP_Packed", 4, 4108, null, null);
        this.objRSPDEFList[31].populateRSDEF("TRRSP_Video_Marker", 2, 7, null, null);
        this.objRSPDEFList[32].populateRSDEF("TRRSP_KB_Status", 3, 521, null, null);
        this.objRSPDEFList[19].populateRSDEF("response", 2, 33, null, null);
        this.objRSPDEFList[20].populateRSDEF("TRRSP_Receive_Serial", 4, 7, null, null);
        this.objRSPDEFList[21].populateRSDEF("response", 2, 0, null, null);
        this.objRSPDEFList[23].populateRSDEF("response", 2, 32, null, null);
        this.objRSPDEFList[33].populateRSDEF("response", 2, 12, null, null);
        this.objRSPDEFList[26].populateRSDEF("response", 2, 121, null, null);
        this.objRSPDEFList[35].populateRSDEF("externalResponse", 3, 4108, null, null);
        this.objRSPDEFList[36].populateRSDEF("responseWithAvailableData", 4, 18, null, null);
        this.objRSPDEFList[37].populateRSDEF("response", 2, 5, null, null);
        this.objRSPDEFList[38].populateRSDEF("response", 2, 5, null, null);
        this.objRSPDEFList[46].populateRSDEF("TRRSP_RDM_Event", 3, 4100, null, null);
    }

    public boolean connect(TRLIB_COMM tRLIB_COMM, TRLIB_USERINFO tRLIB_USERINFO, byte[] byArray, TRLIB_REFERRAL_COMM tRLIB_REFERRAL_COMM) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "objComm = " + tRLIB_COMM);
        }
        try {
            if (tRLIB_COMM == null) {
                TRConnection.setLastError(0x20000002);
                return false;
            }
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "objComm.getInetAddress() = " + tRLIB_COMM.getInetAddress());
            }
            if (tRLIB_COMM.getConnType() == 2 && tRLIB_COMM.getFindBy() == 0 && tRLIB_COMM.getInetAddress().isAnyLocalAddress()) {
                TRConnection.setLastError(0x20000002);
                return false;
            }
            if (tRLIB_USERINFO != null) {
                this.boolHaveLoginData = true;
                this.objUserInfo = tRLIB_USERINFO;
            }
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "objRefComm = " + tRLIB_REFERRAL_COMM);
            }
            if (tRLIB_REFERRAL_COMM != null) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "objRefComm.getVersion() = " + tRLIB_REFERRAL_COMM.getVersion());
                }
                if (tRLIB_REFERRAL_COMM.getVersion() != 1) {
                    TRConnection.setLastError(0x20000002);
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "objRefComm.getVersion() != 1..returning false");
                    }
                    return false;
                }
            }
            if (tRLIB_REFERRAL_COMM != null) {
                this.objReferral = tRLIB_REFERRAL_COMM;
            } else {
                this.objReferral.setVersion(0);
            }
            this.objComm = tRLIB_COMM;
            if (this.isGDMode()) {
                this.rspProc();
            } else if (!this.isAlive()) {
                this.start();
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "RConnection.connect returning true");
        }
        return true;
    }

    @Override
    public void run() {
        this.setName("RspProc Thread");
        boolean bl = this.rspProc();
        this.log("run: RspProc returned " + bl);
    }

    public void dbNotify(int n, int n2) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, " DBNOTIFY Notice " + n + " " + n2);
        }
        if (n != 1028) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 4, System.currentTimeMillis() + " Notice " + n + " " + n2);
            }
            this.notify(n, n2);
        }
    }

    private boolean rasDial() {
        throw new RuntimeException("RAS not Supported in This Version");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean serverHandshake() {
        boolean bl = false;
        Object var2_2 = null;
        int n = 0;
        boolean bl2 = false;
        try {
            Object object;
            TRCMD_HANDSHAKE_ACK_DATA tRCMD_HANDSHAKE_ACK_DATA = new TRCMD_HANDSHAKE_ACK_DATA();
            TRCMD_IDENTIFY_REMOTE_DATA tRCMD_IDENTIFY_REMOTE_DATA = new TRCMD_IDENTIFY_REMOTE_DATA();
            TRSRVR_SERVER_ID tRSRVR_SERVER_ID = new TRSRVR_SERVER_ID();
            InetAddress inetAddress = this.objComm.getInetAddress();
            byte[] byArray = new byte[]{0, 0, 0, 0};
            this.boolSocketOK = true;
            this.objReadSock.setTcpNoDelay(true);
            this.objReadSock.setSoTimeout(174000);
            this.objWriteSock = this.objReadSock;
            this.objSvrID = new TRSRVR_SERVER_ID();
            n = this.negotiateCSC(this.objSvrID);
            if (this.boolDisconnecting) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 4, "Serverhandshake:  negotiateCSC returned " + n);
                }
                return false;
            }
            if (n < 0) {
                this.dbNotify(1017, 122);
                return false;
            }
            if (n > 0) {
                bl2 = true;
            }
            if (this.boolDisconnecting) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 4, "Serverhandshake: DISCONNECTING 1 " + n);
                }
                return false;
            }
            if (!this.isKx2Device) {
                Object object2;
                if (this.objSvrID.getProtocolVersion() <= 11) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(200, 4, "Serverhandshake: ProtocolVersion Not Supported :: Protocol Version = 11");
                    }
                    return false;
                }
                this.boolSingleTCPPortProtocol = true;
                if (this.objSvrID.getProtocolVersion() < 11) {
                    this.dbNotify(1014, 15);
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(200, 4, "Serverhandshake: Server's Protocol Version < CSC Prot Version " + n);
                    }
                    return false;
                }
                if (this.objSvrID.getOldestProtocolVersion() > 30) {
                    this.dbNotify(1013, 16);
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(200, 4, "Server's Protocol Version < CSC Prot Version " + n);
                    }
                    return false;
                }
                switch (this.objSvrID.getSecurityFlags() & 0xF00) {
                    case 512: 
                    case 1024: 
                    case 2048: {
                        this.boolNeedSSL = true;
                        break;
                    }
                    case 256: {
                        this.boolNeedSSL = false;
                        break;
                    }
                    default: {
                        this.dbNotify(1016, 16);
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(200, 4, "Serverhandshake: SecurityFlags Not Set ");
                        }
                        return false;
                    }
                }
                if (this.boolNeedSSL) {
                    bl = this.initializeSSL(this.objComm.getInetAddress().getHostAddress(), true);
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "Serverhandshake:  initializeSSL returned " + bl);
                    }
                    if (!bl) {
                        this.dbNotify(1015, 18);
                    }
                    this.createBufferInputStream();
                }
                if (this.boolDisconnecting) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(200, 4, "Serverhandshake: DISCONNECTING 2" + n);
                    }
                    return false;
                }
                if (this.objReferral.getVersion() > 0) {
                    this.objWriteSock = this.objReadSock;
                    this.objWriteSSLSocket = this.objReadSSLSocket;
                    object = this.objFilterOutputStream;
                    this.createFilterOutputStream();
                    n = this.CSC_Test(this.objReferral.getSessionKey());
                    this.objFilterOutputStream = object;
                    this.objWriteSock = null;
                    this.objWriteSSLSocket = null;
                    this.createBufferInputStream();
                    if (n < 0) {
                        this.dbNotify(1017, 192);
                        return false;
                    }
                }
                bl = this.read(tRCMD_HANDSHAKE_ACK_DATA.toByteArray(), 0, 34);
                if (this.boolDisconnecting) {
                    return true;
                }
                if (!bl) {
                    this.dbNotify(1017, 20);
                    return false;
                }
                if (tRCMD_HANDSHAKE_ACK_DATA.getSignature() == 1547540642) {
                    if (tRCMD_HANDSHAKE_ACK_DATA.getCommand() != 43 || tRCMD_HANDSHAKE_ACK_DATA.getCmdLength() != 34) {
                        this.dbNotify(1016, 21);
                        return false;
                    }
                } else if (tRCMD_HANDSHAKE_ACK_DATA.getSignature() != -1958523193 || tRCMD_HANDSHAKE_ACK_DATA.getCommand() != 43 || tRCMD_HANDSHAKE_ACK_DATA.getCmdLength() != 34) {
                    this.dbNotify(1016, 21);
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(200, 4, "Serverhandshake: CONNECTION- BAD DATA 3");
                    }
                    return false;
                }
                if (this.boolSingleTCPPortProtocol) {
                    bl = this.svrHndshakeAftSocketCreation(tRCMD_HANDSHAKE_ACK_DATA, false);
                    if (this.boolDisconnecting) {
                        return false;
                    }
                    if (!bl) {
                        this.dbNotify(1017, 127);
                        return false;
                    }
                }
                object = this.objCriticalSection;
                synchronized (object) {
                    if (this.boolSingleTCPPortProtocol) {
                        if (this.objComm.getIpPort() == 0) {
                            this.objWriteSock = new Socket(inetAddress, Constants.TR_PORT_BASE);
                        } else {
                            this.objWriteSock = new Socket(inetAddress, this.objComm.getIpPort());
                            this.log("Serverhandshake: Socket Created");
                        }
                    } else if (this.objComm.getIpPort() == 0) {
                        if (!this.boolDisconnecting) {
                            this.objWriteSock = new Socket(inetAddress, Constants.TR_PORT_BASE + 1);
                        }
                    } else if (!this.boolDisconnecting) {
                        this.objWriteSock = new Socket(inetAddress, this.objComm.getIpPort() + 1);
                        this.log("Serverhandshake: Write Socket Created");
                    }
                    this.objWriteSock.setTcpNoDelay(true);
                    this.objWriteSock.setSoTimeout(174000);
                }
                if (this.boolSingleTCPPortProtocol) {
                    object = null;
                    object = this.objReadSock;
                    this.objReadSock = this.objWriteSock;
                    n = this.negotiateCSC(tRSRVR_SERVER_ID);
                    if (n < 0) {
                        this.dbNotify(1017, 233);
                        return false;
                    }
                    if (this.boolDisconnecting) {
                        return false;
                    }
                    this.objReadSock = object;
                    this.createBufferInputStream();
                }
                if (this.boolNeedSSL && !(bl = this.initializeSSL(this.objComm.getInetAddress().getHostAddress(), false))) {
                    this.dbNotify(1015, 25);
                    return false;
                }
                if (this.objReferral.getVersion() > 0) {
                    object = this.objReadSock;
                    object2 = this.objReadSSLSocket;
                    this.objReadSock = this.objWriteSock;
                    this.objReadSSLSocket = this.objWriteSSLSocket;
                    DataOutputStream dataOutputStream = this.objFilterOutputStream;
                    this.createFilterOutputStream();
                    DataInputStream dataInputStream = this.objBufferedInputStream;
                    this.createBufferInputStream();
                    n = this.CSC_Test(this.objReferral.getSessionKey());
                    this.objReadSock = object;
                    this.objReadSSLSocket = object2;
                    this.objBufferedInputStream = dataInputStream;
                    this.createFilterOutputStream();
                    if (n < 0) {
                        this.dbNotify(1017, 192);
                        return false;
                    }
                }
                if (this.boolSingleTCPPortProtocol) {
                    bl = this.svrHndshakeAftSocketCreation(tRCMD_HANDSHAKE_ACK_DATA, true);
                    if (this.boolDisconnecting) {
                        return false;
                    }
                    if (!bl) {
                        this.dbNotify(1017, 227);
                        return false;
                    }
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "Serverhandshake:  svrHndshakeAftSocketCreation returned " + bl);
                    }
                } else {
                    bl = this.write(tRCMD_HANDSHAKE_ACK_DATA.toByteArray(), 34);
                    if (!bl) {
                        this.dbNotify(1017, 327);
                        return false;
                    }
                }
                if (this.boolDisconnecting) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "Serverhandshake: DISCONNECTING 3 " + n);
                    }
                    return false;
                }
                bl = this.read(this.objTRBaseCommand.toByteArray(), 0, 4);
                if (!bl) {
                    this.dbNotify(1017, 31);
                    return false;
                }
                if (this.boolDisconnecting) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "Serverhandshake: DISCONNECTING 4 " + n);
                    }
                    return false;
                }
                if (this.objTRBaseCommand.getCommand() != 2 || this.objTRBaseCommand.getCmdLength() != 4) {
                    if (this.objTRBaseCommand.getCommand() == 3 && this.objTRBaseCommand.getCmdLength() == 8) {
                        int n2 = -1;
                        object2 = new TRRSP_NACK_DATA();
                        ((TRBASECOMMAND)object2).fromByteArray(this.objTRBaseCommand.toByteArray());
                        bl = this.read(((TRBASECOMMAND)object2).toByteArray(), 4, 4);
                        if (this.boolDisconnecting) {
                            if (RRCLogger.logEnabled) {
                                RRCLogger.log(200, 4, "Serverhandshake: DISCONNECTING 4 " + n);
                            }
                            return false;
                        }
                        if (!bl) {
                            this.dbNotify(1017, 28);
                            return false;
                        }
                        if (this.objTRBaseCommand.getInt(4) == 11) {
                            this.dbNotify(1012, 29);
                            if (RRCLogger.logEnabled) {
                                RRCLogger.log(300, 4, "SERVER BUSY");
                            }
                            this.boolOrderlyShutdown = true;
                        }
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(200, 4, "Serverhandshake: this.objTRBaseCommand.getCommand() == TRRSP.NACK)");
                        }
                        return false;
                    }
                    this.dbNotify(1016, 30);
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(200, 4, "Serverhandshake: NOTIFY BAD DATA - What???");
                    }
                    return false;
                }
                this.log("CONNECTION: Everything is OK");
                if (this.boolSingleTCPPortProtocol) {
                    tRCMD_IDENTIFY_REMOTE_DATA.setProtocolVersion(30);
                    tRCMD_IDENTIFY_REMOTE_DATA.setOSType(0);
                    tRCMD_IDENTIFY_REMOTE_DATA.setFlags(0);
                    tRCMD_IDENTIFY_REMOTE_DATA.setExtraData(0);
                    tRCMD_IDENTIFY_REMOTE_DATA.setReserved(0, 0);
                    tRCMD_IDENTIFY_REMOTE_DATA.setReserved(0, 1);
                    tRCMD_IDENTIFY_REMOTE_DATA.setReserved(0, 2);
                    tRCMD_IDENTIFY_REMOTE_DATA.setReserved(0, 3);
                    tRCMD_IDENTIFY_REMOTE_DATA.setCommand((byte)42);
                    tRCMD_IDENTIFY_REMOTE_DATA.setCmdLength((short)36);
                    tRCMD_IDENTIFY_REMOTE_DATA.setPktID((byte)0);
                    bl = this.sendTRCmd(tRCMD_IDENTIFY_REMOTE_DATA);
                    if (!bl) {
                        this.dbNotify(1017, 32);
                    }
                }
            }
            object = this.objCriticalSection;
            synchronized (object) {
                if (!this.boolDisconnecting) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "CONNECTION Successful");
                    }
                    this.boolConnected = true;
                }
            }
            return this.boolConnected;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void prepareSocket() throws IOException {
        InetAddress inetAddress = this.objComm.getInetAddress();
        Object object = this.objCriticalSection;
        synchronized (object) {
            if (!this.boolDisconnecting) {
                if (this.objComm.getIpPort() == 0) {
                    this.objReadSock = new Socket(inetAddress, Constants.TR_PORT_BASE + 0);
                    this.log("Serverhandshake Read & Event Socket Created 1");
                } else {
                    this.objReadSock = new Socket(inetAddress, this.objComm.getIpPort() + 0);
                    this.log("Serverhandshake ReadSock: " + this.objReadSock);
                }
            }
        }
    }

    public boolean closeRFPSocketConnection() {
        if (this.isKX2Device()) {
            try {
                if (this.objRFPSock != null) {
                    this.objRFPSock.close();
                    this.objRFPSock = null;
                }
                if (this.objRFPSSLSocket != null) {
                    this.objRFPSSLSocket = null;
                }
            }
            catch (IOException iOException) {
                this.objRFPSock = null;
                if (RRCLogger.logEnabled) {
                    RRCLogger.logException(iOException);
                }
                return false;
            }
        }
        return true;
    }

    public boolean createRFPSocketConnection() {
        block6: {
            if (this.isKX2Device()) {
                try {
                    InetAddress inetAddress = null;
                    byte[] byArray = new byte[]{0, 0, 0, 0};
                    inetAddress = this.objComm.getInetAddress();
                    int n = 0;
                    n = this.objComm.getIpPort() == 0 ? Constants.TR_PORT_BASE : this.objComm.getIpPort();
                    int n2 = this.connect(inetAddress, n, RFP);
                    if (n2 < 0) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(200, 4, "sendRFPTRCmd:  not able to connect to RFP socket " + n2);
                        }
                        return false;
                    }
                    byte[] byArray2 = this.GetCSCInfo(RFP);
                    int n3 = 0;
                    n3 = this.getRdmSessionID() == null || this.getRdmSessionID().length() == 0 ? this.startNewCSCSession("RFP", inetAddress, n, RFP) : this.startReferralCSCSession("RFP", this.getRdmSessionID(), this.getRdmSessionKey(), RFP);
                    if (n3 < 0) {
                        return false;
                    }
                }
                catch (Exception exception) {
                    if (!RRCLogger.logEnabled) break block6;
                    RRCLogger.logException(exception);
                }
            }
        }
        return true;
    }

    public void readRFPResponseMessage() {
        block17: {
            try {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "Reading RFP Message");
                }
                while (this.boolConnected && this.objRFPSock != null) {
                    boolean bl;
                    int n;
                    RSPDEF rSPDEF = null;
                    int n2 = this.readFromStream(this.objTRBaseCommand.toByteArray(), 0, 4, RFP);
                    this.iReadCount = 4;
                    if (n2 < 0) {
                        if (!this.boolDisconnecting && !this.boolOrderlyShutdown && RRCLogger.logEnabled) {
                            RRCLogger.log(100, 4, "Failed to read RFP Message");
                        }
                        break;
                    }
                    this.dbLogCmd(true, this.objTRBaseCommand);
                    try {
                        n = this.objTRBaseCommand.getCommand();
                        rSPDEF = n >= 0 && n < this.objRSPDEFList.length ? this.objRSPDEFList[n] : null;
                    }
                    catch (Exception exception) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.logException(exception);
                        }
                        rSPDEF = null;
                    }
                    if (rSPDEF == null) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(100, 4, "Connection:RFP Command received does not have a Valid handler Command Received = " + this.objTRBaseCommand.getCommand());
                        }
                        this.dbNotify(1016, 8);
                        this.boolOrderlyShutdown = false;
                        break;
                    }
                    this.lTimeLastMsg = System.currentTimeMillis();
                    n = this.switchRFPFunction(rSPDEF);
                    if (n == 0) {
                        this.log("RFPswitchFunction :entered " + rSPDEF.getWhatToDo() + " AFTER SWITCH FUNCTION iResult=" + n);
                        n2 = -1;
                        if (!this.boolDisconnecting) {
                            this.dbNotify(1017, 10);
                        }
                        break;
                    }
                    if (n == 1) {
                        n2 = 0;
                    } else if (n == 2) continue;
                    boolean bl2 = false;
                    Method method = null;
                    rSPDEF = this.objRSPDEFList[this.objTRBaseCommand.getCommand()];
                    Class<?>[] classArray = null;
                    Class<?> clazz = Class.forName("javaclientlib.clientlib.TRConnection");
                    method = clazz.getDeclaredMethod(rSPDEF.getMethodName(), classArray);
                    if (method == null) {
                        this.dbNotify(1016, 9);
                        break;
                    }
                    Object[] objectArray = null;
                    Object object = method.invoke((Object)this, objectArray);
                    bl2 = (Boolean)object;
                    this.log("RFP Invoked Response Handler returned " + bl2 + " for method " + method);
                    if (bl2 || this.iReadCount >= this.objTRBaseCommand.getCmdLength() || (bl = this.sinkRFPData(this.objTRBaseCommand.getCmdLength() - this.iReadCount))) continue;
                    n2 = -1;
                }
            }
            catch (Exception exception) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.logException(exception);
                }
                if (!RRCLogger.logEnabled) break block17;
                RRCLogger.log(100, 4, "FAILED TO RECIEVE RFP MESSAGE");
            }
        }
    }

    public boolean sendRFPTRCmd(TRCOMMAND tRCOMMAND) {
        block5: {
            if (this.isKx2Device) {
                try {
                    int n;
                    tRCOMMAND.setPktID((byte)0);
                    if (RRCLogger.shouldLog(300, 4)) {
                        this.dbLogCmd(false, tRCOMMAND);
                    }
                    if ((n = this.writeToStream(tRCOMMAND.toByteArray(), tRCOMMAND.getCmdLength(), RFP)) == -20) {
                        return false;
                    }
                    break block5;
                }
                catch (Exception exception) {
                    RRCLogger.logException(exception);
                    return false;
                }
            }
            return this.sendTRCmd(tRCOMMAND);
        }
        return true;
    }

    public boolean sendTRCmd(TRCOMMAND tRCOMMAND) {
        try {
            tRCOMMAND.setPktID((byte)0);
            if (RRCLogger.shouldLog(300, 4)) {
                this.dbLogCmd(false, tRCOMMAND);
            }
            this.write(tRCOMMAND.toByteArray(), tRCOMMAND.getCmdLength());
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
        return true;
    }

    public boolean sendTRCmdEx(TRCOMMAND tRCOMMAND, boolean bl, Monitor monitor, int n) {
        return this.sendTRCmdExx(tRCOMMAND, bl, monitor, n, false, 0);
    }

    public boolean sendTRCmdEx(TRCOMMAND tRCOMMAND, boolean bl, Monitor monitor, int n, int n2) {
        return this.sendTRCmdExx(tRCOMMAND, bl, monitor, n, false, n2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean sendTRCmdExx(TRCOMMAND tRCOMMAND, boolean bl, Monitor monitor, int n, boolean bl2, int n2) {
        boolean bl3 = false;
        try {
            boolean bl4 = false;
            TRCMDWAIT tRCMDWAIT = null;
            Monitor monitor2 = null;
            if (!this.boolConnected) {
                TRConnection.setLastError(0x20000001);
                return false;
            }
            if (bl) {
                if (tRCOMMAND.toByteArray().length < 4) {
                    return false;
                }
                tRCMDWAIT = new TRCMDWAIT();
                tRCMDWAIT.setResponse(tRCOMMAND);
                tRCMDWAIT.setEvent(monitor);
                tRCMDWAIT.setStartTime(System.currentTimeMillis());
                tRCMDWAIT.setExpRespCommand(n);
                tRCMDWAIT.setSync(bl2);
                tRCMDWAIT.setPktID(this.byPktID);
                if (monitor == null) {
                    monitor2 = new Monitor();
                    tRCMDWAIT.setEvent(monitor2);
                }
                Object object = this.objCriticalSection;
                synchronized (object) {
                    bl4 = true;
                    tRCOMMAND.setPktID(this.byPktID);
                    this.objCmdWaitHashtable.put(this.byPktID + "", tRCMDWAIT);
                    this.byPktID = (byte)(this.byPktID + 1);
                    if (this.byPktID == 0) {
                        this.byPktID = (byte)(this.byPktID + 1);
                    }
                }
            } else {
                tRCOMMAND.setPktID((byte)0);
            }
            if (RRCLogger.shouldLog(300, 4)) {
                this.dbLogCmd(false, tRCOMMAND);
            }
            int n3 = tRCOMMAND.getCmdLength();
            this.log("b4:write");
            bl3 = this.write(tRCOMMAND.toByteArray(), n3);
            this.log("aft:write");
            if (!bl3) {
                TRConnection.setLastError(0x20000005);
                return false;
            }
            if (monitor == null) {
                this.log("before:wait" + monitor2);
                if (monitor2.waiting(n2)) {
                    this.log("expired:wait");
                    this.cancelIO(tRCMDWAIT);
                    TRConnection.setLastError(0x2000000B);
                    return false;
                }
                this.log("after:wait");
            }
            return true;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean sendTRCmdExxx(TRCOMMAND tRCOMMAND, TRCMDWAIT tRCMDWAIT, int n, boolean bl, int n2) {
        boolean bl2 = false;
        try {
            boolean bl3 = false;
            Monitor monitor = null;
            if (tRCMDWAIT != null) {
                monitor = tRCMDWAIT.getEvent();
            }
            if (!this.boolConnected) {
                TRConnection.setLastError(0x20000001);
                return false;
            }
            if (tRCOMMAND.toByteArray().length < 4) {
                return false;
            }
            if (tRCMDWAIT == null) {
                tRCMDWAIT = new TRCMDWAIT();
            }
            tRCMDWAIT.setResponse(tRCOMMAND);
            tRCMDWAIT.setStartTime(System.currentTimeMillis());
            tRCMDWAIT.setExpRespCommand(n);
            tRCMDWAIT.setSync(bl);
            tRCMDWAIT.setPktID(this.byPktID);
            if (monitor == null) {
                monitor = new Monitor();
                tRCMDWAIT.setEvent(monitor);
            }
            Object object = this.objCriticalSection;
            synchronized (object) {
                bl3 = true;
                tRCOMMAND.setPktID(this.byPktID);
                this.objCmdWaitHashtable.put(this.byPktID + "", tRCMDWAIT);
                this.byPktID = (byte)(this.byPktID + 1);
                if (this.byPktID == 0) {
                    this.byPktID = (byte)(this.byPktID + 1);
                }
            }
            if (RRCLogger.shouldLog(300, 4)) {
                this.dbLogCmd(false, tRCOMMAND);
            }
            int n3 = tRCOMMAND.getCmdLength();
            this.log("b4:write");
            bl2 = this.write(tRCOMMAND.toByteArray(), n3);
            this.log("aft:write");
            if (!bl2) {
                return false;
            }
            this.log("before:wait" + monitor);
            if (monitor.waiting(n2)) {
                this.log("expired:wait");
                this.cancelIO(tRCMDWAIT);
                TRConnection.setLastError(0x20000005);
                return false;
            }
            this.log("after:wait");
            return true;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean response() throws Exception {
        try {
            TRCMDWAIT tRCMDWAIT;
            Object object = this.objCriticalSection;
            synchronized (object) {
                tRCMDWAIT = (TRCMDWAIT)this.objCmdWaitHashtable.get(this.objTRBaseCommand.getPktID() + "");
                if (tRCMDWAIT != null) {
                    this.objCmdWaitHashtable.remove(this.objTRBaseCommand.getPktID() + "");
                }
            }
            object = new TRRSP_NACK_DATA(this.objTRBaseCommand.toByteArray());
            if (tRCMDWAIT != null) {
                if (this.objTRBaseCommand.getCommand() == 3) {
                    ((TRRSP_NACK_DATA)object).setError(((TRRSP_NACK_DATA)object).getError() + 0x20001000);
                } else if (tRCMDWAIT.getExpRespCommand() != 0 && this.objTRBaseCommand.getCommand() != tRCMDWAIT.getExpRespCommand()) {
                    ((TRCOMMAND)object).setCommand((byte)3);
                    ((TRCOMMAND)object).setCmdLength((short)8);
                    ((TRRSP_NACK_DATA)object).setError(0x20000004);
                }
                if (this.iReadCount > 4108) {
                    ((TRCOMMAND)object).setCommand((byte)3);
                    ((TRCOMMAND)object).setCmdLength((short)8);
                    ((TRRSP_NACK_DATA)object).setError(0x20000003);
                }
                System.arraycopy(this.objTRBaseCommand.toByteArray(), 0, tRCMDWAIT.getResponse().toByteArray(), 0, this.iReadCount);
                if (tRCMDWAIT.getEvent() != null) {
                    this.log("b4Notify - Response()" + tRCMDWAIT.getEvent());
                    tRCMDWAIT.getEvent().notifying();
                    this.log("After Notify - Response()" + tRCMDWAIT.getEvent());
                }
                if (this.objTRBaseCommand.getCommand() == 2 && this.boolPassWordChangeInprocess) {
                    this.boolPassWordChangeInprocess = false;
                    this.sendAccessRequest(false);
                }
                if (tRCMDWAIT.getSync()) {
                    this.objSyncMonitor.waiting(0);
                }
            }
            return tRCMDWAIT != null;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            throw exception;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean responseWithAvailableData() throws Exception {
        try {
            TRCMDWAIT tRCMDWAIT;
            Object object = this.objCriticalSection;
            synchronized (object) {
                tRCMDWAIT = (TRCMDWAIT)this.objCmdWaitHashtable.get(this.objTRBaseCommand.getPktID() + "");
                if (tRCMDWAIT != null) {
                    this.objCmdWaitHashtable.remove(this.objTRBaseCommand.getPktID() + "");
                }
            }
            object = new TRRSP_NACK_DATA(this.objTRBaseCommand.toByteArray());
            if (tRCMDWAIT != null) {
                if (this.objTRBaseCommand.getCommand() == 3) {
                    ((TRRSP_NACK_DATA)object).setError(((TRRSP_NACK_DATA)object).getError() + 0x20001000);
                } else if (tRCMDWAIT.getExpRespCommand() != 0 && this.objTRBaseCommand.getCommand() != tRCMDWAIT.getExpRespCommand()) {
                    ((TRCOMMAND)object).setCommand((byte)3);
                    ((TRCOMMAND)object).setCmdLength((short)8);
                    ((TRRSP_NACK_DATA)object).setError(0x20000004);
                }
                if (this.iReadCount > 4108) {
                    ((TRCOMMAND)object).setCommand((byte)3);
                    ((TRCOMMAND)object).setCmdLength((short)8);
                    ((TRRSP_NACK_DATA)object).setError(0x20000003);
                }
                System.arraycopy(this.objTRBaseCommand.toByteArray(), 0, tRCMDWAIT.getResponse().toByteArray(), 0, this.iReadCount);
                if (this.responseTotalSize > 0) {
                    byte[] byArray = new byte[this.responseTotalSize];
                    this.read(byArray, 0, this.responseTotalSize);
                    this.iReadCount += this.responseTotalSize;
                    tRCMDWAIT.setAvailableData(byArray);
                } else {
                    tRCMDWAIT.setAvailableData(null);
                }
                if (tRCMDWAIT.getEvent() != null) {
                    this.log("b4Notify - Response()" + tRCMDWAIT.getEvent());
                    tRCMDWAIT.getEvent().notifying();
                    this.log("After Notify - Response()" + tRCMDWAIT.getEvent());
                }
                if (this.objTRBaseCommand.getCommand() == 2 && this.boolPassWordChangeInprocess) {
                    this.boolPassWordChangeInprocess = false;
                    this.sendAccessRequest(false);
                }
                if (tRCMDWAIT.getSync()) {
                    this.objSyncMonitor.waiting(0);
                }
            } else {
                this.sinkData(this.objTRBaseCommand.getCmdLength() - this.iReadCount);
                this.iReadCount = this.objTRBaseCommand.getCmdLength();
            }
            return tRCMDWAIT != null;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            throw exception;
        }
    }

    private int negotiateCSC(TRSRVR_SERVER_ID tRSRVR_SERVER_ID) throws Exception {
        boolean bl = this.boolNeedSSL;
        this.boolNeedSSL = false;
        this.createBufferInputStream();
        this.createFilterOutputStream();
        byte[] byArray = new byte[74];
        String string = null;
        byte[] byArray2 = null;
        int n = 0;
        boolean bl2 = false;
        if (this.objReferral.getConnectionID()[0] != 0 && !(bl2 = this.writeCSCMessage((string = "<CSC_Connect ConnectionID=\"" + new String(this.objReferral.getConnectionID()) + "\"/>").getBytes(), 0))) {
            this.boolNeedSSL = bl;
            return -1;
        }
        bl2 = this.read(byArray, 0, 4);
        if (!bl2) {
            this.boolNeedSSL = bl;
            return -1;
        }
        int n2 = ((byArray[3] & 0xFF) << 24) + ((byArray[2] & 0xFF) << 16) + ((byArray[1] & 0xFF) << 8) + ((byArray[0] & 0xFF) << 0);
        n = ((byArray[0] & 0xFF) << 24) + ((byArray[1] & 0xFF) << 16) + ((byArray[2] & 0xFF) << 8) + ((byArray[3] & 0xFF) << 0);
        if (n == 74 || n2 == 74) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "CSC Negotiation: found an older, non-CSC server.");
            }
            if (!(bl2 = this.read(byArray, 4, 70))) {
                this.boolNeedSSL = bl;
                return -1;
            }
            return 0;
        }
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "CSC Negotiation: Talking to a CSC-compliant server.");
        }
        if ((n -= 4) > 2000) {
            return -1;
        }
        byte[] byArray3 = new byte[n + 1];
        bl2 = this.read(byArray3, 0, n);
        if (RRCLogger.logEnabled) {
            RRCLogger.log(400, 4, "CSC Negotiation: Searching for <CSC/>, got: " + new String(byArray3));
        }
        if (!bl2) {
            this.boolNeedSSL = bl;
            return -1;
        }
        bl2 = this.writeCSCMessage("<CSC_Ack/>".getBytes(), 0);
        if (RRCLogger.logEnabled) {
            RRCLogger.log(400, 4, "CSC Negotiation: Writing <CSC_Ack/>.");
        }
        byArray2 = this.readCSCMessage(4096, 0);
        if (RRCLogger.logEnabled) {
            RRCLogger.log(400, 4, "CSC Negotiation: Received for CSC_Info: " + new String(byArray2));
        }
        if (byArray2 == null) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(100, 4, "CSC Negotiation: Received NULL when expecting CSC_Info element.");
            }
            this.boolNeedSSL = bl;
            return -1;
        }
        this.isKx2Device = this.checkForKX2Device(new String(byArray2).substring(0, byArray2.length - 1));
        byArray2 = null;
        string = this.objReferral.getVersion() > 0 ? "<CSC_Start_Session ProtocolID=\"" + (this.isKx2Device ? "RDM" : "IP-Reach") + "\" SessionID=\"" + new String(this.objReferral.getSessionID()) + "\"/>" : "<CSC_Start_Session ProtocolID=\"" + (this.isKx2Device ? "RDM" : "IP-Reach") + "\"/>";
        if (RRCLogger.logEnabled) {
            RRCLogger.log(400, 4, "CSC Negotiation: Writing CSC_Session: " + string);
        }
        if (!(bl2 = this.writeCSCMessage(string.getBytes(), 0))) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(100, 4, "CSC Negotiation: Failure writing request for CSC_Session.");
            }
            this.boolNeedSSL = bl;
            return -1;
        }
        if (!this.isKx2Device) {
            bl2 = this.read(byArray, 0, 74);
            tRSRVR_SERVER_ID.populate(byArray);
            if (!bl2) {
                this.boolNeedSSL = bl;
                return -1;
            }
        }
        this.boolNeedSSL = bl;
        return 1;
    }

    public int startNewCSCSession(String string, InetAddress inetAddress, int n, String string2) {
        int n2;
        block5: {
            String string3 = new String("<CSC_Start_Session ProtocolID=\"" + string + "\"/>");
            int n3 = string3.length() + 1 + 4;
            n2 = this.writeToStream(RRCUtil.getBytesForInt(n3), 4, string2);
            n2 = this.writeToStream(string3.getBytes(), string3.length(), string2);
            n2 = this.writeToStream(byNull, 1, string2);
            if (n2 < 0) {
                return n2;
            }
            try {
                if (EVENT.equals(string2)) {
                    this.objEventSSLSocket = new SecureSocket(this.objEventSock, this.objComm.getInetAddress().getHostAddress(), n, this.getUseTLS());
                    this.objEventSSLSocket.connect();
                }
                if (RFP.equals(string2)) {
                    this.objRFPSSLSocket = new SecureSocket(this.objRFPSock, this.objComm.getInetAddress().getHostAddress(), n, this.getUseTLS());
                    this.objRFPSSLSocket.connect();
                }
                boolean bl = this.boolNeedSSL;
                this.boolNeedSSL = true;
                this.createBufferInputTypeStream(string2);
                this.createFilterOutputTypeStream(string2);
                n2 = this.doCSCAuthentication(string2);
                this.boolNeedSSL = bl;
            }
            catch (Exception exception) {
                n2 = -1;
                if (!RRCLogger.logEnabled) break block5;
                RRCLogger.logException(exception);
            }
        }
        return n2;
    }

    private int doCSCAuthentication(String string) {
        String string2 = Util.escapeXML(new String(this.objUserInfo.getPassword()));
        String string3 = "<CSC_Auth UserName=\"" + new String(this.objUserInfo.getName()) + "\" Password=\"" + string2 + "\"/>";
        int n = this.writeMessage(string3.getBytes(), string);
        byte[] byArray = this.readMessage(256, string);
        String string4 = new String(byArray).substring(0, byArray.length - 1);
        if (string4.equals("<CSC_Pass/>")) {
            return 0;
        }
        return -1;
    }

    public int connect(InetAddress inetAddress, int n, String string) {
        try {
            if (n == 0) {
                n = Constants.TR_PORT_BASE;
            }
            if (string.equals(EVENT)) {
                this.iConnected = 0;
                this.objEventSock = new Socket(inetAddress, n);
                if (this.objEventSock == null) {
                    return -2;
                }
                this.objEventSock.setTcpNoDelay(true);
            }
            if (string.equals(RFP)) {
                this.rfpConnected = 0;
                this.objRFPSock = new Socket(inetAddress, n);
                if (this.objRFPSock == null) {
                    return -2;
                }
                this.objRFPSock.setTcpNoDelay(true);
            }
            this.boolNeedSSL = false;
            this.createBufferInputTypeStream(string);
            this.createFilterOutputTypeStream(string);
            if (string.equals(EVENT)) {
                this.iConnected = 1;
            } else if (string.equals(RFP)) {
                this.rfpConnected = 1;
            }
            if (this.objReferral.getConnectionID()[0] != 0) {
                String string2 = new String(this.objReferral.getConnectionID());
                byte[] byArray = new byte[]{0};
                String string3 = "<CSC_Connect ConnectionID=\"" + string2 + "\"/>";
                int n2 = 4 + string3.getBytes().length + 1;
                this.writeToStream(RRCUtil.getBytesForInt(n2), 4, string);
                this.writeToStream(string3.getBytes(), string3.getBytes().length, string);
                this.writeToStream(byArray, 1, string);
            }
            return 0;
        }
        catch (Exception exception) {
            if (RRCLogger.logEnabled) {
                RRCLogger.logException(exception);
            }
            return -2;
        }
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

    public byte[] GetCSCInfo(String string) {
        String string2 = new String("<CSC_Ack/>");
        byte[] byArray = new byte[200];
        byte[] byArray2 = new byte[4];
        boolean bl = false;
        int n = this.readFromStream(byArray, 0, 4, string);
        if (n < 0) {
            return null;
        }
        int n2 = this.getIntForBytes(byArray, 0);
        if (n2 > 200) {
            return null;
        }
        n = this.readFromStream(byArray, 0, n2, string);
        n2 = string2.length() + 4 + 1;
        n = this.writeToStream(RRCUtil.getBytesForInt(n2), 4, string);
        n = this.writeToStream(string2.getBytes(), string2.length(), string);
        n = this.writeToStream(byNull, 1, string);
        if (n < 0) {
            return null;
        }
        n = this.readFromStream(byArray2, 0, 4, string);
        if (n < 0) {
            return null;
        }
        n2 = this.getIntForBytes(byArray2, 0);
        byArray2 = new byte[n2 -= 4];
        if (byArray2 == null) {
            return null;
        }
        n = this.readFromStream(byArray2, 0, n2, string);
        if (n < 0) {
            return null;
        }
        return byArray2;
    }

    public int writeMessage(byte[] byArray, String string) {
        int n = 0;
        int n2 = 4 + byArray.length + 1;
        n = this.writeToStream(RRCUtil.getBytesForInt(n2), 4, string);
        if (n < 0) {
            return n;
        }
        n = this.writeToStream(byArray, n2 - 4 - 1, string);
        if (n < 0) {
            return n;
        }
        n = this.writeToStream(byNull, 1, string);
        if (n < 0) {
            return n;
        }
        return n;
    }

    public byte[] readMessage(int n, String string) {
        byte[] byArray = new byte[4];
        int n2 = this.readFromStream(byArray, 0, 4, string);
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
        n2 = this.readFromStream(byArray2, 0, n3 - 4, string);
        if (n2 < 0) {
            return null;
        }
        return byArray2;
    }

    public int writeToStream(byte[] byArray, int n, String string) {
        int n2;
        block11: {
            n2 = 0;
            try {
                if (string.equals(EVENT)) {
                    this.objFilterOutputEventStream.write(byArray, 0, n);
                    this.objFilterOutputEventStream.flush();
                }
                if (string.equals(RFP)) {
                    this.objFilterOutputRFPStream.write(byArray, 0, n);
                    this.objFilterOutputRFPStream.flush();
                }
            }
            catch (Exception exception) {
                this.boolConnected = false;
                this.boolSocketOK = false;
                if (RRCLogger.logEnabled) {
                    RRCLogger.logException(exception);
                }
                if (string.equals(EVENT)) {
                    this.iConnected = 0;
                } else if (string.equals(RFP)) {
                    this.rfpConnected = 0;
                }
                n2 = -20;
            }
            try {
                if (n2 == -20) {
                    RRCLogger.log(300, 4, "Calling rspProcExitFunction, iOk: " + n2);
                    this.rspProcExitFunction();
                }
            }
            catch (Exception exception) {
                if (!RRCLogger.logEnabled) break block11;
                RRCLogger.logException(exception);
            }
        }
        return n2;
    }

    public int readFromStream(byte[] byArray, int n, int n2, String string) {
        int n3;
        int n4;
        boolean bl;
        block11: {
            bl = true;
            n4 = 0;
            n3 = -1;
            try {
                if (string.equals(EVENT)) {
                    if (this.iConnected != 0) {
                        n3 = this.objBufferedInputEventStream.read(byArray, n, n2);
                    } else {
                        return -10;
                    }
                }
                if (!string.equals(RFP)) break block11;
                if (this.rfpConnected != 0) {
                    n3 = this.objBufferedInputRFPStream.read(byArray, n, n2);
                    break block11;
                }
                return -10;
            }
            catch (Exception exception) {
                RRCLogger.log(300, -1, "byData : " + byArray + " byDataLen : " + byArray.length + " iOffset : " + n + " iCount : " + n2);
                if (RRCLogger.logEnabled) {
                    RRCLogger.logException(exception);
                }
                bl = false;
            }
        }
        if (n3 < 0 || !bl) {
            if (string.equals(EVENT)) {
                this.iConnected = 0;
            } else if (string.equals(RFP)) {
                this.rfpConnected = 0;
            }
            n4 = -20;
        }
        return n4;
    }

    private int getIntForBytes(byte[] byArray, int n) {
        return ((byArray[n + 0] & 0xFF) << 24) + ((byArray[n + 1] & 0xFF) << 16) + ((byArray[n + 2] & 0xFF) << 8) + ((byArray[n + 3] & 0xFF) << 0);
    }

    public int startReferralCSCSession(String string, String string2, String string3, String string4) {
        int n;
        block4: {
            String string5 = null;
            string5 = string2 != null ? new String("<CSC_Start_Session ProtocolID=\"" + string + "\" SessionID=\"" + string2 + "\"/>") : new String("<CSC_Start_Session ProtocolID=\"" + string + "\"/>");
            n = 0;
            int n2 = string5.length() + 1 + 4;
            this.writeToStream(RRCUtil.getBytesForInt(n2), 4, string4);
            this.writeToStream(string5.getBytes(), n2 - 5, string4);
            this.writeToStream(byNull, 1, string4);
            try {
                if (string4.equals(EVENT)) {
                    this.objEventSSLSocket = this.objComm.getIpPort() == 0 ? new SecureSocket(this.objEventSock, this.objComm.getInetAddress().getHostAddress(), Constants.TR_PORT_BASE, this.getUseTLS()) : new SecureSocket(this.objEventSock, this.objComm.getInetAddress().getHostAddress(), this.objComm.getIpPort(), this.getUseTLS());
                    this.objEventSSLSocket.connect();
                }
                if (string4.equals(RFP)) {
                    this.objRFPSSLSocket = this.objComm.getIpPort() == 0 ? new SecureSocket(this.objRFPSock, this.objComm.getInetAddress().getHostAddress(), Constants.TR_PORT_BASE, this.getUseTLS()) : new SecureSocket(this.objRFPSock, this.objComm.getInetAddress().getHostAddress(), this.objComm.getIpPort(), this.getUseTLS());
                    this.objRFPSSLSocket.connect();
                }
                boolean bl = this.boolNeedSSL;
                this.boolNeedSSL = true;
                this.createBufferInputTypeStream(string4);
                this.createFilterOutputTypeStream(string4);
                n = this.authReferralCSCSession(string3.getBytes(), string4);
                this.boolNeedSSL = bl;
            }
            catch (Exception exception) {
                exception.printStackTrace();
                n = -1;
                if (!RRCLogger.logEnabled) break block4;
                RRCLogger.log(100, 4, "startReferralCSCSession: Failure Creating Sockets.");
                RRCLogger.logException(exception);
            }
        }
        return n;
    }

    public boolean isAdminUser() {
        return this.boolIsAdministrator;
    }

    public boolean isKX2Device() {
        return this.isKx2Device;
    }

    public boolean hasGNULicenseInfo() {
        return this.isKx2Device && (this.deviceType.equals("Dominion_KX2") || this.deviceType.equals("Dominion_KSX2") || this.deviceType.equals("Dominion_KX2_101") || this.deviceType.equals("Dominion_LX"));
    }

    private int authReferralCSCSession(byte[] byArray, String string) {
        try {
            int n;
            byte[] byArray2 = null;
            byte[] byArray3 = null;
            byte[] byArray4 = null;
            byte[] byArray5 = null;
            byte[] byArray6 = null;
            Object var10_8 = null;
            String string2 = "1234567890";
            byte[] byArray7 = null;
            byte[] byArray8 = null;
            byte[] byArray9 = null;
            byArray6 = new BASE64Decoder().decodeBuffer(new String(byArray));
            byArray7 = this.readMessage(0, string);
            String string3 = new String(byArray7);
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
            byte[] byArray10 = string2.getBytes();
            byArray3 = new byte[byArray10.length];
            for (n = 0; n < byArray10.length; ++n) {
                byArray3[n] = (byte)(byArray10[n] ^ (char)n2);
                n2 >>= 3;
                n2 = (int)((long)n2 ^ System.currentTimeMillis());
            }
            byArray5 = new BASE64Encoder().encode(byArray3).getBytes();
            n2 = byArray5.length;
            String string4 = "<CSC_Test2 Encrypted=\"" + new String(byArray4) + "\" ClearText=\"" + new String(byArray5) + "\"/>";
            n2 = this.writeMessage(string4.getBytes(), string);
            if (n2 < 0) {
                return n2;
            }
            byArray7 = this.readMessage(0, string);
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

    private int CSC_Test(byte[] byArray) {
        try {
            int n;
            byte[] byArray2 = null;
            byte[] byArray3 = null;
            byte[] byArray4 = null;
            byte[] byArray5 = null;
            byte[] byArray6 = null;
            Object var9_7 = null;
            String string = "!@%!@#%$%#$%";
            byte[] byArray7 = null;
            byte[] byArray8 = null;
            byte[] byArray9 = null;
            byte[] byArray10 = null;
            byArray7 = string.getBytes();
            byArray6 = new BASE64Decoder().decodeBuffer(new String(byArray));
            byArray8 = this.readCSCMessage(1024, 1);
            if (byArray8 == null) {
                return 0;
            }
            byArray9 = this.getAttributeValue(new String(byArray8), "ClearText");
            byArray8 = null;
            if (byArray9 == null) {
                return -1;
            }
            byArray2 = new BASE64Decoder().decodeBuffer(new String(byArray9));
            int n2 = byArray2.length;
            RC4Cipher rC4Cipher = new RC4Cipher(byArray6);
            rC4Cipher.encrypt(byArray2, 0, byArray2, 0, n2);
            rC4Cipher = null;
            byArray4 = new BASE64Encoder().encode(byArray2).getBytes();
            n2 = (int)System.currentTimeMillis();
            byArray3 = new byte[byArray7.length];
            for (n = 0; n < byArray7.length; ++n) {
                byArray3[n] = (byte)(byArray7[n] ^ (char)n2);
                n2 >>= 3;
                n2 = (int)((long)n2 ^ System.currentTimeMillis());
            }
            byArray5 = new BASE64Encoder().encode(byArray3).getBytes();
            n2 = byArray5.length;
            String string2 = "<CSC_Test2 Encrypted=\"" + new String(byArray4) + "\" ClearText=\"" + new String(byArray5) + "\"/>";
            boolean bl = this.writeCSCMessage(string2.getBytes(), 1);
            if (!bl) {
                return n2;
            }
            byArray8 = this.readCSCMessage(1024, 1);
            if (byArray8 == null) {
                return -1;
            }
            byArray10 = this.getAttributeValue(new String(byArray8), "Encrypted");
            byArray8 = null;
            if (byArray10 == null) {
                return -1;
            }
            byArray2 = new BASE64Decoder().decodeBuffer(new String(byArray10));
            rC4Cipher = new RC4Cipher(byArray6);
            rC4Cipher.decrypt(byArray2, 0, byArray2, 0, byArray2.length);
            if (byArray2.length != byArray7.length) {
                return -1;
            }
            for (n = 0; n < byArray7.length; ++n) {
                if (byArray3[n] == byArray2[n]) continue;
                System.out.println("Not matching at " + n);
                return -1;
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return -1;
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean writeCSCMessage(byte[] byArray, int n) {
        byte[] byArray2 = new byte[]{0};
        int n2 = 4 + byArray.length + 1;
        Object object = this.objWriteCR;
        synchronized (object) {
            boolean bl = this.write(RRCUtil.getBytesForInt(n2), 4);
            if (!bl) {
                return bl;
            }
            bl = this.write(byArray, n2 - 4 - 1);
            bl = this.write(byArray2, 1);
            return bl;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] readCSCMessage(int n, int n2) {
        Object object = this.objWriteCR;
        synchronized (object) {
            byte[] byArray = new byte[4];
            boolean bl = this.read(byArray, 0, 4);
            if (RRCLogger.logEnabled) {
                RRCLogger.log(100, 17, "READ CSC MESSAGE - 4 byte header: " + new String(byArray));
            }
            if (!bl) {
                return null;
            }
            int n3 = ((byArray[0] & 0xFF) << 24) + ((byArray[1] & 0xFF) << 16) + ((byArray[2] & 0xFF) << 8) + ((byArray[3] & 0xFF) << 0);
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
            bl = this.read(byArray2, 0, n3 - 4);
            if (!bl) {
                return null;
            }
            return byArray2;
        }
    }

    private byte[] getAttributeValue(String string, String string2) {
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

    private boolean initializeSSL(String string, boolean bl) {
        try {
            int n = 0;
            this.log("Inside InitializeSSL()");
            n = this.objComm.getIpPort() == 0 ? Constants.TR_PORT_BASE + 0 : this.objComm.getIpPort() + 0;
            if (bl) {
                this.objReadSSLSocket = new SecureSocket(this.objReadSock, string, n, this.getUseTLS());
                this.log("this.objReadSSLSocket  " + this.objReadSSLSocket);
                this.objReadSSLSocket.connect();
                this.log("this.objReadSSLSocket.connect()  ");
            } else {
                this.objWriteSSLSocket = new SecureSocket(this.objWriteSock, string, n, this.getUseTLS());
                this.objWriteSSLSocket.connect();
            }
            return true;
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    private void createBufferInputStream() throws Exception {
        this.objBufferedInputStream = this.boolNeedRC4 ? new DataInputStream(new BufferedInputStream(this.objReadSock.getInputStream())) : (this.boolNeedSSL ? new DataInputStream(new BufferedInputStream(this.objReadSSLSocket.getSecureInputStream())) : new DataInputStream(new BufferedInputStream(this.objReadSock.getInputStream())));
    }

    private void createFilterOutputStream() throws Exception {
        this.objFilterOutputStream = this.boolNeedRC4 ? new DataOutputStream(this.objWriteSock.getOutputStream()) : (this.boolNeedSSL ? new DataOutputStream(this.objWriteSSLSocket.getSecureOutputStream()) : new DataOutputStream(this.objWriteSock.getOutputStream()));
    }

    private void createBufferInputTypeStream(String string) throws Exception {
        if (string.equals(EVENT)) {
            this.objBufferedInputEventStream = this.boolNeedRC4 ? new DataInputStream(new BufferedInputStream(this.objEventSock.getInputStream())) : (this.boolNeedSSL ? new DataInputStream(new BufferedInputStream(this.objEventSSLSocket.getSecureInputStream())) : new DataInputStream(new BufferedInputStream(this.objEventSock.getInputStream())));
        }
        if (string.equals(RFP)) {
            this.objBufferedInputRFPStream = this.boolNeedRC4 ? new DataInputStream(new BufferedInputStream(this.objRFPSock.getInputStream())) : (this.boolNeedSSL ? new DataInputStream(new BufferedInputStream(this.objRFPSSLSocket.getSecureInputStream())) : new DataInputStream(new BufferedInputStream(this.objRFPSock.getInputStream())));
        }
    }

    private void createFilterOutputTypeStream(String string) throws Exception {
        if (string.equals(EVENT)) {
            this.objFilterOutputEventStream = this.boolNeedRC4 ? new DataOutputStream(this.objEventSock.getOutputStream()) : (this.boolNeedSSL ? new DataOutputStream(this.objEventSSLSocket.getSecureOutputStream()) : new DataOutputStream(this.objEventSock.getOutputStream()));
        }
        if (string.equals(RFP)) {
            this.objFilterOutputRFPStream = this.boolNeedRC4 ? new DataOutputStream(this.objRFPSock.getOutputStream()) : (this.boolNeedSSL ? new DataOutputStream(this.objRFPSSLSocket.getSecureOutputStream()) : new DataOutputStream(this.objRFPSock.getOutputStream()));
        }
    }

    public static void setLastError(int n) {
        try {
            iLastError = n;
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 4, "SetLastError" + n);
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    public static int getLastError() {
        return iLastError;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean rspProc() {
        block61: {
            boolean bl = false;
            try {
                Object object;
                int n;
                int n2;
                Object object2;
                block59: {
                    Object object3;
                    this.dbLog(System.currentTimeMillis() + " Connect ----------------");
                    if (!this.isGDMode()) {
                        this.dbNotify(1001, 1);
                    }
                    if (!this.isGDMode()) {
                        try {
                            this.prepareSocket();
                        }
                        catch (IOException iOException) {
                            RRCLogger.log(300, 4, iOException, "Unable to connect to " + this.objComm.getInetAddress());
                            this.dbNotify(1050, 0);
                            return false;
                        }
                    }
                    if (!this.isGDMode()) {
                        bl = this.serverHandshake();
                    } else {
                        this.boolSocketOK = true;
                        this.objReadSock.setTcpNoDelay(true);
                        this.objReadSock.setSoTimeout(174000);
                        this.objWriteSock = this.objReadSock;
                        bl = true;
                        object3 = this.objCriticalSection;
                        synchronized (object3) {
                            if (!this.boolDisconnecting) {
                                if (RRCLogger.logEnabled) {
                                    RRCLogger.log(300, 4, "CONNECTION Successful");
                                }
                                this.boolConnected = true;
                            }
                        }
                    }
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "CONNECTION::RspProc: calling serverHandshake returned " + bl);
                    }
                    if (!bl) {
                        this.rspProcExitFunction();
                        return false;
                    }
                    this.dbNotify(1006, 9);
                    if (!this.isKx2Device) {
                        this.objKeepAliveThrd = new TRKeepAliveThread(this);
                        this.objKeepAliveThrd.start();
                    }
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "objReferral.getVersion() = " + this.objReferral.getVersion());
                    }
                    if (!(bl = this.startAuthentication()) && !this.isGDMode()) {
                        this.rspProcExitFunction();
                        return false;
                    }
                    if (this.isGDMode()) {
                        return true;
                    }
                    if (this.isKx2Device) {
                        try {
                            object3 = null;
                            object2 = new byte[]{0, 0, 0, 0};
                            object3 = this.objComm.getInetAddress();
                            n2 = 0;
                            n2 = this.objComm.getIpPort() == 0 ? Constants.TR_PORT_BASE : this.objComm.getIpPort();
                            n = this.connect((InetAddress)object3, n2, EVENT);
                            if (n < 0 && RRCLogger.logEnabled) {
                                RRCLogger.log(200, 4, "Serverhandshake:  negotiateCSC not able to connect to event socket " + n);
                            }
                            object = this.GetCSCInfo(EVENT);
                        }
                        catch (Exception exception) {
                            if (!RRCLogger.logEnabled) break block59;
                            RRCLogger.logException(exception);
                        }
                    }
                }
                this.lConnectTime = System.currentTimeMillis();
                this.boolPassWordChangeInprocess = false;
                int n3 = 0;
                if (this.isKx2Device) {
                    object2 = null;
                    object = null;
                    if (this.sessionKey != null) {
                        n3 = this.startReferralCSCSession("RDMEvent", this.sessionId, this.sessionKey, EVENT);
                    }
                    if ((n3 < 0 || this.sessionKey == null) && RRCLogger.logEnabled) {
                        RRCLogger.log(100, 4, "RSP Proc: Failure to create CSC_Session for Events.");
                    }
                    while (this.boolConnected) {
                        n2 = 0;
                        n = this.objEventSock.getSoTimeout();
                        this.objEventSock.setSoTimeout(500);
                        object2 = new byte[4];
                        Thread.sleep(250L);
                        Object object4 = this.objCriticalSection;
                        synchronized (object4) {
                            block62: {
                                block60: {
                                    try {
                                        n3 = this.objBufferedInputEventStream.read((byte[])object2, 0, 4);
                                        if (n3 >= 0 || this.updateActive) break block60;
                                        RRCLogger.log(300, 4, "Server dropped the connection iRes: " + n3 + "  updateActive: " + this.updateActive);
                                        this.dbNotify(1010, 9);
                                        this.rspProcExitFunction();
                                        break block61;
                                    }
                                    catch (SocketTimeoutException socketTimeoutException) {
                                        ++this.cnt;
                                        if (this.cnt >= 60 && this.isPingDevice()) {
                                            this.cnt = 0;
                                            this.databaseRequest(GET_DEVICE_ID, 30000);
                                        }
                                        this.objEventSock.setSoTimeout(n);
                                        continue;
                                    }
                                    catch (SocketException socketException) {
                                        if (RRCLogger.logEnabled) {
                                            RRCLogger.logException(socketException);
                                        }
                                        this.dbNotify(1017, 10);
                                        this.rspProcExitFunction();
                                        break block61;
                                    }
                                }
                                if (n3 != 4) {
                                    this.objEventSock.setSoTimeout(n);
                                    continue;
                                }
                                n2 = ((object2[0] & 0xFF) << 24) + ((object2[1] & 0xFF) << 16) + ((object2[2] & 0xFF) << 8) + ((object2[3] & 0xFF) << 0);
                                if (n2 < 5) {
                                    this.objEventSock.setSoTimeout(n);
                                    continue;
                                }
                                object2 = new byte[1024];
                                int n4 = this.objEventSock.getSoTimeout();
                                try {
                                    this.objEventSock.setSoTimeout(120000);
                                    if (this.readFromStream((byte[])object2, 0, n2 - 4, EVENT) >= 0) break block62;
                                    this.dbNotify(1020, 10);
                                    this.rspProcExitFunction();
                                    break block61;
                                }
                                finally {
                                    this.objEventSock.setSoTimeout(n4);
                                }
                            }
                            this.event((byte[])object2);
                        }
                    }
                    break block61;
                }
                while (this.boolConnected) {
                    object2 = null;
                    bl = this.read(this.objTRBaseCommand.toByteArray(), 0, 4);
                    this.iReadCount = 4;
                    if (!bl) {
                        if (!this.boolDisconnecting && !this.boolOrderlyShutdown) {
                            this.dbNotify(1017, 7);
                        }
                        break;
                    }
                    this.dbLogCmd(true, this.objTRBaseCommand);
                    try {
                        n2 = this.objTRBaseCommand.getCommand();
                        object2 = n2 >= 0 && n2 < this.objRSPDEFList.length ? (Object)this.objRSPDEFList[n2] : null;
                    }
                    catch (Exception exception) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.logException(exception);
                        }
                        object2 = null;
                    }
                    if (object2 == null) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(100, 4, "Connection:RspProc Command received does not have a Valid handler Command Received = " + this.objTRBaseCommand.getCommand());
                        }
                        this.dbNotify(1016, 8);
                        this.boolOrderlyShutdown = false;
                        break;
                    }
                    this.lTimeLastMsg = System.currentTimeMillis();
                    n2 = this.switchFunction((RSPDEF)object2);
                    if (n2 == 0) {
                        this.log("switchFunction :entered " + object2.getWhatToDo() + " AFTER SWITCH FUNCTION iResult=" + n2);
                        bl = false;
                        if (!this.boolDisconnecting) {
                            this.dbNotify(1017, 10);
                        }
                        break;
                    }
                    if (n2 == 1) {
                        bl = true;
                    } else if (n2 == 2) continue;
                    n = 0;
                    object = null;
                    if (this.objTRBaseCommand.getCommand() == 45) {
                        n = this.TRRSP_Cell_Data() ? 1 : 0;
                    } else {
                        Class<?>[] classArray = null;
                        Class<?> clazz = Class.forName("javaclientlib.clientlib.TRConnection");
                        object = clazz.getDeclaredMethod(object2.getMethodName(), classArray);
                        if (object == null) {
                            this.dbNotify(1016, 9);
                            break;
                        }
                        Object[] objectArray = null;
                        Object object5 = ((Method)object).invoke((Object)this, objectArray);
                        n = ((Boolean)object5).booleanValue();
                    }
                    this.log("Invoked Response Handler returned " + (n != 0) + " for method " + object);
                    if (n != 0 || this.iReadCount >= this.objTRBaseCommand.getCmdLength()) continue;
                    bl = this.sinkData(this.objTRBaseCommand.getCmdLength() - this.iReadCount);
                }
            }
            catch (InvocationTargetException invocationTargetException) {
                RRCLogger.logException(invocationTargetException);
            }
            catch (InterruptedException interruptedException) {
                RRCLogger.logException(interruptedException);
            }
            catch (Exception exception) {
                RRCLogger.logException(exception);
                this.dbNotify(1010, 9);
            }
        }
        this.log("\t\t OUT OF WHILE ");
        this.boolConnected = false;
        try {
            this.rspProcExitFunction();
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return true;
    }

    private int switchFunction(RSPDEF rSPDEF) {
        boolean bl = false;
        int n = this.objTRBaseCommand.getCmdLength() - 4;
        switch (rSPDEF.getWhatToDo()) {
            case 0: {
                break;
            }
            case 1: {
                if (this.objTRBaseCommand.getCmdLength() == rSPDEF.getDataSize()) break;
                bl = this.sinkData(n);
                return 2;
            }
            case 2: {
                if (this.objTRBaseCommand.getCmdLength() != rSPDEF.getDataSize()) {
                    bl = this.sinkData(n);
                    return 2;
                }
                bl = this.read(this.objTRBaseCommand.toByteArray(), 4, n);
                this.iReadCount += n;
                break;
            }
            case 3: {
                if (this.objTRBaseCommand.getCmdLength() > rSPDEF.getDataSize()) {
                    bl = this.sinkData(this.objTRBaseCommand.getCmdLength());
                    return 2;
                }
                bl = this.read(this.objTRBaseCommand.toByteArray(), 4, n);
                this.iReadCount += n;
                break;
            }
            case 4: {
                if (this.objTRBaseCommand.getCmdLength() < rSPDEF.getDataSize()) {
                    bl = this.sinkData(n);
                    return 2;
                }
                bl = this.read(this.objTRBaseCommand.toByteArray(), 4, rSPDEF.getDataSize() - 4);
                byte[] byArray = new byte[4];
                byArray = this.objTRBaseCommand.getBytes(4, 4);
                this.responseTotalSize = this.getInt(0, byArray);
                this.iReadCount += rSPDEF.getDataSize() - 4;
                break;
            }
            default: {
                return 2;
            }
        }
        return bl ? 1 : 0;
    }

    private int switchRFPFunction(RSPDEF rSPDEF) {
        boolean bl = false;
        int n = this.objTRBaseCommand.getCmdLength() - 4;
        switch (rSPDEF.getWhatToDo()) {
            case 0: {
                break;
            }
            case 1: {
                if (this.objTRBaseCommand.getCmdLength() == rSPDEF.getDataSize()) break;
                bl = this.sinkRFPData(n);
                return 2;
            }
            case 2: {
                if (this.objTRBaseCommand.getCmdLength() != rSPDEF.getDataSize()) {
                    bl = this.sinkRFPData(n);
                    return 2;
                }
                int n2 = this.readFromStream(this.objTRBaseCommand.toByteArray(), 4, n, RFP);
                bl = n2 == 0;
                this.iReadCount += n;
                break;
            }
            case 3: {
                if (this.objTRBaseCommand.getCmdLength() > rSPDEF.getDataSize()) {
                    bl = this.sinkRFPData(this.objTRBaseCommand.getCmdLength());
                    return 2;
                }
                int n3 = this.readFromStream(this.objTRBaseCommand.toByteArray(), 4, n, RFP);
                bl = n3 == 0;
                this.iReadCount += n;
                break;
            }
            case 4: {
                if (this.objTRBaseCommand.getCmdLength() < rSPDEF.getDataSize()) {
                    bl = this.sinkRFPData(n);
                    return 2;
                }
                int n4 = this.readFromStream(this.objTRBaseCommand.toByteArray(), 4, rSPDEF.getDataSize() - 4, RFP);
                bl = n4 == 0;
                byte[] byArray = new byte[4];
                byArray = this.objTRBaseCommand.getBytes(4, 4);
                this.responseTotalSize = this.getInt(0, byArray);
                this.iReadCount += rSPDEF.getDataSize() - 4;
                break;
            }
            default: {
                return 2;
            }
        }
        return bl ? 1 : 0;
    }

    public int getInt(int n, byte[] byArray) {
        int n2 = (byArray[n + 0] & 0xFF) >> 0;
        int n3 = (byArray[n + 1] & 0xFF) >> 0;
        int n4 = (byArray[n + 2] & 0xFF) >> 0;
        int n5 = (byArray[n + 3] & 0xFF) >> 0;
        return (n2 << 24) + (n3 << 16) + (n4 << 8) + (n5 << 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rspProcExitFunction() throws Exception {
        boolean bl = this.disConnect();
        this.dbLog(System.currentTimeMillis() + " Disconnect -------------\n");
        if (!this.isKx2Device) {
            this.log(this.objKeepAliveThrd + "==");
            if (this.objKeepAliveThrd != null) {
                this.log("Interputting " + this.objKeepAliveThrd + "==");
                this.objKeepAliveThrd.interrupt();
                this.objKeepAliveThrd = null;
            }
        }
        Object object = this.objCriticalSection;
        synchronized (object) {
            this.objReadSSLSocket = null;
            this.objReadSock = null;
            this.objWriteSSLSocket = null;
            this.objWriteSock = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean disConnect() {
        this.log("::Disconnect");
        try {
            boolean bl = false;
            Object object = this.objCriticalSection;
            synchronized (object) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "::Disconnect - this.boolDisconnecting=" + this.boolDisconnecting);
                }
                if (!this.boolDisconnecting) {
                    Object object2 = this.objWriteCR;
                    synchronized (object2) {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(300, 4, "::Disconnect - this.boolConnected=" + this.boolConnected);
                        }
                        if (this.boolConnected) {
                            if (this.getAuthenticated()) {
                                if (!this.isKx2Device) {
                                    this.objTRBaseCommand.setCommand((byte)7);
                                    this.objTRBaseCommand.setCmdLength((short)4);
                                    this.objTRBaseCommand.setPktID((byte)0);
                                    this.boolOrderlyShutdown = this.write(this.objTRBaseCommand.toByteArray(), 4);
                                }
                            } else {
                                this.boolOrderlyShutdown = true;
                            }
                        }
                        this.boolDisconnecting = true;
                    }
                    if (!this.isKx2Device) {
                        this.cancelIO();
                    }
                    if (!this.boolOrderlyShutdown) {
                        this.dbNotify(1010, 0);
                    } else {
                        this.dbNotify(1020, 1);
                    }
                    this.boolConnected = false;
                    this.boolIsAdministrator = false;
                    if (this.objReadSock != null) {
                        this.objReadSock.close();
                        this.objReadSock = null;
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(300, 4, "closing read sockets" + this.objBufferedInputStream);
                        }
                    }
                    if (this.objEventSock != null) {
                        this.objEventSock.close();
                        this.objEventSock = null;
                    }
                    if (this.objRFPSock != null) {
                        this.objRFPSock.close();
                        this.objRFPSock = null;
                    }
                    if (this.objWriteSock != null) {
                        this.objWriteSock.close();
                        this.objWriteSock = null;
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(300, 4, "closing write sockets" + this.objFilterOutputStream);
                        }
                    }
                    if (this.objComm.getConnType() == 1) {
                        // empty if block
                    }
                }
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        this.log("this.boolDisconnecting=" + this.boolDisconnecting);
        return this.boolOrderlyShutdown;
    }

    private int CalcHandshakeChecksum(TRCMD_HANDSHAKE_ACK_DATA tRCMD_HANDSHAKE_ACK_DATA) {
        long l = 0L;
        long l2 = tRCMD_HANDSHAKE_ACK_DATA.getClientIp() & 0xFFFFFFFF;
        int n = tRCMD_HANDSHAKE_ACK_DATA.getClientPort() & 0xFFFF;
        l = (long)tRCMD_HANDSHAKE_ACK_DATA.getSignature() + l2 + (long)n + (long)tRCMD_HANDSHAKE_ACK_DATA.getTimeStamp() + (long)tRCMD_HANDSHAKE_ACK_DATA.getIndex() + (long)tRCMD_HANDSHAKE_ACK_DATA.getRandom() + (long)tRCMD_HANDSHAKE_ACK_DATA.getConnCount();
        int n2 = (int)(l & 0xFFFFFFFFFFFFFFFFL);
        return n2;
    }

    private boolean isServerSendingInNetworkByteOrder(byte[] byArray) {
        return (byArray[0] & byArray[1]) == 0;
    }

    private boolean nop() throws Exception {
        this.objTRBaseCommand.setCmdLength((short)4);
        this.objTRBaseCommand.setCommand((byte)2);
        boolean bl = this.sendTRCmd(this.objTRBaseCommand);
        return bl;
    }

    private boolean nack() throws Exception {
        boolean bl = false;
        bl = this.response();
        return bl;
    }

    private boolean TRRSP_Notify() throws Exception {
        TRVideoStream tRVideoStream = this.getVideoStreamDevice(this.objTRBaseCommand.getByte(12));
        if (tRVideoStream != null) {
            if (this.objTRBaseCommand.getCmdLength() > 13) {
                this.objTRBaseCommand.setInt(13, 8);
            }
            tRVideoStream.TRRSP_Notify(this.objTRBaseCommand);
            return true;
        }
        return true;
    }

    private boolean pong() {
        return true;
    }

    private String getXMLNodeValue(String string, String string2) {
        block12: {
            try {
                Document document = xmlParser.getDocument(string);
                NodeList nodeList = document.getElementsByTagName(string2);
                if (nodeList.getLength() != 1) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(100, 4, "There are " + nodeList.getLength() + " " + string2 + " nodes.  Cannot determine " + string2);
                    }
                    return null;
                }
                Node node = nodeList.item(0);
                if (!node.hasChildNodes()) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(100, 4, string2 + " has no child nodes!  Cannot determine " + string2);
                    }
                    return null;
                }
                if (!((node = node.getFirstChild()) instanceof Text)) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(100, 4, string2 + "'s child isn't a text node!  Cannot determine " + string2);
                    }
                    return null;
                }
                return node.getNodeValue();
            }
            catch (ParserConfigurationException parserConfigurationException) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 4, "Parser config Exception parsing the following XML string:\n\t " + string + "\nException Message: " + parserConfigurationException.getMessage());
                }
            }
            catch (SAXException sAXException) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 4, "SAXException getting node " + string2 + " from the following XML string:\n\t" + string + "\nException Message: " + sAXException.getMessage());
                }
            }
            catch (IOException iOException) {
                if (!RRCLogger.logEnabled) break block12;
                RRCLogger.log(200, 4, "IOException getting node " + string2 + " from the following XML string:\n\t" + string + "\nException Message: " + iOException.getMessage());
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setSessionInfo() throws Exception {
        xmlParser = TRConnection.getXmlParserInstance();
        xmlParser = new XMLParser();
        this.boolIsAdministrator = false;
        String string = "<Session><GetSessionID/></Session>";
        byte[] byArray = null;
        String string2 = null;
        Object object = this.objWriteCR;
        synchronized (object) {
            if (!this.writeCSCMessage(string.getBytes(), 0)) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 4, "setSessionInfo: unable to write RDM GetSessionID request.");
                }
                return;
            }
            byArray = this.readCSCMessage(0, 0);
            if (byArray == null) {
                RRCLogger.log(200, 4, "setSessionInfo: unable to read RDM GetSessionID response.");
                return;
            }
            string2 = new String(byArray);
            if (string2.indexOf(0) > 0) {
                string2 = string2.substring(0, string2.indexOf(0));
            }
            this.sessionId = this.getXMLNodeValue(string2, "SessionID");
            this.sessionKey = this.getXMLNodeValue(string2, "SessionKey");
            if (this.sessionId == null || this.sessionKey == null) {
                RRCLogger.log(100, 4, "sessionId or sessionKey is null");
            }
            if (!this.writeCSCMessage((string = "<Database><Get><Select>/System/Sessions/Session[@id=\"" + this.sessionId + "\"]/User</Select>" + "<Nodes>User</Nodes><SubNodes>GroupID</SubNodes></Get></Database>").getBytes(), 0)) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 4, "setSessionInfo: unable to write RDM group ID request.");
                }
                return;
            }
            byArray = this.readCSCMessage(0, 0);
            if (byArray == null) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(200, 4, "setSessionInfo: unable to read RDM group ID response.");
                }
                return;
            }
            string2 = new String(byArray);
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 4, "setSessionInfo: RDM group ID response.: " + string2);
            }
            if (string2.indexOf(0) > 0) {
                string2 = string2.substring(0, string2.indexOf(0));
            }
            String string3 = this.getXMLNodeValue(string2, "GroupID");
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 4, "setSessionInfo: GroupID: " + string3);
            }
            if (string3 == null) {
                RRCLogger.log(200, 4, "GroupId is null");
            } else if (string3.equals("Group0")) {
                this.boolIsAdministrator = true;
            }
        }
    }

    private boolean startAuthentication() throws Exception {
        if (this.isKx2Device) {
            boolean bl;
            boolean bl2 = this.boolNeedSSL;
            this.boolNeedSSL = true;
            if (!this.isGDMode()) {
                this.initializeSSL(this.objComm.getInetAddress().getHostAddress(), false);
                this.createFilterOutputStream();
            }
            this.objReadSSLSocket = this.objWriteSSLSocket;
            this.createBufferInputStream();
            if (this.objReferral.getVersion() > 0) {
                int n = this.CSC_Test(this.objReferral.getSessionKey());
                if (n < 0) {
                    this.dbNotify(1017, 192);
                    return false;
                }
                this.sessionId = new String(this.objReferral.getSessionID());
                this.sessionKey = new String(this.objReferral.getSessionKey());
                this.boolAuthenticated = true;
                return true;
            }
            String string = Util.escapeXML(new String(this.objUserInfo.getPassword()));
            String string2 = "<CSC_Auth UserName=\"" + new String(this.objUserInfo.getName()) + "\" Password=\"" + string + "\"/>";
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "Attempting CSC Authentication.");
            }
            if (!(bl = this.writeCSCMessage(string2.getBytes(), 0))) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "CSC Authentication could not be done: failure writing CSC message.");
                }
                this.boolNeedSSL = bl2;
                return false;
            }
            byte[] byArray = this.readCSCMessage(256, 0);
            if (byArray == null) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, "CSC Authentication failure: returned null!");
                }
                this.boolNeedSSL = bl2;
                return false;
            }
            String string3 = new String(byArray);
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "CSC Authentication result: " + new String(byArray));
            }
            if (string3.startsWith("<CSC_Pass/>")) {
                this.dbNotify(1007, 0);
                this.setSessionInfo();
                this.boolAuthenticated = true;
            } else {
                this.boolAuthenticated = false;
                if (string3.startsWith("<CSC_Fail>-18177</CSC_Fail>")) {
                    this.dbNotify(1040, 0);
                } else if (string3.startsWith("<CSC_Fail>-19931</CSC_Fail>")) {
                    this.dbNotify(1041, 0);
                } else if (string3.startsWith("<CSC_Fail>-19937</CSC_Fail>")) {
                    this.dbNotify(1042, 0);
                } else if (string3.startsWith("<CSC_Fail>-19960</CSC_Fail>")) {
                    this.dbNotify(1043, 0);
                } else if (string3.startsWith("<CSC_Fail>-19959</CSC_Fail>")) {
                    this.dbNotify(1044, 0);
                } else if (string3.startsWith("<CSC_Fail>-19938</CSC_Fail>")) {
                    this.dbNotify(1045, 0);
                } else if (string3.startsWith("<CSC_Fail>-19939</CSC_Fail>")) {
                    this.dbNotify(1026, 0);
                } else if (string3.startsWith("<CSC_Fail>-20240</CSC_Fail>")) {
                    this.dbNotify(1047, 0);
                } else if (string3.startsWith("<CSC_Fail>-19944</CSC_Fail>")) {
                    this.dbNotify(1048, 0);
                } else if (string3.startsWith("<CSC_Fail>-19930</CSC_Fail>")) {
                    this.dbNotify(1049, 0);
                } else if (string3.startsWith("<CSC_Fail>-19911</CSC_Fail>")) {
                    this.setGDMode(true);
                    this.dbNotify(1051, 0);
                } else if (string3.startsWith("<CSC_Fail>-19896</CSC_Fail>")) {
                    this.setGDMode(true);
                    this.dbNotify(1052, 0);
                } else if (string3.startsWith("<CSC_Fail>-19895</CSC_Fail>")) {
                    this.setGDMode(true);
                    this.dbNotify(1053, 0);
                } else {
                    this.dbNotify(1008, 0);
                }
            }
            return this.boolAuthenticated;
        }
        TRCMD_CLIENT_CHALLENGE_DATA tRCMD_CLIENT_CHALLENGE_DATA = new TRCMD_CLIENT_CHALLENGE_DATA();
        this.getRandomData(this.byRand1, 64);
        this.getRandomData(this.byRand2, 64);
        tRCMD_CLIENT_CHALLENGE_DATA.setR1(this.byRand1);
        tRCMD_CLIENT_CHALLENGE_DATA.setR2(this.byRand2);
        tRCMD_CLIENT_CHALLENGE_DATA.setFlags(0);
        tRCMD_CLIENT_CHALLENGE_DATA.setCommand((byte)4);
        tRCMD_CLIENT_CHALLENGE_DATA.setCmdLength((short)136);
        this.dbLogCmd(false, tRCMD_CLIENT_CHALLENGE_DATA);
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "sending TRCMD_CLIENT_CHALLENGE_DATA");
        }
        return this.write(tRCMD_CLIENT_CHALLENGE_DATA.toByteArray(), 136);
    }

    private boolean TRRSP_Server_Challenge() throws Exception {
        TRRSP_SERVER_CHALLENGE_DATA tRRSP_SERVER_CHALLENGE_DATA = new TRRSP_SERVER_CHALLENGE_DATA(this.objTRBaseCommand.toByteArray());
        byte[] byArray = new byte[136];
        String string = "TeleReach Client";
        byte[] byArray2 = string.getBytes();
        TRCMD_CLIENT_RESPONSE_DATA tRCMD_CLIENT_RESPONSE_DATA = new TRCMD_CLIENT_RESPONSE_DATA();
        this.byRand3 = tRRSP_SERVER_CHALLENGE_DATA.getR3();
        this.byRand4 = tRRSP_SERVER_CHALLENGE_DATA.getR4();
        byte[] byArray3 = new byte[24];
        Arrays.fill(byArray3, (byte)0);
        System.arraycopy(this.objComm.getPrivateKey(), 0, byArray3, 0, this.objComm.getPrivateKey().length);
        this.objComm.setPrivateKey(byArray3);
        MD5 mD5 = new MD5();
        mD5.update(byArray2);
        mD5.update(this.byRand1);
        mD5.update(this.byRand3);
        mD5.update(this.objComm.getPrivateKey());
        byte[] byArray4 = mD5.encode();
        tRCMD_CLIENT_RESPONSE_DATA.setMD5(byArray4);
        tRCMD_CLIENT_RESPONSE_DATA.setFlags(0);
        tRCMD_CLIENT_RESPONSE_DATA.setCommand((byte)5);
        tRCMD_CLIENT_RESPONSE_DATA.setCmdLength((short)24);
        this.dbLogCmd(false, tRCMD_CLIENT_RESPONSE_DATA);
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "sending TRCMD.CLIENT_RESPONSE");
        }
        return this.write(tRCMD_CLIENT_RESPONSE_DATA.toByteArray(), 24);
    }

    private boolean TRRSP_Server_Response() throws Exception {
        TRRSP_SERVER_RESPONSE_DATA tRRSP_SERVER_RESPONSE_DATA = new TRRSP_SERVER_RESPONSE_DATA(this.objTRBaseCommand.toByteArray());
        boolean bl = false;
        String string = "TeleReach Server";
        byte[] byArray = string.getBytes();
        byte[] byArray2 = null;
        boolean bl2 = false;
        MD5 mD5 = new MD5();
        mD5.update(byArray);
        mD5.update(this.byRand2);
        mD5.update(this.byRand4);
        mD5.update(this.objComm.getPrivateKey());
        byArray2 = mD5.encode();
        bl2 = Arrays.equals(byArray2, tRRSP_SERVER_RESPONSE_DATA.getMd5());
        if (!bl2) {
            this.dbNotify(1019, 35);
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 4, "---MD5 MISMATCH----");
            }
            bl = false;
        } else {
            this.byChapID = tRRSP_SERVER_RESPONSE_DATA.getChapID();
            this.byChapData = tRRSP_SERVER_RESPONSE_DATA.getChap();
            mD5 = new MD5();
            mD5.update(this.byRand1);
            mD5.update(this.byRand3);
            mD5.update(this.objComm.getPrivateKey());
            byArray2 = mD5.encode();
            mD5 = new MD5();
            mD5.update(this.byRand2);
            mD5.update(this.byRand4);
            mD5.update(this.objComm.getPrivateKey());
            byArray2 = mD5.encode();
            this.RC4Key = byArray2;
            this.log("Calling sendAccessRequest()");
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "Calling sendAccessRequest()");
            }
            bl = this.sendAccessRequest(true);
        }
        return bl;
    }

    private boolean sendAccessRequest(boolean bl) throws Exception {
        this.log("Inside SendAccessRequest");
        TRCMD_ACCESS_REQUEST_DATA tRCMD_ACCESS_REQUEST_DATA = new TRCMD_ACCESS_REQUEST_DATA();
        RADIUS_PACKET rADIUS_PACKET = new RADIUS_PACKET();
        RadiusPacket radiusPacket = new RadiusPacket();
        byte[] byArray = new byte[17];
        boolean bl2 = false;
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "TRConnection.sendAccessRequest  boolHaveLoginData = " + this.boolHaveLoginData + ", boolFirstAttempt = " + bl + ", objReferral.getVersion() = " + this.objReferral.getVersion());
        }
        if (!(this.boolHaveLoginData && bl || this.objReferral.getVersion() != 0)) {
            if (this.objUserInfo == null) {
                this.objUserInfo = new TRLIB_USERINFO();
            }
            this.objUserInfo.getPassword()[0] = 0;
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "sending login request to TRServer objUserInfo = " + this.objUserInfo + ", objSvrID = " + this.objSvrID);
            }
            if (!(bl2 = this.login(this.objSvrID, this.objUserInfo, !bl))) {
                this.boolOrderlyShutdown = true;
                this.disConnect();
                return bl2;
            }
        }
        radiusPacket.clearPacket(rADIUS_PACKET);
        rADIUS_PACKET.setCode((byte)1);
        rADIUS_PACKET.setIdentifier((byte)0);
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "IRadius.ACCESS_REQUEST = 1");
        }
        if (this.objReferral.getVersion() == 1) {
            if (this.objUserInfo == null) {
                this.objUserInfo = new TRLIB_USERINFO();
            }
            bl2 = radiusPacket.appendStringAttribute(rADIUS_PACKET, (byte)1, 0, this.objUserInfo.getName());
        } else {
            bl2 = radiusPacket.appendStringAttribute(rADIUS_PACKET, (byte)1, this.objUserInfo.getName().length, this.objUserInfo.getName());
        }
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "objRadiusPacket.appendStringAttribute = " + bl2 + ", this.objSvrID = " + this.objSvrID + ", this.objSvrID.getSecurityFlags() = " + Integer.toHexString(this.objSvrID.getSecurityFlags()) + ", TR_SECURITY.LOGIN_MASK = " + Integer.toHexString(61440) + ", TR_SECURITY.PAP_LOGIN = " + Integer.toHexString(0));
        }
        if ((this.objSvrID.getSecurityFlags() & 0xF000) == 0) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "adding PAP Password, objUserInfo.getPassword() = " + this.objUserInfo.getPassword());
            }
            this.log("PAP PASSWORD");
            if (this.objReferral.getVersion() == 1) {
                radiusPacket.appendPasswordAttribute(rADIUS_PACKET, 0, this.objUserInfo.getPassword(), null, 0);
            } else {
                radiusPacket.appendPasswordAttribute(rADIUS_PACKET, this.objUserInfo.getPassword().length, this.objUserInfo.getPassword(), null, 0);
            }
        } else {
            byte[] byArray2 = new byte[]{this.byChapID};
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "calling computeCHAPResponse chapID = " + this.byChapID);
            }
            radiusPacket.computeCHAPResponse(byArray2, this.objUserInfo.getPassword(), this.byChapData, byArray);
            byArray[0] = this.byChapID;
            radiusPacket.appendStringAttribute(rADIUS_PACKET, (byte)3, 17, byArray);
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 4, "objRadiusPacket.appendStringAttribute with IRadius.CHAP_PASSWORD = " + bl2);
            }
        }
        tRCMD_ACCESS_REQUEST_DATA.setRadiusPacketData(rADIUS_PACKET.getDataBytes());
        short s = (short)(12 + rADIUS_PACKET.getPktLength());
        tRCMD_ACCESS_REQUEST_DATA.setCommand((byte)6);
        tRCMD_ACCESS_REQUEST_DATA.setCmdLength(s);
        byte[] byArray3 = tRCMD_ACCESS_REQUEST_DATA.toByteArray();
        this.dbLogCmd(false, tRCMD_ACCESS_REQUEST_DATA);
        return this.write(byArray3, s);
    }

    private boolean TRRSP_Access_Response() throws Exception {
        TRRSP_ACCESS_RESPONSE_DATA tRRSP_ACCESS_RESPONSE_DATA = new TRRSP_ACCESS_RESPONSE_DATA(this.objTRBaseCommand.toByteArray());
        byte[] byArray = new byte[4096];
        System.arraycopy(this.objTRBaseCommand.toByteArray(), 12, byArray, 0, byArray.length);
        RADIUS_PACKET rADIUS_PACKET = new RADIUS_PACKET();
        rADIUS_PACKET.populate(byArray);
        RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER = new RADIUS_ATTRIB_HEADER();
        RADIUS_ATTRIB_STRING rADIUS_ATTRIB_STRING = new RADIUS_ATTRIB_STRING();
        RADIUS_PACKET rADIUS_PACKET2 = new RADIUS_PACKET();
        int n = 0;
        int n2 = 0;
        byte[] byArray2 = null;
        byte[] byArray3 = new byte[129];
        boolean bl = false;
        RadiusPacket radiusPacket = new RadiusPacket();
        TRCMD_ACCESS_REQUEST_DATA tRCMD_ACCESS_REQUEST_DATA = new TRCMD_ACCESS_REQUEST_DATA();
        RADIUS_PACKET rADIUS_PACKET3 = new RADIUS_PACKET();
        if (tRRSP_ACCESS_RESPONSE_DATA.getCommand() == 9 && tRRSP_ACCESS_RESPONSE_DATA.getFlags() == 4096) {
            this.dbNotify(1026, 0);
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 4, "Connection:TRRSP_Access_Response: NOTIFY - CHANGE USER PASSWORD ");
            }
            this.boolPassWordChangeInprocess = true;
        }
        switch (rADIUS_PACKET.getCode()) {
            case 2: {
                this.log("Connection:TRRSP_Access_Response: ACCEPT");
                if (this.boolPassWordChangeInprocess) break;
                this.endAuthenticationPhase();
                this.lPermissions = (long)tRRSP_ACCESS_RESPONSE_DATA.getPermissions() & 0xFFFFFFFFL;
                this.boolAuthenticated = true;
                this.dbNotify(1007, 36);
                break;
            }
            case 3: {
                this.log("Connection:TRRSP_Access_Response: REJECT");
                this.dbNotify(1008, 37);
                if ((tRRSP_ACCESS_RESPONSE_DATA.getFlags() & 1) != 0) {
                    bl = this.sendAccessRequest(false);
                    break;
                }
                this.dbNotify(1009, 38);
                this.boolOrderlyShutdown = false;
                break;
            }
            case 6: {
                this.log("Connection:TRRSP_Access_Response: CHALLENGE");
                rADIUS_ATTRIB_HEADER = radiusPacket.findAttribute(rADIUS_PACKET, null, (byte)18);
                rADIUS_ATTRIB_STRING = (RADIUS_ATTRIB_STRING)rADIUS_ATTRIB_HEADER;
                while (rADIUS_ATTRIB_STRING != null) {
                    rADIUS_PACKET2.appendAttributeData(rADIUS_ATTRIB_STRING.getAttribute(), n, rADIUS_ATTRIB_STRING.getAttribute().length);
                    byArray2 = rADIUS_PACKET2.getDataBytes();
                    byArray2[rADIUS_PACKET2.getDataBytes().length - 2 - 1] = 0;
                    n = rADIUS_ATTRIB_HEADER.getLengthAttribute();
                    ++n2;
                    rADIUS_ATTRIB_HEADER = radiusPacket.findAttribute(rADIUS_PACKET, rADIUS_ATTRIB_HEADER, (byte)18);
                    rADIUS_ATTRIB_STRING = (RADIUS_ATTRIB_STRING)rADIUS_ATTRIB_HEADER;
                }
                byArray2 = rADIUS_PACKET2.getDataBytes();
                byArray2[rADIUS_PACKET2.getDataBytes().length - 1] = 0;
                byArray2[rADIUS_PACKET2.getDataBytes().length] = 0;
                bl = this.loginChallenge(n2, byArray2, byArray3);
                if (!bl) {
                    this.boolOrderlyShutdown = true;
                    this.disConnect();
                    bl = false;
                    break;
                }
                radiusPacket.clearPacket(rADIUS_PACKET3);
                rADIUS_PACKET3.setCode((byte)1);
                rADIUS_PACKET3.setIdentifier((byte)0);
                radiusPacket.appendPasswordAttribute(rADIUS_PACKET3, byArray3.length, byArray3, null, 0);
                radiusPacket.appendStringAttribute(rADIUS_PACKET3, (byte)1, this.objUserInfo.getName().length, this.objUserInfo.getName());
                rADIUS_ATTRIB_HEADER = radiusPacket.findAttribute(rADIUS_PACKET, null, (byte)24);
                if (rADIUS_ATTRIB_HEADER != null) {
                    radiusPacket.appendAttribute(rADIUS_PACKET, rADIUS_ATTRIB_HEADER);
                }
                rADIUS_ATTRIB_HEADER = radiusPacket.findAttribute(rADIUS_PACKET, null, (byte)33);
                while (rADIUS_ATTRIB_HEADER != null) {
                    radiusPacket.appendAttribute(rADIUS_PACKET3, rADIUS_ATTRIB_HEADER);
                    rADIUS_ATTRIB_HEADER = radiusPacket.findAttribute(rADIUS_PACKET, rADIUS_ATTRIB_HEADER, (byte)33);
                }
                tRCMD_ACCESS_REQUEST_DATA.setRadiusPacketData(rADIUS_PACKET3.getDataBytes());
                tRCMD_ACCESS_REQUEST_DATA.setCommand((byte)6);
                int n3 = 12 + rADIUS_PACKET3.getPktLength();
                tRCMD_ACCESS_REQUEST_DATA.setCmdLength((short)n3);
                this.dbLogCmd(false, tRCMD_ACCESS_REQUEST_DATA);
                bl = this.write(tRCMD_ACCESS_REQUEST_DATA.toByteArray(), n3);
                break;
            }
            default: {
                bl = false;
            }
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean endAuthenticationPhase() throws Exception {
        boolean bl = false;
        Object object = this.objWriteCR;
        synchronized (object) {
            TRConnection.sleep(500L);
            switch (this.objSvrID.getSecurityFlags() & 0xF00) {
                case 256: 
                case 2048: {
                    break;
                }
                case 512: {
                    bl = true;
                    this.boolNeedSSL = false;
                    break;
                }
                case 1024: {
                    bl = true;
                    this.boolNeedRC4 = true;
                    break;
                }
                default: {
                    this.dbNotify(1018, 45);
                    this.disConnect();
                }
            }
            this.createBufferInputStream();
            this.createFilterOutputStream();
            if (bl) {
                // empty if block
            }
        }
        return true;
    }

    private void cancelIO() {
        Enumeration enumeration = this.objCmdWaitHashtable.keys();
        while (enumeration.hasMoreElements()) {
            Object k = enumeration.nextElement();
            TRCMDWAIT tRCMDWAIT = (TRCMDWAIT)this.objCmdWaitHashtable.get(k);
            if (tRCMDWAIT == null || tRCMDWAIT.getEvent() == null) continue;
            tRCMDWAIT.getEvent().notifying();
        }
    }

    private void cancelIO(TRCMDWAIT tRCMDWAIT) {
        if (tRCMDWAIT == null) {
            return;
        }
        Enumeration enumeration = this.objCmdWaitHashtable.keys();
        while (enumeration.hasMoreElements()) {
            Object k = enumeration.nextElement();
            TRCMDWAIT tRCMDWAIT2 = (TRCMDWAIT)this.objCmdWaitHashtable.get(k);
            if (tRCMDWAIT2 == null || tRCMDWAIT.getPktID() != tRCMDWAIT2.getPktID()) continue;
            this.objCmdWaitHashtable.remove(k);
            if (tRCMDWAIT2.getEvent() == null) continue;
            tRCMDWAIT2.getEvent().notifying();
        }
    }

    private boolean targetParamsChanged() throws Exception {
        TRVideoStream tRVideoStream = this.getVideoStreamDevice(this.objTRBaseCommand.getByte(4));
        if (tRVideoStream != null) {
            TRRSP_TARGET_PARAMS_DATA tRRSP_TARGET_PARAMS_DATA = new TRRSP_TARGET_PARAMS_DATA(this.objTRBaseCommand.toByteArray());
            try {
                TRSRVR_TARGET_PARAMS tRSRVR_TARGET_PARAMS = tRRSP_TARGET_PARAMS_DATA.getParams();
                tRVideoStream.targetParamsChanged(tRSRVR_TARGET_PARAMS);
                return true;
            }
            catch (Exception exception) {
                return false;
            }
        }
        return false;
    }

    private boolean TRRSP_New_Video_Mode() throws Exception {
        TRVideoStream tRVideoStream = this.getVideoStreamDevice(this.objTRBaseCommand.getByte(64));
        if (tRVideoStream != null) {
            return tRVideoStream.TRRSP_New_Video_Mode(this.objTRBaseCommand);
        }
        return false;
    }

    private boolean TRRSP_Cache() throws Exception {
        TRVideoStream tRVideoStream = this.getVideoStreamDevice(this.objTRBaseCommand.getByte(8));
        if (tRVideoStream != null) {
            return tRVideoStream.TRRSP_Cache(this.objTRBaseCommand);
        }
        return false;
    }

    private boolean TRRSP_Compressed() throws Exception {
        TRVideoStream tRVideoStream = this.getVideoStreamDevice(this.objTRBaseCommand.getByte(14));
        if (tRVideoStream != null) {
            return tRVideoStream.TRRSP_Compressed(this.objTRBaseCommand);
        }
        return false;
    }

    private boolean TRRSP_Cell_Data() throws Exception {
        TRVideoStream tRVideoStream = this.getVideoStreamDevice(this.objTRBaseCommand.getByte(6));
        if (tRVideoStream != null) {
            return tRVideoStream.TRRSP_Cell_Data(this.objTRBaseCommand);
        }
        return false;
    }

    private boolean TRRSP_Bitplane() throws Exception {
        TRVideoStream tRVideoStream = this.getVideoStreamDevice(this.objTRBaseCommand.getByte(13));
        if (tRVideoStream != null) {
            return tRVideoStream.TRRSP_Bitplane(this.objTRBaseCommand);
        }
        return false;
    }

    private boolean TRRSP_Packed() {
        return false;
    }

    private boolean TRRSP_Video_Marker() throws Exception {
        TRRSP_VIDEO_MARKER_DATA tRRSP_VIDEO_MARKER_DATA = new TRRSP_VIDEO_MARKER_DATA(this.objTRBaseCommand.toByteArray());
        TRVideoStream tRVideoStream = this.getVideoStreamDevice(tRRSP_VIDEO_MARKER_DATA.getDeviceID());
        short s = 0;
        byte by = 0;
        if (tRVideoStream != null && tRVideoStream.isConnected()) {
            s = tRRSP_VIDEO_MARKER_DATA.getCount();
            by = tRRSP_VIDEO_MARKER_DATA.getDeviceID();
            tRRSP_VIDEO_MARKER_DATA.setCommand((byte)48);
            tRRSP_VIDEO_MARKER_DATA.setCmdLength((short)7);
            tRRSP_VIDEO_MARKER_DATA.setCount(s);
            tRRSP_VIDEO_MARKER_DATA.setDeviceID(by);
            this.sendTRCmd(this.objTRBaseCommand);
        }
        return true;
    }

    private boolean TRRSP_KB_Status() {
        TRRSP_KB_STATUS_DATA tRRSP_KB_STATUS_DATA = new TRRSP_KB_STATUS_DATA(this.objTRBaseCommand.toByteArray());
        TRVideoStream tRVideoStream = null;
        tRVideoStream = this.objSvrID.getProtocolVersion() < 16 ? this.getVideoStreamDevice((byte)0) : this.getVideoStreamDevice(tRRSP_KB_STATUS_DATA.getDeviceID());
        if (tRVideoStream != null) {
            return tRVideoStream.TRRSP_KB_Status(this.objTRBaseCommand);
        }
        return false;
    }

    private boolean TRRSP_Receive_Serial() throws Exception {
        TRSerialStream tRSerialStream = this.getSerialStreamDevice(this.objTRBaseCommand.getByte(6));
        if (tRSerialStream != null) {
            return tRSerialStream.TRRSP_Receive_Serial(this.objTRBaseCommand);
        }
        return false;
    }

    private boolean externalResponse() throws Exception {
        RSPDEF rSPDEF = this.objRSPDEFList[this.objTRBaseCommand.getCommand()];
        Method method = rSPDEF.getExtMethod();
        if (method != null) {
            Object[] objectArray = new Object[]{this.objTRBaseCommand, rSPDEF.getUserData()};
            Object object = method.invoke(rSPDEF.getExtObject(), objectArray);
            return true;
        }
        return true;
    }

    public void setExtRspHandler(int n, Method method, Object object, Object object2, Method method2, Object object3, Object object4) {
        try {
            if (n >= 48) {
                return;
            }
            if (method2 != null) {
                method2 = this.objRSPDEFList[n].getExtMethod();
            }
            if (object3 != null) {
                object3 = this.objRSPDEFList[n].getExtObject();
            }
            if (object4 != null) {
                object4 = this.objRSPDEFList[n].getUserData();
            }
            this.objRSPDEFList[n].setExtMethod(method);
            this.objRSPDEFList[n].setExtObject(object);
            this.objRSPDEFList[n].setUserData(object2);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    private boolean TRRSP_RDM_Event() {
        TRRSP_RDM_EVENT_DATA tRRSP_RDM_EVENT_DATA = new TRRSP_RDM_EVENT_DATA(this.objTRBaseCommand.toByteArray());
        this.event(tRRSP_RDM_EVENT_DATA.getRdmEventData());
        return true;
    }

    public String databaseRequest(String string) {
        return this.databaseRequest(string, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String databaseRequest(String string, int n) {
        String string2;
        block26: {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(200, 17, "Database request: " + string);
            }
            if (this.isKx2Device) {
                int n2 = 0;
                int n3 = 0;
                String string3 = "";
                boolean bl = false;
                Object object = this.objWriteCR;
                synchronized (object) {
                    block25: {
                        if (n > 0) {
                            bl = true;
                            try {
                                n2 = this.objReadSock.getSoTimeout();
                                n3 = this.objWriteSock.getSoTimeout();
                                this.objReadSock.setSoTimeout(n);
                                this.objWriteSock.setSoTimeout(n);
                            }
                            catch (SocketException socketException) {
                                if (RRCLogger.logEnabled) {
                                    RRCLogger.log(200, 17, "Unable to adjust timeout period for databaseRequest(String cmd, int timeout).\n\tSocketException: " + socketException.getMessage() + "\nWill attempt to perform command with default timeout.");
                                }
                                bl = false;
                            }
                        }
                        if (!this.writeCSCMessage(string.getBytes(), 0)) {
                            if (RRCLogger.logEnabled) {
                                RRCLogger.log(100, 17, "Writing database request to device failed!");
                            }
                        } else {
                            byte[] byArray = this.readCSCMessage(0, 0);
                            RRCLogger.log(500, 17, byArray);
                            if (byArray != null) {
                                String string4 = string3 = new String(byArray);
                                if (string3.contains("<Password>")) {
                                    Pattern pattern = Pattern.compile("<Password>(.*)</Password>");
                                    Matcher matcher = pattern.matcher(string4);
                                    string4 = matcher.replaceAll("<Password>########</Password>");
                                }
                                RRCLogger.log(200, 17, "Result of database request: " + string4);
                                string4 = null;
                            } else {
                                RRCLogger.log(200, 17, "No ping response for device " + this.objReadSock);
                                throw new IllegalStateException("Null response for CSC");
                            }
                        }
                        if (bl) {
                            try {
                                this.objReadSock.setSoTimeout(n2);
                                this.objReadSock.setSoTimeout(n3);
                            }
                            catch (SocketException socketException) {
                                if (!RRCLogger.logEnabled) break block25;
                                RRCLogger.log(100, 17, "DANGER: Unable to restore original socket timeout in databaseRequest(String cmd, int timeout)!!\n\tSocketException: " + socketException.getMessage());
                            }
                        }
                    }
                }
                if (string3.indexOf(0) < 1) {
                    return string3;
                }
                return string3.substring(0, string3.indexOf(0));
            }
            boolean bl = false;
            string2 = null;
            TRCMD_DATABASE_REQUEST_DATA tRCMD_DATABASE_REQUEST_DATA = new TRCMD_DATABASE_REQUEST_DATA(4108);
            int n4 = string.length() + 1;
            try {
                tRCMD_DATABASE_REQUEST_DATA.setCommand((byte)54);
                tRCMD_DATABASE_REQUEST_DATA.setCmdLength((short)(18 + n4));
                tRCMD_DATABASE_REQUEST_DATA.setTotalSize(n4);
                tRCMD_DATABASE_REQUEST_DATA.setSize(n4);
                tRCMD_DATABASE_REQUEST_DATA.setOffset(0);
                tRCMD_DATABASE_REQUEST_DATA.setCompFlags((short)0);
                System.arraycopy(string.getBytes(), 0, tRCMD_DATABASE_REQUEST_DATA.toByteArray(), 18, n4 - 1);
                tRCMD_DATABASE_REQUEST_DATA.toByteArray()[18 + n4] = 0;
                TRCMDWAIT tRCMDWAIT = new TRCMDWAIT();
                bl = this.sendTRCmdExxx(tRCMD_DATABASE_REQUEST_DATA, tRCMDWAIT, 36, true, n);
                if (bl) {
                    if (tRCMD_DATABASE_REQUEST_DATA.getCommand() == 36) {
                        int n5;
                        TRRSP_DATABASE_RESPONSE_DATA tRRSP_DATABASE_RESPONSE_DATA = new TRRSP_DATABASE_RESPONSE_DATA(tRCMD_DATABASE_REQUEST_DATA.toByteArray());
                        byte[] byArray = tRCMDWAIT.getAvailableData();
                        for (n5 = 0; byArray != null && byArray.length > n5 && byArray[n5] != 0; ++n5) {
                        }
                        if (n5 > 0) {
                            string2 = new String(byArray, 0, n5);
                        }
                    }
                    bl = this.processResponse(bl, tRCMD_DATABASE_REQUEST_DATA);
                }
                this.syncTRCmd();
            }
            catch (Exception exception) {
                if (!RRCLogger.logEnabled) break block26;
                RRCLogger.logException(exception);
            }
        }
        return string2;
    }

    public boolean isConnected() {
        return this.boolConnected;
    }

    public boolean isAuthenticated() {
        return this.boolAuthenticated;
    }

    public boolean isMultiVideoStream() {
        return this.objSvrID.getProtocolVersion() >= 16 && this.objSvrID.getNumVideoDevices() > 1;
    }

    public boolean isDialUpConnection() {
        return this.objComm.getConnType() == 1;
    }

    public TRConnection enumCTRConnection(int n) {
        if (objConnVectorList.get(n) != null) {
            return (TRConnection)objConnVectorList.get(n);
        }
        return null;
    }

    public boolean getResolvedComm(TRLIB_COMM tRLIB_COMM) {
        return this.boolCommResolved;
    }

    public boolean ChangeUserPassword(TRCMD_CHANGE_PASSWORD_DATA tRCMD_CHANGE_PASSWORD_DATA) {
        TRCOMMAND tRCOMMAND = new TRCOMMAND(4108);
        boolean bl = false;
        try {
            tRCMD_CHANGE_PASSWORD_DATA.setCommand((byte)39);
            tRCMD_CHANGE_PASSWORD_DATA.setCmdLength((short)52);
            System.arraycopy(tRCMD_CHANGE_PASSWORD_DATA.toByteArray(), 0, tRCOMMAND.toByteArray(), 0, 52);
            bl = this.sendTRCmdEx(tRCOMMAND, true, null, 2);
            tRCMD_CHANGE_PASSWORD_DATA.fromByteArray(tRCOMMAND.toByteArray());
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return this.processResponse(bl, tRCOMMAND);
    }

    @Override
    public void notify(int n, int n2) {
    }

    public void event(byte[] byArray) {
    }

    @Override
    public boolean login(TRSRVR_SERVER_ID tRSRVR_SERVER_ID, TRLIB_USERINFO tRLIB_USERINFO, boolean bl) {
        return false;
    }

    @Override
    public boolean loginChallenge(int n, byte[] byArray, byte[] byArray2) {
        return false;
    }

    public boolean enumVideoDevices(int n, TRSRVR_CHANNEL_DATA tRSRVR_CHANNEL_DATA) {
        boolean bl = false;
        try {
            TRCMD_ENUM_VIDEO_DEVICES_DATA tRCMD_ENUM_VIDEO_DEVICES_DATA = new TRCMD_ENUM_VIDEO_DEVICES_DATA(4108);
            tRCMD_ENUM_VIDEO_DEVICES_DATA.setCommand((byte)8);
            tRCMD_ENUM_VIDEO_DEVICES_DATA.setCmdLength((short)9);
            tRCMD_ENUM_VIDEO_DEVICES_DATA.setDeviceID((byte)n);
            tRCMD_ENUM_VIDEO_DEVICES_DATA.setFlags(0);
            bl = this.sendTRCmdEx(tRCMD_ENUM_VIDEO_DEVICES_DATA, true, null, 26);
            if (bl) {
                tRSRVR_CHANNEL_DATA.populate(tRCMD_ENUM_VIDEO_DEVICES_DATA.toByteArray());
            }
            return this.processResponse(bl, tRCMD_ENUM_VIDEO_DEVICES_DATA);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return false;
        }
    }

    public void syncTRCmd() {
        this.log("=syncTRCmd=" + this.objSyncMonitor);
        this.objSyncMonitor.notifying();
    }

    public boolean setDataItemByte(byte by, short s, byte by2) {
        boolean bl = false;
        try {
            TRCMD_SET_DATA_ITEM_DATA tRCMD_SET_DATA_ITEM_DATA = new TRCMD_SET_DATA_ITEM_DATA(4108);
            tRCMD_SET_DATA_ITEM_DATA.setCommand((byte)51);
            tRCMD_SET_DATA_ITEM_DATA.setCmdLength((short)9);
            tRCMD_SET_DATA_ITEM_DATA.setDeviceID(by);
            tRCMD_SET_DATA_ITEM_DATA.setItemID(s);
            tRCMD_SET_DATA_ITEM_DATA.setItemdata(by2);
            bl = this.sendTRCmd(tRCMD_SET_DATA_ITEM_DATA);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return bl;
    }

    public void setExtRspHandler() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addVideoStream(TRVideoStream tRVideoStream) {
        Object object = this.objCriticalSection;
        synchronized (object) {
            this.objVSList.addElement(tRVideoStream);
        }
        return true;
    }

    public TRVideoStream getVideoStreamDevice(byte by) {
        int n = by & 0xFF;
        if (n < 256) {
            return this.objVSArray[n];
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setVideoStreamDevice(TRVideoStream tRVideoStream, byte by) {
        boolean bl = false;
        int n = by & 0xFF;
        try {
            Object object = this.objCriticalSection;
            synchronized (object) {
                if (n < 256 && (this.objVSArray[n] == null || tRVideoStream == null)) {
                    bl = true;
                    this.objVSArray[n] = tRVideoStream;
                }
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeVideoStream(TRVideoStream tRVideoStream) {
        int n = 0;
        try {
            Object object = this.objCriticalSection;
            synchronized (object) {
                for (n = 0; n < 256; ++n) {
                    if (this.objVSArray[n] != tRVideoStream) continue;
                    this.objVSArray[n] = null;
                }
                this.objVSList.remove(tRVideoStream);
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addSerialStream(TRSerialStream tRSerialStream) {
        Object object = this.objCriticalSection;
        synchronized (object) {
            this.objSSList.addElement(tRSerialStream);
        }
        return true;
    }

    public TRSerialStream getSerialStreamDevice(byte by) {
        int n = by & 0xFF;
        if (n < 32) {
            return this.objSSArray[n];
        }
        if (n == 255) {
            return this.objSSAdmin;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setSerialStreamDevice(TRSerialStream tRSerialStream, byte by) {
        int n = by & 0xFF;
        boolean bl = false;
        try {
            Object object = this.objCriticalSection;
            synchronized (object) {
                if (n < 32) {
                    if (this.objSSArray[n] == null || tRSerialStream == null) {
                        bl = true;
                        this.objSSArray[n] = tRSerialStream;
                    }
                } else if (n == 255 && (this.objSSAdmin == null || tRSerialStream == null)) {
                    bl = true;
                    this.objSSAdmin = tRSerialStream;
                }
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeSerialStream(TRSerialStream tRSerialStream) {
        block7: {
            try {
                Object object = this.objCriticalSection;
                synchronized (object) {
                    Object var3_4 = null;
                    int n = 0;
                    for (n = 0; n < 32; ++n) {
                        if (this.objSSArray[n] != tRSerialStream) continue;
                        this.objSSArray[n] = null;
                    }
                    if (this.objSSAdmin == tRSerialStream) {
                        this.objSSAdmin = null;
                    }
                    this.objSSList.remove(tRSerialStream);
                }
            }
            catch (Exception exception) {
                if (!RRCLogger.logEnabled) break block7;
                RRCLogger.log(200, 4, "TRConnection:removeSerialStream() " + exception.getMessage());
            }
        }
    }

    private void getRandomData(byte[] byArray, int n) {
        long l = 0L;
        Random random = null;
        int n2 = 53;
        l = System.currentTimeMillis();
        l = l ^ this.lLastSeed ^ this.lLastSeed << 16;
        random = new Random(l);
        for (int i = 0; i < n; ++i) {
            byArray[i] = (byte)(random.nextLong() >> 1 & 0xFFL ^ (long)n2);
            if (((n2 = (int)((byte)(n2 + (byte)random.nextLong()))) & 1) == 0) continue;
            n2 = (byte)(n2 + 7);
        }
        random.nextBytes(byArray);
        this.lLastSeed = l;
    }

    public long getTimeLastMsg() {
        return this.lTimeLastMsg;
    }

    public long getTimeLastMsgSent() {
        return this.lTimeLastMsgSent;
    }

    public boolean getAuthenticated() {
        return this.boolAuthenticated;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean read(byte[] byArray, int n, int n2) {
        int n3 = 0;
        int n4 = 0;
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "---------- Beginning new Read: offset = " + n + "   bytes to read = " + n2);
        }
        try {
            while (n2 > 0) {
                if (!this.boolDisconnecting && this.boolSocketOK) {
                    try {
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(400, 4, "***read***available=" + this.objBufferedInputStream.available() + "***iCount" + n2 + "***iOffset" + n);
                        }
                        n3 = this.objBufferedInputStream.read(byArray, n, n2);
                    }
                    catch (SocketTimeoutException socketTimeoutException) {
                        break;
                    }
                    catch (Exception exception) {
                        byte[] byArray2 = new byte[n2];
                        this.objBufferedInputStream.read(byArray2, n, n2);
                        byArray2 = null;
                        RRCLogger.log(100, 4, byArray2);
                        this.dbLog(System.currentTimeMillis() + " read socket Error  -------------" + exception.getMessage());
                        if (!this.boolDisconnecting && !this.boolOrderlyShutdown && RRCLogger.logEnabled) {
                            RRCLogger.logException(exception);
                        }
                        break;
                    }
                    if (n3 == -1) {
                        this.dbLog(System.currentTimeMillis() + " read connection closed -------------");
                        break;
                    } else {
                        n += n3;
                        n4 += n3;
                        n2 -= n3;
                        continue;
                    }
                }
                TRConnection.setLastError(0x20000001);
                break;
            }
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        if (n2 != 0) {
            this.boolConnected = false;
            this.boolSocketOK = false;
            return false;
        }
        this.iDataIn += n4;
        n -= n4;
        if (this.boolNeedRC4) {
            if (this.readRC4 == null) {
                this.readRC4 = new RC4Cipher(this.RC4Key);
            }
            this.readRC4.decrypt(byArray, n, byArray, n, n4);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean write(byte[] byArray, int n) {
        boolean bl = true;
        if (byArray != null) {
            try {
                String string = new String(byArray);
                if (string.indexOf("<CSC_Auth UserName") == -1) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(400, 4, "---------- Beginning new Write: data length = " + byArray.length + " requested # of bytes to write: " + n + "\n\tdata: " + string);
                    }
                } else if (RRCLogger.logEnabled) {
                    RRCLogger.log(400, 4, "Csc_Auth Sent.");
                }
                string = null;
                Object object = this.objWriteCR;
                synchronized (object) {
                    if (this.boolNeedRC4) {
                        this.log("Inside WRITE RC4");
                        if (this.writeRC4 == null) {
                            this.writeRC4 = new RC4Cipher(this.RC4Key);
                        }
                        this.writeRC4.encrypt(byArray, 0, byArray, 0, n);
                    }
                    this.lTimeLastMsgSent = System.currentTimeMillis();
                    if (!this.boolDisconnecting && this.boolSocketOK) {
                        this.iDataOut += n;
                        try {
                            if (RRCLogger.logEnabled) {
                                RRCLogger.log(400, 4, "***write***outputstream.size=" + this.objFilterOutputStream.size() + "***iCount" + n + " byInData length " + byArray.length);
                            }
                            this.objFilterOutputStream.write(byArray, 0, n);
                            this.objFilterOutputStream.flush();
                        }
                        catch (Exception exception) {
                            this.dbLog(System.currentTimeMillis() + " send error in write -------------" + exception.getMessage());
                            if (RRCLogger.logEnabled) {
                                RRCLogger.logException(exception);
                            }
                            bl = false;
                            this.boolConnected = false;
                            this.boolSocketOK = false;
                        }
                    } else {
                        TRConnection.setLastError(0x20000001);
                        bl = false;
                    }
                }
                this.log("**written " + n);
            }
            catch (Exception exception) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.logException(exception);
                }
                bl = false;
            }
        } else if (RRCLogger.logEnabled) {
            RRCLogger.log(400, 4, "Bytes to be sent inData is NULL");
        }
        return bl;
    }

    private boolean sinkRFPData(int n) {
        boolean bl = false;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        if (n < 0) {
            n &= 0xFFFF;
        }
        byte[] byArray = new byte[n];
        while (n > 0) {
            n2 = n > 65535 ? 65535 : n;
            n4 = this.readFromStream(byArray, 0, n2, RFP);
            n3 = n2;
            n -= n3;
        }
        byArray = null;
        if (n4 < 0) {
            return false;
        }
        return bl;
    }

    private boolean sinkData(int n) {
        boolean bl = false;
        int n2 = 0;
        int n3 = 0;
        if (n < 0) {
            n &= 0xFFFF;
        }
        byte[] byArray = new byte[n];
        while (n > 0) {
            n2 = n > 65535 ? 65535 : n;
            bl = this.read(byArray, 0, n2);
            n3 = n2;
            n -= n3;
        }
        byArray = null;
        return bl;
    }

    public void dbLog(String string) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(200, 4, string);
        }
    }

    public void dbLog2(String string) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(200, 4, string);
        }
    }

    public void dbLog3(String string) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(200, 4, string);
        }
    }

    private void dbLogCmd(boolean bl, TRCOMMAND tRCOMMAND) {
        if (!RRCLogger.shouldLog(300, 4)) {
            return;
        }
        if (bl) {
            switch (tRCOMMAND.getCommand()) {
                case 16: 
                case 20: 
                case 31: 
                case 32: 
                case 41: 
                case 42: 
                case 43: 
                case 45: {
                    if (RRCLogger.shouldLog(400, 4)) break;
                    return;
                }
            }
            this.dbLog2(System.currentTimeMillis() + " In  " + Integer.toHexString(tRCOMMAND.getCommand()) + " " + Integer.toHexString(tRCOMMAND.getPktID()) + " " + Integer.toHexString(tRCOMMAND.getCmdLength()));
        } else {
            switch (tRCOMMAND.getCommand()) {
                case 17: 
                case 18: 
                case 19: 
                case 24: 
                case 48: {
                    if (RRCLogger.shouldLog(400, 4)) break;
                    return;
                }
            }
            this.dbLog2(System.currentTimeMillis() + " Out " + Integer.toHexString(tRCOMMAND.getCommand()) + " " + Integer.toHexString(tRCOMMAND.getPktID()) + " " + Integer.toHexString(tRCOMMAND.getCmdLength()));
        }
    }

    public boolean processResponse(boolean bl, TRCOMMAND tRCOMMAND) {
        block4: {
            try {
                TRRSP_NACK_DATA tRRSP_NACK_DATA = new TRRSP_NACK_DATA(tRCOMMAND.toByteArray());
                if (!bl) {
                    return false;
                }
                if (tRRSP_NACK_DATA.getCommand() == 3) {
                    TRConnection.setLastError(tRRSP_NACK_DATA.getError() - 0x20001000);
                    return false;
                }
            }
            catch (Exception exception) {
                if (!RRCLogger.logEnabled) break block4;
                RRCLogger.log(200, 4, "TRConnection:processResponse() " + exception.getMessage());
            }
        }
        return true;
    }

    private boolean svrHndshakeAftSocketCreation(TRCMD_HANDSHAKE_ACK_DATA tRCMD_HANDSHAKE_ACK_DATA, boolean bl) throws Exception {
        boolean bl2 = false;
        if (bl) {
            tRCMD_HANDSHAKE_ACK_DATA.setSignature(1203501117);
        } else {
            this.objWriteSock = this.objReadSock;
            this.objWriteSSLSocket = this.objReadSSLSocket;
            tRCMD_HANDSHAKE_ACK_DATA.setSignature(1547540642);
        }
        tRCMD_HANDSHAKE_ACK_DATA.setChecksum(this.CalcHandshakeChecksum(tRCMD_HANDSHAKE_ACK_DATA));
        this.createFilterOutputStream();
        bl2 = this.write(tRCMD_HANDSHAKE_ACK_DATA.toByteArray(), 34);
        if (!bl2) {
            return false;
        }
        if (!bl) {
            this.objWriteSock = null;
            this.objWriteSSLSocket = null;
        }
        return bl2;
    }

    private void log(String string) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 4, "CONNECTION::" + string);
        }
    }

    public void myassert(boolean bl) {
    }

    public TRLIB_COMM getResolvedComm() {
        return this.objComm;
    }

    public long getPermissions() {
        return this.lPermissions;
    }

    public TRSRVR_SERVER_ID getServerID() {
        return this.objSvrID;
    }

    public int getProtocolVersion() {
        return this.objSvrID.getProtocolVersion();
    }

    public static XMLParser getXmlParserInstance() {
        if (xmlParser == null) {
            xmlParser = new XMLParser();
        }
        return xmlParser;
    }

    public boolean checkForKX2Device(String string) {
        boolean bl;
        block28: {
            bl = false;
            try {
                NamedNodeMap namedNodeMap;
                Object object;
                Object object2;
                xmlParser = TRConnection.getXmlParserInstance();
                Document document = xmlParser.getDocument(string);
                if (document == null) {
                    return bl;
                }
                Element element = document.getDocumentElement();
                document = null;
                NodeList nodeList = element.getElementsByTagName("Device");
                if (nodeList == null) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "No <Device>");
                    }
                    return bl;
                }
                if (nodeList.getLength() > 1) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "No <Device> length");
                    }
                    return bl;
                }
                Node node = nodeList.item(0);
                NamedNodeMap namedNodeMap2 = node.getAttributes();
                if (namedNodeMap2 == null) {
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "No <Device> attributes");
                    }
                    return bl;
                }
                Node node2 = namedNodeMap2.getNamedItem("ProductCode");
                Node node3 = namedNodeMap2.getNamedItem("Type");
                this.deviceType = node3.getNodeValue();
                Node node4 = namedNodeMap2.getNamedItem("ProductName");
                Node node5 = namedNodeMap2.getNamedItem("Model");
                Node node6 = namedNodeMap2.getNamedItem("Version");
                this.productVersion = node6.getNodeValue();
                if (node4 != null && node5 != null && node4.getNodeValue().toUpperCase().equals("KX101") && node5.getNodeValue().toUpperCase().equals("KX_KIM")) {
                    this.setKX101G1Device(true);
                    return bl;
                }
                if (node2 != null && node3 != null) {
                    object2 = node2.getNodeValue();
                    object = node3.getNodeValue();
                    if ((((String)object2).toUpperCase().equals("HK8") || ((String)object2).toUpperCase().equals("HK9") || ((String)object2).toUpperCase().equals("HKA") || ((String)object2).toUpperCase().equals("HKB") || ((String)object2).toUpperCase().equals("HKC") || ((String)object2).toUpperCase().equals("HKD") || ((String)object2).toUpperCase().equals("HKE") || ((String)object2).toUpperCase().equals("HKF") || ((String)object2).toUpperCase().equals("HKG") || ((String)object2).toUpperCase().equals("HKH") || ((String)object2).toUpperCase().equals("HKI") || ((String)object2).toUpperCase().equals("QAW") || ((String)object2).toUpperCase().equals("AAK") || ((String)object2).toUpperCase().equals("AAY") || ((String)object2).toUpperCase().equals("AE2") || ((String)object2).toUpperCase().equals("AE1") || ((String)object2).toUpperCase().equals("HKL") || ((String)object2).toUpperCase().equals("HKK") || ((String)object2).toUpperCase().equals("HKJ")) && ((String)object).equals("Dominion_KX2") || "Dominion_KX2_101".equals(object) || "Dominion_KSX2".equals(object) || "Dominion_LX".equals(object)) {
                        bl = true;
                        if (RRCLogger.logEnabled) {
                            RRCLogger.log(300, 4, "Connecting to a G2 Device.");
                        }
                    }
                    if ((((String)object2).toUpperCase().equals("AAK") || ((String)object2).toUpperCase().equals("AAY")) && ((String)object).equals("Dominion_KX2_101")) {
                        this.setKX101G2Device(true);
                    }
                }
                object2 = element.getElementsByTagName("Protocol");
                object = null;
                if (object2.getLength() > 0) {
                    object = object2.item(0);
                }
                if ((namedNodeMap = object.getAttributes()) != null) {
                    Node node7 = namedNodeMap.getNamedItem("id");
                    String string2 = null;
                    if (node7 != null) {
                        string2 = node7.getNodeValue();
                    }
                    if (string2 != null && string2.equals("RDM")) {
                        Node node8 = namedNodeMap.getNamedItem("RequireTLS");
                        String string3 = null;
                        if (node8 != null) {
                            string3 = node8.getNodeValue();
                        }
                        if (string3 != null && string3.equals("1.0")) {
                            this.setUseTLS(true);
                        }
                    }
                }
            }
            catch (NumberFormatException numberFormatException) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, numberFormatException.getMessage());
                }
            }
            catch (DOMException dOMException) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, dOMException.getMessage());
                }
            }
            catch (IOException iOException) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, iOException.getMessage());
                }
            }
            catch (SAXException sAXException) {
                if (RRCLogger.logEnabled) {
                    RRCLogger.log(300, 4, sAXException.getMessage());
                }
            }
            catch (ParserConfigurationException parserConfigurationException) {
                if (!RRCLogger.logEnabled) break block28;
                RRCLogger.log(300, 4, parserConfigurationException.getMessage());
            }
        }
        return bl;
    }

    public boolean isPingDevice() {
        return this.pingDevice;
    }

    public void setPingDevice(boolean bl) {
        this.pingDevice = bl;
    }

    public String getRdmSessionID() {
        return this.sessionId;
    }

    public String getRdmSessionKey() {
        return this.sessionKey;
    }

    public boolean isKX101G1Device() {
        return this.isKX101G1Device;
    }

    public void setKX101G1Device(boolean bl) {
        this.isKX101G1Device = bl;
    }

    public boolean isKX101G2Device() {
        return this.isKX101G2Device;
    }

    public void setKX101G2Device(boolean bl) {
        this.isKX101G2Device = bl;
    }

    protected TRLIB_COMM getTrlib_comm() {
        return this.objComm;
    }

    public boolean isGDMode() {
        return this.gdMode;
    }

    public void setGDMode(boolean bl) {
        this.gdMode = bl;
    }

    public void setUseTLS(boolean bl) {
        this.useTLS = bl;
    }

    public boolean getUseTLS() {
        return this.useTLS;
    }

    public static void main(String[] stringArray) {
        block8: {
            TRLIB_COMM tRLIB_COMM = new TRLIB_COMM();
            TRLIB_USERINFO tRLIB_USERINFO = new TRLIB_USERINFO();
            TRLIB_REFERRAL_COMM tRLIB_REFERRAL_COMM = new TRLIB_REFERRAL_COMM();
            boolean bl = false;
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getByName("10.0.0.229");
                tRLIB_COMM.setIpAddress(inetAddress.hashCode());
                tRLIB_COMM.setConnType(2);
                tRLIB_COMM.setServerName("IPR229".getBytes());
                tRLIB_COMM.setDnsName("IPR229".getBytes());
                tRLIB_COMM.setIpPort(5000);
                tRLIB_COMM.setFindBy(0);
                tRLIB_USERINFO.setName("admin".getBytes());
                tRLIB_USERINFO.setPassword(Constants.DEFAULT_PASSWORD.getBytes());
                tRLIB_REFERRAL_COMM.setVersion(1);
                TRConnection tRConnection = new TRConnection();
                boolean bl2 = tRConnection.connect(tRLIB_COMM, tRLIB_USERINFO, null, null);
                int n = 0;
                while (!tRConnection.getAuthenticated()) {
                    Thread.sleep(500L);
                    if (n > 20) {
                        bl2 = false;
                        break;
                    }
                    ++n;
                }
                if (!bl2) {
                    System.out.println("**************+Authentication Failure **********");
                    System.exit(1);
                } else {
                    System.out.println("************** Connect Successful **********");
                }
                TRCOMMAND tRCOMMAND = new TRCOMMAND();
                tRCOMMAND.setCommand((byte)3);
                tRCOMMAND.setCmdLength((short)4);
                tRCOMMAND.setPktID((byte)0);
                if (!tRConnection.sendTRCmd(tRCOMMAND)) {
                    tRConnection.disConnect();
                    if (RRCLogger.logEnabled) {
                        RRCLogger.log(300, 4, "PING Error");
                    }
                }
                String string = "<Database><Get><Select>/System</Select><Nodes>*</Nodes><SubNodes>*</SubNodes></Get></Database>";
                System.out.println("**************+Database Request **********");
                System.out.println(tRConnection.databaseRequest(string));
                Thread.sleep(6000L);
                tRConnection.disConnect();
                System.out.println("**************+Disconnect Successful **********");
            }
            catch (Exception exception) {
                if (!RRCLogger.logEnabled) break block8;
                RRCLogger.logException(exception);
            }
        }
    }

    static {
        byNull = new byte[]{0};
        EVENT = "Event";
        RFP = "RFP";
    }
}

