/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.ui.helpers;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.MenuSelectionManager;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import nn.pp.common.ResourceLoader;
import nn.pp.core.T;

public class FullScreenMenuHandler {
    private static final int SLIDE_STEP = 3;
    private final JFrame frame;
    private final JMenuBar menuBar;
    private final Component[] mouseOverComponents;
    private final String keyboardHotkey;
    private JLabel txtLabel;
    private JLabel imgLabel;
    private JToggleButton pinButton;
    private GraphicsConfiguration graphicsConfiguration;
    private final Timer slideInTimer = new Timer(100, null);
    private final Timer slideOutTimer = new Timer(100, null);
    private final Timer slideInWaitTimer = new Timer(2000, null);
    private final Timer slideOutWaitTimer = new Timer(1000, null);
    private final MMAWTEventListener mmAWTEventListener = new MMAWTEventListener();
    private final MenuTrigger menuTrigger = new MenuTrigger();
    private FloatingMenuBarStates currentMenuState = FloatingMenuBarStates.NOT_VISIBLE;
    private boolean menuBarBorderPainting;
    private JPanel menuBarPanel;
    private JPanel glassPanel;
    boolean isSMM = false;
    private boolean pinState = false;
    public static final String PIN_ICON = "Common_pin.gif";
    public static final String UNPIN_ICON = "Common_unpin.gif";
    ImageIcon pinIcon;
    ImageIcon unpinIcon;
    JPanel p;

    public FullScreenMenuHandler(JFrame jFrame, JMenuBar jMenuBar, Component[] componentArray, String string, boolean bl, GraphicsConfiguration graphicsConfiguration) {
        this.frame = jFrame;
        this.menuBar = jMenuBar;
        this.mouseOverComponents = componentArray;
        this.keyboardHotkey = string;
        this.pinState = bl;
        this.graphicsConfiguration = graphicsConfiguration;
        this.attachTimerListeners();
        this.pinIcon = ResourceLoader.loadImageIcon(PIN_ICON);
        this.unpinIcon = ResourceLoader.loadImageIcon(UNPIN_ICON);
    }

    public FullScreenMenuHandler(JFrame jFrame, JMenuBar jMenuBar, Component[] componentArray, String string, boolean bl) {
        this(jFrame, jMenuBar, componentArray, string, bl, null);
    }

