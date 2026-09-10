/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import java.awt.Image;
import java.awt.Rectangle;
import javaclientlib.tr.BitMapInfo;
import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;
import javaclientlib.tr.TRRECT;

public class TRLIB_UPDATEINFO
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRLIB_UPDATEINFO.class, new String[]{"rect", "lastField"}, new Class[]{TRRECT.class, Boolean.TYPE}, new int[]{0, 0});
    private TRRECT rect;
    private BitMapInfo bitMapInfo;
    private byte[] frameBuffer;
    private Image image;
    private boolean lastField;
    private Rectangle objRectangle = new Rectangle(0, 0, 0, 0);
    public static final short CMD_LEN = 49;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 49;
    }

    public void setRect(TRRECT tRRECT) {
        this.rect = tRRECT;
    }

    public TRRECT getRect() {
        return this.rect;
    }

    public BitMapInfo getBitMapInfo() {
        return this.bitMapInfo;
    }

    public void setBitMapInfo(BitMapInfo bitMapInfo) {
        this.bitMapInfo = bitMapInfo;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public byte[] getFrameBuffer() {
        return this.frameBuffer;
    }

    public void setFrameBuffer(byte[] byArray) {
        this.frameBuffer = byArray;
    }

    public boolean isLastField() {
        return this.lastField;
    }

    public void setLastField(boolean bl) {
        this.lastField = bl;
    }

    public void setRectangle(Rectangle rectangle) {
        this.objRectangle = rectangle;
    }

    public Rectangle getRectangle() {
        return this.objRectangle;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n\t TRLIB_UPDATEINFO ---------");
        stringBuffer.append("\nRectangle.X  =" + this.objRectangle.getX());
        stringBuffer.append("\nRectangle.Y   =" + this.objRectangle.getY());
        stringBuffer.append("\nRectangle.width =" + this.objRectangle.getWidth());
        stringBuffer.append("\nRectangle.height=" + this.objRectangle.getHeight());
        stringBuffer.append("\nBMInfH.BISize=" + this.bitMapInfo.getBMIHeader().getBISize());
        stringBuffer.append("\nBMInfH.BIWidth=" + this.bitMapInfo.getBMIHeader().getBIWidth());
        stringBuffer.append("\nBMInfH.BIHeight=" + this.bitMapInfo.getBMIHeader().getBIHeight());
        stringBuffer.append("\nBMInfH.BIPlanes=" + this.bitMapInfo.getBMIHeader().getBIPlanes());
        stringBuffer.append("\nBMInfH.BIBitCount=" + this.bitMapInfo.getBMIHeader().getBIBitCount());
        stringBuffer.append("\nBMInfH.BICompression=" + this.bitMapInfo.getBMIHeader().getBICompression());
        stringBuffer.append("\nBMInfH.BISizeImage=" + this.bitMapInfo.getBMIHeader().getBISizeImage());
        stringBuffer.append("\nBMInfH.BIXPelsPerMeter=" + this.bitMapInfo.getBMIHeader().getBIXPelsPerMeter());
        stringBuffer.append("\nBMInfH.BIYPelsPerMeter=" + this.bitMapInfo.getBMIHeader().getBIYPelsPerMeter());
        stringBuffer.append("\nBMInfH.BIClrUsed=" + this.bitMapInfo.getBMIHeader().getBIClrUsed());
        stringBuffer.append("\nBMInfH.BIClrImportant=" + this.bitMapInfo.getBMIHeader().getBIClrImportant());
        stringBuffer.append("\nTRLIB_UpdInf.lastField=" + this.isLastField());
        return stringBuffer.toString();
    }
}

