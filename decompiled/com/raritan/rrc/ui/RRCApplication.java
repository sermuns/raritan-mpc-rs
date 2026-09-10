/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.MonitorSettingsHandler;
import com.raritan.rrc.ui.RRCMain;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ExitSystemCommand;
import com.raritan.rrc.ui.commands.ShowTargetScreenResolutionCommand;
import com.raritan.rrc.ui.components.FullScreenToolBar;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.ui.AbstractUIManager;
import com.raritan.tools.ui.State;
import com.raritan.tools.ui.components.CommonPopups;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import javaclientlib.utils.RRCLogger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import nn.pp.core.JVMVersionInfo;

public class RRCApplication
extends JFrame
implements AbstractUIManager {
    private static final long serialVersionUID = 6415390041361678243L;
    private static RRCMain.InnerWindowAdapter listenerAdapter;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private RRCMain main;
    private RRCScreenContext context;
    private static RaritanPropertyResourceBundle bundle;
    private List<JDialog> disposedShells = new Vector<JDialog>();

    public RRCApplication(String string, RRCScreenContext rRCScreenContext, GraphicsConfiguration graphicsConfiguration) {
        super(string, graphicsConfiguration);
        Object object;
        this.setDefaultCloseOperation(0);
        this.context = rRCScreenContext;
        this.context.setApplication(this);
        this.context.setState(State.INIT);
        this.main = new RRCMain(this, this.context);
        this.main.loadScreenManager();
        bundle = this.main.getBundle();
        this.setTitle(string);
        if (System.getProperty("java.version").compareTo("1.6.0") < 0) {
            object = new JOptionPane(bundle.getString("Application.error.notSupported.java"), 0, -1, null, new Object[]{bundle.getString("basescreen.command.ok.text")});
            JDialog jDialog = ((JOptionPane)object).createDialog(null, bundle.getString("optionpane.error.title"));
            jDialog.setModal(true);
            jDialog.setVisible(true);
            System.exit(0);
        }
        listenerAdapter = this.main.getInnerWindowAdapter();
        listenerAdapter.setCommand(new ExitSystemCommand(this.context));
        listenerAdapter.setConfirmation(true);
        this.addWindowListener(listenerAdapter);
        this.pack();
        this.setSize(800, 600);
        object = rRCScreenContext;
        SwingUtilities.invokeLater(new Runnable((RRCScreenContext)object){
            final /* synthetic */ RRCScreenContext val$scrCtx;
            {
                this.val$scrCtx = rRCScreenContext;
            }

            @Override
            public void run() {
                RRCApplication.this.setVisible(true);
                if (!RRCApplication.this.main.getScreenManager().show()) {
                    CommonPopups.showErrorMessage(bundle.getString("error.connect.port"), RRCApplication.this.getContentPane(), this.val$scrCtx, bundle.getString("optionpane.error.title"));
                    RRCApplication.this.disconnect();
                }
                if (RRCLogger.logEnabled) {
                    CommonPopups.showWarningDialog(bundle.getString("optionpane.warning.tite"), bundle.getString("View.warning.log.on"), RRCApplication.this, RRCApplication.this.context);
                }
            }
        });
        RRCLogger.log(200, 4, JVMVersionInfo.getSystemProperties().toString());
        RRCLogger.log(200, 4, "Multi-platform Client Version: " + bundle.getString("about.label.build"));
        RRCLogger.log(200, 4, "Max memory: " + Runtime.getRuntime().maxMemory());
        RRCLogger.log(200, 4, "Disable Direct Draw: " + System.getProperty("sun.java2d.noddraw"));
    }

    @Override
    public Component getOptionComponent() {
        return this;
    }

    @Override
    public URL getIconBase() {
        return this.main.getIconBase();
    }

    @Override
    public Container getContentPane() {
        Container container = this.main.getActiveContentPane();
        return container != null ? container : super.getContentPane();
    }

    @Override
    public Container getContentPaneForPort(Port port) {
        Container container = this.main.getContentPaneForPort(port);
        return container != null ? container : this.getContentPane();
    }

    @Override
    public boolean isStandalone() {
        return true;
    }

    @Override
    public synchronized void changeScreen(boolean bl, JInternalFrame jInternalFrame, Port port, AbstractUIManager.FullScreenTarget fullScreenTarget) {
        block6: {
            try {
                if (bl) {
                    this.main.enterFullScreen(jInternalFrame, port, fullScreenTarget);
                } else {
                    this.main.returnFromFullScreen();
                }
            }
            catch (InternalError internalError) {
                try {
                    this.context.getPanelMediator().showStatusPanel(true);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                ShowTargetScreenResolutionCommand showTargetScreenResolutionCommand = new ShowTargetScreenResolutionCommand(this.context);
                if (!showTargetScreenResolutionCommand.isExecutable()) break block6;
                showTargetScreenResolutionCommand.execute();
            }
        }
    }

    public static void main(String[] stringArray) {
        try {
            UIManager.setLookAndFeel("com.raritan.plaf.RaritanLookAndFeel");
        }
        catch (Exception exception) {
            // empty catch block
        }
        Logger logger = Logger.getLogger("java.util.prefs");
        logger.setUseParentHandlers(false);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        for (int i = 0; i < stringArray.length; ++i) {
            int n = stringArray[i].indexOf("=");
            hashMap.put(stringArray[i].substring(0, n), stringArray[i].substring(n + 1, stringArray[i].length()));
        }
        String string = null;
        string = hashMap.containsKey("title") ? (String)hashMap.get("title") : "Raritan Multi-Platform Client";
        RRCScreenContext rRCScreenContext = (RRCScreenContext)RRCScreenContext.getNewInstance(hashMap);
        MonitorSettingsHandler.init(rRCScreenContext);
        RRCApplication rRCApplication = new RRCApplication(string, rRCScreenContext, MonitorSettingsHandler.getInstance().getGraphicsConfiguration());
    }

    @Override
    public Cursor getBlankCursor() {
        return this.main.getBlankCursor();
    }

    @Override
    public Cursor getDefaultCursor() {
        return this.main.getDefaultCursor();
    }

    @Override
    public GraphicsDevice getGraphicsDevice() {
        return this.main.getGraphicsDevice();
    }

    @Override
    public void setGraphicsDevice(String string) {
        this.main.setGraphicsDevice(string);
    }

    @Override
    public void disconnect() {
        Device device;
        ArrayList arrayList = (ArrayList)this.main.getScreenManager().getScreenContext().getSelectedDevicesObservable().getComponent();
        if (arrayList != null && (device = (Device)arrayList.get(0)) != null && device.isConnected()) {
            device.disconnect();
        }
        System.exit(0);
    }

    @Override
    public FullScreenToolBar getFSToolBar(JInternalFrame jInternalFrame) {
        return this.main.getFSToolBar(jInternalFrame);
    }

    @Override
    public void setFrameVisible(boolean bl) {
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                for (JDialog jDialog : RRCApplication.this.disposedShells) {
                    jDialog.dispose();
                }
                RRCApplication.this.disposedShells.clear();
            }
        };
        if (bl) {
            SwingUtilities.invokeLater(runnable);
        } else {
            runnable.run();
        }
        super.setVisible(bl);
        if (bl) {
            this.getContentPane().invalidate();
            this.getContentPane().validate();
        }
    }

    @Override
    public String getAppId() {
        return this.main.getAppId();
    }

    @Override
    public void addDisposedShell(JDialog jDialog) {
        if (!this.disposedShells.contains(jDialog)) {
            this.disposedShells.add(jDialog);
        }
    }

    static {
        bundle = null;
    }
}

