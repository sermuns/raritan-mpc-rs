/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util.image;

import com.raritan.tools.util.image.DualImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class DualImageFactory {
    private static final ArrayList cachedImages = new ArrayList();

    public static ImageIcon getDualImageIcon(ImageIcon imageIcon, ImageIcon imageIcon2) {
        if (imageIcon2 == null) {
            return imageIcon;
        }
        if (imageIcon == null) {
            return imageIcon2;
        }
        return DualImageFactory.getDualImage(imageIcon, imageIcon2).getDualIcon();
    }

    public static DualImage getDualImage(ImageIcon imageIcon, ImageIcon imageIcon2) {
        DualImage dualImage = null;
        DualImage dualImage22 = null;
        for (DualImage dualImage22 : cachedImages) {
            if (!dualImage22.getLeftImage().getDescription().equals(imageIcon.getDescription()) || !dualImage22.getRightImage().getDescription().equals(imageIcon2.getDescription())) continue;
            dualImage = dualImage22;
            break;
        }
        if (dualImage == null) {
            dualImage = new DualImage(imageIcon, imageIcon2);
            cachedImages.add(dualImage);
        }
        return dualImage;
    }

    public static void clearCachedImages() {
        cachedImages.clear();
    }
}

