/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.util;

public class Constants {
    public static final String ENCRYPT_AUTH_SSL_DATA_NO = "512";
    public static final String ENCRYPT_AUTH_SSL_DATA = "1024";
    public static final String ENCRYPT_AUTH_SSL_DATA_SSL = "2048";
    public static final String PC_MODE_PRIVATE = "0";
    public static final String PC_MODE_PC_SHARE = "1";
    public static final String LINE_SPEED_AUTODETECT = "0";
    public static final String LINE_SPEED_10MBS_HALF_DUPLEX = "256";
    public static final String LINE_SPEED_10MBS_FULL_DUPLEX = "512";
    public static final String LINE_SPEED_100MBS_HALF_DUPLEX = "768";
    public static final String LINE_SPEED_100MBS_FULL_DUPLEX = "1024";
    public static String BANDWIDTH_NONE = "0";
    public static String BANDWIDTH_10000000 = "10000000";
    public static String BANDWIDTH_5000000 = "5000000";
    public static String BANDWIDTH_2000000 = "2000000";
    public static String BANDWIDTH_1000000 = "1000000";
    public static String BANDWIDTH_512000 = "512000";
    public static String BANDWIDTH_256000 = "256000";
    public static String BANDWIDTH_128000 = "128000";
    public static final String PASSWORDS_KEY = "l2Ge0FQQwlu2yGSF+Jh/nZdhntBUEMJb";
    public static String PERF_TIMEOUT_NONE = "0";
    public static String PERF_TIMEOUT_5_MIN = "5";
    public static String PERF_TIMEOUT_15_MIN = "15";
    public static String PERF_TIMEOUT_30_MIN = "30";
    public static String PERF_TIMEOUT_60_MIN = "60";
    public static String PERF_TIMEOUT_120_MIN = "120";
    public static final String SYSLOG_CATEGORY_NETWORK = "0";
    public static final String SYSLOG_CATEGORY_ADMIN = "1";
    public static final String SYSLOG_CATEGORY_ERROR = "2";
    public static final String SYSLOG_CATEGORY_TEXT = "3";
    public static final String SYSLOG_CATEGORY_SYSTEM = "4";
    public static final String SYSLOG_CATEGORY_ALL = "5";
    public static final String SYSLOG_PRIORITY_EMERGENCY = "0";
    public static final String SYSLOG_PRIORITY_ALERT = "1";
    public static final String SYSLOG_PRIORITY_CRITICAL = "2";
    public static final String SYSLOG_PRIORITY_ERROR = "3";
    public static final String SYSLOG_PRIORITY_WARNING = "4";
    public static final String SYSLOG_PRIORITY_NOTICE = "5";
    public static final String SYSLOG_PRIORITY_INFO = "6";
    public static final String SYSLOG_PRIORITY_DEBUG = "7";
    public static final String AUTH_TYPE_PAP = "0";
    public static final String AUTH_TYPE_CHAP = "4096";
    public static final String INTERFACE_ETHERNET = "Ethernet";
    public static final String INTERFACE_TCPIP = "TCPIP";
    public static final String INTERFACE_MODEM = "Modem";
    public static final String INTERFACE_FAILOVER = "Failover";
    public static final String REMOTEAUTH_LDAP_PROTOCOL = "LDAP";
    public static final String REMOTEAUTH_RADIUS_PROTOCOL = "RADIUS";
    public static final String REMOTEAUTH_LDAP_PROTOCOL_SECURE = "LDAPS";
    public static final String REMOTEAUTH_STANDARD_PORT = "1812";
    public static final String REMOTEAUTH_STANDARD_ACCOUNTING_PORT = "1813";
    public static final String REMOTEAUTH_LEGACY_PORT = "1645";
    public static final String REMOTEAUTH_LEGACY_ACCOUNTING_PORT = "1646";
    public static final String REMOTEAUTH_LDAP_DEFAULTPORT = "389";
    public static final String REMOTEAUTH_LDAP_DEFAULTPORT_SECURE = "636";
    public static String NETWORKCONFIG_DEFAULT_PORT = "5000";
    public static int DEFAULT_BROADCAST_PORT = 5000;
    public static int DEFAULT_HTTPS_PORT = 443;
    public static final int MIN_PORT_NUMBER = 0;
    public static final int MAX_PORT_NUMBER = 65535;
    public static final String DEFAULT_IP_ADDRESS = "0.0.0.0";
    public static final int NAME_MAXIMUM_LENGTH = 15;
    public static final int TIME_POWER_PORT_WAIT_TIMER = 5000;

    public static void setPort(String string, int n) {
        NETWORKCONFIG_DEFAULT_PORT = string;
        DEFAULT_BROADCAST_PORT = n;
    }
}

