/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.smartcard;

import com.raritan.smartcard.CardAccessErrorsListener;
import com.raritan.smartcard.SmartCardReaderListener;
import com.raritan.smartcard.SmartCardReaderSession;
import com.raritan.smartcard.SmartCardSessionEventsListener;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import nn.pp.common.smartcard.SmartCardErrorsAndMessagesHandler;
import nn.pp.core.INotificationEvent;
import nn.pp.core.NotificationEvent;
import nn.pp.core.NotificationListener;
import nn.pp.core.WorkstationUnlockDetector;

public class SmartCardBean
implements SmartCardErrorsAndMessagesHandler {
    public static final String PROPERTY_CARD_READER_MOUNTED = "CARD_READER_MOUNTED";
    public static final String PROPERTY_CARD_STATUS = "CARD_CARD_STATUS";
    public static final String PROPERTY_CARD_READER_SESSION = "CARD_READER_SESSION";
    public static final String PROPERTY_QUIT_ISSUED = "QUIT_ISSUED";
    public static final String PROPERTY_CANCEL_ISSUED = "CANCEL_ISSUED";
    public static final String PROPERTY_QUIT_NOTIFICATION_FROM_SERVER = "QUIT_NOTIFICATION";
    public static final String PROPERTY_DISCONNECTED = "DISCONNECTED";
    public static final String PROPERTY_CARD_READER_STATUS = "CARD_READER_STATUS";
    public static final String PROPERTY_NO_PROTO_SPPORTED = "NO_PROTO_SPPORTED";
    public static final String PROPERTY_NOTIFICATION_FROM_SERVER = "NOTIFICATION_FROM_SERVER";
    private final String host;
    private final int port;
    private final boolean sslMode;
    private final String rdmSession;
    private final String ericKey;
    private final WorkstationUnlockDetector workstationUnlockDetector;
    private int rfbSessionId;
    private int msindex;
    private String tag;
    private SmartCardReaderSession session;
    private SmartCardReaderSession transientSession;
    private JDialog statusDialog;
    private String cardReaderName;
    private final PropertyChangeSupport propChangeSupport;
    private Scrl scrl;
    private NotificationListener nl;
    private SmartCardSessionEventsListener scsel;
    private CardAccessErrorsListener cael;
    private Map<String, Stack<PropertyChangeListener>> listeners = new HashMap<String, Stack<PropertyChangeListener>>();
    private final SmartCardErrorsAndMessagesHandler smartCardErrorsAndMessagesHandler;

    public SmartCardBean(String string, int n, boolean bl, String string2, String string3, SmartCardErrorsAndMessagesHandler smartCardErrorsAndMessagesHandler, WorkstationUnlockDetector workstationUnlockDetector) {
        this.host = string;
        this.port = n;
        this.sslMode = bl;
        this.rdmSession = string2;
        this.ericKey = string3;
        this.smartCardErrorsAndMessagesHandler = smartCardErrorsAndMessagesHandler;
        this.workstationUnlockDetector = workstationUnlockDetector;
        this.propChangeSupport = new PropertyChangeSupport(this);
        this.propChangeSupport.addPropertyChangeListener(new LastInNotifier());
    }

    public SmartCardReaderSession getSession() {
        return this.session;
    }

    public void quitSession() {
        assert (SwingUtilities.isEventDispatchThread());
        SmartCardReaderSession smartCardReaderSession = this.getSession();
        if (smartCardReaderSession != null) {
            this.setSession(null);
            smartCardReaderSession.close();
            this.propChangeSupport.firePropertyChange(PROPERTY_QUIT_ISSUED, null, "Quit");
        }
    }

    public void cancelSession() {
        assert (SwingUtilities.isEventDispatchThread());
        SmartCardReaderSession smartCardReaderSession = this.getTransientSession();
        this.setTransientSession(null);
        smartCardReaderSession.close();
        this.propChangeSupport.firePropertyChange(PROPERTY_CANCEL_ISSUED, null, "Cancel");
    }

    public void setSession(SmartCardReaderSession smartCardReaderSession) {
        if (smartCardReaderSession == this.session) {
            return;
        }
        this.setTransientSession(null);
        SmartCardReaderSession smartCardReaderSession2 = this.session;
        this.session = smartCardReaderSession;
        this.propChangeSupport.firePropertyChange(PROPERTY_CARD_READER_SESSION, smartCardReaderSession2, this.session);
    }

    public void setCardReaderName(String string) {
        this.cardReaderName = string;
    }

    public String getCardReaderName() {
        return this.cardReaderName;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public boolean isSslMode() {
        return this.sslMode;
    }

    public int getRfbSessionId() {
        return this.rfbSessionId;
    }

    public int getMsindex() {
        return this.msindex;
    }

    public String getRdmSession() {
        return this.rdmSession;
    }

    public String getEricKey() {
        return this.ericKey;
    }

    public String getTag() {
        return this.tag;
    }

    public JDialog getStatusDialog() {
        return this.statusDialog;
    }

    public void setStatusDialog(JDialog jDialog) {
        this.statusDialog = jDialog;
    }

    public void addPropertyChangeListener(String string, PropertyChangeListener propertyChangeListener) {
        if (string.equals(PROPERTY_QUIT_NOTIFICATION_FROM_SERVER) || string.equals(PROPERTY_NOTIFICATION_FROM_SERVER) || string.equals(PROPERTY_DISCONNECTED) || string.equals(PROPERTY_NO_PROTO_SPPORTED) || string.equals(PROPERTY_CARD_READER_STATUS)) {
            Stack<PropertyChangeListener> stack = this.listeners.get(string);
            if (stack == null) {
                stack = new Stack();
                this.listeners.put(string, stack);
            }
            stack.push(propertyChangeListener);
        } else {
            this.propChangeSupport.addPropertyChangeListener(string, propertyChangeListener);
        }
    }

    public void removePropertyChangeListener(String string, PropertyChangeListener propertyChangeListener) {
        if (string.equals(PROPERTY_QUIT_NOTIFICATION_FROM_SERVER) || string.equals(PROPERTY_NOTIFICATION_FROM_SERVER) || string.equals(PROPERTY_DISCONNECTED) || string.equals(PROPERTY_NO_PROTO_SPPORTED) || string.equals(PROPERTY_CARD_READER_STATUS)) {
            Stack<PropertyChangeListener> stack = this.listeners.get(string);
            if (stack != null) {
                stack.remove(propertyChangeListener);
                if (stack.isEmpty()) {
                    this.listeners.remove(string);
                }
            }
        } else {
            this.propChangeSupport.removePropertyChangeListener(string, propertyChangeListener);
        }
    }

    public SmartCardReaderListener getNewSmartCardReaderListener(SmartCardReaderSession smartCardReaderSession) {
        this.scrl = new Scrl(smartCardReaderSession);
        return this.scrl;
    }

    public NotificationListener getNewNotificationListener(SmartCardReaderSession smartCardReaderSession) {
        this.nl = new Nl(smartCardReaderSession);
        return this.nl;
    }

    public SmartCardSessionEventsListener getNewSmartCardSessionEventsListener(SmartCardReaderSession smartCardReaderSession) {
        this.scsel = new Scsel(smartCardReaderSession);
        return this.scsel;
    }

    public CardAccessErrorsListener getNewCardAccessErrorsListener(SmartCardReaderSession smartCardReaderSession) {
        this.cael = new Cael(smartCardReaderSession);
        return this.cael;
    }

    public void resetSmartCardReaderListener() {
        this.scrl = null;
    }

    public void resetNotificationListener() {
        this.nl = null;
    }

    public void resetSmartCardSessionEventsListener() {
        this.scsel = null;
    }

    public void resetCardAccessErrorsListener() {
        this.cael = null;
    }

    public boolean isCardInserted() {
        if (this.scrl != null) {
            return this.scrl.isCardInserted();
        }
        return false;
    }

    private boolean isCorrectSessionToFireListener(SmartCardReaderSession smartCardReaderSession) {
        return smartCardReaderSession == this.getSession() || smartCardReaderSession == this.getTransientSession();
    }

    public void setRfbSessionId(int n) {
        this.rfbSessionId = n;
    }

    public void setMsindex(int n) {
        this.msindex = n;
    }

    public void setTag(String string) {
        this.tag = string;
    }

    @Override
    public void errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes errorCodes, Object object, Window window, String string) {
        this.smartCardErrorsAndMessagesHandler.errorOrMessage(errorCodes, object, window, string);
    }

    @Override
    public void notificationReceived(NotificationEvent notificationEvent, Window window, String string) {
        this.smartCardErrorsAndMessagesHandler.notificationReceived(notificationEvent, window, string);
    }

    SmartCardReaderSession getTransientSession() {
        return this.transientSession;
    }

    void setTransientSession(SmartCardReaderSession smartCardReaderSession) {
        this.transientSession = smartCardReaderSession;
    }

    public String toString() {
        return "host : " + this.host + " port : " + this.port + " msindex : " + this.msindex + " rfbSessionId : " + this.rfbSessionId;
    }

    public WorkstationUnlockDetector getWorkstationUnlockDetector() {
        return this.workstationUnlockDetector;
    }

    private class LastInNotifier
    implements PropertyChangeListener {
        private LastInNotifier() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            Stack stack;
            String string = propertyChangeEvent.getPropertyName();
            SmartCardReaderSession smartCardReaderSession = SmartCardBean.this.getSession();
            SmartCardReaderSession smartCardReaderSession2 = SmartCardBean.this.getTransientSession();
            if (string.equals(SmartCardBean.PROPERTY_QUIT_NOTIFICATION_FROM_SERVER) || string.equals(SmartCardBean.PROPERTY_DISCONNECTED) || string.equals(SmartCardBean.PROPERTY_QUIT_ISSUED) || string.equals(SmartCardBean.PROPERTY_CANCEL_ISSUED) || string.equals(SmartCardBean.PROPERTY_CARD_READER_STATUS)) {
                SmartCardBean.this.setCardReaderName(null);
                SmartCardBean.this.setSession(null);
                SmartCardBean.this.setTransientSession(null);
                SmartCardBean.this.resetSmartCardReaderListener();
                SmartCardBean.this.resetNotificationListener();
                SmartCardBean.this.resetSmartCardSessionEventsListener();
                SmartCardBean.this.resetCardAccessErrorsListener();
            }
            if ((smartCardReaderSession != null || smartCardReaderSession2 != null) && (string.equals(SmartCardBean.PROPERTY_QUIT_NOTIFICATION_FROM_SERVER) || string.equals(SmartCardBean.PROPERTY_NOTIFICATION_FROM_SERVER) || string.equals(SmartCardBean.PROPERTY_DISCONNECTED) || string.equals(SmartCardBean.PROPERTY_NO_PROTO_SPPORTED) || string.equals(SmartCardBean.PROPERTY_CARD_READER_STATUS)) && (stack = (Stack)SmartCardBean.this.listeners.get(string)) != null) {
                ((PropertyChangeListener)stack.peek()).propertyChange(propertyChangeEvent);
            }
        }
    }

    private class Cael
    implements CardAccessErrorsListener {
        private final SmartCardReaderSession owningSession;

        public Cael(SmartCardReaderSession smartCardReaderSession) {
            this.owningSession = smartCardReaderSession;
        }

        @Override
        public void errorOnAccessingCard(String string) {
        }

        @Override
        public void noProtocolSupported(String string) {
            if (SmartCardBean.this.isCorrectSessionToFireListener(this.owningSession)) {
                SmartCardBean.this.propChangeSupport.firePropertyChange(SmartCardBean.PROPERTY_NO_PROTO_SPPORTED, null, string);
            }
        }
    }

    private class Scsel
    implements SmartCardSessionEventsListener {
        private final SmartCardReaderSession owningSession;

        public Scsel(SmartCardReaderSession smartCardReaderSession) {
            this.owningSession = smartCardReaderSession;
        }

        @Override
        public void cardReaderMounted(String string) {
            if (SmartCardBean.this.isCorrectSessionToFireListener(this.owningSession)) {
                SmartCardBean.this.propChangeSupport.firePropertyChange(SmartCardBean.PROPERTY_CARD_READER_MOUNTED, null, string);
            }
        }

        @Override
        public void disconnected(Exception exception) {
            if (SmartCardBean.this.isCorrectSessionToFireListener(this.owningSession)) {
                SmartCardBean.this.propChangeSupport.firePropertyChange(SmartCardBean.PROPERTY_DISCONNECTED, null, exception);
            }
        }
    }

    private class Nl
    implements NotificationListener {
        private final SmartCardReaderSession owningSession;

        public Nl(SmartCardReaderSession smartCardReaderSession) {
            this.owningSession = smartCardReaderSession;
        }

        @Override
        public void receivedNotification(INotificationEvent iNotificationEvent) {
            if (SmartCardBean.this.isCorrectSessionToFireListener(this.owningSession)) {
                if (iNotificationEvent.isQuit() || iNotificationEvent.isError()) {
                    SmartCardBean.this.propChangeSupport.firePropertyChange(SmartCardBean.PROPERTY_QUIT_NOTIFICATION_FROM_SERVER, null, iNotificationEvent);
                } else {
                    SmartCardBean.this.propChangeSupport.firePropertyChange(SmartCardBean.PROPERTY_NOTIFICATION_FROM_SERVER, null, iNotificationEvent);
                }
            }
        }

        @Override
        public void textNotification(String string) {
        }
    }

    private class Scrl
    implements SmartCardReaderListener {
        private boolean cardInserted;
        private boolean cardReaderRemoved;
        private final SmartCardReaderSession owningSession;

        public Scrl(SmartCardReaderSession smartCardReaderSession) {
            this.owningSession = smartCardReaderSession;
        }

        @Override
        public void cardInserted(String string) {
            this.cardInserted = true;
            if (SmartCardBean.this.isCorrectSessionToFireListener(this.owningSession)) {
                SmartCardBean.this.propChangeSupport.firePropertyChange(SmartCardBean.PROPERTY_CARD_STATUS, false, true);
            }
        }

        @Override
        public void cardReaderRemoved(String string) {
            this.cardReaderRemoved = true;
            if (SmartCardBean.this.isCorrectSessionToFireListener(this.owningSession)) {
                SmartCardBean.this.propChangeSupport.firePropertyChange(SmartCardBean.PROPERTY_CARD_READER_STATUS, true, false);
            }
        }

        @Override
        public void cardRemoved(String string) {
            this.cardInserted = false;
            if (SmartCardBean.this.isCorrectSessionToFireListener(this.owningSession)) {
                SmartCardBean.this.propChangeSupport.firePropertyChange(SmartCardBean.PROPERTY_CARD_STATUS, true, false);
            }
        }

        public boolean isCardInserted() {
            if (!this.cardReaderRemoved) {
                return this.cardInserted;
            }
            return false;
        }
    }
}

