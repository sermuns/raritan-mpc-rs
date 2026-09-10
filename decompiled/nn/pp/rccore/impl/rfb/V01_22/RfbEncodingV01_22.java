/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.rccore.RCCore;
import nn.pp.rccore.impl.VideoEventListenerList;
import nn.pp.rccore.impl.rfb.RfbConstants;
import nn.pp.rccore.impl.rfb.RfbEncoding;
import nn.pp.rccore.impl.rfb.RfbPixelFormat;
import nn.pp.rccore.impl.rfb.V01_22.Converter;

public class RfbEncodingV01_22
extends RfbEncoding {
    private static Object converterLock = new Object();
    private static Converter converter = null;
    private static final int ENCODING_TYPE_NOT_SET = 0;
    private static final int ENCODING_TYPE_AUTO = 1;
    private static final int ENCODING_TYPE_PREDEFINED = 2;
    private static final int ENCODING_TYPE_ADVANCED = 3;
    private int encodingType = 0;
    private RCCore.ColorDepth colorDepth = RCCore.ColorDepth.COLOR_16_BIT;
    private RCCore.Compression compression = RCCore.Compression.UNCOMPRESSED;
    private boolean lossy = false;
    private List<Predefine> predefinesHw;
    private List<Predefine> predefinesSw;
    private List<Compression> compressionsHw;
    private List<Compression> compressionsSw;
    private List<ColorDepth> colorDepthsHw;
    private List<ColorDepth> colorDepthsSw;
    private List<RCCore.Smoothing> smoothingValues;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadConverter() {
        Object object = converterLock;
        synchronized (object) {
            if (converter == null) {
                converter = new Converter();
            }
        }
    }

    private static RCCore.ColorDepth getColorDepthFromString(String string) {
        RfbEncodingV01_22.loadConverter();
        return converter.getColorDepthFromString(string);
    }

    private static RCCore.Compression getCompressionFromString(String string) {
        RfbEncodingV01_22.loadConverter();
        return converter.getCompressionFromString(string);
    }

    private static RCCore.Predefine getPredefineFromString(String string) {
        RfbEncodingV01_22.loadConverter();
        return converter.getPredefineFromString(string);
    }

    public RfbEncodingV01_22(Logger logger, VideoEventListenerList videoEventListenerList) {
        super(logger, videoEventListenerList);
        this.addHardwareEncodings();
        this.addSoftwareEncodings();
        this.smoothingValues = new Vector<RCCore.Smoothing>();
        for (RCCore.Smoothing smoothing : RCCore.Smoothing.values()) {
            this.smoothingValues.add(smoothing);
        }
    }

    private void addHardwareEncodings() {
        this.predefinesHw = new Vector<Predefine>();
        this.predefinesHw.add(new Predefine(RCCore.Predefine.LAN_HICOLOR, RCCore.Compression.UNCOMPRESSED, RCCore.ColorDepth.COLOR_16_BIT, false));
        this.predefinesHw.add(new Predefine(RCCore.Predefine.LAN, RCCore.Compression.UNCOMPRESSED, RCCore.ColorDepth.COLOR_8_BIT, false));
        this.predefinesHw.add(new Predefine(RCCore.Predefine.DSL, RCCore.Compression.LEVEL_2, RCCore.ColorDepth.COLOR_8_BIT, false));
        this.predefinesHw.add(new Predefine(RCCore.Predefine.UMTS, RCCore.Compression.LEVEL_4, RCCore.ColorDepth.COLOR_8_BIT, true));
        this.predefinesHw.add(new Predefine(RCCore.Predefine.ISDN, RCCore.Compression.LEVEL_6, RCCore.ColorDepth.COLOR_4_BIT, true));
        this.predefinesHw.add(new Predefine(RCCore.Predefine.MODEM, RCCore.Compression.LEVEL_7, RCCore.ColorDepth.GREY_2_BIT, true));
        this.predefinesHw.add(new Predefine(RCCore.Predefine.MODEM_4BIT, RCCore.Compression.LEVEL_8, RCCore.ColorDepth.GREY_4_BIT, true));
        this.predefinesHw.add(new Predefine(RCCore.Predefine.GPRS, RCCore.Compression.LEVEL_8, RCCore.ColorDepth.GREY_2_BIT, true));
        this.predefinesHw.add(new Predefine(RCCore.Predefine.GSM, RCCore.Compression.LEVEL_9, RCCore.ColorDepth.BW_1_BIT, true));
        Vector<RCCore.ColorDepth> vector = new Vector<RCCore.ColorDepth>();
        vector.add(RCCore.ColorDepth.COLOR_16_BIT);
        vector.add(RCCore.ColorDepth.COLOR_8_BIT);
        vector.add(RCCore.ColorDepth.COLOR_4_BIT);
        vector.add(RCCore.ColorDepth.GREY_4_BIT);
        vector.add(RCCore.ColorDepth.GREY_2_BIT);
        vector.add(RCCore.ColorDepth.BW_1_BIT);
        this.compressionsHw = new Vector<Compression>();
        this.compressionsHw.add(new Compression(RCCore.Compression.UNCOMPRESSED, vector, 128, 0));
        this.compressionsHw.add(new Compression(RCCore.Compression.LEVEL_1, vector, 128, 1));
        this.compressionsHw.add(new Compression(RCCore.Compression.LEVEL_2, vector, 128, 2));
        this.compressionsHw.add(new Compression(RCCore.Compression.LEVEL_3, vector, 128, 3));
        this.compressionsHw.add(new Compression(RCCore.Compression.LEVEL_4, vector, 128, 4));
        this.compressionsHw.add(new Compression(RCCore.Compression.LEVEL_5, vector, 128, 5));
        this.compressionsHw.add(new Compression(RCCore.Compression.LEVEL_6, vector, 128, 6));
        this.compressionsHw.add(new Compression(RCCore.Compression.LEVEL_7, vector, 128, 7));
        this.compressionsHw.add(new Compression(RCCore.Compression.LEVEL_8, vector, 128, 8));
        this.compressionsHw.add(new Compression(RCCore.Compression.LEVEL_9, vector, 128, 9));
        Vector<RCCore.Compression> vector2 = new Vector<RCCore.Compression>();
        vector2.add(RCCore.Compression.UNCOMPRESSED);
        vector2.add(RCCore.Compression.LEVEL_1);
        vector2.add(RCCore.Compression.LEVEL_2);
        vector2.add(RCCore.Compression.LEVEL_3);
        vector2.add(RCCore.Compression.LEVEL_4);
        vector2.add(RCCore.Compression.LEVEL_5);
        vector2.add(RCCore.Compression.LEVEL_6);
        vector2.add(RCCore.Compression.LEVEL_7);
        vector2.add(RCCore.Compression.LEVEL_8);
        vector2.add(RCCore.Compression.LEVEL_9);
        this.colorDepthsHw = new Vector<ColorDepth>();
        this.colorDepthsHw.add(new ColorDepth(RCCore.ColorDepth.COLOR_16_BIT, vector2, 0, 0, 1, RfbPixelFormat.rfbPixelFormat16Bit));
        this.colorDepthsHw.add(new ColorDepth(RCCore.ColorDepth.COLOR_8_BIT, vector2, 8, 2, 3, RfbPixelFormat.rfbPixelFormat16Bit));
        this.colorDepthsHw.add(new ColorDepth(RCCore.ColorDepth.COLOR_4_BIT, vector2, 4, 5, 5, RfbPixelFormat.rfbPixelFormat16Bit));
        this.colorDepthsHw.add(new ColorDepth(RCCore.ColorDepth.GREY_4_BIT, vector2, 3, 6, 7, RfbPixelFormat.rfbPixelFormat16Bit));
        this.colorDepthsHw.add(new ColorDepth(RCCore.ColorDepth.GREY_2_BIT, vector2, 2, 10, 11, RfbPixelFormat.rfbPixelFormat16Bit));
        this.colorDepthsHw.add(new ColorDepth(RCCore.ColorDepth.BW_1_BIT, vector2, 1, 12, 13, RfbPixelFormat.rfbPixelFormat16Bit));
    }

    private void addSoftwareEncodings() {
        this.predefinesSw = new Vector<Predefine>();
        this.predefinesSw.add(new Predefine(RCCore.Predefine.VIDEO_HICOLOR, RCCore.Compression.VIDEO_OPTIMIZED, RCCore.ColorDepth.COLOR_16_BIT, false));
        this.predefinesSw.add(new Predefine(RCCore.Predefine.VIDEO, RCCore.Compression.VIDEO_OPTIMIZED, RCCore.ColorDepth.COLOR_8_BIT, false));
        this.predefinesSw.add(new Predefine(RCCore.Predefine.LAN_HICOLOR, RCCore.Compression.UNCOMPRESSED, RCCore.ColorDepth.COLOR_16_BIT, false));
        this.predefinesSw.add(new Predefine(RCCore.Predefine.LAN, RCCore.Compression.UNCOMPRESSED, RCCore.ColorDepth.COLOR_8_BIT, false));
        this.predefinesSw.add(new Predefine(RCCore.Predefine.DSL, RCCore.Compression.LEVEL_2, RCCore.ColorDepth.COLOR_8_BIT, false));
        this.predefinesSw.add(new Predefine(RCCore.Predefine.UMTS, RCCore.Compression.LEVEL_4, RCCore.ColorDepth.COLOR_8_BIT, true));
        this.predefinesSw.add(new Predefine(RCCore.Predefine.ISDN, RCCore.Compression.LEVEL_6, RCCore.ColorDepth.COLOR_4_BIT, true));
        this.predefinesSw.add(new Predefine(RCCore.Predefine.MODEM, RCCore.Compression.LEVEL_7, RCCore.ColorDepth.GREY_2_BIT, true));
        this.predefinesSw.add(new Predefine(RCCore.Predefine.GPRS, RCCore.Compression.LEVEL_8, RCCore.ColorDepth.GREY_2_BIT, true));
        this.predefinesSw.add(new Predefine(RCCore.Predefine.GSM, RCCore.Compression.LEVEL_9, RCCore.ColorDepth.BW_1_BIT, true));
        Vector<RCCore.ColorDepth> vector = new Vector<RCCore.ColorDepth>();
        vector.add(RCCore.ColorDepth.COLOR_8_BIT);
        vector.add(RCCore.ColorDepth.COLOR_4_BIT);
        vector.add(RCCore.ColorDepth.GREY_4_BIT);
        vector.add(RCCore.ColorDepth.GREY_2_BIT);
        vector.add(RCCore.ColorDepth.BW_1_BIT);
        Vector<RCCore.ColorDepth> vector2 = new Vector<RCCore.ColorDepth>();
        vector2.add(RCCore.ColorDepth.COLOR_16_BIT);
        vector2.add(RCCore.ColorDepth.COLOR_8_BIT);
        this.compressionsSw = new Vector<Compression>();
        this.compressionsSw.add(new Compression(RCCore.Compression.VIDEO_OPTIMIZED, vector2, 10, 0));
        this.compressionsSw.add(new Compression(RCCore.Compression.UNCOMPRESSED, vector2, 5, 0));
        this.compressionsSw.add(new Compression(RCCore.Compression.LEVEL_1, vector, 7, 1));
        this.compressionsSw.add(new Compression(RCCore.Compression.LEVEL_2, vector, 7, 2));
        this.compressionsSw.add(new Compression(RCCore.Compression.LEVEL_3, vector, 7, 3));
        this.compressionsSw.add(new Compression(RCCore.Compression.LEVEL_4, vector, 7, 4));
        this.compressionsSw.add(new Compression(RCCore.Compression.LEVEL_5, vector, 7, 5));
        this.compressionsSw.add(new Compression(RCCore.Compression.LEVEL_6, vector, 7, 6));
        this.compressionsSw.add(new Compression(RCCore.Compression.LEVEL_7, vector, 7, 7));
        this.compressionsSw.add(new Compression(RCCore.Compression.LEVEL_8, vector, 7, 8));
        this.compressionsSw.add(new Compression(RCCore.Compression.LEVEL_9, vector, 7, 9));
        Vector<RCCore.Compression> vector3 = new Vector<RCCore.Compression>();
        vector3.add(RCCore.Compression.VIDEO_OPTIMIZED);
        vector3.add(RCCore.Compression.UNCOMPRESSED);
        vector3.add(RCCore.Compression.LEVEL_1);
        vector3.add(RCCore.Compression.LEVEL_2);
        vector3.add(RCCore.Compression.LEVEL_3);
        vector3.add(RCCore.Compression.LEVEL_4);
        vector3.add(RCCore.Compression.LEVEL_5);
        vector3.add(RCCore.Compression.LEVEL_6);
        vector3.add(RCCore.Compression.LEVEL_7);
        vector3.add(RCCore.Compression.LEVEL_8);
        vector3.add(RCCore.Compression.LEVEL_9);
        Vector<RCCore.Compression> vector4 = new Vector<RCCore.Compression>();
        vector4.add(RCCore.Compression.VIDEO_OPTIMIZED);
        vector4.add(RCCore.Compression.UNCOMPRESSED);
        Vector<RCCore.Compression> vector5 = new Vector<RCCore.Compression>();
        vector5.add(RCCore.Compression.LEVEL_1);
        vector5.add(RCCore.Compression.LEVEL_2);
        vector5.add(RCCore.Compression.LEVEL_3);
        vector5.add(RCCore.Compression.LEVEL_4);
        vector5.add(RCCore.Compression.LEVEL_5);
        vector5.add(RCCore.Compression.LEVEL_6);
        vector5.add(RCCore.Compression.LEVEL_7);
        vector5.add(RCCore.Compression.LEVEL_8);
        vector5.add(RCCore.Compression.LEVEL_9);
        this.colorDepthsSw = new Vector<ColorDepth>();
        this.colorDepthsSw.add(new ColorDepth(RCCore.ColorDepth.COLOR_16_BIT, vector4, 0, 0, 0, RfbPixelFormat.rfbPixelFormat16Bit));
        this.colorDepthsSw.add(new ColorDepth(RCCore.ColorDepth.COLOR_8_BIT, vector3, 8, 0, 0, RfbPixelFormat.rfbPixelFormat8Bit));
        this.colorDepthsSw.add(new ColorDepth(RCCore.ColorDepth.COLOR_4_BIT, vector5, 4, 0, 0, RfbPixelFormat.rfbPixelFormat8Bit));
        this.colorDepthsSw.add(new ColorDepth(RCCore.ColorDepth.GREY_4_BIT, vector5, 3, 0, 0, RfbPixelFormat.rfbPixelFormat8Bit));
        this.colorDepthsSw.add(new ColorDepth(RCCore.ColorDepth.GREY_2_BIT, vector5, 2, 0, 0, RfbPixelFormat.rfbPixelFormat8Bit));
        this.colorDepthsSw.add(new ColorDepth(RCCore.ColorDepth.BW_1_BIT, vector5, 1, 0, 0, RfbPixelFormat.rfbPixelFormat8Bit));
    }

    @Override
    public List<RCCore.Predefine> getSupportedPredefines() {
        Vector<RCCore.Predefine> vector = new Vector<RCCore.Predefine>();
        List<Predefine> list = this.supportHwEnc ? this.predefinesHw : this.predefinesSw;
        for (Predefine predefine : list) {
            vector.add(predefine.predefine);
        }
        return vector;
    }

    @Override
    public List<RCCore.ColorDepth> getSupportedColorDepths() {
        Vector<RCCore.ColorDepth> vector = new Vector<RCCore.ColorDepth>();
        List<ColorDepth> list = this.supportHwEnc ? this.colorDepthsHw : this.colorDepthsSw;
        for (ColorDepth colorDepth : list) {
            vector.add(colorDepth.depth);
        }
        return vector;
    }

    @Override
    public List<RCCore.Compression> getSupportedCompressions() {
        Vector<RCCore.Compression> vector = new Vector<RCCore.Compression>();
        List<Compression> list = this.supportHwEnc ? this.compressionsHw : this.compressionsSw;
        for (Compression compression : list) {
            vector.add(compression.comp);
        }
        return vector;
    }

    @Override
    public List<RCCore.Smoothing> getSupportedSmoothingValues() {
        Vector<RCCore.Smoothing> vector = new Vector<RCCore.Smoothing>();
        for (RCCore.Smoothing smoothing : this.smoothingValues) {
            vector.add(smoothing);
        }
        return vector;
    }

    @Override
    public List<RCCore.ColorDepth> getSupportedColorDepthsForCompression(RCCore.Compression compression) {
        Compression compression2 = this.lookupCompression(compression);
        return compression2.possibleColorDepths;
    }

    @Override
    public List<RCCore.Compression> getSupportedCompressionsForColorDepth(RCCore.ColorDepth colorDepth) {
        ColorDepth colorDepth2 = this.lookupColorDepth(colorDepth);
        return colorDepth2.possibleCompressions;
    }

    @Override
    public RCCore.ColorDepth getColorDepthForPredefine(RCCore.Predefine predefine) {
        Predefine predefine2 = this.lookupPredefine(predefine);
        return predefine2.colorDepth;
    }

    @Override
    public RCCore.Compression getCompressionForPredefine(RCCore.Predefine predefine) {
        Predefine predefine2 = this.lookupPredefine(predefine);
        return predefine2.compression;
    }

    @Override
    public boolean getLossyForPredefine(RCCore.Predefine predefine) {
        Predefine predefine2 = this.lookupPredefine(predefine);
        return predefine2.lossy;
    }

    @Override
    public void setPredefine(RCCore.Predefine predefine) throws IllegalArgumentException {
        Predefine predefine2 = this.lookupPredefine(predefine);
        this.encodingType = 2;
        this.colorDepth = predefine2.colorDepth;
        this.compression = predefine2.compression;
        this.lossy = predefine2.lossy;
    }

    @Override
    public void setEncodingToAuto() {
        this.encodingType = 1;
    }

    @Override
    public void setColorDepth(RCCore.ColorDepth colorDepth) {
        this.encodingType = 3;
        this.colorDepth = colorDepth;
    }

    @Override
    public void setLossy(boolean bl) {
        this.lossy = bl;
    }

    @Override
    public void setCompression(RCCore.Compression compression) {
        this.encodingType = 3;
        this.compression = compression;
    }

    private Predefine lookupPredefine(RCCore.Predefine predefine) {
        List<Predefine> list = this.supportHwEnc ? this.predefinesHw : this.predefinesSw;
        for (Predefine predefine2 : list) {
            if (predefine2.predefine != predefine) continue;
            return predefine2;
        }
        throw new IllegalArgumentException(T._("Predefined encoding not found"));
    }

    private Compression lookupCompression(RCCore.Compression compression) {
        List<Compression> list = this.supportHwEnc ? this.compressionsHw : this.compressionsSw;
        for (Compression compression2 : list) {
            if (compression2.comp != compression) continue;
            return compression2;
        }
        throw new IllegalArgumentException();
    }

    private ColorDepth lookupColorDepth(RCCore.ColorDepth colorDepth) {
        List<ColorDepth> list = this.supportHwEnc ? this.colorDepthsHw : this.colorDepthsSw;
        for (ColorDepth colorDepth2 : list) {
            if (colorDepth2.depth != colorDepth) continue;
            return colorDepth2;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void setEncodingTypeFromRfb(String string) {
        if (this.encodingType != 0) {
            return;
        }
        this.logger.log(Level.FINE, "Using encoding type " + string);
        if (string.equalsIgnoreCase("auto")) {
            this.encodingType = 1;
        } else if (string.equalsIgnoreCase("preconf")) {
            this.encodingType = 2;
        } else if (string.equalsIgnoreCase("manual")) {
            this.encodingType = 3;
        } else {
            this.logger.log(Level.WARNING, MessageFormat.format(T._("Unknown RFB encoding type received: {0}, using Auto"), string));
            this.encodingType = 1;
        }
        this.listeners.fireEncodingAutoChanged(this.encodingType == 1);
    }

    @Override
    public void setEncodingPredefineFromRfb(String string) {
        if (this.encodingType == 2) {
            this.logger.log(Level.FINE, "Using predefined encoding " + string);
            this.setPredefine(RfbEncodingV01_22.getPredefineFromString(string));
            this.listeners.fireEncodingColorDepthChanged(this.colorDepth);
            this.listeners.fireEncodingCompressionChanged(this.compression);
            this.listeners.fireEncodingLossyChanged(this.lossy);
        }
    }

    @Override
    public void setEncodingCompressionFromRfb(String string) {
        this.setEncodingCompression(RfbEncodingV01_22.getCompressionFromString(string));
    }

    @Override
    public void setEncodingCompression(RCCore.Compression compression) {
        if (this.encodingType == 3) {
            this.logger.log(Level.FINE, "Using compression " + (Object)((Object)compression));
            this.setCompression(compression);
            this.listeners.fireEncodingCompressionChanged(this.compression);
        }
    }

    @Override
    public void setEncodingColorDepthFromRfb(String string) {
        this.setEncodingColorDepth(RfbEncodingV01_22.getColorDepthFromString(string));
    }

    @Override
    public void setEncodingColorDepth(RCCore.ColorDepth colorDepth) {
        if (this.encodingType == 3) {
            this.logger.log(Level.FINE, "Using color depth " + (Object)((Object)colorDepth));
            this.setColorDepth(colorDepth);
            this.listeners.fireEncodingColorDepthChanged(this.colorDepth);
        }
    }

    protected int getRfbEncodingParamZLIB(int n) {
        return RfbConstants.rfbEncodingParamZLIB(n);
    }

    protected int getRfbEncodingParamSubenc(int n) {
        return RfbConstants.rfbEncodingParamSubenc(n);
    }

    @Override
    public int[] getRfbEncodings() {
        int[] nArray = new int[4];
        if (this.encodingType == 1) {
            Compression compression = this.lookupCompression(RCCore.Compression.LEVEL_5);
            ColorDepth colorDepth = this.lookupColorDepth(RCCore.ColorDepth.COLOR_8_BIT);
            if (this.supportHwEnc) {
                nArray[0] = 255;
                nArray[1] = compression.rfbEncoding | this.getRfbEncodingParamZLIB(compression.rfbCompressionLevel) | this.getRfbEncodingParamSubenc(colorDepth.rfbLrleSubencLossless);
            } else {
                nArray[0] = 127;
                nArray[1] = compression.rfbEncoding | this.getRfbEncodingParamZLIB(compression.rfbCompressionLevel) | this.getRfbEncodingParamSubenc(colorDepth.rfbSubencSw);
            }
        } else {
            Compression compression = this.lookupCompression(this.compression);
            ColorDepth colorDepth = this.lookupColorDepth(this.colorDepth);
            nArray[0] = compression.rfbEncoding | this.getRfbEncodingParamZLIB(compression.rfbCompressionLevel);
            nArray[0] = !this.supportHwEnc ? nArray[0] | this.getRfbEncodingParamSubenc(colorDepth.rfbSubencSw) : (this.lossy ? nArray[0] | this.getRfbEncodingParamSubenc(colorDepth.rfbLrleSubencLossy) : nArray[0] | this.getRfbEncodingParamSubenc(colorDepth.rfbLrleSubencLossless));
        }
        return nArray;
    }

    @Override
    public RfbPixelFormat getRfbPixelFormat() {
        if (this.encodingType == 1) {
            ColorDepth colorDepth = this.lookupColorDepth(RCCore.ColorDepth.COLOR_8_BIT);
            return colorDepth.rfbPixelFormat;
        }
        ColorDepth colorDepth = this.lookupColorDepth(this.colorDepth);
        return colorDepth.rfbPixelFormat;
    }

    class ColorDepth {
        RCCore.ColorDepth depth;
        int rfbSubencSw;
        int rfbLrleSubencLossy;
        int rfbLrleSubencLossless;
        RfbPixelFormat rfbPixelFormat;
        List<RCCore.Compression> possibleCompressions;

        ColorDepth(RCCore.ColorDepth colorDepth, List<RCCore.Compression> list, int n, int n2, int n3, RfbPixelFormat rfbPixelFormat) {
            this.depth = colorDepth;
            this.possibleCompressions = list;
            this.rfbSubencSw = n;
            this.rfbLrleSubencLossy = n2;
            this.rfbLrleSubencLossless = n3;
            this.rfbPixelFormat = rfbPixelFormat;
        }
    }

    class Compression {
        RCCore.Compression comp;
        int rfbEncoding;
        int rfbCompressionLevel;
        List<RCCore.ColorDepth> possibleColorDepths;

        Compression(RCCore.Compression compression, List<RCCore.ColorDepth> list, int n, int n2) {
            this.comp = compression;
            this.possibleColorDepths = list;
            this.rfbEncoding = n;
            this.rfbCompressionLevel = n2;
        }
    }

    class Predefine {
        RCCore.Predefine predefine;
        RCCore.Compression compression;
        RCCore.ColorDepth colorDepth;
        boolean lossy;

        Predefine(RCCore.Predefine predefine, RCCore.Compression compression, RCCore.ColorDepth colorDepth, boolean bl) {
            this.predefine = predefine;
            this.compression = compression;
            this.colorDepth = colorDepth;
            this.lossy = bl;
        }
    }
}

