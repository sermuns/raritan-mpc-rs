/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.components.serialconsole;

import com.raritan.rrc.components.serialconsole.MessageBox;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;

public class FileLogger {
    private FileOutputStream fos = null;
    private boolean writeGranted = true;

    public boolean startLogging() {
        try {
            this.writeGranted = true;
        }
        catch (Exception exception) {
            if (exception.toString().indexOf("ClassNotFoundException") != -1) {
                this.writeGranted = true;
            }
            this.writeGranted = false;
            return false;
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
        if (this.writeGranted) {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.showOpenDialog(null);
            File file = jFileChooser.getSelectedFile();
            if (file != null) {
                try {
                    this.fos = new FileOutputStream(file, true);
                    return true;
                }
                catch (IOException iOException) {
                    new MessageBox("Logging", "Could not open file for logging. File may be read-only or in use.", false).showMessage();
                }
            }
        }
        return false;
    }

    public boolean startLogging(File file) {
        try {
            this.writeGranted = true;
        }
        catch (Exception exception) {
            if (exception.toString().indexOf("ClassNotFoundException") != -1) {
                this.writeGranted = true;
            }
            this.writeGranted = false;
            return false;
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
        if (this.writeGranted && file != null) {
            try {
                this.fos = new FileOutputStream(file, true);
                return true;
            }
            catch (IOException iOException) {
                new MessageBox("Logging", "Could not open file for logging. File may be read-only or in use.", false).showMessage();
            }
        }
        return false;
    }

    public boolean stopLogging() {
        if (this.fos != null) {
            try {
                this.fos.close();
                this.fos = null;
                return true;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return false;
    }

    public void log(byte[] byArray, int n) {
        try {
            if (this.fos != null) {
                this.fos.write(byArray, 0, n);
                this.fos.flush();
            }
        }
        catch (Exception exception) {
            new MessageBox("Logging", "Could not write to file.", false).showMessage();
            this.stopLogging();
        }
    }
}

