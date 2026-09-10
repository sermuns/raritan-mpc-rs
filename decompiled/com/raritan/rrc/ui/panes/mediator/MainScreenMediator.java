/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes.mediator;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.components.CommandToggleButton;
import com.raritan.rrc.ui.components.ConnectedServersToolBar;
import com.raritan.rrc.ui.components.RRCToolBar;
import com.raritan.rrc.ui.screens.MainScreen;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.components.CommandCheckMenuItem;
import com.raritan.tools.ui.components.CommandMenu;
import com.raritan.tools.ui.components.CommandRadioMenuItem;
import com.raritan.tools.ui.panes.BaseTreePanel;
import com.raritan.tools.ui.panes.RaritanStatusBarPanel;
import java.util.List;
import java.util.Vector;
import javaclientlib.utils.RRCLogger;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.Timer;

public class MainScreenMediator {
    protected RRCScreenContext scrContext;
    private JToggleButton toolBarSingleMouseCursorButton;
    private CommandToggleButton toolBarNavigatorButton;
    private CommandToggleButton toolBarScaleVideoButton;
    private CommandToggleButton toolBarBrowseAllDevicesButton;
    private CommandToggleButton toolBarFullScreenButton;
    private CommandButton toolBarRefreshButton;
    private JTabbedPane navigator;
    private JPanel message;
    private RRCToolBar toolBar;
    private RaritanStatusBarPanel statusBar;
    private JSplitPane splLeftVertical;
    private JSplitPane splHorizontal;
    private List<CommandRadioMenuItem> radioAbsoluteMouseMenuItems = new Vector<CommandRadioMenuItem>();
    private List<CommandRadioMenuItem> radioIntelligentMouseMenuItems = new Vector<CommandRadioMenuItem>();
    private List<CommandRadioMenuItem> radioStandardMouseMenuItems = new Vector<CommandRadioMenuItem>();
    private List<CommandCheckMenuItem> checkToolBarMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkStatusBarMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkFullScreenMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkNavigatorMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkMessageMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkAllDevicesMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkScaleVideoMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkShowWithoutTargetsMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkShowPowerstripsMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkShowToolsMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkSortByChannelNumberMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkSortByChannelNameMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkSortByChannelStatusMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandMenu> windowMenus = new Vector<CommandMenu>();
    private List<CommandMenu> keyboardMenus = new Vector<CommandMenu>();
    private List<CommandMenu> virtualMediaMenus = new Vector<CommandMenu>();
    private BaseTreePanel deviceByIPTree;
    Timer busyTimer = null;
    private ConnectedServersToolBar csToolBar;
    private List<CommandCheckMenuItem> checkCSToolBarMenuItems = new Vector<CommandCheckMenuItem>();
    private List<CommandCheckMenuItem> checkGroupsMenuItems = new Vector<CommandCheckMenuItem>();

    public MainScreenMediator(RRCScreenContext rRCScreenContext) {
        this.scrContext = rRCScreenContext;
    }

