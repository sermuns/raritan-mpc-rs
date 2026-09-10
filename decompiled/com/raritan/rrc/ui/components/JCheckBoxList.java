/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class JCheckBoxList
extends JList {
    static Color listForeground;
    static Color listBackground;
    static Color listSelectionForeground;
    static Color listSelectionBackground;
    private JCheckBox[] listCheckBoxes;

    public JCheckBoxList() {
        new JCheckBoxList(new String[0]);
    }

    public JCheckBoxList(String[] stringArray) {
        this.listCheckBoxes = new JCheckBox[stringArray.length];
        this.setListData(stringArray);
        for (int i = 0; i < stringArray.length; ++i) {
            this.listCheckBoxes[i] = new JCheckBox(stringArray[i]);
        }
        this.finishConstruction();
    }

    public JCheckBoxList(JCheckBox[] jCheckBoxArray) {
        DefaultListModel<String> defaultListModel = new DefaultListModel<String>();
        this.listCheckBoxes = jCheckBoxArray;
        for (int i = 0; i < jCheckBoxArray.length; ++i) {
            defaultListModel.addElement(jCheckBoxArray[i].getText());
        }
        this.finishConstruction();
        this.setModel(defaultListModel);
    }

    private void finishConstruction() {
        this.addMouseListener(new ListMouseListener());
        this.addKeyListener(new ListKeyListener());
        this.setCellRenderer(new CheckBoxRenderer());
    }

    public int getSelectedCheckBoxCount() {
        int n = 0;
        for (int i = 0; i < this.listCheckBoxes.length; ++i) {
            if (!this.listCheckBoxes[i].isSelected()) continue;
            ++n;
        }
        return n;
    }

    public List getSelectedLabels() {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < this.listCheckBoxes.length; ++i) {
            if (!this.listCheckBoxes[i].isSelected()) continue;
            arrayList.add(this.listCheckBoxes[i].getText());
        }
        return arrayList;
    }

    public void selectAllCheckBoxes(boolean bl) {
        for (int i = 0; i < this.listCheckBoxes.length; ++i) {
            this.listCheckBoxes[i].setSelected(bl);
        }
        this.repaint();
    }

    static {
        UIDefaults uIDefaults = UIManager.getLookAndFeel().getDefaults();
        listForeground = uIDefaults.getColor("List.foreground");
        listBackground = uIDefaults.getColor("List.background");
        listSelectionForeground = uIDefaults.getColor("List.selectionForeground");
        listSelectionBackground = uIDefaults.getColor("List.selectionBackground");
    }

    class CheckBoxRenderer
    implements ListCellRenderer {
        protected Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

        CheckBoxRenderer() {
        }

        public Component getListCellRendererComponent(JList jList, Object object, int n, boolean bl, boolean bl2) {
            JCheckBox jCheckBox = JCheckBoxList.this.listCheckBoxes[n];
            jCheckBox.setBackground(bl ? jList.getSelectionBackground() : jList.getBackground());
            jCheckBox.setForeground(bl ? jList.getSelectionForeground() : jList.getForeground());
            jCheckBox.setEnabled(jList.isEnabled());
            jCheckBox.setFont(jList.getFont());
            jCheckBox.setFocusPainted(false);
            jCheckBox.setBorderPainted(true);
            jCheckBox.setBorder(bl ? UIManager.getBorder("List.focusCellHighlightBorder") : this.noFocusBorder);
            return jCheckBox;
        }
    }

    class ListKeyListener
    extends KeyAdapter {
        ListKeyListener() {
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 32 && keyEvent.getID() == 401 && JCheckBoxList.this.getSelectedIndex() >= 0) {
                JCheckBox jCheckBox;
                jCheckBox.setSelected(!(jCheckBox = JCheckBoxList.this.listCheckBoxes[JCheckBoxList.this.getSelectedIndex()]).isSelected());
                JCheckBoxList.this.repaint();
            }
        }
    }

    class ListMouseListener
    extends MouseAdapter {
        ListMouseListener() {
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            JCheckBox jCheckBox;
            int n = JCheckBoxList.this.locationToIndex(mouseEvent.getPoint());
            if (n < 0 || n >= JCheckBoxList.this.getModel().getSize()) {
                return;
            }
            jCheckBox.setSelected(!(jCheckBox = JCheckBoxList.this.listCheckBoxes[n]).isSelected());
            JCheckBoxList.this.repaint();
        }
    }
}

