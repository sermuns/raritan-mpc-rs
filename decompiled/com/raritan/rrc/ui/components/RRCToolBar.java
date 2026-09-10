/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ConnectAudioCommand;
import com.raritan.rrc.ui.commands.DoAutoSenseCommand;
import com.raritan.rrc.ui.commands.DoCalibrateColorCommand;
import com.raritan.rrc.ui.commands.DoCaptureTargetScreenshotCommand;
import com.raritan.rrc.ui.commands.DoEnterOnscreenMenuCommand;
import com.raritan.rrc.ui.commands.DoExitOnscreenMenuCommand;
import com.raritan.rrc.ui.commands.DoRefreshNavigatorCommand;
import com.raritan.rrc.ui.commands.DoRefreshScreenCommand;
import com.raritan.rrc.ui.commands.DoSendCtrlAltDelCommand;
import com.raritan.rrc.ui.commands.DoSynchronizeMouseCommand;
import com.raritan.rrc.ui.commands.SelectCardReaderCommand;
import com.raritan.rrc.ui.commands.ShowAboutCommand;
import com.raritan.rrc.ui.commands.ShowAllDevicesCommand;
import com.raritan.rrc.ui.commands.ShowNavigatorCommand;
import com.raritan.rrc.ui.commands.ShowNewProfileCommand;
import com.raritan.rrc.ui.commands.ShowPropertiesCommand;
import com.raritan.rrc.ui.commands.ShowSingleCursorInstructionCommand;
import com.raritan.rrc.ui.commands.ShowTargetScreenResolutionCommand;
import com.raritan.rrc.ui.commands.ShowVideoScaleCommand;
import com.raritan.rrc.ui.commands.ShowVideoSettingsCommand;
import com.raritan.rrc.ui.commands.SmartCardMenuCommand;
import com.raritan.rrc.ui.components.CommandToggleButton;
import com.raritan.rrc.ui.components.MenuItemResourceBundleConstants;
import com.raritan.tools.commands.CommandHolder;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.commands.ConfirmableCommandInterface;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.util.CommandUtil;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import javax.swing.BorderFactory;
import javax.swing.JToolBar;
import javax.swing.border.CompoundBorder;
import nn.pp.common.ResourceLoader;

