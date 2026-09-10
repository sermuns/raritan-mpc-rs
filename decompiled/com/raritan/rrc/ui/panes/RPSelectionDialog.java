/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.Vector;
import javaclientlib.utils.RRCLogger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

public class RPSelectionDialog
extends JDialog
implements ActionListener,
WindowListener {
    private JPanel optionList;
    private JPanel customOptionList;
    private JButton okButton;
    private JButton cancelButton;
    private JRadioButton customRestore = null;
    private Hashtable<JToggleButton, String> itemToRPID;
    private String[] selectedPackages = null;
    private boolean hasValidPackages = false;
    private static JFrame dummyParentFrame = new JFrame();
    private static Image icon = new BufferedImage(1, 1, 3);
    private RaritanPropertyResourceBundle rprBundle;

    @Override
    public void windowActivated(WindowEvent windowEvent) {
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        this.cancelOperation();
    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {
    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        if (object == this.cancelButton) {
            this.cancelOperation();
        } else if (object == this.okButton) {
            Component[] componentArray = this.customRestore.isSelected() ? this.customOptionList.getComponents() : this.optionList.getComponents();
            Vector<String> vector = new Vector<String>();
            for (int i = 0; i < componentArray.length; ++i) {
                if (!((JToggleButton)componentArray[i]).isSelected()) continue;
                vector.add(this.itemToRPID.get(componentArray[i]));
            }
            if (vector.size() == 0) {
                this.selectedPackages = null;
                JOptionPane.showMessageDialog(this, this.rprBundle.getString("rpid.package.error.NothingSelected"));
                return;
            }
            this.selectedPackages = vector.toArray(new String[0]);
            this.setVisible(false);
        } else if (object instanceof JRadioButton) {
            boolean bl = this.customRestore.isSelected();
            Component[] componentArray = this.customOptionList.getComponents();
            for (int i = 0; i < componentArray.length; ++i) {
                componentArray[i].setEnabled(bl);
                if (bl) continue;
                ((JCheckBox)componentArray[i]).setSelected(false);
            }
        }
    }

    public RPSelectionDialog(Container container, String[] stringArray, RaritanPropertyResourceBundle raritanPropertyResourceBundle) {
        super(dummyParentFrame, raritanPropertyResourceBundle.getString("RPSelectionDialog.title"), true);
        int n;
        dummyParentFrame.setIconImage(icon);
        if (stringArray == null) {
            if (RRCLogger.logEnabled) {
                RRCLogger.log(300, 65, "RPSelectionDialog called with null string array for rpidlist");
            }
            this.hasValidPackages = false;
            return;
        }
        this.setLocationRelativeTo(container);
        this.rprBundle = raritanPropertyResourceBundle;
        this.optionList = new JPanel();
        this.customOptionList = new JPanel();
        this.itemToRPID = new Hashtable();
        ButtonGroup buttonGroup = new ButtonGroup();
        JToggleButton jToggleButton = null;
        String string = null;
        for (n = 0; n < stringArray.length; ++n) {
            if (stringArray[n] == null) continue;
            if (stringArray[n].startsWith("C")) {
                jToggleButton = new JCheckBox();
            } else {
                if (!stringArray[n].startsWith("UC")) continue;
                jToggleButton = new JRadioButton();
            }
            try {
                string = this.rprBundle.getString("rpid.package.label." + stringArray[n]);
            }
            catch (MissingResourceException missingResourceException) {
                continue;
            }
            this.itemToRPID.put(jToggleButton, stringArray[n]);
            jToggleButton.setText(string);
            if (jToggleButton instanceof JRadioButton) {
                buttonGroup.add(jToggleButton);
                this.optionList.add(jToggleButton);
                jToggleButton.addActionListener(this);
                continue;
            }
            this.customOptionList.add(jToggleButton);
        }
        Component[] componentArray = this.customOptionList.getComponents();
        if (componentArray.length > 0) {
            for (n = 0; n < componentArray.length; ++n) {
                componentArray[n].setEnabled(false);
            }
            this.customRestore = new JRadioButton(this.rprBundle.getString("rpid.package.label.CustomRestore"));
            buttonGroup.add(this.customRestore);
            this.optionList.add(this.customRestore);
            this.customRestore.addActionListener(this);
        }
        if ((componentArray = this.optionList.getComponents()).length == 0) {
            this.hasValidPackages = false;
            return;
        }
        ((JRadioButton)componentArray[0]).setSelected(true);
        this.hasValidPackages = true;
        this.makeLayout();
        this.addWindowListener(this);
    }

    private void makeLayout() {
        JPanel jPanel = new JPanel();
        JPanel jPanel2 = new JPanel();
        this.optionList.setLayout(new BoxLayout(this.optionList, 3));
        if (this.customOptionList.getComponents().length > 0) {
            this.customOptionList.setLayout(new BoxLayout(this.customOptionList, 3));
            this.customOptionList.setBorder(BorderFactory.createTitledBorder(this.rprBundle.getString("rpid.package.label.ChooseCustomPackages")));
        }
        this.okButton = new JButton(this.rprBundle.getString("generic.dialog.ok.text"));
        this.cancelButton = new JButton(this.rprBundle.getString("generic.dialog.cancel.text"));
        jPanel2.setLayout(new FlowLayout(1));
        jPanel2.add(this.okButton);
        jPanel2.add(this.cancelButton);
        this.okButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        jPanel.setLayout(new BoxLayout(jPanel, 3));
        jPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        jPanel.add(this.optionList);
        if (this.customOptionList != null) {
            jPanel.add(this.customOptionList);
        }
        jPanel.setAlignmentX(0.5f);
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        container.add((Component)jPanel, "Center");
        container.add((Component)jPanel2, "Last");
        this.okButton.enableInputMethods(false);
        this.cancelButton.enableInputMethods(false);
        this.pack();
        this.setResizable(false);
    }

    private void cancelOperation() {
        this.selectedPackages = null;
        this.setVisible(false);
    }

    public String[] selectRPDialog() {
        if (this.hasValidPackages) {
            this.setVisible(true);
        }
        return this.selectedPackages;
    }

    public boolean hasValidPackages() {
        return this.hasValidPackages;
    }
}

