/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.MonitorSettingsHandler;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commonInits;
import com.raritan.rrc.ui.components.CommandMenuItemCache;
import com.raritan.rrc.ui.components.FullScreenFrame;
import com.raritan.rrc.ui.components.FullScreenToolBar;
import com.raritan.rrc.ui.filterPrintStream;
import com.raritan.rrc.ui.screens.RRCScreenManager;
import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.ConfirmableCommandInterface;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.AbstractUIManager;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.util.ImageHolder;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.MemoryImageSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javaclientlib.utils.RRCLogger;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import nn.pp.core.Platform;

public class RRCMain {
    private RRCScreenContext context;
    private static final filterPrintStream fps = new filterPrintStream();
    private AbstractUIManager ui;
    private RaritanPropertyResourceBundle bundle;
    MonitorSettingsHandler mh;
    private RRCScreenManager screenManager;
    private InnerWindowAdapter listenerAdapter = new InnerWindowAdapter();
    private GraphicsDevice gd;
    private GraphicsConfiguration graphicsConfig;
    private boolean dontShowMainFrame = false;
    private Cursor defaultCursor;
    private Cursor blankCursor;
    private String appId = null;
    private HashMap<JInternalFrame, FullScreenFrame> fullScreenFrames = new HashMap();

    public RRCMain(AbstractUIManager abstractUIManager, RRCScreenContext rRCScreenContext) {
        Object object;
        this.ui = abstractUIManager;
        this.context = rRCScreenContext;
        String string = rRCScreenContext.getApplicationProperty("application.resource.file");
        rRCScreenContext.setLocale(abstractUIManager.getLocale());
        if (abstractUIManager.getLocale() == Locale.US || abstractUIManager.getLocale() == Locale.UK) {
            string = rRCScreenContext.getApplicationProperty("application.resource.file") + "_" + Locale.US.getLanguage() + ".properties";
        } else {
            string = string + "_" + rRCScreenContext.getLocale().toString() + ".properties";
            System.out.println(string);
            object = this.getClass().getResource(string);
            System.out.println("Checking for existence of:" + object);
            if (object == null) {
                System.out.println("Properties File Doesn't Exist, defaulting to English");
                RRCLogger.log(200, 4, "Resource File for Locale " + abstractUIManager.getLocale() + " doesn't exist defaulting to English");
                string = rRCScreenContext.getApplicationProperty("application.resource.file") + "_" + Locale.US.getLanguage() + ".properties";
            }
        }
        RaritanResourceBundle.initResourceBundle(string, this.getClass(), rRCScreenContext.getLocale());
        this.bundle = RaritanResourceBundle.getResourceBundle(rRCScreenContext.getLocale());
        this.initFileChooserLabels();
        commonInits.initializePseudoConstants(this.bundle, fps, rRCScreenContext);
        commonInits.initializeCommonItems(rRCScreenContext);
        this.mh = MonitorSettingsHandler.getInstance();
        this.mh.determineConfiguredGraphicsConfiguration();
        this.gd = this.mh.getGraphicsDevice();
        this.graphicsConfig = this.mh.getGraphicsConfiguration();
        object = this.bundle.getString("help.window.title");
        ImageHolder imageHolder = new ImageHolder(rRCScreenContext);
        Image image = imageHolder.getImage(this.bundle.getString("raritan.icon"));
        abstractUIManager.setIconImage(image);
        if (abstractUIManager.getLocale() == Locale.US || abstractUIManager.getLocale() == Locale.TRADITIONAL_CHINESE || abstractUIManager.getLocale() == Locale.SIMPLIFIED_CHINESE || abstractUIManager.getLocale() == Locale.JAPAN) {
            rRCScreenContext.setHelpManager(RRCScreenContext.createHelpManager((String)object, image, rRCScreenContext.getLocale()));
        } else {
            rRCScreenContext.setHelpManager(RRCScreenContext.createHelpManager((String)object, image, Locale.US));
        }
    }

