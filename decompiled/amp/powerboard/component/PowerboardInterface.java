/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.component;

import java.awt.Image;
import java.net.URL;

public interface PowerboardInterface {
    public void consoleCallback();

    public void powerHelpCallback();

    public void finalCleanUp();

    public URL getCodebaseURL();

    public void addStateManager();

    public Image getImage(String var1);
}