public class RRCToolBar
extends JToolBar
implements MouseListener {
    private static final long serialVersionUID = 1550118845028268726L;
    private static final String NEW_PROFILE_IMAGE = "NewProfileAction.image";
    private static final String NEW_PROFILE_TOOLTIP = "NewProfileAction.tooltip";
    private static final String PROPERTIES_IMAGE = "PropertiesAction.image";
    private static final String PROFILE_TOOLTIP = "PropertiesAction.tooltip";
    private static final String VIDEO_SETTINGS_IMAGE = "VideoSettingsAction.image";
    private static final String VIDEO_SETTINGS_TOOLTIP = "VideoSettingsAction.tooltip";
    private static final String COLOR_CALIBRATE_IMAGE = "ColorCalibrateAction.image";
    private static final String COLOR_CALIBRATE_TOOLTIP = "ColorCalribrateAction.tooltip";
    private static final String CAPTURE_SCREENSHOT_IMAGE = "Screenshot.image";
    private static final String CAPTURE_SCREENSHOT_TOOLIP = "Screenshot.tooltip";
    private static final String VIRTUAL_MEDIA_IMAGE = "virtualMedia.toolbar.image";
    private static final String VIRTUAL_MEDIA_TOOLTIP = "virtualMedia.tooltip";
    private static final String SYNCHRONIZE_MOUSE_IMAGE = "SynchronizeMouseAction.image";
    private static final String SYNCHRONIZE_MOUSE_TOOLTIP = "SynchronizeMouseAction.tooltip";
    private static final String REFRESH_SCREEN_IMAGE = "RefreshScreenAction.image";
    private static final String REFRESH_SCREEN_TOOLTIP = "RefreshScreenAction.tooltip";
    private static final String SMARTCARD_SELECT_CARDREADER_IMAGE = "SmartCardSelectCardReader.toolbar.image";
    private static final String AUTO_SENSE_IMAGE = "AutoSenseAction.image";
    private static final String AUTO_SENSE_TOOLTIP = "AutoSenseAction.tooltip";
    private static final String ENTER_ONSCREEN_MENU_IMAGE = "EnterOnScreenMenuAction.image";
    private static final String ENTER_ONSCREEN_MENU_TOOLTIP = "EnterOnScreenMenuAction.tooltip";
    private static final String EXIT_ONSCREEN_MENU_IMAGE = "ExitOnScreenMenuAction.image";
    private static final String EXIT_ONSCREEN_MENU_TOOLTIP = "ExitOnScreenMenuAction.tooltip";
    private static final String SEND_CTRL_ALT_DEL_IMAGE = "SendCtrlAltDeleteAction.image";
    private static final String SEND_CTRL_ALT_DEL_TOOLTIP = "SendCtrlAltDeleteAction.tooltip";
    private static final String SINGLE_MOUSE_CURSOR_IMAGE = "SingleMouseCursorAction.image";
    private static final String SINGLE_MOUSE_CURSOR_TOOLTIP = "SingleMouseCursorAction.tooltip";
    private static final String FULL_SCREEN_IMAGE = "FullScreenAction.image";
    private static final String FULL_SCREEN_TOOLTIP = "FullScreenAction.tooltip";
    private static final String NAVIGATOR_IMAGE = "NavigatorAction.image";
    private static final String NAVIGATOR_TOOLTIP = "NavigatorAction.tooltip";
    private static final String REFRESH_NAVIGATOR_IMAGE = "RefreshNavigatorAction.image";
    private static final String REFRESH_NAVIGATOR_TOOLTIP = "RefreshNavigatorAction.tooltip";
    private static final String BROWSE_IMAGE = "BrowseAction.image";
    private static final String BROWSE_TOOLTIP = "BrowseAction.tooltip";
    private static final String ABOUT_IMAGE = "AboutAction.image";
    private static final String ABOUT_TOOLTIP = "AboutAction.tooltip";
    private static final String SCALE_VIDEO_IMAGE = "ScaleVideoAction.image";
    private static final String SCALE_VIDEO_TOOLTIP = "ScaleVideoAction.tooltip";
    private static final String AUDIO_IMAGE = "Audio.image";
    protected RRCScreenContext scrContext;
    protected RaritanPropertyResourceBundle bundle;
    private CommandButton toolBarButton;
    private CommandButton selectCardReaderButton;
    private CommandToggleButton toolBarFullScreenButton;
    private CommandToggleButton toolBarSingleMouseCursorButton;
    private CommandToggleButton toolBarBrowseAllDevicesButton;
    private CommandToggleButton toolBarNavigatorButton;
    private CommandToggleButton toolBarScaleVideoButton;
    private CommandToggleButton toolBarAudioButton;
    private static Object lastPressed = null;
    private static boolean useLastPressed = false;

    public RRCToolBar(RRCScreenContext rRCScreenContext) {
        this.scrContext = rRCScreenContext;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        Dimension dimension = new Dimension(30, 30);
        Dimension dimension2 = new Dimension(30, 30);
        Dimension dimension3 = new Dimension(5, 25);
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1), BorderFactory.createRaisedBevelBorder());
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(NEW_PROFILE_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.setCommand(new ShowNewProfileCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(NEW_PROFILE_TOOLTIP));
        this.add(this.toolBarButton);
        this.addSeparator(dimension3);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(PROPERTIES_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarButton.setCommand(new ShowPropertiesCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(PROFILE_TOOLTIP));
        this.add(this.toolBarButton);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(VIDEO_SETTINGS_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarButton.setCommand(new ShowVideoSettingsCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(VIDEO_SETTINGS_TOOLTIP));
        this.add(this.toolBarButton);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(COLOR_CALIBRATE_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarButton.setCommand(new DoCalibrateColorCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(COLOR_CALIBRATE_TOOLTIP));
        this.add(this.toolBarButton);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(CAPTURE_SCREENSHOT_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarButton.setCommand(new DoCaptureTargetScreenshotCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(CAPTURE_SCREENSHOT_TOOLIP));
        this.add(this.toolBarButton);
        this.addSeparator(dimension3);
        this.toolBarAudioButton = new CommandToggleButton((ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                ConnectAudioCommand connectAudioCommand = (ConnectAudioCommand)this.getCommand();
                if (connectAudioCommand.isExecutable()) {
                    this.setToolTipText(connectAudioCommand.isConnected() ? RRCToolBar.this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_DISCONNECT) : RRCToolBar.this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT));
                    this.setSelected(connectAudioCommand.isConnected());
                } else {
                    this.setToolTipText(connectAudioCommand.getToolTip());
                }
            }
        };
        this.toolBarAudioButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(AUDIO_IMAGE)));
        this.toolBarAudioButton.addMouseListener(this);
        this.toolBarAudioButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarAudioButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarAudioButton.addObservable(this.scrContext.getAudioObserver());
        this.toolBarAudioButton.setCommand(new ConnectAudioCommand(this.scrContext));
        this.toolBarAudioButton.setOpaque(false);
        this.toolBarAudioButton.setMinimumSize(dimension2);
        this.toolBarAudioButton.setMaximumSize(dimension2);
        this.toolBarAudioButton.setPreferredSize(dimension2);
        this.toolBarAudioButton.setToolTipText(this.bundle.getString(MenuItemResourceBundleConstants.AUDIO_CONNECT));
        this.add(this.toolBarAudioButton);
        this.addSeparator(dimension3);
        this.toolBarButton = new CommandButton((ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                DoSynchronizeMouseCommand doSynchronizeMouseCommand = (DoSynchronizeMouseCommand)this.getCommand();
                String string = doSynchronizeMouseCommand.getTooltipText();
                if (string == null) {
                    string = RRCToolBar.this.bundle.getString(RRCToolBar.SYNCHRONIZE_MOUSE_TOOLTIP);
                }
                this.setToolTipText(string);
            }
        };
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(SYNCHRONIZE_MOUSE_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarButton.setCommand(new DoSynchronizeMouseCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(SYNCHRONIZE_MOUSE_TOOLTIP));
        this.add(this.toolBarButton);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(REFRESH_SCREEN_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarButton.setCommand(new DoRefreshScreenCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(REFRESH_SCREEN_TOOLTIP));
        this.add(this.toolBarButton);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(AUTO_SENSE_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarButton.setCommand(new DoAutoSenseCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(AUTO_SENSE_TOOLTIP));
        this.add(this.toolBarButton);
        this.addSeparator(dimension3);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(ENTER_ONSCREEN_MENU_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarButton.setCommand(new DoEnterOnscreenMenuCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(ENTER_ONSCREEN_MENU_TOOLTIP));
        this.add(this.toolBarButton);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(EXIT_ONSCREEN_MENU_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarButton.setCommand(new DoExitOnscreenMenuCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(EXIT_ONSCREEN_MENU_TOOLTIP));
        this.add(this.toolBarButton);
        this.selectCardReaderButton = new CommandButton((ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                this.setToolTipText(((SmartCardMenuCommand)this.getCommand()).getToolTip());
            }
        };
        this.selectCardReaderButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(SMARTCARD_SELECT_CARDREADER_IMAGE)));
        this.selectCardReaderButton.addMouseListener(this);
        this.selectCardReaderButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.selectCardReaderButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.selectCardReaderButton.addObservable(this.scrContext.getSmartCardObserver());
        this.selectCardReaderButton.setCommand(new SelectCardReaderCommand(this.scrContext));
        this.selectCardReaderButton.setOpaque(false);
        this.selectCardReaderButton.setMinimumSize(dimension);
        this.selectCardReaderButton.setMaximumSize(dimension);
        this.selectCardReaderButton.setPreferredSize(dimension);
        this.selectCardReaderButton.setBorder(compoundBorder);
        this.add(this.selectCardReaderButton);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(SEND_CTRL_ALT_DEL_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarButton.setCommand(new DoSendCtrlAltDelCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(SEND_CTRL_ALT_DEL_TOOLTIP));
        this.add(this.toolBarButton);
        this.addSeparator(dimension3);
        this.toolBarSingleMouseCursorButton = new CommandToggleButton((ScreenContext)this.scrContext){

            @Override
            public void update(Observable observable, Object object) {
                super.update(observable, object);
                ShowSingleCursorInstructionCommand showSingleCursorInstructionCommand = (ShowSingleCursorInstructionCommand)this.getCommand();
                String string = showSingleCursorInstructionCommand.getTooltipText();
                if (string == null) {
                    string = RRCToolBar.this.bundle.getString(RRCToolBar.SINGLE_MOUSE_CURSOR_TOOLTIP);
                }
                this.setToolTipText(string);
            }
        };
        this.toolBarSingleMouseCursorButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(SINGLE_MOUSE_CURSOR_IMAGE)));
        this.scrContext.getMainScreenMediator().setSingleMouseCursorButton(this.toolBarSingleMouseCursorButton);
        this.toolBarSingleMouseCursorButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarSingleMouseCursorButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarSingleMouseCursorButton.setCommand(new ShowSingleCursorInstructionCommand(this.scrContext));
        this.toolBarSingleMouseCursorButton.setOpaque(false);
        this.toolBarSingleMouseCursorButton.setMinimumSize(dimension2);
        this.toolBarSingleMouseCursorButton.setMaximumSize(dimension2);
        this.toolBarSingleMouseCursorButton.setPreferredSize(dimension2);
        this.toolBarSingleMouseCursorButton.setToolTipText(this.bundle.getString(SINGLE_MOUSE_CURSOR_TOOLTIP));
        this.toolBarSingleMouseCursorButton.addMouseListener(this);
        this.add(this.toolBarSingleMouseCursorButton);
        this.toolBarFullScreenButton = new CommandToggleButton(this.scrContext);
        this.toolBarFullScreenButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(FULL_SCREEN_IMAGE)));
        this.scrContext.getMainScreenMediator().setToolBarFullScreenButton(this.toolBarFullScreenButton);
        this.toolBarFullScreenButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarFullScreenButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarFullScreenButton.setCommand(new ShowTargetScreenResolutionCommand(this.scrContext));
        this.toolBarFullScreenButton.setOpaque(false);
        this.toolBarFullScreenButton.setMinimumSize(dimension2);
        this.toolBarFullScreenButton.setMaximumSize(dimension2);
        this.toolBarFullScreenButton.setPreferredSize(dimension2);
        this.toolBarFullScreenButton.setToolTipText(this.bundle.getString(FULL_SCREEN_TOOLTIP));
        this.toolBarFullScreenButton.addMouseListener(this);
        this.add(this.toolBarFullScreenButton);
        this.toolBarScaleVideoButton = new CommandToggleButton(this.scrContext);
        this.toolBarScaleVideoButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(SCALE_VIDEO_IMAGE)));
        this.toolBarScaleVideoButton.setCommand(new ShowVideoScaleCommand(this.scrContext));
        this.scrContext.getMainScreenMediator().setScaleVideoButton(this.toolBarScaleVideoButton);
        this.toolBarScaleVideoButton.addObservable(this.scrContext.getSelectedDevicesObservable());
        this.toolBarScaleVideoButton.addObservable(this.scrContext.getOpenPortsObservable());
        this.toolBarScaleVideoButton.addMouseListener(this);
        this.toolBarScaleVideoButton.setOpaque(false);
        this.toolBarScaleVideoButton.setMinimumSize(dimension2);
        this.toolBarScaleVideoButton.setMaximumSize(dimension2);
        this.toolBarScaleVideoButton.setPreferredSize(dimension2);
        this.toolBarScaleVideoButton.setToolTipText(this.bundle.getString(SCALE_VIDEO_TOOLTIP));
        this.add(this.toolBarScaleVideoButton);
        this.addSeparator(dimension3);
        this.toolBarNavigatorButton = new CommandToggleButton(this.scrContext);
        this.toolBarNavigatorButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(NAVIGATOR_IMAGE)));
        this.toolBarNavigatorButton.setCommand(new ShowNavigatorCommand(this.scrContext));
        this.scrContext.getMainScreenMediator().setNavigatorButton(this.toolBarNavigatorButton);
        this.toolBarNavigatorButton.addMouseListener(this);
        this.toolBarNavigatorButton.setOpaque(false);
        this.toolBarNavigatorButton.setMinimumSize(dimension2);
        this.toolBarNavigatorButton.setMaximumSize(dimension2);
        this.toolBarNavigatorButton.setPreferredSize(dimension2);
        this.toolBarNavigatorButton.setToolTipText(this.bundle.getString(NAVIGATOR_TOOLTIP));
        this.add(this.toolBarNavigatorButton);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(REFRESH_NAVIGATOR_IMAGE)));
        this.scrContext.getMainScreenMediator().setToolBarRefreshButton(this.toolBarButton);
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.setCommand(new DoRefreshNavigatorCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(REFRESH_NAVIGATOR_TOOLTIP));
        this.add(this.toolBarButton);
        this.toolBarBrowseAllDevicesButton = new CommandToggleButton(this.scrContext);
        this.toolBarBrowseAllDevicesButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(BROWSE_IMAGE)));
        this.scrContext.getMainScreenMediator().setBrowseAllDevicesButton(this.toolBarBrowseAllDevicesButton);
        this.toolBarBrowseAllDevicesButton.setCommand(new ShowAllDevicesCommand(this.scrContext));
        this.toolBarBrowseAllDevicesButton.addMouseListener(this);
        this.toolBarBrowseAllDevicesButton.setOpaque(false);
        this.toolBarBrowseAllDevicesButton.setMinimumSize(dimension2);
        this.toolBarBrowseAllDevicesButton.setMaximumSize(dimension2);
        this.toolBarBrowseAllDevicesButton.setPreferredSize(dimension2);
        this.toolBarBrowseAllDevicesButton.setToolTipText(this.bundle.getString(BROWSE_TOOLTIP));
        this.add(this.toolBarBrowseAllDevicesButton);
        this.addSeparator(dimension3);
        this.toolBarButton = new CommandButton(this.scrContext);
        this.toolBarButton.setIcon(ResourceLoader.loadImageIcon(this.bundle.getString(ABOUT_IMAGE)));
        this.toolBarButton.addMouseListener(this);
        this.toolBarButton.setCommand(new ShowAboutCommand(this.scrContext));
        this.toolBarButton.setOpaque(false);
        this.toolBarButton.setMinimumSize(dimension);
        this.toolBarButton.setMaximumSize(dimension);
        this.toolBarButton.setPreferredSize(dimension);
        this.toolBarButton.setBorder(compoundBorder);
        this.toolBarButton.setToolTipText(this.bundle.getString(ABOUT_TOOLTIP));
        this.add(this.toolBarButton);
        this.addSeparator(dimension3);
    }

    protected void handleCommandResult(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    protected void handleCommandResultErrorDescription(CommandResult commandResult) {
        if (commandResult == null) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        if (commandResult.getErrorDescription() != null && commandResult.getErrorDescription().length > 0) {
            for (int i = 0; i < commandResult.getErrorDescription().length; ++i) {
                stringBuffer.append(commandResult.getErrorDescription()[i]);
                stringBuffer.append("\n");
            }
        } else if (commandResult.getStatusMessage() != null) {
            stringBuffer.append(commandResult.getStatusMessage());
        } else {
            stringBuffer.append("No command result info.");
        }
        CommonPopups.showCommandResultErrorMessage(stringBuffer.toString(), null, (ScreenContext)this.scrContext);
        this.scrContext.getLogger().logStatus(commandResult.getStatusMessage());
        this.scrContext.getLogger().logTextInfo(commandResult.getStatusMessage());
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == lastPressed) {
            useLastPressed = true;
        }
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == lastPressed) {
            useLastPressed = false;
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        lastPressed = mouseEvent.getSource();
        useLastPressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        Object object = mouseEvent.getSource();
        if (object instanceof CommandButton && !((CommandButton)object).isEnabled()) {
            return;
        }
        if (object instanceof CommandToggleButton && !((CommandToggleButton)object).isEnabled()) {
            return;
        }
        if (object != lastPressed || !useLastPressed) {
            lastPressed = null;
            useLastPressed = false;
            return;
        }
        lastPressed = null;
        useLastPressed = false;
        boolean bl = false;
        if (object instanceof ConfirmableCommandInterface) {
            bl = ((ConfirmableCommandInterface)mouseEvent.getSource()).getConfirmation();
        }
        if (CommandUtil.isCommandHolder(object)) {
            if (bl) {
                boolean bl2 = bl = CommonPopups.showExitConfirmationDialog(this.scrContext.getApplication().getContentPane(), (ScreenContext)this.scrContext) != 2;
            }
            if (!bl) {
                CommandResult commandResult;
                boolean bl3 = false;
                String string = null;
                if (object instanceof CommandToggleButton) {
                    if ((CommandToggleButton)object == this.toolBarNavigatorButton) {
                        bl3 = this.toolBarNavigatorButton.isSelected();
                        string = "showNavigatorMode";
                    }
                    if ((CommandToggleButton)object == this.toolBarBrowseAllDevicesButton) {
                        bl3 = this.toolBarBrowseAllDevicesButton.isSelected();
                        string = "showAllDevicesMode";
                    }
                    if ((CommandToggleButton)object == this.toolBarSingleMouseCursorButton) {
                        this.toolBarSingleMouseCursorButton.setSelected(!this.toolBarSingleMouseCursorButton.isSelected());
                    }
                    if ((CommandToggleButton)object == this.toolBarFullScreenButton) {
                        bl3 = this.toolBarFullScreenButton.isSelected();
                        string = "showFullScreenMode";
                    }
                    if ((CommandToggleButton)object == this.toolBarScaleVideoButton) {
                        bl3 = this.toolBarScaleVideoButton.isSelected();
                        string = "scaleVideoMode";
                    }
                }
                CommandHolder commandHolder = (CommandHolder)object;
                if (string != null) {
                    commandHolder.getCommand().getContext(true).setCommandParameter(string, new Boolean(bl3));
                }
                if (!(commandResult = commandHolder.getCommand().execute()).isSuccess() && commandResult.hasErrorDescription()) {
                    this.handleCommandResultErrorDescription(commandResult);
                } else {
                    this.handleCommandResult(commandResult);
                    this.scrContext.getPanelMediator().showPanel(commandHolder.getCommand().getContext());
                }
                this.scrContext.resetDefaultFocus();
            }
        }
    }
}

