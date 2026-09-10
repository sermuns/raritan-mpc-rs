/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.AppletContext
 */
package com.raritan.rrc.ui.panes;

import java.applet.AppletContext;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import javax.swing.JOptionPane;

public class BrowserLaunch {
    private static final String errMsg = "Error attempting to launch web browser.";

    public static void openURL(String string, AppletContext appletContext) {
        String string2 = System.getProperty("os.name");
        try {
            if (string2.startsWith("Mac OS")) {
                Class<?> clazz = Class.forName("com.apple.eio.FileManager");
                Method method = clazz.getDeclaredMethod("openURL", String.class);
                method.invoke(null, string);
            } else if (string2.startsWith("Windows")) {
                if (appletContext != null) {
                    appletContext.showDocument(new URL(string), "_blank");
                } else {
                    String[] stringArray = new String[]{"rundll32", "url.dll,FileProtocolHandler", BrowserLaunch.displayURLinNew(string)};
                    Runtime.getRuntime().exec(stringArray);
                }
            } else {
                String[] stringArray = new String[]{"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                String string3 = null;
                for (int i = 0; i < stringArray.length && string3 == null; ++i) {
                    if (Runtime.getRuntime().exec(new String[]{"which", stringArray[i]}).waitFor() != 0) continue;
                    string3 = stringArray[i];
                }
                if (string3 == null) {
                    throw new Exception("Could not find web browser");
                }
                Runtime.getRuntime().exec(new String[]{string3, string});
            }
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "Error attempting to launch web browser.:\n" + exception.getLocalizedMessage());
        }
    }

    public static String displayURLinNew(String string) throws IOException {
        File file = File.createTempFile("DisplayURLs", ".html");
        file.deleteOnExit();
        file = file.getCanonicalFile();
        PrintWriter printWriter = new PrintWriter(new FileWriter(file));
        printWriter.println("<!-- saved from url=(0014)about:internet -->");
        printWriter.println("<html>");
        printWriter.println("<head>");
        printWriter.println("<title> Admin </title>");
        printWriter.println("<script language=\"javascript\" type=\"text/javascript\">");
        printWriter.println("function displayURLs(){");
        printWriter.println("window.open(\"" + string + "\", \"_self\", \"toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes\");");
        printWriter.println("}");
        printWriter.println("</script>");
        printWriter.println("</head>");
        printWriter.println("<body onload=\"javascript:displayURLs()\">");
        printWriter.println("<noscript>");
        printWriter.println("<a target=\"_blank\" href=\"" + string + "\"> Launch Admin </a><br>");
        printWriter.println("</noscript>");
        printWriter.println("</body>");
        printWriter.println("</html>");
        printWriter.close();
        return file.getAbsolutePath();
    }
}

