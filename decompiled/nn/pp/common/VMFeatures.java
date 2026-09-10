/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

public class VMFeatures
implements Comparable<VMFeatures> {
    private final int index;
    private final int featureCount;

    public VMFeatures(int n, int n2) {
        this.index = n;
        int n3 = 0;
        for (int i = 0; i < 8; ++i) {
            n3 += n2 >> i & 1;
        }
        this.featureCount = n3;
    }

    @Override
    public int compareTo(VMFeatures vMFeatures) {
        return this.featureCount - vMFeatures.featureCount;
    }

    public int getIndex() {
        return this.index;
    }
}

