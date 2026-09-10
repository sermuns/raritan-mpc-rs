/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.vmcore.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import nn.pp.core.T;
import nn.pp.vmcore.VMException;

public class NativeLibraryDownloader {
    private static final String libraryName = "DrvRedirNative2";
    private static final String libraryName_x64 = "DrvRedirNative2_x64";
    private static final String localLibraryName = "DrvRedir2";
    private static final int libraryMajor = 1;
    private static final int libraryMinor = 1;

    public void loadNativeLibrary() throws VMException, IOException {
        this.deleteOldLibraryFiles();
        URL uRL = NativeLibraryDownloader.class.getClassLoader().getResource(this.buildSourceFileName());
        URLConnection uRLConnection = uRL.openConnection();
        uRLConnection.connect();
        InputStream inputStream = uRLConnection.getInputStream();
        File file = this.downloadToLocal(inputStream);
        try {
            System.load(file.getCanonicalPath().trim());
        }
        catch (Throwable throwable) {
            throw new VMException(throwable.getMessage());
        }
        int n = this.getVersionMajor();
        int n2 = this.getVersionMinor();
        if (n != 1 || n2 != 1) {
            throw new VMException(T._("Native library version mismatch"));
        }
        if (!this.isSupportedByOSVersion()) {
            throw new VMException(T._("Native implementation not supported by Operation System."));
        }
    }

    private static void debug(String string) {
    }

    private String getFileSuffix() throws VMException {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return ".dll";
        }
        throw new VMException(T._("Not supported by Operating System"));
    }

    private String buildSourceFileName() throws VMException {
        if ("amd64".equals(System.getProperty("os.arch"))) {
            return libraryName_x64 + this.getFileSuffix();
        }
        return libraryName + this.getFileSuffix();
    }

    private String getTimeString() {
        Calendar calendar = Calendar.getInstance();
        int n = calendar.get(1);
        int n2 = calendar.get(2);
        int n3 = calendar.get(5);
        int n4 = calendar.get(11);
        int n5 = calendar.get(12);
        int n6 = calendar.get(13);
        String string = new String("");
        string = string + n;
        if (n2 < 10) {
            string = string + "0";
        }
        string = string + n2;
        if (n3 < 10) {
            string = string + "0";
        }
        string = string + n3;
        string = string + "-";
        if (n4 < 10) {
            string = string + "0";
        }
        string = string + n4;
        if (n5 < 10) {
            string = string + "0";
        }
        string = string + n5;
        if (n6 < 10) {
            string = string + "0";
        }
        string = string + n6;
        return string;
    }

    private File downloadToLocal(InputStream inputStream) throws IOException, VMException {
        int n;
        File file = null;
        file = File.createTempFile("DrvRedir2-" + this.getTimeString() + "-", this.getFileSuffix());
        file.deleteOnExit();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] byArray = new byte[4096];
        int n2 = 0;
        while ((n = inputStream.read(byArray)) != -1) {
            fileOutputStream.write(byArray, 0, n);
            n2 += n;
        }
        fileOutputStream.close();
        inputStream.close();
        NativeLibraryDownloader.debug("Downloaded " + n2 + " bytes of native code to " + file.getCanonicalPath() + ".");
        return file;
    }

    private void deleteOldLibraryFiles() {
        String string = System.getProperty("java.io.tmpdir");
        File file = new File(string);
        if (file != null) {
            File[] fileArray = file.listFiles(new FilenameFilter(){

                @Override
                public boolean accept(File file, String string) {
                    try {
                        return string.startsWith(NativeLibraryDownloader.localLibraryName) && string.endsWith(NativeLibraryDownloader.this.getFileSuffix());
                    }
                    catch (VMException vMException) {
                        return false;
                    }
                }
            });
            for (int i = 0; i < fileArray.length; ++i) {
                fileArray[i].delete();
            }
        }
    }

    private native int getVersionMajor();

    private native int getVersionMinor();

    private native boolean isSupportedByOSVersion();
}

