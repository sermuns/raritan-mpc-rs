/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb;

class TightCacheTile {
    private byte[][] a;

    public TightCacheTile(int n, int n2) {
        this.a = new byte[n][];
        for (int i = 0; i < n; ++i) {
            this.a[i] = new byte[n2];
        }
    }

    public void restoreDataStream(int n, int n2, int n3, byte[] byArray, int n4) {
        byte[] byArray2 = this.a[n];
        System.arraycopy(byArray2, n2, byArray, n4, n3);
    }

    public void saveDataStream(int n, int n2, int n3, byte[] byArray, int n4) {
        byte[] byArray2 = this.a[n];
        System.arraycopy(byArray, n4, byArray2, n2, n3);
    }
}

