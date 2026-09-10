/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.smartcard;

import com.raritan.smartcard.SmartCardCore;
import com.raritan.smartcard.SmartCardReaderSession;
import java.awt.Component;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import nn.pp.common.smartcard.MountInProgressDialog;
import nn.pp.common.smartcard.SmartCardBean;
import nn.pp.common.smartcard.SmartCardDialogAdapter;
import nn.pp.common.smartcard.SmartCardErrorsAndMessagesHandler;
import nn.pp.core.NotificationEvent;
import nn.pp.core.T;

public class SmartCardReaderDialog
implements SmartCardDialogAdapter {
    private static final String NONE_SELECTED = T._("None");
    private static final String vmLimitationMessage = "<html><font color=\"#0000ff\"><b><i>" + T._("Connecting a Card Reader limits Virtual Media to one Mass Storage Device.") + "<br>" + T._("Also, if you intend on mounting Virtual Media or Audio, do so before connecting Card Reader.") + "<i/></b></font></html>";
    private final JLabel selectedCardReader = new JLabel(NONE_SELECTED){

        @Override
        public void setText(String string) {
            super.setText(string);
            this.setToolTipText(string);
        }
    };
    private final JList cardReaderList = new JList();
    private final JButton refreshButton = new JButton();
    private final SmartCardCore smartCardCore;
    private final PropertyChangeListener mountUnmountListener = new CardReaderMountUnMountListener();
    private final PropertyChangeListener cardAndReaderStateTracker = new CardAndReaderStateTracker();
    private final PropertyChangeListener notificationHandler = new NotificationHandler();
    private final PropertyChangeListener smartCardSessionEventsHandler = new SmartCardSessionEventsHandler();
    private final PropertyChangeListener cardAccessErrorHandler = new CardAccessErrorHandler();
    private final RemoveReinsertAction removeReinsertAction = new RemoveReinsertAction();
    private final MountAction mountAction = new MountAction();
    private SmartCardBean smartcardBean;
    private final JDialog dlg;
    private AbstractAction closeAction = new AbstractAction(T._("Close")){

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SmartCardReaderDialog.this.closeAction();
        }
    };
    private AbstractAction refreshAction = new AbstractAction(T._("Refresh List")){

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SmartCardReaderDialog.this.loadCardReaders();
        }
    };
    private AbstractAction unmountAction = new AbstractAction(T._("Un-Mount")){

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SmartCardReaderDialog.this.smartcardBean.quitSession();
            MountInProgressDialog.saveNoneAsCardReaderName(SmartCardReaderDialog.this.smartcardBean.getHost());
        }
    };

    public SmartCardReaderDialog(Frame frame, SmartCardCore smartCardCore) {
        this.dlg = new JDialog(frame, T._("Select Card Reader"));
        this.smartCardCore = smartCardCore;
        this.init();
    }

    public SmartCardReaderDialog(JDialog jDialog, SmartCardCore smartCardCore) {
        this.dlg = jDialog;
        this.dlg.setTitle(T._("Select Card Reader"));
        this.smartCardCore = smartCardCore;
        this.init();
    }

    private void init() {
        this.dlg.setModal(true);
        this.dlg.setResizable(false);
        this.dlg.setDefaultCloseOperation(0);
        this.dlg.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                SmartCardReaderDialog.this.closeAction();
            }
        });
        this.addComponents();
        this.cardReaderList.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                SmartCardReaderDialog.this.mountAction.setCardReaderName((String)SmartCardReaderDialog.this.cardReaderList.getSelectedValue());
            }
        });
    }

    private void addComponents() {
        this.dlg.getContentPane().add(this.createTopPanel());
        ((JComponent)this.dlg.getContentPane()).getInputMap(2).put(KeyStroke.getKeyStroke(27, 0), "SmartCardReaderDialogClose");
        ((JComponent)this.dlg.getContentPane()).getActionMap().put("SmartCardReaderDialogClose", this.closeAction);
    }

    private JPanel createTopPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        gridBagConstraints.fill = 2;
        this.selectedCardReader.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(T._("Card Reader Currently Mounted")), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jPanel.add((Component)this.selectedCardReader, gridBagConstraints);
        final JScrollPane jScrollPane = new JScrollPane(this.cardReaderList);
        final JPanel jPanel2 = this.createButtonPanel();
        JPanel jPanel3 = new JPanel(){

            @Override
            public Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                return new Dimension(jPanel2.getPreferredSize().width, dimension.height + jScrollPane.getHorizontalScrollBar().getPreferredSize().height);
            }
        };
        jPanel3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(T._("Card Readers Detected")), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jPanel3.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.fill = 1;
        jPanel3.add((Component)jScrollPane, gridBagConstraints2);
        this.cardReaderList.setVisibleRowCount(8);
        this.cardReaderList.setModel(new CardReadersListModel(Arrays.asList("M")));
        this.cardReaderList.setSelectionMode(0);
        gridBagConstraints.gridy = 1;
        jPanel.add((Component)jPanel3, gridBagConstraints);
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(15, 10, 0, 10);
        jPanel.add((Component)new JLabel(vmLimitationMessage), gridBagConstraints);
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(15, 10, 10, 10);
        gridBagConstraints.fill = 0;
        gridBagConstraints.anchor = 13;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        return jPanel;
    }

    public JPanel createButtonPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1, 5, 5, 0));
        JButton jButton = new JButton();
        jButton.setAction(this.mountAction);
        this.refreshButton.setAction(this.refreshAction);
        JButton jButton2 = new JButton();
        jButton2.setAction(this.removeReinsertAction);
        JButton jButton3 = new JButton();
        jButton3.setAction(this.unmountAction);
        JButton jButton4 = new JButton();
        jButton4.setAction(this.closeAction);
        jPanel.add(jButton);
        jPanel.add(this.refreshButton);
        jPanel.add(jButton2);
        jPanel.add(jButton3);
        jPanel.add(jButton4);
        this.dlg.getRootPane().setDefaultButton(jButton);
        return jPanel;
    }

    @Override
    public void setVisible(boolean bl) {
        if (bl) {
            this.loadCardReaders();
            this.smartcardBean.addPropertyChangeListener("CARD_READER_SESSION", this.mountUnmountListener);
            this.enableDisableOnMountUnMount(this.smartcardBean.getSession());
            this.smartcardBean.addPropertyChangeListener("CARD_CARD_STATUS", this.cardAndReaderStateTracker);
            this.removeReinsertAction.setCardInserted(this.smartcardBean.isCardInserted());
            this.smartcardBean.addPropertyChangeListener("QUIT_NOTIFICATION", this.notificationHandler);
            this.smartcardBean.addPropertyChangeListener("NOTIFICATION_FROM_SERVER", this.notificationHandler);
            this.smartcardBean.addPropertyChangeListener("DISCONNECTED", this.smartCardSessionEventsHandler);
            this.smartcardBean.addPropertyChangeListener("NO_PROTO_SPPORTED", this.cardAccessErrorHandler);
            this.dlg.setLocationRelativeTo(this.dlg.getOwner());
            if (this.cardReaderList.getModel().getSize() > 0) {
                this.cardReaderList.requestFocus();
            } else {
                this.refreshButton.requestFocus();
            }
        }
        this.dlg.setVisible(bl);
    }

    public void pack() {
        this.dlg.pack();
    }

    public void dispose() {
        this.dlg.dispose();
    }

    private void closeAction() {
        this.smartcardBean.removePropertyChangeListener("CARD_READER_SESSION", this.mountUnmountListener);
        this.smartcardBean.removePropertyChangeListener("CARD_CARD_STATUS", this.cardAndReaderStateTracker);
        this.smartcardBean.removePropertyChangeListener("QUIT_NOTIFICATION", this.notificationHandler);
        this.smartcardBean.removePropertyChangeListener("NOTIFICATION_FROM_SERVER", this.notificationHandler);
        this.smartcardBean.removePropertyChangeListener("DISCONNECTED", this.smartCardSessionEventsHandler);
        this.smartcardBean.removePropertyChangeListener("NO_PROTO_SPPORTED", this.cardAccessErrorHandler);
        this.smartcardBean = null;
        this.setVisible(false);
    }

    private void loadCardReaders() {
        List<String> list = this.smartCardCore.getAvailableSmartCardReaders();
        this.cardReaderList.setModel(new CardReadersListModel(list));
        if (this.cardReaderList.getModel().getSize() > 0) {
            this.cardReaderList.setSelectedIndex(0);
        }
    }

    private void enableDisableOnMountUnMount(SmartCardReaderSession smartCardReaderSession) {
        boolean bl = smartCardReaderSession != null;
        this.unmountAction.setEnabled(bl);
        this.removeReinsertAction.setMounted(bl);
        this.mountAction.setMounted(bl);
        if (bl) {
            this.selectedCardReader.setText(this.smartcardBean.getCardReaderName());
        } else {
            this.selectedCardReader.setText(NONE_SELECTED);
            this.enableDisableOnCardStatus(false);
        }
    }

    private void enableDisableOnCardStatus(boolean bl) {
        this.removeReinsertAction.setCardInserted(bl);
    }

    public void setSmartcardBean(SmartCardBean smartCardBean) {
        this.smartcardBean = smartCardBean;
    }

    static void cleanState(SmartCardBean smartCardBean) {
        smartCardBean.setTransientSession(null);
        smartCardBean.setCardReaderName(null);
        smartCardBean.resetSmartCardReaderListener();
        smartCardBean.resetNotificationListener();
        smartCardBean.resetSmartCardSessionEventsListener();
        smartCardBean.resetCardAccessErrorsListener();
    }

    private class CardAccessErrorHandler
    implements PropertyChangeListener {
        private CardAccessErrorHandler() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("NO_PROTO_SPPORTED")) {
                SmartCardReaderDialog.this.smartcardBean.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.NO_SUPPORTED_PROTOCOL, null, SmartCardReaderDialog.this.dlg, SmartCardReaderDialog.this.dlg.getTitle());
            }
        }
    }

    private class SmartCardSessionEventsHandler
    implements PropertyChangeListener {
        private SmartCardSessionEventsHandler() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            Object object;
            if (propertyChangeEvent.getPropertyName().equals("DISCONNECTED") && (object = propertyChangeEvent.getNewValue()) != null) {
                if (object instanceof IOException) {
                    SmartCardReaderDialog.this.smartcardBean.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.COMMUNICATION_ERROR_OCCURED, object, SmartCardReaderDialog.this.dlg, SmartCardReaderDialog.this.dlg.getTitle());
                } else {
                    SmartCardReaderDialog.this.smartcardBean.errorOrMessage(SmartCardErrorsAndMessagesHandler.ErrorCodes.UNKNOWN_ERROR, object, SmartCardReaderDialog.this.dlg, SmartCardReaderDialog.this.dlg.getTitle());
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
            if (propertyChangeEvent.getPropertyName().equals("QUIT_NOTIFICATION") || propertyChangeEvent.getPropertyName().equals("NOTIFICATION_FROM_SERVER")) {
                SmartCardReaderDialog.this.smartcardBean.notificationReceived((NotificationEvent)propertyChangeEvent.getNewValue(), SmartCardReaderDialog.this.dlg, SmartCardReaderDialog.this.dlg.getTitle());
            }
        }
    }

    private class CardAndReaderStateTracker
    implements PropertyChangeListener {
        private CardAndReaderStateTracker() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("CARD_CARD_STATUS")) {
                SmartCardReaderDialog.this.enableDisableOnCardStatus((Boolean)propertyChangeEvent.getNewValue());
            }
        }
    }

    private class CardReaderMountUnMountListener
    implements PropertyChangeListener {
        private CardReaderMountUnMountListener() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("CARD_READER_SESSION")) {
                SmartCardReaderDialog.this.enableDisableOnMountUnMount((SmartCardReaderSession)propertyChangeEvent.getNewValue());
            }
        }
    }

    private class MountAction
    extends AbstractAction {
        private boolean mounted;
        private String cardReaderName;

        public MountAction() {
            super(T._("Mount..."));
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            MountInProgressDialog mountInProgressDialog = new MountInProgressDialog(SmartCardReaderDialog.this.dlg, SmartCardReaderDialog.this.smartcardBean, SmartCardReaderDialog.this.smartCardCore, this.cardReaderName);
            mountInProgressDialog.pack();
            mountInProgressDialog.setLocationRelativeTo(SmartCardReaderDialog.this.dlg);
            mountInProgressDialog.setVisible(true);
            mountInProgressDialog.dispose();
        }

        public void setMounted(boolean bl) {
            this.mounted = bl;
            this.evaluateEnbled();
        }

        public void setCardReaderName(String string) {
            this.cardReaderName = string;
            this.evaluateEnbled();
        }

        private void evaluateEnbled() {
            this.setEnabled(!this.mounted && this.cardReaderName != null);
        }
    }

    private class RemoveReinsertAction
    extends AbstractAction {
        private boolean mounted;
        private boolean cardInserted;

        public RemoveReinsertAction() {
            super(T._("Remove/Reinsert Card"));
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            SmartCardReaderDialog.this.smartcardBean.getSession().simulateRemoveAndReinsert();
        }

        public void setMounted(boolean bl) {
            this.mounted = bl;
            this.evaluateEnabled();
        }

        public void setCardInserted(boolean bl) {
            this.cardInserted = bl;
            this.evaluateEnabled();
        }

        private void evaluateEnabled() {
            this.setEnabled(this.mounted && this.cardInserted);
        }
    }

    private static class CardReadersListModel
    extends AbstractListModel {
        private final List<String> cardReaders;

        public CardReadersListModel(List<String> list) {
            Collections.sort(list);
            this.cardReaders = list;
        }

        @Override
        public Object getElementAt(int n) {
            return this.cardReaders.get(n);
        }

        @Override
        public int getSize() {
            int n = this.cardReaders.size();
            if (n > 8) {
                return 8;
            }
            return n;
        }
    }
}

