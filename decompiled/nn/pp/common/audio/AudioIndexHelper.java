/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.audio;

import java.util.Collections;
import java.util.List;
import nn.pp.common.VMFeatures;

public class AudioIndexHelper {
    private final List<VMFeatures> vmFeatures;

    public AudioIndexHelper(List<VMFeatures> list) {
        this.vmFeatures = list == null ? Collections.emptyList() : list;
    }

    public boolean isAudioAvailable() {
        return this.vmFeatures.size() > 0;
    }

    public int getAudioIndex(boolean bl, boolean bl2, boolean bl3, int n, int n2, int n3) {
        List<VMFeatures> list = this.vmFeatures;
        assert (list.size() > 0);
        Collections.sort(list);
        int n4 = list.get(0).getIndex();
        if (bl) {
            if (n == n4) {
                assert (list.size() > 1);
                return list.get(1).getIndex();
            }
            return n4;
        }
        if (bl2) {
            if (n2 == n4) {
                assert (list.size() > 1);
                return list.get(1).getIndex();
            }
            return n4;
        }
        if (bl3) {
            if (n3 == n4) {
                assert (list.size() > 1);
                return list.get(1).getIndex();
            }
            return n4;
        }
        return n4;
    }
}

