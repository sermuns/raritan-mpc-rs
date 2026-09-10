/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.rfb.V01_22;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nn.pp.core.T;
import nn.pp.rccore.RCException;
import nn.pp.rccore.impl.rfb.RfbHandler;

public class RfbAuthenticatorV01_22 {
    private RfbHandler rfbHandler;
    private Logger logger;
    protected String username;
    protected String password;
    protected String httpSessionID;
    protected String rdmSessionID;
    private MessageDigest digest;
    private static final int AUTH_TYPE_UNKNOWN = -1;
    private static final int AUTH_TYPE_PLAIN = 0;
    private static final int AUTH_TYPE_MD5 = 1;
    private static final int AUTH_TYPE_HTTP_SESSION_ID = 2;
    private static final int AUTH_TYPE_RDM_SESSION_ID = 3;
    private int authCaps = 0;
    private int authMethod = -1;

    public RfbAuthenticatorV01_22(RfbHandler rfbHandler, Logger logger) {
        this.rfbHandler = rfbHandler;
        this.logger = logger;
    }

    public void setAuthParameters(int n, String string, String string2, String string3, String string4) {
        this.authCaps = n;
        this.username = string;
        this.password = string2;
        this.httpSessionID = string3;
        this.rdmSessionID = string4;
    }

    public void negotiateAuthentication() throws IOException, RCException {
        if (this.authMethod == -1 && (this.authCaps & 4) != 0 && this.username != null && this.password != null) {
            try {
                this.digest = MessageDigest.getInstance("MD5");
                this.logger.log(Level.FINE, T._("Using MD5 authentication"));
                this.authMethod = 1;
            }
            catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                this.logger.log(Level.WARNING, T._("MD5 not available."));
            }
        }
        if (this.authMethod == -1) {
            if ((this.authCaps & 2) != 0 && this.username != null && this.password != null) {
                this.logger.log(Level.FINE, T._("Using Plain authentication"));
                this.authMethod = 0;
            } else if ((this.authCaps & 0x10) != 0 && this.rdmSessionID != null) {
                this.logger.log(Level.FINE, T._("Using RDM Session ID authentication"));
                this.authMethod = 3;
            } else if ((this.authCaps & 1) != 0 && this.httpSessionID != null) {
                this.logger.log(Level.FINE, T._("Using HTTP Session ID authentication"));
                this.authMethod = 2;
            }
        }
        switch (this.authMethod) {
            case 0: {
                this.rfbHandler.writeLogin(this.username, 2, 0);
                break;
            }
            case 1: {
                this.rfbHandler.writeLogin(this.username, 4, 0);
                break;
            }
            case 3: {
                this.rfbHandler.writeLogin("super", 16, 0);
                break;
            }
            case 2: {
                this.rfbHandler.writeLogin("", 1, 0);
                break;
            }
            default: {
                throw new RCException(T._("No proper authentication scheme found"));
            }
        }
    }

    public void processChallenge(byte[] byArray) throws IOException, RCException {
        switch (this.authMethod) {
            case 0: {
                if (byArray != null) {
                    throw new RCException(T._("Protocol error: bad challenge size"));
                }
                this.rfbHandler.writeChallengeResponse(this.password);
                break;
            }
            case 1: {
                if (byArray != null) {
                    this.digest.update(byArray);
                }
                this.digest.update(this.password.getBytes("ISO-8859-1"));
                byte[] byArray2 = this.digest.digest();
                String string = this.rfbHandler.binToHex(byArray2);
                this.rfbHandler.writeChallengeResponse(string);
                break;
            }
            case 2: {
                if (byArray == null || byArray.length != 65) {
                    throw new RCException(T._("Protocol error: bad challenge size"));
                }
                String string = new String(byArray);
                String string2 = string.startsWith("{SHA256}") ? "SHA-256" : "MD5";
                byte[] byArray3 = new byte[64];
                System.arraycopy(byArray, 0, byArray3, 0, 64);
                String string3 = this.rfbHandler.getChallengeResponse(byArray3, string2);
                this.rfbHandler.writeChallengeResponse(string3 + '\u0000');
                break;
            }
            case 3: {
                if (byArray != null) {
                    throw new RCException(T._("Protocol error: bad challenge size"));
                }
                this.rfbHandler.writeChallengeResponse(this.rdmSessionID + '\u0000');
                break;
            }
            default: {
                throw new RCException(T._("No proper authentication scheme found"));
            }
        }
    }
}

