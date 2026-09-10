/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.swing;

import amp.powerboard.component.PowerboardInterface;
import amp.powerboard.component.RelayStatus;
import amp.powerboard.component.SystemStatus;
import amp.powerboard.swing.StateManager;
import amp.powerboard.utils.PbResource;
import amp.powerboard.utils.SpringUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class Powerboard
extends JFrame
implements MouseListener {
    StateManager stateManager;
    boolean refreshed = false;
    String codeBaseString;
    int columns;
    int rows;
    boolean initialized = false;
    ImageIcon grUp;
    ImageIcon grDown;
    ImageIcon grDis;
    ImageIcon redUp;
    ImageIcon redDown;
    ImageIcon redDis;
    ImageIcon pbLogo;
    ImageIcon raritanLogo;
    JLabel[] lblOutlets;
    JButton[] jibOutlets;
    private JPanel pbPanel;
    private PowerboardInterface applet;
    JLabel lblUnitId = new JLabel();
    JPanel panelSystemStatus = new JPanel();
    JPanel panelOutlets = new JPanel();
    JPanel panelButtons = new JPanel();
    JPanel panelLogos = new JPanel();
    JButton btnClose = new JButton();
    JButton btnConfig = new JButton();
    JPanel panelBorder = new JPanel();
    JLabel label4 = new JLabel("", 4);
    JLabel label5 = new JLabel("", 4);
    JLabel label6 = new JLabel("", 4);
    JLabel label7 = new JLabel("", 4);
    JLabel label8 = new JLabel("", 4);
    JLabel label9 = new JLabel("", 4);
    JLabel label10 = new JLabel("", 4);
    JLabel lblAvPower = new JLabel("tempvalue");
    JLabel lblRMSCurrent = new JLabel("tempvalue");
    JLabel lblRMSVolt = new JLabel("tempvalue");
    JLabel lblIntTemp = new JLabel("tempvalue");
    JLabel lblAppPower = new JLabel("tempvalue");
    JLabel lblMaxDetected = new JLabel("tempvalue");
    JLabel lblCircBrk = new JLabel("tempvalue");
    JLabel lblPowerBoard = new JLabel("tempvalue");
    JLabel jibRaritan = new JLabel();
    Font font18;
    Font font22;

    public Powerboard(String string, StateManager stateManager) {
        this.stateManager = stateManager;
        this.getImages();
        this.font18 = new Font("Arial", 1, Powerboard.adjustedFontSize(18));
        this.font22 = new Font("Arial", 1, Powerboard.adjustedFontSize(22));
        this.pbPanel = new JPanel();
    }

    public void initUI(int n) {
        Object object;
        this.setBackground(Color.white);
        this.pbPanel.setBackground(Color.white);
        SpringLayout springLayout = new SpringLayout();
        this.pbPanel.setLayout(springLayout);
        this.panelLogos.setLayout(new GridLayout(1, 3));
        this.panelLogos.setBackground(Color.white);
        this.panelLogos.setMaximumSize(this.panelLogos.getPreferredSize());
        this.jibRaritan.setIcon(this.raritanLogo);
        this.panelLogos.add(this.jibRaritan);
        this.panelLogos.add(Box.createVerticalGlue());
        this.lblPowerBoard.setAlignmentX(1.0f);
        this.lblPowerBoard.setBackground(Color.white);
        this.lblPowerBoard.setText(PbResource.getString("PowerboardLabel.name") + PbResource.getString("VersionNo"));
        this.lblPowerBoard.setFont(this.font22);
        this.lblPowerBoard.setForeground(Color.black);
        this.panelLogos.add(this.lblPowerBoard);
        this.pbPanel.add(this.panelLogos);
        this.lblUnitId.setFont(this.font18);
        this.lblUnitId.setText("");
        this.pbPanel.add(this.lblUnitId);
        this.panelOutlets.setLayout(new SpringLayout());
        this.panelOutlets.setBackground(Color.white);
        this.panelOutlets.setMaximumSize(this.panelOutlets.getPreferredSize());
        if (!this.initialized) {
            this.lblOutlets = new JLabel[this.stateManager.getNumOutlets()];
            this.jibOutlets = new JButton[this.stateManager.getNumOutlets()];
            if (n == 8) {
                this.columns = 2;
            } else if (n == 12) {
                this.columns = 3;
            } else if (n == 18 || n == 20) {
                this.columns = 4;
            }
            this.rows = n / this.columns;
            if (n % this.columns > 0) {
                ++this.rows;
            }
            int n2 = 0;
            int n3 = 0;
            while (n3 < this.columns) {
                object = new JPanel(new SpringLayout());
                ((JComponent)object).setBackground(Color.white);
                ((JComponent)object).setMaximumSize(((JComponent)object).getPreferredSize());
                int n4 = 0;
                while (n4 < this.rows) {
                    if (n2 < n) {
                        this.jibOutlets[n2] = new JButton("button " + n4, this.grUp);
                        this.jibOutlets[n2].setHorizontalAlignment(2);
                        this.jibOutlets[n2].setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
                        this.jibOutlets[n2].setFocusPainted(false);
                        this.jibOutlets[n2].setBorderPainted(false);
                        this.jibOutlets[n2].setContentAreaFilled(false);
                        this.jibOutlets[n2].setCursor(Cursor.getPredefinedCursor(12));
                        this.jibOutlets[n2].addMouseListener(this);
                        ((Container)object).add(this.jibOutlets[n2]);
                    } else {
                        JButton jButton = new JButton("Empty");
                        jButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
                        jButton.setVisible(false);
                        ((Container)object).add(jButton);
                    }
                    ++n2;
                    ++n4;
                }
                SpringUtilities.makeCompactGrid((Container)object, ((Container)object).getComponentCount(), 1, 6, 6, 6, 6);
                this.panelOutlets.add((Component)object);
                ++n3;
            }
            this.pbPanel.add(this.panelOutlets);
            SpringUtilities.makeCompactGrid(this.panelOutlets, 1, this.columns, 6, 6, 6, 6);
        }
        this.panelSystemStatus.setLayout(new SpringLayout());
        this.panelSystemStatus.setMaximumSize(this.panelSystemStatus.getPreferredSize());
        this.panelSystemStatus.setBackground(Color.lightGray);
        this.label4.setText(PbResource.getString("AveragePowerLabel.name") + " ");
        this.label4.setLabelFor(this.lblAppPower);
        this.panelSystemStatus.add(this.label4);
        this.panelSystemStatus.add(this.lblAppPower);
        this.lblAppPower.setForeground(Color.blue);
        this.panelSystemStatus.add(Box.createVerticalGlue());
        this.label5.setText(PbResource.getString("ApparentPowerLabel.name") + " ");
        this.label5.setLabelFor(this.lblAvPower);
        this.panelSystemStatus.add(this.label5);
        this.panelSystemStatus.add(this.lblAvPower);
        this.lblAvPower.setForeground(Color.blue);
        this.label6.setText(PbResource.getString("TrueRMSCurrentLabel.name") + " ");
        this.label6.setLabelFor(this.lblRMSCurrent);
        this.panelSystemStatus.add(this.label6);
        this.panelSystemStatus.add(this.lblRMSCurrent);
        this.lblRMSCurrent.setForeground(Color.blue);
        this.panelSystemStatus.add(Box.createVerticalGlue());
        this.label7.setText(PbResource.getString("MaxDetectedLabel.name") + " ");
        this.label7.setLabelFor(this.lblMaxDetected);
        this.panelSystemStatus.add(this.label7);
        this.panelSystemStatus.add(this.lblMaxDetected);
        this.lblMaxDetected.setForeground(Color.blue);
        this.label8.setText(PbResource.getString("TrueRMSVoltageLabel.name") + " ");
        this.label8.setLabelFor(this.lblRMSVolt);
        this.panelSystemStatus.add(this.label8);
        this.panelSystemStatus.add(this.lblRMSVolt);
        this.lblRMSVolt.setForeground(Color.blue);
        this.panelSystemStatus.add(Box.createVerticalGlue());
        this.panelSystemStatus.add(Box.createVerticalGlue());
        this.panelSystemStatus.add(Box.createVerticalGlue());
        this.label9.setText(PbResource.getString("InternalTempLabel.name") + " ");
        this.label9.setLabelFor(this.lblIntTemp);
        this.panelSystemStatus.add(this.label9);
        this.panelSystemStatus.add(this.lblIntTemp);
        this.lblIntTemp.setForeground(Color.blue);
        this.panelSystemStatus.add(Box.createVerticalGlue());
        this.label10.setText(PbResource.getString("OutletCircuitBreakerLabel.name") + " ");
        this.label10.setLabelFor(this.lblCircBrk);
        this.panelSystemStatus.add(this.label10);
        this.panelSystemStatus.add(this.lblCircBrk);
        this.lblCircBrk.setForeground(Color.blue);
        this.panelSystemStatus.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        SpringUtilities.makeCompactGrid(this.panelSystemStatus, 4, 5, 6, 6, 6, 6);
        JPanel jPanel = new JPanel(new FlowLayout());
        jPanel.setBackground(Color.white);
        jPanel.add(Box.createVerticalGlue());
        jPanel.add((Component)this.panelSystemStatus, 1);
        jPanel.add(Box.createVerticalGlue());
        this.pbPanel.add(jPanel);
        this.btnConfig.setText(PbResource.getString("ConfigButton.name"));
        this.btnConfig.setMaximumSize(this.btnConfig.getPreferredSize());
        this.panelButtons.add(this.btnConfig);
        this.panelButtons.setBackground(Color.white);
        this.panelButtons.setMaximumSize(this.panelButtons.getPreferredSize());
        this.pbPanel.add(this.panelButtons);
        this.pbPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        SpringUtilities.makeCompactGrid(this.pbPanel, 5, 1, 6, 6, 6, 20);
        this.initialized = true;
        SymMouse symMouse = new SymMouse();
        object = new SymAction();
        this.btnConfig.addActionListener((ActionListener)object);
        this.addMouseListener(symMouse);
        this.btnClose.addActionListener((ActionListener)object);
    }

    public void updateUI(RelayStatus relayStatus, SystemStatus systemStatus) {
        boolean[] blArray = relayStatus.getRelayStates();
        String[] stringArray = relayStatus.getRelayNames();
        int n = 0;
        while (n < this.lblOutlets.length) {
            this.jibOutlets[n].setText(n + 1 + ". " + stringArray[n]);
            if (!blArray[n]) {
                this.jibOutlets[n].setIcon(this.redUp);
                this.jibOutlets[n].setSelectedIcon(this.redUp);
                this.jibOutlets[n].setPressedIcon(this.redDown);
                this.jibOutlets[n].setDisabledIcon(this.redDis);
            } else {
                this.jibOutlets[n].setIcon(this.grUp);
                this.jibOutlets[n].setSelectedIcon(this.grUp);
                this.jibOutlets[n].setPressedIcon(this.grDown);
                this.jibOutlets[n].setDisabledIcon(this.grDis);
            }
            ++n;
        }
        if (systemStatus != null) {
            this.lblUnitId.setText(systemStatus.getUnitId());
            this.lblAvPower.setText(systemStatus.getAveragePower() + " " + PbResource.getString("AveragePowerUnit.name"));
            this.lblRMSCurrent.setText(systemStatus.getTrueRMSCurrent() + " " + PbResource.getString("TrueRMSCurrentUnit.name"));
            this.lblRMSVolt.setText(systemStatus.getTrueRMSVoltage() + " " + PbResource.getString("TrueRMSVoltageUnit.name"));
            this.lblIntTemp.setText(systemStatus.getInternalTemp() + " " + PbResource.getString("InternalTempUnit.name"));
            this.lblAppPower.setText(systemStatus.getApparentPower() + " " + PbResource.getString("ApparentPowerUnit.name"));
            this.lblMaxDetected.setText(systemStatus.getMaxDetected() + " " + PbResource.getString("MaxDetectedUnit.name"));
            this.lblCircBrk.setText(systemStatus.getCircuitBreaker());
        }
        this.setDefaultCursor();
    }

    public void enableAll(boolean bl) {
        if (bl) {
            this.refreshed = true;
        }
    }

    public void updateStatus(String string) {
    }

    public void updateUsers(int n) {
    }

    public void getImages() {
        this.raritanLogo = PbResource.getIcon("RaritanLogo.image");
        this.grUp = PbResource.getIcon("GreenUp.image");
        this.grDown = PbResource.getIcon("GreenDown.image");
        this.grDis = this.grUp;
        this.redUp = PbResource.getIcon("RedUp.image");
        this.redDown = PbResource.getIcon("RedDown.image");
        this.redDis = this.redUp;
    }

    public Container getPowerboardUI() {
        return this.pbPanel;
    }

    public Frame getFrame() {
        Container container = this.pbPanel;
        while (container != null && !(container instanceof Frame)) {
            container = container.getParent();
        }
        return (Frame)container;
    }

    public void setPowerboardAppletInterface(PowerboardInterface powerboardInterface) {
        this.applet = powerboardInterface;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    void repaintPanel() {
        this.jibRaritan.repaint();
        int n = 0;
        while (n < this.jibOutlets.length) {
            this.jibOutlets[n].repaint();
            ++n;
        }
    }

    public void setWaitCursor() {
        this.pbPanel.setCursor(Cursor.getPredefinedCursor(3));
    }

    public void setDefaultCursor() {
        this.pbPanel.setCursor(Cursor.getDefaultCursor());
    }

    public static int adjustedFontSize(int n) {
        int n2 = n;
        JPanel jPanel = new JPanel();
        int n3 = jPanel.getFontMetrics(new Font("Dialog", 0, n2)).getHeight();
        while (n3 > n + 4) {
            n3 = jPanel.getFontMetrics(new Font("Helvetica", 0, n2 -= 2)).getHeight();
        }
        return n2;
    }

    void btnConfig_ActionPerformed(ActionEvent actionEvent) {
        this.setWaitCursor();
        this.stateManager.readSetupData();
    }

    void Powerboard_mouseClicked(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        if (this.stateManager.getRelayStatus() != null) {
            boolean[] blArray = this.stateManager.getRelayStatus().getRelayStates();
            int n = 0;
            while (n < blArray.length) {
                if (mouseEvent.getSource() == this.jibOutlets[n]) {
                    if (blArray[n]) {
                        this.stateManager.sendOff(n + 1);
                        break;
                    }
                    this.stateManager.sendOn(n + 1);
                    break;
                }
                ++n;
            }
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mouseClicked(MouseEvent mouseEvent) {
    }

    void btnClose_ActionPerformed(ActionEvent actionEvent) {
        this.setVisible(false);
        this.dispose();
    }

    void Powerboard_WindowClosing(WindowEvent windowEvent) {
        this.setVisible(false);
        this.dispose();
    }

    class SymWindow
    extends WindowAdapter {
        SymWindow() {
        }

        public void windowClosing(WindowEvent windowEvent) {
            Object object = windowEvent.getSource();
            if (object == Powerboard.this) {
                Powerboard.this.Powerboard_WindowClosing(windowEvent);
            }
        }
    }

    class SymAction
    implements ActionListener {
        SymAction() {
        }

        public void actionPerformed(ActionEvent actionEvent) {
            Object object = actionEvent.getSource();
            if (object == Powerboard.this.btnConfig) {
                Powerboard.this.btnConfig_ActionPerformed(actionEvent);
            } else if (object == Powerboard.this.btnClose) {
                Powerboard.this.btnClose_ActionPerformed(actionEvent);
            }
        }
    }

    class SymMouse
    extends MouseAdapter {
        SymMouse() {
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            Object object = mouseEvent.getSource();
            if (object == Powerboard.this) {
                Powerboard.this.Powerboard_mouseClicked(mouseEvent);
            }
        }

        public void mouseReleased(MouseEvent mouseEvent) {
        }
    }
}

