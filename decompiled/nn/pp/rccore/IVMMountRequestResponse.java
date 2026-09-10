/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

public interface IVMMountRequestResponse {
    public int getOption();

    public void setOption(int var1);

    public int getIndex();

    public String getUser();

    public String getPassword();

    public String getHost();

    public String getImage();

    public int getRetCode();
}

