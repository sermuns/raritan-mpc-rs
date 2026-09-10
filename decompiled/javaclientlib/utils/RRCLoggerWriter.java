/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.utils;

import java.io.FileWriter;
import java.io.IOException;

public class RRCLoggerWriter
extends FileWriter {
    protected long count;

    public RRCLoggerWriter(String string, boolean bl) throws IOException {
        super(string, bl);
    }

    public void write(byte[] byArray) throws IOException {
        for (int i = 0; i < byArray.length; ++i) {
            super.write(byArray[i]);
        }
        this.count += (long)byArray.length;
    }

    @Override
    public void write(char[] cArray) throws IOException {
    }

    @Override
    public void write(String string) throws IOException {
        super.write(string);
        this.count += (long)string.getBytes().length;
    }

    public long getCount() {
        return this.count;
    }

    public void setCount(long l) {
        this.count = l;
    }
}

