/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import com.raritan.rrc.components.serialconsole.DECSpecial;
import com.raritan.rrc.components.serialconsole.Terminal;
import com.raritan.rrc.components.serialconsole.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

public class VDU
extends JComponent {
    static final int HALF_BRIGHT = 1;
    private static final long serialVersionUID = -7997946847764882081L;
    private static final int PLAIN = 0;
    private static final int BOLD = 1;
    private static final int UNDERLINE = 4;
    private static final int BLINK = 5;
    private static final int REVERSE = 7;
    private static final int BOLD_UNDERLINE = 14;
    private static final int BOLD_BLINK = 15;
    private static final int BOLD_REVERSE = 17;
    private static final int UNDERLINE_BLINK = 45;
    private static final int BLINK_REVERSE = 57;
    private static final int REVERSE_UNDERLINE = 74;
    private static final int BOLD_UNDERLINE_BLINK = 145;
    private static final int BOLD_BLINK_REVERSE = 157;
    private static final int BOLD_REVERSE_UNDERLINE = 174;
    private static final int UNDERLINE_BLINK_REVERSE = 457;
    private static final int BOLD_UNDERLINE_BLINK_REVERSE = 1457;
    private static final int SINGLE_WIDTH = 0;
    private static final int DOUBLE_WIDTH = 1;
    private static final int DOUBLE_HEIGHT_TOP_HALF = 2;
    private static final int DOUBLE_HEIGHT_BOT_HALF = 3;
    private static final int REPLACE = 0;
    private static final int INSERT = 1;
    private static final int FULL_BRIGHT = 0;
    private static final int AUTOWRAP_OFF = 0;
    private static final int AUTOWRAP_ON = 1;
    private static final int ORIGIN_ABSOLUTE = 0;
    private static final int ORIGIN_RELATIVE = 1;
    private static final int CURSOR_VISIBLE = 1;
    private static final int CURSOR_INVISIBLE = 0;
    private static final int SCREEN_TOP = 1;
    private static final int SCREEN_BOT = 25;
    private static final int BUFFER_MAX = 5002;
    private static final Color DARK_BACKGROUND = Color.black;
    private static final Color LIGHT_BACKGROUND = Color.lightGray.darker();
    private static final int PAINT_EXECUTED = 0;
    private static final int NORMAL_PAINT = 1;
    private static final int REDRAW_PAINT = 2;
    private static final int BLINK_PAINT = 3;
    private static final int CURSOR_PAINT = 4;
    private static final char[] DECSPECIAL = new char[]{'@', '\u2666', '\u2592', '\u2409', '\u240c', '\u240d', '\u240a', '\u00ba', '\u00b1', '\u2424', '\u240b', '\u2518', '\u2510', '\u250c', '\u2514', '\u253c', '\u2594', '\u2580', '\u2500', '\u25ac', '_', '\u251c', '\u2524', '\u2534', '\u252c', '\u2502', '\u2264', '\u2265', '\u00b6', '\u2260', '\u00a3', '\u00b7'};
    DECSpecial decSpl;
    private Color normalFgcolor = Color.lightGray;
    private Color boldFgcolor = Color.white;
    private Color reverseFgcolor = Color.black;
    private Color boldReverseFgcolor = Color.black;
    private Color normalBgcolor = Color.black;
    private Color boldBgcolor = Color.black;
    private Color reverseBgcolor = Color.lightGray;
    private Color boldReverseBgcolor = Color.white;
    private Color normalNormalFgcolor = Color.lightGray;
    private Color normalBoldFgcolor = Color.white;
    private Color normalReverseFgcolor = Color.black;
    private Color normalBoldReverseFgcolor = Color.black;
    private Color normalNormalBgcolor = Color.black;
    private Color normalBoldBgcolor = Color.black;
    private Color normalReverseBgcolor = Color.lightGray;
    private Color normalBoldReverseBgcolor = Color.white;
    private Color blinkNormalFgcolor = Color.gray;
    private Color blinkBoldFgcolor = Color.lightGray;
    private Color blinkReverseFgcolor = Color.lightGray;
    private Color blinkBoldReverseFgcolor = Color.white;
    private Color blinkNormalBgcolor = Color.black;
    private Color blinkBoldBgcolor = Color.black;
    private Color blinkReverseBgcolor = Color.black;
    private Color blinkBoldReverseBgcolor = Color.black;
    private boolean update = false;
    private char[][] charArrays = null;
    private char[][] bufferArrays = null;
    private int[][] charAttributes = null;
    private int[][] bufCharAttributes = null;
    private int[] bufLineAttributes = null;
    private int[] lineAttributes = null;
    private boolean[] charUpdates;
    private int cursorRow = 1;
    private int cursorCol = 1;
    private int diffCursor = 0;
    private int topMargin = 1;
    private int botMargin = 25;
    private int rightMargin = 80;
    private int bufBotMargin = 5002;
    private int charHeight;
    private int charWidth;
    private int charDescent;
    private int maxDis;
    private char graphicsLeft = (char)66;
    private char graphicsRight = (char)48;
    private int fontStyle = 0;
    private Color bgColor = DARK_BACKGROUND;
    private Color fgColor = this.normalFgcolor;
    private int insertReplace = 0;
    private int cursorType = 1;
    private int cursorOnOff = 1;
    private int autoWrap = 1;
    private int originMode = 0;
    private Terminal terminal;
    private Image image10 = null;
    private Image image13 = null;
    private Image image = null;
    private Point startPoint;
    private Point endPoint;
    private boolean hasSelection = false;
    private Point lastEndPoint = null;
    private int[][] cutpasteBuf = new int[5002][this.rightMargin];
    private boolean allSelected = false;
    private int offSet = 0;
    private int base = 0;
    private Timer blinkTimer;
    private int paintFrom = 1;
    private int repaintFrom = 0;
    private boolean paintEnded = true;
    private boolean blinkStatus = false;
    private Color charColor = Color.lightGray;
    private int imageWidth = 0;
    private int imageHeight = 0;

    VDU(Terminal terminal) {
        int n;
        int n2;
        this.terminal = terminal;
        this.addMouseListener(new FocusRequester());
        this.decSpl = new DECSpecial();
        this.charArrays = new char[this.botMargin][this.rightMargin];
        this.bufferArrays = new char[5002][this.rightMargin];
        this.charAttributes = new int[this.botMargin][this.rightMargin];
        this.bufCharAttributes = new int[5002][this.rightMargin];
        this.lineAttributes = new int[this.botMargin];
        this.bufLineAttributes = new int[5002];
        this.charUpdates = new boolean[this.botMargin];
        this.bgColor = DARK_BACKGROUND;
        this.fgColor = this.normalFgcolor;
        if (this.terminal.isAdmin() && !this.terminal.isDiagnostic()) {
            this.normalNormalBgcolor = Color.blue;
            this.normalBoldBgcolor = Color.blue;
        }
        this.setBackground(this.bgColor);
        this.setFont(new Font("Monospaced", 0, 12));
        this.charDescent = this.getFontMetrics(this.getFont()).getDescent();
        this.charHeight = this.getFontMetrics(this.getFont()).getHeight();
        this.charWidth = this.getFontMetrics(this.getFont()).charWidth('W');
        for (n2 = 0; n2 < this.botMargin; ++n2) {
            for (n = 0; n < this.rightMargin; ++n) {
                this.charArrays[n2][n] = 32;
                this.charAttributes[n2][n] = 0;
                this.lineAttributes[n2] = 0;
                this.charUpdates[n2] = true;
            }
        }
        for (n2 = 0; n2 < 5002; ++n2) {
            for (n = 0; n < this.rightMargin; ++n) {
                this.bufferArrays[n2][n] = 32;
                this.bufCharAttributes[n2][n] = 0;
                this.bufLineAttributes[n2] = 0;
            }
        }
        this.blinkTimer = new Timer(this);
        this.blinkTimer.start();
    }

    public String selectAllText() throws Exception {
        int n;
        int n2 = 0;
        for (int i = 0; i < 5002; ++i) {
            if (new String(this.bufferArrays[i]).trim().equals("")) continue;
            n2 = i;
        }
        this.hasSelection = true;
        this.allSelected = true;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.bufferArrays[0]);
        for (n = 0; n < this.rightMargin; ++n) {
            this.cutpasteBuf[0][n] = this.bufCharAttributes[0][n];
            this.bufCharAttributes[0][n] = 7;
        }
        for (n = 1; n <= n2; ++n) {
            stringBuffer.append('\n');
            stringBuffer.append(this.bufferArrays[n]);
            for (int i = 0; i < this.rightMargin; ++i) {
                this.cutpasteBuf[n][i] = this.bufCharAttributes[n][i];
                this.bufCharAttributes[n][i] = 7;
            }
        }
        this.offSet = 0;
        this.transferArray();
        this.repaint(0, 0, this.getWidth(), this.getHeight());
        return stringBuffer.toString();
    }

    public void deselectText() throws Exception {
        int n;
        this.hasSelection = false;
        this.requestFocus();
        this.lastEndPoint = null;
        if (!this.allSelected) {
            this.selectBetween(this.startPoint, this.endPoint, false);
            return;
        }
        this.allSelected = false;
        for (n = 0; n < 5002; ++n) {
            for (int i = 0; i < this.rightMargin; ++i) {
                this.bufCharAttributes[n][i] = this.cutpasteBuf[n][i];
            }
        }
        for (n = 1; n <= 25; ++n) {
            this.charUpdates[n - 1] = true;
        }
        this.repaintFrom = 2;
        this.repaint(0, 0, this.getWidth(), this.getHeight());
    }

    public boolean hasSelection() {
        return this.hasSelection;
    }

    private int translateXCoord(int n) {
        return (int)Math.floor(n / this.charWidth);
    }

    private int translateYCoord(int n) {
        return (int)Math.floor(n / this.charHeight);
    }

    private Point validatePoint(int n, int n2, boolean bl) throws Exception {
        Point point = new Point(this.translateXCoord(n), this.translateYCoord(n2));
        boolean bl2 = false;
        if (point.y >= this.botMargin) {
            point.y = this.botMargin - 1;
            point.x = this.rightMargin;
            bl2 = true;
        }
        if (point.y < this.topMargin - 1) {
            point.y = this.topMargin - 1;
            point.x = 0;
            bl2 = true;
        }
        if (point.x < 0) {
            point.x = 0;
            bl2 = true;
        }
        if (point.x > this.rightMargin) {
            point.x = this.rightMargin;
            bl2 = true;
        }
        if (!bl2 && bl && (this.startPoint.y > point.y || this.startPoint.y == point.y && this.startPoint.x > point.x)) {
            ++point.x;
        }
        return point;
    }

    public void setSelectionStart(int n, int n2) throws Exception {
        this.startPoint = this.validatePoint(n, n2, false);
    }

    public void setSelectionEnd(int n, int n2) throws Exception {
        this.endPoint = this.validatePoint(n, n2, true);
    }

    private void swapPoints() {
        Point point = this.startPoint;
        this.startPoint = this.endPoint;
        this.endPoint = point;
    }

    public String getSelectedText() throws Exception {
        int n;
        if (this.startPoint.equals(this.endPoint)) {
            return "";
        }
        this.hasSelection = true;
        StringBuffer stringBuffer = new StringBuffer();
        if (this.after(this.startPoint, this.endPoint)) {
            this.swapPoints();
        }
        if (this.startPoint.y == this.endPoint.y) {
            char[] cArray = new char[this.endPoint.x - this.startPoint.x];
            for (int i = 0; i < cArray.length; ++i) {
                cArray[i] = this.charArrays[this.startPoint.y][this.startPoint.x + i];
            }
            return new String(cArray);
        }
        char[] cArray = new char[this.rightMargin - this.startPoint.x];
        for (n = 0; n < cArray.length; ++n) {
            cArray[n] = this.charArrays[this.startPoint.y][this.startPoint.x + n];
        }
        stringBuffer.append(new String(cArray));
        stringBuffer.append('\n');
        for (n = this.startPoint.y + 1; n < this.endPoint.y; ++n) {
            stringBuffer.append(new String(this.charArrays[n]));
            stringBuffer.append('\n');
        }
        cArray = new char[this.endPoint.x];
        for (int i = 0; i < cArray.length; ++i) {
            cArray[i] = this.charArrays[n][i];
        }
        stringBuffer.append(new String(cArray));
        return stringBuffer.toString();
    }

    private boolean after(Point point, Point point2) {
        return point.y > point2.y || point.y == point2.y && point.x > point2.x;
    }

    private void selectBetween(Point point, Point point2, boolean bl) {
        Point point3 = point;
        Point point4 = point2;
        try {
            int n = 0;
            if (bl) {
                this.base = this.offSet;
            } else {
                n = this.offSet - this.base;
            }
            if (this.after(point3, point4)) {
                Point point5 = new Point(point3);
                point3 = point4;
                point4 = point5;
            }
            if (point3.y == point4.y) {
                for (int i = point3.x; i < point4.x; ++i) {
                    if (bl) {
                        this.cutpasteBuf[point3.y][i] = this.charAttributes[point3.y][i];
                        this.charAttributes[point3.y][i] = 7;
                        this.charUpdates[point3.y] = true;
                        continue;
                    }
                    if (point3.y + n < 0 || point3.y + n >= this.botMargin) continue;
                    this.charAttributes[point3.y + n][i] = this.cutpasteBuf[point3.y][i];
                    this.charUpdates[point3.y + n] = true;
                }
                this.repaintFrom = 2;
                this.repaint(0, 0, this.getWidth(), this.getHeight());
            } else {
                int n2;
                int n3;
                for (n3 = point3.x; n3 < this.rightMargin; ++n3) {
                    if (bl) {
                        this.cutpasteBuf[point3.y][n3] = this.charAttributes[point3.y][n3];
                        this.charAttributes[point3.y][n3] = 7;
                        this.charUpdates[point3.y] = true;
                        continue;
                    }
                    if (point3.y + n < 0 || point3.y + n >= this.botMargin) continue;
                    this.charAttributes[point3.y + n][n3] = this.cutpasteBuf[point3.y][n3];
                    this.charUpdates[point3.y + n] = true;
                }
                for (n3 = point3.y + 1; n3 < point4.y; ++n3) {
                    for (n2 = 0; n2 < this.rightMargin; ++n2) {
                        if (bl) {
                            this.cutpasteBuf[n3][n2] = this.charAttributes[n3][n2];
                            this.charAttributes[n3][n2] = 7;
                            this.charUpdates[n3] = true;
                            continue;
                        }
                        if (n3 + n < 0 || n3 + n >= this.botMargin) continue;
                        this.charAttributes[n3 + n][n2] = this.cutpasteBuf[n3][n2];
                        this.charUpdates[n3 + n] = true;
                    }
                }
                for (n2 = 0; n2 < point4.x; ++n2) {
                    if (bl) {
                        this.cutpasteBuf[n3][n2] = this.charAttributes[n3][n2];
                        this.charAttributes[n3][n2] = 7;
                        this.charUpdates[n3] = true;
                        continue;
                    }
                    if (n3 < 0 || n3 + n >= this.botMargin) continue;
                    this.charAttributes[n3 + n][n2] = this.cutpasteBuf[n3][n2];
                    this.charUpdates[n3 + n] = true;
                }
                this.repaintFrom = 2;
                this.repaint(0, 0, this.getWidth(), this.getHeight());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void updateSelection(int n, int n2) throws Exception {
        this.setSelectionEnd(n, n2);
        if (this.lastEndPoint == null) {
            this.selectBetween(this.startPoint, this.endPoint, true);
            this.lastEndPoint = this.endPoint;
            return;
        }
        if (this.after(this.lastEndPoint, this.startPoint)) {
            if (!this.after(this.endPoint, this.startPoint)) {
                this.selectBetween(this.startPoint, this.lastEndPoint, false);
                this.selectBetween(this.startPoint, this.endPoint, true);
            } else if (this.after(this.endPoint, this.startPoint) && !this.after(this.endPoint, this.lastEndPoint)) {
                this.selectBetween(this.lastEndPoint, this.endPoint, false);
            } else if (this.after(this.endPoint, this.lastEndPoint)) {
                this.selectBetween(this.lastEndPoint, this.endPoint, true);
            }
        } else if (this.after(this.endPoint, this.startPoint)) {
            this.selectBetween(this.startPoint, this.lastEndPoint, false);
            this.selectBetween(this.startPoint, this.endPoint, true);
        } else if (!this.after(this.endPoint, this.startPoint) && this.after(this.endPoint, this.lastEndPoint)) {
            this.selectBetween(this.lastEndPoint, this.endPoint, false);
        } else if (!this.after(this.endPoint, this.lastEndPoint)) {
            this.selectBetween(this.lastEndPoint, this.endPoint, true);
        }
        this.lastEndPoint = this.endPoint;
    }

    void setGraphicsRight(char c) {
        this.graphicsRight = c;
    }

    char getGraphicsRight() {
        return this.graphicsRight;
    }

    void setGraphicsLeft(char c) {
        this.graphicsLeft = c;
    }

    char getGraphicsLeft() {
        return this.graphicsLeft;
    }

    int getCharHeight() {
        return this.charHeight;
    }

    int getCharWidth() {
        return this.charWidth;
    }

    int getCharDescent() {
        return this.charDescent;
    }

    synchronized void reSetSettings() {
        int n;
        int n2;
        this.cursorRow = 1;
        this.cursorCol = 1;
        this.diffCursor = 0;
        this.charArrays = new char[this.botMargin][this.rightMargin];
        this.bufferArrays = new char[5002][this.rightMargin];
        this.charAttributes = new int[this.botMargin][this.rightMargin];
        this.bufCharAttributes = new int[5002][this.rightMargin];
        this.lineAttributes = new int[this.botMargin];
        this.bufLineAttributes = new int[5002];
        this.charUpdates = new boolean[this.botMargin];
        for (n2 = 0; n2 < this.botMargin; ++n2) {
            for (n = 0; n < this.rightMargin; ++n) {
                this.charArrays[n2][n] = 32;
                this.charAttributes[n2][n] = 0;
                this.lineAttributes[n2] = 0;
                this.charUpdates[n2] = true;
            }
        }
        for (n2 = 0; n2 < 5002; ++n2) {
            for (n = 0; n < this.rightMargin; ++n) {
                this.bufferArrays[n2][n] = 32;
                this.bufCharAttributes[n2][n] = 0;
                this.bufLineAttributes[n2] = 0;
            }
        }
        this.setBackground(this.bgColor);
        this.terminal.setMyFont("VDU.reSetSettings", this.getSize().width, this.getSize().height);
        this.charDescent = this.getFontMetrics(this.getFont()).getDescent();
        this.charHeight = this.getFontMetrics(this.getFont()).getHeight();
        this.charWidth = this.getFontMetrics(this.getFont()).charWidth('W');
        this.update = true;
        this.reDraw();
    }

    void adjustBuffer(int n) {
        int n2 = n + 1;
        if (n2 < this.bufBotMargin) {
            for (int i = n2 - 2; i < this.bufBotMargin; ++i) {
                for (int j = 0; j < this.rightMargin; ++j) {
                    this.bufferArrays[i][j] = 32;
                    this.bufCharAttributes[i][j] = 0;
                    this.bufLineAttributes[i] = 0;
                }
            }
            this.bufBotMargin = n2;
            this.diffCursor = this.bufBotMargin - this.botMargin - 1;
            this.transferArray();
        } else {
            this.bufBotMargin = n2;
        }
        this.terminal.setThumbPosition(this.diffCursor);
    }

    void getImagesForDouble() {
        this.charWidth = this.getFontMetrics(this.getFont()).charWidth('W');
        if (this.charWidth < 8) {
            this.image = this.image10;
            this.imageWidth = 6;
            this.imageHeight = 12;
        } else {
            this.image = this.image13;
            this.imageWidth = 8;
            this.imageHeight = 16;
        }
    }

    void setFontStyle(int n) {
        this.fontStyle = n;
    }

    synchronized void setBackgroundColor(int n) {
        if (n == 1) {
            this.bgColor = LIGHT_BACKGROUND;
            this.normalNormalFgcolor = Color.black;
            this.normalBoldFgcolor = Color.black;
            this.normalReverseFgcolor = Color.lightGray;
            this.normalBoldReverseFgcolor = Color.white;
            this.normalNormalBgcolor = Color.lightGray.darker();
            this.normalBoldBgcolor = Color.white;
            this.normalReverseBgcolor = Color.black;
            this.normalBoldReverseBgcolor = Color.black;
            this.blinkNormalFgcolor = Color.lightGray.darker();
            this.blinkBoldFgcolor = Color.white;
            this.blinkReverseFgcolor = Color.lightGray.darker();
            this.blinkBoldReverseFgcolor = Color.lightGray;
            this.blinkNormalBgcolor = Color.black;
            this.blinkBoldBgcolor = Color.black;
            this.blinkReverseBgcolor = Color.black;
            this.blinkBoldReverseBgcolor = Color.black;
        } else {
            this.bgColor = DARK_BACKGROUND;
            this.normalNormalFgcolor = Color.lightGray;
            this.normalBoldFgcolor = Color.white;
            this.normalReverseFgcolor = Color.black;
            this.normalBoldReverseFgcolor = Color.black;
            this.normalNormalBgcolor = Color.black;
            this.normalBoldBgcolor = Color.black;
            this.normalReverseBgcolor = Color.lightGray;
            this.normalBoldReverseBgcolor = Color.white;
            this.blinkNormalFgcolor = Color.gray;
            this.blinkBoldFgcolor = Color.lightGray;
            this.blinkReverseFgcolor = Color.lightGray;
            this.blinkBoldReverseFgcolor = Color.white;
            this.blinkNormalBgcolor = Color.black;
            this.blinkBoldBgcolor = Color.black;
            this.blinkReverseBgcolor = Color.black;
            this.blinkBoldReverseBgcolor = Color.black;
        }
        this.normalFgcolor = this.normalNormalFgcolor;
        this.boldFgcolor = this.normalBoldFgcolor;
        this.reverseFgcolor = this.normalReverseFgcolor;
        this.boldReverseFgcolor = this.normalBoldReverseFgcolor;
        this.normalBgcolor = this.normalNormalBgcolor;
        this.boldBgcolor = this.normalBoldBgcolor;
        this.reverseBgcolor = this.normalReverseBgcolor;
        this.boldReverseBgcolor = this.normalBoldReverseBgcolor;
        this.setBackground(this.bgColor);
        this.reDraw();
    }

    void setLineAttribute(int n) {
        this.charUpdates[this.cursorRow - 1] = true;
        this.bufLineAttributes[this.cursorRow + this.diffCursor - 1] = n;
    }

    void setCursorType(int n) {
        this.cursorType = n;
    }

    void setCursorOnOff(int n) {
        this.cursorOnOff = n;
    }

    int getFontStyle() {
        return this.fontStyle;
    }

    void setCursorPosition(int n, int n2) {
        int n3 = n;
        int n4 = n2;
        if (this.originMode == 0) {
            if (n3 < this.topMargin) {
                n3 = 1;
            }
            if (n3 > this.botMargin) {
                n3 = 24;
            }
        }
        if (this.originMode == 1) {
            n3 = n3 + this.topMargin - 1;
        }
        if (n4 > this.rightMargin) {
            n4 = this.rightMargin;
        }
        this.charUpdates[this.cursorRow - 1] = true;
        this.cursorRow = n3;
        this.cursorCol = n4;
        this.charUpdates[this.cursorRow - 1] = true;
    }

    int getCursorRow() {
        return this.cursorRow;
    }

    int getCursorCol() {
        return this.cursorCol;
    }

    synchronized void setScrollRegion(int n, int n2) {
        this.topMargin = n;
        this.botMargin = n2;
        this.cursorRow = this.topMargin;
    }

    synchronized void setOriginMode(int n) {
        this.originMode = n;
    }

    void setInsertReplace(int n) {
        this.insertReplace = n;
    }

    int getTopMargin() {
        return this.topMargin;
    }

    int getBottomMargin() {
        return this.botMargin;
    }

    int getRightMargin() {
        return this.rightMargin;
    }

    synchronized void setRightMargin(int n) {
        this.rightMargin = n;
    }

    void setBufferSize(int n) {
        this.bufBotMargin = n;
    }

    void setAutoWrap(int n) {
        this.autoWrap = n;
    }

    void putChar(String string) {
        for (int i = 0; i < string.length(); ++i) {
            this.putChar(string.charAt(i));
        }
        this.update = true;
    }

    void putChar(char c) {
        char c2 = c;
        switch (c2) {
            case '\n': {
                this.charUpdates[this.cursorRow - 1] = true;
                ++this.cursorRow;
                this.update = true;
                if (this.cursorRow > this.botMargin) {
                    this.cursorRow = this.botMargin;
                    if (this.topMargin > 1) {
                        int n;
                        char[] cArray = this.bufferArrays[this.topMargin - 1 + this.diffCursor];
                        int[] nArray = this.bufCharAttributes[this.topMargin - 1 + this.diffCursor];
                        for (n = this.topMargin - 1 + this.diffCursor; n < this.diffCursor + this.botMargin - 1; ++n) {
                            this.bufferArrays[n] = this.bufferArrays[n + 1];
                            this.bufCharAttributes[n] = this.bufCharAttributes[n + 1];
                        }
                        this.bufferArrays[this.diffCursor + this.botMargin - 1] = cArray;
                        this.bufCharAttributes[this.diffCursor + this.botMargin - 1] = nArray;
                        for (n = 0; n < this.rightMargin; ++n) {
                            this.bufferArrays[this.diffCursor + this.botMargin - 1][n] = 32;
                            this.bufCharAttributes[this.diffCursor + this.botMargin - 1][n] = 0;
                        }
                    } else {
                        this.update = true;
                        ++this.diffCursor;
                        if (this.diffCursor > this.bufBotMargin - this.botMargin - 1) {
                            int n;
                            --this.diffCursor;
                            char[] cArray = this.bufferArrays[0];
                            int[] nArray = this.bufCharAttributes[0];
                            for (n = 0; n < this.bufBotMargin - 1; ++n) {
                                this.bufferArrays[n] = this.bufferArrays[n + 1];
                                this.bufCharAttributes[n] = this.bufCharAttributes[n + 1];
                            }
                            this.bufferArrays[this.bufBotMargin - 1] = cArray;
                            this.bufCharAttributes[this.bufBotMargin - 1] = nArray;
                            for (n = 0; n < this.rightMargin; ++n) {
                                this.bufferArrays[this.bufBotMargin - 1][n] = 32;
                                this.bufCharAttributes[this.bufBotMargin - 1][n] = 0;
                            }
                        }
                    }
                }
                if (this.diffCursor <= 0 || this.diffCursor > this.bufBotMargin - this.botMargin - 1) break;
                this.terminal.enableScrollbar();
                this.terminal.setThumbPosition(this.diffCursor);
                break;
            }
            case '\r': {
                this.cursorCol = 1;
                break;
            }
            case '\b': {
                --this.cursorCol;
                if (this.cursorCol >= 1) break;
                this.cursorCol = 1;
                break;
            }
            default: {
                if (c2 == '\u0000') break;
                if (c2 == '\t') {
                    this.cursorCol = (this.cursorCol - 1 & 0xFFF8) + 8 + 1;
                    break;
                }
                if (c2 == '\u0007') {
                    Toolkit.getDefaultToolkit().beep();
                    break;
                }
                if (this.autoWrap == 0) {
                    if (this.bufLineAttributes[this.cursorRow + this.diffCursor - 1] == 0 && this.cursorCol > this.rightMargin) {
                        this.cursorCol = this.rightMargin;
                    }
                    if (this.bufLineAttributes[this.cursorRow + this.diffCursor - 1] != 0 && this.cursorCol > this.rightMargin / 2) {
                        this.cursorCol = this.rightMargin / 2;
                    }
                } else if (this.bufLineAttributes[this.cursorRow + this.diffCursor - 1] == 0 && this.cursorCol > this.rightMargin || this.bufLineAttributes[this.cursorRow + this.diffCursor - 1] != 0 && this.cursorCol > this.rightMargin / 2) {
                    this.charUpdates[this.cursorRow - 1] = true;
                    ++this.cursorRow;
                    this.cursorCol = 1;
                    if (this.cursorRow > this.botMargin) {
                        this.cursorRow = this.botMargin;
                        this.update = true;
                        ++this.diffCursor;
                        if (this.diffCursor > this.bufBotMargin - this.botMargin - 1) {
                            int n;
                            this.diffCursor = this.bufBotMargin - this.botMargin - 1;
                            char[] cArray = this.bufferArrays[0];
                            int[] nArray = this.bufCharAttributes[0];
                            for (n = 0; n < this.bufBotMargin - 1; ++n) {
                                this.bufferArrays[n] = this.bufferArrays[n + 1];
                                this.bufCharAttributes[n] = this.bufCharAttributes[n + 1];
                            }
                            this.bufferArrays[this.bufBotMargin - 1] = cArray;
                            this.bufCharAttributes[this.bufBotMargin - 1] = nArray;
                            for (n = 1; n < this.rightMargin; ++n) {
                                this.bufferArrays[this.bufBotMargin - 1][n] = 32;
                                this.bufCharAttributes[this.bufBotMargin - 1][n] = 0;
                            }
                        }
                    }
                    if (this.diffCursor > 0 && this.diffCursor <= this.bufBotMargin - this.botMargin - 1) {
                        this.terminal.enableScrollbar();
                        this.terminal.setThumbPosition(this.diffCursor);
                    }
                }
                if (this.graphicsLeft == '0') {
                    if (c2 >= '_' && c2 <= '~') {
                        c2 = DECSPECIAL[(short)c2 - 95];
                    }
                } else if (this.graphicsLeft == 'A' && c2 == '#') {
                    c2 = '\u00a3';
                }
                if (this.originMode == 1) {
                    if (this.cursorRow < this.topMargin) {
                        this.cursorRow = this.topMargin;
                    }
                    if (this.cursorRow > this.botMargin) {
                        this.cursorRow = this.botMargin;
                    }
                }
                if (this.insertReplace == 0) {
                    this.putChar(this.cursorRow, this.cursorCol, c2);
                } else {
                    this.insertChar(this.cursorRow, this.cursorCol, c2);
                }
                ++this.cursorCol;
                if (this.autoWrap != 0) break;
                if (this.bufLineAttributes[this.cursorRow + this.diffCursor - 1] == 0) {
                    if (this.cursorCol <= this.rightMargin) break;
                    this.cursorCol = this.rightMargin;
                    break;
                }
                if (this.cursorCol <= this.rightMargin / 2) break;
                this.cursorCol = this.rightMargin / 2;
            }
        }
    }

    synchronized void putChar(int n, int n2, char c) {
        int n3 = this.checkBounds(n, 1, this.bufBotMargin);
        int n4 = this.checkBounds(n2, 1, this.rightMargin);
        this.bufferArrays[(n3 += this.diffCursor) - 1][n4 - 1] = c;
        this.bufCharAttributes[n3 - 1][n4 - 1] = this.fontStyle;
    }

    synchronized void insertChar(int n, int n2, char c) {
        int n3 = this.checkBounds(n, 1, this.bufBotMargin);
        int n4 = this.checkBounds(n2, 1, this.rightMargin);
        n3 += this.diffCursor;
        for (int i = this.rightMargin; i >= n4 + 1; --i) {
            this.bufferArrays[n3 - 1][i - 1] = this.bufferArrays[n3 - 1][i - 2];
            this.bufCharAttributes[n3 - 1][i - 1] = this.bufCharAttributes[n3 - 1][i - 2];
        }
        this.bufferArrays[n3 - 1][n4 - 1] = c;
        this.bufCharAttributes[n3 - 1][n4 - 1] = this.fontStyle;
    }

    synchronized void transferArray() {
        int n;
        for (n = 1; n <= 25; ++n) {
            this.charArrays[n - 1] = this.bufferArrays[n + this.diffCursor - 1];
            this.charAttributes[n - 1] = this.bufCharAttributes[n + this.diffCursor - 1];
            this.lineAttributes[n - 1] = this.bufLineAttributes[n + this.diffCursor - 1];
        }
        for (n = 1; n <= 25; ++n) {
            this.charUpdates[n - 1] = true;
        }
        if (this.diffCursor > 0 && this.diffCursor <= this.bufBotMargin - this.botMargin) {
            this.terminal.enableScrollbar();
            this.terminal.setThumbPosition(this.diffCursor);
        }
        this.update = false;
    }

    synchronized void transferArray(int n) {
        if (this.update) {
            this.transferArray();
        } else {
            this.charArrays[n - 1] = this.bufferArrays[this.diffCursor + n - 1];
            this.charAttributes[n - 1] = this.bufCharAttributes[this.diffCursor + n - 1];
            this.lineAttributes[n - 1] = this.bufLineAttributes[this.diffCursor + n - 1];
            this.charUpdates[n - 1] = true;
        }
    }

    public void reDraw() {
        int n = this.cursorRow;
        this.transferArray(n);
        if (this.repaintFrom != 1) {
            this.repaintFrom = 2;
            this.repaint(0, 0, this.getWidth(), this.getHeight());
        }
    }

    void reDrawBlink() {
        if (this.repaintFrom != 1 && this.repaintFrom != 2) {
            this.repaintFrom = 3;
            this.blinkStatus = !this.blinkStatus;
            this.blinkTimer.setBlinkChars(false);
            this.repaint(0, 0, this.getWidth(), this.getHeight());
        }
    }

    synchronized void reDrawCursor() {
        this.blinkStatus = !this.blinkStatus;
        this.repaintFrom = 4;
        this.repaint(0, 0, this.getWidth(), this.getHeight());
        this.cursorOnOff = this.cursorOnOff == 1 ? 0 : 1;
    }

    @Override
    public synchronized void paint(Graphics graphics) {
        graphics.setColor(this.bgColor);
        graphics.fillRect(0, 0, this.getSize().width, this.getSize().height);
        try {
            if (this.paintFrom != 4) {
                this.charDescent = this.getFontMetrics(this.getFont()).getDescent();
                this.charHeight = this.getFontMetrics(this.getFont()).getHeight();
                this.charWidth = this.getFontMetrics(this.getFont()).charWidth('W');
                block8: for (int i = 1; i <= 25; ++i) {
                    if (this.paintFrom == 2) {
                        if (!this.charUpdates[i - 1]) continue;
                        graphics.clearRect(0, (i - 1) * this.charHeight, (this.rightMargin + 40) * this.charWidth, this.charHeight);
                        this.charUpdates[i - 1] = false;
                    }
                    switch (this.lineAttributes[i - 1]) {
                        case 0: {
                            this.paintSingleWidthLine(graphics, i);
                            continue block8;
                        }
                        case 1: {
                            this.paintDoubleWidthLine(graphics, i);
                            continue block8;
                        }
                        case 2: {
                            this.paintDoubleHeightLine(graphics, i, true);
                            continue block8;
                        }
                        case 3: {
                            this.paintDoubleHeightLine(graphics, i, false);
                            continue block8;
                        }
                    }
                }
            }
            if (this.cursorOnOff == 1) {
                if (this.lineAttributes[this.cursorRow - 1] == 0) {
                    graphics.setColor(Color.white);
                    graphics.setXORMode(this.bgColor);
                    if (this.cursorType == 0) {
                        graphics.fillRect((this.cursorCol - 1) * this.charWidth, (this.cursorRow - 1) * this.charHeight, this.charWidth, this.charHeight);
                    } else {
                        graphics.fillRect((this.cursorCol - 1) * this.charWidth, this.cursorRow * this.charHeight - 1, this.charWidth, 1);
                    }
                } else {
                    graphics.setColor(Color.white);
                    graphics.setXORMode(this.bgColor);
                    if (this.cursorType == 0) {
                        graphics.fillRect((this.cursorCol - 1) * this.charWidth * 2, (this.cursorRow - 1) * this.charHeight, this.charWidth * 2, this.charHeight);
                    } else {
                        graphics.fillRect((this.cursorCol - 1) * this.charWidth * 2, this.cursorRow * this.charHeight - 1, this.charWidth * 2, 1);
                    }
                }
            }
            this.charUpdates[this.cursorRow - 1] = true;
            this.paintFrom = 1;
            this.paintEnded = true;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public synchronized void update(Graphics graphics) {
        this.paintFrom = this.repaintFrom;
        this.repaintFrom = 0;
        this.paintEnded = false;
        this.paint(graphics);
        while (!this.paintEnded) {
            if (this.repaintFrom == 0) continue;
            while (this.paintEnded) {
                this.repaint(0, 0, this.getWidth(), this.getHeight());
            }
        }
    }

    private synchronized void paintSingleWidthLine(Graphics graphics, int n) throws Exception {
        int n2 = this.charAttributes[n - 1][0];
        int n3 = 1;
        while (n3 <= this.rightMargin) {
            int n4;
            if (this.decSpl.isGlyph(this.charArrays[n - 1][n3 - 1])) {
                n2 = this.charAttributes[n - 1][n3 - 1];
                n4 = n3;
                while (n3 <= this.rightMargin && n2 == this.charAttributes[n - 1][n3 - 1] && this.decSpl.isGlyph(this.charArrays[n - 1][n3 - 1])) {
                    ++n3;
                }
                if (n2 >= 30000) {
                    n2 -= 30000;
                    this.blinkTimer.setBlinkChars(true);
                    if (this.blinkStatus) {
                        this.normalFgcolor = this.blinkNormalFgcolor;
                        this.boldFgcolor = this.blinkBoldFgcolor;
                        this.reverseFgcolor = this.blinkReverseFgcolor;
                        this.boldReverseFgcolor = this.blinkBoldReverseFgcolor;
                        this.normalBgcolor = this.blinkNormalBgcolor;
                        this.boldBgcolor = this.blinkBoldBgcolor;
                        this.reverseBgcolor = this.blinkReverseBgcolor;
                        this.boldReverseBgcolor = this.blinkBoldReverseBgcolor;
                    }
                } else if (this.paintFrom == 3) continue;
                switch (n2) {
                    case 1: {
                        if (this.boldBgcolor != this.bgColor) {
                            graphics.setColor(this.boldBgcolor);
                            graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                        }
                        graphics.setColor(this.boldFgcolor);
                        break;
                    }
                    case 7: {
                        graphics.setColor(this.reverseBgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                        graphics.setColor(this.reverseFgcolor);
                        break;
                    }
                    case 4: {
                        graphics.setColor(this.normalFgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, n * this.charHeight - 1, (n3 - n4) * this.charWidth, 1);
                        graphics.setColor(this.normalFgcolor);
                        break;
                    }
                    case 17: {
                        graphics.setColor(this.boldReverseBgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                        graphics.setColor(this.boldReverseFgcolor);
                        break;
                    }
                    case 14: {
                        if (this.normalBgcolor != this.bgColor) {
                            graphics.setColor(this.normalBgcolor);
                            graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                        }
                        graphics.setColor(this.boldFgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, n * this.charHeight - 1, (n3 - n4) * this.charWidth, 1);
                        graphics.setColor(this.boldFgcolor);
                        break;
                    }
                    case 74: {
                        graphics.setColor(this.reverseBgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                        graphics.setColor(this.reverseFgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, n * this.charHeight - 1, (n3 - n4) * this.charWidth, 1);
                        graphics.setColor(this.reverseFgcolor);
                        break;
                    }
                    case 174: {
                        graphics.setColor(this.boldReverseBgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                        graphics.setColor(this.boldReverseFgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, n * this.charHeight - 1, (n3 - n4) * this.charWidth, 1);
                        graphics.setColor(this.boldReverseFgcolor);
                        break;
                    }
                    default: {
                        if (this.normalBgcolor != this.bgColor) {
                            graphics.setColor(this.normalBgcolor);
                            graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                        }
                        graphics.setColor(this.normalFgcolor);
                    }
                }
                for (int i = n4; i < n3; ++i) {
                    this.decSpl.drawGlyph(graphics, this.charArrays[n - 1][i - 1], (i - 1) * this.charWidth, (n - 1) * this.charHeight, this.charWidth, this.charHeight);
                }
                if (!this.blinkStatus) continue;
                this.normalFgcolor = this.normalNormalFgcolor;
                this.boldFgcolor = this.normalBoldFgcolor;
                this.reverseFgcolor = this.normalReverseFgcolor;
                this.boldReverseFgcolor = this.normalBoldReverseFgcolor;
                this.normalBgcolor = this.normalNormalBgcolor;
                this.boldBgcolor = this.normalBoldBgcolor;
                this.reverseBgcolor = this.normalReverseBgcolor;
                this.boldReverseBgcolor = this.normalBoldReverseBgcolor;
                continue;
            }
            n2 = this.charAttributes[n - 1][n3 - 1];
            n4 = n3;
            while (n3 <= this.rightMargin && n2 == this.charAttributes[n - 1][n3 - 1] && !this.decSpl.isGlyph(this.charArrays[n - 1][n3 - 1])) {
                ++n3;
            }
            if (n2 >= 30000) {
                n2 -= 30000;
                this.blinkTimer.setBlinkChars(true);
                if (this.blinkStatus) {
                    this.normalFgcolor = this.blinkNormalFgcolor;
                    this.boldFgcolor = this.blinkBoldFgcolor;
                    this.reverseFgcolor = this.blinkReverseFgcolor;
                    this.boldReverseFgcolor = this.blinkBoldReverseFgcolor;
                    this.normalBgcolor = this.blinkNormalBgcolor;
                    this.boldBgcolor = this.blinkBoldBgcolor;
                    this.reverseBgcolor = this.blinkReverseBgcolor;
                    this.boldReverseBgcolor = this.blinkBoldReverseBgcolor;
                }
            } else if (this.paintFrom == 3) continue;
            switch (n2) {
                case 1: 
                case 15: {
                    if (this.boldBgcolor != this.bgColor) {
                        graphics.setColor(this.boldBgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                    }
                    graphics.setColor(this.boldFgcolor);
                    break;
                }
                case 7: 
                case 57: {
                    graphics.setColor(this.reverseBgcolor);
                    graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                    graphics.setColor(this.reverseFgcolor);
                    break;
                }
                case 4: 
                case 45: {
                    if (this.normalBgcolor != this.bgColor) {
                        graphics.setColor(this.normalBgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                    }
                    graphics.setColor(this.normalFgcolor);
                    graphics.fillRect((n4 - 1) * this.charWidth, n * this.charHeight - 1, (n3 - n4) * this.charWidth, 1);
                    graphics.setColor(this.normalFgcolor);
                    break;
                }
                case 17: 
                case 157: {
                    graphics.setColor(this.boldReverseBgcolor);
                    graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                    graphics.setColor(this.boldReverseFgcolor);
                    break;
                }
                case 14: 
                case 145: {
                    if (this.boldBgcolor != this.bgColor) {
                        graphics.setColor(this.boldBgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                    }
                    graphics.setColor(this.boldFgcolor);
                    graphics.fillRect((n4 - 1) * this.charWidth, n * this.charHeight - 1, (n3 - n4) * this.charWidth, 1);
                    graphics.setColor(this.boldFgcolor);
                    break;
                }
                case 74: 
                case 457: {
                    graphics.setColor(this.reverseBgcolor);
                    graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                    graphics.setColor(this.reverseFgcolor);
                    graphics.fillRect((n4 - 1) * this.charWidth, n * this.charHeight - 1, (n3 - n4) * this.charWidth, 1);
                    graphics.setColor(this.reverseFgcolor);
                    break;
                }
                case 174: 
                case 1457: {
                    graphics.setColor(this.boldReverseBgcolor);
                    graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                    graphics.setColor(this.boldReverseFgcolor);
                    graphics.fillRect((n4 - 1) * this.charWidth, n * this.charHeight - 1, (n3 - n4) * this.charWidth, 1);
                    graphics.setColor(this.boldReverseFgcolor);
                    break;
                }
                default: {
                    if (this.normalBgcolor != this.bgColor) {
                        graphics.setColor(this.normalBgcolor);
                        graphics.fillRect((n4 - 1) * this.charWidth, (n - 1) * this.charHeight, (n3 - n4) * this.charWidth, this.charHeight);
                    }
                    graphics.setColor(this.normalFgcolor);
                }
            }
            graphics.drawChars(this.charArrays[n - 1], n4 - 1, n3 - n4, (n4 - 1) * this.charWidth, n * this.charHeight - this.charDescent);
            if (!this.blinkStatus) continue;
            this.normalFgcolor = this.normalNormalFgcolor;
            this.boldFgcolor = this.normalBoldFgcolor;
            this.reverseFgcolor = this.normalReverseFgcolor;
            this.boldReverseFgcolor = this.normalBoldReverseFgcolor;
            this.normalBgcolor = this.normalNormalBgcolor;
            this.boldBgcolor = this.normalBoldBgcolor;
            this.reverseBgcolor = this.normalReverseBgcolor;
            this.boldReverseBgcolor = this.normalBoldReverseBgcolor;
        }
    }

    void draw(Graphics graphics, char[] cArray, int n, int n2, int n3, int n4, boolean bl) {
        if (!bl) {
            graphics.setColor(Color.black);
            this.setCursorOnOff(0);
        } else {
            graphics.setColor(Color.white);
            this.setCursorOnOff(1);
        }
        if (this.decSpl.isGlyph(cArray[n])) {
            for (int i = n; i < n + n2; ++i) {
                if (!this.decSpl.isGlyph(cArray[i])) continue;
                this.decSpl.drawGlyph(graphics, cArray[i], i * (n3 - 1), n4 - 1, this.charWidth, this.charHeight);
            }
        } else {
            graphics.drawChars(cArray, n, n2, n3, n4);
        }
    }

    void blink(boolean bl) {
        for (int i = 0; i < this.botMargin; ++i) {
            for (int j = 0; j < this.rightMargin; ++j) {
                if (this.lineAttributes[i] != 0 || this.charAttributes[i][j] != 5 && this.charAttributes[i][j] != 15 && this.charAttributes[i][j] != 45 && this.charAttributes[i][j] != 57 && this.charAttributes[i][j] != 145 && this.charAttributes[i][j] != 157 && this.charAttributes[i][j] != 457 && this.charAttributes[i][j] != 1457) continue;
                int n = this.charAttributes[i][j];
                int n2 = j;
                while (this.charAttributes[i][j++] == n && j < this.rightMargin) {
                }
                int n3 = j;
                this.draw(this.getGraphics(), this.charArrays[i - 0], n2, n3 - n2, n2 * this.charWidth, (i + 1) * this.charHeight - this.charDescent, bl);
            }
        }
    }

    private synchronized void paintDoubleWidthLine(Graphics graphics, int n) throws Exception {
        int n2;
        int n3 = this.charAttributes[n - 1][n2 - 1];
        int n4 = 0;
        int n5 = 0;
        for (n2 = 1; n2 <= this.rightMargin / 2; ++n2) {
            n3 = this.charAttributes[n - 1][n2 - 1];
            if (n3 >= 30000) {
                n3 -= 30000;
                this.blinkTimer.setBlinkChars(true);
                if (this.blinkStatus) {
                    this.normalFgcolor = this.blinkNormalFgcolor;
                    this.boldFgcolor = this.blinkBoldFgcolor;
                    this.reverseFgcolor = this.blinkReverseFgcolor;
                    this.boldReverseFgcolor = this.blinkBoldReverseFgcolor;
                    this.normalBgcolor = this.blinkNormalBgcolor;
                    this.boldBgcolor = this.blinkBoldBgcolor;
                    this.reverseBgcolor = this.blinkReverseBgcolor;
                    this.boldReverseBgcolor = this.blinkBoldReverseBgcolor;
                }
            }
            switch (n3) {
                case 1: 
                case 14: {
                    if (this.bgColor == LIGHT_BACKGROUND && this.blinkStatus && this.charArrays[n - 1][n2 - 1] != ' ' || this.bgColor == DARK_BACKGROUND) {
                        n4 = 0;
                        this.charColor = this.boldFgcolor;
                        break;
                    }
                    n4 = 2;
                    this.charColor = this.boldBgcolor;
                    break;
                }
                case 7: 
                case 74: {
                    if (this.bgColor == LIGHT_BACKGROUND || this.bgColor == DARK_BACKGROUND && this.blinkStatus) {
                        n4 = 0;
                        this.charColor = this.reverseFgcolor;
                        break;
                    }
                    n4 = 2;
                    this.charColor = this.reverseBgcolor;
                    break;
                }
                case 17: 
                case 174: {
                    if (this.bgColor == LIGHT_BACKGROUND || this.bgColor == DARK_BACKGROUND && this.blinkStatus) {
                        n4 = 0;
                        this.charColor = this.boldReverseFgcolor;
                        break;
                    }
                    n4 = 2;
                    this.charColor = this.boldReverseBgcolor;
                    break;
                }
                default: {
                    if (this.bgColor == LIGHT_BACKGROUND && this.blinkStatus && this.charArrays[n - 1][n2 - 1] != ' ' || this.bgColor == DARK_BACKGROUND) {
                        n4 = 0;
                        this.charColor = this.normalFgcolor;
                        break;
                    }
                    n4 = 2;
                    this.charColor = this.normalBgcolor;
                }
            }
            if (this.decSpl.isGlyph(this.charArrays[n - 1][n2 - 1])) {
                for (n5 = 0; n5 < DECSPECIAL.length && this.charArrays[n - 1][n2 - 1] != DECSPECIAL[n5]; ++n5) {
                }
                n4 = n4 == 0 ? (n4 += 4) : (n4 += 3);
            } else {
                char c = this.charArrays[n - 1][n2 - 1];
                if (c >= ' ') {
                    if (c < '\u007f') {
                        n5 = c - 32;
                    } else if (c >= '\u00a0' && c < '\u00fe') {
                        n5 = c - 160;
                        ++n4;
                    }
                }
            }
            graphics.drawImage(this.image, (n2 - 1) * this.charWidth * 2, (n - 1) * this.charHeight, n2 * this.charWidth * 2, n * this.charHeight, n5 * this.imageWidth, n4 * this.imageHeight, (n5 + 1) * this.imageWidth, (n4 + 1) * this.imageHeight, this.charColor, this);
            if (n3 == 4) {
                graphics.setColor(this.normalFgcolor);
                graphics.fillRect((n2 - 1) * this.charWidth * 2, n * this.charHeight - 1, this.charWidth * 2, 1);
                graphics.setColor(this.normalFgcolor);
            }
            if (n3 == 14) {
                graphics.setColor(this.boldFgcolor);
                graphics.fillRect((n2 - 1) * this.charWidth * 2, n * this.charHeight - 1, this.charWidth * 2, 1);
                graphics.setColor(this.normalFgcolor);
            }
            if (n3 == 74) {
                graphics.setColor(this.reverseFgcolor);
                graphics.fillRect((n2 - 1) * this.charWidth * 2, n * this.charHeight - 1, this.charWidth * 2, 1);
                graphics.setColor(this.normalFgcolor);
            }
            if (n3 == 174) {
                graphics.setColor(this.boldReverseFgcolor);
                graphics.fillRect((n2 - 1) * this.charWidth * 2, n * this.charHeight - 1, this.charWidth * 2, 1);
                graphics.setColor(this.normalFgcolor);
            }
            if (!this.blinkStatus) continue;
            this.normalFgcolor = this.normalNormalFgcolor;
            this.boldFgcolor = this.normalBoldFgcolor;
            this.reverseFgcolor = this.normalReverseFgcolor;
            this.boldReverseFgcolor = this.normalBoldReverseFgcolor;
            this.normalBgcolor = this.normalNormalBgcolor;
            this.boldBgcolor = this.normalBoldBgcolor;
            this.reverseBgcolor = this.normalReverseBgcolor;
            this.boldReverseBgcolor = this.normalBoldReverseBgcolor;
        }
    }

    private synchronized void paintDoubleHeightLine(Graphics graphics, int n, boolean bl) throws Exception {
        int n2;
        int n3 = this.charAttributes[n - 1][n2 - 1];
        int n4 = 0;
        int n5 = 0;
        for (n2 = 1; n2 <= this.rightMargin / 2; ++n2) {
            n3 = this.charAttributes[n - 1][n2 - 1];
            if (n3 >= 30000) {
                n3 -= 30000;
                this.blinkTimer.setBlinkChars(true);
                if (this.blinkStatus) {
                    this.normalFgcolor = this.blinkNormalFgcolor;
                    this.boldFgcolor = this.blinkBoldFgcolor;
                    this.reverseFgcolor = this.blinkReverseFgcolor;
                    this.boldReverseFgcolor = this.blinkBoldReverseFgcolor;
                    this.normalBgcolor = this.blinkNormalBgcolor;
                    this.boldBgcolor = this.blinkBoldBgcolor;
                    this.reverseBgcolor = this.blinkReverseBgcolor;
                    this.boldReverseBgcolor = this.blinkBoldReverseBgcolor;
                }
            }
            switch (n3) {
                case 1: 
                case 14: {
                    if (this.bgColor == LIGHT_BACKGROUND && this.blinkStatus && this.charArrays[n - 1][n2 - 1] != ' ' || this.bgColor == DARK_BACKGROUND) {
                        n4 = 0;
                        this.charColor = this.boldFgcolor;
                        break;
                    }
                    n4 = 2;
                    this.charColor = this.boldBgcolor;
                    break;
                }
                case 7: 
                case 74: {
                    if (this.bgColor == LIGHT_BACKGROUND || this.bgColor == DARK_BACKGROUND && this.blinkStatus) {
                        n4 = 0;
                        this.charColor = this.reverseFgcolor;
                        break;
                    }
                    n4 = 2;
                    this.charColor = this.reverseBgcolor;
                    break;
                }
                case 17: 
                case 174: {
                    if (this.bgColor == LIGHT_BACKGROUND || this.bgColor == DARK_BACKGROUND && this.blinkStatus) {
                        n4 = 0;
                        this.charColor = this.boldReverseFgcolor;
                        break;
                    }
                    n4 = 2;
                    this.charColor = this.boldReverseBgcolor;
                    break;
                }
                default: {
                    if (this.bgColor == LIGHT_BACKGROUND && this.blinkStatus && this.charArrays[n - 1][n2 - 1] != ' ' || this.bgColor == DARK_BACKGROUND) {
                        n4 = 0;
                        this.charColor = this.normalFgcolor;
                        break;
                    }
                    n4 = 2;
                    this.charColor = this.normalBgcolor;
                }
            }
            if (this.decSpl.isGlyph(this.charArrays[n - 1][n2 - 1])) {
                for (n5 = 0; n5 < DECSPECIAL.length && this.charArrays[n - 1][n2 - 1] != DECSPECIAL[n5]; ++n5) {
                }
                n4 = n4 == 0 ? (n4 += 4) : (n4 += 3);
            } else {
                char c = this.charArrays[n - 1][n2 - 1];
                if (c >= ' ') {
                    if (c < '\u007f') {
                        n5 = c - 32;
                    } else if (c >= '\u00a0' && c < '\u00fe') {
                        n5 = c - 160;
                        ++n4;
                    }
                }
            }
            if (bl) {
                graphics.drawImage(this.image, (n2 - 1) * this.charWidth * 2, (n - 1) * this.charHeight, n2 * this.charWidth * 2, n * this.charHeight, n5 * this.imageWidth, n4 * this.imageHeight, (n5 + 1) * this.imageWidth, n4 * this.imageHeight + this.imageHeight / 2, this.charColor, this);
            } else {
                graphics.drawImage(this.image, (n2 - 1) * this.charWidth * 2, (n - 1) * this.charHeight, n2 * this.charWidth * 2, n * this.charHeight, n5 * this.imageWidth, n4 * this.imageHeight + this.imageHeight / 2, (n5 + 1) * this.imageWidth, (n4 + 1) * this.imageHeight, this.charColor, this);
                if (n3 == 4) {
                    graphics.setColor(this.normalFgcolor);
                    graphics.fillRect((n2 - 1) * this.charWidth * 2, n * this.charHeight - 1, this.charWidth * 2, 1);
                    graphics.setColor(this.normalFgcolor);
                }
                if (n3 == 14) {
                    graphics.setColor(this.boldFgcolor);
                    graphics.fillRect((n2 - 1) * this.charWidth * 2, n * this.charHeight - 1, this.charWidth * 2, 1);
                    graphics.setColor(this.normalFgcolor);
                }
                if (n3 == 74) {
                    graphics.setColor(this.reverseFgcolor);
                    graphics.fillRect((n2 - 1) * this.charWidth * 2, n * this.charHeight - 1, this.charWidth * 2, 1);
                    graphics.setColor(this.normalFgcolor);
                }
                if (n3 == 174) {
                    graphics.setColor(this.boldReverseFgcolor);
                    graphics.fillRect((n2 - 1) * this.charWidth * 2, n * this.charHeight - 1, this.charWidth * 2, 1);
                    graphics.setColor(this.normalFgcolor);
                }
            }
            if (!this.blinkStatus) continue;
            this.normalFgcolor = this.normalNormalFgcolor;
            this.boldFgcolor = this.normalBoldFgcolor;
            this.reverseFgcolor = this.normalReverseFgcolor;
            this.boldReverseFgcolor = this.normalBoldReverseFgcolor;
            this.normalBgcolor = this.normalNormalBgcolor;
            this.boldBgcolor = this.normalBoldBgcolor;
            this.reverseBgcolor = this.normalReverseBgcolor;
            this.boldReverseBgcolor = this.normalBoldReverseBgcolor;
        }
    }

    void insertChars(int n, int n2) {
        for (int i = 0; i < n2; ++i) {
            System.arraycopy(this.bufferArrays[n + this.diffCursor - 1], this.cursorCol + i - 1, this.bufferArrays[n + this.diffCursor - 1], this.cursorCol + i, this.rightMargin - (this.cursorCol + i) - 1);
            System.arraycopy(this.bufCharAttributes[n + this.diffCursor - 1], this.cursorCol + i - 1, this.bufCharAttributes[n + this.diffCursor - 1], this.cursorCol + i, this.rightMargin - (this.cursorCol + i) - 1);
            this.bufferArrays[n + this.diffCursor - 1][this.cursorCol + i - 1] = 32;
            this.bufCharAttributes[n + this.diffCursor - 1][this.cursorCol + i - 1] = 0;
        }
    }

    void deleteChars(int n, int n2) {
        int n3 = this.rightMargin - 1;
        for (int i = 0; i < n2; ++i) {
            for (int j = this.cursorCol; j <= n3; ++j) {
                this.bufferArrays[n + this.diffCursor - 1][j - 1] = this.bufferArrays[n + this.diffCursor - 1][j];
            }
            this.bufferArrays[n + this.diffCursor - 1][n3] = 32;
            this.bufCharAttributes[n + this.diffCursor - 1][n3] = 0;
            --n3;
        }
    }

    void screenEraseCharacters(int n) {
        for (int i = 0; i < this.rightMargin - n && i + this.cursorCol - 1 != 80; ++i) {
            this.bufferArrays[this.cursorRow + this.diffCursor - 1][i + this.cursorCol - 1] = 32;
            this.bufCharAttributes[this.cursorRow + this.diffCursor - 1][i + this.cursorCol - 1] = 0;
        }
    }

    void clearLine(int n) {
        for (int i = 0; i < this.rightMargin; ++i) {
            this.bufferArrays[n - 1][i] = 32;
            this.bufCharAttributes[n - 1][i] = 0;
        }
        this.bufLineAttributes[n - 1] = 0;
    }

    void insertLine(int n, int n2) {
        int n3 = n;
        int n4 = this.cursorRow + this.diffCursor;
        this.update = true;
        if (n3 != 1 && n3 > this.botMargin - this.cursorRow) {
            n3 = this.botMargin - this.cursorRow;
        }
        for (int i = 0; i < n3; ++i) {
            int n5;
            int n6;
            if (n2 == 2) {
                for (n6 = this.diffCursor + this.botMargin; n6 > n4; --n6) {
                    for (n5 = 0; n5 < this.rightMargin; ++n5) {
                        this.bufferArrays[n6 - 1][n5] = this.bufferArrays[n6 - 2][n5];
                        this.bufCharAttributes[n6 - 1][n5] = this.bufCharAttributes[n6 - 2][n5];
                    }
                    this.bufLineAttributes[n6 - 1] = this.bufLineAttributes[n6 - 2];
                }
                this.clearLine(n4);
            } else if (n2 == 1) {
                for (n6 = 0; n6 < n4; ++n6) {
                    for (n5 = 0; n5 < this.rightMargin; ++n5) {
                        if (n6 >= n4 - 1) continue;
                        this.bufferArrays[n6][n5] = this.bufferArrays[n6 + 1][n5];
                        this.bufCharAttributes[n6][n5] = this.bufCharAttributes[n6 + 1][n5];
                    }
                    this.bufLineAttributes[n6] = this.bufLineAttributes[n6 + 1];
                }
                this.clearLine(n4);
            }
            ++n4;
        }
    }

    void scrollRows(int n) {
        int n2 = 0;
        this.maxDis = n + this.botMargin;
        if (this.maxDis > this.bufBotMargin) {
            this.maxDis = this.bufBotMargin;
        }
        int n3 = n;
        while (n3 < this.maxDis) {
            this.charArrays[n2] = this.bufferArrays[n3];
            this.charAttributes[n2] = this.bufCharAttributes[n3];
            this.lineAttributes[n2] = this.bufLineAttributes[n3];
            this.charUpdates[n2] = true;
            ++n3;
            ++n2;
        }
        if (n2 < this.botMargin) {
            while (n2 < this.botMargin) {
                for (n3 = 0; n3 < this.rightMargin; ++n3) {
                    this.charArrays[n2][n3] = 32;
                    this.charAttributes[n2][n3] = 0;
                }
                this.charUpdates[n2] = true;
                ++n2;
            }
        }
        this.repaintFrom = 2;
        this.cursorOnOff = 0;
        this.repaint(0, 0, this.getWidth(), this.getHeight());
        this.update = true;
        this.cursorOnOff = 1;
    }

    private int checkBounds(int n, int n2, int n3) {
        if (n < n2) {
            return n2;
        }
        if (n > n3) {
            return n3;
        }
        return n;
    }

    void screenClearEOD() {
        int n;
        this.update = true;
        for (n = this.cursorCol - 1; n < this.rightMargin; ++n) {
            this.bufferArrays[this.cursorRow + this.diffCursor - 1][n] = 32;
            this.bufCharAttributes[this.cursorRow + this.diffCursor - 1][n] = 0;
        }
        for (n = this.cursorRow + this.diffCursor; n <= this.bufBotMargin; ++n) {
            for (int i = 0; i < this.rightMargin; ++i) {
                this.bufferArrays[n - 1][i] = 32;
                this.bufCharAttributes[n - 1][i] = 0;
            }
            this.bufLineAttributes[n - 1] = 0;
        }
    }

    void screenClearBOD() {
        this.update = true;
        for (int i = this.diffCursor; i < this.cursorRow + this.diffCursor; ++i) {
            for (int j = 0; j <= (this.cursorRow + this.diffCursor - 1 != i ? this.rightMargin - 1 : this.cursorCol - 1); ++j) {
                this.bufferArrays[i][j] = 32;
                this.bufCharAttributes[i][j] = 0;
            }
            this.bufLineAttributes[i] = 0;
        }
    }

    synchronized void screenClearEntire() {
        for (int i = this.diffCursor + 1 - 1; i < this.diffCursor + 25; ++i) {
            for (int j = 0; j < this.rightMargin; ++j) {
                this.bufferArrays[i][j] = 32;
                this.bufCharAttributes[i][j] = 0;
            }
            this.bufLineAttributes[i] = 0;
        }
        this.update = true;
    }

    void screenClearEOL() {
        for (int i = this.cursorCol - 1; i < this.rightMargin; ++i) {
            this.bufferArrays[this.cursorRow + this.diffCursor - 1][i] = 32;
            this.bufCharAttributes[this.cursorRow + this.diffCursor - 1][i] = 0;
        }
    }

    void screenClearBOL() {
        for (int i = 0; i < this.cursorCol; ++i) {
            this.bufferArrays[this.cursorRow + this.diffCursor - 1][i] = 32;
            this.bufCharAttributes[this.cursorRow + this.diffCursor - 1][i] = 0;
        }
    }

    void screenClearLine() {
        for (int i = 0; i < this.rightMargin; ++i) {
            this.bufferArrays[this.cursorRow + this.diffCursor - 1][i] = 32;
            this.bufCharAttributes[this.cursorRow + this.diffCursor - 1][i] = 0;
        }
    }

    void deleteLine(int n) {
        int n2;
        int n3;
        int n4 = this.cursorRow + this.diffCursor;
        this.update = true;
        for (n3 = n4 - 1; n3 < this.diffCursor + this.botMargin - n; ++n3) {
            for (n2 = 0; n2 < this.rightMargin; ++n2) {
                this.bufferArrays[n3][n2] = this.bufferArrays[n3 + n][n2];
                this.bufCharAttributes[n3][n2] = this.bufCharAttributes[n3 + n][n2];
            }
        }
        while (n3 < this.diffCursor + this.botMargin) {
            for (n2 = 0; n2 < this.rightMargin; ++n2) {
                this.bufferArrays[n3][n2] = 32;
                this.bufCharAttributes[n3][n2] = 0;
            }
            this.bufLineAttributes[n3] = 0;
            ++n3;
        }
    }

    void fillWithE() {
        this.update = true;
        for (int i = 0; i < this.bufBotMargin; ++i) {
            for (int j = 0; j < this.rightMargin; ++j) {
                this.bufferArrays[i][j] = 69;
                this.bufCharAttributes[i][j] = 0;
            }
            this.bufLineAttributes[i] = 0;
        }
        this.diffCursor = 0;
    }

    void setImage10(Image image) {
        this.image10 = image;
    }

    void setImage13(Image image) {
        this.image13 = image;
    }

    public void onFinalize() {
        this.blinkTimer.stop();
        this.cutpasteBuf = null;
        this.charArrays = null;
        this.bufferArrays = null;
        this.charAttributes = null;
        this.bufCharAttributes = null;
        this.lineAttributes = null;
        this.bufLineAttributes = null;
        this.charUpdates = null;
        this.decSpl = null;
    }

    private class FocusRequester
    extends MouseAdapter {
        private FocusRequester() {
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            VDU.this.requestFocus();
        }
    }
}

