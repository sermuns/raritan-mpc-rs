/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

import nn.pp.rccore.IVMMountRequestResponse;

public class VMMountRequestResponse
implements IVMMountRequestResponse {
    private int option;
    private int index;
    private String user;
    private String password;
    private String host;
    private String image;
    private int retCode;

    public String toString() {
        return "[option=" + this.option + ",index=" + this.index + ",user=" + this.user + ",host=" + this.host + ",image=" + this.image + ",retCode=" + Integer.toHexString(this.retCode) + "]";
    }

    public VMMountRequestResponse(String string, String string2) {
        this.host = string;
        this.image = string2;
    }

    public VMMountRequestResponse(int n, int n2, String string, String string2, String string3, String string4) {
        this.option = n;
        this.index = n2;
        this.user = string;
        this.password = string2;
        this.host = string3;
        this.image = string4;
    }

    public VMMountRequestResponse(int n, int n2, int n3) {
        this.option = n;
        this.index = n2;
        this.retCode = n3;
    }

    public VMMountRequestResponse(IVMMountRequestResponse iVMMountRequestResponse) {
        this.option = iVMMountRequestResponse.getOption();
        this.index = iVMMountRequestResponse.getIndex();
        if (iVMMountRequestResponse.getUser() != null) {
            this.user = new String(iVMMountRequestResponse.getUser());
        }
        if (iVMMountRequestResponse.getPassword() != null) {
            this.password = new String(iVMMountRequestResponse.getPassword());
        }
        if (iVMMountRequestResponse.getHost() != null) {
            this.host = new String(iVMMountRequestResponse.getHost());
        }
        if (iVMMountRequestResponse.getImage() != null) {
            this.image = new String(iVMMountRequestResponse.getImage());
        }
        this.retCode = iVMMountRequestResponse.getRetCode();
    }

    @Override
    public int getOption() {
        return this.option;
    }

    @Override
    public void setOption(int n) {
        this.option = n;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public String getImage() {
        return this.image;
    }

    @Override
    public int getRetCode() {
        return this.retCode;
    }
}

