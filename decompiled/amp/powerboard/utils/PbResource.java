/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.utils;

import java.awt.Cursor;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

public class PbResource {
    private static ResourceBundle bundle;
    private static final Cursor HAND_CURSOR;
    private static final Cursor WAIT_CURSOR;
    private static final Cursor DEFAULT_CURSOR;
    static /* synthetic */ Class class$amp$powerboard$utils$PbResource;

    protected static ResourceBundle getResourceBundle() {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("powerboard");
        }
        return bundle;
    }

    public static String getString(String string) {
        String string2 = null;
        try {
            string2 = PbResource.getResourceBundle().getString(string);
        }
        catch (MissingResourceException missingResourceException) {
            System.out.println("Resource string for '" + string + "' does not exist ");
        }
        catch (Exception exception) {
            System.out.println("Throwing " + exception.getClass().getName() + " exception for key " + string);
        }
        return string2;
    }

    public static ImageIcon getIcon(String string) {
        ImageIcon imageIcon = null;
        StringBuffer stringBuffer = new StringBuffer("/images/");
        stringBuffer.append(PbResource.getString(string));
        try {
            imageIcon = new ImageIcon((class$amp$powerboard$utils$PbResource == null ? (class$amp$powerboard$utils$PbResource = PbResource.class$("amp.powerboard.utils.PbResource")) : class$amp$powerboard$utils$PbResource).getResource(stringBuffer.toString()));
        }
        catch (MissingResourceException missingResourceException) {
            System.out.println("Resource string for '" + string + "' does not exist ");
        }
        catch (Exception exception) {
            System.out.println("Throwing " + exception.getClass().getName() + " exception for key " + string);
        }
        return imageIcon;
    }

    public static Cursor getWaitCursor() {
        return WAIT_CURSOR;
    }

    public static Cursor getDefaultCursor() {
        return DEFAULT_CURSOR;
    }

    public static Cursor getHandCursor() {
        return HAND_CURSOR;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        HAND_CURSOR = Cursor.getPredefinedCursor(12);
        WAIT_CURSOR = Cursor.getPredefinedCursor(3);
        DEFAULT_CURSOR = Cursor.getDefaultCursor();
    }
}

