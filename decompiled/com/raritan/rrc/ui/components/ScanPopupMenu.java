/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.commands.ShowScanFrameCommand;
import com.raritan.rrc.ui.components.MenuItemResourceBundleConstants;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandMenuItem;
import com.raritan.tools.ui.components.RaritanPopupMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class ScanPopupMenu
extends RaritanPopupMenu
implements PopupMenuListener {
    private CommandMenuItem menuItem = null;
    private List<CommandMenuItem> context = new ArrayList<CommandMenuItem>();
    private RRCScreenContext screenContext;

    public ScanPopupMenu(ScreenContext screenContext) {
        super(screenContext);
        this.screenContext = (RRCScreenContext)screenContext;
        this.setContext();
        this.addContext(this.context);
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
    }

    @Override
    public void setContext() {
        this.menuItem = new CommandMenuItem(this.bundle.getString(MenuItemResourceBundleConstants.SCAN_START), this.scrContext);
        this.menuItem.addActionListener(this.mali);
        this.menuItem.setCommand(new ShowScanFrameCommand(this.screenContext));
        if (this.scrContext.getLocale() == Locale.US || this.scrContext.getLocale() == Locale.UK) {
            this.menuItem.setDisplayedMnemonicIndex(Integer.parseInt(this.bundle.getString(MenuItemResourceBundleConstants.SCAN_START_MNEMONIC_INDEX)));
        }
        this.menuItem.setMnemonic(new Character(this.bundle.getString(MenuItemResourceBundleConstants.SCAN_START_MNEMONIC).toCharArray()[0]).charValue());
        this.context.add(this.menuItem);
    }
}