    public void selectToolBarView(boolean bl) {
        if (this.toolBar != null) {
            this.toolBar.setVisible(bl);
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkToolBarMenuItems) {
                commandCheckMenuItem.setSelected(bl);
            }
        } else {
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkToolBarMenuItems) {
                commandCheckMenuItem.setSelected(false);
            }
        }
    }

    public void selectStatusBarView(boolean bl) {
        if (this.statusBar != null) {
            this.statusBar.setVisible(bl);
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkStatusBarMenuItems) {
                commandCheckMenuItem.setSelected(bl);
            }
        } else {
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkStatusBarMenuItems) {
                commandCheckMenuItem.setSelected(false);
            }
        }
    }

    public void selectNavigatorView(boolean bl) {
        if (this.navigator != null) {
            this.navigator.setVisible(bl);
            if (!bl) {
                ((MainScreen)this.splLeftVertical.getParent().getParent()).setHorisontal((this.navigator.getSize().getWidth() + 4.0) / (this.splHorizontal.getSize().getWidth() - 7.0));
            }
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkMessageMenuItems) {
                commandCheckMenuItem.setEnabled(bl);
            }
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkNavigatorMenuItems) {
                commandCheckMenuItem.setSelected(bl);
            }
            this.toolBarNavigatorButton.setSelected(bl);
            this.toolBarBrowseAllDevicesButton.setEnabled(bl);
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkAllDevicesMenuItems) {
                commandCheckMenuItem.setEnabled(bl);
            }
            this.toolBarRefreshButton.setEnabled(bl);
        } else {
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkNavigatorMenuItems) {
                commandCheckMenuItem.setSelected(false);
            }
            this.toolBarNavigatorButton.setSelected(false);
        }
        if (this.splLeftVertical != null && this.splHorizontal != null) {
            this.splLeftVertical.setVisible(bl);
            ((MainScreen)this.splLeftVertical.getParent().getParent()).setFirstTimePainting(true);
            this.splLeftVertical.getParent().getParent().validate();
            this.splLeftVertical.getParent().getParent().repaint();
        }
    }

    public void selectMessageView(boolean bl) {
        if (this.message != null) {
            this.message.setVisible(bl);
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkMessageMenuItems) {
                commandCheckMenuItem.setSelected(bl);
            }
        } else {
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkMessageMenuItems) {
                commandCheckMenuItem.setSelected(false);
            }
        }
        if (this.splLeftVertical != null && this.splHorizontal != null) {
            this.message.setVisible(bl);
            if (bl) {
                this.splLeftVertical.setVisible(bl);
                ((MainScreen)this.splLeftVertical.getParent().getParent()).setFirstTimePainting(true);
                this.splLeftVertical.getParent().getParent().repaint();
            }
            this.splLeftVertical.getParent().getParent().validate();
        }
    }

    public void selectSingleMouseCursorMode() {
        if (this.toolBarSingleMouseCursorButton != null) {
            boolean bl = this.toolBarSingleMouseCursorButton.isSelected();
            this.toolBarSingleMouseCursorButton.setSelected(!bl);
        }
    }

    public void selectAbsoluteMouseModeView(boolean bl) {
        for (CommandRadioMenuItem commandRadioMenuItem : this.radioAbsoluteMouseMenuItems) {
            commandRadioMenuItem.setSelected(bl);
            RRCLogger.log(300, 4, "AbsoluteMouseMenuItem Selecting: " + bl);
        }
    }

    public void enableAbsoluteMouseModeView(boolean bl) {
        for (CommandRadioMenuItem commandRadioMenuItem : this.radioAbsoluteMouseMenuItems) {
            commandRadioMenuItem.setEnabled(bl);
            RRCLogger.log(300, 4, "AbsoluteMouseMenuItem Enabling: " + bl);
        }
    }

    public void selectStandardMouseModeView(boolean bl) {
        for (CommandRadioMenuItem commandRadioMenuItem : this.radioStandardMouseMenuItems) {
            commandRadioMenuItem.setSelected(bl);
            RRCLogger.log(300, 4, "StandardMouseMenuItem Selecting:" + bl);
        }
    }

    public void enableStandardMouseModeView(boolean bl) {
        for (CommandRadioMenuItem commandRadioMenuItem : this.radioStandardMouseMenuItems) {
            commandRadioMenuItem.setEnabled(bl);
            RRCLogger.log(300, 4, "StandardMouseMenuItem Enabling:" + bl);
        }
    }

    public void selectIntelligentMouseModeView(boolean bl) {
        for (CommandRadioMenuItem commandRadioMenuItem : this.radioIntelligentMouseMenuItems) {
            commandRadioMenuItem.setSelected(bl);
            RRCLogger.log(300, 4, "IntelligentMouseMenuItem Selecting:" + bl);
        }
    }

    public void enableIntelligentMouseModeView(boolean bl) {
        for (CommandRadioMenuItem commandRadioMenuItem : this.radioIntelligentMouseMenuItems) {
            commandRadioMenuItem.setEnabled(bl);
            RRCLogger.log(300, 4, "IntelligentMouseMenuItem Enabling:" + bl);
        }
    }

    public void selectScaleVideo(boolean bl) {
        for (CommandCheckMenuItem commandCheckMenuItem : this.checkScaleVideoMenuItems) {
            commandCheckMenuItem.setSelected(bl);
        }
    }

    public void selectBrowseAllDevicesView(boolean bl) {
        for (CommandCheckMenuItem commandCheckMenuItem : this.checkAllDevicesMenuItems) {
            commandCheckMenuItem.setSelected(bl);
        }
        if (this.toolBarBrowseAllDevicesButton != null) {
            this.toolBarBrowseAllDevicesButton.setSelected(bl);
        }
    }

    public void selectShowWithoutTargetsView(boolean bl) {
        for (CommandCheckMenuItem commandCheckMenuItem : this.checkShowWithoutTargetsMenuItems) {
            commandCheckMenuItem.setSelected(bl);
        }
    }

    public void selectShowWithoutGroupsView(boolean bl) {
        for (CommandCheckMenuItem commandCheckMenuItem : this.checkGroupsMenuItems) {
            commandCheckMenuItem.setSelected(bl);
        }
    }

    public void selectShowPowerstripsView(boolean bl) {
        for (CommandCheckMenuItem commandCheckMenuItem : this.checkShowPowerstripsMenuItems) {
            commandCheckMenuItem.setSelected(bl);
        }
    }

    public void selectShowToolsView(boolean bl) {
        for (CommandCheckMenuItem commandCheckMenuItem : this.checkShowToolsMenuItems) {
            commandCheckMenuItem.setSelected(bl);
        }
    }

    public List<CommandCheckMenuItem> getCheckFullScreenMenuItems() {
        return this.checkFullScreenMenuItems;
    }

    public List<CommandRadioMenuItem> getRadioAbsoluteMouseMenuItems() {
        return this.radioAbsoluteMouseMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckToolbarMenuItems() {
        return this.checkToolBarMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckStatusBarMenuItems() {
        return this.checkStatusBarMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckNavigatorMenuItems() {
        return this.checkNavigatorMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckMessageMenuItems() {
        return this.checkMessageMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckAllDevicesMenuItems() {
        return this.checkAllDevicesMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckShowWithoutTargetsMenuItems() {
        return this.checkShowWithoutTargetsMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckShowGroupsMenuItems() {
        return this.checkGroupsMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckShowPowerstripsMenuItems() {
        return this.checkShowPowerstripsMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckShowToolsMenuItems() {
        return this.checkShowToolsMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckSortByChannelNumberMenuItems() {
        return this.checkSortByChannelNumberMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckSortByChannelNameMenuItems() {
        return this.checkSortByChannelNameMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckSortByChannelStatusMenuItems() {
        return this.checkSortByChannelStatusMenuItems;
    }

    public List<CommandCheckMenuItem> getCheckScaleVideoMenuItems() {
        return this.checkScaleVideoMenuItems;
    }

    public JTabbedPane getTabDevice() {
        return this.navigator;
    }

    public void addRadioAbsoluteMouseMenuItem(CommandRadioMenuItem commandRadioMenuItem) {
        this.radioAbsoluteMouseMenuItems.add(commandRadioMenuItem);
    }

    public void removeRadioAbsoluteMouseMenuItem(CommandRadioMenuItem commandRadioMenuItem) {
        this.radioAbsoluteMouseMenuItems.remove(commandRadioMenuItem);
    }

    public void addRadioIntelligentMouseMenuItem(CommandRadioMenuItem commandRadioMenuItem) {
        this.radioIntelligentMouseMenuItems.add(commandRadioMenuItem);
    }

    public void removeRadioIntelligentMouseMenuItem(CommandRadioMenuItem commandRadioMenuItem) {
        this.radioIntelligentMouseMenuItems.remove(commandRadioMenuItem);
    }

    public void addRadioStandardMouseMenuItem(CommandRadioMenuItem commandRadioMenuItem) {
        this.radioStandardMouseMenuItems.add(commandRadioMenuItem);
    }

    public void removeRadioStandardMouseMenuItem(CommandRadioMenuItem commandRadioMenuItem) {
        this.radioStandardMouseMenuItems.remove(commandRadioMenuItem);
    }

    public void addCheckToolbarMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkToolBarMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckToolbarMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkToolBarMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckStatusBarMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkStatusBarMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckStatusBarMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkStatusBarMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckFullScreenMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkFullScreenMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckFullScreenMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkFullScreenMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckNavigatorMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkNavigatorMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckNavigatorMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkNavigatorMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckMessageMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkMessageMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckMessageMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkMessageMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckAllDevicesMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkAllDevicesMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckAllDevicesMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkAllDevicesMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckShowWithoutTargetsMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkShowWithoutTargetsMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckShowWithoutTargetsMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkShowWithoutTargetsMenuItems.remove(commandCheckMenuItem);
    }

    public void addGroupsMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkGroupsMenuItems.add(commandCheckMenuItem);
    }

    public void removeGroupsMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkGroupsMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckShowPowerstripsMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkShowPowerstripsMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckShowPowerstripsMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkShowPowerstripsMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckShowToolsMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkShowToolsMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckShowToolsMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkShowToolsMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckSortByChannelNumberMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkSortByChannelNumberMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckSortByChannelNumberMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkSortByChannelNumberMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckSortByChannelNameMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkSortByChannelNameMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckSortByChannelNameMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkSortByChannelNameMenuItems.remove(commandCheckMenuItem);
    }

    public void addCheckSortByChannelStatusMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkSortByChannelStatusMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckSortByChannelStatusMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkSortByChannelStatusMenuItems.remove(commandCheckMenuItem);
    }

    public void selectSortByChannelNumberView(boolean bl) {
        for (CommandCheckMenuItem commandCheckMenuItem : this.checkSortByChannelNumberMenuItems) {
            commandCheckMenuItem.setSelected(bl);
        }
    }

    public void selectSortByChannelNameView(boolean bl) {
        for (CommandCheckMenuItem commandCheckMenuItem : this.checkSortByChannelNameMenuItems) {
            commandCheckMenuItem.setSelected(bl);
        }
    }

    public void selectSortByChannelStatusView(boolean bl) {
        for (CommandCheckMenuItem commandCheckMenuItem : this.checkSortByChannelStatusMenuItems) {
            commandCheckMenuItem.setSelected(bl);
        }
    }

    public void addCheckScaleVideoMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkScaleVideoMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckScaleVideoMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkScaleVideoMenuItems.remove(commandCheckMenuItem);
    }

    public void setTabDevice(JTabbedPane jTabbedPane) {
        this.navigator = jTabbedPane;
    }

    public void setMessagePane(JPanel jPanel) {
        this.message = jPanel;
    }

    public void setSingleMouseCursorButton(JToggleButton jToggleButton) {
        this.toolBarSingleMouseCursorButton = jToggleButton;
    }

    public void setNavigatorButton(CommandToggleButton commandToggleButton) {
        this.toolBarNavigatorButton = commandToggleButton;
    }

    public void setScaleVideoButton(CommandToggleButton commandToggleButton) {
        this.toolBarScaleVideoButton = commandToggleButton;
    }

    public void setBrowseAllDevicesButton(CommandToggleButton commandToggleButton) {
        this.toolBarBrowseAllDevicesButton = commandToggleButton;
    }

    public void setToolBar(RRCToolBar rRCToolBar) {
        this.toolBar = rRCToolBar;
    }

    public JToolBar getToolBar() {
        return this.toolBar;
    }

    public void addCheckToolBarMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkToolBarMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckToolBarMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkToolBarMenuItems.remove(commandCheckMenuItem);
    }

    public void setStatusBar(RaritanStatusBarPanel raritanStatusBarPanel) {
        this.statusBar = raritanStatusBarPanel;
    }

    public RaritanStatusBarPanel getStatusBar() {
        return this.statusBar;
    }

    public void setLeftVerticalPane(JSplitPane jSplitPane) {
        this.splLeftVertical = jSplitPane;
    }

    public JSplitPane getLeftVerticalPane() {
        return this.splLeftVertical;
    }

    public BaseTreePanel getDeviceByIPTree() {
        return this.deviceByIPTree;
    }

    public void setDeviceByIPTree(BaseTreePanel baseTreePanel) {
        this.deviceByIPTree = baseTreePanel;
    }

    public List<CommandMenu> getWindowMenus() {
        return this.windowMenus;
    }

    public void addWindowMenu(CommandMenu commandMenu) {
        this.windowMenus.add(commandMenu);
    }

    public void removeWindowMenu(CommandMenu commandMenu) {
        this.windowMenus.remove(commandMenu);
    }

    public void enableWindowMenu(boolean bl) {
        for (CommandMenu commandMenu : this.windowMenus) {
            commandMenu.setEnabled(bl);
        }
    }

    public List<CommandMenu> getKeyboardMenus() {
        return this.keyboardMenus;
    }

    public void addKeyboardMenu(CommandMenu commandMenu) {
        this.keyboardMenus.add(commandMenu);
    }

    public void removeKeyboardMenu(CommandMenu commandMenu) {
        this.keyboardMenus.remove(commandMenu);
    }

    public List<CommandMenu> getVirtualMediaMenus() {
        return this.virtualMediaMenus;
    }

    public void addVirtualMediaMenu(CommandMenu commandMenu) {
        this.virtualMediaMenus.add(commandMenu);
    }

    public void removeVirtualMediaMenu(CommandMenu commandMenu) {
        this.virtualMediaMenus.remove(commandMenu);
    }

    public CommandButton getToolBarRefreshButton() {
        return this.toolBarRefreshButton;
    }

    public CommandToggleButton getToolBarScaleVideoButton() {
        return this.toolBarScaleVideoButton;
    }

    public void setToolBarRefreshButton(CommandButton commandButton) {
        this.toolBarRefreshButton = commandButton;
    }

    public JSplitPane getSplHorizontal() {
        return this.splHorizontal;
    }

    public void setSplHorizontal(JSplitPane jSplitPane) {
        this.splHorizontal = jSplitPane;
    }

    public CommandToggleButton getToolBarFullScreenButton() {
        return this.toolBarFullScreenButton;
    }

    public void setToolBarFullScreenButton(CommandToggleButton commandToggleButton) {
        this.toolBarFullScreenButton = commandToggleButton;
    }

    public ConnectedServersToolBar getCsToolBar() {
        return this.csToolBar;
    }

    public void setCsToolBar(ConnectedServersToolBar connectedServersToolBar) {
        this.csToolBar = connectedServersToolBar;
    }

    public List<CommandCheckMenuItem> getCheckCSToolBarMenuItems() {
        return this.checkCSToolBarMenuItems;
    }

    public void addCheckCSToolBarMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkCSToolBarMenuItems.add(commandCheckMenuItem);
    }

    public void removeCheckCSToolBarMenuItem(CommandCheckMenuItem commandCheckMenuItem) {
        this.checkCSToolBarMenuItems.remove(commandCheckMenuItem);
    }

    public void selectCSToolBarView(boolean bl) {
        if (this.csToolBar != null) {
            this.csToolBar.setVisible(bl);
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkCSToolBarMenuItems) {
                commandCheckMenuItem.setSelected(bl);
            }
        } else {
            for (CommandCheckMenuItem commandCheckMenuItem : this.checkCSToolBarMenuItems) {
                commandCheckMenuItem.setSelected(false);
            }
        }
    }
}

