/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.rrc.util.MPCUtil;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import javaclientlib.utils.RRCLogger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class KeyboardMacrosPreferences {
    public static final String ROOT_NODE = "/KeyboardMacros";
    private static final String FILE_NAME = System.getProperty("user.home") != null ? System.getProperty("user.home") + System.getProperty("file.separator") + "KeyboardMacros.xml" : "KeyboardMacros.xml";
    private static final String TEMP_FILE_NAME = System.getProperty("user.home") != null ? System.getProperty("user.home") + System.getProperty("file.separator") + "KeyboardMacrosTemp.xml" : "KeyboardMacrosTemp.xml";
    private static final String MACRO_NAME_KEY = "name";
    private static final String HOT_KEY_COMBINATION_KEY = "hotKey";
    private static final String MACRO_SEQUENCE_KEY = "macroSequence";
    private static final int NUMBER_OF_ATTRIBUTES = 3;
    private static final int TIME_DELAY_INTERVAL = 700;
    private static final int TIME_DELAY_CYCLES = 3;
    private Preferences rootPrefs = Preferences.userRoot().node("/KeyboardMacros");
    private String macroName = "";
    private int hotKeyCombination = -1;
    private String macroSequence = "";

    public static void exportPreferences() {
        KeyboardMacrosPreferences.exportPreferencesFunction(FILE_NAME);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean exportPreferencesFunction(String string) {
        Preferences preferences = Preferences.userRoot().node(ROOT_NODE);
        OutputStream outputStream = null;
        boolean bl = true;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(KeyboardMacrosPreferences.getOrCreateXmlFile(string)));
            preferences.exportSubtree(outputStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
            bl = false;
            RRCLogger.log(100, 33, fileNotFoundException, "File Not Found when attempting to export Keyboard Macros from backing store to file: " + string);
        }
        catch (IOException iOException) {
            bl = false;
            RRCLogger.log(100, 33, iOException, "IO Exception occurred when attempting to export Keyboard Macros from backing store to file: " + string);
        }
        catch (BackingStoreException backingStoreException) {
            bl = false;
            RRCLogger.log(100, 33, backingStoreException, "Backing Store Exception occurred when attempting to export Keyboard Macros from backing store to file: " + string);
        }
        finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream = null;
                }
                catch (IOException iOException) {
                    bl = false;
                }
            }
        }
        return bl;
    }

    private static boolean attemptSafeExport(String string) {
        boolean bl = false;
        for (int i = 0; !bl && i < 2100; i += 700) {
            if (i > 0) {
                try {
                    Thread.sleep(i);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
            bl = KeyboardMacrosPreferences.exportPreferencesFunction(string);
        }
        return bl;
    }

    public static CommandResult doExport(TreeMap treeMap, File file, RaritanPropertyResourceBundle raritanPropertyResourceBundle) {
        File file2;
        CommandResult commandResult = new CommandResult(true, raritanPropertyResourceBundle.getString("macro.export.success"));
        if (!KeyboardMacrosPreferences.attemptSafeExport(TEMP_FILE_NAME)) {
            commandResult.setStatusMessage(raritanPropertyResourceBundle.getString("macro.export.write.temp.failed"));
            commandResult.setIsSuccess(false);
            return commandResult;
        }
        String[] stringArray = KeyboardMacrosPreferences.returnNodes();
        for (int i = 0; i < stringArray.length; ++i) {
            if (treeMap.containsKey(stringArray[i])) continue;
            KeyboardMacrosPreferences.deleteNode(stringArray[i], false);
        }
        if (!KeyboardMacrosPreferences.attemptSafeExport(file.getAbsolutePath())) {
            commandResult.setStatusMessage(raritanPropertyResourceBundle.getString("macro.export.write.failed"));
            commandResult.setIsSuccess(false);
        }
        if (!KeyboardMacrosPreferences.importPreferences(file2 = new File(TEMP_FILE_NAME))) {
            try {
                Thread.sleep(700L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            if (!KeyboardMacrosPreferences.importPreferences(file2)) {
                commandResult.setStatusMessage((commandResult.isSuccess() ? "" : commandResult.getStatusMessage() + "\n\n") + raritanPropertyResourceBundle.getString("macro.export.reimport.failed"));
            }
        }
        if (commandResult.isSuccess()) {
            file2.delete();
        }
        return commandResult;
    }

    public void exportPreferences(String string) {
        this.exportPreferences(string, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void exportPreferences(String string, boolean bl) {
        Preferences preferences = this.rootPrefs.node(string);
        preferences.put(MACRO_NAME_KEY, this.macroName);
        preferences.putInt(HOT_KEY_COMBINATION_KEY, this.hotKeyCombination);
        preferences.put(MACRO_SEQUENCE_KEY, this.macroSequence);
        if (!bl) {
            return;
        }
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(KeyboardMacrosPreferences.getOrCreateXmlFile(FILE_NAME)));
            this.rootPrefs.exportSubtree(outputStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
            RRCLogger.log(100, 33, fileNotFoundException, "File Not Found Exception occurred when attempting to export Keyboard Macro '" + string + "' from backing store to file: " + FILE_NAME);
        }
        catch (IOException iOException) {
            RRCLogger.log(100, 33, iOException, "IO Exception occurred when attempting to export Keyboard Macro '" + string + "' from backing store to file: " + FILE_NAME);
        }
        catch (BackingStoreException backingStoreException) {
            RRCLogger.log(100, 33, backingStoreException, "Backing Store Exception occurred when attempting to export Keyboard Macro '" + string + "' from backing store to file: " + FILE_NAME);
        }
        finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream = null;
                }
                catch (IOException iOException) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void importPreferences() {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(KeyboardMacrosPreferences.getOrCreateXmlFile(FILE_NAME)));
            KeyboardMacrosPreferences.importPreferences(inputStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
            RRCLogger.log(100, 33, fileNotFoundException, "File Not Found Exception occurred when attempting to import Keyboard Macros from file: " + FILE_NAME);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                }
                catch (IOException iOException) {}
            }
        }
    }

    public static boolean importPreferences(File file) {
        boolean bl = false;
        try {
            file.createNewFile();
            FileInputStream fileInputStream = new FileInputStream(file);
            bl = KeyboardMacrosPreferences.importPreferences(fileInputStream);
        }
        catch (IOException iOException) {
            RRCLogger.log(100, 33, iOException, "IO Exception occurred when attempting to import Keyboard Macros from file: " + file);
            return false;
        }
        return bl;
    }

    public static boolean importPreferences(InputStream inputStream) {
        try {
            Preferences.importPreferences(inputStream);
        }
        catch (InvalidPreferencesFormatException invalidPreferencesFormatException) {
            RRCLogger.log(100, 33, invalidPreferencesFormatException, "Invalid Preferences Format Exception occurred when attempting to import Keyboard Macros from input stream");
            return false;
        }
        catch (IOException iOException) {
            RRCLogger.log(100, 33, iOException, "IO Exception occurred when attempting to import Keyboard Macros from input stream");
            return false;
        }
        return true;
    }

    public void importSinglePreferenceInfo(String string) {
        Preferences preferences = this.rootPrefs.node(string);
        this.macroName = preferences.get(MACRO_NAME_KEY, "");
        this.hotKeyCombination = preferences.getInt(HOT_KEY_COMBINATION_KEY, -1);
        this.macroSequence = preferences.get(MACRO_SEQUENCE_KEY, "");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void importPreferences(String string) {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(KeyboardMacrosPreferences.getOrCreateXmlFile(FILE_NAME)));
            Preferences.importPreferences(inputStream);
            Preferences preferences = this.rootPrefs.node(string);
            this.macroName = preferences.get(MACRO_NAME_KEY, "");
            this.hotKeyCombination = preferences.getInt(HOT_KEY_COMBINATION_KEY, -1);
            this.macroSequence = preferences.get(MACRO_SEQUENCE_KEY, "");
        }
        catch (InvalidPreferencesFormatException invalidPreferencesFormatException) {
            RRCLogger.log(100, 33, invalidPreferencesFormatException, "Invalid Preferences Format Exception occurred when attempting to import Keyboard Macro '" + string + "' from file: " + FILE_NAME);
        }
        catch (IOException iOException) {
            RRCLogger.log(100, 33, iOException, "IOException occurred when attempting to import Keyboard Macro '" + string + "' from file: " + FILE_NAME);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                }
                catch (IOException iOException) {}
            }
        }
    }

    public static void deleteNode(String string) {
        KeyboardMacrosPreferences.deleteNode(string, true);
    }

    public static void deleteNode(String string, boolean bl) {
        Preferences preferences = Preferences.userRoot().node(ROOT_NODE);
        try {
            preferences.node(string).removeNode();
            if (bl) {
                KeyboardMacrosPreferences.exportPreferences();
            }
        }
        catch (BackingStoreException backingStoreException) {
            RRCLogger.log(100, 33, backingStoreException, "Backing Store Exception occurred when attempting to delete Keyboard Macros '" + string);
        }
    }

    public static String[] returnNodes() {
        KeyboardMacrosPreferences.importPreferences();
        Preferences preferences = Preferences.userRoot().node(ROOT_NODE);
        String[] stringArray = null;
        try {
            stringArray = preferences.childrenNames();
        }
        catch (BackingStoreException backingStoreException) {
            RRCLogger.log(100, 33, backingStoreException, "Backing Store Exception occurred when attempting to get list of Keyboard Macros");
        }
        return stringArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static File getOrCreateXmlFile(String string) {
        File file = new File(string);
        BufferedWriter bufferedWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
                bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                bufferedWriter.write("\n\n");
                bufferedWriter.write("<!DOCTYPE preferences SYSTEM 'http://java.sun.com/dtd/preferences.dtd'>");
                bufferedWriter.write("\n\n");
                bufferedWriter.write("<preferences EXTERNAL_XML_VERSION=\"1.0\">");
                bufferedWriter.write("\n  <root type=\"user\">");
                bufferedWriter.write("\n    <map />\n    <node name=\"KeyboardMacros\">\n      <map />");
                bufferedWriter.write("\n    </node>\n  </root>\n</preferences>");
                bufferedWriter.flush();
            }
        }
        catch (IOException iOException) {
            RRCLogger.log(100, 33, iOException, "IO Exception occurred when attempting to check for and create Keyboard Macros file: " + string);
        }
        finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                    bufferedWriter = null;
                }
                catch (IOException iOException) {}
            }
        }
        return file;
    }

    public static boolean containsNode(String string) {
        Preferences preferences = Preferences.userRoot().node(ROOT_NODE);
        try {
            return preferences.nodeExists(string);
        }
        catch (BackingStoreException backingStoreException) {
            RRCLogger.log(100, 33, backingStoreException, "Backing Store Exception occurred when attempting to check for the existence of Keyboard Macro '" + string + "'");
            return false;
        }
    }

    public int getHotKeyCombination() {
        return this.hotKeyCombination;
    }

    public String getMacroName() {
        return this.macroName;
    }

    public String getMacroSequence() {
        return this.macroSequence;
    }

    public void setHotKeyCombination(int n) {
        this.hotKeyCombination = n;
    }

    public void setMacroName(String string) {
        this.macroName = string;
    }

    public void setMacroSequence(String string) {
        this.macroSequence = string;
    }

    public static File chooseXmlFile(String string, int n, Component component) {
        JFileChooser jFileChooser = new JFileChooser(){

            @Override
            protected JDialog createDialog(Component component) throws HeadlessException {
                JDialog jDialog = super.createDialog(component);
                MPCUtil.jre17WorkaroundInheritAlwaysOnTop(jDialog);
                return jDialog;
            }
        };
        KeyboardMacrosPreferences keyboardMacrosPreferences = new KeyboardMacrosPreferences();
        keyboardMacrosPreferences.getClass();
        xmlFileFilter xmlFileFilter2 = keyboardMacrosPreferences.new xmlFileFilter();
        jFileChooser.setFileFilter(xmlFileFilter2);
        jFileChooser.setDialogTitle(string);
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setDialogType(n);
        jFileChooser.setAcceptAllFileFilterUsed(false);
        if (System.getProperty("user.home") != null) {
            jFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }
        int n2 = -1;
        if (n == 0) {
            n2 = jFileChooser.showOpenDialog(component);
        } else if (n == 1) {
            n2 = jFileChooser.showSaveDialog(component);
        }
        if (n2 == 0) {
            return jFileChooser.getSelectedFile().toString().endsWith(".xml") ? jFileChooser.getSelectedFile() : new File(jFileChooser.getSelectedFile().toString() + ".xml");
        }
        return null;
    }

    public static Element getMacrosElement(File file) {
        Document document = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        }
        catch (SAXException sAXException) {
            RRCLogger.log(100, 33, sAXException, "SAX Exception occurred when creating Document to read and find the Keyboard Macros element from file: " + file);
            return null;
        }
        catch (IOException iOException) {
            RRCLogger.log(100, 33, iOException, "IO Exception occurred when creating Document to read and find the Keyboard Macros element from file: " + file);
            return null;
        }
        catch (ParserConfigurationException parserConfigurationException) {
            RRCLogger.log(100, 33, parserConfigurationException, "Parser Configuration Exception occurred when creating Document to read and find the Keyboard Macros element from file: " + file);
            return null;
        }
        Element element = document.getDocumentElement();
        NodeList nodeList = element.getElementsByTagName("node");
        element = null;
        Node node = null;
        for (int i = 0; i < nodeList.getLength(); ++i) {
            node = nodeList.item(i);
            if (node.getNodeType() != 1 || !((Element)node).getAttribute(MACRO_NAME_KEY).equals("KeyboardMacros")) continue;
            element = (Element)node;
        }
        return element;
    }

    public static TreeMap getValidMacros(NodeList nodeList) {
        TreeMap<String, KeyboardMacrosPreferences> treeMap = new TreeMap<String, KeyboardMacrosPreferences>();
        KeyboardMacrosPreferences keyboardMacrosPreferences = null;
        Element element = null;
        NodeList nodeList2 = null;
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (nodeList.item(i).getNodeType() != 1 || (nodeList2 = ((Element)nodeList.item(i)).getElementsByTagName("entry")).getLength() < 3) continue;
            keyboardMacrosPreferences = new KeyboardMacrosPreferences();
            for (int j = 0; j < nodeList2.getLength(); ++j) {
                if (nodeList2.item(j).getNodeType() != 1) continue;
                element = (Element)nodeList2.item(j);
                if (element.getAttribute("key").equals(MACRO_NAME_KEY)) {
                    keyboardMacrosPreferences.setMacroName(element.getAttribute("value"));
                }
                if (element.getAttribute("key").equals(HOT_KEY_COMBINATION_KEY)) {
                    keyboardMacrosPreferences.setHotKeyCombination(Integer.parseInt(element.getAttribute("value")));
                }
                if (!element.getAttribute("key").equals(MACRO_SEQUENCE_KEY)) continue;
                keyboardMacrosPreferences.setMacroSequence(element.getAttribute("value"));
            }
            if (keyboardMacrosPreferences.getMacroName().length() <= 0 || keyboardMacrosPreferences.getMacroSequence().length() <= 0) continue;
            treeMap.put(keyboardMacrosPreferences.getMacroName(), keyboardMacrosPreferences);
        }
        return treeMap;
    }

    private class xmlFileFilter
    extends FileFilter {
        private xmlFileFilter() {
        }

        @Override
        public boolean accept(File file) {
            return file.getName().endsWith(".xml") || file.isDirectory();
        }

        @Override
        public String getDescription() {
            return "XML Files (*.xml)";
        }
    }
}

