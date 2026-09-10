/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.AppletContext
 */
package com.raritan.tools.ui;

import com.raritan.protocol.browser.DeviceNameAddressProvider;
import com.raritan.tools.ui.AbstractUIManager;
import com.raritan.tools.ui.State;
import com.raritan.tools.ui.blocking.BlockingHelper;
import com.raritan.tools.ui.panes.mediator.PanelMediator;
import com.raritan.tools.util.ImageHolder;
import com.raritan.tools.util.Logger;
import com.raritan.tools.util.ObservableContainer;
import java.applet.AppletContext;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

public abstract class ScreenContext
extends Observable {
    public static final String PROPERTY_LOCALE = "default.locale";
    public static final String PROPERTY_APPLICATION_LOG_LEVEL = "application.log.level";
    public static final String PROPERTY_APPLICATION_LOG_OUTPUT = "application.log.output";
    public static final String PROPERTY_LOG_HISTORY = "log.panel.history";
    public static final String PROPERTY_LOG_TIMESTAMP_FORMAT = "log.timestamp.format";
    public static final String PROPERTY_LOG_ADD_TIMESTAMP = "log.timestamp";
    private Locale locale;
    protected Properties applicationProperties;
    private ImageHolder imageHolder;
    private AbstractUIManager application;
    private State currentState;
    private PanelMediator panelMediator;
    protected HashMap notifiers = new HashMap();
    protected Logger logger;
    protected HashMap observableContainers;
    private BlockingHelper blockingHelper;
    private AppletContext appletContext = null;
    private URL codeBase;
    private Map vmLocalMap;
    private Map vmImagelMap;
    private ArrayList selectedDrives = new ArrayList();
    public static String BUILD = null;

    protected ScreenContext(State state) {
        this.currentState = state;
        this.imageHolder = new ImageHolder(this);
        this.applicationProperties = null;
        if (Locale.getDefault().getLanguage().equalsIgnoreCase(Locale.ENGLISH.getLanguage())) {
            this.setFonts();
        }
        this.observableContainers = new HashMap();
    }

    public static ScreenContext getNewInstance(Map map) {
        return null;
    }

    public Properties getProperties() {
        return this.applicationProperties;
    }

    public synchronized void setState(State state) {
        if (this.getState() == State.LOGGEDIN && state == State.LOGGEDOUT) {
            this.getPanelMediator().hideAll();
        }
        if (state == State.LOGGEDIN) {
            this.createNotificationHandlers();
        } else {
            this.destroyNotificationHandlers();
        }
        this.currentState = state;
        this.setChanged();
        this.notifyObservers();
    }

    public State getState() {
        return this.currentState;
    }

    public synchronized void setPanelMediator(PanelMediator panelMediator) {
        this.panelMediator = panelMediator;
    }

    public PanelMediator getPanelMediator() {
        return this.panelMediator;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getApplicationProperty(String string) {
        return this.applicationProperties.getProperty(string);
    }

    public void setApplication(AbstractUIManager abstractUIManager) {
        this.application = abstractUIManager;
    }

    public AbstractUIManager getApplication() {
        return this.application;
    }

    public Component getOptionComponent() {
        return this.application.getOptionComponent();
    }

    public URL getIconBase() {
        return this.application.getIconBase();
    }

    public ImageIcon getImageIcon(String string) {
        Image image = this.imageHolder.getImage(string);
        if (image == null) {
            return null;
        }
        ImageIcon imageIcon = new ImageIcon(image);
        imageIcon.setDescription(string);
        return imageIcon;
    }

    private void setFonts() {
        Font font = new Font("Verdana", 0, 10);
        Font font2 = font.deriveFont(0);
        Font font3 = font.deriveFont(10.0f);
        UIManager.put("Button.font", font2);
        UIManager.put("CheckBox.font", font2);
        UIManager.put("CheckBoxMenuItem.font", font2);
        UIManager.put("CheckBoxMenuItem.acceleratorFont", font3);
        UIManager.put("ColorChooser.font", font);
        UIManager.put("ComboBox.font", font2);
        UIManager.put("DesctopIcon.font", font2);
        UIManager.put("EditorPane.font", font);
        UIManager.put("FormattedTextField.font", font);
        UIManager.put("InternalFrame.titleFont", font2);
        UIManager.put("Label.font", font2);
        UIManager.put("List.font", font2);
        UIManager.put("Menu.font", font2);
        UIManager.put("Menu.acceleratorFont", font3);
        UIManager.put("MenuBar.font", font2);
        UIManager.put("MenuItem.font", font2);
        UIManager.put("MenuItem.acceleratorFont", font3);
        UIManager.put("OptionPane.font", font);
        UIManager.put("Panel.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("PopupMenu.font", font2);
        UIManager.put("ProgressBar.font", font2);
        UIManager.put("RadioButton.font", font2);
        UIManager.put("RadioButtonMenuItem.font", font2);
        UIManager.put("RadioButtonMenuItem.acceleratorFont", font3);
        UIManager.put("ScrollPane.font", font);
        UIManager.put("Spinner.font", font2);
        UIManager.put("TabbedPane.font", font2);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("TitledBorder.font", font2);
        UIManager.put("ToggleButton.font", font2);
        UIManager.put("ToolBar.font", font2);
        UIManager.put("ToolTip.font", font);
        UIManager.put("Tree.font", font);
        UIManager.put("Viewport.font", font);
    }

    public abstract void createNotificationHandlers();

    public abstract void destroyNotificationHandlers();

    public void addObservable(ObservableContainer observableContainer) {
        this.observableContainers.put(observableContainer.getContainerName(), observableContainer);
    }

    public ObservableContainer getObservable(String string) {
        Object v = this.observableContainers.get(string);
        return (ObservableContainer)v;
    }

    public void clearObservableContainers() {
        this.observableContainers.clear();
    }

    public Logger getLogger() {
        if (this.logger == null) {
            this.logger = new Logger(this);
        }
        return this.logger;
    }

    public void destroy() {
        this.destroyNotificationHandlers();
        this.panelMediator.destroy();
        this.panelMediator = null;
        this.application = null;
    }

    public Properties getApplicationProperties() {
        return this.applicationProperties;
    }

    public void setApplicationProperties(Properties properties) {
        this.applicationProperties = properties;
    }

    public void setAppletContext(AppletContext appletContext) {
        this.appletContext = appletContext;
    }

    public AppletContext getAppletContext() {
        return this.appletContext;
    }

    public void setAppletCodeBase(URL uRL) {
        this.codeBase = uRL;
    }

    public URL getAppletCodeBase() {
        return this.codeBase;
    }

    public BlockingHelper getBlockingHelper() {
        if (this.blockingHelper == null) {
            this.blockingHelper = new BlockingHelper(this);
        }
        return this.blockingHelper;
    }

    public void resetDefaultFocus() {
    }

    public void setVitualMediaLocalMap(Map map) {
        this.vmLocalMap = map;
    }

    public Map getVirtualMediaLocalMap() {
        return this.vmLocalMap;
    }

    public synchronized void setVitualMediaImageMap(Map map) {
        this.vmImagelMap = map;
    }

    public synchronized Map getVirtualMediaImageMap() {
        return this.vmImagelMap;
    }

    public ArrayList getSelectedDrives() {
        return this.selectedDrives;
    }

    public void setSelectedDrives(ArrayList arrayList) {
        this.selectedDrives = arrayList;
    }

    public void addSelectedDrive(String string) {
        this.selectedDrives.add(string);
    }

    public void removeSelectedDrive(String string) {
        this.selectedDrives.remove(string);
    }

    public abstract DeviceNameAddressProvider getDeviceNameAddressProvider();
}

