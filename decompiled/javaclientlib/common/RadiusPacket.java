/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.common;

import javaclientlib.tr.RADIUS_ATTRIB_HEADER;
import javaclientlib.tr.RADIUS_ATTRIB_STRING;
import javaclientlib.tr.RADIUS_ATTRIB_VALUE;
import javaclientlib.tr.RADIUS_PACKET;
import javaclientlib.utils.MD5;

public class RadiusPacket {
    public boolean appendAttribute(RADIUS_PACKET rADIUS_PACKET, RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER) {
        if (this.validateAttribute(rADIUS_ATTRIB_HEADER) == 2) {
            return false;
        }
        if (rADIUS_ATTRIB_HEADER.getLengthAttribute() > 4096) {
            return false;
        }
        rADIUS_PACKET.appendAttributePacket(rADIUS_ATTRIB_HEADER);
        return true;
    }

    public boolean appendValueAttribute(RADIUS_PACKET rADIUS_PACKET, byte by, int n) {
        RADIUS_ATTRIB_VALUE rADIUS_ATTRIB_VALUE = new RADIUS_ATTRIB_VALUE();
        rADIUS_ATTRIB_VALUE.setType(by);
        rADIUS_ATTRIB_VALUE.setLengthAttribute(6);
        rADIUS_ATTRIB_VALUE.setValue(n);
        return this.appendAttribute(rADIUS_PACKET, rADIUS_ATTRIB_VALUE);
    }

    public boolean appendStringAttribute(RADIUS_PACKET rADIUS_PACKET, byte by, int n, byte[] byArray) {
        RADIUS_ATTRIB_STRING rADIUS_ATTRIB_STRING = new RADIUS_ATTRIB_STRING();
        if (n > 253) {
            return false;
        }
        rADIUS_ATTRIB_STRING.setType(by);
        rADIUS_ATTRIB_STRING.setLengthAttribute((byte)(2 + n));
        rADIUS_ATTRIB_STRING.setAttribute(byArray);
        return this.appendAttribute(rADIUS_PACKET, rADIUS_ATTRIB_STRING);
    }

    public boolean insertAttribute(RADIUS_PACKET rADIUS_PACKET, RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER, RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER2) {
        if (this.validateAttribute(rADIUS_ATTRIB_HEADER2) == 2) {
            return false;
        }
        if (rADIUS_ATTRIB_HEADER2.getLengthAttribute() > 4096) {
            return false;
        }
        int n = rADIUS_PACKET.findAttributePosition(0, rADIUS_ATTRIB_HEADER.getType());
        if (n == -1) {
            return false;
        }
        rADIUS_PACKET.insertAttributeData(n, rADIUS_ATTRIB_HEADER2);
        return true;
    }

    public boolean insertValueAttribute(RADIUS_PACKET rADIUS_PACKET, RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER, byte by, int n) {
        RADIUS_ATTRIB_VALUE rADIUS_ATTRIB_VALUE = new RADIUS_ATTRIB_VALUE();
        rADIUS_ATTRIB_VALUE.setType(by);
        rADIUS_ATTRIB_VALUE.setLengthAttribute(6);
        rADIUS_ATTRIB_VALUE.setValue(n);
        return this.insertAttribute(rADIUS_PACKET, rADIUS_ATTRIB_HEADER, rADIUS_ATTRIB_VALUE);
    }

    public boolean insertStringAttribute(RADIUS_PACKET rADIUS_PACKET, RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER, byte by, int n, byte[] byArray) {
        RADIUS_ATTRIB_STRING rADIUS_ATTRIB_STRING = new RADIUS_ATTRIB_STRING();
        if (n > 253) {
            return false;
        }
        rADIUS_ATTRIB_STRING.setType(by);
        rADIUS_ATTRIB_STRING.setLengthAttribute((byte)(2 + n));
        rADIUS_ATTRIB_STRING.setAttribute(byArray);
        return this.insertAttribute(rADIUS_PACKET, rADIUS_ATTRIB_HEADER, rADIUS_ATTRIB_STRING);
    }