    private void attachTimerListeners() {
        this.slideInTimer.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (FullScreenMenuHandler.this.currentMenuState != FloatingMenuBarStates.DISPOSED) {
                    int n = FullScreenMenuHandler.this.menuBarPanel.getY() + 3;
                    if (n >= 0) {
                        n = 0;
                        FullScreenMenuHandler.this.slideInTimer.stop();
                        FullScreenMenuHandler.this.slideInWaitTimer.start();
                        FullScreenMenuHandler.this.glassPanel.setVisible(false);
                    }
                    FullScreenMenuHandler.this.showHotKeyText(FullScreenMenuHandler.this.isSMM);
                    FullScreenMenuHandler.this.menuBarPanel.setLocation(FullScreenMenuHandler.this.menuBarPanel.getX(), n);
                }
            }
        });
        this.slideInWaitTimer.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (FullScreenMenuHandler.this.currentMenuState != FloatingMenuBarStates.DISPOSED) {
                    FullScreenMenuHandler.this.slideInWaitTimer.stop();
                    FullScreenMenuHandler.this.currentMenuState = FloatingMenuBarStates.SLIDED;
                }
            }
        });
        this.slideOutTimer.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (FullScreenMenuHandler.this.currentMenuState != FloatingMenuBarStates.DISPOSED) {
                    MenuSelectionManager.defaultManager().clearSelectedPath();
                    int n = FullScreenMenuHandler.this.menuBarPanel.getY() - 3;
                    if (n + FullScreenMenuHandler.this.menuBarPanel.getHeight() <= 0) {
                        n = -FullScreenMenuHandler.this.menuBarPanel.getHeight();
                        FullScreenMenuHandler.this.slideOutTimer.stop();
                        FullScreenMenuHandler.this.glassPanel.setVisible(false);
                        FullScreenMenuHandler.this.currentMenuState = FloatingMenuBarStates.NOT_VISIBLE;
                    }
                    FullScreenMenuHandler.this.showHotKeyText(FullScreenMenuHandler.this.isSMM);
                    FullScreenMenuHandler.this.menuBarPanel.setLocation(FullScreenMenuHandler.this.menuBarPanel.getX(), n);
                }
            }
        });
        this.slideOutWaitTimer.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (FullScreenMenuHandler.this.currentMenuState != FloatingMenuBarStates.DISPOSED) {
                    Toolkit.getDefaultToolkit().removeAWTEventListener(FullScreenMenuHandler.this.mmAWTEventListener);
                    FullScreenMenuHandler.this.slideOutWaitTimer.stop();
                    if (FullScreenMenuHandler.this.mmAWTEventListener.isWithinComponent()) {
                        if (!FullScreenMenuHandler.this.pinState) {
                            FullScreenMenuHandler.this.triggerSlideOut();
                        }
                    } else {
                        FullScreenMenuHandler.this.currentMenuState = FloatingMenuBarStates.SLIDED;
                    }
                }
            }
        });
    }

    private void triggerSlideOut() {
        this.currentMenuState = FloatingMenuBarStates.SLIDING_OUT;
        this.slideOutTimer.start();
        this.glassPanel.setVisible(true);
    }

    public void startSliding(boolean bl) {
        for (Component component : this.mouseOverComponents) {
            component.addMouseMotionListener(this.menuTrigger);
        }
        this.menuBarBorderPainting = this.menuBar.isBorderPainted();
        this.menuBar.setBorderPainted(false);
        Component[] componentArray = this.createMenuBarPanel(this.menuBar);
        this.menuBarPanel = componentArray[0];
        this.glassPanel = componentArray[1];
        this.showHotKeyText(this.isSMM);
        this.frame.getLayeredPane().add(this.menuBarPanel, JLayeredPane.PALETTE_LAYER, 0);
        Dimension dimension = this.graphicsConfiguration != null ? this.graphicsConfiguration.getBounds().getSize() : Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimension2 = this.menuBarPanel.getPreferredSize();
        this.menuBarPanel.setBounds((dimension.width - dimension2.width) / 2, -dimension2.height, dimension2.width, dimension2.height);
        if (bl) {
            assert (this.currentMenuState == FloatingMenuBarStates.NOT_VISIBLE);
            this.currentMenuState = FloatingMenuBarStates.SLIDING_IN;
            this.glassPanel.setVisible(true);
            this.slideInTimer.start();
        }
    }

    public void stopSliding() {
        this.currentMenuState = FloatingMenuBarStates.DISPOSED;
        this.menuBar.setBorderPainted(this.menuBarBorderPainting);
        Toolkit.getDefaultToolkit().removeAWTEventListener(this.mmAWTEventListener);
        for (Component component : this.mouseOverComponents) {
            component.removeMouseMotionListener(this.menuTrigger);
        }
        this.menuBar.getParent().remove(this.menuBar);
    }

    private JPanel[] createMenuBarPanel(JMenuBar jMenuBar) {
        this.p = new JPanel();
        this.p.setBorder(BorderFactory.createEtchedBorder());
        this.p.setLayout(new OverlayLayout(this.p));
        JPanel jPanel = new JPanel();
        GlassPanel glassPanel = new GlassPanel(jPanel);
        glassPanel.setVisible(false);
        this.p.add(glassPanel);
        jPanel.setAlignmentX(0.0f);
        jPanel.setAlignmentY(0.0f);
        this.pinButton = new JToggleButton(this.pinState ? this.pinIcon : this.unpinIcon);
        this.pinButton.setPreferredSize(new Dimension(this.pinIcon.getIconWidth(), this.pinIcon.getIconHeight()));
        this.pinButton.setOpaque(false);
        this.pinButton.addItemListener(new ItemListener(){

            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                FullScreenMenuHandler.this.pinState = !FullScreenMenuHandler.this.pinState;
                if (!FullScreenMenuHandler.this.pinState) {
                    FullScreenMenuHandler.this.triggerSlideOut();
                }
                FullScreenMenuHandler.this.togglePinState();
                if (FullScreenMenuHandler.this.mouseOverComponents[0] != null) {
                    FullScreenMenuHandler.this.mouseOverComponents[0].requestFocusInWindow();
                }
            }
        });
        this.imgLabel = new JLabel(ResourceLoader.loadImageIcon("Common_Raritan_Title.gif"));
        this.txtLabel = new JLabel(this.keyboardHotkey + T._(" : Disable Single Mouse"));
        jPanel.add(this.pinButton);
        jPanel.add(this.txtLabel);
        jPanel.add(this.imgLabel);
        jPanel.add(jMenuBar);
        this.p.add(jPanel);
        return new JPanel[]{this.p, glassPanel};
    }

    private void togglePinState() {
        this.pinButton.setIcon(this.pinState ? this.pinIcon : this.unpinIcon);
    }

    public void showHotKeyText(boolean bl) {
        if (this.txtLabel != null) {
            this.txtLabel.setVisible(bl);
        }
        Dimension dimension = this.graphicsConfiguration != null ? this.graphicsConfiguration.getBounds().getSize() : Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimension2 = this.menuBarPanel.getPreferredSize();
        this.menuBarPanel.setBounds((dimension.width - dimension2.width) / 2, this.menuBarPanel.getLocation().y, dimension2.width, dimension2.height);
        this.menuBarPanel.validate();
    }

    public void setSMM(boolean bl) {
        this.isSMM = bl;
    }

    private static class GlassPanel
    extends JPanel {
        private final JComponent comp;
        private final Insets inset = new Insets(0, 0, 0, 0);

        public GlassPanel(JComponent jComponent) {
            this.setAlignmentX(0.0f);
            this.setAlignmentY(0.0f);
            this.setOpaque(false);
            this.enableEvents(32L);
            this.setLayout(null);
            this.comp = jComponent;
        }

        @Override
        public Dimension getPreferredSize() {
            return this.comp.getPreferredSize();
        }

        @Override
        public Dimension getMinimumSize() {
            return this.comp.getMinimumSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return this.comp.getMaximumSize();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Color color = graphics.getColor();
            Color color2 = color.brighter();
            graphics.setColor(color2);
            Graphics2D graphics2D = (Graphics2D)graphics;
            Composite composite = graphics2D.getComposite();
            graphics2D.setComposite(AlphaComposite.getInstance(3, 0.25f));
            Insets insets = this.getInsets(this.inset);
            int n = insets.left;
            int n2 = insets.top;
            int n3 = this.getWidth() - insets.left - insets.right;
            int n4 = this.getHeight() - insets.top - insets.bottom;
            graphics2D.fillRect(n, n2, n3, n4);
            graphics2D.setComposite(composite);
            graphics.setColor(color);
        }
    }

    private class MenuTrigger
    implements MouseMotionListener {
        private MenuTrigger() {
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            if (FullScreenMenuHandler.this.currentMenuState != FloatingMenuBarStates.DISPOSED) {
                Point point = SwingUtilities.convertPoint((Component)mouseEvent.getSource(), mouseEvent.getPoint(), FullScreenMenuHandler.this.frame.getContentPane());
                if (FullScreenMenuHandler.this.currentMenuState == FloatingMenuBarStates.NOT_VISIBLE && point.y <= 3) {
                    FullScreenMenuHandler.this.currentMenuState = FloatingMenuBarStates.SLIDING_IN;
                    FullScreenMenuHandler.this.glassPanel.setVisible(true);
                    FullScreenMenuHandler.this.slideInTimer.start();
                } else if (FullScreenMenuHandler.this.currentMenuState == FloatingMenuBarStates.SLIDED && !FullScreenMenuHandler.this.pinState) {
                    FullScreenMenuHandler.this.currentMenuState = FloatingMenuBarStates.SLIDEOUT_WAIT;
                    Toolkit.getDefaultToolkit().addAWTEventListener(FullScreenMenuHandler.this.mmAWTEventListener, 32L);
                    FullScreenMenuHandler.this.mmAWTEventListener.reset();
                    FullScreenMenuHandler.this.slideOutWaitTimer.start();
                }
            }
        }
    }

    private class MMAWTEventListener
    implements AWTEventListener {
        private MouseEvent compEvent;
        private MouseEvent other;

        private MMAWTEventListener() {
        }

        @Override
        public void eventDispatched(AWTEvent aWTEvent) {
            if (aWTEvent.getID() == 503) {
                MouseEvent mouseEvent = (MouseEvent)aWTEvent;
                Object object = mouseEvent.getSource();
                for (Component component : FullScreenMenuHandler.this.mouseOverComponents) {
                    if (component != object) continue;
                    this.compEvent = mouseEvent;
                    return;
                }
                this.other = mouseEvent;
            }
        }

        public void reset() {
            this.compEvent = null;
            this.other = null;
        }

        public boolean isWithinComponent() {
            if (this.compEvent == null) {
                return false;
            }
            if (this.other == null) {
                return true;
            }
            return this.compEvent.getWhen() > this.other.getWhen();
        }
    }

    private static enum FloatingMenuBarStates {
        NOT_VISIBLE,
        SLIDING_IN,
        SLIDED,
        SLIDEOUT_WAIT,
        SLIDING_OUT,
        DISPOSED;

    }
}

