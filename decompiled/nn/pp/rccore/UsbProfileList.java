/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import java.util.ArrayList;
import java.util.List;
import nn.pp.core.T;
import nn.pp.rccore.IUsbProfile;
import nn.pp.rccore.IUsbProfileList;
import nn.pp.rccore.UsbProfile;

public class UsbProfileList
implements Cloneable,
IUsbProfileList {
    private UsbProfile active;
    private UsbProfile preferred;
    private List<IUsbProfile> profiles = new ArrayList<IUsbProfile>();
    private String errorMsg = "ERROR: ";

    public void addProfile(UsbProfile usbProfile, boolean bl, boolean bl2) {
        this.profiles.add(usbProfile);
        if (bl && this.active != null) {
            this.errorMsg = this.errorMsg + T._("Duplicate Active: " + this.active.getName() + " : " + usbProfile.getName()) + "\n";
        } else if (bl && this.active == null) {
            this.active = usbProfile;
        }
        if (bl2 && this.preferred != null) {
            this.errorMsg = this.errorMsg + T._("Duplicate Preferred: " + this.preferred.getName() + " : " + usbProfile.getName()) + "\n";
        } else if (bl2 && this.preferred == null) {
            this.preferred = usbProfile;
        }
    }

    public void clear() {
        this.profiles.clear();
        this.active = null;
        this.preferred = null;
        System.runFinalization();
        System.gc();
    }

    @Override
    public List<IUsbProfile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(List<IUsbProfile> list) {
        this.profiles = list;
    }

    @Override
    public IUsbProfile getActive() {
        return this.active;
    }

    @Override
    public IUsbProfile getPreferred() {
        return this.preferred;
    }

    public void setActive(UsbProfile usbProfile) {
        this.active = usbProfile;
    }

    public void setPreferred(UsbProfile usbProfile) {
        this.preferred = usbProfile;
    }

    public int validate() {
        int n = 0;
        if (this.profiles.size() > 0) {
            UsbProfile usbProfile;
            if (this.active == null) {
                this.errorMsg = this.errorMsg + T._("No Active profile in message") + "\n";
            }
            if (this.preferred == null) {
                this.errorMsg = this.errorMsg + T._("No Preferred profile in message") + "\n";
            }
            for (n = 0; n < this.profiles.size() && !(usbProfile = (UsbProfile)this.profiles.get(n)).isSelected(); ++n) {
            }
            if (n == this.profiles.size()) {
                this.errorMsg = this.errorMsg + T._("No Selected profile");
            }
            if (this.errorMsg.length() > 7) {
                this.clear();
                return -1;
            }
        }
        return 1;
    }

    public String getErrorMessage() {
        return this.errorMsg != null ? this.errorMsg : " ";
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("UsbProfileList [Active: " + (this.active == null ? "null" : String.valueOf(this.active.getId())) + ", Preferred: " + (this.preferred == null ? "null" : String.valueOf(this.preferred.getId())));
        for (IUsbProfile iUsbProfile : this.profiles) {
            stringBuffer.append(", Entry: " + iUsbProfile.toString());
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    protected Object clone() throws CloneNotSupportedException {
        UsbProfileList usbProfileList = new UsbProfileList();
        usbProfileList.setActive(this.active);
        usbProfileList.setPreferred(this.preferred);
        usbProfileList.setProfiles(this.getProfiles());
        return usbProfileList;
    }
}

