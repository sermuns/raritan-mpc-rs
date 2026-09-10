/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class DoubleClickHandler
extends MouseAdapter {
    private static int dblClickDelay;
    private final Timer timer = new Timer(dblClickDelay, new ActionListener(){

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            DoubleClickHandler.this.fireMouseEvent(DoubleClickHandler.this.mouseEvent);
            DoubleClickHandler.this.mouseEvent = null;
        }
    });
    private MouseEvent mouseEvent;
    private final List<MouseListener> listerners = new ArrayList<MouseListener>();

    public DoubleClickHandler() {
        this.timer.setRepeats(false);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 1) {
            if (mouseEvent.getClickCount() > 1) {
                if (this.timer.isRunning()) {
                    this.fireMouseEvent(mouseEvent);
                    this.timer.stop();
                    this.mouseEvent = null;
                }
            } else {
                this.mouseEvent = mouseEvent;
                this.timer.restart();
            }
        }
    }

    public void addMouseListener(MouseListener mouseListener) {
        if (!this.listerners.contains(mouseListener)) {
            this.listerners.add(mouseListener);
        }
    }

    public void removeMouseListener(MouseListener mouseListener) {
        this.listerners.remove(mouseListener);
    }

    private void fireMouseEvent(MouseEvent mouseEvent) {
        for (MouseListener mouseListener : this.listerners) {
            mouseListener.mouseClicked(mouseEvent);
        }
    }

    public static void main(String[] stringArray) {
        JFrame jFrame = new JFrame("DblClickHandler");
        jFrame.setDefaultCloseOperation(3);
        JPanel jPanel = new JPanel();
        DoubleClickHandler doubleClickHandler = new DoubleClickHandler();
        doubleClickHandler.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 1) {
                    System.out.println("Single Clicked");
                } else {
                    System.out.println("Double Clicked");
                }
            }
        });
        jPanel.addMouseListener(doubleClickHandler);
        jFrame.add(jPanel);
        jFrame.setSize(new Dimension(200, 200));
        jFrame.setVisible(true);
    }

    static {
        try {
            dblClickDelay = (Integer)Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
        }
        catch (Exception exception) {
            dblClickDelay = 500;
        }
    }
}

