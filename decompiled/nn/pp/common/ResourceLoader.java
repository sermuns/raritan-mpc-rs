/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import nn.pp.logging.RemoteConsoleLogger;

public class ResourceLoader {
    public static Image loadImage(String string) {
        URL uRL = ResourceLoader.class.getResource("/" + ResourceLoader.class.getPackage().getName().replace('.', '/') + "/icons/" + string);
        if (uRL != null) {
            return Toolkit.getDefaultToolkit().getImage(uRL);
        }
        RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "Failed to load image " + string + " from:n\t" + ResourceLoader.class.getPackage().getName().replace('.', '/') + "/icons/" + string);
        return null;
    }

    public static ImageIcon loadImageIcon(String string) {
        Image image = ResourceLoader.loadImage(string);
        if (image != null) {
            ImageIcon imageIcon = new ImageIcon(image);
            imageIcon.setDescription(string);
            return imageIcon;
        }
        return null;
    }
}

