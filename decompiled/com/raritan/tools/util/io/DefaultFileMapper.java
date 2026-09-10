/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util.io;

import com.raritan.tools.util.io.AbstractFileMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class DefaultFileMapper
extends AbstractFileMapper {
    @Override
    public byte[] toBytes(List list) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            for (int i = 0; i < list.size(); ++i) {
                byteArrayOutputStream.write(this.encodeString(list.get(i).toString() + "\n"));
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public List fromBytes(ByteArrayInputStream byteArrayInputStream) {
        throw new UnsupportedOperationException("This method is not implemented yet because it is not needed");
    }
}

