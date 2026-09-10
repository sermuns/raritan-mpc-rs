/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.smartcard;

import com.raritan.smartcard.CardAccessErrorsListener;
import com.raritan.smartcard.SmartCardCore;
import com.raritan.smartcard.SmartCardException;
import com.raritan.smartcard.SmartCardIncompatibleProtoException;
import com.raritan.smartcard.SmartCardReaderListener;
import com.raritan.smartcard.SmartCardReaderSession;
import com.raritan.smartcard.SmartCardSessionEventsListener;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.MessageFormat;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import nn.pp.common.smartcard.SmartCardBean;
import nn.pp.common.smartcard.SmartCardDialogAdapter;
import nn.pp.common.smartcard.SmartCardErrorsAndMessagesHandler;
import nn.pp.common.smartcard.SmartCardReaderDialog;
import nn.pp.common.ui.helpers.SwingWorker;
import nn.pp.core.NotificationEvent;
import nn.pp.core.NotificationListener;
import nn.pp.core.T;
import nn.pp.ext.devPref.DevicePrefs;

public class MountInProgressDialog
implements SmartCardDialogAdapter {
    private final SmartCardBean smartCardBean;
    private final String cardReaderName;
    private final SmartCardCore smartCardCore;
    private final String title;
    private final CancelAction cancelAction = new CancelAction();
    private final PropertyChangeListener cardAndReaderStateTracker = new CardAndReaderStateTracker();
    private final PropertyChangeListener notificationHandler = new NotificationHandler();
    private final PropertyChangeListener smartCardSessionEventsHandler = new SmartCardSessionEventsHandler();
    private final boolean autoMode;
    private static final String AUTO_MOUNT_MESSAGE = T._("Mount selected card reader automatically on connection to targets.");
    private static final int DIALOG_WIDTH = 440;
    private JProgressBar progressBar;
    private JCheckBox autoMount;
    private MountCardReaderWorker worker;
    private final JDialog dlg;

    public MountInProgressDialog(Dialog dialog, SmartCardBean smartCardBean, SmartCardCore smartCardCore, String string) {
        this.dlg = new JDialog(dialog);
        this.title = dialog.getTitle();
        this.smartCardBean = smartCardBean;
        this.cardReaderName = string;
        this.smartCardCore = smartCardCore;
        this.autoMode = false;
        this.init();
    }

    public MountInProgressDialog(Frame frame, SmartCardBean smartCardBean, SmartCardCore smartCardCore, String string) {
        this.dlg = new JDialog(frame);
        this.title = frame.getTitle();
        this.smartCardBean = smartCardBean;
        this.cardReaderName = string;
        this.smartCardCore = smartCardCore;
        this.autoMode = true;
        this.init();
    }

    public MountInProgressDialog(JDialog jDialog, String string, SmartCardBean smartCardBean, SmartCardCore smartCardCore, String string2) {
        this.dlg = jDialog;
        this.title = string;
        this.smartCardBean = smartCardBean;
        this.cardReaderName = string2;
        this.smartCardCore = smartCardCore;
        this.autoMode = true;
        this.init();
    }

    private void init() {
        this.dlg.setResizable(false);
        this.dlg.setTitle(MessageFormat.format(T._("Mount {0}"), this.cardReaderName));
        this.dlg.setModal(true);
        this.dlg.setDefaultCloseOperation(0);
        this.dlg.getContentPane().add(this.createContentPanel());
        this.dlg.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                MountInProgressDialog.this.closeAction();
            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                MountInProgressDialog.this.cancelAction.actionPerformed(null);
            }
        });
    }

    public void startMounting() {
        assert (SwingUtilities.isEventDispatchThread());
        OkAction okAction = new OkAction();
        okAction.actionPerformed(null);
    }

    @Override
    public void setVisible(boolean bl) {
        if (bl) {
            this.smartCardBean.addPropertyChangeListener("QUIT_NOTIFICATION", this.notificationHandler);
            this.smartCardBean.addPropertyChangeListener("DISCONNECTED", this.smartCardSessionEventsHandler);
            this.smartCardBean.addPropertyChangeListener("CARD_READER_STATUS", this.cardAndReaderStateTracker);
            this.smartCardBean.addPropertyChangeListener("CARD_READER_MOUNTED", this.smartCardSessionEventsHandler);
        }
        this.dlg.setVisible(bl);
    }

    private void closeAction() {
        this.dlg.setVisible(false);
        this.smartCardBean.removePropertyChangeListener("CARD_READER_STATUS", this.cardAndReaderStateTracker);
        this.smartCardBean.removePropertyChangeListener("QUIT_NOTIFICATION", this.notificationHandler);
        this.smartCardBean.removePropertyChangeListener("CARD_READER_MOUNTED", this.smartCardSessionEventsHandler);
        this.smartCardBean.removePropertyChangeListener("DISCONNECTED", this.smartCardSessionEventsHandler);
    }

    public void pack() {
        this.dlg.pack();
    }

    public void dispose() {
        this.dlg.dispose();
    }

    public void setLocationRelativeTo(Component component) {
        this.dlg.setLocationRelativeTo(component);
    }

    private JPanel createContentPanel() {
        JPanel jPanel = new JPanel(){

            @Override
            public Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                return new Dimension(dimension.width > 440 ? dimension.width : 440, dimension.height);
            }
        };
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = -1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        gridBagConstraints.fill = 2;
        JLabel jLabel = new JLabel(T._("Progress:"));
        jPanel.add((Component)jLabel, gridBagConstraints);
        this.progressBar = new JProgressBar();
        gridBagConstraints.insets = new Insets(5, 10, 0, 10);
        jPanel.add((Component)this.progressBar, gridBagConstraints);
        if (!this.isAutoMode()) {
            this.autoMount = new JCheckBox(AUTO_MOUNT_MESSAGE);
            this.autoMount.setVerticalTextPosition(1);
            gridBagConstraints.insets = new Insets(10, 10, 0, 10);
            jPanel.add((Component)this.autoMount, gridBagConstraints);
        }
        gridBagConstraints.insets = new Insets(15, 10, 10, 10);
        gridBagConstraints.fill = 0;
        gridBagConstraints.anchor = 13;
        jPanel.add((Component)this.createButtonPanel(), gridBagConstraints);
        return jPanel;
    }

    public JPanel createButtonPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1, this.isAutoMode() ? 1 : 2, 5, 0));
        JButton jButton = new JButton(this.cancelAction);
        if (!this.isAutoMode()) {
            JButton jButton2 = new JButton(new OkAction());
            jPanel.add(jButton2);
            this.dlg.getRootPane().setDefaultButton(jButton2);
        }
        jPanel.add(jButton);
        return jPanel;
    }

    private boolean isAutoMode() {
        return this.autoMode;
    }

    private void handleMountFailure() {
        if (this.isAutoMode()) {
            MountInProgressDialog.saveNoneAsCardReaderName(this.smartCardBean.getHost());
        }
    }

    static void saveNoneAsCardReaderName(String string) {
        DevicePrefs devicePrefs = DevicePrefs.getNode(string);
        if (devicePrefs != null) {
            devicePrefs.setCardReaderName(null);
            devicePrefs.exportPreferences(string);
        }
    }

    private class CardAndReaderStateTracker
    implements PropertyChangeListener {
        private CardAndReaderStateTracker() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("CARD_READER_STATUS")) {
                MountInProgressDialog.this.dlg.dispose();
                MountInProgressDialog.this.handleMountFailure();
                MountInProgressDialog.this.smartCardBean.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.CARD_READER_DOES_NOT_EXIST, null, MountInProgressDialog.this.dlg.getOwner(), MountInProgressDialog.this.title);
            }
        }
    }

    private class SmartCardSessionEventsHandler
    implements PropertyChangeListener {
        private SmartCardSessionEventsHandler() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("CARD_READER_MOUNTED")) {
                MountInProgressDialog.this.dlg.dispose();
                MountInProgressDialog.this.smartCardBean.setSession(MountInProgressDialog.this.smartCardBean.getTransientSession());
                MountInProgressDialog.this.worker = null;
                if (!MountInProgressDialog.this.isAutoMode()) {
                    DevicePrefs devicePrefs = DevicePrefs.getNode(MountInProgressDialog.this.smartCardBean.getHost());
                    if (MountInProgressDialog.this.autoMount.isSelected()) {
                        if (devicePrefs == null) {
                            devicePrefs = new DevicePrefs();
                        }
                        devicePrefs.setCardReaderName(MountInProgressDialog.this.smartCardBean.getCardReaderName());
                        devicePrefs.exportPreferences(MountInProgressDialog.this.smartCardBean.getHost());
                    } else if (devicePrefs != null && devicePrefs.getCardReaderName() != null) {
                        devicePrefs.setCardReaderName(null);
                        devicePrefs.exportPreferences(MountInProgressDialog.this.smartCardBean.getHost());
                    }
                }
            } else if (propertyChangeEvent.getPropertyName().equals("DISCONNECTED")) {
                MountInProgressDialog.this.dlg.dispose();
                Object object = propertyChangeEvent.getNewValue();
                if (object != null) {
                    if (object instanceof IOException) {
                        MountInProgressDialog.this.smartCardBean.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.COMMUNICATION_ERROR_OCCURED, object, MountInProgressDialog.this.dlg.getOwner(), MountInProgressDialog.this.title);
                    } else {
                        MountInProgressDialog.this.smartCardBean.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.UNKNOWN_ERROR, object, MountInProgressDialog.this.dlg.getOwner(), MountInProgressDialog.this.title);
                    }
                }
            }
        }
    }

    private class NotificationHandler
    implements PropertyChangeListener {
        private NotificationHandler() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("QUIT_NOTIFICATION")) {
                MountInProgressDialog.this.dlg.dispose();
                MountInProgressDialog.this.smartCardBean.notificationReceived((NotificationEvent)propertyChangeEvent.getNewValue(), MountInProgressDialog.this.dlg.getOwner(), MountInProgressDialog.this.title);
            }
        }
    }

    private class MountCardReaderWorker
    extends SwingWorker {
        private final SmartCardReaderSession session;
        private final SmartCardReaderListener scrl;
        private final NotificationListener nl;
        private final CardAccessErrorsListener cael;
        private final SmartCardSessionEventsListener scsel;
        private final SmartCardBean smartCardBean;
        private boolean cancelled;

        public MountCardReaderWorker(SmartCardReaderSession smartCardReaderSession, SmartCardReaderListener smartCardReaderListener, NotificationListener notificationListener, CardAccessErrorsListener cardAccessErrorsListener, SmartCardSessionEventsListener smartCardSessionEventsListener, SmartCardBean smartCardBean) {
            this.session = smartCardReaderSession;
            this.cael = cardAccessErrorsListener;
            this.scrl = smartCardReaderListener;
            this.scsel = smartCardSessionEventsListener;
            this.nl = notificationListener;
            this.smartCardBean = smartCardBean;
        }

        @Override
        public Object construct() {
            try {
                this.session.start(this.scrl, this.cael, this.nl, this.scsel, this.smartCardBean.getWorkstationUnlockDetector());
            }
            catch (SmartCardException smartCardException) {
                return smartCardException;
            }
            catch (IOException iOException) {
                return iOException;
            }
            catch (Exception exception) {
                return exception;
            }
            return this.session;
        }

        @Override
        public void finished() {
            if (this.isCancelled()) {
                return;
            }
            Object object = this.get();
            if (object instanceof Exception) {
                MountInProgressDialog.this.dlg.dispose();
                SmartCardReaderDialog.cleanState(this.smartCardBean);
                if (object instanceof SmartCardIncompatibleProtoException) {
                    this.smartCardBean.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.INCOMPATIBLE_VERSION_OF_DEVICE_PROTOCOL, object, MountInProgressDialog.this.dlg.getOwner(), MountInProgressDialog.this.title);
                } else if (object instanceof IOException) {
                    this.smartCardBean.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.COMMUNICATION_ERROR_OCCURED, object, MountInProgressDialog.this.dlg.getOwner(), MountInProgressDialog.this.title);
                } else {
                    this.smartCardBean.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.UNKNOWN_ERROR, object, MountInProgressDialog.this.dlg.getOwner(), MountInProgressDialog.this.title);
                }
            }
        }

        private boolean isCancelled() {
            return this.cancelled;
        }

        private void setCancelled(boolean bl) {
            this.cancelled = bl;
        }
    }

    private class CancelAction
    extends AbstractAction {
        public CancelAction() {
            super(T._("Cancel"));
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (MountInProgressDialog.this.worker != null) {
                MountInProgressDialog.this.worker.setCancelled(true);
                MountInProgressDialog.this.smartCardBean.cancelSession();
                SmartCardReaderDialog.cleanState(MountInProgressDialog.this.smartCardBean);
            }
            MountInProgressDialog.this.dlg.dispose();
        }
    }

    private class OkAction
    extends AbstractAction {
        public OkAction() {
            super(T._("OK"));
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            this.setEnabled(false);
            MountInProgressDialog.this.progressBar.setIndeterminate(true);
            MountInProgressDialog.this.smartCardBean.setCardReaderName(MountInProgressDialog.this.cardReaderName);
            if (!MountInProgressDialog.this.isAutoMode()) {
                MountInProgressDialog.this.autoMount.setEnabled(false);
            }
            SmartCardReaderSession smartCardReaderSession = MountInProgressDialog.this.smartCardBean.getRdmSession() != null ? MountInProgressDialog.this.smartCardCore.createCardReaderSessionWithRdmSessionID(MountInProgressDialog.this.cardReaderName, MountInProgressDialog.this.smartCardBean.getHost(), MountInProgressDialog.this.smartCardBean.getPort(), MountInProgressDialog.this.smartCardBean.isSslMode(), MountInProgressDialog.this.smartCardBean.getRfbSessionId(), MountInProgressDialog.this.smartCardBean.getMsindex(), MountInProgressDialog.this.smartCardBean.getRdmSession(), MountInProgressDialog.this.smartCardBean.getTag()) : MountInProgressDialog.this.smartCardCore.createCardReaderSessionWithEricKey(MountInProgressDialog.this.cardReaderName, MountInProgressDialog.this.smartCardBean.getHost(), MountInProgressDialog.this.smartCardBean.getPort(), MountInProgressDialog.this.smartCardBean.isSslMode(), MountInProgressDialog.this.smartCardBean.getRfbSessionId(), MountInProgressDialog.this.smartCardBean.getMsindex(), MountInProgressDialog.this.smartCardBean.getEricKey(), MountInProgressDialog.this.smartCardBean.getTag());
            MountInProgressDialog.this.smartCardBean.setTransientSession(smartCardReaderSession);
            MountInProgressDialog.this.worker = new MountCardReaderWorker(smartCardReaderSession, MountInProgressDialog.this.smartCardBean.getNewSmartCardReaderListener(smartCardReaderSession), MountInProgressDialog.this.smartCardBean.getNewNotificationListener(smartCardReaderSession), MountInProgressDialog.this.smartCardBean.getNewCardAccessErrorsListener(smartCardReaderSession), MountInProgressDialog.this.smartCardBean.getNewSmartCardSessionEventsListener(smartCardReaderSession), MountInProgressDialog.this.smartCardBean);
            MountInProgressDialog.this.worker.start();
        }
    }
}

