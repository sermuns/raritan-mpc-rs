/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.ui.helpers;

import nn.pp.common.KXHttpConnection;
import nn.pp.common.ui.helpers.ICopyrightHandler;
import nn.pp.common.ui.helpers.SwingWorker;
import nn.pp.core.T;

public class AboutSwingWorker
extends SwingWorker {
    private String url = null;
    private String descrip = null;
    private ICopyrightHandler callingDialog = null;
    private String host = null;

    public AboutSwingWorker(ICopyrightHandler iCopyrightHandler, String string, String string2, String string3) {
        this.url = string;
        this.descrip = string2;
        this.callingDialog = iCopyrightHandler;
        this.host = string3;
    }

    @Override
    public Object construct() {
        String string = KXHttpConnection.getArbitraryURL("https://" + this.host, this.url, this.descrip);
        return string;
    }

    @Override
    public void finished() {
        String string = (String)this.getValue();
        if (string.length() == 0) {
            string = T._("Unable to obtain license and copyright data from device.");
            this.callingDialog.fillCopyrightInfo(string, "", "");
        } else {
            String string2 = "";
            String string3 = "";
            try {
                int n = string.indexOf("<td id=\"licstatement\"");
                n = string.indexOf(">", n) + 1;
                int n2 = string.indexOf("</td", n);
                string2 = string.substring(n, n2);
                string3 = this.parseTable(string);
                string2 = this.reassignHREF(string2);
                string = "";
            }
            catch (RuntimeException runtimeException) {
                string = T._("Unable to obtain license and copyright data from device.");
            }
            this.callingDialog.fillCopyrightInfo(string, string3, string2);
        }
    }

    private String reassignHREF(String string) {
        return string.replaceAll("a href=\"/license/", "a href=\"https://" + this.host + "/license/");
    }

    private String parseTable(String string) {
        if (string == null || string.length() < 16) {
            return "";
        }
        int n = string.indexOf("id=\"licences\"");
        n = string.substring(0, n).lastIndexOf("<table");
        int n2 = n + string.substring(n).indexOf("</table");
        n2 = n2 + 1 + string.substring(n2).indexOf(">");
        String string2 = string.substring(n, n2);
        string2 = string2.replaceFirst("<table[^>]*>", "<table border=\"1\" rules=\"all\"><caption align=\"left\"><b>" + T._("Open Source Packages:") + "</b></caption>");
        string2 = string2.replaceAll("A_\\(\"", "");
        string2 = string2.replaceAll("\"\\)", "");
        string2 = this.reassignHREF(string2);
        return string2;
    }
}

