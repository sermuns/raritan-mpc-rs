/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import javaclientlib.tr.Constants;
import javaclientlib.tr.RFP;
import javaclientlib.tr.RFP_FILE;
import javaclientlib.utils.RRCLogger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RFPParser {
    private int iCurFile;
    private RFP_FILE curFile;
    private RFP objRfp;
    private int iHeaderOffset;
    private int iHeaderLength;
    private int iSignatureOffset;
    private int iSignatureLength;
    protected int iDataOffset = 0;
    public static final int RFP_MAX_HEADER_SIZE = 65536;
    public static final int RFP_MAX_SIGNATURE_SIZE = 300;
    public static final int RFP_ERROR_MEMORY = 1;
    public static final int RFP_ERROR_TOO_MANY_FILES = 2;
    public static final int RFP_ERROR_CANNOT_READ_RSA_FILE = 3;
    public static final int RFP_ERROR_RSA_BAD = 4;
    public static final int RFP_ERROR_BAD_SIGNATURE = 5;
    public static final int RFP_ERROR_CANNOT_READ_FILE = 6;
    public static final int RFP_FILE_META_FILENAME = 1;

    public int parseHeader(String string) {
        int n = this.parse(string, string.length());
        return n;
    }

    public int parseFile(RandomAccessFile randomAccessFile) {
        int n = 0;
        byte[] byArray = new byte[65836];
        try {
            int n2 = randomAccessFile.read(byArray, 0, 65836);
            if (n2 <= 0) {
                n = 6;
            }
            n = this.parseHeaderAndSignature(byArray, n2);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            n = 6;
        }
        return n;
    }

    public int parseHeaderAndSignature(byte[] byArray, int n) {
        int n2 = 0;
        byte[] byArray2 = byArray;
        String string = new String(byArray2);
        String string2 = "</" + Constants.COMPANY_NAME_NOSPACE + "_File_Package>";
        int n3 = string.indexOf(string2);
        if (n3 == -1) {
            return n2;
        }
        String string3 = string.substring(0, n3 + string2.length());
        this.iHeaderLength = this.iSignatureOffset = string3.length();
        n2 = this.parseHeader(new String(string3));
        if (this.objRfp.getIsSigned() != 0) {
            this.iSignatureLength = this.objRfp.getRfpFile(0).getSignature().length;
            this.iDataOffset = this.iHeaderLength + this.iSignatureLength;
        } else {
            this.iDataOffset = this.iHeaderLength;
        }
        return n2;
    }

    public int verifyHeaderSignature(String string, String string2, String string3) {
        int n = 0;
        ByteArrayInputStream byteArrayInputStream = null;
        if (this.objRfp.getIsSigned() == 0) {
            return 0;
        }
        int n2 = string.length() + 1;
        byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        n = this.verifySignature(byteArrayInputStream, string2.getBytes(), string3);
        byteArrayInputStream = null;
        return n;
    }

    public int verifyFileSignatures(String string) {
        int n = 0;
        for (int i = 0; i < this.objRfp.getFileCount() && (n = this.verifyFileSignature(this.objRfp.getRfpFile(i), null, string)) == 0; ++i) {
        }
        return n;
    }

    public int verifyFileSignature(RFP_FILE rFP_FILE, String string, String string2) {
        FileInputStream fileInputStream;
        int n = 0;
        if (rFP_FILE.getIsSigned() == 0) {
            return 0;
        }
        if (string == null) {
            string = new String(rFP_FILE.getFileName());
        }
        try {
            fileInputStream = new FileInputStream(string);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
            return 6;
        }
        n = this.verifySignature(fileInputStream, rFP_FILE.getSignature(), string2);
        return n;
    }

    public int verifyFileSignature_SSLOBJECT(RFP_FILE rFP_FILE, InputStream inputStream, String string) {
        int n = 0;
        if (rFP_FILE.getIsSigned() == 0) {
            return 0;
        }
        n = this.verifySignature(inputStream, rFP_FILE.getSignature(), string);
        return n;
    }

    public RFP_FILE enumFile(int n) {
        if (n < this.objRfp.getFileCount()) {
            return this.objRfp.getRfpFile(n);
        }
        return null;
    }

    public RFP getHeader() {
        return this.objRfp;
    }

    public int verifySignature(InputStream inputStream, byte[] byArray, String string) {
        byte[] byArray2 = null;
        X509EncodedKeySpec x509EncodedKeySpec = null;
        PublicKey publicKey = null;
        boolean bl = false;
        try {
            try {
                byArray2 = new byte[inputStream.available()];
                inputStream.read(byArray2);
                inputStream.close();
            }
            catch (Exception exception) {
                return 3;
            }
            x509EncodedKeySpec = new X509EncodedKeySpec(byArray2);
            KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
            publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
            signature.initVerify(publicKey);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] byArray3 = new byte[1024];
            while (bufferedInputStream.available() != 0) {
                int n = bufferedInputStream.read(byArray3);
                signature.update(byArray3, 0, n);
            }
            bufferedInputStream.close();
            bl = signature.verify(byArray);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        if (!bl) {
            return 5;
        }
        return 0;
    }

    public int parse(String string, int n) {
        this.log("parsing file *************" + n);
        try {
            this.objRfp = this.convertXML(string);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return 0;
    }

    public RFP convertXML(String string) {
        RFP rFP = null;
        RFP_FILE[] rFP_FILEArray = new RFP_FILE[64];
        int n = 0;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new ByteArrayInputStream(string.getBytes()));
            NodeList nodeList = document.getChildNodes();
            Node node = nodeList.item(0);
            NodeList nodeList2 = node.getChildNodes();
            for (int i = 0; i < nodeList2.getLength(); ++i) {
                Node node2;
                if (nodeList2.item(i).getNodeType() != 1 || (node2 = nodeList2.item(i)) == null) continue;
                if (rFP == null) {
                    rFP = new RFP();
                }
                if (node2.getNodeName().equals("Title")) {
                    rFP.setTitle(this.getNodeTextValue(node2));
                    continue;
                }
                if (node2.getNodeName().equals("Description")) {
                    rFP.setDescription(this.getNodeTextValue(node2));
                    continue;
                }
                if (node2.getNodeName().equals("Publisher")) {
                    rFP.setPublisher(this.getNodeTextValue(node2));
                    continue;
                }
                if (node2.getNodeName().equals("Copyright")) {
                    rFP.setCopyRight(this.getNodeTextValue(node2));
                    continue;
                }
                if (node2.getNodeName().equals("Signed")) {
                    rFP.setIsSigned(1);
                    continue;
                }
                if (node2.getNodeName().equals("RFP_File")) {
                    if (rFP_FILEArray == null) continue;
                    rFP_FILEArray[n] = this.getRFPFile(node2);
                    ++n;
                    continue;
                }
                if (!node2.getNodeName().equals("RP")) continue;
                rFP.addRPID(node2.getAttributes().getNamedItem("rpid").getNodeValue());
            }
            rFP.setFileCount(n);
            rFP.setRfpFile(rFP_FILEArray);
        }
        catch (Exception exception) {
            RRCLogger.logException(exception);
        }
        return rFP;
    }

    private byte[] getNodeTextValue(Node node) {
        if (node.getFirstChild() != null && node.getFirstChild().getNodeValue() != null) {
            return node.getFirstChild().getNodeValue().getBytes();
        }
        return null;
    }

    private int getNodeIntValue(Node node) {
        if (node.getFirstChild() != null && node.getFirstChild().getNodeValue() != null) {
            String string = node.getFirstChild().getNodeValue();
            return Integer.parseInt(string);
        }
        return 0;
    }

    public RFP_FILE getRFPFile(Node node) {
        RFP_FILE rFP_FILE = null;
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node2;
            if (nodeList.item(i).getNodeType() != 1) continue;
            if (rFP_FILE == null) {
                rFP_FILE = new RFP_FILE();
            }
            if ((node2 = nodeList.item(i)) == null) continue;
            if (node2.getNodeName().equals("Length")) {
                rFP_FILE.setLength(this.getNodeIntValue(node2));
                continue;
            }
            if (node2.getNodeName().equals("Signature")) {
                rFP_FILE.setSignature(this.getNodeTextValue(node2));
                continue;
            }
            if (node2.getNodeName().equals("Encrypted")) {
                rFP_FILE.setIsEncrypted(this.getNodeIntValue(node2));
                continue;
            }
            if (node2.getNodeName().equals("FileName")) {
                rFP_FILE.setFileName(this.getNodeTextValue(node2));
                continue;
            }
            if (node2.getNodeName().equals("Model")) {
                rFP_FILE.setModel(this.getNodeTextValue(node2));
                continue;
            }
            if (node2.getNodeName().equals("Version")) {
                rFP_FILE.setVersion(this.getNodeTextValue(node2));
                continue;
            }
            if (node2.getNodeName().equals("VersionMin")) {
                rFP_FILE.setVersionMin(this.getNodeTextValue(node2));
                continue;
            }
            if (node2.getNodeName().equals("VersionMax")) {
                rFP_FILE.setVersionMax(this.getNodeTextValue(node2));
                continue;
            }
            if (node2.getNodeName().equals("Script")) {
                rFP_FILE.setScript(this.getNodeTextValue(node2));
                continue;
            }
            if (!node2.getNodeName().equals("MetaFileName")) continue;
            rFP_FILE.setFileName(this.getNodeTextValue(node2));
            rFP_FILE.setMetaFileName(1);
        }
        return rFP_FILE;
    }

    private void log(String string) {
        if (RRCLogger.logEnabled) {
            RRCLogger.log(300, 64, "RFPParser::" + string);
        }
    }
}