    public RADIUS_ATTRIB_HEADER findAttribute(RADIUS_PACKET rADIUS_PACKET, RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER, byte by) {
        int n;
        if (rADIUS_ATTRIB_HEADER == null) {
            n = rADIUS_PACKET.findAttributePosition(0, by);
        } else {
            n = rADIUS_PACKET.findAttributePosition(0, rADIUS_ATTRIB_HEADER.getType());
            n = rADIUS_PACKET.findAttributePosition(n, by);
        }
        if (n == -1) {
            return null;
        }
        byte by2 = rADIUS_PACKET.getAttributeData(n + 1);
        String string = rADIUS_PACKET.getAttributeData(n + 2, by2 - 2);
        if (string.getClass().isAssignableFrom(Integer.TYPE)) {
            RADIUS_ATTRIB_VALUE rADIUS_ATTRIB_VALUE = new RADIUS_ATTRIB_VALUE();
            rADIUS_ATTRIB_VALUE.setType(rADIUS_PACKET.getAttributeData(n));
            rADIUS_ATTRIB_VALUE.setLengthAttribute((int)by2);
            rADIUS_ATTRIB_VALUE.setValue(Integer.parseInt(string));
            return rADIUS_ATTRIB_VALUE;
        }
        RADIUS_ATTRIB_STRING rADIUS_ATTRIB_STRING = new RADIUS_ATTRIB_STRING();
        rADIUS_ATTRIB_STRING.setType(rADIUS_PACKET.getAttributeData(n));
        rADIUS_ATTRIB_STRING.setLengthAttribute((int)by2);
        rADIUS_ATTRIB_STRING.setAttribute(string);
        return rADIUS_ATTRIB_STRING;
    }

    public boolean removeAttribute(RADIUS_PACKET rADIUS_PACKET, RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER) {
        int n = rADIUS_PACKET.findAttributePosition(0, rADIUS_ATTRIB_HEADER.getType());
        if (n == -1) {
            return false;
        }
        rADIUS_PACKET.removeAttributeData(n, rADIUS_ATTRIB_HEADER.getLengthAttribute());
        return true;
    }

    private int validateAttribute(RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER) {
        if (rADIUS_ATTRIB_HEADER.getLengthAttribute() < 2) {
            return 2;
        }
        switch (rADIUS_ATTRIB_HEADER.getType()) {
            case 1: 
            case 11: 
            case 18: 
            case 19: 
            case 20: 
            case 22: 
            case 24: 
            case 25: 
            case 30: 
            case 31: 
            case 32: 
            case 33: 
            case 34: 
            case 35: 
            case 39: 
            case 44: 
            case 50: 
            case 63: {
                if (rADIUS_ATTRIB_HEADER.getLengthAttribute() >= 2) break;
                return 2;
            }
        }
        switch (rADIUS_ATTRIB_HEADER.getType()) {
            case 2: {
                if (rADIUS_ATTRIB_HEADER.getLengthAttribute() >= 18 && rADIUS_ATTRIB_HEADER.getLengthAttribute() <= 130) break;
                return 2;
            }
            case 3: {
                if (rADIUS_ATTRIB_HEADER.getLengthAttribute() == 19) break;
                return 2;
            }
            case 26: {
                if (rADIUS_ATTRIB_HEADER.getLengthAttribute() >= 7) break;
                return 2;
            }
            case 36: {
                if (rADIUS_ATTRIB_HEADER.getLengthAttribute() == 34) break;
                return 2;
            }
            case 60: {
                if (rADIUS_ATTRIB_HEADER.getLengthAttribute() >= 7) break;
                return 2;
            }
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 23: 
            case 27: 
            case 28: 
            case 29: 
            case 37: 
            case 38: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 45: 
            case 46: 
            case 47: 
            case 48: 
            case 49: 
            case 51: 
            case 61: 
            case 62: {
                if (rADIUS_ATTRIB_HEADER.getLengthAttribute() == 6) break;
                return 2;
            }
            default: {
                return 1;
            }
        }
        return 0;
    }

