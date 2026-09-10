/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.KeyStroke;
import nn.pp.core.Platform;
import nn.pp.core.T;
import nn.pp.rccore.IKeyboardMacro;
import nn.pp.rccore.impl.KeyboardInfoListenerList;
import nn.pp.rccore.impl.KeyboardListenerList;
import nn.pp.rccore.impl.RemoteConsoleRendererGraphical;
import nn.pp.rccore.impl.keyboard.CharTranslator;
import nn.pp.rccore.impl.keyboard.CharTranslator_fr_to_en;
import nn.pp.rccore.impl.keyboard.CharTranslator_fr_to_en_intl;
import nn.pp.rccore.impl.keyboard.KeyTranslator;
import nn.pp.rccore.impl.keyboard.KeyTranslator_da_DK;
import nn.pp.rccore.impl.keyboard.KeyTranslator_de_CH;
import nn.pp.rccore.impl.keyboard.KeyTranslator_de_DE;
import nn.pp.rccore.impl.keyboard.KeyTranslator_en_GB;
import nn.pp.rccore.impl.keyboard.KeyTranslator_en_US;
import nn.pp.rccore.impl.keyboard.KeyTranslator_es_ES;
import nn.pp.rccore.impl.keyboard.KeyTranslator_fr_BE;
import nn.pp.rccore.impl.keyboard.KeyTranslator_fr_CH;
import nn.pp.rccore.impl.keyboard.KeyTranslator_fr_FR_Impl;
import nn.pp.rccore.impl.keyboard.KeyTranslator_hu_HU;
import nn.pp.rccore.impl.keyboard.KeyTranslator_it_IT;
import nn.pp.rccore.impl.keyboard.KeyTranslator_ja_JP_Impl;
import nn.pp.rccore.impl.keyboard.KeyTranslator_ko_KR;
import nn.pp.rccore.impl.keyboard.KeyTranslator_no_NO;
import nn.pp.rccore.impl.keyboard.KeyTranslator_pt_PT;
import nn.pp.rccore.impl.keyboard.KeyTranslator_sl_SL;
import nn.pp.rccore.impl.keyboard.KeyTranslator_sv_SE;
import nn.pp.rccore.impl.keyboard.KeyboardEventConsumer;
import nn.pp.rccore.impl.mouse.MouseHandler;