    public void loadScreenManager() {
        this.screenManager = RRCScreenManager.getNewInstance(this.context);
        this.context.addObserver(this.screenManager);
        this.screenManager.setApplication(this.ui);
        this.screenManager.setFullScreenStrategy();
        CommandMenuItemCache.init();
    }

    public void dispose() {
        if (this.screenManager != null) {
            this.screenManager.destroy();
            this.screenManager = null;
        }
    }

    public RaritanPropertyResourceBundle getBundle() {
        return this.bundle;
    }

    public GraphicsDevice getGraphicsDevice() {
        return this.gd;
    }

    public void setGraphicsDevice(String string) {
        this.mh.setGraphicsDevice(string);
        this.gd = this.mh.getGraphicsDevice();
        this.graphicsConfig = this.mh.getGraphicsConfiguration();
    }

    public GraphicsConfiguration getGraphicsConfig() {
        return this.graphicsConfig;
    }

    public InnerWindowAdapter getInnerWindowAdapter() {
        return this.listenerAdapter;
    }

    public RRCScreenManager getScreenManager() {
        return this.screenManager;
    }

    public synchronized void enterFullScreen(JInternalFrame jInternalFrame, Port port, AbstractUIManager.FullScreenTarget fullScreenTarget) {
        this.context.getPanelMediator().showStatusPanel(false);
        MonitorSettingsHandler monitorSettingsHandler = MonitorSettingsHandler.getInstance();
        GraphicsConfiguration graphicsConfiguration = this.graphicsConfig;
        if (fullScreenTarget == AbstractUIManager.FullScreenTarget.SECONDARY) {
            graphicsConfiguration = monitorSettingsHandler.getSecondaryGraphicsConfiguration(1);
            if (graphicsConfiguration == null) {
                return;
            }
        } else if (fullScreenTarget == AbstractUIManager.FullScreenTarget.PRIMARY) {
            if (monitorSettingsHandler.getMonitorCount() == 1) {
                fullScreenTarget = AbstractUIManager.FullScreenTarget.SINGLE;
            } else {
                graphicsConfiguration = monitorSettingsHandler.getPrimaryGraphicsConfiguration();
            }
        }
        this.listenerAdapter.setConfirmation(false);
        FullScreenFrame fullScreenFrame = new FullScreenFrame(this.context, this, this.screenManager, this.bundle, graphicsConfiguration, jInternalFrame, port, fullScreenTarget);
        this.fullScreenFrames.put(jInternalFrame, fullScreenFrame);
        if (!this.dontShowMainFrame) {
            this.ui.setFrameVisible(false);
        }
        fullScreenFrame.setVisible(true);
        this.context.getMenuBar().setMenuItemTargetScreenResolution(true);
        this.context.getMenuBar().enableMenusInFullScreenMode(false);
        this.context.getMainScreenMediator().enableWindowMenu(false);
    }

