/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.components.ContextPopupMenu;
import com.raritan.rrc.ui.panes.DefaultCommandHandler;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.ICommandHandler;
import com.raritan.rrc.ui.panes.KvmPanel;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javaclientlib.tr.TRLIB_UPDATEINFO;
import javaclientlib.tr.TRRSP_NEW_VIDEO_MODE_DATA;
import javaclientlib.tr.TRSRVR_COMP_PARAMS;
import javaclientlib.tr.TRSRVR_VIDEO_PARAMS;

public class DeviceViewAdapter
extends DeviceView {
    private DefaultCommandHandler defaultCommandHandler = new DefaultCommandHandler();

    public DeviceViewAdapter(RRCScreenContext rRCScreenContext) {
        super(rRCScreenContext);
    }

    @Override
    public void makeLayout() {
    }

    @Override
    public boolean disconnect() {
        return false;
    }

    @Override
    public boolean isScaleVideoFlag() {
        return false;
    }

    @Override
    public boolean isTargetScreenResolution() {
        return false;
    }

    @Override
    public void newVideoModeNotify(TRRSP_NEW_VIDEO_MODE_DATA tRRSP_NEW_VIDEO_MODE_DATA) {
    }

    @Override
    public void notify(int n, int n2) {
    }

    @Override
    public void removeListeners() {
    }

    @Override
    public void setSingleCursor(boolean bl) {
    }

    @Override
    public void setUpdateFrequency(long l) {
    }

    @Override
    public void updateNotify(TRLIB_UPDATEINFO tRLIB_UPDATEINFO) {
    }

    @Override
    public void setTargetScreenResolution(boolean bl) {
    }

    @Override
    public void autoSenseVideo() {
    }

    @Override
    public void calibrateColor() {
    }

    @Override
    public void enterOnScreenMenu() {
    }

    @Override
    public void exitOnScreenMenu() {
    }

    @Override
    public void synchronizeMouse(boolean bl) {
    }

    @Override
    public void setCompParameters(TRSRVR_COMP_PARAMS tRSRVR_COMP_PARAMS) {
    }

    @Override
    public void refreshScreen() {
    }

    @Override
    public void ResumeVideo() {
    }

    public KvmPanel getKvmPanel() {
        return null;
    }

    @Override
    public ContextPopupMenu getContextPopupMenu() {
        return null;
    }

    @Override
    public void sendCtrlAltDelete() {
    }

    @Override
    public void sendAltTab() {
    }

    @Override
    public void sendCtrlNumlock() {
    }

    @Override
    public void sendKVMPopupKey() {
    }

    @Override
    public void updateVideoSettings(TRSRVR_VIDEO_PARAMS tRSRVR_VIDEO_PARAMS) {
    }

    @Override
    public void toggleTargetScreenResolution() {
    }

    @Override
    public void setScaleVideoFlag(boolean bl) {
    }

    @Override
    public void setViewFocus() {
    }

    @Override
    public void keyboardTypeChanged(int n) {
    }

    @Override
    public boolean isCommandOperable(AbstractCommand abstractCommand) {
        return true;
    }

    @Override
    public void customizeTarget(AbstractDisplay abstractDisplay) {
    }

    @Override
    public void forceRepaint() {
    }

    @Override
    public boolean macroMenuActionPerformed(String string) {
        return false;
    }

    @Override
    public ICommandHandler getCommandHandler() {
        return this.defaultCommandHandler;
    }

    @Override
    public boolean isFullScreenMode() {
        return false;
    }

    @Override
    public void setFullScreenMode(boolean bl) {
    }

    @Override
    public void addCustomMouseMotionListener(MouseMotionListener mouseMotionListener) {
    }

    @Override
    public void addCustomMouseListener(MouseListener mouseListener) {
    }

    @Override
    public void removeCustomMouseListener(MouseListener mouseListener) {
    }

    @Override
    public void removeCustomMouseMotionListener(MouseMotionListener mouseMotionListener) {
    }

    @Override
    public boolean getAbsoluteMouseSupported() {
        return this.isAbsoluteMouseSupported;
    }

    @Override
    public boolean getIntelligentMouseSupported() {
        return this.isIntelligentMouseSupported;
    }

    @Override
    public boolean getStandardMouseSupported() {
        return this.isStandardMouseSupported;
    }

    @Override
    public void setIsAbsoluteMouseSupported(boolean bl) {
        this.isAbsoluteMouseSupported = bl;
    }

    @Override
    public void setIsIntelligentMouseSupported(boolean bl) {
        this.isIntelligentMouseSupported = bl;
    }

    @Override
    public void setIsStandardMouseSupported(boolean bl) {
        this.isStandardMouseSupported = bl;
    }

    @Override
    public void setContextMenuKVMVisible(boolean bl) {
    }

    @Override
    public boolean isSingleCursor() {
        return false;
    }
}

