/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.util.Arrays;
import java.util.List;
import nn.pp.rccore.IVMConfigInfo;

public class VMConfigInfo
implements IVMConfigInfo {
    private int[] vmconfig;
    private String errorMsg = "ERROR: ";
    private static final List<Integer> validValues;

    public VMConfigInfo(int[] nArray) {
        this.vmconfig = nArray;
    }

    @Override
    public int[] getVMConfig() {
        return this.vmconfig;
    }

    public int validate() {
        if (this.vmconfig.length < 0 || this.vmconfig.length > 4) {
            this.errorMsg = this.errorMsg + "Invalid number of devices";
        } else {
            for (int i = 0; i < this.vmconfig.length; ++i) {
                if (this.vmconfig[i] == 0 || this.vmconfig[i] == 1 || this.vmconfig[i] == 4 || this.vmconfig[i] == 2 || this.vmconfig[i] == 5) continue;
                this.errorMsg = this.errorMsg + "received index: " + this.vmconfig[i];
            }
        }
        if (this.errorMsg.length() > 7) {
            return -1;
        }
        return 1;
    }

    public int validate_28() {
        if (this.vmconfig.length < 0 || this.vmconfig.length > 4) {
            this.errorMsg = this.errorMsg + "Invalid number of devices";
        } else {
            for (int i = 0; i < this.vmconfig.length; ++i) {
                if (validValues.contains(this.vmconfig[i])) continue;
                this.errorMsg = this.errorMsg + "received index: " + this.vmconfig[i];
            }
        }
        if (this.errorMsg.length() > 7) {
            return -1;
        }
        return 1;
    }

    public String getErrorMessage() {
        return this.errorMsg;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("VMConfigInfo: [");
        for (int i = 0; i < this.vmconfig.length; ++i) {
            stringBuffer.append(this.vmconfig[i] + (i != this.vmconfig.length - 1 ? "," : "]"));
        }
        return stringBuffer.toString();
    }

    static {
        int n = 0;
        int n2 = 2;
        int n3 = 8;
        int n4 = 1;
        int n5 = 4;
        int n6 = 5;
        int n7 = 9;
        int n8 = 12;
        int n9 = 13;
        int n10 = 16;
        int n11 = 17;
        int n12 = 20;
        int n13 = 21;
        int n14 = 24;
        int n15 = 25;
        int n16 = 28;
        int n17 = 29;
        validValues = Arrays.asList(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17);
    }
}

