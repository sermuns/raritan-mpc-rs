/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.data;

import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;

public class ProductCodes {
    public static final int PRODID_dominionKXG2 = 0;
    public static final int PRODID_dominionKXG1 = 1;
    public static final int PRODID_dominionKSXG2 = 2;
    public static final int PRODID_dominionKSXG1 = 3;
    public static final int PRODID_dominionLX = 4;
    public static final int PRODID_ipReach = 5;
    public static final int PRODID_ustipG1 = 6;
    public static final int PRODID_kx101G2 = 7;
    public static final int PRODID_kx101 = 8;
    private final String[] productNames;
    private static ProductCodes sm_instance;

    private ProductCodes() {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle();
        this.productNames = new String[9];
        this.productNames[0] = raritanPropertyResourceBundle.getString("dominionKXG2");
        this.productNames[1] = raritanPropertyResourceBundle.getString("dominionKXG1");
        this.productNames[2] = raritanPropertyResourceBundle.getString("dominionKSXG2");
        this.productNames[3] = raritanPropertyResourceBundle.getString("dominionKSXG1");
        this.productNames[4] = raritanPropertyResourceBundle.getString("dominionLX");
        this.productNames[5] = raritanPropertyResourceBundle.getString("ipReach");
        this.productNames[6] = raritanPropertyResourceBundle.getString("ustipG1");
        this.productNames[7] = raritanPropertyResourceBundle.getString("kx101G2");
        this.productNames[8] = raritanPropertyResourceBundle.getString("kx101");
    }

    public static synchronized ProductCodes getInstance() {
        if (sm_instance == null) {
            sm_instance = new ProductCodes();
        }
        return sm_instance;
    }

    public boolean isKSXG2(String string) {
        return this.productNames[2].equals(string);
    }

    public boolean isKX101G2(String string) {
        return this.productNames[7].equals(string);
    }

    public boolean isKXG2(String string) {
        return this.productNames[0].equals(string);
    }

    public boolean isDominionLX(String string) {
        return this.productNames[4].equals(string);
    }

    public String getProductCode(int n) {
        if (n >= 0 && n <= 8) {
            return this.productNames[n];
        }
        return null;
    }
}

