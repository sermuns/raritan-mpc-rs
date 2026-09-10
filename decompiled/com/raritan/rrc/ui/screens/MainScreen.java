/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.screens;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.components.ConnectedServersToolBar;
import com.raritan.rrc.ui.components.RRCMenuBar;
import com.raritan.rrc.ui.components.RRCStatusBar;
import com.raritan.rrc.ui.components.RRCToolBar;
import com.raritan.rrc.ui.controller.DeviceTreeController;
import com.raritan.rrc.ui.panes.mediator.MainScreenMediator;
import com.raritan.rrc.ui.panes.mediator.RRCPanelMediator;
import com.raritan.rrc.ui.screens.RRCScreenManager;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.ui.panes.BaseTreePanel;
import com.raritan.tools.ui.panes.RaritanStatusBarPanel;
import com.raritan.tools.ui.screens.BaseScreen;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainScreen
extends BaseScreen {
    private static final long serialVersionUID = -6873084021082688228L;
    private JSplitPane splLeftVertical;
    private JSplitPane splHorizontal;
    private RRCMenuBar menuBar;
    private RRCToolBar toolBar;
    private RaritanStatusBarPanel statusBar;
    private JTabbedPane left;
    private RaritanDesktopPane right;
    private JPanel northern;
    private JPanel bottomLeft;
    private double vertical = 0.8;
    private double horisontal = 0.3;
    private MainScreenMediator mainScreenMediator;
    private boolean firstTimePainting = true;
    private RaritanPropertyResourceBundle bundle;
    private ConnectedServersToolBar csToolBar;
    private JPanel menubarPanel;

    public MainScreen(ScreenContext screenContext, RRCScreenManager rRCScreenManager) {
        super(screenContext, rRCScreenManager);
    }

    public void addMenuBar() {
        this.northern.add((Component)this.menuBar, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
    }

    @Override
    protected void initComponents() {
        Object object;
        Object object2;
        boolean bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.setLayout(new BorderLayout());
        this.mainScreenMediator = new MainScreenMediator((RRCScreenContext)this.scrContext);
        ((RRCScreenContext)this.scrContext).setMainScreenMediator(this.mainScreenMediator);
        this.right = new RaritanDesktopPane((RRCScreenContext)this.scrContext);
        this.right.addObservable(((RRCScreenContext)this.scrContext).getSelectedDevicesObservable());
        RRCPanelMediator rRCPanelMediator = new RRCPanelMediator(this.right, this.scrContext);
        this.scrContext.setPanelMediator(rRCPanelMediator);
        final BaseTreePanel baseTreePanel = new BaseTreePanel((RRCScreenContext)this.scrContext);
        this.mainScreenMediator.setDeviceByIPTree(baseTreePanel);
        this.initBars();
        this.northern = new JPanel(new GridBagLayout());
        this.northern.add((Component)this.menuBar, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, 17, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.northern.add((Component)this.toolBar, new GridBagConstraints(0, 1, 1, 1, 0.5, 1.0, 17, 1, new Insets(0, 0, 0, 0), 10, 0));
        this.northern.add((Component)this.csToolBar, new GridBagConstraints(1, 1, 1, 1, 0.5, 1.0, 13, 1, new Insets(0, 0, 0, 0), 0, 0));
        this.add((Component)this.northern, "North");
        if (this.menuBar != null) {
            ((RRCScreenContext)this.scrContext).setMenuBar(this.menuBar);
        }
        boolean bl2 = bl = this.scrContext.getApplicationProperty("connection") != null;
        if (bl) {
            this.add((Component)this.right, "Center");
        } else {
            this.left = new JTabbedPane(3);
            object2 = new JPanel(new BorderLayout());
            object = new JPanel(new BorderLayout());
            JPanel jPanel = new JPanel(new BorderLayout());
            JPanel jPanel2 = new JPanel(new BorderLayout());
            ((Container)object2).add((Component)baseTreePanel, "Center");
            this.left.addTab(this.bundle.getString("Tab.viewByName"), (Component)object2);
            this.left.addTab(this.bundle.getString("Tab.viewByIP"), (Component)object);
            this.left.addTab(this.bundle.getString("Tab.viewByHostName"), jPanel);
            this.left.addTab(this.bundle.getString("Tab.viewByScan"), jPanel2);
            this.left.addChangeListener(new ChangeListener((JPanel)object2, (JPanel)object, jPanel, jPanel2){
                final /* synthetic */ JPanel val$p1;
                final /* synthetic */ JPanel val$p2;
                final /* synthetic */ JPanel val$p3;
                final /* synthetic */ JPanel val$p4;
                {
                    this.val$p1 = jPanel;
                    this.val$p2 = jPanel2;
                    this.val$p3 = jPanel3;
                    this.val$p4 = jPanel4;
                }

                @Override
                public void stateChanged(ChangeEvent changeEvent) {
                    switch (MainScreen.this.left.getSelectedIndex()) {
                        case 0: {
                            ((RRCScreenContext)MainScreen.this.scrContext).setDeviceViewBy(0);
                            break;
                        }
                        case 1: {
                            ((RRCScreenContext)MainScreen.this.scrContext).setDeviceViewBy(1);
                            break;
                        }
                        case 2: {
                            ((RRCScreenContext)MainScreen.this.scrContext).setDeviceViewBy(2);
                            break;
                        }
                        case 3: {
                            ((RRCScreenContext)MainScreen.this.scrContext).setDeviceViewBy(3);
                        }
                    }
                    baseTreePanel.tabChanged(MainScreen.this.left, this.val$p1, this.val$p2, this.val$p3, this.val$p4);
                }
            });
            this.left.setBorder(BorderFactory.createLoweredBevelBorder());
            this.mainScreenMediator.setTabDevice(this.left);
            this.mainScreenMediator.selectNavigatorView(true);
            this.bottomLeft = this.scrContext.getPanelMediator().getLogPanel();
            this.bottomLeft.setBorder(BorderFactory.createLoweredBevelBorder());
            this.mainScreenMediator.setMessagePane(this.bottomLeft);
            this.mainScreenMediator.selectMessageView(false);
            this.splLeftVertical = new JSplitPane(0, this.left, this.bottomLeft);
            this.splLeftVertical.setDividerSize(5);
            this.mainScreenMediator.setLeftVerticalPane(this.splLeftVertical);
            this.splHorizontal = new JSplitPane(1, this.splLeftVertical, this.right);
            this.splHorizontal.setBorder(BorderFactory.createBevelBorder(1));
            this.splHorizontal.setDividerSize(5);
            this.mainScreenMediator.setSplHorizontal(this.splHorizontal);
            this.add((Component)this.splHorizontal, "Center");
            DeviceTreeController.getInstance((RRCScreenContext)this.scrContext);
            this.scrContext.getLogger().logStatus(this.bundle.getString("Status.rrcLoaded"));
        }
        this.add((Component)this.statusBar, "South");
        this.addComponentListener(new MainScreenResizer());
        if (this.scrContext instanceof RRCScreenContext) {
            object2 = ((RRCScreenContext)this.scrContext).getAppSettings();
            object2.importPreferences();
            this.mainScreenMediator.selectMessageView(object2.getViewMessage());
            this.mainScreenMediator.selectBrowseAllDevicesView(object2.getShowAll());
            this.mainScreenMediator.selectShowWithoutTargetsView(object2.getShowUnassigned());
            this.mainScreenMediator.selectShowToolsView(object2.getShowTools());
            this.mainScreenMediator.selectCSToolBarView(object2.isShowCSTools());
            this.mainScreenMediator.selectShowWithoutGroupsView(object2.isShowGroups());
            this.mainScreenMediator.selectSortByChannelNumberView(false);
            this.mainScreenMediator.selectSortByChannelNameView(false);
            this.mainScreenMediator.selectSortByChannelStatusView(false);
            DeviceTreeController.getInstance((RRCScreenContext)this.scrContext).setSortType(object2.getChannelSortMethod());
            switch (object2.getChannelSortMethod()) {
                case 1: {
                    this.mainScreenMediator.selectSortByChannelNameView(true);
                    break;
                }
                case 2: {
                    this.mainScreenMediator.selectSortByChannelStatusView(true);
                    break;
                }
                default: {
                    this.mainScreenMediator.selectSortByChannelNumberView(true);
                }
            }
            object = ((CommandHolder)this.mainScreenMediator.getCheckShowWithoutTargetsMenuItems().get(0)).getCommand();
            object.getContext(true).setCommandParameter("showWithoutTargetsMode", new Boolean(object2.getShowUnassigned()));
            object.execute();
            this.scrContext.getPanelMediator().showPanel(object.getContext());
            object = ((CommandHolder)this.mainScreenMediator.getCheckShowToolsMenuItems().get(0)).getCommand();
            object.getContext(true).setCommandParameter("showToolsMode", new Boolean(object2.getShowTools()));
            object.execute();
            this.scrContext.getPanelMediator().showPanel(object.getContext());
            object = ((CommandHolder)this.mainScreenMediator.getCheckCSToolBarMenuItems().get(0)).getCommand();
            object.getContext(true).setCommandParameter("showCSToolbarMode", new Boolean(object2.isShowCSTools()));
            object.execute();
            this.scrContext.getPanelMediator().showPanel(object.getContext());
            this.scrContext.resetDefaultFocus();
        }
    }

    @Override
    protected void handleCommandResult(CommandResult commandResult) {
    }

    private void initBars() {
        this.menuBar = new RRCMenuBar((RRCScreenContext)this.scrContext, false);
        this.toolBar = new RRCToolBar((RRCScreenContext)this.scrContext);
        this.csToolBar = new ConnectedServersToolBar((RRCScreenContext)this.scrContext);
        this.statusBar = (RaritanStatusBarPanel)this.scrContext.getPanelMediator().getStatusPanel();
        ((RRCStatusBar)this.statusBar).addObservable(((RRCScreenContext)this.scrContext).getSelectedDevicesObservable());
        this.mainScreenMediator.selectBrowseAllDevicesView(true);
        this.mainScreenMediator.selectShowWithoutTargetsView(true);
        this.mainScreenMediator.selectShowPowerstripsView(true);
        this.mainScreenMediator.selectShowToolsView(true);
        this.mainScreenMediator.selectShowWithoutGroupsView(true);
        this.mainScreenMediator.selectSortByChannelNumberView(true);
        this.mainScreenMediator.setToolBar(this.toolBar);
        this.mainScreenMediator.selectToolBarView(true);
        this.mainScreenMediator.setStatusBar(this.statusBar);
        this.mainScreenMediator.selectStatusBarView(true);
        this.mainScreenMediator.selectStandardMouseModeView(true);
        this.mainScreenMediator.setCsToolBar(this.csToolBar);
    }

    @Override
    public void paint(Graphics graphics) {
        boolean bl;
        super.paint(graphics);
        boolean bl2 = bl = this.scrContext.getApplicationProperty("connection") != null;
        if (!bl && this.firstTimePainting) {
            this.splHorizontal.setDividerLocation(this.getHorisontal());
            this.splLeftVertical.setDividerLocation(this.getVertical());
            this.firstTimePainting = false;
        }
    }

    public void setFirstTimePainting(boolean bl) {
        this.firstTimePainting = bl;
    }

    public double getVertical() {
        return this.vertical;
    }

    public void setVertical(double d) {
        this.vertical = d;
    }

    public double getHorisontal() {
        return this.horisontal;
    }

    public void setHorisontal(double d) {
        this.horisontal = d;
    }

    private class MainScreenResizer
    extends ComponentAdapter {
        private MainScreenResizer() {
        }

        @Override
        public void componentResized(ComponentEvent componentEvent) {
            if (componentEvent.getID() == 101 && MainScreen.this.right.getWidth() == 0) {
                MainScreen.this.setFirstTimePainting(true);
            }
        }
    }
}

