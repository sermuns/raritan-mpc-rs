/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util;

import com.raritan.tools.ui.ScreenContext;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import nn.pp.common.ResourceLoader;

public class ImageHolder {
    protected ScreenContext ctx;
    private Map iconMap;

    public ImageHolder(ScreenContext screenContext) {
        this.ctx = screenContext;
        this.iconMap = new HashMap();
    }

    public Image getImage(String string) {
        Image image = (Image)this.iconMap.get(string);
        if (image == null) {
            image = ResourceLoader.loadImage(string);
            this.iconMap.put(string, image);
        }
        return image;
    }
}

