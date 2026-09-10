/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  sun.misc.BASE64Encoder
 */
package nn.pp.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import nn.pp.common.CommonFunctions;
import nn.pp.logging.RemoteConsoleLogger;
import sun.misc.BASE64Encoder;

public class KXHttpConnection {
    private String userName = null;
    private String password = null;
    private String httpURL = null;
    private boolean isCC = false;
    private LinkedHashMap<String, String> paramValueMap = new LinkedHashMap();
    private static String PARAM_NAME = "param name=";
    private static String ENDING_QUOTE = "\"";
    private static String PARAM_VALUE = "value=";
    private static String TITLE_APP_ASP = "/title_app.asp";
    private static String BANNER_PAGE = "/sec_banner.asp";
    private static String BANNER_LOG_PAGE = "/sec_banner_mpc.asp";
    private static String BANNER_CONTENTS_PAGE = "/sec_banner_agreement.asp";

    public KXHttpConnection(String string, String string2, String string3, boolean bl) {
        if (string == null || string2 == null || string3 == null) {
            throw new NullPointerException("All the parameters are necessary");
        }
        this.userName = string;
        this.password = string2;
        this.httpURL = string3;
        this.isCC = bl;
    }

    public KXHttpConnection(String string, String string2, String string3) {
        if (string == null || string2 == null || string3 == null) {
            throw new NullPointerException("All the parameters are necessary");
        }
        this.userName = string;
        this.password = string2;
        this.httpURL = string3;
    }

