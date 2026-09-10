/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

public interface ConnectCallback {
    public void connect(int var1, String var2, String var3, String var4, String var5, String var6, String var7);

    public void connect_ports(String[] var1, String[] var2, String[] var3, String[] var4, String[] var5, int[] var6, int[] var7, int[] var8);

    public void scanFrameClosed(boolean var1);
}

