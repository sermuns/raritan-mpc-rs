/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.clientapi.event;

import amp.powerboard.clientapi.event.CDataEvent;
import amp.powerboard.clientapi.event.CInternalTimeEvent;

public class CGetTimeEvent
extends CDataEvent {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int sec;

    public CGetTimeEvent(int n, int n2, int n3, int n4, int n5, int n6, boolean bl, String string) {
        super(47, bl);
        this.year = n;
        this.month = n2;
        this.day = n3;
        this.hour = n4;
        this.min = n5;
        this.sec = n6;
        this.lockerName = string;
    }

    public CGetTimeEvent(boolean bl, String string) {
        super(47, bl);
        this.lockerName = string;
    }

    public int getYear() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMin() {
        return this.min;
    }

    public int getSec() {
        return this.sec;
    }

    public void setTimeData(CInternalTimeEvent cInternalTimeEvent) {
        this.year = cInternalTimeEvent.getYear();
        this.month = cInternalTimeEvent.getMonth();
        this.day = cInternalTimeEvent.getDay();
        this.hour = cInternalTimeEvent.getHour();
        this.min = cInternalTimeEvent.getMin();
        this.sec = cInternalTimeEvent.getSec();
    }
}

