/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.commands;

import com.raritan.rrc.data.Device;
import com.raritan.rrc.data.Port;
import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.components.CommonPopups;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javaclientlib.utils.RRCLogger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import nn.pp.common.FileNameExtensionFilter;
import nn.pp.rccore.RCCore;

public class DoCaptureTargetScreenshotCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "doCaptureTargetScreenshotCommand";

    public DoCaptureTargetScreenshotCommand(RRCScreenContext rRCScreenContext) {
        super(rRCScreenContext);
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    @Override
    public CommandResult execute() {
        this.scrContext.getLogger().logTextDebug(" Started ");
        Port port = ((RRCScreenContext)this.scrContext).getSelectedPort();
        if (port != null && port.isConnected() && port.getDeviceClass().equals("KVM") && port.isConnected()) {
            RFBView rFBView;
            RFBView rFBView2 = rFBView = port.getView() instanceof RFBView ? (RFBView)port.getView() : null;
            if (rFBView != null) {
                ScreenshotHandler screenshotHandler = new ScreenshotHandler(rFBView, rFBView.getRCCore());
                screenshotHandler.screenshotToFile();
            } else {
                RRCLogger.log(100, 1, "View is null in DoCalibrateColorCommand");
            }
        }
        this.scrContext.getLogger().logTextDebug(" Finished ");
        return new CommandResult();
    }

    @Override
    public boolean isExecutable() {
        Port port;
        ArrayList arrayList = (ArrayList)((RRCScreenContext)this.scrContext).getSelectedDevicesObservable().getComponent();
        if (arrayList == null || arrayList.size() == 0) {
            return false;
        }
        Device device = (Device)arrayList.get(0);
        return device instanceof Port && device.isConnected() && (port = (Port)device).getDeviceClass().equalsIgnoreCase("KVM") && port.getDevice().getHandler().canDoTargetScreenCapture();
    }

    class ScreenshotHandler {
        private AbstractDisplay parent;
        private RCCore rcCore;

        public ScreenshotHandler(AbstractDisplay abstractDisplay, RCCore rCCore) {
            this.parent = abstractDisplay;
            this.rcCore = rCCore;
        }

        private File adjustFile(JFileChooser jFileChooser) {
            FileNameExtensionFilter fileNameExtensionFilter = (FileNameExtensionFilter)jFileChooser.getFileFilter();
            File file = jFileChooser.getSelectedFile();
            String string = fileNameExtensionFilter.getExtensions()[0];
            String string2 = file.getName();
            if (!string2.toLowerCase().endsWith(string)) {
                string2 = string2 + "." + string;
                file = new File(file.getParent(), string2);
            }
            return file;
        }

        private int showSaveDisplayQuestion(File file, JFileChooser jFileChooser) {
            return CommonPopups.showFileOverwriteConfirmationDialog(this.parent, DoCaptureTargetScreenshotCommand.this.scrContext);
        }

        public void screenshotToFile() {
            JFileChooser jFileChooser = new JFileChooser(System.getProperty("user.home")){

                @Override
                public void approveSelection() {
                    int n;
                    File file = ScreenshotHandler.this.adjustFile(this);
                    if (file != null && file.exists() && (n = ScreenshotHandler.this.showSaveDisplayQuestion(file, this)) == 1) {
                        return;
                    }
                    super.approveSelection();
                }
            };
            jFileChooser.setFileSelectionMode(0);
            jFileChooser.setMultiSelectionEnabled(false);
            jFileChooser.setAcceptAllFileFilterUsed(false);
            jFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG image files", "png"));
            jFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP image files", "bmp"));
            jFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG image files", "jpg"));
            int n = jFileChooser.showSaveDialog(this.parent);
            if (n == 0) {
                FileNameExtensionFilter fileNameExtensionFilter = (FileNameExtensionFilter)jFileChooser.getFileFilter();
                String string = fileNameExtensionFilter.getExtensions()[0];
                File file = this.adjustFile(jFileChooser);
                BufferedImage bufferedImage = this.rcCore.getSnapshot();
                if (bufferedImage == null) {
                    this.error(DoCaptureTargetScreenshotCommand.this.getBundle().getString("CaptureScreenshot.cannotGetImage"));
                    return;
                }
                try {
                    if (!ImageIO.write((RenderedImage)bufferedImage, string, file)) {
                        throw new Exception();
                    }
                }
                catch (Exception exception) {
                    this.error(DoCaptureTargetScreenshotCommand.this.getBundle().getString("CaptureScreenshot.cannotWriteImage"));
                    return;
                }
            }
        }

        public void screenshotToClipboard() {
            final BufferedImage bufferedImage = this.rcCore.getSnapshot();
            if (bufferedImage == null) {
                this.error(DoCaptureTargetScreenshotCommand.this.getBundle().getString("CaptureScreenshot.cannotGetImage"));
                return;
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable(){

                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{DataFlavor.imageFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
                    return DataFlavor.imageFlavor.equals(dataFlavor);
                }

                @Override
                public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
                    if (!DataFlavor.imageFlavor.equals(dataFlavor)) {
                        throw new UnsupportedFlavorException(dataFlavor);
                    }
                    return bufferedImage;
                }
            }, null);
        }

        private void error(String string) {
            CommonPopups.showCommandResultErrorMessage(string, this.parent, DoCaptureTargetScreenshotCommand.this.scrContext);
        }
    }
}

