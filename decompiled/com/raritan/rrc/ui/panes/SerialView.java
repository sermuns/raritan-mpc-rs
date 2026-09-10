/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.components.serialconsole.Emulator;
import com.raritan.rrc.components.serialconsole.Terminal;
import com.raritan.rrc.data.SerialPort;
import com.raritan.rrc.data.SerialStream;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.panes.DeviceViewAdapter;
import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import javaclientlib.clientlib.ISerialStream;
import javaclientlib.utils.RRCLogger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import nn.pp.common.ResourceLoader;

public class SerialView
extends DeviceViewAdapter
implements ISerialStream,
Runnable,
AdjustmentListener {
    private static final long serialVersionUID = 2337001065266455708L;
    private Terminal terminal;
    private Emulator emulator;
    private RRCScreenContext scrContext;
    private SerialPort serialPort;
    private JScrollPane jsp;
    private JScrollBar verticalScrollBar;
    private JScrollBar horizontalScrollBar;
    private JPanel terminalPanel;
    private SerialStream serialStream;
    JLabel southEastLabel;

    public SerialView(boolean bl, ScreenContext screenContext, boolean bl2) {
        super((RRCScreenContext)screenContext);
        this.scrContext = (RRCScreenContext)screenContext;
        this.isInternalFrame = true;
        this.isNewPanel = true;
        this.setFocusable(true);
        this.makeLayout();
    }

    public Terminal getSerialTerminal() {
        return this.terminal;
    }

    @Override
    public void setDefaultFocussedComponent() {
        boolean bl = this.terminal.getVDU().requestFocusInWindow();
    }

    @Override
    public void serialIn(int n, byte[] byArray) {
        while (this.emulator == null) {
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException interruptedException) {}
        }
        if (this.terminal != null) {
            this.emulator.bytesArrived(byArray, n);
        }
    }

    @Override
    public void makeLayout() {
        try {
            this.serialPort = (SerialPort)((ArrayList)this.scrContext.getSelectedDevicesObservable().getComponent()).get(0);
            this.serialPort.connect();
            this.serialPort.setView(this);
            RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.scrContext.getPanelMediator().getParent();
            this.serialStream = (SerialStream)this.serialPort.getStream();
            this.terminal = new Terminal(this.serialStream, this.serialPort.isAdmin(), this.serialPort.isDiagnosticPort());
            this.emulator = this.terminal.getEmulator();
            this.serialPort.setTerminal(this.terminal);
            this.addFocusListener(this);
            if (!this.serialPort.isAdmin()) {
                this.terminal.setPreferredSize(new Dimension(665, 445));
            } else {
                this.terminal.setPreferredSize(new Dimension(580, 435));
            }
            this.setLayout(new BorderLayout());
            this.addComponentListener(new SerialResizer());
            this.jsp = new JScrollPane(this.terminal);
            this.jsp.setHorizontalScrollBarPolicy(31);
            this.jsp.setVerticalScrollBarPolicy(21);
            this.terminalPanel = new JPanel(new FlowLayout(0));
            this.terminalPanel.setBackground(Color.DARK_GRAY);
            this.terminalPanel.add(this.jsp);
            this.horizontalScrollBar = new JScrollBar(0);
            this.verticalScrollBar = new JScrollBar(1);
            this.horizontalScrollBar.setVisible(false);
            this.verticalScrollBar.setVisible(false);
            this.southEastLabel = new JLabel(ResourceLoader.loadImageIcon(this.bundle.getString("Transparent.image")));
            this.southEastLabel.setBackground(Color.GRAY);
            this.southEastLabel.setPreferredSize(new Dimension(20, 20));
            this.southEastLabel.setVisible(false);
            JPanel jPanel = new JPanel(new BorderLayout());
            jPanel.add((Component)this.horizontalScrollBar, "Center");
            jPanel.add((Component)this.southEastLabel, "East");
            this.add((Component)this.terminalPanel, "Center");
            this.add((Component)jPanel, "South");
            this.add((Component)this.verticalScrollBar, "East");
            this.horizontalScrollBar.addAdjustmentListener(this);
            this.verticalScrollBar.addAdjustmentListener(this);
            SwingUtilities.invokeLater(this);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        this.serialStream = null;
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        this.grabFocus();
    }

    @Override
    public void run() {
        MPCUtil.notifyObservers(this.scrContext, this.serialPort);
        this.terminal.setSerialStream((SerialStream)this.serialPort.getStream());
        this.setDefaultFocussedComponent();
        if (this.jsp.getVisibleRect().getWidth() < (double)this.terminal.getWidth()) {
            this.horizontalScrollBar.setEnabled(true);
            this.horizontalScrollBar.setVisible(true);
        } else {
            this.horizontalScrollBar.setEnabled(false);
            this.horizontalScrollBar.setVisible(false);
        }
        if (this.jsp.getVisibleRect().getHeight() < (double)this.terminal.getHeight()) {
            this.verticalScrollBar.setEnabled(true);
            this.verticalScrollBar.setVisible(true);
        } else {
            this.verticalScrollBar.setEnabled(false);
            this.verticalScrollBar.setVisible(false);
        }
        if (this.verticalScrollBar.isVisible() && this.horizontalScrollBar.isVisible()) {
            this.southEastLabel.setVisible(true);
        } else {
            this.southEastLabel.setVisible(false);
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
        double d = this.horizontalScrollBar.getValue();
        double d2 = this.verticalScrollBar.getValue();
        d = d / 90.0 * ((double)this.jsp.getHorizontalScrollBar().getMaximum() - this.terminal.getVisibleRect().getWidth());
        d2 = d2 / 90.0 * ((double)this.jsp.getVerticalScrollBar().getMaximum() - this.terminal.getVisibleRect().getHeight());
        this.jsp.getViewport().setViewPosition(new Point((int)d, (int)d2));
    }

    @Override
    public boolean serialOut(int n, byte[] byArray) throws Exception {
        return false;
    }

    @Override
    public void setViewFocus() {
        try {
            this.getShellInternalFrame().setSelected(true);
            this.setDefaultFocussedComponent();
        }
        catch (PropertyVetoException propertyVetoException) {
            RRCLogger.logException(propertyVetoException);
        }
    }

    @Override
    public boolean hasFocus() {
        return this.getShellInternalFrame() != null && this.getShellInternalFrame().isSelected();
    }

    private class SerialResizer
    extends ComponentAdapter {
        private SerialResizer() {
        }

        @Override
        public void componentResized(ComponentEvent componentEvent) {
            if (componentEvent.getID() == 101) {
                if (SerialView.this.jsp.getVisibleRect().getWidth() < (double)SerialView.this.terminal.getWidth()) {
                    SerialView.this.horizontalScrollBar.setEnabled(true);
                    SerialView.this.horizontalScrollBar.setVisible(true);
                } else {
                    SerialView.this.horizontalScrollBar.setEnabled(false);
                    SerialView.this.horizontalScrollBar.setVisible(false);
                }
                if (SerialView.this.jsp.getVisibleRect().getHeight() < (double)SerialView.this.terminal.getHeight()) {
                    SerialView.this.verticalScrollBar.setEnabled(true);
                    SerialView.this.verticalScrollBar.setVisible(true);
                } else {
                    SerialView.this.verticalScrollBar.setEnabled(false);
                    SerialView.this.verticalScrollBar.setVisible(false);
                }
                if (SerialView.this.verticalScrollBar.isVisible() && SerialView.this.horizontalScrollBar.isVisible()) {
                    SerialView.this.southEastLabel.setVisible(true);
                } else {
                    SerialView.this.southEastLabel.setVisible(false);
                }
            }
        }

        @Override
        public void componentMoved(ComponentEvent componentEvent) {
            SerialView.this.terminal.repaint();
        }
    }
}

