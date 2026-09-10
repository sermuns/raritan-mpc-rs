/*
 * Decompiled with CFR 0.152.
 */
package amp.powerboard.component;

import amp.powerboard.component.Constants;
import java.util.StringTokenizer;

public class SystemStatus {
    private String unitId = "";
    private int averagePower;
    private int apparentPower;
    private float trueRMSVoltage;
    private float trueRMSCurrent;
    private float maxDetected;
    private float internalTemp;
    private String circuitBreaker;

    public SystemStatus(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, Constants.DL_CR);
        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken();
            if (string2.indexOf(Constants.ST_ID) != -1) {
                this.unitId = string2.substring(Constants.ST_ID.length()).trim();
                if (this.unitId == null) {
                    this.unitId = "";
                }
            }
            if (string2.indexOf(Constants.ST_AVG_PWR) != -1 && string2.indexOf(Constants.ST_APP_PWR) != -1) {
                this.averagePower = Integer.parseInt(string2.substring(Constants.ST_AVG_PWR.length(), string2.indexOf(Constants.ST_WATTS) - 1).trim());
                this.apparentPower = Integer.parseInt(string2.substring(string2.indexOf(Constants.ST_APP_PWR) + Constants.ST_APP_PWR.length(), string2.indexOf(Constants.ST_VA) - 1).trim());
            }
            if (string2.indexOf(Constants.ST_RMS_VOLT) != -1) {
                this.trueRMSVoltage = new Float(string2.substring(Constants.ST_RMS_VOLT.length(), string2.indexOf(Constants.ST_VOLTS) - 1).trim()).floatValue();
            }
            if (string2.indexOf(Constants.ST_RMS_CURRENT) != -1) {
                this.trueRMSCurrent = new Float(string2.substring(Constants.ST_RMS_CURRENT.length(), string2.indexOf(Constants.ST_AMPS) - 1).trim()).floatValue();
            }
            if (string2.indexOf(Constants.ST_MAX_DETECTED) != -1) {
                this.maxDetected = new Float(string2.substring(string2.indexOf(Constants.ST_MAX_DETECTED) + Constants.ST_MAX_DETECTED.length(), string2.lastIndexOf(Constants.ST_AMPS) - 1).trim()).floatValue();
            }
            if (string2.indexOf(Constants.ST_INT_TEMP) != -1) {
                this.internalTemp = new Float(string2.substring(Constants.ST_INT_TEMP.length(), string2.indexOf(Constants.ST_CELCIUS) - 1).trim()).floatValue();
            }
            if (string2.indexOf(Constants.ST_CIRCUIT_BR) == -1) continue;
            this.circuitBreaker = string2.substring(Constants.ST_CIRCUIT_BR.length()).trim();
        }
    }

    public String getCircuitBreaker() {
        return this.circuitBreaker;
    }

    public void setCircuitBreaker(String string) {
        this.circuitBreaker = string;
    }

    public float getInternalTemp() {
        return this.internalTemp;
    }

    public void setInternalTemp(float f) {
        this.internalTemp = f;
    }

    public float getMaxDetected() {
        return this.maxDetected;
    }

    public void setMaxDetected(float f) {
        this.maxDetected = f;
    }

    public float getTrueRMSCurrent() {
        return this.trueRMSCurrent;
    }

    public void setTrueRMSCurrent(float f) {
        this.trueRMSCurrent = this.trueRMSCurrent;
    }

    public float getTrueRMSVoltage() {
        return this.trueRMSVoltage;
    }

    public void setTrueRMSVoltage(float f) {
        this.trueRMSVoltage = f;
    }

    public int getApparentPower() {
        return this.apparentPower;
    }

    public void setApparentPower(int n) {
        this.apparentPower = n;
    }

    public int getAveragePower() {
        return this.averagePower;
    }

    public void setAveragePower(int n) {
        this.averagePower = this.averagePower;
    }

    public String getUnitId() {
        return this.unitId;
    }

    public void setUnitId(String string) {
        this.unitId = this.unitId;
    }
}