public class KeyboardHandler
implements KeyListener,
FocusListener {
    private static final int CAPS = 28;
    private static final int LEFT_SHIFT = 41;
    private static final int RIGHT_SHIFT = 53;
    private static final int LEFT_CTRL = 54;
    private static final int ALT = 55;
    private static final int ALTGR = 57;
    private static final int RIGHT_CTRL = 58;
    private static final int NUMLOCK = 85;
    private static final int LEFT_WIN = 105;
    private static final int RIGHT_WIN = 107;
    private static final int ESCAPE = 59;
    private static final int ENTER = 27;
    private static final int NUMPAD_ENTER = 98;
    private static final int KANJI = 0;
    private static final int JP_CONVERT = 109;
    private static final int JP_NO_CONVERT = 108;
    private static final int JP_CAPS_LOCK = 28;
    private static final int JP_KANA = 110;
    private Logger logger;
    private KeyboardListenerList listenerList;
    private KeyboardInfoListenerList infoListenerList;
    private RemoteConsoleRendererGraphical renderer;
    private KeyStroke mouseSyncKeyStroke = null;
    private HashMap<Integer, KeyStroke> hotkeyKeyStrokes = null;
    private Hashtable<Integer, Boolean> hashMouseSync = null;
    private boolean mouseSyncCombo = false;
    private boolean altGrDown = false;
    private Object lock = new Object();
    private Vector<Integer> pressedKeys = new Vector();
    private Timer altGrPressTimer = null;
    private boolean ctrlPressPending = false;
    private boolean ctrlDown = false;
    private boolean leftAltPressed = false;
    private boolean leftAltReleased = false;
    private boolean leftCtrlReleased = false;
    private boolean altGraphDown = false;
    private boolean hotKeyDetected = false;
    private int[] modifiers = new int[]{41, 53, 54, 55, 57, 58, 105, 107};
    private int[] permanentModifiers = new int[]{28, 85};
    private KeyTranslator[] keyTranslatorList = new KeyTranslator[]{new KeyTranslator_en_US(), new KeyTranslator_da_DK(), new KeyTranslator_de_CH(), new KeyTranslator_de_DE(), new KeyTranslator_en_GB(), new KeyTranslator_es_ES(), new KeyTranslator_fr_CH(), new KeyTranslator_fr_FR_Impl(), new KeyTranslator_hu_HU(), new KeyTranslator_it_IT(), new KeyTranslator_ja_JP_Impl(), new KeyTranslator_ko_KR(), new KeyTranslator_fr_BE(), new KeyTranslator_no_NO(), new KeyTranslator_pt_PT(), new KeyTranslator_sl_SL(), new KeyTranslator_sv_SE(), new CharTranslator_fr_to_en(), new CharTranslator_fr_to_en_intl()};
    private KeyTranslator keyTranslator;
    private boolean ignoreNextNumpadKeyTyped = false;
    private boolean holdLCtrl = false;
    private long LCtrlTime;
    private int heldNumpadKeyNo;
    private boolean holdNumpadPress = false;
    private boolean macroJustSent = false;

    public KeyboardHandler(Logger logger, KeyboardListenerList keyboardListenerList, KeyboardInfoListenerList keyboardInfoListenerList, RemoteConsoleRendererGraphical remoteConsoleRendererGraphical) {
        this.logger = logger;
        this.listenerList = keyboardListenerList;
        this.infoListenerList = keyboardInfoListenerList;
        this.renderer = remoteConsoleRendererGraphical;
        this.loadKeyTranslator();
    }

    public void dispose() {
        this.releaseAllKeys();
        if (this.keyTranslatorList != null) {
            this.keyTranslatorList = null;
        }
        if (this.keyTranslator != null) {
            this.keyTranslator.dispose();
            this.keyTranslator = null;
        }
    }

    public void setMouseSyncHotkey(String string, KeyStroke keyStroke) {
        this.mouseSyncKeyStroke = keyStroke;
        this.renderer.setMouseSyncString(string);
    }

    public void setMouseSyncHotkey(String string, String string2) throws ParseException {
        if (this.mouseSyncKeyStroke != null) {
            return;
        }
        this.mouseSyncCombo = true;
        this.hashMouseSync = new Hashtable();
        if (string2 == null) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string2);
        while (stringTokenizer.hasMoreTokens()) {
            try {
                int n = Integer.parseInt(stringTokenizer.nextToken(), 16);
                this.logger.log(Level.FINER, "Sync: parsed keyNo: " + n);
                this.hashMouseSync.put(new Integer(n), new Boolean(false));
            }
            catch (NumberFormatException numberFormatException) {
                throw new ParseException(string2, 0);
            }
        }
        this.renderer.setMouseSyncString(string);
    }

    public boolean haveMouseSyncHotkey() {
        return this.mouseSyncKeyStroke != null || this.hashMouseSync != null;
    }

    public void setHotkeys(Map<Integer, KeyStroke> map) {
        this.hotkeyKeyStrokes = new HashMap();
        for (Map.Entry<Integer, KeyStroke> entry : map.entrySet()) {
            this.hotkeyKeyStrokes.put(entry.getKey(), entry.getValue());
        }
    }

    private boolean isJapaneseKey(int n) {
        return this.keyTranslator.getLocale().getLanguage().equals("ja") && (n >= 108 && n <= 110 || n == 0 || n == 28 || n >= 243 && n <= 245);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (this.handleHotkeys(keyEvent)) {
            return;
        }
        int n = this.translateKeyEvent(keyEvent);
        if (Platform.isMac() && n == 28) {
            this.processKeyPressed(n);
            this.processKeyReleased(n);
            this.logger.log(Level.INFO, T._("Key Pressed: Mac : Sending Press/Release for CAPS LOCK."));
            return;
        }
        if (Platform.isSun() && this.isPermanentModifier(n)) {
            this.processKeyPressed(n);
            this.processKeyReleased(n);
            this.logger.log(Level.INFO, T._("Key Pressed: Solaris : Sending Press/Release for CAPS/NUM LOCK."));
            return;
        }
        if ((n == 0 || n == 108 || n == 109 || n == 110 || n == 28) && this.keyTranslator.getLocale().getLanguage().equals("ja")) {
            return;
        }
        if (!this.keyTranslator.allowAltGr()) {
            if (Platform.isWindows()) {
                if (this.holdLCtrl) {
                    if (keyEvent.getWhen() == this.LCtrlTime && n == 57) {
                        this.holdLCtrl = false;
                        return;
                    }
                    this.holdLCtrl = false;
                    this.processKeyPressed(54);
                }
                if (n == 54) {
                    this.holdLCtrl = true;
                    this.LCtrlTime = keyEvent.getWhen();
                    return;
                }
            }
            if (n == 57) {
                return;
            }
        }
        this.setLeftAltDownFlag(keyEvent);
        this.setAltGraphDownFlag(keyEvent);
        this.processCtrlAltKeys(keyEvent);
        if (!(this.keyTranslator instanceof CharTranslator)) {
            keyEvent.consume();
        }
        if (keyEvent.getKeyLocation() == 4 && !this.holdNumpadPress) {
            switch (keyEvent.getKeyCode()) {
                case 10: 
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: {
                    this.ignoreNextNumpadKeyTyped = true;
                    if (!(this.keyTranslator instanceof CharTranslator)) break;
                    this.holdNumpadPress = true;
                    this.heldNumpadKeyNo = n;
                    return;
                }
            }
        }
        if (n < 0) {
            this.logger.log(Level.INFO, T._("No key translation found for key pressed event"));
            return;
        }
        boolean bl = false;
        if (keyEvent.isAltGraphDown() && n != 57 && !this.altGrDown) {
            this.logger.log(Level.FINER, " !!! Using virtual AltGr-Press !!!");
            this.processKeyPressed(57);
            bl = true;
        }
        this.handleKeyPressed(n);
        this.handleMouseSyncKey(n, false);
        if (bl) {
            this.processKeyReleased(57);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public void keyTyped(KeyEvent keyEvent) {
        if (this.holdLCtrl) {
            this.processKeyPressed(54);
            this.holdLCtrl = false;
        }
        this.handleHotkeys(keyEvent);
        this.handleMouseSyncKey(keyEvent);
        if (this.ignoreNextNumpadKeyTyped) {
            switch (keyEvent.getKeyChar()) {
                case '\n': 
                case '*': 
                case '+': 
                case ',': 
                case '-': 
                case '.': 
                case '/': 
                case '0': 
                case '1': 
                case '2': 
                case '3': 
                case '4': 
                case '5': 
                case '6': 
                case '7': 
                case '8': 
                case '9': {
                    this.ignoreNextNumpadKeyTyped = false;
                    if (!this.holdNumpadPress) return;
                    this.holdNumpadPress = false;
                    this.processKeyPressed(this.heldNumpadKeyNo);
                    return;
                }
            }
        }
        int n = this.translateKeyEvent(keyEvent);
        keyEvent.consume();
        if (n >= 0) {
            this.handleKeyTyped(n);
            return;
        }
        if (this.keyTranslator instanceof CharTranslator) {
            this.handleCharTranslation(keyEvent);
            return;
        }
        this.logger.log(Level.FINEST, T._("No key translation found for key typed event"));
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if (this.holdLCtrl) {
            this.processKeyPressed(54);
            this.holdLCtrl = false;
        }
        this.setLeftAltDownFlag(keyEvent);
        this.setAltGraphDownFlag(keyEvent);
        this.processCtrlAltKeys(keyEvent);
        int n = this.translateKeyEvent(keyEvent);
        if (!(this.keyTranslator instanceof CharTranslator)) {
            keyEvent.consume();
        }
        if (n < 0) {
            this.logger.log(Level.INFO, T._("No key translation found for key released event"));
            return;
        }
        this.handleMouseSyncKey(n, true);
        if (Platform.isMac() && n == 28) {
            this.processKeyPressed(n);
            this.processKeyReleased(n);
            this.logger.log(Level.INFO, T._("Key Released: Mac : Sending Press/Release for CAPS LOCK."));
            return;
        }
        if (Platform.isSun() && this.isPermanentModifier(n)) {
            this.processKeyPressed(n);
            this.processKeyReleased(n);
            this.logger.log(Level.INFO, T._("Key Released: Solaris : Sending Press/Release for CAPS/NUM LOCK."));
            return;
        }
        if (this.isJapaneseKey(n)) {
            this.handleKeyTyped(n);
        } else {
            this.handleKeyReleased(n);
        }
    }

    private void loadKeyTranslator() {
        this.setKeyTranslator(Locale.getDefault(), true);
        this.infoListenerList.fireLocalKeyboardMappingChanged(this.keyTranslator.getLocale());
    }

    public List<Locale> getLocalKeyboardMappings() {
        Vector<Locale> vector = new Vector<Locale>();
        for (int i = 0; i < this.keyTranslatorList.length; ++i) {
            vector.add(this.keyTranslatorList[i].getLocale());
        }
        return vector;
    }

    public void setLocalKeyboardMapping(Locale locale) throws IllegalArgumentException {
        this.setKeyTranslator(locale, false);
    }

    private void setKeyTranslator(Locale locale, boolean bl) throws IllegalArgumentException {
        String string;
        String string2;
        Object object;
        int n;
        KeyTranslator keyTranslator = null;
        for (n = 0; n < this.keyTranslatorList.length; ++n) {
            String string3;
            keyTranslator = this.keyTranslatorList[n];
            object = keyTranslator.getLocale();
            string2 = ((Locale)object).getLanguage();
            if (!string2.equals(locale.getLanguage()) || !(string = ((Locale)object).getCountry()).equals(locale.getCountry()) || !(string3 = ((Locale)object).getVariant()).equals(locale.getVariant())) continue;
            this.keyTranslator = keyTranslator;
            this.logger.log(Level.INFO, MessageFormat.format(T._("Found keyboard translator for language {0}, country {1}, variant {2}"), string2, string, string3));
            return;
        }
        if (!bl) {
            object = MessageFormat.format(T._("No keyboard translator for Locale {0} found!"), locale);
            this.logger.log(Level.SEVERE, (String)object);
            throw new IllegalArgumentException((String)object);
        }
        for (n = 0; n < this.keyTranslatorList.length; ++n) {
            keyTranslator = this.keyTranslatorList[n];
            object = keyTranslator.getLocale();
            string2 = ((Locale)object).getLanguage();
            if (!string2.equals(locale.getLanguage()) || !(string = ((Locale)object).getCountry()).equals(locale.getCountry())) continue;
            this.keyTranslator = keyTranslator;
            this.logger.log(Level.INFO, MessageFormat.format(T._("Found keyboard translator for language {0}, country {1}"), string2, string));
            return;
        }
        for (n = 0; n < this.keyTranslatorList.length; ++n) {
            keyTranslator = this.keyTranslatorList[n];
            object = keyTranslator.getLocale();
            string2 = ((Locale)object).getLanguage();
            if (!string2.equals(locale.getLanguage())) continue;
            this.keyTranslator = keyTranslator;
            this.logger.log(Level.INFO, MessageFormat.format(T._("Found keyboard translator for language {0}"), string2));
            return;
        }
        this.keyTranslator = this.keyTranslatorList[0];
        this.logger.log(Level.WARNING, MessageFormat.format(T._("No keyboard translator for Locale {0} found, using default one for {1}"), locale, this.keyTranslator.getLocale()));
    }

    private int translateKeyEvent(KeyEvent keyEvent) {
        return this.keyTranslator.translateKeyEvent(keyEvent);
    }

    public int translateKeyEvent(int n, char c, int n2) {
        return this.keyTranslator.translateKeyEvent(n, c, n2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleKeyPressed(int n) {
        Object object = this.lock;
        synchronized (object) {
            if (!this.pressedKeys.contains(n)) {
                this.pressedKeys.addElement(n);
                this.processKeyPressed(n);
            } else if (!this.isModifier(n) && !this.isPermanentModifier(n)) {
                this.processKeyPressed(n);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleKeyTyped(int n) {
        Object object = this.lock;
        synchronized (object) {
            if (!this.pressedKeys.contains(n)) {
                this.processKeyPressed(n);
                this.processKeyReleased(n);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleKeyReleased(int n) {
        Object object = this.lock;
        synchronized (object) {
            int n2 = this.pressedKeys.indexOf(n);
            if (n2 >= 0) {
                this.pressedKeys.removeElementAt(n2);
            } else if (Platform.isLinux() || this.keyTranslator.getLocale().getLanguage().equals("ja")) {
                switch (n) {
                    case 27: 
                    case 59: 
                    case 98: {
                        break;
                    }
                    default: {
                        if (this.macroJustSent) {
                            this.macroJustSent = false;
                            break;
                        }
                        this.processKeyPressed(n);
                    }
                }
            }
            this.processKeyReleased(n);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleCharTranslation(KeyEvent keyEvent) {
        Vector<Integer> vector = ((CharTranslator)this.keyTranslator).translateChar(keyEvent);
        if (vector != null) {
            for (int n : vector) {
                Object object;
                if ((n & 0x10000) != 0) {
                    object = this.lock;
                    synchronized (object) {
                        this.processKeyReleased(n ^ 0x10000);
                        continue;
                    }
                }
                object = this.lock;
                synchronized (object) {
                    this.processKeyPressed(n);
                }
            }
        } else {
            this.logger.log(Level.INFO, T._("No character translation sequence found for key typed event"));
            return;
        }
    }

    private void processKeyPressed(int n) {
        if (n == 54 && this.altGrPressTimer == null && this.keyTranslator.allowAltGr()) {
            this.startAltGrPressTimer();
            return;
        }
        if (n == 57 && this.keyTranslator.allowAltGr()) {
            if (this.ctrlPressPending) {
                this.ctrlPressPending = false;
                if (this.altGrPressTimer != null) {
                    this.altGrPressTimer.cancel();
                    this.altGrPressTimer = null;
                }
                this.altGrDown = true;
            } else if (this.altGrDown) {
                return;
            }
        }
        if (n == 57 && !this.keyTranslator.allowAltGr()) {
            return;
        }
        this.processKeyPressedInternal(n);
    }

    private void processKeyPressedInternal(int n) {
        KeyboardEventConsumer keyboardEventConsumer = this.renderer.getKeyboardEventConsumer();
        if (keyboardEventConsumer != null) {
            keyboardEventConsumer.consumeKeyboardEvent(n, false);
        }
        this.listenerList.fireKeyboardEvent(n, false);
    }

    private void processKeyReleased(int n) {
        if (n == 54) {
            if (this.altGrDown && !this.ctrlDown) {
                return;
            }
            this.ctrlDown = false;
            if (this.ctrlPressPending) {
                if (this.altGrPressTimer != null) {
                    this.altGrPressTimer.cancel();
                    this.altGrPressTimer = null;
                }
                this.processKeyPressedInternal(n);
            }
        } else if (n == 57 && this.keyTranslator instanceof CharTranslator) {
            this.altGrDown = false;
        }
        this.processKeyReleasedInternal(n);
    }

    private void processKeyReleasedInternal(int n) {
        KeyboardEventConsumer keyboardEventConsumer = this.renderer.getKeyboardEventConsumer();
        if (keyboardEventConsumer != null) {
            keyboardEventConsumer.consumeKeyboardEvent(n, true);
        }
        this.listenerList.fireKeyboardEvent(n, true);
    }

    private void startAltGrPressTimer() {
        this.altGrPressTimer = new Timer("AltGrPress");
        this.altGrPressTimer.schedule(new TimerTask(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                Object object = KeyboardHandler.this.lock;
                synchronized (object) {
                    if (KeyboardHandler.this.ctrlPressPending) {
                        KeyboardHandler.this.processKeyPressedInternal(54);
                        KeyboardHandler.this.ctrlPressPending = false;
                        KeyboardHandler.this.ctrlDown = true;
                        KeyboardHandler.this.altGrPressTimer = null;
                    }
                }
            }
        }, 30L);
        this.ctrlPressPending = true;
    }

    private boolean isModifier(int n) {
        for (int i = 0; i < this.modifiers.length; ++i) {
            if (n != this.modifiers[i]) continue;
            return true;
        }
        return false;
    }

    private boolean isPermanentModifier(int n) {
        for (int i = 0; i < this.permanentModifiers.length; ++i) {
            if (n != this.permanentModifiers[i]) continue;
            return true;
        }
        return false;
    }

    private void handleMouseSyncKey(int n, boolean bl) {
        if (this.mouseSyncKeyStroke != null) {
            return;
        }
        if (this.hashMouseSync == null || this.hashMouseSync.isEmpty()) {
            return;
        }
        if (bl) {
            if (this.hashMouseSync.containsKey(n)) {
                this.hashMouseSync.put(n, new Boolean(false));
            }
        } else if (this.hashMouseSync.containsKey(n)) {
            this.hashMouseSync.put(n, new Boolean(true));
        }
        if (!this.hashMouseSync.contains(new Boolean(false))) {
            this.logger.log(Level.FINER, "Sync: all keys pressed");
            if (this.mouseSyncCombo) {
                MouseHandler mouseHandler = this.renderer.getMouseHandler();
                if (mouseHandler != null) {
                    mouseHandler.handleMouseSyncHotkey();
                }
                this.mouseSyncCombo = false;
            }
        }
        if (!this.hashMouseSync.contains(new Boolean(true))) {
            this.mouseSyncCombo = true;
        }
    }

    private void handleMouseSyncKey(KeyEvent keyEvent) {
        MouseHandler mouseHandler;
        if (this.mouseSyncKeyStroke == null) {
            return;
        }
        if (KeyStroke.getKeyStrokeForEvent(keyEvent).equals(this.mouseSyncKeyStroke) && (mouseHandler = this.renderer.getMouseHandler()) != null) {
            mouseHandler.handleMouseSyncHotkey();
        }
    }

    private boolean handleHotkeys(KeyEvent keyEvent) {
        if (this.hotkeyKeyStrokes == null) {
            return false;
        }
        if (!(this.keyTranslator instanceof CharTranslator)) {
            keyEvent.consume();
        }
        this.hotKeyDetected = false;
        for (Map.Entry<Integer, KeyStroke> entry : this.hotkeyKeyStrokes.entrySet()) {
            KeyStroke keyStroke = entry.getValue();
            if (!KeyStroke.getKeyStrokeForEvent(keyEvent).equals(keyStroke) || !this.leftAltPressed) continue;
            this.listenerList.fireHotkeyDetected(entry.getKey());
            this.hotKeyDetected = true;
            this.leftAltPressed = false;
            return true;
        }
        return false;
    }

    @Override
    public void focusLost(FocusEvent focusEvent) {
        this.releaseAllKeys();
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        this.releaseAllKeys();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void releaseAllKeys() {
        Object object = this.lock;
        synchronized (object) {
            this.altGrDown = false;
            this.ctrlDown = false;
            for (int n : this.pressedKeys) {
                this.logger.log(Level.FINER, "Releasing key " + n);
                this.processKeyReleasedInternal(n);
            }
            this.pressedKeys.clear();
            this.ctrlPressPending = false;
        }
    }

    public synchronized void doKeyboardMacro(IKeyboardMacro iKeyboardMacro, boolean bl) throws IOException {
        Stack<Integer> stack = new Stack<Integer>();
        IKeyboardMacro.IKeyCode[] iKeyCodeArray = iKeyboardMacro.getKeycodes();
        if (bl) {
            this.processKeyReleasedInternal(55);
            this.processKeyReleasedInternal(54);
            this.processKeyReleasedInternal(55);
            this.processKeyReleasedInternal(58);
            this.processKeyReleasedInternal(55);
        }
        block10: for (int i = 0; i < iKeyCodeArray.length; ++i) {
            IKeyboardMacro.IKeyCode iKeyCode = iKeyCodeArray[i];
            int n = iKeyCode.getCode();
            if (iKeyCode.isDelay()) {
                try {
                    Thread.sleep(n);
                }
                catch (Exception exception) {}
                continue;
            }
            this.logger.log(Level.FINER, "ButtonKey: keyNo: " + n);
            switch (n) {
                case 242: {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (Exception exception) {}
                    continue block10;
                }
                case 241: {
                    this.releaseStackKeys(stack);
                    continue block10;
                }
                case 243: {
                    this.releaseLastStackKey(stack);
                    continue block10;
                }
                case 240: {
                    continue block10;
                }
                default: {
                    Integer n2 = new Integer(n);
                    if (iKeyCode.isPress()) {
                        stack.push(n2);
                        this.processKeyPressedInternal(n2);
                        continue block10;
                    }
                    int n3 = stack.lastIndexOf(n2);
                    if (n3 != -1) {
                        stack.removeElementAt(n3);
                    }
                    this.processKeyReleasedInternal(n2);
                }
            }
        }
        this.releaseStackKeys(stack);
    }

    public void sendKeyboardMacro(IKeyboardMacro iKeyboardMacro, boolean bl) {
        final IKeyboardMacro iKeyboardMacro2 = iKeyboardMacro;
        final boolean bl2 = bl;
        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    KeyboardHandler.this.doKeyboardMacro(iKeyboardMacro2, bl2);
                }
                catch (IOException iOException) {
                    KeyboardHandler.this.logger.log(Level.WARNING, "Unable to send keyboard macro");
                }
            }
        });
        thread.start();
        this.macroJustSent = true;
    }

    private void releaseStackKeys(Stack<Integer> stack) {
        try {
            while (true) {
                Integer n = stack.pop();
                this.processKeyReleasedInternal(n);
            }
        }
        catch (EmptyStackException emptyStackException) {
            return;
        }
    }

    private void releaseLastStackKey(Stack<Integer> stack) {
        try {
            Integer n = stack.pop();
            this.processKeyReleasedInternal(n);
        }
        catch (EmptyStackException emptyStackException) {
            // empty catch block
        }
    }

    private void setLeftAltDownFlag(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 18 && !this.altGraphDown && keyEvent.getKeyLocation() == 2 && keyEvent.getID() == 401) {
            this.leftAltPressed = true;
        }
    }

    private void setAltGraphDownFlag(KeyEvent keyEvent) {
        if (keyEvent.isControlDown() && keyEvent.isAltDown() && keyEvent.getKeyLocation() == 3 && keyEvent.getID() == 401) {
            this.altGraphDown = true;
        } else if (keyEvent.getKeyLocation() == 3 && this.altGraphDown && keyEvent.getID() == 402) {
            this.altGraphDown = false;
        }
    }

    private void processCtrlAltKeys(KeyEvent keyEvent) {
        if (this.leftAltPressed && keyEvent.getKeyCode() == 18 && keyEvent.getID() == 402 && keyEvent.getKeyLocation() == 2) {
            this.leftAltReleased = true;
            this.leftAltPressed = false;
        } else if (this.leftCtrlReleased) {
            this.leftCtrlReleased = false;
            this.leftAltReleased = false;
        } else if (keyEvent.getKeyCode() == 17 && keyEvent.getID() == 402) {
            this.leftCtrlReleased = true;
        } else if (this.leftAltReleased) {
            this.leftCtrlReleased = false;
            this.leftAltReleased = false;
        }
        if (this.leftAltReleased && this.leftCtrlReleased) {
            if (!this.hotKeyDetected) {
                this.listenerList.fireCtrlAltReleaseDetected(true);
            }
            this.leftAltReleased = false;
            this.leftCtrlReleased = false;
        }
    }
}

