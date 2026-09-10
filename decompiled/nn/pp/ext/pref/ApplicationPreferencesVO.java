/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.ext.pref;

public class ApplicationPreferencesVO {
    public boolean showScrollBorders;
    public boolean autoSyncMouse;
    public boolean singleCursorInstructions;
    public int osuiHotKey;
    public int keyboardType;
    public int broadcastPort;
    public int defaultHttpsPort;
    public boolean autoColorCal;
    public String keyboardMenuHotkeyValue;
    public boolean enableLogging;
    final boolean ipv6NetworkingEnabled;
    final boolean isAlwaysOpenInFS;
    final String monitorSetting;
    final boolean enableSingleMouse;
    final boolean enableScaling;
    final boolean pinMenu;
    final int scanInterval;
    final int portScanInterval;
    final String thumbnailSize;
    final String orientation;
    int monitorCount = -1;

    public ApplicationPreferencesVO(boolean bl, boolean bl2, boolean bl3, int n, int n2, int n3, int n4, boolean bl4, String string, boolean bl5, boolean bl6, boolean bl7, String string2, int n5, boolean bl8, boolean bl9, boolean bl10, int n6, int n7, String string3, String string4) {
        this.showScrollBorders = bl;
        this.autoSyncMouse = bl2;
        this.singleCursorInstructions = bl3;
        this.osuiHotKey = n;
        this.keyboardType = n2;
        this.broadcastPort = n3;
        this.defaultHttpsPort = n4;
        this.autoColorCal = bl4;
        this.keyboardMenuHotkeyValue = string;
        this.enableLogging = bl5;
        this.ipv6NetworkingEnabled = bl6;
        this.isAlwaysOpenInFS = bl7;
        this.monitorSetting = string2;
        this.monitorCount = n5;
        this.enableSingleMouse = bl8;
        this.enableScaling = bl9;
        this.pinMenu = bl10;
        this.scanInterval = n6;
        this.portScanInterval = n7;
        this.thumbnailSize = string3;
        this.orientation = string4;
    }

    public ApplicationPreferencesVO(boolean bl, boolean bl2, boolean bl3, int n, int n2, int n3, int n4, boolean bl4, String string, boolean bl5, boolean bl6) {
        this(bl, bl2, bl3, n, n2, n3, n4, bl4, string, bl5, bl6, false, null, -1, false, false, false, 10, 10, "", "");
    }

    public ApplicationPreferencesVO(boolean bl, boolean bl2, boolean bl3, int n, int n2, int n3, int n4, boolean bl4, String string) {
        this(bl, bl2, bl3, n, n2, n3, n4, bl4, string, false, false);
    }
}

