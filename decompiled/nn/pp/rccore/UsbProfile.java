/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import nn.pp.rccore.IUsbProfile;

public class UsbProfile
implements IUsbProfile {
    private int id;
    private String name;
    private String description;
    private boolean selected;

    public UsbProfile(int n, String string, String string2, boolean bl) {
        this.id = n;
        this.name = string;
        this.description = string2;
        this.selected = bl;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setId(int n) {
        this.id = n;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String string) {
        this.description = string;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean bl) {
        this.selected = bl;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof UsbProfile) {
            UsbProfile usbProfile = (UsbProfile)object;
            boolean bl = usbProfile.id == this.id;
            if ((bl = bl && this.strEquals(usbProfile.name, this.name)) && (bl = bl && this.strEquals(usbProfile.description, this.description))) {
                return bl && usbProfile.selected == this.selected;
            }
            return bl;
        }
        return false;
    }

    private boolean strEquals(String string, String string2) {
        if (string == null && string2 == null) {
            return true;
        }
        if (string2 != null) {
            return string2.equals(string);
        }
        return string.equals(string2);
    }

    public String toString() {
        return new String(this.id + ":" + this.name + ":" + (this.selected ? " (selected)" : " (not selected)"));
    }
}

