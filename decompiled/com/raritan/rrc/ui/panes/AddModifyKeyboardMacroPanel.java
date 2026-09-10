/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.commands.ShowMacroTextInterpreterCommand;
import com.raritan.rrc.util.SpringUtilities;
import com.raritan.tools.commands.CancelButtonCommand;
import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.CommandButton;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.util.kbd.KeyboardKey;
import com.util.kbd.KeyboardMappings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import nn.pp.common.ui.helpers.IMacroCreatorDialog;
import nn.pp.core.T;

public class AddModifyKeyboardMacroPanel
extends AbstractDisplay
implements ListDataListener,
ListSelectionListener,
IMacroCreatorDialog {
    protected static final int ALL_KEYS = 0;
    protected static final int LETTERS = 1;
    protected static final int NUMBERS = 2;
    protected static final int NON_GRAPHIC_KEYS = 3;
    protected static final int SHIFT_MODIFIERS = 4;
    protected static final int SYMBOLS = 5;
    protected static final int DIRECTION_KEYS = 6;
    protected static final int FUNCTIONAL = 7;
    protected static final int JAPANESE_KEYS = 8;
    protected static final int KEY_PAD_KEYS = 9;
    protected static final int SPECIAL_FUNCTIONS = 10;
    protected static final int KOREAN_KEYS = 11;
    protected static final int SUN_KEYS = 12;
    private static final long serialVersionUID = 1L;
    protected JTextField nameField;
    protected JComboBox hotKeyComboBox;
    protected JList keysToPressList;
    protected JList macroSequenceList;
    protected JButton addKeyButton;
    protected JButton removeButton;
    protected JButton upButton;
    protected JButton downButton;
    protected JButton clearButton;
    protected KeyListActionListener buttonListener;
    protected DefaultComboBoxModel hotKeyCombinationModel;
    protected DefaultComboBoxModel keysToPressModel;
    protected DefaultListModel keysToPressListModel;
    protected DefaultListModel macroSequenceListModel;
    protected final String[] hotKeyCombinationType = new String[11];
    protected final String[] keysToPressType = new String[13];
    protected KeyboardMappings keyboardMappings;
    protected final int[] letters = new int[]{57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82};
    protected final int[] numbers = new int[]{92, 83, 84, 85, 86, 87, 88, 89, 90, 91};
    protected final int[] nonGraphicKeys = new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 40};
    protected final int[] shiftModifiers = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    protected final int[] symbols = new int[]{93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 134};
    protected final int[] directionKeys = new int[]{19, 20, 21, 22, 23, 24, 25, 26, 27, 28};
    protected final int[] functionalKeys = new int[]{45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 135, 136, 137, 138};
    protected final int[] japaneseKeys = new int[]{104, 105, 106, 107, 108, 109, 110, 111, 116};
    protected final int[] keyPadKeys = new int[]{29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
    protected final int[] specialFunctions = new int[]{112, 113, 132, 133};
    protected final int[] koreanKeys = new int[]{114, 115};
    protected final int[] sunKeys = new int[]{117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131};
    private CommandButton launchMacroTextInterpreter = null;

    public AddModifyKeyboardMacroPanel(boolean bl, ScreenContext screenContext) {
        super(screenContext);
        this.isDialog = bl;
        this.bundle = RaritanResourceBundle.getResourceBundle(this.scrContext.getLocale());
        this.keyboardMappings = new KeyboardMappings();
        this.hotKeyCombinationType[0] = this.bundle.getString("NoHotKey.option");
        this.hotKeyCombinationType[1] = this.bundle.getString("CtrlAlt0.option");
        this.hotKeyCombinationType[2] = this.bundle.getString("CtrlAlt1.option");
        this.hotKeyCombinationType[3] = this.bundle.getString("CtrlAlt2.option");
        this.hotKeyCombinationType[4] = this.bundle.getString("CtrlAlt3.option");
        this.hotKeyCombinationType[5] = this.bundle.getString("CtrlAlt4.option");
        this.hotKeyCombinationType[6] = this.bundle.getString("CtrlAlt5.option");
        this.hotKeyCombinationType[7] = this.bundle.getString("CtrlAlt6.option");
        this.hotKeyCombinationType[8] = this.bundle.getString("CtrlAlt7.option");
        this.hotKeyCombinationType[9] = this.bundle.getString("CtrlAlt8.option");
        this.hotKeyCombinationType[10] = this.bundle.getString("CtrlAlt9.option");
        this.hotKeyCombinationModel = new DefaultComboBoxModel<String>(this.hotKeyCombinationType);
        this.keysToPressType[0] = this.bundle.getString("AllKeys.option");
        this.keysToPressType[1] = this.bundle.getString("Letters.option");
        this.keysToPressType[2] = this.bundle.getString("Numbers.option");
        this.keysToPressType[3] = this.bundle.getString("NonGraphicKeys.option");
        this.keysToPressType[4] = this.bundle.getString("ShiftModifiers.option");
        this.keysToPressType[5] = this.bundle.getString("Symbols.option");
        this.keysToPressType[6] = this.bundle.getString("DirectionKeys.option");
        this.keysToPressType[7] = this.bundle.getString("F1-F16.option");
        this.keysToPressType[8] = this.bundle.getString("JapaneseKeys.option");
        this.keysToPressType[9] = this.bundle.getString("KeyPadKeys.option");
        this.keysToPressType[10] = this.bundle.getString("SpecialFunctions.option");
        this.keysToPressType[11] = this.bundle.getString("KoreanKeys.option");
        this.keysToPressType[12] = this.bundle.getString("SunKeys.option");
        this.keysToPressModel = new DefaultComboBoxModel<String>(this.keysToPressType);
        this.keysToPressModel.addListDataListener(this);
        this.keysToPressListModel = new DefaultListModel();
        this.setKeysToPressModel(0);
        this.macroSequenceListModel = new DefaultListModel();
        this.buttonListener = new KeyListActionListener();
        this.makeLayout();
    }

    @Override
    public void makeLayout() {
        this.setLayout(new SpringLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel jPanel = new JPanel(new SpringLayout());
        jPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""), BorderFactory.createEmptyBorder(5, 65, 5, 65)));
        JLabel jLabel = new JLabel(this.bundle.getString("MacroName.label"), 11);
        jPanel.add(jLabel);
        this.nameField = new JTextField();
        jLabel.setLabelFor(this.nameField);
        jPanel.add(this.nameField);
        JLabel jLabel2 = new JLabel(this.bundle.getString("HotKeyCombination.label"), 11);
        jPanel.add(jLabel2);
        this.hotKeyComboBox = new JComboBox(this.hotKeyCombinationModel);
        jLabel2.setLabelFor(this.hotKeyComboBox);
        jPanel.add(this.hotKeyComboBox);
        SpringUtilities.makeCompactGrid(jPanel, 2, 2, 25, 5, 25, 5);
        this.add(jPanel);
        JPanel jPanel2 = new JPanel(new SpringLayout());
        jPanel2.setBorder(BorderFactory.createTitledBorder(""));
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new GridLayout(2, 1));
        JLabel jLabel3 = new JLabel(this.bundle.getString("KeysToPress.label"));
        jLabel3.setBackground(Color.RED);
        jLabel3.setAlignmentX(0.0f);
        jPanel3.add(jLabel3);
        JComboBox jComboBox = new JComboBox(this.keysToPressModel);
        jComboBox.setAlignmentX(0.0f);
        jPanel3.add(jComboBox);
        jPanel2.add(jPanel3);
        JPanel jPanel4 = new JPanel(new GridLayout(2, 1));
        JLabel jLabel4 = new JLabel(this.bundle.getString("MacroSequence.label"));
        jPanel4.add(jLabel4);
        this.launchMacroTextInterpreter = new CommandButton(this.scrContext);
        this.launchMacroTextInterpreter.setCommand(new ShowMacroTextInterpreterCommand(this.scrContext));
        this.launchMacroTextInterpreter.setText(T._("Construct Macro From Text"));
        this.launchMacroTextInterpreter.addActionListener(this);
        jPanel4.add(this.launchMacroTextInterpreter);
        jPanel2.add(jPanel4);
        this.keysToPressList = new JList(this.keysToPressListModel);
        this.keysToPressList.addListSelectionListener(this);
        this.keysToPressList.setSelectionMode(0);
        JScrollPane jScrollPane = new JScrollPane(this.keysToPressList, 20, 31);
        jPanel2.add(jScrollPane);
        Dimension dimension = jScrollPane.getSize();
        this.macroSequenceList = new JList(this.macroSequenceListModel);
        this.macroSequenceList.setSelectionMode(0);
        this.macroSequenceList.addListSelectionListener(this);
        JScrollPane jScrollPane2 = new JScrollPane(this.macroSequenceList, 20, 31);
        jScrollPane2.setMinimumSize(dimension);
        jScrollPane2.setMaximumSize(dimension);
        jScrollPane2.setPreferredSize(dimension);
        jPanel2.add(jScrollPane2);
        JPanel jPanel5 = new JPanel(new FlowLayout(1));
        this.addKeyButton = new JButton(this.bundle.getString("AddKey.button"));
        this.addKeyButton.setEnabled(false);
        this.addKeyButton.addActionListener(this.buttonListener);
        jPanel5.add(this.addKeyButton);
        jPanel2.add(jPanel5);
        JPanel jPanel6 = new JPanel(new FlowLayout(1));
        this.removeButton = new JButton(this.bundle.getString("Remove.button"));
        this.removeButton.setEnabled(false);
        this.removeButton.addActionListener(this.buttonListener);
        this.upButton = new JButton("^");
        this.upButton.setEnabled(false);
        this.upButton.addActionListener(this.buttonListener);
        this.downButton = new JButton("v");
        this.downButton.setEnabled(false);
        this.downButton.addActionListener(this.buttonListener);
        jPanel6.add(this.removeButton);
        jPanel6.add(this.upButton);
        jPanel6.add(this.downButton);
        jPanel2.add(jPanel6);
        SpringUtilities.makeCompactGrid(jPanel2, 3, 2, 10, 10, 10, 10);
        this.add(jPanel2);
        JPanel jPanel7 = new JPanel(new FlowLayout(1));
        this.ok = new CommandButton(this.bundle.getString("basescreen.command.ok.text"), this.scrContext);
        this.ok.setCommand(null);
        this.ok.addActionListener(this);
        jPanel7.add(this.ok);
        this.cancel = new CommandButton(this.bundle.getString("basescreen.command.cancel.text"), this.scrContext);
        this.cancel.setCommand(new CancelButtonCommand(this.scrContext));
        this.cancel.addActionListener(this);
        jPanel7.add(this.cancel);
        this.clearButton = new JButton(this.bundle.getString("Clear.button"));
        this.clearButton.setEnabled(false);
        this.clearButton.addActionListener(this.buttonListener);
        jPanel7.add(this.clearButton);
        this.add(jPanel7);
        SpringUtilities.makeCompactGrid(this, 3, 1, 6, 6, 6, 6);
    }

    @Override
    public void fillComponents(CommandContext commandContext) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                AddModifyKeyboardMacroPanel.this.nameField.grabFocus();
            }
        });
    }

    @Override
    protected void feedCommandContext(CommandContext commandContext) {
        if (commandContext.getCommandKey() == "showMacroTextInterpreterCommand") {
            commandContext.setCommandParameter("macroTextInterpreter", this.keyboardMappings);
            commandContext.setCommandParameter("macroTextInterpreterParent", this);
        }
    }

    protected void setKeysToPressModel(int n) {
        switch (n) {
            case 0: {
                int n2;
                this.keysToPressListModel.clear();
                Vector<KeyboardKey> vector = new Vector<KeyboardKey>();
                for (n2 = 0; n2 < this.keyboardMappings.length(); ++n2) {
                    if (this.isSpecialFunction(n2)) {
                        vector.add(this.keyboardMappings.getItem(n2));
                        continue;
                    }
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(n2));
                }
                for (n2 = 0; n2 < vector.size(); ++n2) {
                    this.keysToPressListModel.addElement(vector.get(n2));
                }
                break;
            }
            case 1: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.letters.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.letters[i]));
                }
                break;
            }
            case 2: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.numbers.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.numbers[i]));
                }
                break;
            }
            case 3: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.nonGraphicKeys.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.nonGraphicKeys[i]));
                }
                break;
            }
            case 4: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.shiftModifiers.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.shiftModifiers[i]));
                }
                break;
            }
            case 5: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.symbols.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.symbols[i]));
                }
                break;
            }
            case 6: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.directionKeys.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.directionKeys[i]));
                }
                break;
            }
            case 7: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.functionalKeys.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.functionalKeys[i]));
                }
                break;
            }
            case 8: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.japaneseKeys.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.japaneseKeys[i]));
                }
                break;
            }
            case 9: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.keyPadKeys.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.keyPadKeys[i]));
                }
                break;
            }
            case 10: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.specialFunctions.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.specialFunctions[i]));
                }
                break;
            }
            case 11: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.koreanKeys.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.koreanKeys[i]));
                }
                break;
            }
            case 12: {
                this.keysToPressListModel.clear();
                for (int i = 0; i < this.sunKeys.length; ++i) {
                    this.keysToPressListModel.addElement(this.keyboardMappings.getItem(this.sunKeys[i]));
                }
                break;
            }
        }
    }

    @Override
    public void contentsChanged(ListDataEvent listDataEvent) {
        DefaultComboBoxModel defaultComboBoxModel;
        if (listDataEvent.getSource() instanceof DefaultComboBoxModel && (defaultComboBoxModel = (DefaultComboBoxModel)listDataEvent.getSource()) == this.keysToPressModel) {
            Object object = defaultComboBoxModel.getSelectedItem();
            int n = defaultComboBoxModel.getIndexOf(object);
            this.setKeysToPressModel(n);
        }
    }

    @Override
    public void intervalAdded(ListDataEvent listDataEvent) {
    }

    @Override
    public void intervalRemoved(ListDataEvent listDataEvent) {
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (listSelectionEvent.getSource() instanceof JList) {
            JList jList = (JList)listSelectionEvent.getSource();
            if (jList == this.keysToPressList) {
                this.macroSequenceList.removeListSelectionListener(this);
                this.macroSequenceList.addListSelectionListener(this);
                this.removeButton.setEnabled(false);
                this.upButton.setEnabled(false);
                this.downButton.setEnabled(false);
            } else if (jList == this.macroSequenceList) {
                this.keysToPressList.removeListSelectionListener(this);
                this.keysToPressList.addListSelectionListener(this);
                this.removeButton.setEnabled(true);
                this.upButton.setEnabled(true);
                this.downButton.setEnabled(true);
            }
            this.addKeyButton.setEnabled(!this.keysToPressList.isSelectionEmpty());
            jList.ensureIndexIsVisible(jList.getSelectedIndex());
        }
    }

    protected int getHotKeyIndex(String string) {
        for (int i = 0; i < this.hotKeyCombinationType.length; ++i) {
            if (!string.equals(this.hotKeyCombinationType[i])) continue;
            return i - 1;
        }
        return -1;
    }

    private boolean isSpecialFunction(int n) {
        return Arrays.binarySearch(this.specialFunctions, n) > -1;
    }

    @Override
    public void fillSequenceModel(String string) {
        String[] stringArray = string.split("&&");
        KeyboardKey keyboardKey = null;
        KeyboardKey keyboardKey2 = null;
        int n = 0;
        String string2 = null;
        String string3 = this.bundle.getString("press.text");
        String string4 = this.bundle.getString("release.text");
        for (String string5 : stringArray) {
            n = Integer.parseInt(string5.substring(string5.lastIndexOf(" ") + 1));
            string2 = string5.substring(0, string5.lastIndexOf(" "));
            string2 = string2.equals("p") ? string3 : string4;
            keyboardKey = this.keyboardMappings.getItem(n);
            if (keyboardKey == null) continue;
            keyboardKey2 = new KeyboardKey(keyboardKey.getIndex(), keyboardKey.getKeyName(), keyboardKey.getKeyCode(), keyboardKey.getKeyLocation());
            keyboardKey2.setString(string2 + " " + keyboardKey2.getKeyName());
            this.macroSequenceListModel.addElement(keyboardKey2);
        }
        this.clearButton.setEnabled(!this.macroSequenceListModel.isEmpty());
    }

    class KeyListActionListener
    implements ActionListener {
        KeyListActionListener() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() instanceof JButton) {
                int n;
                JButton jButton = (JButton)actionEvent.getSource();
                if (jButton == AddModifyKeyboardMacroPanel.this.addKeyButton) {
                    KeyboardKey keyboardKey = (KeyboardKey)AddModifyKeyboardMacroPanel.this.keysToPressList.getSelectedValue();
                    KeyboardKey keyboardKey2 = new KeyboardKey(keyboardKey.getIndex(), keyboardKey.getKeyName(), keyboardKey.getKeyCode(), keyboardKey.getKeyLocation());
                    if (AddModifyKeyboardMacroPanel.this.isSpecialFunction(keyboardKey.getIndex())) {
                        AddModifyKeyboardMacroPanel.this.macroSequenceListModel.addElement(keyboardKey2);
                    } else {
                        keyboardKey2.setString(AddModifyKeyboardMacroPanel.this.bundle.getString("press.text") + " " + keyboardKey);
                        AddModifyKeyboardMacroPanel.this.macroSequenceListModel.addElement(keyboardKey2);
                        keyboardKey2 = new KeyboardKey(keyboardKey.getIndex(), keyboardKey.getKeyName(), keyboardKey.getKeyCode(), keyboardKey.getKeyLocation());
                        keyboardKey2.setString(AddModifyKeyboardMacroPanel.this.bundle.getString("release.text") + " " + keyboardKey);
                        AddModifyKeyboardMacroPanel.this.macroSequenceListModel.addElement(keyboardKey2);
                        AddModifyKeyboardMacroPanel.this.clearButton.setEnabled(true);
                    }
                    if (!AddModifyKeyboardMacroPanel.this.macroSequenceListModel.isEmpty()) {
                        AddModifyKeyboardMacroPanel.this.macroSequenceList.ensureIndexIsVisible(AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getSize() - 1);
                    }
                } else if (jButton == AddModifyKeyboardMacroPanel.this.removeButton) {
                    KeyboardKey keyboardKey = (KeyboardKey)AddModifyKeyboardMacroPanel.this.macroSequenceList.getSelectedValue();
                    if (keyboardKey != null) {
                        String string = keyboardKey.toString();
                        KeyboardKey keyboardKey3 = null;
                        String string2 = null;
                        string2 = string.startsWith(AddModifyKeyboardMacroPanel.this.bundle.getString("press.text")) ? string.replaceFirst(AddModifyKeyboardMacroPanel.this.bundle.getString("press.text"), AddModifyKeyboardMacroPanel.this.bundle.getString("release.text")) : string.replaceFirst(AddModifyKeyboardMacroPanel.this.bundle.getString("release.text"), AddModifyKeyboardMacroPanel.this.bundle.getString("press.text"));
                        if (string2 != null) {
                            for (int i = 0; i < AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getSize() && !string2.equals((keyboardKey3 = (KeyboardKey)AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getElementAt(i)).toString()); ++i) {
                            }
                        }
                        if (keyboardKey != null && keyboardKey3 != null) {
                            AddModifyKeyboardMacroPanel.this.macroSequenceListModel.removeElement(keyboardKey3);
                            AddModifyKeyboardMacroPanel.this.macroSequenceListModel.removeElement(keyboardKey);
                        }
                        if (AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getSize() == 0) {
                            AddModifyKeyboardMacroPanel.this.clearButton.setEnabled(false);
                        }
                    }
                } else if (jButton == AddModifyKeyboardMacroPanel.this.clearButton) {
                    AddModifyKeyboardMacroPanel.this.macroSequenceListModel.clear();
                    AddModifyKeyboardMacroPanel.this.clearButton.setEnabled(false);
                } else if (jButton == AddModifyKeyboardMacroPanel.this.upButton) {
                    int n2 = AddModifyKeyboardMacroPanel.this.macroSequenceList.getSelectedIndex();
                    if (n2 > 0) {
                        KeyboardKey keyboardKey = (KeyboardKey)AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getElementAt(n2 - 1);
                        KeyboardKey keyboardKey4 = (KeyboardKey)AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getElementAt(n2);
                        if (keyboardKey4.getKeyCode() == keyboardKey.getKeyCode() && keyboardKey4.toString().startsWith(AddModifyKeyboardMacroPanel.this.bundle.getString("release.text")) && keyboardKey.toString().startsWith(AddModifyKeyboardMacroPanel.this.bundle.getString("press.text"))) {
                            if (n2 > 1) {
                                KeyboardKey keyboardKey5 = (KeyboardKey)AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getElementAt(n2 - 2);
                                AddModifyKeyboardMacroPanel.this.macroSequenceListModel.set(n2 - 2, keyboardKey);
                                AddModifyKeyboardMacroPanel.this.macroSequenceListModel.set(n2 - 1, keyboardKey4);
                                AddModifyKeyboardMacroPanel.this.macroSequenceListModel.set(n2, keyboardKey5);
                                AddModifyKeyboardMacroPanel.this.macroSequenceList.setSelectedIndex(n2 - 1);
                            }
                        } else {
                            AddModifyKeyboardMacroPanel.this.macroSequenceListModel.set(n2 - 1, keyboardKey4);
                            AddModifyKeyboardMacroPanel.this.macroSequenceListModel.set(n2, keyboardKey);
                            AddModifyKeyboardMacroPanel.this.macroSequenceList.setSelectedIndex(n2 - 1);
                        }
                    }
                } else if (jButton == AddModifyKeyboardMacroPanel.this.downButton && (n = AddModifyKeyboardMacroPanel.this.macroSequenceList.getSelectedIndex()) < AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getSize() - 1) {
                    KeyboardKey keyboardKey = (KeyboardKey)AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getElementAt(n);
                    KeyboardKey keyboardKey6 = (KeyboardKey)AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getElementAt(n + 1);
                    if (keyboardKey.getKeyCode() == keyboardKey6.getKeyCode() && keyboardKey.toString().startsWith(AddModifyKeyboardMacroPanel.this.bundle.getString("press.text")) && keyboardKey6.toString().startsWith(AddModifyKeyboardMacroPanel.this.bundle.getString("release.text"))) {
                        if (n < AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getSize() - 2) {
                            KeyboardKey keyboardKey7 = (KeyboardKey)AddModifyKeyboardMacroPanel.this.macroSequenceListModel.getElementAt(n + 2);
                            AddModifyKeyboardMacroPanel.this.macroSequenceListModel.set(n, keyboardKey7);
                            AddModifyKeyboardMacroPanel.this.macroSequenceListModel.set(n + 1, keyboardKey);
                            AddModifyKeyboardMacroPanel.this.macroSequenceListModel.set(n + 2, keyboardKey6);
                            AddModifyKeyboardMacroPanel.this.macroSequenceList.setSelectedIndex(n + 1);
                        }
                    } else {
                        AddModifyKeyboardMacroPanel.this.macroSequenceListModel.set(n + 1, keyboardKey);
                        AddModifyKeyboardMacroPanel.this.macroSequenceListModel.set(n, keyboardKey6);
                        AddModifyKeyboardMacroPanel.this.macroSequenceList.setSelectedIndex(n + 1);
                    }
                }
            }
        }
    }
}

