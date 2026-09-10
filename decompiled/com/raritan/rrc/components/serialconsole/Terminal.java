/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import com.raritan.rrc.components.serialconsole.ConfigDialog;
import com.raritan.rrc.components.serialconsole.ConfigManager;
import com.raritan.rrc.components.serialconsole.Emulator;
import com.raritan.rrc.components.serialconsole.FileLogger;
import com.raritan.rrc.components.serialconsole.MessageBox;
import com.raritan.rrc.components.serialconsole.StatusBar;
import com.raritan.rrc.components.serialconsole.VDU;
import com.raritan.rrc.components.serialconsole.VT100Emulator;
import com.raritan.rrc.components.serialconsole.VT100_8859_15Emulator;
import com.raritan.rrc.components.serialconsole.VT100_8859_1Emulator;
import com.raritan.rrc.util.CircBuffer;
import com.raritan.rrc.util.StringUtils;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import javaclientlib.clientlib.TRSerialStream;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class Terminal
extends JPanel
implements InternalFrameListener,
ActionListener,
AdjustmentListener,
ComponentListener,
KeyListener,
MouseListener,
FocusListener,
MouseMotionListener,
ClipboardOwner {
    public static final String CODESET_USASCII = "US ASCII (True VT100)";
    public static final String CODESET_ISO_8859_1 = "ISO 8859-1 (Latin-1)";
    public static final String CODESET_ISO_8859_15 = "ISO 8859-15 (Latin-9)";
    private static final int ROWS = 24;
    private static final int BUFFER_SIZE = 5002;
    private static final int PORT_NUMBER = 23;
    private static final String KEY_INSERT = "\u001b[4h";
    private static final String KEY_REMOVE = "\u001b[6h";
    private static final String KEY_HOME = "\u001b[H";
    private static final String KP_0 = "\u001bOp";
    private static final String KP_1 = "\u001bOq";
    private static final String KP_2 = "\u001bOr";
    private static final String KP_3 = "\u001bOs";
    private static final String KP_4 = "\u001bOt";
    private static final String KP_5 = "\u001bOu";
    private static final String KP_6 = "\u001bOv";
    private static final String KP_7 = "\u001bOw";
    private static final String KP_8 = "\u001bOx";
    private static final String KP_9 = "\u001bOy";
    private static final String KP_MINUS = "\u001bOm";
    private static final String KP_COMMA = "\u001bOl";
    private static final String KP_PERIOD = "\u001bOn";
    private static final String PF1 = "\u001bOP";
    private static final String PF2 = "\u001bOQ";
    private static final String PF3 = "\u001bOR";
    private static final String PF4 = "\u001bOS";
    private static final int OFF = 0;
    private static final int ON = 1;
    private static final long serialVersionUID = 2580990810031856951L;
    private static final int MAX_BUFFER_SIZE = 1024;
    private static final int EVENT_BYTE_ID = 32456;
    StatusBar statusBar;
    VDU vdu;
    FileLogger fileLogger;
    private CircBuffer historyBuffer;
    private KeyEventDispatcher keyEventDisp;
    private int autoRepeat = 1;
    private boolean keyReleased = true;
    private TRSerialStream serialStream;
    private JMenu menuConfigure;
    private JMenuItem menuitemEmulator;
    private JMenuItem menuitemSelect;
    private JMenuItem menuitemCopy;
    private JMenuItem menuitemPaste;
    private JMenuItem menuitemBreak;
    private JMenuItem menuitemHistory;
    private JMenuItem menuitemStartLogging;
    private JMenuItem menuitemStopLogging;
    private ArrayList menuList;
    private JScrollBar scrollbar;
    private ConfigDialog configDialog;
    private int columnSize;
    private int bufferSize = 5002;
    private int portNo = 23;
    private int cursorType = 1;
    private Emulator emulator;
    private boolean altKey = false;
    private boolean ctrlKey = false;
    private boolean shiftKey = false;
    private Font genFont;
    private boolean logging = false;
    private Clipboard systemClipboard;
    private String clipboardData;
    private boolean rightClicked = false;
    private boolean admin = false;
    private boolean diagnostic = false;
    private String codeSet = "US ASCII (True VT100)";
    private ConfigManager configManager;

    public Terminal(TRSerialStream tRSerialStream, boolean bl, boolean bl2) {
        this.enableEvents(32456L);
        this.serialStream = tRSerialStream;
        this.admin = bl;
        this.diagnostic = bl2;
        this.historyBuffer = new CircBuffer(1024);
        this.setLayout(new BorderLayout());
        this.addComponentListener(this);
        this.menuList = new ArrayList();
        this.menuConfigure = new JMenu("Emulator");
        this.menuConfigure.setOpaque(false);
        this.menuitemEmulator = new JMenuItem("Settings...");
        this.menuitemEmulator.addActionListener(this);
        this.menuConfigure.add(this.menuitemEmulator);
        this.menuList.add(this.menuitemEmulator);
        this.menuConfigure.addSeparator();
        this.menuitemBreak = new JMenuItem("Get Write Access");
        this.menuitemHistory = new JMenuItem("History");
        this.menuitemHistory.addActionListener(this);
        this.menuConfigure.add(this.menuitemHistory);
        this.menuList.add(this.menuitemHistory);
        this.menuConfigure.addSeparator();
        this.menuitemCopy = new JMenuItem("Copy");
        this.menuitemCopy.setEnabled(false);
        this.menuitemCopy.addActionListener(this);
        this.menuConfigure.add(this.menuitemCopy);
        this.menuList.add(this.menuitemCopy);
        this.menuitemPaste = new JMenuItem("Paste");
        this.menuitemPaste.addActionListener(this);
        this.menuConfigure.add(this.menuitemPaste);
        this.menuList.add(this.menuitemPaste);
        this.menuitemSelect = new JMenuItem("Select All Text");
        this.menuitemSelect.addActionListener(this);
        this.menuConfigure.add(this.menuitemSelect);
        this.menuList.add(this.menuitemSelect);
        this.menuConfigure.addSeparator();
        this.menuitemStartLogging = new JMenuItem("Start Logging...");
        this.menuitemStartLogging.addActionListener(this);
        this.menuConfigure.add(this.menuitemStartLogging);
        this.menuList.add(this.menuitemStartLogging);
        this.menuitemStopLogging = new JMenuItem("Stop Logging");
        this.menuitemStopLogging.addActionListener(this);
        this.menuitemStopLogging.setEnabled(false);
        this.menuConfigure.add(this.menuitemStopLogging);
        this.menuList.add(this.menuitemStopLogging);
        this.scrollbar = new JScrollBar(1, 0, 1, 0, this.bufferSize - 24 + 1);
        this.scrollbar.setUnitIncrement(1);
        this.scrollbar.setBlockIncrement(3);
        this.scrollbar.setMaximum(14);
        this.scrollbar.addAdjustmentListener(this);
        this.scrollbar.setEnabled(false);
        this.addKeyListener(this);
        this.statusBar = new StatusBar();
        this.vdu = new VDU(this);
        this.vdu.addKeyListener(this);
        this.add((Component)this.vdu, "Center");
        this.add((Component)this.scrollbar, "East");
        if (!bl && !bl2) {
            this.add((Component)this.statusBar, "South");
        }
        this.vdu.addMouseListener(this);
        this.vdu.addMouseMotionListener(this);
        this.vdu.addFocusListener(this);
        Font font = this.vdu.getFont();
        int n = font.getSize();
        this.genFont = new Font("Monospaced", 0, n);
        this.setFont(this.genFont);
        this.configManager = new ConfigManager();
        Hashtable hashtable = this.configManager.readConfig();
        if (hashtable != null && hashtable.get("CodeSet") != null) {
            this.setCodeSet((String)hashtable.get("CodeSet"));
        }
        this.fileLogger = new FileLogger();
        this.initEmulator();
    }

    void fireBytesArrived(byte[] byArray) {
        ByteEvent byteEvent = new ByteEvent(this, 32456, byArray);
        this.getToolkit().getSystemEventQueue().postEvent(byteEvent);
    }

    @Override
    protected void processEvent(AWTEvent aWTEvent) {
        if (aWTEvent instanceof ByteEvent) {
            if (this.emulator != null) {
                this.emulator.preProcessByte(((ByteEvent)aWTEvent).b);
            }
        } else {
            super.processEvent(aWTEvent);
        }
    }

    public static int adjustedFontSize(int n) {
        int n2 = n;
        JPanel jPanel = new JPanel();
        int n3 = jPanel.getFontMetrics(new Font("Monospaced", 0, n2)).getHeight();
        while (n3 > n + 4) {
            n3 = jPanel.getFontMetrics(new Font("Monospaced", 0, n2 -= 2)).getHeight();
        }
        return n2;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public boolean isDiagnostic() {
        return this.diagnostic;
    }

    boolean isAltKey() {
        return this.altKey;
    }

    boolean isCtrlKey() {
        return this.ctrlKey;
    }

    boolean isShiftKey() {
        return this.shiftKey;
    }

    void setThumbPosition(int n) {
        this.scrollbar.setValue(n);
        this.scrollbar.removeAdjustmentListener(this);
        if (n >= this.scrollbar.getMaximum()) {
            this.scrollbar.setMaximum(this.scrollbar.getMaximum() + 1);
        }
        this.scrollbar.addAdjustmentListener(this);
    }

    void enableScrollbar() {
        this.scrollbar.setEnabled(true);
    }

    public int getCursorType() {
        return this.cursorType;
    }

    public void setCursorType(int n) {
        this.cursorType = n;
        this.vdu.setCursorType(n);
    }

    int getColumnSize() {
        return this.columnSize;
    }

    void setBufferSize(int n) {
        this.bufferSize = n;
        this.vdu.adjustBuffer(n);
    }

    int getBufferSize() {
        return this.bufferSize;
    }

    void setColumnSize(int n) {
        this.columnSize = n;
        this.vdu.setRightMargin(n);
    }

    void setPortNo(int n) {
        this.portNo = n;
    }

    int getPortNo() {
        return this.portNo;
    }

    void setAutoRepeat(int n) {
        this.autoRepeat = n;
    }

    public String getCodeSet() {
        return this.codeSet;
    }

    public void setCodeSet(String string) {
        this.codeSet = string;
        this.statusBar.setStatus(string.substring(0, string.indexOf("(")).trim());
    }

    public Emulator getEmulator() {
        return this.emulator;
    }

    public VDU getVDU() {
        return this.vdu;
    }

    public void saveConfig() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("CodeSet", this.codeSet);
        this.configManager.writeConfig(hashtable);
    }

    public void initEmulator() {
        this.emulator = this.codeSet.equals(CODESET_ISO_8859_15) ? new VT100_8859_15Emulator(this) : (this.codeSet.equals(CODESET_ISO_8859_1) ? new VT100_8859_1Emulator(this) : new VT100Emulator(this));
        this.vdu.setVisible(true);
        this.statusBar.setStatus(this.codeSet.substring(0, this.codeSet.indexOf("(")).trim(), "", "");
    }

    void callWhenExit() {
        this.emulator = null;
        try {
            this.serialStream.stopSerialStream();
            this.serialStream = null;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    void resetTerminal() {
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand() == "Settings...") {
            this.doSettings();
        } else if (actionEvent.getActionCommand() == "Select All Text") {
            this.doSelectAll();
        } else if (actionEvent.getActionCommand() == "History") {
            this.doShowHistory();
        } else if (actionEvent.getActionCommand() == "Start Logging...") {
            this.doStartLogging();
        } else if (actionEvent.getActionCommand() == "Stop Logging") {
            this.doStopLogging();
        } else if (actionEvent.getActionCommand() == "Copy") {
            this.doCopy();
        } else if (actionEvent.getActionCommand() == "Paste") {
            this.doPaste();
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
        this.vdu.scrollRows(adjustmentEvent.getValue());
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        this.requestFocus();
        if (this.emulator != null && !keyEvent.isControlDown() && keyEvent.isActionKey()) {
            this.write(keyEvent.getKeyCode());
        }
        if (!(this.emulator == null || keyEvent.isActionKey() || keyEvent.getKeyCode() != 127 && keyEvent.getKeyCode() != 27)) {
            this.write(keyEvent.getKeyChar());
            this.keyReleased = false;
        }
        this.shiftKey = keyEvent.isShiftDown();
        keyEvent.consume();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        Transferable transferable;
        this.requestFocus();
        this.altKey = keyEvent.isAltDown();
        this.ctrlKey = keyEvent.isControlDown();
        this.shiftKey = keyEvent.isShiftDown();
        if (keyEvent.getKeyCode() == 80 && keyEvent.isAltDown()) {
            this.write("|");
        }
        if (keyEvent.isControlDown() && keyEvent.getKeyCode() == 119) {
            this.emulator.sendAnswerBack();
        }
        if (this.vdu.hasSelection() && keyEvent.isControlDown() && keyEvent.getKeyCode() == 67) {
            try {
                if (this.getSystemClipboard() != null && !this.clipboardData.equals("")) {
                    transferable = new StringSelection(this.clipboardData);
                    this.systemClipboard.setContents(transferable, this);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this.rightClicked && keyEvent.isControlDown() && keyEvent.getKeyCode() == 86 && this.getSystemClipboard() != null && (transferable = this.systemClipboard.getContents(this)) != null) {
            String string = null;
            try {
                string = (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
            catch (Exception exception) {
                string = null;
            }
            if (string != null) {
                this.write(string);
                if (this.vdu.hasSelection()) {
                    try {
                        this.vdu.deselectText();
                        this.menuitemCopy.setEnabled(false);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }
        this.keyReleased = true;
        if (this.rightClicked) {
            this.rightClicked = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        if (keyEvent.getKeyChar() == '\t') {
            keyEvent.consume();
            return;
        }
        if (this.emulator != null && !keyEvent.isActionKey()) {
            if (!(keyEvent.getKeyChar() != '\r' && keyEvent.getKeyChar() != '\n' || keyEvent.isControlDown())) {
                if (this.emulator.sendcrlf == 1) {
                    this.write("\r\n");
                } else {
                    this.write("\r");
                }
            } else if (this.emulator.keypadMode == 1 && (keyEvent.getKeyCode() >= 95 && keyEvent.getKeyCode() <= 105 || keyEvent.getKeyCode() == 109 || keyEvent.getKeyCode() == 110 || keyEvent.getKeyCode() == 188)) {
                this.write(keyEvent.getKeyCode());
            } else if (!keyEvent.isAltDown() && keyEvent.getKeyCode() != 17 && keyEvent.getKeyCode() != 18 && keyEvent.getKeyCode() != 16 && this.keyReleased) {
                if (!this.vdu.hasSelection() && !this.rightClicked) {
                    this.write(keyEvent.getKeyChar());
                }
                if (this.autoRepeat == 0) {
                    this.keyReleased = false;
                }
            }
        }
    }

    void setMyFont(String string) {
        Dimension dimension = this.vdu.getSize();
        this.setMyFont(string, dimension.width, dimension.height);
    }

    void setMyFont(String string, int n, int n2) {
        int n3;
        Font font = this.vdu.getFont();
        int n4 = n3 = font.getSize();
        int n5 = 80;
        int n6 = 24;
        int n7 = 10;
        int n8 = 15;
        int n9 = n - n7;
        int n10 = n2 - n8;
        if (n9 < n5 || n10 < n6) {
            return;
        }
        boolean bl = true;
        boolean bl2 = false;
        boolean bl3 = false;
        int n11 = 0;
        int n12 = 0;
        FontMetrics fontMetrics = null;
        while (bl) {
            font = new Font("Monospaced", 0, n3);
            this.vdu.setFont(font);
            fontMetrics = this.vdu.getFontMetrics(font);
            n11 = n5 * fontMetrics.charWidth('W');
            n12 = n6 * fontMetrics.getHeight();
            int n13 = n9 - n11;
            int n14 = n10 - n12;
            if (bl2 && bl3) {
                bl = false;
                continue;
            }
            if (n13 > 0 && n14 > 0) {
                if (bl2) {
                    bl = false;
                    continue;
                }
                bl3 = true;
                ++n3;
                continue;
            }
            if ((n13 < 0 || n14 < 0) && n3 > 2) {
                --n3;
                bl2 = true;
                continue;
            }
            bl = false;
        }
        this.vdu.getImagesForDouble();
    }

    void write(int n) {
        switch (n) {
            case 96: {
                this.write(KP_0);
                break;
            }
            case 97: {
                this.write(KP_1);
                break;
            }
            case 98: {
                this.write(KP_2);
                break;
            }
            case 99: {
                this.write(KP_3);
                break;
            }
            case 100: {
                this.write(KP_4);
                break;
            }
            case 101: {
                this.write(KP_5);
                break;
            }
            case 102: {
                this.write(KP_6);
                break;
            }
            case 103: {
                this.write(KP_7);
                break;
            }
            case 104: {
                this.write(KP_8);
                break;
            }
            case 105: {
                this.write(KP_9);
                break;
            }
            case 109: {
                this.write(KP_MINUS);
                break;
            }
            case 188: {
                this.write(KP_COMMA);
                break;
            }
            case 110: {
                this.write(KP_PERIOD);
                break;
            }
            case 38: {
                this.write(this.emulator.keyUp);
                break;
            }
            case 40: {
                this.write(this.emulator.keyDown);
                break;
            }
            case 39: {
                this.write(this.emulator.keyRight);
                break;
            }
            case 37: {
                this.write(this.emulator.keyLeft);
                break;
            }
            case 36: {
                this.write(KEY_HOME);
                break;
            }
            case 155: {
                this.write(KEY_INSERT);
                break;
            }
            case 112: {
                if (this.altKey) break;
                this.write(PF1);
                break;
            }
            case 113: {
                if (this.altKey) break;
                this.write(PF2);
                break;
            }
            case 114: {
                if (this.altKey) break;
                this.write(PF3);
                break;
            }
            case 115: {
                if (this.altKey) break;
                this.write(PF4);
                break;
            }
            case 118: {
                if (this.altKey) break;
                this.write("\n");
                break;
            }
            case 119: {
                if (this.altKey) break;
                try {
                    this.serialStream.sendSunBreak();
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case 123: {
                if (this.altKey) break;
                this.write(10);
                break;
            }
        }
    }

    void write(char c) {
        try {
            this.historyBuffer.writeBulk(new char[]{c}, 1);
            boolean bl = this.serialStream.serialOut(1, new byte[]{(byte)c});
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    void write(byte[] byArray) {
        try {
            this.historyBuffer.writeBulk(new String(byArray).toCharArray(), byArray.length);
            boolean bl = this.serialStream.serialOut(byArray.length, byArray);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    void write(String string) {
        byte[] byArray = string.getBytes();
        this.write(byArray);
    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {
    }

    public void windowActivated(WindowEvent windowEvent) {
    }

    public void windowClosed(WindowEvent windowEvent) {
    }

    public void windowClosing(WindowEvent windowEvent) {
    }

    public void windowDeactivated(WindowEvent windowEvent) {
    }

    public void windowDeiconified(WindowEvent windowEvent) {
    }

    public void windowIconified(WindowEvent windowEvent) {
    }

    public void windowOpened(WindowEvent windowEvent) {
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (this.vdu.hasSelection()) {
            try {
                this.vdu.deselectText();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        try {
            this.vdu.setSelectionStart(mouseEvent.getX(), mouseEvent.getY());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        try {
            this.vdu.setSelectionEnd(mouseEvent.getX(), mouseEvent.getY());
            this.setClipboard(this.vdu.getSelectedText());
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (mouseEvent.isPopupTrigger()) {
            this.rightClicked = true;
            this.setClipboard("");
        } else {
            this.rightClicked = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        try {
            this.vdu.updateSelection(mouseEvent.getX(), mouseEvent.getY());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
    }

    private void setClipboard(String string) {
        if (string.equals("")) {
            this.menuitemCopy.setEnabled(false);
            return;
        }
        this.menuitemCopy.setEnabled(true);
        this.clipboardData = string;
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        this.keyEventDisp = new KeyEventDispatcher(){
            int step = 1;

            @Override
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 9) {
                    ++this.step;
                    this.step %= 2;
                    if (this.step == 0) {
                        if (Terminal.this.shiftKey) {
                            Terminal.this.write(38);
                        } else {
                            Terminal.this.write(keyEvent.getKeyChar());
                        }
                    }
                    keyEvent.consume();
                    return true;
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this.keyEventDisp);
    }

    @Override
    public void focusLost(FocusEvent focusEvent) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this.keyEventDisp);
    }

    public void dispose() {
    }

    public void produceImages(int n) {
        int n2;
        Font font = this.vdu.getFont();
        Font font2 = new Font("Monospaced", 0, n);
        this.vdu.setFont(font2);
        FontMetrics fontMetrics = this.vdu.getFontMetrics(font2);
        int n3 = fontMetrics.getHeight();
        int n4 = fontMetrics.charWidth('W');
        int n5 = fontMetrics.getDescent();
        this.vdu.setBackground(Color.black);
        this.vdu.setForeground(Color.lightGray);
        Image image = this.vdu.createImage(n4 * 100, n3 * 8);
        Graphics graphics = image.getGraphics();
        for (n2 = 32; n2 < 128; ++n2) {
            graphics.drawString("" + (char)n2, (n2 - 32) * n4, n3 - n5);
        }
        for (n2 = 161; n2 < 256; ++n2) {
            graphics.drawString("" + (char)n2, (n2 - 160) * n4, n3 * 2 - n5);
        }
        for (n2 = 32; n2 < 128; ++n2) {
            graphics.drawString("" + (char)n2, (n2 - 32) * n4, n3 * 3 - n5);
        }
        for (n2 = 161; n2 < 256; ++n2) {
            graphics.drawString("" + (char)n2, (n2 - 160) * n4, n3 * 4 - n5);
        }
        for (n2 = 96; n2 <= 101; ++n2) {
            this.vdu.decSpl.drawGlyph(graphics, (char)n2, (n2 - 95) * n4, n3 * 4 - n5, n4, n3);
        }
        for (n2 = 104; n2 <= 124; ++n2) {
            this.vdu.decSpl.drawGlyph(graphics, (char)n2, (n2 - 95) * n4, n3 * 4 - n5, n4, n3);
        }
        for (n2 = 96; n2 <= 101; ++n2) {
            this.vdu.decSpl.drawGlyph(graphics, (char)n2, (n2 - 95) * n4, n3 * 5 - n5, n4, n3);
        }
        for (n2 = 104; n2 <= 124; ++n2) {
            this.vdu.decSpl.drawGlyph(graphics, (char)n2, (n2 - 95) * n4, n3 * 5 - n5, n4, n3);
        }
        if (n == 13) {
            this.vdu.setImage13(image);
        } else {
            this.vdu.setImage10(image);
        }
        this.vdu.setFont(font);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable transferable) {
    }

    private Clipboard getSystemClipboard() {
        if (this.systemClipboard == null) {
            this.systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        return this.systemClipboard;
    }

    public void doSettings() {
        this.configDialog = new ConfigDialog(this.statusBar, this);
        this.configDialog.setVisible(true);
    }

    public void doSelectAll() {
        if (this.vdu.hasSelection()) {
            try {
                this.vdu.deselectText();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        try {
            this.setClipboard(this.vdu.selectAllText());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void doStartLogging() {
        this.logging = this.fileLogger.startLogging();
        this.emulator.setLogging(this.logging);
        this.statusBar.setLoggingStatus(this.logging);
        if (this.logging) {
            this.menuitemStartLogging.setEnabled(false);
            this.menuitemStopLogging.setEnabled(true);
        }
        this.requestFocus();
    }

    public boolean doStartLogging(File file) {
        this.logging = this.fileLogger.startLogging(file);
        this.emulator.setLogging(this.logging);
        this.statusBar.setLoggingStatus(this.logging);
        if (this.logging) {
            this.menuitemStartLogging.setEnabled(false);
            this.menuitemStopLogging.setEnabled(true);
        }
        this.requestFocus();
        return this.logging;
    }

    public void doStopLogging() {
        boolean bl = this.fileLogger.stopLogging();
        if (bl) {
            this.logging = false;
            this.emulator.setLogging(this.logging);
            this.statusBar.setLoggingStatus(this.logging);
            this.menuitemStopLogging.setEnabled(false);
            this.menuitemStartLogging.setEnabled(true);
        } else {
            new MessageBox("Logging", "Could not stop logging.", false).showMessage();
        }
    }

    public void doCopy() {
        try {
            if (this.getSystemClipboard() != null && !this.clipboardData.equals("")) {
                StringSelection stringSelection = new StringSelection(this.clipboardData);
                this.systemClipboard.setContents(stringSelection, this);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void doPaste() {
        Transferable transferable;
        if (this.getSystemClipboard() != null && (transferable = this.systemClipboard.getContents(this)) != null) {
            String string = null;
            try {
                string = (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
            catch (Exception exception) {
                string = null;
            }
            if (string != null) {
                this.write(string);
                if (this.vdu.hasSelection()) {
                    try {
                        this.vdu.deselectText();
                        this.menuitemCopy.setEnabled(false);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent internalFrameEvent) {
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
        int n = JOptionPane.showConfirmDialog(this, "Are you sure that you want to exit this console window?", "Exit console", 0, -1);
        if (n != 0) {
            return;
        }
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent internalFrameEvent) {
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent internalFrameEvent) {
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        this.vdu.requestFocus();
    }

    public boolean isLogging() {
        return this.logging;
    }

    public CircBuffer getHistoryBuffer() {
        return this.historyBuffer;
    }

    public void doShowHistory() {
        byte[] byArray = new byte[16384];
        this.getHistoryBuffer().readHistory(byArray);
        String string = StringUtils.trim(new String(byArray));
        this.emulator.setWritingHistoryBufferOn(true);
        this.emulator.preProcessByte(byArray);
        this.emulator.setWritingHistoryBufferOn(false);
    }

    public void setSerialStream(TRSerialStream tRSerialStream) {
        this.serialStream = tRSerialStream;
    }

    void writeFromHistory(char c) {
        try {
            boolean bl = this.serialStream.serialOut(1, new byte[]{(byte)c});
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    void writeFromHistory(byte[] byArray) {
        try {
            boolean bl = this.serialStream.serialOut(byArray.length, byArray);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    void writeFromHistory(String string) {
        byte[] byArray = string.getBytes();
        this.writeFromHistory(byArray);
    }

    public void onFinalize() {
        this.historyBuffer = null;
        this.emulator = null;
        this.statusBar = null;
        this.vdu.onFinalize();
        this.vdu = null;
    }

    private static class ByteEvent
    extends AWTEvent {
        private static final long serialVersionUID = -1322966874491550736L;
        private byte[] b;

        ByteEvent(Object object, int n, byte[] byArray) {
            super(object, n);
            this.b = byArray;
        }
    }
}

