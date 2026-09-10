/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.component;

import amp.powerboard.component.Constants;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class RelayStatus {
    private String data;
    private int[] relayNumbers;
    private String[] relayNames;
    private boolean[] relayStates;
    private Hashtable hashNames = new Hashtable(20);
    private Hashtable hashStates = new Hashtable(20);

    public RelayStatus(String string) {
        this.data = string;
        string = string.substring(string.indexOf(Constants.DL_OUTLET) - 2);
        this.parseData(string);
        int n = this.hashNames.size();
        this.relayNumbers = new int[n];
        this.relayNames = new String[n];
        this.relayStates = new boolean[n];
        int n2 = 0;
        while (n2 < n) {
            this.relayNumbers[n2] = n2 + 1;
            this.relayNames[n2] = (String)this.hashNames.get(new Integer(n2 + 1));
            this.relayStates[n2] = (Boolean)this.hashStates.get(new Integer(n2 + 1));
            ++n2;
        }
    }

    private void parseData(String string) {
        int n = 1;
        StringTokenizer stringTokenizer = new StringTokenizer(string, "\n\r");
        while (stringTokenizer.hasMoreTokens()) {
            int n2;
            int n3;
            String string2 = stringTokenizer.nextToken();
            StringTokenizer stringTokenizer2 = new StringTokenizer(string2, ":");
            if (stringTokenizer2.countTokens() == 1) continue;
            String string3 = "";
            boolean bl = false;
            if (stringTokenizer2.countTokens() == 3) {
                n3 = 6;
                string3 = "";
                n2 = 0;
                while (n2 < 2) {
                    int n4 = string2.indexOf(": On");
                    int n5 = string2.indexOf(": Off");
                    if (n5 == -1) {
                        n5 = string2.indexOf(":Off");
                    }
                    if (n4 != -1 && n5 != -1) {
                        if (n4 < n5) {
                            string3 = string2.substring(n3, n4);
                            bl = true;
                            n3 += n4 + 3;
                        } else {
                            string3 = string2.substring(n3, n5);
                            bl = false;
                            n3 += n5 + 4;
                        }
                    } else if (n4 != -1) {
                        string3 = string2.substring(n3, n4);
                        bl = true;
                        n3 += n4 + 3;
                    } else if (n5 != -1) {
                        string3 = string2.substring(n3, n5);
                        bl = false;
                        n3 += n5 + 4;
                    }
                    this.hashNames.put(new Integer(n), string3);
                    this.hashStates.put(new Integer(n++), new Boolean(bl));
                    if (n3 < string2.length()) {
                        string2 = string2.substring(n3).trim();
                    }
                    n3 = n > 9 ? 6 : 5;
                    ++n2;
                }
                continue;
            }
            n3 = stringTokenizer2.countTokens() - 1;
            n2 = 0;
            while (n2 < n3) {
                string3 = string3 + stringTokenizer2.nextToken();
                ++n2;
            }
            String string4 = stringTokenizer2.nextToken();
            bl = string4.indexOf("Off") == -1;
            this.hashNames.put(new Integer(n), string3.substring(6));
            this.hashStates.put(new Integer(n++), new Boolean(bl));
        }
    }

    public int[] getRelayNumbers() {
        return this.relayNumbers;
    }

    public String[] getRelayNames() {
        return this.relayNames;
    }

    public boolean[] getRelayStates() {
        return this.relayStates;
    }

    public int getNumOutlets() {
        return this.relayNames.length;
    }
}

