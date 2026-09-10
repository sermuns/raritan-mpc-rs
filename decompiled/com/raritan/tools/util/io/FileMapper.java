/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util.io;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface FileMapper {
    public byte[] toBytes(List var1);

    public List fromBytes(ByteArrayInputStream var1);
}

