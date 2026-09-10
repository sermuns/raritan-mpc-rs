/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext.macro;

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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import nn.pp.ext.macro.IMacro;
import nn.pp.ext.macro.IMacroParser;
import nn.pp.ext.macro.Macro;
import nn.pp.ext.macro.MacroException;
import nn.pp.logging.RemoteConsoleLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class KeyboardMacroParserImpl
implements IMacroParser {
    private static DTDResolverAndErrorHandler ENTITY_RESOLVER_ERROR_HANDLER = new DTDResolverAndErrorHandler();
    private static final String FILE_NAME = System.getProperty("user.home") != null ? System.getProperty("user.home") + System.getProperty("file.separator") + "KeyboardMacros.xml" : "KeyboardMacros.xml";
    public static final String ROOT_NODE = "/KeyboardMacros";
    private static final String MACRO_NAME_KEY = "name";
    private static final String HOT_KEY_COMBINATION_KEY = "hotKey";
    private static final String MACRO_SEQUENCE_KEY = "macroSequence";
    private static final String CONN_SCRIPT_EDIT_SEQUENCE_KEY = "EditSequence";

    KeyboardMacroParserImpl() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static File getOrCreateXmlFile(String string) throws IOException {
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
                bufferedWriter.write("<root type=\"user\">");
                bufferedWriter.write("<map /><node name=\"KeyboardMacros\"><map />");
                bufferedWriter.write("</node></root></preferences>");
                bufferedWriter.flush();
            }
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

    private List<IMacro> getMacroList(String string) throws MacroException {
        InputStream inputStream = null;
        List<IMacro> list = Collections.emptyList();
        try {
            try {
                inputStream = new BufferedInputStream(new FileInputStream(KeyboardMacroParserImpl.getOrCreateXmlFile(string)));
                Preferences.importPreferences(inputStream);
            }
            catch (FileNotFoundException fileNotFoundException) {
                System.out.println("Unable to load keyboard macrof from file " + string);
                RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "Exception trying to import Keyboard Macro.", fileNotFoundException);
            }
            Preferences preferences = Preferences.userRoot().node(ROOT_NODE);
            String[] stringArray = null;
            stringArray = preferences.childrenNames();
            list = new ArrayList();
            for (int i = 0; i < stringArray.length; ++i) {
                Preferences preferences2 = preferences.node(stringArray[i]);
                Macro macro = new Macro(preferences2.get(MACRO_NAME_KEY, ""), preferences2.get(MACRO_SEQUENCE_KEY, ""), preferences2.getInt(HOT_KEY_COMBINATION_KEY, -1));
                list.add(macro);
            }
        }
        catch (BackingStoreException backingStoreException) {
            throw new MacroException("Unable to get macros", backingStoreException);
        }
        catch (InvalidPreferencesFormatException invalidPreferencesFormatException) {
            throw new MacroException("Unable to get macros", invalidPreferencesFormatException);
        }
        catch (IOException iOException) {
            throw new MacroException("Unable to get macros", iOException);
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
        return list;
    }

    @Override
    public IMacro getKeyboardMacro(String string) {
        String string2;
        Preferences preferences;
        Preferences preferences2;
        Macro macro = null;
        if (string != null && !"".equals(string) && (preferences2 = (preferences = Preferences.userRoot().node(ROOT_NODE)).node(string)) != null && (string2 = preferences2.get(MACRO_NAME_KEY, null)) != null) {
            macro = new Macro(string, preferences2.get(MACRO_SEQUENCE_KEY, ""), preferences2.getInt(HOT_KEY_COMBINATION_KEY, -1));
        }
        return macro;
    }

    @Override
    public List<IMacro> getKeyboardMacros() throws MacroException {
        List<IMacro> list = this.getMacroList(FILE_NAME);
        return list;
    }

    @Override
    public List<IMacro> getKeyboardMacros(String string) throws MacroException {
        List<IMacro> list = this.getMacroList(string);
        return list;
    }

    @Override
    public void createKeyboardMacro(IMacro iMacro) throws MacroException {
        this.updateKeyboardMacro(iMacro, null, true);
    }

    @Override
    public void createKeyboardMacro(IMacro iMacro, boolean bl) throws MacroException {
        this.updateKeyboardMacro(iMacro, null, bl);
    }

    @Override
    public void deleteKeyboardMacro(IMacro iMacro) throws MacroException {
        this.deleteKeyboardMacro(iMacro, true);
    }

    @Override
    public void deleteKeyboardMacro(IMacro iMacro, boolean bl) throws MacroException {
        Preferences preferences = Preferences.userRoot().node(ROOT_NODE);
        try {
            preferences.node(iMacro.getName()).removeNode();
            if (bl) {
                this.saveKeyboardMacro();
            }
        }
        catch (BackingStoreException backingStoreException) {
            throw new MacroException("Unable to delete macro", backingStoreException);
        }
    }

    @Override
    public void saveKeyboardMacro() throws MacroException {
        this.saveKeyboardMacro(FILE_NAME);
    }

    @Override
    public void saveKeyboardMacro(String string) throws MacroException {
        Preferences preferences = Preferences.userRoot().node(ROOT_NODE);
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(KeyboardMacroParserImpl.getOrCreateXmlFile(string)));
            preferences.exportSubtree(outputStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
            throw new MacroException("Unable to save macro", fileNotFoundException);
        }
        catch (IOException iOException) {
            throw new MacroException("Unable to save macro", iOException);
        }
        catch (BackingStoreException backingStoreException) {
            throw new MacroException("Unable to save macro", backingStoreException);
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

    @Override
    public void updateKeyboardMacro(IMacro iMacro, String string) throws MacroException {
        this.updateKeyboardMacro(iMacro, string, true);
    }

    @Override
    public void updateKeyboardMacro(IMacro iMacro, String string, boolean bl) throws MacroException {
        Preferences preferences = Preferences.userRoot().node(ROOT_NODE);
        OutputStream outputStream = null;
        try {
            if (string != null) {
                preferences.node(string).removeNode();
            }
            Preferences preferences2 = preferences.node(iMacro.getName());
            preferences2.put(MACRO_NAME_KEY, iMacro.getName());
            preferences2.putInt(HOT_KEY_COMBINATION_KEY, iMacro.getHotKey());
            preferences2.put(MACRO_SEQUENCE_KEY, iMacro.getSequence());
            outputStream = new BufferedOutputStream(new FileOutputStream(KeyboardMacroParserImpl.getOrCreateXmlFile(FILE_NAME)));
            preferences.exportSubtree(outputStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
            throw new MacroException("Unable to update macro", fileNotFoundException);
        }
        catch (IOException iOException) {
            throw new MacroException("Unable to update macro", iOException);
        }
        catch (BackingStoreException backingStoreException) {
            throw new MacroException("Unable to update macro", backingStoreException);
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

    private Element getXmlElement(File file, String string) throws IOException, SAXException {
        Document document = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(true);
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException parserConfigurationException) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.SEVERE, "Parser Configuration Exception attempting to parse out the Keyboard Macros from the file: " + file.getAbsolutePath(), parserConfigurationException);
            return null;
        }
        documentBuilder.setEntityResolver(ENTITY_RESOLVER_ERROR_HANDLER);
        documentBuilder.setErrorHandler(ENTITY_RESOLVER_ERROR_HANDLER);
        document = documentBuilder.parse(file);
        Element element = document.getDocumentElement();
        NodeList nodeList = element.getElementsByTagName("node");
        element = null;
        Node node = null;
        for (int i = 0; i < nodeList.getLength(); ++i) {
            node = nodeList.item(i);
            if (node.getNodeType() != 1 || !((Element)node).getAttribute(MACRO_NAME_KEY).equals(string)) continue;
            element = (Element)node;
        }
        return element;
    }

    private Element getMacrosElement(File file) throws IOException, SAXException {
        return this.getXmlElement(file, "KeyboardMacros");
    }

    private Element getConnectionScriptsElement(File file) throws IOException, SAXException {
        return this.getXmlElement(file, "ConnectionScripts");
    }

    @Override
    public SortedMap<String, IMacro> getValidMacros(File file) throws IOException, SAXException {
        TreeMap<String, IMacro> treeMap = new TreeMap<String, IMacro>();
        Macro macro = null;
        Element element = this.getMacrosElement(file);
        if (element == null) {
            element = this.getConnectionScriptsElement(file);
        }
        if (element == null) {
            return treeMap;
        }
        NodeList nodeList = element.getElementsByTagName("node");
        NodeList nodeList2 = null;
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (nodeList.item(i).getNodeType() != 1 || (nodeList2 = ((Element)nodeList.item(i)).getElementsByTagName("entry")).getLength() < 3) continue;
            macro = new Macro();
            for (int j = 0; j < nodeList2.getLength(); ++j) {
                if (nodeList2.item(j).getNodeType() != 1) continue;
                element = (Element)nodeList2.item(j);
                if (element.getAttribute("key").equals(MACRO_NAME_KEY)) {
                    macro.setName(element.getAttribute("value"));
                }
                if (element.getAttribute("key").equals(HOT_KEY_COMBINATION_KEY)) {
                    macro.setHotKey(Integer.parseInt(element.getAttribute("value")));
                }
                if (element.getAttribute("key").equals(MACRO_SEQUENCE_KEY)) {
                    macro.setSequence(element.getAttribute("value"));
                }
                if (!element.getAttribute("key").equals(CONN_SCRIPT_EDIT_SEQUENCE_KEY)) continue;
                macro.setSequence(this.convertConnectionScriptSequence(element.getAttribute("value")));
            }
            if (macro.getName().length() <= 0 || macro.getSequence().length() <= 0) continue;
            treeMap.put(macro.getName(), macro);
        }
        return treeMap;
    }

    private String convertConnectionScriptSequence(String string) {
        String[] stringArray = string.split(";");
        String string2 = "";
        for (int i = 0; i < stringArray.length; ++i) {
            string2 = string2 + stringArray[i].substring(0, 1) + " " + stringArray[i].substring(1);
            if (i >= stringArray.length - 1) continue;
            string2 = string2 + "&&";
        }
        return string2;
    }

    @Override
    public String getBackupFilename() {
        return FILE_NAME + ".bkp";
    }

    private static class DTDResolverAndErrorHandler
    implements EntityResolver,
    ErrorHandler {
        private static final String PREFS_DTD = new String("<!ELEMENT preferences (root)><!ATTLIST preferences EXTERNAL_XML_VERSION CDATA \"0.0\" ><!ELEMENT root (map, node*) ><!ATTLIST root type (system|user) #REQUIRED ><!ELEMENT node (map, node*) ><!ATTLIST node name CDATA #REQUIRED ><!ELEMENT map (entry*) ><!ELEMENT entry EMPTY ><!ATTLIST entry key   CDATA #REQUIRED value CDATA #REQUIRED >");

        private DTDResolverAndErrorHandler() {
        }

        @Override
        public InputSource resolveEntity(String string, String string2) throws SAXException, IOException {
            if ("http://java.sun.com/dtd/preferences.dtd".equals(string2)) {
                return new InputSource(new StringReader(PREFS_DTD));
            }
            return null;
        }

        @Override
        public void error(SAXParseException sAXParseException) throws SAXException {
            throw sAXParseException;
        }

        @Override
        public void fatalError(SAXParseException sAXParseException) throws SAXException {
            throw sAXParseException;
        }

        @Override
        public void warning(SAXParseException sAXParseException) throws SAXException {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "Warning Parsing the XML KeyboardMacro file", sAXParseException);
        }
    }
}

