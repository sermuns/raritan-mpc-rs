/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.MonitorSettingsHandler;
import com.raritan.rrc.ui.RRCMain;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.DoDisconnectCommand;
import com.raritan.rrc.ui.commands.ShowTargetScreenResolutionCommand;
import com.raritan.rrc.ui.components.RRCMenuBar;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.components.CommandMenu;
import com.raritan.tools.ui.components.MultyObserverComponentInterface;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.util.ObservableContainer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import javaclientlib.utils.RRCLogger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class FullScreenToolBar
extends JToolBar
implements MultyObserverComponentInterface {
    private static final long serialVersionUID = 8859595065369693406L;
    private ButtonGroup bGroup;
    private Port selectedButton;
    private RRCScreenContext context;
    private RaritanPropertyResourceBundle bundle = null;
    private FullScreenMouseHandler mmh;
    private MouseAdapter smh;
    private RRCMain rrcMain;
    private JInternalFrame newframe;
    private JComponent northPane = null;
    private Dimension northPaneSize = null;
    private Rectangle viewFrameBounds = null;
    private Border jifBorder = null;
    private JPanel pane = null;
    private JPanel portPanel;
    private JPanel widgetPanel;
    private HashMap buttonMappings;
    private HashMap observables = new HashMap();
    private boolean hasExtraPortsOpen = false;
    private RRCMenuBar menubar;
    private RRCMenuBar disabledMenubar;
    private boolean createNewMenuBar;
    private JPanel menubarContainer;
    private JPanel disabledMenubarContainer;
    JPanel p;
    JScrollPane scrollPane;
    JLabel txtLabel;
    String hotKey;
    private boolean pinState = false;
    private boolean showFlag = false;
    private GraphicsConfiguration graphicsConfig;
    private boolean useTargetButtons = false;
    private boolean disposed = false;
    private ImageIcon pinIcon;
    private ImageIcon unpinIcon;
    private JToggleButton pinButton;

    public FullScreenToolBar(RRCScreenContext rRCScreenContext, RaritanPropertyResourceBundle raritanPropertyResourceBundle, JPanel jPanel, RRCMain rRCMain, JInternalFrame jInternalFrame, Border border, JComponent jComponent, Dimension dimension, Rectangle rectangle, boolean bl, GraphicsConfiguration graphicsConfiguration, Port port, MouseAdapter mouseAdapter, boolean bl2, boolean bl3) {
        this.context = rRCScreenContext;
        this.bundle = raritanPropertyResourceBundle;
        this.rrcMain = rRCMain;
        this.newframe = jInternalFrame;
        this.pane = jPanel;
        this.jifBorder = border;
        this.northPane = jComponent;
        this.northPaneSize = dimension;
        this.viewFrameBounds = rectangle;
        this.pinState = bl;
        this.graphicsConfig = graphicsConfiguration;
        this.useTargetButtons = bl2;
        this.pinIcon = rRCScreenContext.getImageIcon("Common_pin.gif");
        this.unpinIcon = rRCScreenContext.getImageIcon("Common_unpin.gif");
        this.mmh = new FullScreenMouseHandler();
        this.smh = mouseAdapter;
        this.selectedButton = port != null ? port : rRCScreenContext.getSelectedPort();
        this.createNewMenuBar = bl3;
        if (bl3) {
            RRCMenuBar rRCMenuBar = new RRCMenuBar(rRCScreenContext, false);
            rRCMenuBar.setMenuItemTargetScreenResolution(true);
            rRCMenuBar.enableMenusInFullScreenMode(false);
            rRCMenuBar.getWindowMenu().setEnabled(false);
            this.menubar = rRCMenuBar;
        } else {
            this.menubar = rRCScreenContext.getMenuBar();
        }
        this.setRollover(true);
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1), BorderFactory.createRaisedBevelBorder()));
        this.bGroup = new ButtonGroup();
        this.buttonMappings = new HashMap();
        this.addObservable(this.context.getSelectedDevicesObservable());
        this.addObservable(this.context.getOpenPortsObservable());
        this.setLayout(new BorderLayout());
        this.createPortPanel();
        this.selectedButton.getView().addCustomMouseListener(this.smh);
        this.selectedButton.getView().addCustomMouseMotionListener(this.smh);
    }

    private void createPortPanel() {
        this.hotKey = this.context.getAppSettings().getkeyboardMenuHotkey();
        this.hotKey = this.hotKey.replace("Alt", "LeftAlt");
        this.portPanel = new JPanel(new FlowLayout(2));
        this.scrollPane = new JScrollPane();
        Dimension dimension = this.graphicsConfig.getBounds().getSize();
        this.scrollPane.setPreferredSize(new Dimension(dimension.width, 50));
        JPanel jPanel = new JPanel();
        jPanel.add(this.createOptionButton());
        jPanel.add(this.portPanel);
        this.scrollPane.getViewport().add(jPanel);
        this.scrollPane.getViewport().validate();
        this.scrollPane.setHorizontalScrollBarPolicy(30);
        this.scrollPane.setVerticalScrollBarPolicy(21);
        JScrollBar jScrollBar = this.scrollPane.getHorizontalScrollBar();
        jScrollBar.setPreferredSize(new Dimension(5, 10));
        this.add((Component)this.scrollPane, "West");
        this.createPortButtons();
        this.createWidgetsPanel();
        this.portPanel.add(this.widgetPanel);
        this.setLabelText();
    }

    private void createWidgetsPanel() {
        this.widgetPanel = new JPanel(new FlowLayout(2));
        CommandButton commandButton = new CommandButton(this.bundle.getString("FullScreenToolbar.returnToNormalButton.label"), (ScreenContext)this.context);
        commandButton.setToolTipText(this.bundle.getString("FullScreenToolbar.returnToNormalButton.tooltip"));
        commandButton.setBorder(new BevelBorder(0));
        commandButton.setBorderPainted(true);
        commandButton.setPreferredSize(new Dimension(20, 20));
        final ShowTargetScreenResolutionCommand showTargetScreenResolutionCommand = new ShowTargetScreenResolutionCommand(this.context);
        commandButton.setCommand(showTargetScreenResolutionCommand);
        commandButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        showTargetScreenResolutionCommand.execute();
                        FullScreenToolBar.this.context.resetDefaultFocus();
                    }
                });
            }
        });
        this.widgetPanel.add(commandButton);
        CommandButton commandButton2 = new CommandButton(this.bundle.getString("FullScreenToolbar.crossButton.label"), (ScreenContext)this.context);
        commandButton2.setToolTipText(this.bundle.getString("FullScreenToolbar.crossButton.tooltip"));
        commandButton2.setBorder(new BevelBorder(0));
        commandButton2.setBorderPainted(true);
        commandButton2.setPreferredSize(new Dimension(20, 20));
        final DoDisconnectCommand doDisconnectCommand = new DoDisconnectCommand(this.context);
        commandButton2.setCommand(doDisconnectCommand);
        commandButton2.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                doDisconnectCommand.getContext().setCommandParameter("selectedPortDevice", FullScreenToolBar.this.selectedButton);
                SwingUtilities.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        doDisconnectCommand.execute();
                        if (FullScreenToolBar.this.selectedButton != null) {
                            MPCUtil.notifyObservers(FullScreenToolBar.this.context, FullScreenToolBar.this.selectedButton);
                            for (CommandMenu commandMenu : FullScreenToolBar.this.context.getMainScreenMediator().getWindowMenus()) {
                                commandMenu.setEnabled(false);
                            }
                        }
                    }
                });
            }
        });
        this.widgetPanel.add(commandButton2);
    }

    private JPanel createOptionButton() {
        this.p = new JPanel();
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.setAlignmentX(0.0f);
        jPanel.setAlignmentY(0.0f);
        if (this.selectedButton.isMultiMonitorPort()) {
            this.menubarContainer = new JPanel(new BorderLayout());
            this.menubarContainer.setAlignmentX(0.0f);
            this.menubarContainer.setAlignmentY(0.0f);
            this.menubarContainer.add((Component)this.menubar, "Center");
            this.disabledMenubarContainer = new JPanel(new BorderLayout());
            this.disabledMenubarContainer.setAlignmentX(0.0f);
            this.disabledMenubarContainer.setAlignmentY(0.0f);
            this.disabledMenubar = new RRCMenuBar(this.context, true);
            this.disabledMenubarContainer.add((Component)this.disabledMenubar, "Center");
            jPanel.add((Component)this.menubarContainer, "North");
            jPanel.add((Component)this.disabledMenubarContainer, "South");
            this.disabledMenubarContainer.setVisible(false);
        } else {
            jPanel.add((Component)this.menubar, "North");
        }
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new FlowLayout());
        this.pinButton = new JToggleButton(this.pinState ? this.pinIcon : this.unpinIcon);
        this.pinButton.setPreferredSize(new Dimension(this.pinIcon.getIconWidth(), this.pinIcon.getIconHeight()));
        this.pinButton.setOpaque(false);
        this.pinButton.addItemListener(new ItemListener(){

            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                FullScreenToolBar.this.pinState = !FullScreenToolBar.this.pinState;
                if (FullScreenToolBar.this.pinState) {
                    FullScreenToolBar.this.setVisible(true);
                    if (FullScreenToolBar.this.mmh.getPort() != null) {
                        FullScreenToolBar.this.mmh.getPort().getView().removeCustomMouseMotionListener(FullScreenToolBar.this.mmh);
                        FullScreenToolBar.this.mmh.getPort().getView().removeCustomMouseListener(FullScreenToolBar.this.mmh);
                    }
                } else {
                    FullScreenToolBar.this.setVisible(false);
                    if (FullScreenToolBar.this.context != null && FullScreenToolBar.this.context.getSelectView() != null) {
                        FullScreenToolBar.this.context.getSelectView().setContextMenuKVMVisible(false);
                    }
                    if (FullScreenToolBar.this.mmh.getPort() != null) {
                        System.out.println("Adding pin listener");
                        FullScreenToolBar.this.mmh.getPort().getView().addCustomMouseMotionListener(FullScreenToolBar.this.mmh);
                        FullScreenToolBar.this.mmh.getPort().getView().addCustomMouseListener(FullScreenToolBar.this.mmh);
                    }
                    FullScreenToolBar.this.pane.validate();
                }
                FullScreenToolBar.this.togglePinState();
                FullScreenToolBar.this.focusVideo();
            }
        });
        JLabel jLabel = new JLabel(this.context.getImageIcon("Common_Raritan_Title.gif"));
        this.txtLabel = new JLabel();
        jPanel2.add(this.txtLabel);
        jPanel2.add(this.pinButton);
        jPanel2.add(jLabel);
        this.p.add(jPanel2);
        this.p.add(jPanel);
        return this.p;
    }

    private void togglePinState() {
        this.pinButton.setIcon(this.pinState ? this.pinIcon : this.unpinIcon);
    }

    private void focusVideo() {
        this.selectedButton.getView().setViewFocus();
    }

    public void removeMenuBar() {
        this.remove(this.p);
    }

    public void createPortButtons() {
        ArrayList arrayList = this.context.getListOfOpenPorts();
        for (int i = 0; i < arrayList.size(); ++i) {
            String string = (String)arrayList.get(i);
            if (string == null) continue;
            final Port port = (Port)this.context.getPortByKeyObservable(string);
            if (port instanceof KvmPort) {
                if (this.useTargetButtons && (port.isPrimaryOrSinglePort() || MonitorSettingsHandler.getInstance().getMonitorCount() == 1)) {
                    final JToggleButton jToggleButton = new JToggleButton(port.getName());
                    this.buttonMappings.put(port, jToggleButton);
                    jToggleButton.addActionListener(new ActionListener(){

                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            if (!port.equals(FullScreenToolBar.this.selectedButton)) {
                                Port port2 = FullScreenToolBar.this.selectedButton;
                                FullScreenToolBar.this.selectedButton = port;
                                if (jToggleButton.isSelected()) {
                                    FullScreenToolBar.this.selectButtonFrame(port2, port, true);
                                }
                            }
                            port.getView().setViewFocus();
                        }
                    });
                    jToggleButton.setToolTipText(port.getViewName());
                    jToggleButton.setBorder(new BevelBorder(0));
                    jToggleButton.setBorderPainted(true);
                    jToggleButton.setPreferredSize(new Dimension(50, 20));
                    jToggleButton.setIcon(this.context.getImageIcon(RaritanResourceBundle.getResourceBundle(this.context.getLocale()).getString("KvmPortActive.image")));
                    this.bGroup.add(jToggleButton);
                    if (port.equals(this.selectedButton)) {
                        jToggleButton.setSelected(true);
                        this.mmh.setPort(port);
                    } else {
                        try {
                            port.getView().getShellInternalFrame().setSelected(false);
                        }
                        catch (PropertyVetoException propertyVetoException) {
                            propertyVetoException.printStackTrace();
                        }
                    }
                    this.portPanel.add(jToggleButton);
                    continue;
                }
                this.mmh.setPort(this.selectedButton);
                this.buttonMappings.put(port, null);
                continue;
            }
            try {
                port.getView().getShellInternalFrame().setSelected(false);
                this.hasExtraPortsOpen = true;
                continue;
            }
            catch (PropertyVetoException propertyVetoException) {
                propertyVetoException.printStackTrace();
            }
        }
    }

    public void switchToPortView(Port port, Port port2, boolean bl) {
        try {
            JComponent jComponent;
            if (bl) {
                this.pane.removeAll();
                this.mmh.removePort();
                jComponent = (RaritanDesktopPane)this.context.getPanelMediator().getParent();
                this.newframe.setSize(jComponent.getSize());
                this.northPane.setPreferredSize(this.northPaneSize);
                this.northPane.setVisible(true);
                this.newframe.setBorder(this.jifBorder);
                this.newframe.setBounds(this.viewFrameBounds);
                jComponent.add(this.newframe);
                this.newframe.setSelected(false);
                if (port != null && port.getView() != null) {
                    port.getView().setFullScreenMode(false);
                    port.getView().removeCustomMouseListener(this.smh);
                    port.getView().removeCustomMouseMotionListener(this.smh);
                }
            }
            port2.getView().setFullScreenMode(true);
            port2.getView().addCustomMouseListener(this.smh);
            port2.getView().addCustomMouseMotionListener(this.smh);
            this.mmh.setPort(port2);
            jComponent = this.newframe;
            this.newframe = port2.getView().getShellInternalFrame();
            if (this.newframe.isIcon()) {
                this.newframe.setIcon(false);
            }
            this.northPane = ((BasicInternalFrameUI)this.newframe.getUI()).getNorthPane();
            this.northPane.setVisible(false);
            this.northPaneSize = this.northPane.getSize();
            this.viewFrameBounds = this.newframe.getBounds();
            this.jifBorder = this.newframe.getBorder();
            this.newframe.setBorder(new EmptyBorder(0, 0, 0, 0));
            this.northPane.setPreferredSize(new Dimension(0, 0));
            this.pane.add((Component)this.newframe, "Center");
            this.pane.validate();
            this.newframe.setSelected(true);
            this.rrcMain.updateInternalFrameReference(this.getGraphicsConfiguration().getDevice(), port, port2, (JInternalFrame)jComponent, this.newframe, this.jifBorder, this.northPane, this.northPaneSize, this.viewFrameBounds);
        }
        catch (PropertyVetoException propertyVetoException) {
            RRCLogger.logException(propertyVetoException);
        }
    }

    private void selectButtonFrame(Port port, Port port2, boolean bl) {
        if (!this.useTargetButtons) {
            return;
        }
        if (this.rrcMain.fullScreenRequiresNewFrame(port2, this.newframe)) {
            this.rrcMain.recreateFullScreen(port2);
        } else {
            this.switchToPortView(port, port2, bl);
        }
    }

    public void dispose() {
        this.disposed = true;
        this.removeObservable(this.context.getOpenPortsObservable());
        this.observables = null;
        this.buttonMappings = null;
        this.portPanel.removeAll();
        this.widgetPanel.removeAll();
        this.mmh.removePort();
        if (this.selectedButton != null && this.selectedButton.getView() != null) {
            this.selectedButton.getView().removeCustomMouseListener(this.smh);
            this.selectedButton.getView().removeCustomMouseMotionListener(this.smh);
        }
        this.portPanel = null;
        this.widgetPanel = null;
        this.mmh = null;
        if (this.createNewMenuBar && this.menubar != null) {
            this.menubar.dispose();
        }
        this.menubar = null;
        if (this.disabledMenubar != null) {
            this.disabledMenubar.dispose();
        }
        this.disabledMenubar = null;
    }

    @Override
    public void addObservable(Observable observable) {
        this.observables.put(this.hashCode() + "", this);
        observable.addObserver(this);
    }

    @Override
    public void clearObservables() {
        if (this.observables != null && this.observables.size() > 0) {
            for (Observable observable : this.observables.values()) {
                observable.deleteObserver(this);
            }
            this.observables.clear();
        }
    }

    @Override
    public List getObservables() {
        return new ArrayList(this.observables.values());
    }

    @Override
    public void removeObservable(Observable observable) {
        this.observables.remove(this.hashCode() + "");
        observable.deleteObserver(this);
    }

    @Override
    public synchronized void update(Observable observable, Object object) {
        if (this.disposed) {
            return;
        }
        if ("Open Ports".equals(((ObservableContainer)observable).getContainerName())) {
            if (object != null) {
                HashMap hashMap = (HashMap)object;
                if (this.buttonMappings.keySet().containsAll(hashMap.values()) || this.hasExtraPortsOpen) {
                    if (!hashMap.values().containsAll(this.buttonMappings.keySet())) {
                        for (Port port : this.buttonMappings.keySet()) {
                            if (hashMap.containsValue(port)) continue;
                            JToggleButton jToggleButton = (JToggleButton)this.buttonMappings.remove(port);
                            if (jToggleButton != null) {
                                this.bGroup.remove(jToggleButton);
                                this.portPanel.remove(jToggleButton);
                            }
                            this.portPanel.revalidate();
                            this.portPanel.repaint();
                            this.scrollPane.revalidate();
                            this.scrollPane.repaint();
                            jToggleButton = null;
                            if (!this.useTargetButtons && port == this.selectedButton) {
                                this.clearSelection();
                                this.rrcMain.returnOnSecondaryDisconnect(this.newframe);
                            } else if (this.buttonMappings.size() <= 0) {
                                this.clearSelection();
                                this.rrcMain.returnOnDisconnect();
                                this.rrcMain.addMenuBar();
                            } else if (this.selectedButton != null && this.selectedButton.equals(port)) {
                                Port port2 = this.selectedButton;
                                this.mmh.removePort();
                                for (Port this.selectedButton : this.buttonMappings.keySet()) {
                                    JToggleButton jToggleButton2 = (JToggleButton)this.buttonMappings.get(this.selectedButton);
                                    if (jToggleButton2 != null) {
                                        jToggleButton2.setSelected(true);
                                        break;
                                    }
                                    this.selectedButton = null;
                                }
                                if (this.selectedButton != null) {
                                    this.selectButtonFrame(port2, this.selectedButton, false);
                                    for (CommandMenu commandMenu : this.context.getMainScreenMediator().getWindowMenus()) {
                                        commandMenu.setEnabled(false);
                                    }
                                } else {
                                    this.clearSelection();
                                    this.rrcMain.returnOnDisconnect();
                                    this.rrcMain.addMenuBar();
                                }
                            }
                            break;
                        }
                    } else if (hashMap.values().containsAll(this.buttonMappings.keySet())) {
                        for (Port port : hashMap.values()) {
                            JToggleButton jToggleButton;
                            if (!this.buttonMappings.containsKey(port) || (jToggleButton = (JToggleButton)this.buttonMappings.get(port)) == null || jToggleButton.getText().equals(port.getName())) continue;
                            jToggleButton.setText(port.getName());
                            jToggleButton.setToolTipText(port.getViewName());
                        }
                    }
                }
            }
        } else if ("Selected Devices".equals(((ObservableContainer)observable).getContainerName()) && ((ArrayList)object).get(0) != null) {
            this.enableMenuBar((Device)((ArrayList)object).get(0));
        }
    }

    public void clearSelection() {
        this.selectedButton = null;
        this.bGroup.setSelected(new JButton("dummy").getModel(), true);
    }

    public void setLabelText() {
        if (this.context.getSelectView().isSingleCursor()) {
            this.txtLabel.setText(this.hotKey + ", " + this.bundle.getString("FullScreen.exitSMM"));
        } else {
            this.txtLabel.setText("");
        }
        this.validate();
    }

    public void setToolBarVisible(boolean bl) {
        try {
            Timer timer = new Timer(true);
            timer.schedule((TimerTask)new ShowMessage(), 0L, 5000L);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
    }

    public JInternalFrame getViewFrame() {
        return this.newframe;
    }

    public void enableMenuBar(Device device) {
        if (this.menubarContainer != null && this.disabledMenubarContainer != null && device != null && device instanceof Port) {
            this.menubarContainer.setVisible(device == this.selectedButton);
            this.disabledMenubarContainer.setVisible(device != this.selectedButton);
        }
    }

    class FullScreenMouseHandler
    extends MouseAdapter {
        private Port port;

        FullScreenMouseHandler() {
        }

        public void setPort(Port port) {
            this.port = port;
            if (!FullScreenToolBar.this.pinState) {
                port.getView().addCustomMouseListener(this);
                port.getView().addCustomMouseMotionListener(this);
            }
        }

        public Port getPort() {
            return this.port;
        }

        public void removePort() {
            if (this.port != null && this.port.getView() != null) {
                this.port.getView().removeCustomMouseListener(this);
                this.port.getView().removeCustomMouseMotionListener(this);
            }
        }

        public void removeBar(MouseEvent mouseEvent) {
            FullScreenToolBar.this.setVisible(false);
            if (FullScreenToolBar.this.context != null && FullScreenToolBar.this.context.getSelectView() != null) {
                FullScreenToolBar.this.context.getSelectView().setContextMenuKVMVisible(false);
            }
            FullScreenToolBar.this.pane.validate();
        }

        public void addBar(MouseEvent mouseEvent) {
            FullScreenToolBar.this.setVisible(true);
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            if (mouseEvent.getY() <= 20 || mouseEvent.getY() < 0 || FullScreenToolBar.this.showFlag) {
                this.addBar(mouseEvent);
            } else if (FullScreenToolBar.this.isVisible()) {
                this.removeBar(mouseEvent);
            }
            FullScreenToolBar.this.setLabelText();
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            this.addBar(mouseEvent);
            FullScreenToolBar.this.setLabelText();
        }
    }

    class ShowMessage
    extends TimerTask {
        int num = 1;

        @Override
        public void run() {
            if (this.num > 0) {
                FullScreenToolBar.this.showFlag = true;
                if (!FullScreenToolBar.this.isVisible()) {
                    FullScreenToolBar.this.setFocusable(true);
                    FullScreenToolBar.this.setVisible(true);
                }
                --this.num;
            } else {
                FullScreenToolBar.this.showFlag = false;
                this.cancel();
            }
        }
    }
}

