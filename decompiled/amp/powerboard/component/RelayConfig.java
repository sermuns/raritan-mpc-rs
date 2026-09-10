/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.component;

import amp.powerboard.component.Constants;
import java.util.StringTokenizer;

public class RelayConfig {
    private int[] relayNumbers;
    private String[] relayNames;

    public RelayConfig(String string, int n) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, Constants.DL_CR);
        this.relayNumbers = new int[n];
        this.relayNames = new String[n];
        int n2 = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken();
            int n3 = string2.indexOf(Constants.DL_OUTLET);
            if (n3 == -1) continue;
            this.relayNumbers[n2] = n2 + 1;
            this.relayNames[n2++] = string2.substring(n3 + Constants.DL_OUTLET.length()).trim();
        }
    }

    public int[] getRelayNumbers() {
        return this.relayNumbers;
    }

    public String[] getRelayNames() {
        return this.relayNames;
    }
}

