/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.displays.Shell;
import com.raritan.tools.ui.panes.displays.ShellForSmartCardDialog;
import javax.swing.SwingUtilities;
import nn.pp.common.smartcard.MountInProgressDialog;
import nn.pp.common.smartcard.SmartCardBean;

public class SmartCardAutoMountPanel
extends AbstractDisplay {
    private ShellForSmartCardDialog shellForSmartCard;

    public SmartCardAutoMountPanel(boolean bl, ScreenContext screenContext, boolean bl2) {
        super(screenContext);
        this.isDialog = true;
        this.isNewPanel = bl2;
        this.shellForSmartCard = new ShellForSmartCardDialog(screenContext);
    }

    @Override
    public Shell getShell() {
        return this.shellForSmartCard;
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        SmartCardBean smartCardBean = (SmartCardBean)commandContext.getCommandParameter("SmartCard_Bean");
        String string = (String)commandContext.getCommandParameter("SmartCard_CardReader_Name");
        String string2 = (String)commandContext.getCommandParameter("SmartCard_Dialog_Title");
        final MountInProgressDialog mountInProgressDialog = new MountInProgressDialog(this.shellForSmartCard, string2, smartCardBean, ((RRCScreenContext)this.scrContext).getSmartCardCore(), string);
        this.shellForSmartCard.setSmartCardDialogAdapter(mountInProgressDialog);
        mountInProgressDialog.pack();
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                mountInProgressDialog.startMounting();
            }
        });
    }

    @Override
    public void makeLayout() {
    }
}

