/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.components;

import com.raritan.tools.resources.RaritanPropertyResourceBundle;
import com.raritan.tools.resources.RaritanResourceBundle;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.displays.ShellFocusObserver;
import com.raritan.tools.util.Util;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class CommonPopups {
    public static final int POPUP_RESULT_UNKNOWN = 0;
    public static final int POPUP_RESULT_CANCEL = 1;
    public static final int POPUP_RESULT_OK = 2;

    public static void showCommandResultErrorMessage(Object[] objectArray, AbstractDisplay abstractDisplay, ScreenContext screenContext) {
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < objectArray.length; ++i) {
            stringBuffer.append(objectArray[i]);
            stringBuffer.append("\n");
        }
        CommonPopups.showCommandResultErrorMessage(stringBuffer.toString(), abstractDisplay, screenContext);
    }

    public static void showCommandResultErrorMessage(String string, AbstractDisplay abstractDisplay, ScreenContext screenContext) {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        JOptionPane jOptionPane = new JOptionPane(string, 0, -1, null, new Object[]{raritanPropertyResourceBundle.getString("basescreen.command.ok.text")});
        CommonPopups.addKeyListener(new EnterKeyListener(), jOptionPane.getComponents());
        screenContext.getPanelMediator().showDialogOnTop(jOptionPane, raritanPropertyResourceBundle.getString("optionpane.error.title"), abstractDisplay);
    }

    public static void showErrorMessage(String string, Component component, ScreenContext screenContext, String string2) {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        JOptionPane jOptionPane = new JOptionPane(string, 0, -1, null, new Object[]{raritanPropertyResourceBundle.getString("basescreen.command.ok.text")});
        CommonPopups.addKeyListener(new EnterKeyListener(), jOptionPane.getComponents());
        String string3 = raritanPropertyResourceBundle.getString("optionpane.error.title") + " " + string2;
        if (component instanceof AbstractDisplay) {
            screenContext.getPanelMediator().showDialogOnTop(jOptionPane, string3, (AbstractDisplay)component);
        } else {
            JDialog jDialog = jOptionPane.createDialog(component, string3);
            Util.jre17WorkaroundInheritAlwaysOnTop(jDialog);
            jDialog.setLocationRelativeTo(component);
            jDialog.setModal(true);
            jDialog.setVisible(true);
            jDialog.dispose();
        }
    }

    public static int showExitConfirmationDialog(AbstractDisplay abstractDisplay, ScreenContext screenContext) {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        return CommonPopups.showConfirmationDialog(raritanPropertyResourceBundle.getString("confirmation.dialog.command.execution.title"), raritanPropertyResourceBundle.getString("confirmation.dialog.command.execution.text"), abstractDisplay, screenContext);
    }

    public static int showExitConfirmationDialog(Component component, ScreenContext screenContext) {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        JOptionPane jOptionPane = new JOptionPane(raritanPropertyResourceBundle.getString("confirmation.dialog.command.execution.text"), 2, 0, null, new Object[]{raritanPropertyResourceBundle.getString("basescreen.command.yes.text"), raritanPropertyResourceBundle.getString("basescreen.command.no.text")});
        CommonPopups.addKeyListener(new EnterKeyListener(), jOptionPane.getComponents());
        Object object = screenContext.getPanelMediator().showDialogOnTop(jOptionPane, raritanPropertyResourceBundle.getString("confirmation.dialog.command.execution.title"), component);
        if (object != null) {
            if (object.equals(raritanPropertyResourceBundle.getString("basescreen.command.yes.text"))) {
                return 2;
            }
            return 1;
        }
        return 1;
    }

    public static int showConfirmationDialog(String string, Object object, AbstractDisplay abstractDisplay, ScreenContext screenContext) {
        return CommonPopups.showConfirmationDialog(string, object, abstractDisplay, screenContext, null);
    }

    public static int showConfirmationDialog(String string, Object object, AbstractDisplay abstractDisplay, ScreenContext screenContext, String[] stringArray) {
        Object[] objectArray;
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        if (stringArray != null && stringArray.length < 2) {
            throw new IllegalArgumentException("Buttons array should contain exactly 2 elements");
        }
        if (stringArray != null) {
            Object[] objectArray2 = new Object[2];
            objectArray2[0] = raritanPropertyResourceBundle.getString(stringArray[0]);
            objectArray = objectArray2;
            objectArray2[1] = raritanPropertyResourceBundle.getString(stringArray[1]);
        } else {
            Object[] objectArray3 = new Object[2];
            objectArray3[0] = raritanPropertyResourceBundle.getString("basescreen.command.yes.text");
            objectArray = objectArray3;
            objectArray3[1] = raritanPropertyResourceBundle.getString("basescreen.command.no.text");
        }
        JOptionPane jOptionPane = new JOptionPane(object, 2, 0, null, objectArray);
        CommonPopups.addKeyListener(new EnterKeyListener(), jOptionPane.getComponents());
        Object object2 = screenContext.getPanelMediator().showDialogOnTop(jOptionPane, string, abstractDisplay);
        if (object2 != null) {
            if (object2.equals(stringArray != null && stringArray.length > 0 ? raritanPropertyResourceBundle.getString(stringArray[0]) : raritanPropertyResourceBundle.getString("basescreen.command.yes.text"))) {
                return 2;
            }
            return 1;
        }
        return 1;
    }

    public static int showInfoDialog(String string, Object object, AbstractDisplay abstractDisplay, ScreenContext screenContext) {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        JOptionPane jOptionPane = new JOptionPane(object, 1, -1, null, new Object[]{raritanPropertyResourceBundle.getString("basescreen.command.ok.text")});
        CommonPopups.addKeyListener(new EnterKeyListener(), jOptionPane.getComponents());
        Object object2 = screenContext.getPanelMediator().showDialogOnTop(jOptionPane, string, abstractDisplay);
        return 2;
    }

    public static int showFileOverwriteConfirmationDialog(AbstractDisplay abstractDisplay, ScreenContext screenContext) {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        return CommonPopups.showConfirmationDialog(raritanPropertyResourceBundle.getString("filebrowser.save.fileexists.title"), raritanPropertyResourceBundle.getString("filebrowser.save.fileexists.message"), abstractDisplay, screenContext);
    }

    public static void showWarningDialog(String string, Object object, Component component, ScreenContext screenContext) {
        RaritanPropertyResourceBundle raritanPropertyResourceBundle = RaritanResourceBundle.getResourceBundle(screenContext.getLocale());
        JOptionPane jOptionPane = new JOptionPane(object, 2, -1, null, new Object[]{raritanPropertyResourceBundle.getString("basescreen.command.ok.text")});
        JDialog jDialog = jOptionPane.createDialog(component, string);
        Util.jre17WorkaroundInheritAlwaysOnTop(jDialog);
        if (component != null) {
            jDialog.setLocationRelativeTo(component);
        }
        jDialog.setModal(true);
        ShellFocusObserver.addShellVisible(jDialog, screenContext);
        jDialog.setVisible(true);
        ShellFocusObserver.removeShellVisible(jDialog, screenContext);
        jDialog.dispose();
    }

    private static void addKeyListener(KeyListener keyListener, Component[] componentArray) {
        if (componentArray == null) {
            return;
        }
        for (int i = 0; i < componentArray.length; ++i) {
            if (componentArray[i] instanceof JButton) {
                ((JButton)componentArray[i]).enableInputMethods(false);
                ((JButton)componentArray[i]).addKeyListener(keyListener);
                continue;
            }
            if (!(componentArray[i] instanceof Container)) continue;
            CommonPopups.addKeyListener(keyListener, ((Container)componentArray[i]).getComponents());
        }
    }

    private static class EnterKeyListener
    extends KeyAdapter {
        private EnterKeyListener() {
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 10 && keyEvent.getSource() instanceof JButton) {
                ((JButton)keyEvent.getSource()).doClick();
            }
        }
    }
}

