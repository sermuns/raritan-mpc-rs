/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.util.modem;

import com.raritan.rrc.util.OS;
import com.raritan.rrc.util.modem.ModemConnector;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;
import javaclientlib.utils.RRCLogger;

public class PPPDConnector
extends ModemConnector {
    private static final int STATUS_DIALING_WAIT_FOR_CALLBACK = 17;
    private static final String DIAL_LAUNCH_SCRIPT_NAME = "launch-dial.sh";
    private static final String CHECKPERMS_SCRIPT_NAME = "checkperms.sh";
    private static final String DIAL_SCRIPT_NAME = "dial-up.sh";
    private static final String DIAL_CALLBACK_SCRIPT_NAME = "dial-up-callback.sh";
    private static final String UPDATE_PPPD_SCRIPT_NAME = "update-pppd.sh";
    private static final String MODEM_DETECT_SCRIPT_NAME = "find-modem.sh";
    private static final String MODEM_HANGUP_SCRIPT_NAME = "hangup.sh";
    private static final String CHAT_COMMAND_NAME = "chat";
    private static final int BUFFER_SIZE = 1024;
    private String serverIPAddress = null;
    private String launchDialScriptName = null;
    private String checkPermsScriptName = null;
    private String dialScriptFilename = null;
    private String updatePPPDScriptFilename = null;
    private String chatCommandFilename = null;
    private String hangupScriptFilename = null;
    private String dialCallbackScriptFilename = null;
    private static final int EXIT_CODE_PPPD_SUCCESS = 0;
    private static final int EXIT_CODE_PPPD_FATAL_ERROR = 1;
    private static final int EXIT_CODE_PPPD_OPTIONS_ERROR = 2;
    private static final int EXIT_CODE_PPPD_PERMISSIONS_ERROR = 3;
    private static final int EXIT_CODE_PPPD_NOT_SUPPORTED = 4;
    private static final int EXIT_CODE_PPPD_SIGNALED = 5;
    private static final int EXIT_CODE_PPPD_PORT_NOT_LOCKABLE = 6;
    private static final int EXIT_CODE_PPPD_PORT_NOT_OPENABLE = 7;
    private static final int EXIT_CODE_PPPD_CONNECT_SCRIPT_FAILED = 8;
    private static final int EXIT_CODE_PPPD_PTY_ERROR = 9;
    private static final int EXIT_CODE_PPPD_NEGO_FAILED = 10;
    private static final int EXIT_CODE_PPPD_SERVER_AUTH_FAILED = 11;
    private static final int EXIT_CODE_PPPD_DISCONNECTED_ON_IDLE = 12;
    private static final int EXIT_CODE_PPPD_DISCONNECTED_ON_CONNECT_TIME = 13;
    private static final int EXIT_CODE_PPPD_CALLBACK = 14;
    private static final int EXIT_CODE_PPPD_DISCONNECTED_ON_FAILED_ECHO = 15;
    private static final int EXIT_CODE_PPPD_MODEM_HUNGUP = 16;
    private static final int EXIT_CODE_PPPD_SERIAL_LOOPBACK = 17;
    private static final int EXIT_CODE_PPPD_INIT_SCRIPT_FAILED = 18;
    private static final int EXIT_CODE_PPPD_AUTH_FAILED = 19;
    private static final int EXIT_CODE_ALREADY_CONNECTED = 201;
    private static final int EXIT_CODE_NOT_ENOUNG_ARGUMENTS = 202;
    private static final int EXIT_CODE_OS_NOT_SUPPORTED = 203;
    private static final int EXIT_CODE_CHAT_NOT_FOUND = 204;
    private static final int EXIT_CODE_OPTIONSFILE_MISSING = 100;
    private static final int EXIT_CODE_INSUFFICIENT_PERMISSIONS = 101;
    private static final int EXIT_CODE_PPPD_NOT_IN_PATH = 102;
    public static final int ERROR_CHECK_CALLBACK_NUMBER = 1001;
    public static final int ERROR_CHECK_PPPDVER = 1002;
    public static final int ERROR_CHECK_PPPD_SETUID = 1003;
    public static final int ERROR_CHECK_OPTIONS_FILE = 1004;
    public static final int ERROR_CHECK_PERMISSIONS = 1005;
    public static final int ERROR_CHECK_PPPD_PATH = 1006;
    private static PPPDConnector sConnectionOwner = null;
    private Process process;
    private Object HANGUP_SERIALIZER = new Object();
    private RaritanPropertyResourceBundle bundle;

    private static synchronized void setCurrentOwner(PPPDConnector pPPDConnector) {
        sConnectionOwner = pPPDConnector;
    }

    private static synchronized boolean isCurrentOwner(PPPDConnector pPPDConnector) {
        return sConnectionOwner == null ? false : sConnectionOwner == pPPDConnector;
    }

    private static synchronized boolean isOwned() {
        return sConnectionOwner != null;
    }

    private synchronized void setProcess(Process process) {
        this.process = process;
    }

    private synchronized Process getProcess() {
        return this.process;
    }

    public static String getDefaultModem() {
        OS oS = OS.getCurrent();
        if (oS == OS.LINUX) {
            return "/dev/ttyS0";
        }
        if (oS == OS.MAC) {
            return "/dev/cu.modem";
        }
        if (oS == OS.SOLARIS) {
            return "/dev/ttya";
        }
        return "";
    }

    public PPPDConnector(Locale locale) {
        if (locale != null) {
            this.bundle = RaritanResourceBundle.getResourceBundle(locale);
        }
    }

    private static Process exec(String string) throws IOException {
        return Runtime.getRuntime().exec(string);
    }

    private static Process execAndWait(String string) throws IOException {
        Process process = PPPDConnector.exec(string);
        try {
            process.waitFor();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        return process;
    }

    private static void deleteFile(String string) {
        File file;
        if (string != null && (file = new File(string)).exists()) {
            file.delete();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String copyScript(String string, boolean bl) {
        InputStream inputStream = this.getClass().getResourceAsStream("/" + string);
        if (inputStream == null) {
            RRCLogger.log(300, -1, "Unable to locate " + string + " in jar file");
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            Object object;
            File file = File.createTempFile("javarrc_", "_" + string);
            if (bl) {
                fileOutputStream = new FileOutputStream(file);
                object = new byte[1024];
                int n = inputStream.read((byte[])object);
                while (n > 0) {
                    fileOutputStream.write((byte[])object, 0, n);
                    n = inputStream.read((byte[])object);
                }
            } else {
                bufferedWriter = new BufferedWriter(new FileWriter(file));
                object = bufferedReader.readLine();
                while (object != null) {
                    bufferedWriter.write((String)object);
                    bufferedWriter.newLine();
                    object = bufferedReader.readLine();
                }
            }
            PPPDConnector.execAndWait("chmod 755 " + file.getAbsolutePath());
            object = file.getAbsolutePath();
            return object;
        }
        catch (IOException iOException) {
            RRCLogger.log(300, -1, iOException, "Exception on copying file " + string);
            String string2 = null;
            return string2;
        }
        finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }
        }
    }

    @Override
    public long rasConnect(String string, String string2) throws Exception {
        return this.rasConnectWithIdentity(string, string2, "PPP", "PPPpw", false);
    }

    @Override
    public long rasConnectWithIdentity(String string, String string2, String string3, String string4) throws Exception {
        if (null == string3 || "".equals(string3)) {
            RRCLogger.log(300, 4, "Username invalid");
            throw new IllegalArgumentException("Username invalid");
        }
        if (null == string4 || "".equals(string4)) {
            RRCLogger.log(300, 4, "Null or empty password not valid");
            throw new IllegalArgumentException("Null or empty password not valid");
        }
        return this.rasConnectWithIdentity(string, string2, string3, string4, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long rasConnectWithIdentity(String string, String string2, String string3, String string4, boolean bl) throws Exception {
        boolean bl2;
        if (PPPDConnector.isOwned()) {
            RRCLogger.log(300, -1, "Already in use by an existing connection");
            return 633L;
        }
        if (!this.prepareScripts()) {
            RRCLogger.log(300, -1, "Error on preparing scripts for dialing");
            return 1L;
        }
        int n = this.checkPermissions();
        if (n != 0) {
            return n;
        }
        boolean bl3 = bl2 = OS.getCurrent() == OS.MAC;
        if (bl2) {
            this.chatCommandFilename = "/usr/sbin/chat";
        }
        String string5 = " -p " + string + " -m " + string2 + " -upd " + this.updatePPPDScriptFilename + (bl2 ? " -chat " + this.chatCommandFilename : "");
        String string6 = this.launchDialScriptName + " " + this.dialScriptFilename + string5 + (bl ? " -callback 0" : "");
        String string7 = this.launchDialScriptName + " " + this.dialScriptFilename + string5;
        String string8 = this.launchDialScriptName + " " + this.dialCallbackScriptFilename + string5;
        n = -1;
        try {
            String string9;
            if (bl && !bl2 && OS.getCurrent() != OS.SOLARIS) {
                n = this.launchPPPD(string6, string3, string4);
                RRCLogger.log(300, -1, "command : " + string6 + " Exit code : " + n);
                switch (n) {
                    case 0: {
                        this.readServerIP();
                        n = 0;
                        long l = n;
                        return l;
                    }
                    case 2: {
                        RRCLogger.log(300, -1, "Probably callback option not supported");
                        this.destroyShellProcess();
                        string9 = string7;
                        break;
                    }
                    case 3: {
                        long l = 1003L;
                        return l;
                    }
                    case 6: {
                        long l = 633L;
                        return l;
                    }
                    case 7: {
                        long l = 633L;
                        return l;
                    }
                    case 8: {
                        long l = 651L;
                        return l;
                    }
                    case 14: {
                        RRCLogger.log(300, -1, "Callback negotiated");
                        String string10 = "dial.windialstatus.17";
                        if (this.bundle != null) {
                            string10 = this.bundle.getString(string10);
                        }
                        this.fireDialStatusMsg(string10);
                        this.destroyShellProcess();
                        string9 = string8;
                        break;
                    }
                    case 10: 
                    case 16: {
                        long l = 1001L;
                        return l;
                    }
                    case 19: {
                        long l = 691L;
                        return l;
                    }
                    case 201: {
                        long l = 633L;
                        return l;
                    }
                    case 204: {
                        RRCLogger.log(300, -1, "chat script used to talk to modem not found");
                    }
                    default: {
                        long l = 1L;
                        return l;
                    }
                }
            } else {
                string9 = string7;
            }
            n = this.launchPPPD(string9, string3, string4);
            RRCLogger.log(300, -1, "command : " + string9 + " Exit code : " + n);
            switch (n) {
                case 0: {
                    this.readServerIP();
                    n = 0;
                    long l = n;
                    return l;
                }
                case 3: {
                    long l = 1003L;
                    return l;
                }
                case 6: {
                    long l = 633L;
                    return l;
                }
                case 7: {
                    long l = 633L;
                    return l;
                }
                case 8: {
                    long l = 651L;
                    return l;
                }
                case 10: 
                case 16: {
                    if (bl) {
                        long l = 1002L;
                        return l;
                    }
                    long l = 1L;
                    return l;
                }
                case 19: {
                    long l = 691L;
                    return l;
                }
                case 201: {
                    long l = 633L;
                    return l;
                }
                case 204: {
                    RRCLogger.log(300, -1, "chat script used to talk to modem not found");
                }
            }
            long l = 1L;
            return l;
        }
        catch (IOException iOException) {
            RRCLogger.log(300, -1, iOException, "Exception on modem connect");
            long l = 1L;
            return l;
        }
        catch (InterruptedException interruptedException) {
            RRCLogger.log(300, -1, interruptedException, "Exception on modem connect");
            long l = 1L;
            return l;
        }
        finally {
            if (n == 0) {
                PPPDConnector.setCurrentOwner(this);
                this.destroyShellProcess();
                this.cleanUpScripts();
            } else {
                this.rasHangUp();
            }
        }
    }

    private void destroyShellProcess() {
        Process process = this.getProcess();
        if (process != null) {
            process.destroy();
            this.setProcess(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void readServerIP() throws IOException {
        Process process = this.getProcess();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        try {
            this.serverIPAddress = bufferedReader.readLine();
        }
        finally {
            bufferedReader.close();
        }
    }

    private int launchPPPD(String string, String string2, String string3) throws IOException, InterruptedException {
        Process process;
        try {
            process = PPPDConnector.exec(string);
        }
        catch (IOException iOException) {
            RRCLogger.log(300, -1, iOException, "Exception on launching pppd process");
            throw iOException;
        }
        this.setProcess(process);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        try {
            bufferedWriter.write(string2);
            bufferedWriter.newLine();
            bufferedWriter.write(string3);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch (IOException iOException) {
            RRCLogger.log(300, -1, iOException, "Exception on passing username and password to pppd process");
            throw iOException;
        }
        finally {
            try {
                bufferedWriter.close();
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        return process.waitFor();
    }

    private boolean prepareScripts() {
        this.launchDialScriptName = this.copyScript(DIAL_LAUNCH_SCRIPT_NAME, false);
        this.checkPermsScriptName = this.copyScript(CHECKPERMS_SCRIPT_NAME, false);
        this.dialScriptFilename = this.copyScript(DIAL_SCRIPT_NAME, false);
        this.dialCallbackScriptFilename = this.copyScript(DIAL_CALLBACK_SCRIPT_NAME, false);
        this.updatePPPDScriptFilename = this.copyScript(UPDATE_PPPD_SCRIPT_NAME, false);
        this.hangupScriptFilename = this.copyScript(MODEM_HANGUP_SCRIPT_NAME, false);
        return this.dialScriptFilename != null && this.checkPermsScriptName != null && this.launchDialScriptName != null && this.dialCallbackScriptFilename != null && this.updatePPPDScriptFilename != null && this.hangupScriptFilename != null;
    }

    private int checkPermissions() throws IOException {
        Process process = null;
        try {
            process = PPPDConnector.exec(this.checkPermsScriptName);
        }
        catch (IOException iOException) {
            RRCLogger.log(300, -1, iOException, "Exception on launching check permissions script");
            throw iOException;
        }
        try {
            int n = process.waitFor();
            RRCLogger.log(300, -1, "Return value of check permissions script : " + n);
            switch (n) {
                case 100: {
                    return 1004;
                }
                case 101: {
                    return 1005;
                }
                case 102: {
                    return 1006;
                }
            }
            return n;
        }
        catch (InterruptedException interruptedException) {
            RRCLogger.log(300, -1, interruptedException, "Exception on executing check permissions script");
            return 1;
        }
    }

    @Override
    public long rasEnumConnections() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long rasEnumDevices() {
        return 1L;
    }

    @Override
    public long rasEnumEntries() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String rasGetDeviceName(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String rasGetDeviceType(long l) {
        return "modem";
    }

    @Override
    public String rasGetEntryName(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String rasGetServerIP() {
        return this.serverIPAddress;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void rasHangUp() {
        Object object = this.HANGUP_SERIALIZER;
        synchronized (object) {
            if (!PPPDConnector.isOwned()) {
                this.hangUp();
            } else if (PPPDConnector.isCurrentOwner(this)) {
                this.hangUp();
                PPPDConnector.setCurrentOwner(null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void hangUp() {
        try {
            this.hangupScriptFilename = this.copyScript(MODEM_HANGUP_SCRIPT_NAME, false);
            if (this.hangupScriptFilename != null) {
                PPPDConnector.exec(this.hangupScriptFilename);
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
            this.destroyShellProcess();
        }
        catch (IOException iOException) {
            RRCLogger.logException(iOException);
        }
        finally {
            if (this.hangupScriptFilename != null) {
                PPPDConnector.deleteFile(this.hangupScriptFilename);
                this.hangupScriptFilename = null;
            }
        }
        this.cleanUpScripts();
    }

    private void cleanUpScripts() {
        PPPDConnector.deleteFile(this.dialScriptFilename);
        this.dialScriptFilename = null;
        PPPDConnector.deleteFile(this.checkPermsScriptName);
        this.checkPermsScriptName = null;
        PPPDConnector.deleteFile(this.updatePPPDScriptFilename);
        this.updatePPPDScriptFilename = null;
        PPPDConnector.deleteFile(this.launchDialScriptName);
        PPPDConnector.deleteFile(this.dialCallbackScriptFilename);
        if (OS.getCurrent() == OS.MAC) {
            this.chatCommandFilename = null;
        }
    }
}

