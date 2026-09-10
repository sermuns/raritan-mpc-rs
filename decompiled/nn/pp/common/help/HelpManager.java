/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.help;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class HelpManager {
    private ResourceBundle bundle;
    private final ClassLoader clsLoader;
    private final String prefix;
    private JFrame frame;
    private final JEditorPane htmlPane = new JEditorPane();
    private final String title;
    private final Image windowImage;

    public HelpManager(ResourceBundle resourceBundle, ClassLoader classLoader, String string, String string2, Image image) {
        this.clsLoader = classLoader;
        assert (this.clsLoader != null) : "clsLoader for loading html files null";
        this.prefix = string == null ? "" : string;
        this.bundle = resourceBundle;
        assert (this.bundle != null);
        this.title = string2;
        this.windowImage = image;
    }

    public HelpManager(ClassLoader classLoader, String string, String string2, Image image) {
        this.clsLoader = classLoader;
        assert (this.clsLoader != null) : "clsLoader for loading html files null";
        this.prefix = string == null ? "" : string;
        this.title = string2;
        this.windowImage = image;
    }

    public void setHelpWindowOnTop(boolean bl) {
        if (this.frame != null) {
            this.frame.setAlwaysOnTop(bl);
        }
    }

    public void showHelp(String string, Component component) {
        assert (string != null) : " Null topic to display";
        String string2 = string;
        if (this.bundle != null) {
            string2 = this.bundle.getString(string);
        }
        URL uRL = this.getHelpURL(string2);
        try {
            this.htmlPane.setPage(uRL);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        this.getFrame(component).setVisible(true);
    }

    private JFrame getFrame(Component component) {
        if (this.frame == null) {
            this.frame = new JFrame();
            this.frame.setDefaultCloseOperation(1);
            this.htmlPane.setEditable(false);
            JScrollPane jScrollPane = new JScrollPane(this.htmlPane);
            this.frame.getContentPane().add(jScrollPane);
            if (component != null) {
                Component component2;
                Component component3 = component2 = component instanceof Window ? component : SwingUtilities.getWindowAncestor(component);
                if (component2 != null) {
                    Point point = component2.getLocationOnScreen();
                    int n = point.x + component2.getWidth();
                    int n2 = point.y;
                    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                    n = Math.min(n, dimension.width - 800);
                    n2 = Math.min(n2, dimension.height - 600);
                    this.frame.setLocation(n, n2);
                }
            }
            this.frame.setSize(800, 600);
            this.frame.setTitle(this.title);
            this.frame.setIconImage(this.windowImage);
        }
        return this.frame;
    }

    private URL getHelpURL(String string) {
        assert (string != null) : "Null html help file to display";
        String string2 = this.prefix + string;
        URL uRL = this.clsLoader.getResource(string2);
        assert (uRL != null) : "Unable to find the HTML help file: " + string2;
        return uRL;
    }

    public void cleanup() {
        if (this.frame != null) {
            this.frame.dispose();
        }
    }
}

