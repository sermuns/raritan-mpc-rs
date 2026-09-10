/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRDATASTRUCTURE;
import javaclientlib.tr.TRDATASTRUCTURE_DEF;

public class TRSRVR_LOG
extends TRDATASTRUCTURE {
    protected static final TRDATASTRUCTURE_DEF definition = new TRDATASTRUCTURE_DEF(TRSRVR_LOG.class, new String[]{"eventCode", "eventType", "reserved1", "year", "month", "day", "hour", "minute", "second", "reserved2"}, new Class[]{Integer.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE}, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    private int eventCode;
    private short eventType;
    private short reserved1;
    private short year;
    private byte month;
    private byte day;
    private byte hour;
    private byte minute;
    private byte second;
    private byte reserved2;
    private Object objTRSRVR_LOG;
    public static final short CMD_LEN = 20;

    @Override
    public TRDATASTRUCTURE_DEF getDefinition() {
        return definition;
    }

    @Override
    public short getLength() {
        return 20;
    }

    public int getEventCode() {
        return this.eventCode;
    }

    public void setEventCode(int n) {
        this.eventCode = n;
    }

    public short getEventType() {
        return this.eventType;
    }

    public void setEventType(short s) {
        this.eventType = s;
    }

    public short getReserved1() {
        return this.reserved1;
    }

    public void setReserved1(short s) {
        this.reserved1 = s;
    }

    public short getYear() {
        return this.year;
    }

    public void setYear(short s) {
        this.year = s;
    }

    public byte getMonth() {
        return this.month;
    }

    public void setMonth(byte by) {
        this.month = by;
    }

    public byte getDay() {
        return this.day;
    }

    public void setDay(byte by) {
        this.day = by;
    }

    public byte getHour() {
        return this.hour;
    }

    public void setHour(byte by) {
        this.hour = by;
    }

    public byte getMinute() {
        return this.minute;
    }

    public void setMinute(byte by) {
        this.minute = by;
    }

    public byte getSecond() {
        return this.second;
    }

    public void setSecond(byte by) {
        this.second = by;
    }

    public byte getReserved2() {
        return this.reserved2;
    }

    public void setReserved2(byte by) {
        this.reserved2 = by;
    }

    public Object getObjTRSRVR_LOG() {
        return this.objTRSRVR_LOG;
    }

    public void setObjTRSRVR_LOG(Object object) {
        this.objTRSRVR_LOG = object;
    }
}

