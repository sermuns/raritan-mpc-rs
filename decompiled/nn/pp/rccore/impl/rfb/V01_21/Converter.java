/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_21;

import java.util.HashMap;
import nn.pp.rccore.RCCore;

class Converter {
    private HashMap<String, RCCore.ColorDepth> colorMap = new HashMap();
    private HashMap<String, RCCore.Compression> compressionMap;
    private HashMap<String, RCCore.Predefine> predefinesMap;

    Converter() {
        this.colorMap.put("color_32bpp", RCCore.ColorDepth.COLOR_32_BIT);
        this.colorMap.put("color_24bpp", RCCore.ColorDepth.COLOR_24_BIT);
        this.colorMap.put("color_16bpp", RCCore.ColorDepth.COLOR_16_BIT);
        this.colorMap.put("color_8bpp", RCCore.ColorDepth.COLOR_8_BIT);
        this.colorMap.put("color_4bpp", RCCore.ColorDepth.COLOR_4_BIT);
        this.colorMap.put("grey_4bpp", RCCore.ColorDepth.GREY_4_BIT);
        this.colorMap.put("grey_3bpp", RCCore.ColorDepth.GREY_3_BIT);
        this.colorMap.put("grey_2bpp", RCCore.ColorDepth.GREY_2_BIT);
        this.colorMap.put("grey_1bpp", RCCore.ColorDepth.BW_1_BIT);
        this.compressionMap = new HashMap();
        this.compressionMap.put("uncompressed", RCCore.Compression.UNCOMPRESSED);
        this.compressionMap.put("comp1", RCCore.Compression.LEVEL_1);
        this.compressionMap.put("comp2", RCCore.Compression.LEVEL_2);
        this.compressionMap.put("comp3", RCCore.Compression.LEVEL_3);
        this.compressionMap.put("comp4", RCCore.Compression.LEVEL_4);
        this.compressionMap.put("comp5", RCCore.Compression.LEVEL_5);
        this.compressionMap.put("comp6", RCCore.Compression.LEVEL_6);
        this.compressionMap.put("comp7", RCCore.Compression.LEVEL_7);
        this.compressionMap.put("comp8", RCCore.Compression.LEVEL_8);
        this.compressionMap.put("comp9", RCCore.Compression.LEVEL_9);
        this.predefinesMap = new HashMap();
        this.predefinesMap.put("videohi", RCCore.Predefine.LAN_HICOLOR);
        this.predefinesMap.put("video", RCCore.Predefine.LAN);
        this.predefinesMap.put("lanhi", RCCore.Predefine.LAN_HICOLOR);
        this.predefinesMap.put("lan", RCCore.Predefine.LAN);
        this.predefinesMap.put("dsl", RCCore.Predefine.DSL);
        this.predefinesMap.put("umts", RCCore.Predefine.UMTS);
        this.predefinesMap.put("isdn", RCCore.Predefine.ISDN);
        this.predefinesMap.put("modem", RCCore.Predefine.MODEM);
        this.predefinesMap.put("modem_4bit", RCCore.Predefine.MODEM_4BIT);
        this.predefinesMap.put("gprs", RCCore.Predefine.GPRS);
        this.predefinesMap.put("gsm", RCCore.Predefine.GSM);
    }

    RCCore.ColorDepth getColorDepthFromString(String string) {
        RCCore.ColorDepth colorDepth = this.colorMap.get(string);
        if (colorDepth != null) {
            return colorDepth;
        }
        return RCCore.ColorDepth.COLOR_16_BIT;
    }

    RCCore.Compression getCompressionFromString(String string) {
        RCCore.Compression compression = this.compressionMap.get(string);
        if (compression != null) {
            return compression;
        }
        return RCCore.Compression.UNCOMPRESSED;
    }

    RCCore.Predefine getPredefineFromString(String string) {
        RCCore.Predefine predefine = this.predefinesMap.get(string);
        if (predefine != null) {
            return predefine;
        }
        return RCCore.Predefine.LAN_HICOLOR;
    }
}

