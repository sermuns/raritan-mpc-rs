/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.data.Port;
import com.raritan.rrc.data.VirtualMediaBean;
import com.raritan.rrc.data.VirtualMediaLocalBean;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.components.CommandMenuItemCache;
import com.raritan.rrc.ui.components.MenuItemResourceBundleConstants;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandMenuItem;
import com.raritan.tools.ui.components.RaritanPopupMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import nn.pp.core.Platform;

public class ContextMenuDevice
extends RaritanPopupMenu
implements PopupMenuListener {
    private static final long serialVersionUID = 4350799418719060415L;
    private AbstractCommand loginCommand;
    private List context = new ArrayList();
    int vitrualMediaConnect = 0;
    private CommandMenuItem menuItem = null;
    private ScreenContext scrContext;

    public ContextMenuDevice(ScreenContext screenContext, AbstractCommand abstractCommand) {
        super(screenContext);
        this.scrContext = screenContext;
        this.loginCommand = abstractCommand;
        this.setContext();
        this.addContext(this.context);
        this.addPopupMenuListener(this);
    }

    public ContextMenuDevice(ScreenContext screenContext, AbstractCommand abstractCommand, int n) {
        super(screenContext);
        this.scrContext = screenContext;
        this.loginCommand = abstractCommand;
        this.setContext();
        this.addContext(this.context);
        this.addPopupMenuListener(this);
        this.vitrualMediaConnect = n;
    }

    @Override
    public void setContext() {
        this.menuItem = new CommandMenuItem(this.bundle.getString(MenuItemResourceBundleConstants.NEW_CONNECTION_ACTION_NAME), this.scrContext);
        this.menuItem.addActionListener(this.mali);
        this.menuItem.addObservable(((RRCScreenContext)this.scrContext).getSelectedDevicesObservable());
        this.menuItem.setCommand(this.loginCommand);
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(MenuItemResourceBundleConstants.NEW_CONNECTION_ACTION_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(MenuItemResourceBundleConstants.NEW_CONNECTION_ACTION_MNEMONIC).toCharArray()[0]).charValue());
        this.context.add(this.menuItem);
        this.context.addAll(CommandMenuItemCache.getInstance(this.scrContext, this.mali).getCommandMenuItems());
        this.refreshExecuteStatus();
    }

    private void refreshExecuteStatus() {
        Port port = null;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList != null && arrayList.size() != 0 && arrayList.get(0) instanceof Port) {
            port = (Port)arrayList.get(0);
        }
        if (port != null) {
            Object object;
            RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
            String string = "";
            string = port.getViewName() + port.getPortIndex();
            Map map = this.scrContext.getVirtualMediaLocalMap();
            Map map2 = this.scrContext.getVirtualMediaImageMap();
            if (map != null && map.size() > 0) {
                object = (VirtualMediaLocalBean)map.get(string);
                if (object != null && ((VirtualMediaLocalBean)object).isDriveConnected()) {
                    CommandMenuItemCache.getInstance().getVirtualMediaLocalMenu().setText(raritanPropertyResourceBundle.getString("VirtualMediaDisconnect") + " " + ((VirtualMediaLocalBean)object).getConnectedDrive());
                } else {
                    CommandMenuItemCache.getInstance().getVirtualMediaLocalMenu().setText(raritanPropertyResourceBundle.getString("VirtualMediaLocalConnect.name"));
                }
            } else {
                CommandMenuItemCache.getInstance().getVirtualMediaLocalMenu().setText(raritanPropertyResourceBundle.getString("VirtualMediaLocalConnect.name"));
            }
            if (!Platform.isVirtualMediaSupported()) {
                CommandMenuItemCache.getInstance().getVirtualMediaLocalMenu().setEnabled(false);
            }
            if (map2 != null && map2.size() > 0) {
                object = (VirtualMediaBean)map2.get(string);
                if (object != null && ((VirtualMediaBean)object).isDriveConnected()) {
                    CommandMenuItemCache.getInstance().getVirtualMediaImageMenu().setText(raritanPropertyResourceBundle.getString("VirtualMediaDisconnect") + " " + ((VirtualMediaBean)object).getConnectedDrive());
                } else {
                    CommandMenuItemCache.getInstance().getVirtualMediaImageMenu().setText(raritanPropertyResourceBundle.getString("VirtualMediaImageConnect.name"));
                }
            } else {
                CommandMenuItemCache.getInstance().getVirtualMediaImageMenu().setText(raritanPropertyResourceBundle.getString("VirtualMediaImageConnect.name"));
            }
        }
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
        this.menuItem.removeObservable(((RRCScreenContext)this.scrContext).getSelectedDevicesObservable());
        Thread thread = new Thread(){

            @Override
            public void run() {
                ((RRCScreenContext)ContextMenuDevice.this.scrContext).resetDefaultFocus();
            }
        };
        thread.start();
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
    }
}

