/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.data.KeyboardMacrosPreferences;
import com.raritan.rrc.ui.components.JCheckBoxList;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

public class MacroSelectionPanel
extends AbstractDisplay
implements ActionListener,
ItemListener {
    private static final long serialVersionUID = 2028595561540428642L;
    private JCheckBoxList macroList;
    private JScrollPane macroScrollPane;
    protected JButton selectAll;
    protected JButton deselectAll;
    protected TreeMap macros;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);
        Object object = actionEvent.getSource();
        if (object == this.selectAll || object == this.deselectAll) {
            this.macroList.selectAllCheckBoxes(object == this.selectAll);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        this.ok.setEnabled(this.macroList.getSelectedCheckBoxCount() > 0);
    }

    public MacroSelectionPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.makeLayout();
    }

    @Override
    public void makeLayout() {
        this.selectAll = new JButton(this.bundle.getString("generic.dialog.selectall.text"));
        this.deselectAll = new JButton(this.bundle.getString("generic.dialog.deselectall.text"));
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel jPanel = new JPanel(new SpringLayout());
        jPanel.add(this.selectAll);
        jPanel.add(this.deselectAll);
        SpringUtilities.makeCompactGrid(jPanel, 2, 1, 5, 5, 5, 5);
        this.macroScrollPane = new JScrollPane(new JPanel(), 20, 30);
        this.macroScrollPane.setPreferredSize(new Dimension(200, 150));
        JPanel jPanel2 = new JPanel(new SpringLayout());
        jPanel2.add(this.macroScrollPane);
        jPanel2.add(jPanel);
        SpringUtilities.makeCompactGrid(jPanel2, 1, 2, 15, 15, 15, 15);
        this.add((Component)jPanel2, "Center");
        this.add((Component)this.doButtonWidget(), "Last");
        this.selectAll.addActionListener(this);
        this.deselectAll.addActionListener(this);
    }

    @Override
    public void feedCommandContext(CommandContext commandContext) {
        List list = this.macroList.getSelectedLabels();
        TreeMap treeMap = new TreeMap();
        for (int i = 0; i < list.size(); ++i) {
            treeMap.put(list.get(i), (KeyboardMacrosPreferences)this.macros.get(list.get(i)));
        }
        this.macros = treeMap;
        commandContext.setCommandParameter("importExportSelectedMacros", this.macros);
        commandContext.setCommandParameter("importExportSelectedMacrosCommandParent", this);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.macros = (TreeMap)commandContext.getCommandParameter("importExportSelectedMacros");
        String[] stringArray = this.macros.keySet().toArray(new String[0]);
        JCheckBox[] jCheckBoxArray = new JCheckBox[stringArray.length];
        for (int i = 0; i < stringArray.length; ++i) {
            jCheckBoxArray[i] = new JCheckBox(stringArray[i]);
            jCheckBoxArray[i].addItemListener(this);
        }
        this.macroList = new JCheckBoxList(jCheckBoxArray);
        this.macroScrollPane.setViewportView(this.macroList);
        this.macroList.selectAllCheckBoxes(true);
        this.ok.setEnabled(true);
    }
}

