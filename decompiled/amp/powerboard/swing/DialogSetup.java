/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.swing;

import amp.powerboard.swing.OutletTextField;
import amp.powerboard.swing.StateManager;
import amp.powerboard.utils.PbResource;
import amp.powerboard.utils.SpringUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.plaf.basic.BasicOptionPaneUI;

public class DialogSetup
extends JDialog {
    StateManager stateManager;
    String[] relayNames;
    String[] relayStatus;
    String[] relayFeedback;
    OutletTextField[] outletNames;
    String errorMessage;
    Hashtable changeList = new Hashtable(10);
    Hashtable nameChangeList = new Hashtable(20);
    int cycleTime;
    int numRelays;
    private JPanel configPanel;
    private JPanel buttonPane;
    private JPanel panelRelay;
    JTextField textFieldUnitId = new JTextField(20);
    JTextField textFieldAlarm = new JTextField(10);
    private static String dialogTitle = PbResource.getString("ConfigDialog.title");
    private static String[] options = new String[]{PbResource.getString("ConfigDialog.okLabel"), PbResource.getString("ConfigDialog.cancelLabel")};
    private String validationErrMsg = PbResource.getString("ValidationErrorMessage");

    public DialogSetup(Frame frame) {
        super(frame, dialogTitle, true);
        this.getContentPane().setLayout(new BorderLayout());
        JOptionPane.setRootFrame(frame);
        this.configPanel = new JPanel(new SpringLayout());
        this.configPanel.setBackground(Color.lightGray);
        JPanel jPanel = new JPanel();
        JLabel jLabel = new JLabel("", 4);
        JLabel jLabel2 = new JLabel("", 4);
        JLabel jLabel3 = new JLabel("", 4);
        JLabel jLabel4 = new JLabel("", 4);
        jPanel.setLayout(new SpringLayout());
        jLabel.setText(PbResource.getString("UnitIdLabel.name"));
        jLabel.setFont(new Font("Dialog", 1, 12));
        jLabel.setLabelFor(this.textFieldUnitId);
        this.textFieldUnitId.setMaximumSize(this.textFieldUnitId.getPreferredSize());
        jPanel.add(jLabel);
        jPanel.add(this.textFieldUnitId);
        jPanel.add(Box.createVerticalGlue());
        jLabel2.setText(PbResource.getString("AlarmThreshHoldLabel.name"));
        jLabel2.setFont(new Font("Dialog", 1, 12));
        this.textFieldAlarm.setMaximumSize(this.textFieldAlarm.getPreferredSize());
        jLabel2.setLabelFor(this.textFieldAlarm);
        jPanel.add(jLabel2);
        jPanel.add(this.textFieldAlarm);
        jLabel3.setText("Amps");
        jLabel3.setFont(new Font("Dialog", 1, 12));
        jPanel.add(jLabel3);
        SpringUtilities.makeCompactGrid(jPanel, 2, 3, 6, 6, 10, 4);
        this.panelRelay = new JPanel();
        this.panelRelay.setLayout(new SpringLayout());
        String string = PbResource.getString("RelayPanel.name");
        this.panelRelay.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(string), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        this.configPanel.add(jPanel);
        this.configPanel.add(this.panelRelay);
        SpringUtilities.makeCompactGrid(this.configPanel, 2, 1, 6, 6, 4, 4);
        JButton jButton = new JButton(options[0]);
        JButton jButton2 = new JButton(options[1]);
        ButtonActionListener buttonActionListener = new ButtonActionListener();
        jButton.addActionListener(buttonActionListener);
        jButton2.addActionListener(buttonActionListener);
        this.getRootPane().setDefaultButton(jButton);
        this.buttonPane = new JPanel();
        this.buttonPane.setLayout(new BasicOptionPaneUI.ButtonAreaLayout(true, 6));
        this.buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        this.buttonPane.add(jButton);
        this.buttonPane.add(jButton2);
        this.getContentPane().add((Component)this.configPanel, "Center");
        this.getContentPane().add((Component)this.buttonPane, "South");
        this.setResizable(false);
        this.setCursor(PbResource.getDefaultCursor());
    }

    public DialogSetup(StateManager stateManager, String string, boolean bl) {
        this(stateManager.getFrame());
        this.stateManager = stateManager;
        this.fillFields();
        this.pack();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int n = this.getSize().width;
        int n2 = this.getSize().height;
        this.setLocation(n / 2, n2 / 2);
    }

    void fillFields() {
        this.textFieldUnitId.setText(this.stateManager.getUnitId());
        this.textFieldAlarm.setText("" + this.stateManager.getAlarmThreshold());
        this.relayNames = this.stateManager.getRelayStatus().getRelayNames();
        this.outletNames = new OutletTextField[this.stateManager.getNumOutlets()];
        JLabel[] jLabelArray = new JLabel[this.stateManager.getNumOutlets()];
        int n = 4;
        int n2 = this.stateManager.getNumOutlets();
        if (n2 == 8) {
            n = 2;
        } else if (n2 == 12) {
            n = 3;
        } else if (n2 == 18 || n2 == 20) {
            n = 4;
        }
        int n3 = n2 / n;
        if (n2 % n > 0) {
            ++n3;
        }
        int n4 = 0;
        int n5 = 0;
        while (n5 < n) {
            JPanel jPanel = new JPanel(new SpringLayout());
            int n6 = 0;
            while (n6 < n3) {
                Object object;
                if (n4 < n2) {
                    object = this.relayNames[n4];
                    this.outletNames[n4] = new OutletTextField();
                    this.outletNames[n4].setText((String)object);
                    jLabelArray[n4] = new JLabel("" + (n4 + 1));
                    jLabelArray[n4].setHorizontalAlignment(4);
                    jLabelArray[n4].setLabelFor(this.outletNames[n4]);
                    jPanel.add(jLabelArray[n4]);
                    jPanel.add(this.outletNames[n4]);
                } else {
                    object = new JLabel();
                    JTextField jTextField = new JTextField();
                    ((JComponent)object).setVisible(false);
                    jTextField.setVisible(false);
                    jPanel.add((Component)object);
                    jPanel.add(jTextField);
                }
                ++n4;
                ++n6;
            }
            SpringUtilities.makeCompactGrid(jPanel, jPanel.getComponentCount() / 2, 2, 0, 0, 4, 0);
            this.panelRelay.add(jPanel);
            ++n5;
        }
        SpringUtilities.makeCompactGrid(this.panelRelay, 1, n, 20, 6, 20, 0);
    }

    private boolean isConfigChanged() {
        boolean bl = false;
        int n = 0;
        while (n < this.relayNames.length) {
            if (!this.relayNames[n].equals(this.outletNames[n].getText())) {
                bl = true;
                this.nameChangeList.put("" + (n + 1), this.outletNames[n].getText());
            }
            ++n;
        }
        if (!this.stateManager.getUnitId().equals(this.textFieldUnitId.getText())) {
            bl = true;
            this.changeList.put("5", this.textFieldUnitId.getText());
        }
        if (this.stateManager.getAlarmThreshold() != new Float(this.textFieldAlarm.getText()).floatValue()) {
            bl = true;
            this.changeList.put("6", this.textFieldAlarm.getText());
        }
        return bl;
    }

    private boolean isConfigValid() {
        try {
            float f = new Float(this.textFieldAlarm.getText()).floatValue();
            return true;
        }
        catch (NumberFormatException numberFormatException) {
            this.errorMessage = PbResource.getString("InvalidConfigErrorMessage");
            JOptionPane.showMessageDialog(this.stateManager.getFrame(), this.errorMessage, PbResource.getString("ValidationErrorMessage.title"), 0);
            this.textFieldAlarm.setCaretPosition(this.textFieldAlarm.getText().length());
            return false;
        }
    }

    boolean validateOutletNames() {
        boolean bl = true;
        int n = 0;
        while (n < this.outletNames.length) {
            if (this.outletNames[n].getText().indexOf(".") != -1 || this.outletNames[n].getText().indexOf("(") != -1 || this.outletNames[n].getText().indexOf(")") != -1 || this.outletNames[n].getText().indexOf(":") != -1) {
                this.errorMessage = this.validationErrMsg;
                bl = false;
                break;
            }
            ++n;
        }
        return bl;
    }

    class ButtonActionListener
    implements ActionListener {
        ButtonActionListener() {
        }

        public void actionPerformed(ActionEvent actionEvent) {
            JButton jButton = (JButton)actionEvent.getSource();
            if (jButton.getText().equalsIgnoreCase(options[0])) {
                DialogSetup.this.setCursor(PbResource.getWaitCursor());
                boolean bl = DialogSetup.this.isConfigValid();
                boolean bl2 = DialogSetup.this.validateOutletNames();
                if (bl && bl2) {
                    if (DialogSetup.this.isConfigChanged()) {
                        DialogSetup.this.stateManager.save(DialogSetup.this.changeList, DialogSetup.this.nameChangeList);
                    }
                    DialogSetup.this.setVisible(false);
                    DialogSetup.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(DialogSetup.this.stateManager.getFrame(), DialogSetup.this.errorMessage, PbResource.getString("ValidationErrorMessage.title"), 0);
                    DialogSetup.this.setCursor(PbResource.getDefaultCursor());
                }
            } else {
                DialogSetup.this.setVisible(false);
                DialogSetup.this.dispose();
            }
        }
    }
}

