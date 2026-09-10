/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.DoSerialParametersCommand;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javaclientlib.tr.TRSRVR_SERIAL_PARAMS;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

public class SerialParametersPanel
extends AbstractDisplay {
    private static final long serialVersionUID = -6123899233869686923L;
    private TRSRVR_SERIAL_PARAMS serialParams = null;
    private final String[] baudRateValues = new String[]{"110", "300", "600", "1200", "2400", "4800", "9600", "19200", "38400", "57600", "115200"};
    private final String[] dataBitsValues = new String[]{"5", "6", "7", "8"};
    private final String[] parityValues = new String[5];
    private final String[] stopBitsValues = new String[]{"1", "1.5", "2"};
    private DefaultComboBoxModel baudRateModel;
    private DefaultComboBoxModel dataBitsModel;
    private DefaultComboBoxModel parityModel;
    private DefaultComboBoxModel stopBitsModel;

    public SerialParametersPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.parityValues[0] = this.bundle.getString("parityNone");
        this.parityValues[1] = this.bundle.getString("parityEven");
        this.parityValues[2] = this.bundle.getString("parityOdd");
        this.parityValues[3] = this.bundle.getString("parityMark");
        this.parityValues[4] = this.bundle.getString("paritySpace");
        this.makeLayout();
        this.setShell(this.bundle.getString("SerialParametersDialog.title"));
        this.ok.setCommand(new DoSerialParametersCommand(this.scrContext));
    }

    @Override
    public void makeLayout() {
        this.initComboDataModels();
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JPanel jPanel = new JPanel(new SpringLayout());
        jPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25), BorderFactory.createTitledBorder("")));
        JLabel jLabel = new JLabel(this.bundle.getString("baudRate"), 11);
        jPanel.add(jLabel);
        JComboBox jComboBox = new JComboBox(this.baudRateModel);
        jPanel.add(jComboBox);
        jLabel.setLabelFor(jComboBox);
        jLabel = new JLabel(this.bundle.getString("dataBits"), 11);
        jPanel.add(jLabel);
        jComboBox = new JComboBox(this.dataBitsModel);
        jPanel.add(jComboBox);
        jLabel.setLabelFor(jComboBox);
        jLabel = new JLabel(this.bundle.getString("parity"), 11);
        jPanel.add(jLabel);
        jComboBox = new JComboBox(this.parityModel);
        jPanel.add(jComboBox);
        jLabel.setLabelFor(jComboBox);
        jLabel = new JLabel(this.bundle.getString("stopBits"), 11);
        jPanel.add(jLabel);
        jComboBox = new JComboBox(this.stopBitsModel);
        jPanel.add(jComboBox);
        jLabel.setLabelFor(jComboBox);
        SpringUtilities.makeCompactGrid(jPanel, 4, 2, 15, 15, 15, 15);
        JTabbedPane jTabbedPane = new JTabbedPane();
        String string = this.bundle.getString("SerialParametersTab.name");
        jTabbedPane.addTab(string, jPanel);
        this.add((Component)jTabbedPane, "Center");
        this.add((Component)this.doButtonWidget(), "Last");
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.clearFields();
        this.serialParams = (TRSRVR_SERIAL_PARAMS)commandContext.getCommandParameter("serialParams");
        if (this.serialParams != null) {
            this.baudRateModel.setSelectedItem(String.valueOf(this.serialParams.getBaudRate()));
            this.dataBitsModel.setSelectedItem(String.valueOf(this.serialParams.getDataBits()));
            this.parityModel.setSelectedItem(this.parityValues[this.serialParams.getParity()]);
            this.stopBitsModel.setSelectedItem(this.stopBitsValues[this.serialParams.getStopBits()]);
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        this.serialParams.setBaudRate(Integer.parseInt((String)this.baudRateModel.getSelectedItem()));
        this.serialParams.setDataBits(Short.parseShort((String)this.dataBitsModel.getSelectedItem()));
        String string = (String)this.parityModel.getSelectedItem();
        this.serialParams.setParity((short)this.parityModel.getIndexOf(string));
        string = (String)this.stopBitsModel.getSelectedItem();
        this.serialParams.setStopBits((short)this.stopBitsModel.getIndexOf(string));
        commandContext.setCommandParameter("serialParams", this.serialParams);
    }

    private void clearFields() {
        this.baudRateModel.setSelectedItem(this.baudRateValues[6]);
        this.dataBitsModel.setSelectedItem(this.dataBitsValues[3]);
        this.parityModel.setSelectedItem(this.parityValues[0]);
        this.stopBitsModel.setSelectedItem(this.stopBitsValues[0]);
    }

    private void initComboDataModels() {
        this.baudRateModel = new DefaultComboBoxModel<String>(this.baudRateValues);
        this.dataBitsModel = new DefaultComboBoxModel<String>(this.dataBitsValues);
        this.parityModel = new DefaultComboBoxModel<String>(this.parityValues);
        this.stopBitsModel = new DefaultComboBoxModel<String>(this.stopBitsValues);
    }

    @Override
    public JPanel doButtonWidget() {
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.addActionListener(this);
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        JPanel jPanel = new JPanel(new FlowLayout(2));
        jPanel.add(this.ok);
        jPanel.add(this.cancel);
        return jPanel;
    }
}