    private static void exceptionHandler(Exception exception, String string) {
        if (exception instanceof MalformedURLException) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "MalformedURLException trying to " + string + ".", exception);
        } else if (exception instanceof NoSuchAlgorithmException) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "NoSuchAlgorithmException trying to " + string + ".", exception);
        } else if (exception instanceof KeyManagementException) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "KeyManagementException trying to " + string + ".", exception);
        } else if (exception instanceof ProtocolException) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "ProtocolException trying to " + string + ".", exception);
        } else if (exception instanceof IOException) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "IOException trying to " + string + ".", exception);
        } else {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "Unanticipated Exception type trying to " + string + ".", exception);
        }
    }

    private static HttpsURLConnection getHttpsURLConnection(String string, String string2, String string3, String string4, String string5) throws IOException, ProtocolException, MalformedURLException, KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection httpsURLConnection = null;
        URL uRL = new URL(string + string2);
        TrustManager[] trustManagerArray = new TrustManager[]{new DummyTrustManager()};
        SSLContext sSLContext = SSLContext.getInstance("TLSv1");
        sSLContext.init(null, trustManagerArray, null);
        httpsURLConnection = (HttpsURLConnection)uRL.openConnection();
        httpsURLConnection.setSSLSocketFactory(sSLContext.getSocketFactory());
        httpsURLConnection.setHostnameVerifier(new DummyHostNameVerifier());
        httpsURLConnection.setRequestMethod(string3);
        httpsURLConnection.setAllowUserInteraction(false);
        httpsURLConnection.setDoInput(true);
        httpsURLConnection.setDoOutput(true);
        httpsURLConnection.setUseCaches(false);
        if (string4 != null && string5 != null) {
            String string6 = string4 + ":" + string5;
            String string7 = new BASE64Encoder(){

                protected int bytesPerLine() {
                    return 250;
                }
            }.encode(string6.getBytes());
            httpsURLConnection.setRequestProperty("Authorization", "Basic " + string7);
        }
        return httpsURLConnection;
    }

    public LinkedHashMap<String, String> getAppletParameterMap() throws IOException, ProtocolException, MalformedURLException, KeyManagementException, NoSuchAlgorithmException {
        String string = null;
        HttpsURLConnection httpsURLConnection = null;
        try {
            httpsURLConnection = KXHttpConnection.getHttpsURLConnection(this.httpURL, TITLE_APP_ASP, "GET", this.userName, this.password);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            KXHttpConnection.exceptionHandler(exception, "get applet parameter map");
        }
        if (httpsURLConnection == null) {
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
        while ((string = bufferedReader.readLine()) != null) {
            this.parseLine(string);
        }
        bufferedReader.close();
        httpsURLConnection.disconnect();
        return this.paramValueMap;
    }

    public boolean changePwd(String string, String string2, String string3, String string4, boolean bl) {
        String string5 = null;
        StringBuffer stringBuffer = new StringBuffer();
        HttpsURLConnection httpsURLConnection = null;
        String string6 = bl ? "/pwchangeforced.asp?pw_change_forced=yes&FV_0_umpwchange=" + URLEncoder.encode(string2) + "&FV_1_umpwchange=" + URLEncoder.encode(string3) + "&FV_2_umpwchange=" + URLEncoder.encode(string4) + "&action_apply=1&__templates__=umpwchange" : "/pwchange.asp?FV_0_umpwchange=" + URLEncoder.encode(string2) + "&FV_1_umpwchange=" + URLEncoder.encode(string3) + "&FV_2_umpwchange=" + URLEncoder.encode(string4) + "&action_apply=1&__templates__=umpwchange";
        try {
            httpsURLConnection = KXHttpConnection.getHttpsURLConnection(this.httpURL, string6, "GET", this.userName, this.password);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            while ((string5 = bufferedReader.readLine()) != null) {
                stringBuffer.append(string5);
            }
            bufferedReader.close();
        }
        catch (Exception exception) {
            KXHttpConnection.exceptionHandler(exception, "change " + (bl ? "expired " : "") + "password.  Message: " + exception.getMessage());
        }
        if (httpsURLConnection == null) {
            return false;
        }
        httpsURLConnection.disconnect();
        if (stringBuffer == null) {
            return false;
        }
        return stringBuffer.toString().indexOf("ERIC_RESPONSE_ERROR") < 0;
    }

    public static String getArbitraryURL(String string, String string2, String string3) {
        StringBuilder stringBuilder;
        block9: {
            String string4 = null;
            stringBuilder = new StringBuilder("");
            HttpsURLConnection httpsURLConnection = null;
            try {
                httpsURLConnection = KXHttpConnection.getHttpsURLConnection(string, string2, "GET", null, null);
                if (httpsURLConnection != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream(), "UTF-8"));
                    while ((string4 = bufferedReader.readLine()) != null) {
                        stringBuilder.append(string4 + "\n");
                    }
                    bufferedReader.close();
                    httpsURLConnection.disconnect();
                }
            }
            catch (IOException iOException) {
                if (!string2.equals(CommonFunctions.getLicenseFilePath())) break block9;
                try {
                    httpsURLConnection = KXHttpConnection.getHttpsURLConnection(string, CommonFunctions.getOldLicenseFilePath(), "GET", null, null);
                    if (httpsURLConnection != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream(), "UTF-8"));
                        while ((string4 = bufferedReader.readLine()) != null) {
                            stringBuilder.append(string4 + "\n");
                        }
                        bufferedReader.close();
                        httpsURLConnection.disconnect();
                    }
                }
                catch (Exception exception) {
                    KXHttpConnection.exceptionHandler(exception, string3);
                }
            }
            catch (Exception exception) {
                KXHttpConnection.exceptionHandler(exception, string3);
            }
        }
        return stringBuilder.toString();
    }

    public BannerSettings getSecurityBannerSettings() throws ProtocolException, MalformedURLException, KeyManagementException, NoSuchAlgorithmException {
        BannerSettings bannerSettings = new BannerSettings();
        StringBuffer stringBuffer = new StringBuffer();
        String string = null;
        HttpsURLConnection httpsURLConnection = null;
        try {
            httpsURLConnection = KXHttpConnection.getHttpsURLConnection(this.httpURL, BANNER_CONTENTS_PAGE, "GET", this.userName, this.password);
        }
        catch (Exception exception) {
            KXHttpConnection.exceptionHandler(exception, "get Banner Page Settings");
        }
        if (httpsURLConnection == null) {
            return null;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            while ((string = bufferedReader.readLine()) != null) {
                stringBuffer.append(string + "\n");
            }
            String string2 = new String(stringBuffer);
            String string3 = this.parseBannerMessage(string2);
            bannerSettings.setRsaText(string3);
            String string4 = this.parseBannerTitle(string2);
            bannerSettings.setRsaTitle(string4);
            RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "Security Banner Text :" + bannerSettings.getRsaText());
            bufferedReader.close();
            httpsURLConnection.disconnect();
            string2 = null;
            stringBuffer = null;
        }
        catch (IOException iOException) {
            KXHttpConnection.exceptionHandler(iOException, "get Banner Page Settings");
            return bannerSettings;
        }
        return bannerSettings;
    }

    private String parseBannerMessage(String string) {
        String string2 = "";
        try {
            int n = string.indexOf("<textarea name=\"_message_text\"");
            String string3 = string.substring(n);
            int n2 = string3.indexOf("</textarea>");
            String string4 = string3.substring(0, n2 + 11);
            int n3 = string4.indexOf("</textarea>");
            string2 = string4.substring(string4.indexOf(">") + 1, n3);
        }
        catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "StringIndexOutOfBoundsException trying to get security banner message.", stringIndexOutOfBoundsException);
            return "";
        }
        return string2;
    }

    private String parseBannerTitle(String string) {
        String string2 = "";
        try {
            int n = string.indexOf("<td class=\"wdgt_header\">");
            String string3 = string.substring(n);
            int n2 = string3.indexOf("</td>");
            String string4 = string3.substring(0, n2);
            string2 = string4.substring(string4.indexOf(">") + 2);
        }
        catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "StringIndexOutOfBoundsException trying to get security banner title.", stringIndexOutOfBoundsException);
            return "";
        }
        return string2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void callSecurityBannerAcceptPage(int n) {
        String string = null;
        HttpURLConnection httpURLConnection = null;
        try {
            String string2 = n == 1 ? "accept" : "decline";
            URL uRL = new URL(this.httpURL + BANNER_LOG_PAGE + "?" + string2);
            try {
                httpURLConnection = KXHttpConnection.getHttpsURLConnection(this.httpURL, BANNER_LOG_PAGE + "?" + string2, "GET", this.userName, this.password);
            }
            catch (Exception exception) {
                KXHttpConnection.exceptionHandler(exception, "get Banner Page Settings");
            }
            if (httpURLConnection == null) {
                RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "CONNECTION IS NULL WHILE ACCESSING SECURITY BANNER LOG PAGE");
            } else {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((string = bufferedReader.readLine()) != null) {
                }
                bufferedReader.close();
                RemoteConsoleLogger.getInstance().getLogger().log(Level.INFO, "User accepted Security Banner:" + string2.toUpperCase());
            }
        }
        catch (Exception exception) {
            RemoteConsoleLogger.getInstance().getLogger().log(Level.WARNING, "Exception occured when calling Security Banner Log page.", exception);
        }
        finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    private void parseLine(String string) {
        int n = -1;
        int n2 = -1;
        int n3 = -1;
        int n4 = -1;
        n = string.indexOf(PARAM_NAME);
        n3 = string.indexOf(PARAM_VALUE);
        if (n >= 0 && n3 >= 0) {
            n2 = string.indexOf(ENDING_QUOTE, n += PARAM_NAME.length() + 1);
            n4 = string.indexOf(ENDING_QUOTE, n3 += PARAM_VALUE.length() + 1);
            if (n2 >= 0 && n4 >= 0) {
                String string2 = string.substring(n, n2);
                String string3 = string.substring(n3, n4);
                this.paramValueMap.put(string2, string3);
            }
        }
    }

    public static void main(String[] stringArray) {
        try {
            KXHttpConnection kXHttpConnection = new KXHttpConnection("admin", "111", "https://192.168.52.141");
            System.out.println(kXHttpConnection.getAppletParameterMap());
            BannerSettings bannerSettings = kXHttpConnection.getSecurityBannerSettings();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public class BannerSettings {
        private String rsaText = "";
        private String rsaTitle = "";

        public String getRsaText() {
            return this.rsaText;
        }

        public void setRsaText(String string) {
            this.rsaText = string;
        }

        public String getRsaTitle() {
            return this.rsaTitle;
        }

        public void setRsaTitle(String string) {
            this.rsaTitle = string;
        }
    }

    static class DummyTrustManager
    implements X509TrustManager {
        DummyTrustManager() {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509CertificateArray, String string) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509CertificateArray, String string) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    static class DummyHostNameVerifier
    implements HostnameVerifier {
        DummyHostNameVerifier() {
        }

        @Override
        public boolean verify(String string, SSLSession sSLSession) {
            return true;
        }
    }
}