    public int validatePacket(RADIUS_PACKET rADIUS_PACKET) {
        int n = 20;
        int n2 = 0;
        switch (rADIUS_PACKET.getCode()) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: {
                break;
            }
            default: {
                return 3;
            }
        }
        if (rADIUS_PACKET.getPktLength() > 4096 || rADIUS_PACKET.getPktLength() < 20) {
            return 3;
        }
        RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER = new RADIUS_ATTRIB_HEADER();
        int n3 = rADIUS_PACKET.getPktLength();
        for (int i = 0; i < n3; i += rADIUS_ATTRIB_HEADER.getLengthAttribute()) {
            rADIUS_ATTRIB_HEADER.setType(rADIUS_PACKET.getAttributeData(i));
            rADIUS_ATTRIB_HEADER.setLengthAttribute(rADIUS_PACKET.getAttributeData(i + 1));
            if (rADIUS_ATTRIB_HEADER.getLengthAttribute() + n <= rADIUS_PACKET.getPktLength()) continue;
            return 3;
        }
        return n2;
    }

    public int deleteAttributeType(RADIUS_PACKET rADIUS_PACKET, byte by) {
        int n = 0;
        RADIUS_ATTRIB_HEADER rADIUS_ATTRIB_HEADER = new RADIUS_ATTRIB_HEADER();
        int n2 = rADIUS_PACKET.findAttributePosition(0, by);
        if (n2 == -1) {
            return 0;
        }
        do {
            byte by2 = rADIUS_PACKET.getAttributeData(n2 + 1);
            rADIUS_PACKET.removeAttributeData(n2, by2);
            ++n;
        } while ((n2 = rADIUS_PACKET.findAttributePosition(0, by)) != -1);
        return n;
    }

    public int deleteAllButStateAttributes(RADIUS_PACKET rADIUS_PACKET) {
        byte by;
        int n = 2;
        int n2 = 0;
        StringBuffer stringBuffer = new StringBuffer();
        int n3 = rADIUS_PACKET.findAttributePosition(0, (byte)24);
        if (n3 != -1) {
            by = rADIUS_PACKET.getAttributeData(n3 + 1);
            stringBuffer.append(rADIUS_PACKET.getAttributeData(n3, by));
            n2 += by;
        }
        if ((n3 = rADIUS_PACKET.findAttributePosition(0, (byte)33)) != -1) {
            by = rADIUS_PACKET.getAttributeData(n3 + 1);
            stringBuffer.append(rADIUS_PACKET.getAttributeData(n3, by));
            n2 += by;
        }
        rADIUS_PACKET.clearAttributeData();
        rADIUS_PACKET.setAttributeData(stringBuffer.toString());
        rADIUS_PACKET.setPktLength(n2);
        return n;
    }

    public void clearPacket(RADIUS_PACKET rADIUS_PACKET) {
        rADIUS_PACKET.clearPacket();
        rADIUS_PACKET.setPktLength(20);
    }

    public void computeCHAPResponse(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) throws Exception {
        MD5 mD5 = new MD5();
        mD5.update(byArray);
        mD5.update(byArray2);
        mD5.update(byArray3);
        byte[] byArray5 = mD5.encode();
        System.arraycopy(byArray5, 0, byArray4, 1, 16);
    }

    public boolean appendPasswordAttribute(RADIUS_PACKET rADIUS_PACKET, int n, byte[] byArray, byte[] byArray2, int n2) throws Exception {
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        byte[] byArray3 = new byte[128];
        n3 = n;
        n4 = n3 + 15 & 0xFFFFFFF0;
        if (n4 == 0) {
            n4 = 16;
        }
        if (n3 > 128) {
            n3 = 128;
        }
        for (n5 = 0; n5 < n4; ++n5) {
            byArray3[n5] = 0;
        }
        byArray3 = byArray;
        if (byArray2 != null) {
            this.encryptPassword(rADIUS_PACKET, byArray3, n4, byArray2, n2);
        }
        return this.appendStringAttribute(rADIUS_PACKET, (byte)2, n4, byArray3);
    }

    private void encryptPassword(RADIUS_PACKET rADIUS_PACKET, byte[] byArray, int n, byte[] byArray2, int n2) throws Exception {
        byte[] byArray3 = new byte[16];
        byte[] byArray4 = new byte[16];
        MD5 mD5 = new MD5();
        byArray3 = rADIUS_PACKET.getAuthenticator();
        int n3 = 0;
        while (n3 < n) {
            mD5.update(byArray2, n2);
            mD5.update(byArray3, 16);
            byArray3 = rADIUS_PACKET.getAuthenticator(n3, 16);
            byArray4 = mD5.encode();
            int n4 = 0;
            while (n4 < 16) {
                byArray[n3] = (byte)(byArray[n3] ^ byArray4[n4]);
                ++n4;
                ++n3;
            }
        }
    }
}

