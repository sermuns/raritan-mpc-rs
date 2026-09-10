/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.ui.pan;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import nn.pp.common.ui.pan.PanningBorder;
import nn.pp.common.ui.pan.PanningEvent;
import nn.pp.common.ui.pan.PanningEventListener;

public class PanningHolder
extends JPanel {
    private int panningWidth;
    private EventListenerList listeners = new EventListenerList();
    private PanningBorder border;
    private Timer panningTimer;

    public PanningHolder(JComponent jComponent, int n) {
        this.setLayout(new BorderLayout());
        this.add((Component)jComponent, "Center");
        this.panningWidth = n;
        this.border = new PanningBorder(n, 0);
        this.setBorder(this.border);
        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                int n = PanningHolder.this.border.getDirectionFromPoint(new Point(mouseEvent.getX(), mouseEvent.getY()));
                PanningHolder.this.setBorder(PanningHolder.this.border = new PanningBorder(PanningHolder.this.panningWidth, n));
                PanningHolder.this.firePanningEvent(new PanningEvent(PanningHolder.this, n));
                PanningHolder.this.setupPanningTimer(n);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                PanningHolder.this.setBorder(PanningHolder.this.border = new PanningBorder(PanningHolder.this.panningWidth, 0));
                PanningHolder.this.killPanningTimer();
            }
        });
    }

    public PanningHolder(JComponent jComponent) {
        this(jComponent, 10);
    }

    public void addPanningEventListener(PanningEventListener panningEventListener) {
        if (panningEventListener != null) {
            this.listenerList.add(PanningEventListener.class, panningEventListener);
        }
    }

    public void removePanningEventListener(PanningEventListener panningEventListener) {
        if (panningEventListener != null) {
            this.listenerList.remove(PanningEventListener.class, panningEventListener);
        }
    }

    private void firePanningEvent(PanningEvent panningEvent) {
        for (PanningEventListener panningEventListener : (PanningEventListener[])this.listenerList.getListeners(PanningEventListener.class)) {
            panningEventListener.panning(panningEvent);
        }
    }

    private void setupPanningTimer(final int n) {
        this.killPanningTimer();
        this.panningTimer = new Timer();
        this.panningTimer.schedule(new TimerTask(){

            @Override
            public void run() {
                Runnable runnable = new Runnable(){

                    @Override
                    public void run() {
                        PanningHolder.this.firePanningEvent(new PanningEvent(PanningHolder.this, n));
                    }
                };
                if (SwingUtilities.isEventDispatchThread()) {
                    runnable.run();
                } else {
                    SwingUtilities.invokeLater(runnable);
                }
            }
        }, 1000L, 25L);
    }

    private void killPanningTimer() {
        if (this.panningTimer != null) {
            this.panningTimer.cancel();
            this.panningTimer = null;
        }
    }

    public static void main(String[] stringArray) {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(3);
        jFrame.setLayout(new BorderLayout());
        jFrame.add((Component)new JLabel("North", 0), "North");
        jFrame.add((Component)new JLabel("South", 0), "South");
        jFrame.add((Component)new JLabel("East"), "East");
        jFrame.add((Component)new JLabel("West"), "West");
        JButton jButton = new JButton("Hello World");
        jButton.setPreferredSize(new Dimension(300, 200));
        PanningHolder panningHolder = new PanningHolder(jButton);
        jFrame.add((Component)panningHolder, "Center");
        panningHolder.addPanningEventListener(new PanningEventListener(){

            @Override
            public void panning(PanningEvent panningEvent) {
                System.out.println("Panning to: " + panningEvent.getDirection());
            }
        });
        jFrame.pack();
        jFrame.setVisible(true);
    }
}

