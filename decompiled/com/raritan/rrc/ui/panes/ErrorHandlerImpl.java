/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.rrc.ui.panes;

import com.raritan.rrc.ui.RRCScreenContext;
import com.raritan.rrc.ui.rfbbridge.RFBView;
import com.raritan.rrc.util.MPCUtil;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javaclientlib.utils.RRCLogger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ErrorHandlerImpl {
    private static ErrorHandlerImpl errorHandlerImpl;
    protected static ResourceBundle bundle;
    private static RRCScreenContext scrContext;

    private ErrorHandlerImpl() {
    }

    public static synchronized void init(ResourceBundle resourceBundle, RRCScreenContext rRCScreenContext) {
        ErrorHandlerImpl.getInstance();
        bundle = resourceBundle;
        scrContext = rRCScreenContext;
    }

    public static synchronized ErrorHandlerImpl getInstance() {
        if (errorHandlerImpl == null) {
            errorHandlerImpl = new ErrorHandlerImpl();
        }
        return errorHandlerImpl;
    }

    private String replaceTokens(String string, String[] stringArray) {
        if (stringArray != null && stringArray.length > 0) {
            return MessageFormat.format(string, stringArray);
        }
        RRCLogger.log(300, -1, "Tokens are null");
        return null;
    }

    public void handleError(int n, Component component, RFBView rFBView, String string) {
        this.showMessage(this.getFormatedMessage(n, rFBView), component, 0, bundle.getString("optionpane.error.title") + " " + string, rFBView);
    }

    public void handleError(int n, Component component, RFBView rFBView) {
        this.showMessage(this.getFormatedMessage(n, rFBView), component, 0, bundle.getString("optionpane.error.title"), rFBView);
    }

    private String getFormatedMessage(int n, RFBView rFBView) {
        String string = Integer.toHexString(n);
        String string2 = "RFB_PROTO_ERR." + string;
        String string3 = "[0x" + string + "]: ";
        RRCLogger.log(300, 512, "errorCode: " + n + ", bundleKey: " + string2);
        string3 = string3 + this.getErrorMessage(string2);
        if (rFBView != null) {
            string3 = this.replaceTokens(string3, rFBView.getTokens(new int[]{n}));
        } else {
            RRCLogger.log(300, -1, "IErrorHandlerCallback is null");
        }
        return string3;
    }

    public void handleError(int n, Component component) {
        String string = Integer.toHexString(n);
        String string2 = "RFB_PROTO_ERR." + string;
        String string3 = "[0x" + string + "]: ";
        RRCLogger.log(300, 512, "errorCode: " + n + ", bundleKey: " + string2);
        string3 = string3 + this.getErrorMessage(string2);
        this.showMessage(string3, component, 0, bundle.getString("optionpane.error.title"), null);
    }

    public void handleError(String string, Component component) {
        this.showMessage(this.getErrorMessage(string), component, 0, bundle.getString("optionpane.error.title"), null);
    }

    public void handleScanError(int n, Component component, String[] stringArray) {
        String string = Integer.toHexString(n);
        String string2 = "RFB_PROTO_ERR." + string;
        String string3 = "[0x" + string + "]: ";
        RRCLogger.log(300, 512, "errorCode: " + n + ", bundleKey: " + string2);
        string3 = string3 + this.getErrorMessage(string2);
        if (stringArray.length > 0) {
            String string4 = ErrorHandlerImpl.getPortNumbersAsString(stringArray);
            String[] stringArray2 = new String[]{string4};
            string3 = string3 + this.replaceTokens(string3, stringArray2);
        }
    }

    private String getErrorMessage(String string) {
        if (bundle == null) {
            RRCLogger.log(300, 512, "bundle is null");
            return string;
        }
        try {
            bundle.getString(string);
            return bundle.getString(string);
        }
        catch (MissingResourceException missingResourceException) {
            RRCLogger.log(300, 512, "No mapping found for key " + string);
            RRCLogger.logException(missingResourceException);
            return bundle.getString("RFB_PROTO_UNKNOWN_ERROR");
        }
    }

    private void showMessage(final String string, Component component, final int n, final String string2, final RFBView rFBView) {
        if (string != null) {
            boolean bl = component == scrContext.getApplication().getContentPane();
            final JOptionPane jOptionPane = new JOptionPane(string, n);
            JDialog jDialog = jOptionPane.createDialog(component, string2);
            if (component != null) {
                jDialog.setLocationRelativeTo(component);
            }
            jDialog.addWindowListener(new WindowAdapter(){

                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    jOptionPane.setValue(0);
                }
            });
            jDialog.setAlwaysOnTop(true);
            jDialog.setModal(true);
            jDialog.setResizable(false);
            jDialog.setDefaultCloseOperation(2);
            jDialog.setFocusable(true);
            scrContext.getApplication().addDisposedShell(jDialog);
            jDialog.setVisible(true);
            if (bl && (!(jOptionPane.getValue() instanceof Integer) || (Integer)jOptionPane.getValue() != 0) && component != scrContext.getApplication().getContentPane()) {
                jDialog.dispose();
                scrContext.getApplication().addDisposedShell(jDialog);
                SwingUtilities.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        ErrorHandlerImpl.this.showMessage(string, scrContext.getApplication().getContentPane(), n, string2, rFBView);
                    }
                });
            } else if (rFBView != null) {
                JOptionPane.getFrameForComponent(rFBView).toFront();
            }
        } else {
            RRCLogger.log(300, -1, "message is null, not displaying dialog");
        }
    }

    public void handleWarning(int n, Component component, RFBView rFBView) {
        this.showMessage(this.getFormatedMessage(n, rFBView), component, 2, bundle.getString("optionpane.warning.tite"), rFBView);
    }

    public void handleInfo(int n, Component component, RFBView rFBView) {
        this.showMessage(this.getFormatedMessage(n, rFBView), component, 1, bundle.getString("optionpane.info.title"), rFBView);
    }

    public boolean showErrorWithOptionToRemember(Component component, String string, String string2) {
        Component component2 = component;
        JOptionPane jOptionPane = new JOptionPane();
        JCheckBox jCheckBox = ErrorHandlerImpl.getCheckBox(jOptionPane);
        jOptionPane.setMessage(new Object[]{string, jCheckBox});
        jOptionPane.setMessageType(0);
        JButton jButton = new JButton("OK");
        JDialog jDialog = jOptionPane.createDialog(component, string2);
        MPCUtil.jre17WorkaroundInheritAlwaysOnTop(jDialog);
        final JDialog jDialog2 = jDialog;
        jButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jDialog2.dispose();
            }
        });
        jOptionPane.setOptions(new Object[]{jButton});
        jOptionPane.setOptionType(0);
        jDialog.setVisible(true);
        boolean bl = (Boolean)jOptionPane.getInputValue();
        return bl;
    }

    static JCheckBox getCheckBox(final JOptionPane jOptionPane) {
        JCheckBox jCheckBox = new JCheckBox(bundle.getString("DoNotDisplayAgain"));
        ChangeListener changeListener = new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                JCheckBox jCheckBox = (JCheckBox)changeEvent.getSource();
                jOptionPane.setInputValue(jCheckBox.isSelected());
            }
        };
        jCheckBox.addChangeListener(changeListener);
        return jCheckBox;
    }

    private static String getPortNumbersAsString(String[] stringArray) {
        int n;
        String string = "";
        for (n = 0; n < stringArray.length; ++n) {
            string = string + "," + stringArray[n];
        }
        n = string.length();
        string = string.substring(1, n - 1);
        return string;
    }
}

