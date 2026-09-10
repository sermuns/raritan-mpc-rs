/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.CancelRFPOperationCommand;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.util.config.ConfigurationManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javaclientlib.clientlib.RFPClient;
import javaclientlib.tr.TRCOMMAND;
import javaclientlib.utils.RRCLogger;

public class RRCRFPClient
extends RFPClient {
    private static RRCRFPClient instance;
    private RRCScreenContext scrContext;
    private RaritanPropertyResourceBundle bundle = RaritanResourceBundle.getResourceBundle();
    private final String tempCertificateFilename = "cert_temp.rfp";
    private final String tempKeyFilename = "key_temp.rfp";
    private final int bufferSize = 1024;
    private boolean showProgress = false;
    private boolean isKX = false;
    private static Object mutexForInstantiation;

    private RRCRFPClient() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RRCRFPClient getInstance() {
        Object object = mutexForInstantiation;
        synchronized (object) {
            if (instance == null) {
                instance = new RRCRFPClient();
            }
        }
        return instance;
    }

    public void setScreenContext(ScreenContext screenContext) {
        this.scrContext = (RRCScreenContext)screenContext;
        ArrayList arrayList = (ArrayList)this.scrContext.getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() > 0) {
            Device device = (Device)arrayList.get(0);
            DeviceConnector deviceConnector = device.getDeviceConnector();
            try {
                this.setTRConnection(deviceConnector);
            }
            catch (Exception exception) {
                RRCLogger.logException(exception);
            }
        }
    }

    public void setShowProgress(boolean bl) {
        this.showProgress = bl;
    }

    @Override
    public boolean TRRSP_RFP_Message_Glue(TRCOMMAND tRCOMMAND, Object object) {
        return super.TRRSP_RFP_Message_Glue(tRCOMMAND, object);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean updateLDAPCertificate(File file) {
        boolean bl = false;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            byte[] byArray = new byte[1024];
            inputStream = ConfigurationManager.getResourceInputStream(this.scrContext.getClass(), this.scrContext.getApplicationProperty("ldapcert.template.file"));
            fileOutputStream = new FileOutputStream("cert_temp.rfp");
            int n = inputStream.read(byArray);
            while (n > 0) {
                fileOutputStream.write(byArray, 0, n);
                n = inputStream.read(byArray);
            }
            fileInputStream = new FileInputStream(file);
            n = fileInputStream.read(byArray);
            while (n > 0) {
                fileOutputStream.write(byArray, 0, n);
                n = fileInputStream.read(byArray);
            }
            bl = this.send("cert_temp.rfp", 0, false);
        }
        catch (Exception exception) {
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                }
                catch (IOException iOException) {}
            }
            if (fileOutputStream != null) {
                try {
                    new File("key_temp.rfp").deleteOnExit();
                    fileOutputStream.close();
                    fileOutputStream = null;
                }
                catch (IOException iOException) {}
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                    fileInputStream = null;
                }
                catch (IOException iOException) {}
            }
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean updateLDAPKey(File file) {
        boolean bl = false;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            byte[] byArray = new byte[1024];
            inputStream = ConfigurationManager.getResourceInputStream(this.scrContext.getClass(), this.scrContext.getApplicationProperty("ldapkey.template.file"));
            fileOutputStream = new FileOutputStream("key_temp.rfp");
            int n = inputStream.read(byArray);
            while (n > 0) {
                fileOutputStream.write(byArray, 0, n);
                n = inputStream.read(byArray);
            }
            fileInputStream = new FileInputStream(file);
            n = fileInputStream.read(byArray);
            while (n > 0) {
                fileOutputStream.write(byArray, 0, n);
                n = fileInputStream.read(byArray);
            }
            bl = this.send("key_temp.rfp", 0, false);
        }
        catch (Exception exception) {
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                }
                catch (IOException iOException) {}
            }
            if (fileOutputStream != null) {
                try {
                    new File("key_temp.rfp").deleteOnExit();
                    fileOutputStream.close();
                    fileOutputStream = null;
                }
                catch (IOException iOException) {}
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                    fileInputStream = null;
                }
                catch (IOException iOException) {}
            }
        }
        return bl;
    }

    @Override
    public void progress(int n, int n2) {
        if (this.showProgress && !this.isKX) {
            int n3 = (int)((double)n2 / (double)n * 100.0);
            ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setRFPOperationProgress(n3);
            if (n3 == 0) {
                n3 = 1;
            }
            String string = n3 - 1 + "% " + this.bundle.getString("rfpclient.progresslabel.text");
            this.scrContext.getLogger().logStatus(string);
        }
    }

    @Override
    public boolean receive(String string, String string2, int n) throws Exception {
        CancelRFPOperationCommand cancelRFPOperationCommand = new CancelRFPOperationCommand(this.scrContext);
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).initRFPProgressComponents(false);
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setCancelRFPOperationCommand(cancelRFPOperationCommand);
        boolean bl = super.receive(string, string2, n);
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).hideRFPProgressComponents();
        return bl;
    }

    @Override
    public boolean send(String string, int n, boolean bl) throws Exception {
        this.isKX = bl;
        CancelRFPOperationCommand cancelRFPOperationCommand = new CancelRFPOperationCommand(this.scrContext);
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).initRFPProgressComponents(bl);
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).setCancelRFPOperationCommand(cancelRFPOperationCommand);
        boolean bl2 = false;
        bl2 = super.send(string, n, bl);
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).hideRFPProgressComponents();
        return bl2;
    }

    @Override
    public void cancel() {
        super.cancel();
        ((RRCStatusBar)this.scrContext.getPanelMediator().getStatusPanel()).hideRFPProgressComponents();
    }

    public String showError(int n) {
        RRCLogger.log(300, -1, "RFP Error Received: " + n + ". Hex Value:0x" + Integer.toHexString(n));
        String string = null;
        switch (n) {
            case 0x20000001: {
                string = this.bundle.getString("trlib.error.not.connected");
                break;
            }
            case 0x20000002: {
                string = this.bundle.getString("trlib.error.invalid.parameter");
                break;
            }
            case 0x20000003: {
                string = this.bundle.getString("trlib.error.response.too.big");
                break;
            }
            case 0x20000004: {
                string = this.bundle.getString("trlib.error.bad.server.response");
                break;
            }
            case 0x20000005: {
                string = this.bundle.getString("trlib.error.io.cancel");
                break;
            }
            case 0x20000006: {
                string = this.bundle.getString("trlib.error.server.not.found");
                break;
            }
            case 0x20000007: {
                string = this.bundle.getString("trlib.error.server.busy");
                break;
            }
            case 0x20000008: {
                string = this.bundle.getString("trlib.error.socket");
                break;
            }
            case 0x20000009: {
                string = this.bundle.getString("trlib.error.dialup.failed");
                break;
            }
            case 0x2000000A: {
                string = this.bundle.getString("trlib.error.browser.error");
                break;
            }
            case 0x20001001: {
                string = this.bundle.getString("tr.error.invalid.param");
                break;
            }
            case 0x20001002: {
                string = this.bundle.getString("tr.error.invalid.command");
                break;
            }
            case 536875011: {
                string = this.bundle.getString("tr.error.user.already.exists");
                break;
            }
            case 536875012: {
                string = this.bundle.getString("tr.error.user.not.found");
                break;
            }
            case 536875013: {
                string = this.bundle.getString("tr.error.permission.denied");
                break;
            }
            case 536875014: {
                string = this.bundle.getString("tr.error.public.view.denied");
                break;
            }
            case 536875015: {
                string = this.bundle.getString("tr.error.no.resources");
                break;
            }
            case 536875016: {
                string = this.bundle.getString("tr.error.internal.error");
                break;
            }
            case 536875017: {
                string = this.bundle.getString("tr.error.console.busy");
                break;
            }
            case 536875018: {
                string = this.bundle.getString("tr.error.device.busy");
                break;
            }
            case 536875019: {
                string = this.bundle.getString("tr.error.server.busy");
                break;
            }
            case 536875021: {
                string = this.bundle.getString("tr.error.file.not.found");
                break;
            }
            case 13: {
                string = this.bundle.getString("tr.error.file.not.found");
                break;
            }
            case 0x2002000: {
                string = this.bundle.getString("unknown.error.client");
                break;
            }
            case 0x2002007: {
                string = this.bundle.getString("trlib.error.rfp.error.model.mismatch");
                break;
            }
            case 0x2002008: {
                string = this.bundle.getString("trlib.error.rfp.error.version.mismatch");
                break;
            }
            case 0x2002009: {
                string = this.bundle.getString("trlib.error.rfp.error.timeout");
                break;
            }
            case 0x200200A: {
                string = this.bundle.getString("trlib.error.rfp.error.cancel");
                break;
            }
            case 0x200200B: {
                string = this.bundle.getString("trlib.error.rfp.error.requset.format");
                break;
            }
            case 0x200200C: {
                string = this.bundle.getString("trlib.error.rfp.error.file.not.found");
                break;
            }
            case 0x200200D: {
                string = this.bundle.getString("trlib.error.rfp.error.internal");
                break;
            }
            case 33562648: {
                string = this.bundle.getString("trlib.error.rfp.pack.ack");
                break;
            }
            case 0x2002001: 
            case 0x2002002: {
                string = this.bundle.getString("trlib.error.rfp.error.corrupt.file");
                break;
            }
            case 0x2002003: 
            case 0x2002004: 
            case 0x2002005: 
            case 0x2002006: {
                string = this.bundle.getString("trlib.error.rfp.failure.occurred");
                break;
            }
            case 0x200200E: 
            case 0x200200F: 
            case 0x2002010: 
            case 0x2002011: 
            case 0x2002012: 
            case 33562643: 
            case 33562644: 
            case 33562645: 
            case 33562646: 
            case 33562647: {
                string = this.bundle.getString("trlib.error.rfp.internal.error");
                break;
            }
            case 33562650: {
                string = this.bundle.getString("trlib.error.rfp.file.wrong.model");
                break;
            }
            case 33562651: {
                string = this.bundle.getString("trlib.error.rfp.file.wrong.type");
                break;
            }
            case 33562652: 
            case 33562653: {
                string = this.bundle.getString("trlib.error.rfp.file.missing.info");
                break;
            }
            case 33562654: {
                string = this.bundle.getString("trlib.error.rfp.file.not.compatible");
                break;
            }
            case 33562655: 
            case 0x2002020: 
            case 33562724: 
            case 33562725: 
            case 0x2002066: 
            case 33562729: 
            case 33562730: 
            case 33562731: 
            case 33562732: 
            case 33562733: 
            case 33562734: 
            case 33562735: 
            case 0x2002070: 
            case 33562737: 
            case 0x2002072: {
                string = this.bundle.getString("trlib.error.rfp.problem.with.file");
                break;
            }
            case 33562727: 
            case 33563124: 
            case 33563125: 
            case 33563126: 
            case 33563127: 
            case 33563128: 
            case 33563129: {
                string = this.bundle.getString("trlib.error.rfp.restore.failed.problem.with.file");
                break;
            }
            case 33562728: {
                string = this.bundle.getString("trlib.error.rfp.backup.problem.with.file");
                break;
            }
            case 33563624: {
                string = this.bundle.getString("trlib.error.rfp.skip.file");
                break;
            }
            case 0x2002021: {
                string = this.bundle.getString("trlib.error.rfp.out.of.memory");
                break;
            }
            default: {
                string = this.bundle.getString("unknown.error");
            }
        }
        string = "[Error 0x" + Integer.toHexString(n) + "]: " + string;
        int n2 = 50;
        int n3 = 7;
        int n4 = string.length();
        if (n4 <= n2 + n3) {
            return string;
        }
        int n5 = 0;
        int n6 = string.indexOf(32, n2);
        StringBuffer stringBuffer = new StringBuffer(string.substring(0, n6));
        while (n6 > -1) {
            n5 = n6;
            if ((n6 = string.indexOf(32, n6 + n2)) + n3 >= n4) {
                n6 = -1;
            }
            stringBuffer.append("\n  " + string.substring(n5, n6 < 0 ? n4 : n6).trim());
        }
        return stringBuffer.toString();
    }

    static {
        mutexForInstantiation = new Object();
    }
}

