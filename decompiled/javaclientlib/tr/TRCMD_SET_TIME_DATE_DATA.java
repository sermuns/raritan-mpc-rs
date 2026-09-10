/*
 * Decompiled with CFR 0.152.
 */
package javaclientlib.tr;

import javaclientlib.tr.TRCOMMAND;

public class TRCMD_SET_TIME_DATE_DATA
extends TRCOMMAND {
    private int year_OFFSET = 4;
    private int month_OFFSET = this.year_OFFSET + 2;
    private int day_OFFSET = this.month_OFFSET + 1;
    private int dayOfWeek_OFFSET = this.day_OFFSET + 1;
    private int hour_OFFSET = this.dayOfWeek_OFFSET + 1;
    private int minute_OFFSET = this.hour_OFFSET + 1;
    private int second_OFFSET = this.minute_OFFSET + 1;
    private int setTime_OFFSET = this.second_OFFSET + 1;
    private int setDate_OFFSET = this.setTime_OFFSET + 1;
    public static final short CMD_LEN = 14;

    public TRCMD_SET_TIME_DATE_DATA(byte[] byArray) {
        super(byArray);
    }

    public TRCMD_SET_TIME_DATE_DATA() {
        super((short)14);
    }

    public short getYear() {
        return this.getShort(this.year_OFFSET);
    }

    public void setYear(short s) {
        this.setShort(s, this.year_OFFSET);
    }

    public byte getMonth() {
        return this.getByte(this.month_OFFSET);
    }

    public void setMonth(byte by) {
        this.setByte(by, this.month_OFFSET);
    }

    public byte getDay() {
        return this.getByte(this.day_OFFSET);
    }

    public void setDay(byte by) {
        this.setByte(by, this.day_OFFSET);
    }

    public byte getDayOfWeek() {
        return this.getByte(this.dayOfWeek_OFFSET);
    }

    public void setDayOfWeek(byte by) {
        this.setByte(by, this.dayOfWeek_OFFSET);
    }

    public byte getHour() {
        return this.getByte(this.hour_OFFSET);
    }

    public void setHour(byte by) {
        this.setByte(by, this.hour_OFFSET);
    }

    public byte getMinute() {
        return this.getByte(this.minute_OFFSET);
    }

    public void setMinute(byte by) {
        this.setByte(by, this.minute_OFFSET);
    }

    public byte getSecond() {
        return this.getByte(this.second_OFFSET);
    }

    public void setSecond(byte by) {
        this.setByte(by, this.second_OFFSET);
    }

    public byte getSetTime() {
        return this.getByte(this.setTime_OFFSET);
    }

    public void setSetTime(byte by) {
        this.setByte(by, this.setTime_OFFSET);
    }

    public byte getSetDate() {
        return this.getByte(this.setDate_OFFSET);
    }

    public void setSetDate(byte by) {
        this.setByte(by, this.setDate_OFFSET);
    }
}

