/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import java.io.DataInput;
import java.io.DataOutput;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import javaclientlib.tr.TRDATASTRUCTURE;

public class TRDATASTRUCTURE_DEF {
    protected static final int debug = 0;
    protected static final int TYPE_Unknown = 0;
    protected static final int TYPE_BOOLEAN = 1;
    protected static final int TYPE_BYTE = 2;
    protected static final int TYPE_CHAR = 3;
    protected static final int TYPE_SHORT = 4;
    protected static final int TYPE_INT = 5;
    protected static final int TYPE_LONG = 6;
    protected static final int TYPE_FLOAT = 7;
    protected static final int TYPE_DOUBLE = 8;
    protected static final int TYPE_STRUCT = 9;
    protected static final int TYPE_ARRAY = 16;
    protected Class structType;
    protected Property[] properties;

    public TRDATASTRUCTURE_DEF(Class clazz, String[] stringArray, Class[] classArray, int[] nArray) {
        try {
            TRDATASTRUCTURE_DEF.log("Init( " + clazz.getName() + " )");
            this.properties = new Property[stringArray.length];
            this.structType = clazz;
            Method[] methodArray = this.structType.getDeclaredMethods();
            for (int i = 0; i < this.properties.length; ++i) {
                String string = "get" + stringArray[i];
                String string2 = null;
                if (Boolean.TYPE.equals(classArray[i])) {
                    string2 = "is" + stringArray[i];
                }
                String string3 = "set" + stringArray[i];
                Method method = null;
                Method method2 = null;
                for (int j = 0; j < methodArray.length; ++j) {
                    Method method3 = methodArray[j];
                    if (method == null && string.equalsIgnoreCase(method3.getName())) {
                        if (method3.getParameterTypes().length != 0 || !TRDATASTRUCTURE_DEF.isCompatibleTypes(nArray[i] != 0, classArray[i], method3.getReturnType())) continue;
                        method = method3;
                        if (method2 == null) continue;
                        break;
                    }
                    if (method == null && string2 != null && string2.equalsIgnoreCase(method3.getName())) {
                        if (method3.getParameterTypes().length != 0 || !TRDATASTRUCTURE_DEF.isCompatibleTypes(nArray[i] != 0, classArray[i], method3.getReturnType())) continue;
                        method = method3;
                        if (method2 == null) continue;
                        break;
                    }
                    if (method2 != null || !string3.equalsIgnoreCase(method3.getName()) || method3.getParameterTypes().length != 1 || !TRDATASTRUCTURE_DEF.isCompatibleTypes(nArray[i] != 0, classArray[i], method3.getParameterTypes()[0])) continue;
                    method2 = method3;
                    if (method != null) break;
                }
                if (method == null || method2 == null) {
                    System.err.println("TRDATASTRUCTURE(" + this.structType.getName() + ")." + stringArray[i] + "[" + nArray[i] + "]" + " Fatal Error! getter=" + method + " setter=" + method2);
                    throw new NullPointerException();
                }
                this.properties[i] = new Property(stringArray[i], classArray[i], nArray[i], method, method2);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    protected static int getTypeCode(Class clazz) {
        int n = 0;
        if (clazz.isArray()) {
            n = 16;
            clazz = clazz.getComponentType();
        }
        if (Boolean.TYPE.equals(clazz)) {
            return n | 1;
        }
        if (Byte.TYPE.equals(clazz)) {
            return n | 2;
        }
        if (Character.TYPE.equals(clazz)) {
            return n | 3;
        }
        if (Short.TYPE.equals(clazz)) {
            return n | 4;
        }
        if (Integer.TYPE.equals(clazz)) {
            return n | 5;
        }
        if (Long.TYPE.equals(clazz)) {
            return n | 6;
        }
        if (Float.TYPE.equals(clazz)) {
            return n | 7;
        }
        if (Double.TYPE.equals(clazz)) {
            return n | 8;
        }
        if (TRDATASTRUCTURE.class.isAssignableFrom(clazz)) {
            return n | 9;
        }
        return 0;
    }

    protected static boolean isCompatibleTypes(boolean bl, Class clazz, Class clazz2) {
        int n;
        int n2 = TRDATASTRUCTURE_DEF.getTypeCode(clazz);
        if (n2 == 0) {
            return false;
        }
        if (bl) {
            n2 |= 0x10;
        }
        if (n2 != (n = TRDATASTRUCTURE_DEF.getTypeCode(clazz2))) {
            return false;
        }
        if (n2 == 9) {
            return clazz.equals(clazz2);
        }
        if (n == 25) {
            clazz2 = clazz2.getComponentType();
            return clazz.equals(clazz2);
        }
        return true;
    }

    public int read(TRDATASTRUCTURE tRDATASTRUCTURE, DataInput dataInput) throws Exception {
        int n = 0;
        Object[] objectArray = new Object[1];
        TRDATASTRUCTURE tRDATASTRUCTURE2 = null;
        for (int i = 0; i < this.properties.length; ++i) {
            Property property = this.properties[i];
            int n2 = 0;
            if ((property.typeCode & 0x10) != 0) {
                objectArray[0] = null;
                if (property.len > 0) {
                    n2 = property.len;
                } else {
                    objectArray[0] = property.getter.invoke((Object)tRDATASTRUCTURE, null);
                    n2 = Array.getLength(objectArray[0]);
                }
            }
            switch (property.typeCode) {
                case 1: {
                    objectArray[0] = new Boolean(dataInput.readBoolean());
                    ++n;
                    break;
                }
                case 2: {
                    objectArray[0] = new Byte(dataInput.readByte());
                    ++n;
                    break;
                }
                case 3: {
                    objectArray[0] = new Character(dataInput.readChar());
                    n += 2;
                    break;
                }
                case 4: {
                    objectArray[0] = new Short(dataInput.readShort());
                    n += 2;
                    break;
                }
                case 5: {
                    objectArray[0] = new Integer(dataInput.readInt());
                    n += 4;
                    break;
                }
                case 6: {
                    objectArray[0] = new Long(dataInput.readLong());
                    n += 8;
                    break;
                }
                case 7: {
                    objectArray[0] = new Float(dataInput.readFloat());
                    n += 4;
                    break;
                }
                case 8: {
                    objectArray[0] = new Double(dataInput.readDouble());
                    n += 8;
                    break;
                }
                case 9: {
                    tRDATASTRUCTURE2 = (TRDATASTRUCTURE)property.type.newInstance();
                    n += tRDATASTRUCTURE2.getDefinition().read(tRDATASTRUCTURE2, dataInput);
                    TRDATASTRUCTURE_DEF.log("read_nested( " + property.type.getName() + " )");
                    objectArray[0] = tRDATASTRUCTURE2;
                    break;
                }
                case 17: {
                    int n3;
                    if (objectArray[0] == null) {
                        objectArray[0] = new boolean[n2];
                    }
                    for (n3 = 0; n3 < n2; ++n3) {
                        ((boolean[])objectArray[0])[n3] = dataInput.readBoolean();
                    }
                    n += n2;
                    break;
                }
                case 18: {
                    int n3;
                    if (objectArray[0] == null) {
                        objectArray[0] = new byte[n2];
                    }
                    for (n3 = 0; n3 < n2; ++n3) {
                        ((byte[])objectArray[0])[n3] = dataInput.readByte();
                    }
                    n += n2;
                    break;
                }
                case 19: {
                    int n3;
                    if (objectArray[0] == null) {
                        objectArray[0] = new char[n2];
                    }
                    for (n3 = 0; n3 < n2; ++n3) {
                        ((char[])objectArray[0])[n3] = dataInput.readChar();
                    }
                    n += n2 * 2;
                    break;
                }
                case 20: {
                    int n3;
                    if (objectArray[0] == null) {
                        objectArray[0] = new short[n2];
                    }
                    for (n3 = 0; n3 < n2; ++n3) {
                        ((short[])objectArray[0])[n3] = dataInput.readShort();
                    }
                    n += n2 * 2;
                    break;
                }
                case 21: {
                    int n3;
                    if (objectArray[0] == null) {
                        objectArray[0] = new int[n2];
                    }
                    for (n3 = 0; n3 < n2; ++n3) {
                        ((int[])objectArray[0])[n3] = dataInput.readInt();
                    }
                    n += n2 * 4;
                    break;
                }
                case 22: {
                    int n3;
                    if (objectArray[0] == null) {
                        objectArray[0] = new long[n2];
                    }
                    for (n3 = 0; n3 < n2; ++n3) {
                        ((long[])objectArray[0])[n3] = dataInput.readLong();
                    }
                    n += n2 * 8;
                    break;
                }
                case 23: {
                    int n3;
                    if (objectArray[0] == null) {
                        objectArray[0] = new float[n2];
                    }
                    for (n3 = 0; n3 < n2; ++n3) {
                        ((float[])objectArray[0])[n3] = dataInput.readFloat();
                    }
                    n += n2 * 4;
                    break;
                }
                case 24: {
                    int n3;
                    if (objectArray[0] == null) {
                        objectArray[0] = new double[n2];
                    }
                    for (n3 = 0; n3 < n2; ++n3) {
                        ((double[])objectArray[0])[n3] = dataInput.readDouble();
                    }
                    n += n2 * 8;
                    break;
                }
                case 25: {
                    int n3;
                    TRDATASTRUCTURE_DEF.log("read nested array begin!");
                    if (objectArray[0] == null) {
                        objectArray[0] = Array.newInstance(property.type, n2);
                    }
                    for (n3 = 0; n3 < n2; ++n3) {
                        tRDATASTRUCTURE2 = (TRDATASTRUCTURE)property.type.newInstance();
                        n += tRDATASTRUCTURE2.getDefinition().read(tRDATASTRUCTURE2, dataInput);
                        Array.set(objectArray[0], n3, tRDATASTRUCTURE2);
                        TRDATASTRUCTURE_DEF.log("read_nested( " + property.type.getName() + "[" + n3 + "] )");
                    }
                    TRDATASTRUCTURE_DEF.log("read nested array end!");
                }
            }
            property.setter.invoke((Object)tRDATASTRUCTURE, objectArray);
        }
        return n;
    }

    public int write(TRDATASTRUCTURE tRDATASTRUCTURE, DataOutput dataOutput) throws Exception {
        int n = 0;
        Object object = null;
        TRDATASTRUCTURE tRDATASTRUCTURE2 = null;
        block20: for (int i = 0; i < this.properties.length; ++i) {
            Property property = this.properties[i];
            object = property.getter.invoke((Object)tRDATASTRUCTURE, null);
            int n2 = 0;
            if ((property.typeCode & 0x10) != 0) {
                n2 = property.len > 0 ? property.len : Array.getLength(object);
            }
            switch (property.typeCode) {
                case 1: {
                    dataOutput.writeBoolean((Boolean)object);
                    ++n;
                    continue block20;
                }
                case 2: {
                    dataOutput.writeByte(((Byte)object).byteValue());
                    ++n;
                    continue block20;
                }
                case 3: {
                    dataOutput.writeChar(((Character)object).charValue());
                    n += 2;
                    continue block20;
                }
                case 4: {
                    dataOutput.writeShort(((Short)object).shortValue());
                    n += 2;
                    continue block20;
                }
                case 5: {
                    dataOutput.writeInt((Integer)object);
                    n += 4;
                    continue block20;
                }
                case 6: {
                    dataOutput.writeLong((Long)object);
                    n += 8;
                    continue block20;
                }
                case 7: {
                    dataOutput.writeFloat(((Float)object).floatValue());
                    n += 4;
                    continue block20;
                }
                case 8: {
                    dataOutput.writeDouble((Double)object);
                    n += 8;
                    continue block20;
                }
                case 9: {
                    n += ((TRDATASTRUCTURE)object).getDefinition().write((TRDATASTRUCTURE)object, dataOutput);
                    continue block20;
                }
                case 17: {
                    int n3;
                    for (n3 = 0; n3 < n2; ++n3) {
                        dataOutput.writeBoolean(Array.getBoolean(object, n3));
                    }
                    n += n2;
                    continue block20;
                }
                case 18: {
                    int n3;
                    for (n3 = 0; n3 < n2; ++n3) {
                        dataOutput.writeByte(Array.getByte(object, n3));
                    }
                    n += n2;
                    continue block20;
                }
                case 19: {
                    int n3;
                    for (n3 = 0; n3 < n2; ++n3) {
                        dataOutput.writeChar(Array.getChar(object, n3));
                    }
                    n += n2 * 2;
                    continue block20;
                }
                case 20: {
                    int n3;
                    for (n3 = 0; n3 < n2; ++n3) {
                        dataOutput.writeShort(Array.getShort(object, n3));
                    }
                    n += n2 * 2;
                    continue block20;
                }
                case 21: {
                    int n3;
                    for (n3 = 0; n3 < n2; ++n3) {
                        dataOutput.writeInt(Array.getInt(object, n3));
                    }
                    n += n2 * 4;
                    continue block20;
                }
                case 22: {
                    int n3;
                    for (n3 = 0; n3 < n2; ++n3) {
                        dataOutput.writeLong(Array.getLong(object, n3));
                    }
                    n += n2 * 8;
                    continue block20;
                }
                case 23: {
                    int n3;
                    for (n3 = 0; n3 < n2; ++n3) {
                        dataOutput.writeFloat(Array.getFloat(object, n3));
                    }
                    n += n2 * 4;
                    continue block20;
                }
                case 24: {
                    int n3;
                    for (n3 = 0; n3 < n2; ++n3) {
                        dataOutput.writeDouble(Array.getDouble(object, n3));
                    }
                    n += n2 * 8;
                    continue block20;
                }
                case 25: {
                    int n3;
                    for (n3 = 0; n3 < n2; ++n3) {
                        tRDATASTRUCTURE2 = (TRDATASTRUCTURE)Array.get(object, n3);
                        n += tRDATASTRUCTURE2.getDefinition().write(tRDATASTRUCTURE2, dataOutput);
                    }
                    continue block20;
                }
            }
        }
        return n;
    }

    public int getLength(TRDATASTRUCTURE tRDATASTRUCTURE) throws Exception {
        int n = 0;
        Object object = null;
        TRDATASTRUCTURE tRDATASTRUCTURE2 = null;
        block20: for (int i = 0; i < this.properties.length; ++i) {
            Property property = this.properties[i];
            int n2 = 0;
            if ((property.typeCode & 0x10) != 0) {
                if (property.len > 0) {
                    n2 = property.len;
                } else {
                    object = property.getter.invoke((Object)tRDATASTRUCTURE, null);
                    n2 = Array.getLength(object);
                }
            }
            switch (property.typeCode) {
                case 1: {
                    ++n;
                    continue block20;
                }
                case 2: {
                    ++n;
                    continue block20;
                }
                case 3: {
                    n += 2;
                    continue block20;
                }
                case 4: {
                    n += 2;
                    continue block20;
                }
                case 5: {
                    n += 4;
                    continue block20;
                }
                case 6: {
                    n += 8;
                    continue block20;
                }
                case 7: {
                    n += 4;
                    continue block20;
                }
                case 8: {
                    n += 8;
                    continue block20;
                }
                case 9: {
                    object = property.getter.invoke((Object)tRDATASTRUCTURE, null);
                    n += ((TRDATASTRUCTURE)object).getDefinition().getLength((TRDATASTRUCTURE)object);
                    continue block20;
                }
                case 17: {
                    n += n2;
                    continue block20;
                }
                case 18: {
                    n += n2;
                    continue block20;
                }
                case 19: {
                    n += n2 * 2;
                    continue block20;
                }
                case 20: {
                    n += n2 * 2;
                    continue block20;
                }
                case 21: {
                    n += n2 * 4;
                    continue block20;
                }
                case 22: {
                    n += n2 * 8;
                    continue block20;
                }
                case 23: {
                    n += n2 * 4;
                    continue block20;
                }
                case 24: {
                    n += n2 * 8;
                    continue block20;
                }
                case 25: {
                    if (object == null) {
                        object = property.getter.invoke((Object)tRDATASTRUCTURE, null);
                    }
                    for (int j = 0; j < n2; ++j) {
                        tRDATASTRUCTURE2 = (TRDATASTRUCTURE)Array.get(object, j);
                        n += tRDATASTRUCTURE2.getDefinition().getLength(tRDATASTRUCTURE2);
                    }
                    continue block20;
                }
            }
        }
        return n;
    }

    protected static final void log(Object object) {
    }

    protected class Property {
        protected String name;
        protected Class type;
        protected int typeCode;
        protected int len;
        protected Method getter;
        protected Method setter;

        protected Property(String string, Class clazz, int n, Method method, Method method2) {
            this.name = string;
            this.type = clazz;
            this.typeCode = TRDATASTRUCTURE_DEF.getTypeCode(clazz);
            if (n != 0) {
                this.typeCode |= 0x10;
            }
            this.len = n;
            this.getter = method;
            this.setter = method2;
        }
    }
}

