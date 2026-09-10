/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

class KeycodeWithLocation {
    private int keyCode;
    private int keyLocation;

    KeycodeWithLocation(int n, int n2) {
        this.keyCode = n;
        this.keyLocation = n2;
    }

    public boolean equals(Object object) {
        if (object != null && object instanceof KeycodeWithLocation) {
            KeycodeWithLocation keycodeWithLocation = (KeycodeWithLocation)object;
            if (this.keyCode != keycodeWithLocation.keyCode) {
                return false;
            }
            if (this.keyLocation == 0 || keycodeWithLocation.keyLocation == 0) {
                return true;
            }
            return this.keyLocation == keycodeWithLocation.keyLocation;
        }
        return false;
    }

    public int hashCode() {
        return 65536 * this.keyLocation + this.keyCode;
    }
}

