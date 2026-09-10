/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.clientlib;

import java.lang.reflect.Method;

public class RSPDEF {
    private String szMethodName;
    private int iWhatToDo;
    private int iDataSize;
    private Method objExtMethod;
    private Object objExtObject;
    private Object objUserData;

    public String getMethodName() {
        return this.szMethodName;
    }

    public void setMethodName(String string) {
        this.szMethodName = string;
    }

    public int getWhatToDo() {
        return this.iWhatToDo;
    }

    public void setWhatToDo(int n) {
        this.iWhatToDo = n;
    }

    public int getDataSize() {
        return this.iDataSize;
    }

    public void setDataSize(int n) {
        this.iDataSize = n;
    }

    public Method getExtMethod() {
        return this.objExtMethod;
    }

    public void setExtMethod(Method method) {
        this.objExtMethod = method;
    }

    public Object getExtObject() {
        return this.objExtObject;
    }

    public void setExtObject(Object object) {
        this.objExtObject = object;
    }

    public Object getUserData() {
        return this.objUserData;
    }

    public void setUserData(Object object) {
        this.objUserData = object;
    }

    public void populateRSDEF(String string, int n, int n2, Object object, Object object2) {
        this.szMethodName = string;
        this.iWhatToDo = n;
        this.iDataSize = n2;
        this.objUserData = object;
        this.objExtObject = object2;
    }
}

