/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.KvmPort;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.displays.Shell;
import com.raritan.tools.ui.panes.displays.ShellForSmartCardDialog;
import java.util.ArrayList;
import nn.pp.common.smartcard.SmartCardReaderDialog;

public class SmartCardPanel
extends AbstractDisplay {
    private ShellForSmartCardDialog shellForSmartCard;
    private SmartCardReaderDialog smartCardReaderDialog;

    public SmartCardPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = true;
        this.shellForSmartCard = new ShellForSmartCardDialog(screenContext);
        this.smartCardReaderDialog = new SmartCardReaderDialog(this.shellForSmartCard, ((RRCScreenContext)screenContext).getSmartCardCore());
        this.shellForSmartCard.setSmartCardDialogAdapter(this.smartCardReaderDialog);
        this.shellForSmartCard.pack();
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
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        KvmPort kvmPort = (KvmPort)arrayList.get(0);
        this.smartCardReaderDialog.setSmartcardBean(kvmPort.getSelectCardReaderAction().getConfiguredSmartCardBean());
    }

    @Override
    public void makeLayout() {
    }
}