    public synchronized void returnFromFullScreen() {
        if (this.fullScreenFrames.size() == 0) {
            return;
        }
        this.context.getPanelMediator().showStatusPanel(true);
        this.addMenuBar();
        this.listenerAdapter.setConfirmation(true);
        for (FullScreenFrame fullScreenFrame : this.fullScreenFrames.values()) {
            fullScreenFrame.setVisible(false);
            fullScreenFrame.dispose();
        }
        this.fullScreenFrames.clear();
        if (!this.dontShowMainFrame) {
            this.ui.setFrameVisible(true);
        }
        System.runFinalization();
        System.gc();
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                RRCMain.this.context.resetDefaultFocus();
            }
        });
        this.context.getMenuBar().setMenuItemTargetScreenResolution(false);
        this.context.getMenuBar().enableMenusInFullScreenMode(true);
        this.context.getMainScreenMediator().enableWindowMenu(true);
    }

    public synchronized void returnOnDisconnect() {
        this.listenerAdapter.setConfirmation(true);
        if (this.screenManager.getFullScreenStrategy() == 0) {
            this.gd.setFullScreenWindow(null);
        }
        this.context.getMenuBar().setMenuItemTargetScreenResolution(false);
        this.context.getMenuBar().enableMenusInFullScreenMode(true);
        this.context.getMainScreenMediator().enableWindowMenu(true);
        for (FullScreenFrame fullScreenFrame : this.fullScreenFrames.values()) {
            fullScreenFrame.dispose();
        }
        this.fullScreenFrames.clear();
        if (!this.dontShowMainFrame) {
            this.ui.setFrameVisible(true);
        }
        this.context.getMainScreenMediator().selectStatusBarView(this.context.getMainScreenMediator().getCheckStatusBarMenuItems().get(0).isSelected());
        System.runFinalization();
        System.gc();
    }

    public synchronized void returnOnSecondaryDisconnect(JInternalFrame jInternalFrame) {
        FullScreenFrame fullScreenFrame = this.fullScreenFrames.remove(jInternalFrame);
        if (fullScreenFrame != null) {
            fullScreenFrame.dispose();
        }
    }

    public synchronized void updateInternalFrameReference(GraphicsDevice graphicsDevice, Port port, Port port2, JInternalFrame jInternalFrame, JInternalFrame jInternalFrame2, Border border, JComponent jComponent, Dimension dimension, Rectangle rectangle) {
        Object object;
        HashMap hashMap = new HashMap();
        if (port != null) {
            block0: for (Port object2 : port.getSecondaryPorts()) {
                for (Map.Entry entry : this.fullScreenFrames.entrySet()) {
                    if (((FullScreenFrame)entry.getValue()).getPort() != object2) continue;
                    hashMap.put(((FullScreenFrame)entry.getValue()).getGraphicsConfiguration(), entry.getKey());
                    continue block0;
                }
            }
        }
        for (Port port3 : port2.getSecondaryPorts()) {
            Container container;
            object = this.mh.getSecondaryGraphicsConfiguration(graphicsDevice, port3.getMonitorIndex());
            if (object == null) continue;
            JInternalFrame jInternalFrame3 = (JInternalFrame)hashMap.remove(object);
            if (jInternalFrame3 != null && (container = this.fullScreenFrames.get(jInternalFrame3)) != null) {
                ((FullScreenFrame)container).switchToPortView(((FullScreenFrame)container).getPort(), port3);
                continue;
            }
            container = port3.getView().getShellInternalFrame();
            FullScreenFrame fullScreenFrame = new FullScreenFrame(this.context, this, this.screenManager, this.bundle, (GraphicsConfiguration)object, (JInternalFrame)container, port3, AbstractUIManager.FullScreenTarget.SECONDARY);
            this.fullScreenFrames.put((JInternalFrame)container, fullScreenFrame);
            port3.getView().setFullScreenMode(true);
            fullScreenFrame.setVisible(true);
        }
        for (JInternalFrame jInternalFrame4 : hashMap.values()) {
            object = this.fullScreenFrames.remove(jInternalFrame4);
            if (object == null) continue;
            ((FullScreenFrame)object).setVisible(false);
            ((FullScreenFrame)object).getPort().getView().setFullScreenMode(false);
            ((FullScreenFrame)object).dispose();
        }
        FullScreenFrame fullScreenFrame = this.fullScreenFrames.remove(jInternalFrame);
        if (fullScreenFrame != null) {
            fullScreenFrame.updateInternalFrameReference(port, port2, jInternalFrame2, border, jComponent, dimension, rectangle);
        }
        this.fullScreenFrames.put(jInternalFrame2, fullScreenFrame);
    }

    public boolean fullScreenRequiresNewFrame(Port port, JInternalFrame jInternalFrame) {
        if (Platform.isLinux()) {
            return true;
        }
        FullScreenFrame fullScreenFrame = this.fullScreenFrames.get(jInternalFrame);
        GraphicsConfiguration graphicsConfiguration = this.mh.getGraphicsConfiguration();
        if (port.isPrimaryPort()) {
            graphicsConfiguration = this.mh.getPrimaryGraphicsConfiguration();
        }
        return fullScreenFrame != null && fullScreenFrame.getGraphicsConfiguration() != graphicsConfiguration;
    }

    public void recreateFullScreen(final Port port) {
        SwingUtilities.invokeLater(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                RRCMain rRCMain = RRCMain.this;
                synchronized (rRCMain) {
                    RRCMain.this.dontShowMainFrame = true;
                    RRCMain.this.returnFromFullScreen();
                    try {
                        port.getView().getShellInternalFrame().setSelected(true);
                    }
                    catch (PropertyVetoException propertyVetoException) {
                        // empty catch block
                    }
                    port.getView().setTargetScreenResolution(true);
                    RRCMain.this.dontShowMainFrame = false;
                }
            }
        });
    }

    public Cursor getBlankCursor() {
        if (this.blankCursor == null) {
            int[] nArray = new int[256];
            Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, nArray, 0, 16));
            try {
                this.blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisiblecursor");
            }
            catch (HeadlessException headlessException) {
            }
            catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                // empty catch block
            }
        }
        return this.blankCursor;
    }

    public Cursor getDefaultCursor() {
        if (this.defaultCursor == null) {
            this.defaultCursor = new Cursor(0);
        }
        return this.defaultCursor;
    }

    public URL getIconBase() {
        try {
            File file = new File("nn/pp/common/icons/");
            if (!file.exists()) {
                return this.getClass().getClassLoader().getResource("nn/pp/common/icons/");
            }
            return file.toURL();
        }
        catch (MalformedURLException malformedURLException) {
            return null;
        }
    }

    public Container getActiveContentPane() {
        return FullScreenFrame.getActiveContentPane();
    }

    public Container getContentPaneForPort(Port port) {
        return FullScreenFrame.getContentPaneForPort(port);
    }

    public FullScreenToolBar getFSToolBar(JInternalFrame jInternalFrame) {
        FullScreenFrame fullScreenFrame = this.fullScreenFrames.get(jInternalFrame);
        return fullScreenFrame != null ? fullScreenFrame.getFSToolBar() : null;
    }

    public void addMenuBar() {
        this.screenManager.getMainScreen().addMenuBar();
    }

    public synchronized String getAppId() {
        if (this.appId == null) {
            this.appId = new Date().getTime() + "_" + new Random().nextInt();
        }
        return this.appId;
    }

    private void initFileChooserLabels() {
        UIManager.put("FileChooser.acceptAllFileFilterText", this.bundle.getString("FileChooser.acceptAllFileFilterText"));
        UIManager.put("FileChooser.cancelButtonText", this.bundle.getString("FileChooser.cancelButtonText"));
        UIManager.put("FileChooser.cancelButtonToolTipText", this.bundle.getString("FileChooser.cancelButtonToolTipText"));
        UIManager.put("FileChooser.detailsViewButtonAccessibleName", this.bundle.getString("FileChooser.detailsViewButtonAccessibleName"));
        UIManager.put("FileChooser.detailsViewButtonToolTipText", this.bundle.getString("FileChooser.detailsViewButtonToolTipText"));
        UIManager.put("FileChooser.directoryDescriptionText", this.bundle.getString("FileChooser.directoryDescriptionText"));
        UIManager.put("FileChooser.directoryOpenButtonText", this.bundle.getString("FileChooser.directoryOpenButtonText"));
        UIManager.put("FileChooser.directoryOpenButtonToolTipText", this.bundle.getString("FileChooser.directoryOpenButtonToolTipText"));
        UIManager.put("FileChooser.fileDescriptionText", this.bundle.getString("FileChooser.fileDescriptionText"));
        UIManager.put("FileChooser.fileAttrHeaderText", this.bundle.getString("FileChooser.fileAttrHeaderText"));
        UIManager.put("FileChooser.fileDateHeaderText", this.bundle.getString("FileChooser.fileDateHeaderText"));
        UIManager.put("FileChooser.fileNameHeaderText", this.bundle.getString("FileChooser.fileNameHeaderText"));
        UIManager.put("FileChooser.fileNameLabelText", this.bundle.getString("FileChooser.fileNameLabelText"));
        UIManager.put("FileChooser.fileSizeHeaderText", this.bundle.getString("FileChooser.fileSizeHeaderText"));
        UIManager.put("FileChooser.filesOfTypeLabelText", this.bundle.getString("FileChooser.filesOfTypeLabelText"));
        UIManager.put("FileChooser.fileTypeHeaderText", this.bundle.getString("FileChooser.fileTypeHeaderText"));
        UIManager.put("FileChooser.helpButtonText", this.bundle.getString("FileChooser.helpButtonText"));
        UIManager.put("FileChooser.helpButtonToolTipText", this.bundle.getString("FileChooser.helpButtonToolTipText"));
        UIManager.put("FileChooser.homeFolderAccessibleName", this.bundle.getString("FileChooser.homeFolderAccessibleName"));
        UIManager.put("FileChooser.homeFolderToolTipText", this.bundle.getString("FileChooser.homeFolderToolTipText"));
        UIManager.put("FileChooser.listViewButtonAccessibleName", this.bundle.getString("FileChooser.listViewButtonAccessibleName"));
        UIManager.put("FileChooser.listViewButtonToolTipText", this.bundle.getString("FileChooser.listViewButtonToolTipText"));
        UIManager.put("FileChooser.lookInLabelText", this.bundle.getString("FileChooser.lookInLabelText"));
        UIManager.put("FileChooser.newFolderAccessibleName", this.bundle.getString("FileChooser.newFolderAccessibleName"));
        UIManager.put("FileChooser.newFolderErrorSeparator", this.bundle.getString("FileChooser.newFolderErrorSeparator"));
        UIManager.put("FileChooser.newFolderErrorText", this.bundle.getString("FileChooser.newFolderErrorText"));
        UIManager.put("FileChooser.newFolderToolTipText", this.bundle.getString("FileChooser.newFolderToolTipText"));
        UIManager.put("FileChooser.openButtonText", this.bundle.getString("FileChooser.openButtonText"));
        UIManager.put("FileChooser.openButtonToolTipText", this.bundle.getString("FileChooser.openButtonToolTipText"));
        UIManager.put("FileChooser.openDialogTitleText", this.bundle.getString("FileChooser.openDialogTitleText"));
        UIManager.put("FileChooser.saveButtonText", this.bundle.getString("FileChooser.saveButtonText"));
        UIManager.put("FileChooser.saveButtonToolTipText", this.bundle.getString("FileChooser.saveButtonToolTipText"));
        UIManager.put("FileChooser.saveDialogTitleText", this.bundle.getString("FileChooser.saveDialogTitleText"));
        UIManager.put("FileChooser.saveInLabelText", this.bundle.getString("FileChooser.saveInLabelText"));
        UIManager.put("FileChooser.updateButtonText", this.bundle.getString("FileChooser.updateButtonText"));
        UIManager.put("FileChooser.updateButtonToolTipText", this.bundle.getString("FileChooser.updateButtonToolTipText"));
        UIManager.put("FileChooser.upFolderAccessibleName", this.bundle.getString("FileChooser.upFolderAccessibleName"));
        UIManager.put("FileChooser.upFolderToolTipText", this.bundle.getString("FileChooser.upFolderToolTipText"));
    }

    public class InnerWindowAdapter
    extends WindowAdapter
    implements CommandHolder,
    ConfirmableCommandInterface {
        private boolean confirmation = false;
        private Command command;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void windowClosing(WindowEvent windowEvent) {
            boolean bl = false;
            Command command = this.getCommand();
            try {
                if (this.getConfirmation()) {
                    bl = CommonPopups.showExitConfirmationDialog(RRCMain.this.ui.getContentPane(), (ScreenContext)RRCMain.this.context) != 2;
                }
            }
            finally {
                if (!bl) {
                    if (command != null) {
                        command.execute();
                    } else {
                        RRCMain.this.context.getLogger().logTextError(" (This Item has NO command attached!) ");
                    }
                }
            }
        }

        @Override
        public void setConfirmation(boolean bl) {
            this.confirmation = bl;
        }

        @Override
        public boolean getConfirmation() {
            return this.confirmation;
        }

        @Override
        public Command getCommand() {
            return this.command;
        }

        @Override
        public void setCommand(Command command) {
            this.command = command;
        }
    }
}

