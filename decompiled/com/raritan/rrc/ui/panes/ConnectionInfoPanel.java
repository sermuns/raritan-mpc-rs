/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.DeviceConnector;
import com.raritan.rrc.data.IPReach;
import com.raritan.rrc.ui.commands.DoCopyToClipboardCommand;
import com.raritan.rrc.ui.panes.DeviceView;
import com.raritan.rrc.ui.panes.KvmView;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.Command;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import javaclientlib.tr.TRRSP_NEW_VIDEO_MODE_DATA;
import javaclientlib.utils.RRCLogger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import nn.pp.rccore.IVideoSettings;
import nn.pp.rccore.RCAdapter;
import nn.pp.rccore.RCCore;

public class ConnectionInfoPanel
extends AbstractDisplay {
    private static final long serialVersionUID = -3541216498677992876L;
    private static final int MILLISECONDS_IN_SECOND = 1000;
    private static final int MILLISECONDS_IN_MINUTE = 60000;
    private static final int MILLISECONDS_IN_HOUR = 3600000;
    private static final int MILLISECONDS_IN_DAY = 86400000;
    private final String[] headerItems;
    private JTable table;
    private DefaultTableModel model;
    private IPReach dev;
    private DeviceConnector devCon;
    private TRRSP_NEW_VIDEO_MODE_DATA videoMode;
    private final String[] labels;
    private Timer updateTimer;
    private long lastTime;
    private long lastDataIn;
    private long lastDataOut;
    private DeviceView parentView;
    private RCCore rccore;
    private RCAdapter adapter;

    public ConnectionInfoPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.headerItems = this.bundle.getMultyValue("connectioninfo.tableheader.lable");
        this.labels = new String[24];
        this.parentView = null;
        this.rccore = null;
        this.adapter = null;
        this.isDialog = bl;
        this.labels[0] = this.bundle.getString("connectioninfo.devicename.row");
        this.labels[1] = this.bundle.getString("connectioninfo.ipaddress.row");
        this.labels[2] = this.bundle.getString("connectioninfo.port.row");
        this.labels[3] = this.bundle.getString("connectioninfo.datainpersecond.row");
        this.labels[4] = this.bundle.getString("connectioninfo.dataoutpersecond.row");
        this.labels[5] = this.bundle.getString("connectioninfo.connecttime.row");
        this.labels[6] = this.bundle.getString("connectioninfo.horizontalresolution.row");
        this.labels[7] = this.bundle.getString("connectioninfo.verticalresolution.row");
        this.labels[8] = this.bundle.getString("connectioninfo.refreshrate.row");
        this.labels[9] = this.bundle.getString("connectioninfo.protocolversion.row");
        this.labels[10] = this.bundle.getString("connectioninfo.oldestsupportedversion.row");
        this.labels[11] = this.bundle.getString("connectioninfo.hardwareversion.row");
        this.labels[12] = this.bundle.getString("connectioninfo.softwareversion.row");
        this.labels[13] = this.bundle.getString("connectioninfo.postcode.row");
        this.labels[14] = this.bundle.getString("connectioninfo.networkflags.row");
        this.labels[15] = this.bundle.getString("connectioninfo.securityflags.row");
        this.labels[16] = this.bundle.getString("connectioninfo.options.row");
        this.labels[17] = this.bundle.getString("connectioninfo.framegrabberinfo.row");
        this.labels[18] = this.bundle.getString("connectioninfo.kmeinfo.row");
        this.labels[19] = this.bundle.getString("connectioninfo.serialinfo.row");
        this.labels[20] = this.bundle.getString("connectioninfo.videodevicescount.row");
        this.labels[21] = this.bundle.getString("connectioninfo.serialdevicescount.row");
        this.labels[22] = this.bundle.getString("connectioninfo.reserved.row");
        this.labels[23] = this.bundle.getString("connectioninfo.FPS.row");
        this.makeLayout();
        this.setShell(this.bundle.getString("connectioninfo.panel.title"));
    }

    @Override
    public void makeLayout() {
        this.setLayout(new BorderLayout());
        this.model = new DefaultTableModel(this.headerItems, 0);
        this.table = new JTable(this.model);
        TableColumn tableColumn = this.table.getColumn(this.headerItems[1]);
        int n = tableColumn.getWidth();
        tableColumn = this.table.getColumn(this.headerItems[0]);
        n = tableColumn.getWidth();
        this.table.setEnabled(false);
        this.table.setShowGrid(false);
        this.table.setModel(this.model);
        this.table.setRowSelectionAllowed(false);
        this.table.setColumnSelectionAllowed(false);
        JScrollPane jScrollPane = new JScrollPane(this.table);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add((Component)jScrollPane, "Center");
        this.add((Component)this.doButtonWidget(), "South");
    }

    @Override
    public JPanel doButtonWidget() {
        this.ok = new CommandButton(this.bundle.getString("connectioninfo.copytoclipboard.button"), this.scrContext);
        this.ok.setCommand(new DoCopyToClipboardCommand(this.scrContext));
        this.ok.addActionListener(this);
        this.cancel = new CommandButton(this.bundle.getString("connectioninfo.close.button"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(1));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        return jPanel;
    }

    private void addRows(Class clazz) {
        int n;
        if (this.model != null && this.model.getRowCount() > 0) {
            for (n = this.model.getRowCount() - 1; n >= 0; --n) {
                this.model.removeRow(n);
            }
        }
        if (KvmView.class.equals((Object)clazz)) {
            for (n = 0; n < this.labels.length; ++n) {
                this.model.addRow(new Object[]{new Integer(n), this.labels[n], null});
            }
        } else if (RFBView.class.equals((Object)clazz)) {
            this.model.addRow(new Object[]{new Integer(0), this.labels[0], null});
            this.model.addRow(new Object[]{new Integer(1), this.labels[1], null});
            this.model.addRow(new Object[]{new Integer(2), this.labels[2], null});
            this.model.addRow(new Object[]{new Integer(3), this.labels[3], null});
            this.model.addRow(new Object[]{new Integer(4), this.labels[4], null});
            this.model.addRow(new Object[]{new Integer(5), this.labels[23], null});
            this.model.addRow(new Object[]{new Integer(6), this.labels[5], null});
            this.model.addRow(new Object[]{new Integer(7), this.labels[6], null});
            this.model.addRow(new Object[]{new Integer(8), this.labels[7], null});
            this.model.addRow(new Object[]{new Integer(9), this.labels[8], null});
            this.model.addRow(new Object[]{new Integer(10), this.labels[9], null});
        } else {
            throw new IllegalArgumentException("Unknown view " + clazz);
        }
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.dev = (IPReach)commandContext.getCommandParameter("deviceNode");
        this.devCon = this.dev.getDeviceConnector();
        this.videoMode = (TRRSP_NEW_VIDEO_MODE_DATA)commandContext.getCommandParameter("VIDEO_MODE");
        this.parentView = (DeviceView)commandContext.getCommandParameter("DEVICE_VIEW");
        if (this.parentView == null) {
            throw new NullPointerException("Parent view is null in command context");
        }
        this.lastTime = System.currentTimeMillis();
        this.lastDataIn = this.devCon.getDataIn();
        this.lastDataOut = this.devCon.getDataOut();
        this.addRows(this.parentView.getClass());
        if (this.parentView instanceof KvmView) {
            this.setKX1StaticData(commandContext);
            this.updateTable((KvmView)this.parentView);
        } else if (this.parentView instanceof RFBView) {
            this.rccore = ((RFBView)this.parentView).getRCCore();
            this.adapter = new RCAdapter(){

                @Override
                public void videoSettingsUpdated(IVideoSettings iVideoSettings) {
                    if (iVideoSettings.getRefreshRate().isSupported()) {
                        if (iVideoSettings.getRefreshRate().isSupported()) {
                            ConnectionInfoPanel.this.model.setValueAt(iVideoSettings.getRefreshRate().getValue() + " Hz", 9, 2);
                        }
                        ConnectionInfoPanel.this.rccore.removeVideoEventListener(ConnectionInfoPanel.this.adapter);
                    }
                }

                @Override
                public void deviceNameChanged(String string) {
                    ConnectionInfoPanel.this.model.setValueAt(string, 0, 2);
                }
            };
            this.rccore.addVideoEventListener(this.adapter, 8);
            this.rccore.addConnectionEventListener(this.adapter, 128);
            try {
                this.rccore.requestVideoSettingsUpdates();
            }
            catch (IOException iOException) {
                RRCLogger.log(150, "Unable to request Video Settings Updates");
            }
            this.setKX2StaticData(commandContext);
            this.updateTable((RFBView)this.parentView);
        }
        int n = ConnectionInfoPanel.initColumnSizes(this.table);
        n += this.table.getIntercellSpacing().width * this.table.getColumnCount();
        this.table.setPreferredScrollableViewportSize(new Dimension(n += 50, this.table.getRowHeight() * this.table.getRowCount()));
        this.getShell().pack();
        this.startUpdateTimer();
    }

    private void setKX2StaticData(CommandContext commandContext) {
        Map map = (Map)commandContext.getCommandParameter("STATIC_CONN_INFO");
        if (map != null) {
            this.model.setValueAt(this.rccore.getDeviceName(), 0, 2);
            String string = (String)map.get("IP");
            if (string != null) {
                this.model.setValueAt(string, 1, 2);
            } else {
                this.model.setValueAt("", 1, 2);
            }
            string = map.get("PORT");
            if (string != null) {
                this.model.setValueAt(string, 2, 2);
            } else {
                this.model.setValueAt("", 2, 2);
            }
            IVideoSettings iVideoSettings = ((RFBView)this.parentView).getVideoSettings();
            this.model.setValueAt("", 7, 2);
            this.model.setValueAt("", 8, 2);
            this.model.setValueAt("", 9, 2);
            if (iVideoSettings != null) {
                if (iVideoSettings.getResolutionX().isSupported()) {
                    this.model.setValueAt(String.valueOf(iVideoSettings.getResolutionX().getValue()), 7, 2);
                }
                if (iVideoSettings.getResolutionY().isSupported()) {
                    this.model.setValueAt(String.valueOf(iVideoSettings.getResolutionY().getValue()), 8, 2);
                }
                if (iVideoSettings.getRefreshRate().isSupported()) {
                    this.model.setValueAt(iVideoSettings.getRefreshRate().getValue() + " Hz", 9, 2);
                }
            } else {
                RRCLogger.log(300, 512, "Unable to fetch Video Settings");
            }
            if ((string = map.get("PROTO_VER")) != null) {
                this.model.setValueAt(string, 10, 2);
            }
        }
    }

    private void setKX1StaticData(CommandContext commandContext) {
        Map map = (Map)commandContext.getCommandParameter("STATIC_CONN_INFO");
        if (map != null) {
            String string = (String)map.get("DESCRIPTION");
            if (string != null) {
                this.model.setValueAt(string, 0, 2);
            } else {
                this.model.setValueAt("", 0, 2);
            }
            string = (String)map.get("IP");
            if (string != null) {
                this.model.setValueAt(string, 1, 2);
            } else {
                this.model.setValueAt("", 1, 2);
            }
            string = map.get("PORT");
            if (string != null) {
                this.model.setValueAt(string, 2, 2);
            } else {
                this.model.setValueAt("", 2, 2);
            }
            string = map.get("SCREEN_SIZE");
            if (string != null) {
                Dimension dimension = (Dimension)((Object)string);
                this.model.setValueAt(new Integer(dimension.width), 6, 2);
                this.model.setValueAt(new Integer(dimension.height), 7, 2);
            } else {
                this.model.setValueAt("", 22, 2);
            }
            string = map.get("PROTO_VER");
            if (string != null) {
                this.model.setValueAt(string, 9, 2);
            } else {
                this.model.setValueAt("", 9, 2);
            }
            string = map.get("OLDEST_PROTO_VER");
            if (string != null) {
                this.model.setValueAt(string, 10, 2);
            } else {
                this.model.setValueAt("", 10, 2);
            }
            string = map.get("HW_VER");
            if (string != null) {
                this.model.setValueAt(string, 11, 2);
            } else {
                this.model.setValueAt("", 11, 2);
            }
            string = map.get("SW_VER");
            if (string != null) {
                this.model.setValueAt(string, 12, 2);
            } else {
                this.model.setValueAt("", 12, 2);
            }
            string = map.get("POST");
            if (string != null) {
                this.model.setValueAt(string, 13, 2);
            } else {
                this.model.setValueAt("", 13, 2);
            }
            string = map.get("NET_FLAGS");
            if (string != null) {
                this.model.setValueAt(string, 14, 2);
            } else {
                this.model.setValueAt("", 14, 2);
            }
            string = map.get("SECURITY_FLAGS");
            if (string != null) {
                this.model.setValueAt(string, 15, 2);
            } else {
                this.model.setValueAt("", 15, 2);
            }
            string = map.get("OPTIONS");
            if (string != null) {
                this.model.setValueAt(string, 16, 2);
            } else {
                this.model.setValueAt("", 16, 2);
            }
            string = map.get("FG_INFO");
            if (string != null) {
                this.model.setValueAt(string, 17, 2);
            } else {
                this.model.setValueAt("", 17, 2);
            }
            string = map.get("KVM_INFO");
            if (string != null) {
                this.model.setValueAt(string, 18, 2);
            } else {
                this.model.setValueAt("", 18, 2);
            }
            string = map.get("SERIAL_INFO");
            if (string != null) {
                this.model.setValueAt(string, 19, 2);
            } else {
                this.model.setValueAt("", 19, 2);
            }
            string = map.get("NUM_VD_DEVICES");
            if (string != null) {
                this.model.setValueAt(string, 20, 2);
            } else {
                this.model.setValueAt("", 20, 2);
            }
            string = map.get("NUM_SERIAL_DEVICES");
            if (string != null) {
                this.model.setValueAt(string, 21, 2);
            } else {
                this.model.setValueAt("", 21, 2);
            }
            string = map.get("RESERVED");
            if (string != null) {
                this.model.setValueAt(string, 22, 2);
            } else {
                this.model.setValueAt("", 22, 2);
            }
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        StringBuffer stringBuffer = new StringBuffer();
        Vector<Vector> vector = this.model.getDataVector();
        for (int i = 0; i < this.model.getRowCount(); ++i) {
            stringBuffer.append(vector.elementAt(i).elementAt(0));
            stringBuffer.append(" ");
            stringBuffer.append(vector.elementAt(i).elementAt(1));
            stringBuffer.append(" : ");
            stringBuffer.append(vector.elementAt(i).elementAt(2));
            stringBuffer.append("\n");
        }
        commandContext.setCommandParameter("clipboardText", stringBuffer.toString());
    }

    private void updateTable(KvmView kvmView) {
        if (this.dev != null && this.devCon != null && this.videoMode != null) {
            long l;
            int n;
            long l2 = System.currentTimeMillis();
            int n2 = this.devCon.getDataIn();
            if (this.lastDataIn <= (long)n2) {
                n = 0;
                l = (long)n2 - this.lastDataIn;
                if (l2 != this.lastTime) {
                    n = (int)(l * 1000L / (l2 - this.lastTime));
                }
                this.model.setValueAt(new Integer(n), 3, 2);
            }
            this.lastDataIn = n2;
            n = this.devCon.getDataOut();
            if (this.lastDataOut <= (long)n) {
                int n3 = 0;
                long l3 = (long)n - this.lastDataOut;
                if (l2 != this.lastTime) {
                    n3 = (int)(l3 * 1000L / (l2 - this.lastTime));
                }
                this.model.setValueAt(new Integer(n3), 4, 2);
            }
            this.lastDataOut = n;
            this.lastTime = l2;
            l = l2 - this.devCon.lConnectTime;
            int n4 = (int)l / 86400000;
            int n5 = (int)(l -= (long)(n4 * 86400000)) / 3600000;
            int n6 = (int)(l -= (long)(n5 * 3600000)) / 60000;
            int n7 = (int)(l -= (long)(n6 * 60000)) / 1000;
            StringBuffer stringBuffer = new StringBuffer();
            if (n4 > 0) {
                stringBuffer.append(n4);
                stringBuffer.append(" days ");
            }
            if (n5 < 10) {
                stringBuffer.append("0");
            }
            stringBuffer.append(n5);
            stringBuffer.append(":");
            if (n6 < 10) {
                stringBuffer.append("0");
            }
            stringBuffer.append(n6);
            stringBuffer.append(":");
            if (n7 < 10) {
                stringBuffer.append("0");
            }
            stringBuffer.append(n7);
            this.model.setValueAt(stringBuffer.toString(), 5, 2);
            this.model.setValueAt(new Short(this.videoMode.getActualHSize()), 6, 2);
            this.model.setValueAt(new Short(this.videoMode.getActualVSize()), 7, 2);
            short s = this.videoMode.getRefresh();
            if (s != 0) {
                this.model.setValueAt(new Double((double)(s / 100) + (double)(s % 100) * 0.01) + " Hz", 8, 2);
            } else {
                this.model.setValueAt("0", 8, 2);
            }
        }
    }

    private void updateTable(RFBView rFBView) {
        if (this.dev != null && this.devCon != null) {
            long l = System.currentTimeMillis();
            int n = this.rccore.getIncomingTrafficSpeed();
            this.model.setValueAt(this.formatSpeed(n), 3, 2);
            int n2 = this.rccore.getOutgoingTrafficSpeed();
            this.model.setValueAt(this.formatSpeed(n2), 4, 2);
            this.model.setValueAt(String.valueOf(rFBView.getFpsCount()), 5, 2);
            long l2 = l - this.devCon.lConnectTime;
            int n3 = (int)l2 / 86400000;
            int n4 = (int)(l2 -= (long)(n3 * 86400000)) / 3600000;
            int n5 = (int)(l2 -= (long)(n4 * 3600000)) / 60000;
            int n6 = (int)(l2 -= (long)(n5 * 60000)) / 1000;
            StringBuffer stringBuffer = new StringBuffer();
            if (n3 > 0) {
                stringBuffer.append(n3);
                stringBuffer.append(" days ");
            }
            if (n4 < 10) {
                stringBuffer.append("0");
            }
            stringBuffer.append(n4);
            stringBuffer.append(":");
            if (n5 < 10) {
                stringBuffer.append("0");
            }
            stringBuffer.append(n5);
            stringBuffer.append(":");
            if (n6 < 10) {
                stringBuffer.append("0");
            }
            stringBuffer.append(n6);
            this.model.setValueAt(stringBuffer.toString(), 6, 2);
        }
    }

    public void startUpdateTimer() {
        this.updateTimer = new Timer(1000, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (ConnectionInfoPanel.this.parentView instanceof KvmView) {
                    ConnectionInfoPanel.this.updateTable((KvmView)ConnectionInfoPanel.this.parentView);
                } else if (ConnectionInfoPanel.this.parentView instanceof RFBView) {
                    ConnectionInfoPanel.this.updateTable((RFBView)ConnectionInfoPanel.this.parentView);
                }
            }
        });
        this.updateTimer.start();
    }

    public void stopUpdateTimer() {
        if (this.updateTimer != null && this.updateTimer.isRunning()) {
            this.updateTimer.stop();
            this.updateTimer = null;
        }
    }

    @Override
    protected CommandResult executeCommand(Command command) {
        command.getContext(true);
        this.setCommandContext(command.getContext());
        this.feedCommandContext(command.getContext());
        CommandResult commandResult = command.execute();
        if (command.getKey().equals("cancelButtonCommand")) {
            this.stopUpdateTimer();
        }
        if (!commandResult.isSuccess() && commandResult.hasErrorDescription()) {
            this.handleCommandResultErrorDescription(commandResult);
        } else if (command.getKey().equals("doCopyToClipboardCommand")) {
            this.handleCommandResult(commandResult);
            this.scrContext.getPanelMediator().showPanel(command.getContext());
        } else {
            this.handleCommandResult(commandResult);
            this.setVisibleAfterCommand(command);
            this.scrContext.getPanelMediator().showPanel(command.getContext());
        }
        return commandResult;
    }

    private static int initColumnSizes(JTable jTable) {
        TableModel tableModel = jTable.getModel();
        int n = 0;
        TableColumn tableColumn = null;
        Component component = null;
        int n2 = 0;
        int n3 = 0;
        int n4 = jTable.getColumnCount();
        int n5 = jTable.getRowCount();
        TableCellRenderer tableCellRenderer = jTable.getTableHeader().getDefaultRenderer();
        for (int i = 0; i < n4; ++i) {
            tableColumn = jTable.getColumnModel().getColumn(i);
            component = tableCellRenderer.getTableCellRendererComponent(null, tableColumn.getHeaderValue(), false, false, 0, 0);
            n2 = component.getPreferredSize().width;
            TableCellRenderer tableCellRenderer2 = jTable.getDefaultRenderer(tableModel.getColumnClass(i));
            for (int j = 0; j < n5; ++j) {
                component = tableCellRenderer2.getTableCellRendererComponent(jTable, tableModel.getValueAt(j, i), false, false, j, i);
                n3 = Math.max(n3, component.getPreferredSize().width);
            }
            tableColumn.setPreferredWidth(Math.max(n2, n3));
            n += tableColumn.getPreferredWidth();
            n3 = 0;
        }
        return n;
    }

    public String formatSpeed(int n) {
        String string = n > 1024 ? Integer.toString(n / 1024) + " " + this.bundle.getString("connectioninfo.kilobyte") : Integer.toString(n) + " " + this.bundle.getString("connectioninfo.byte");
        return string;
    }
}

