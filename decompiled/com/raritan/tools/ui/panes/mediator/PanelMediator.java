/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.panes.mediator;

import com.raritan.tools.commands.CommandContext;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.components.RaritanDesktopPane;
import com.raritan.tools.ui.panes.LogPanel;
import com.raritan.tools.ui.panes.RaritanStatusBarPanel;
import com.raritan.tools.ui.panes.displays.AbstractDisplay;
import com.raritan.tools.ui.panes.displays.AbstractDisplayStrategy;
import com.raritan.tools.ui.panes.displays.Shell;
import com.raritan.tools.ui.panes.displays.ShellFocusObserver;
import com.raritan.tools.util.Util;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Hashtable;
import javaclientlib.utils.RRCLogger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public abstract class PanelMediator {
    protected static final boolean SHOW_IN_DIALOG = true;
    protected static final boolean SHOW_IN_CONTAINER = false;
    public static final String DEVICE_TABBED_PANEL = "DEVICE_TABBED_PANEL";
    public static final String USER_GROUP_TABBED_PANEL = "USER_GROUP_TABBED_PANEL";
    protected Container parent = null;
    private Hashtable panels = new Hashtable();
    protected Hashtable targetPanels = new Hashtable();
    protected Hashtable panelClasses = new Hashtable();
    protected ScreenContext scrContext;

    public PanelMediator(Container container, ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.parent = container;
        this.initPanels();
    }

    public void showPanel(CommandContext commandContext) {
        if (commandContext == null) {
            return;
        }
        this.scrContext.getLogger().logTextDebug(" Invoked with command key " + commandContext.getCommandKey());
        AbstractDisplay abstractDisplay = this.getTarget(commandContext.getCommandKey());
        if (abstractDisplay != null) {
            abstractDisplay.setCommandContext(commandContext);
            abstractDisplay.fillComponents(commandContext);
            if (abstractDisplay.isDialog()) {
                abstractDisplay.onActivateDisplay();
                Shell shell = abstractDisplay.getShell();
                shell.setCentered();
                shell.setVisible(true);
            } else {
                RaritanDesktopPane raritanDesktopPane = (RaritanDesktopPane)this.parent;
                raritanDesktopPane.addAbstractDisplayComponent(abstractDisplay);
            }
        }
        this.scrContext.getLogger().logTextDebug(" Finished");
    }

    public void hideAll() {
        for (Object v : this.panels.values()) {
            if (!(v instanceof AbstractDisplay) || !((AbstractDisplay)v).isDialog()) continue;
            ((AbstractDisplay)v).getShell().setVisible(false);
        }
    }

    public void showLogMessage(final CommandContext commandContext) {
        final AbstractDisplay abstractDisplay = this.getTarget(commandContext.getCommandKey());
        if (SwingUtilities.isEventDispatchThread()) {
            abstractDisplay.setCommandContext(commandContext);
        } else {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    abstractDisplay.setCommandContext(commandContext);
                }
            });
        }
        abstractDisplay.fillComponents(commandContext);
        this.parent.repaint();
    }

    public Container getParent() {
        return this.parent;
    }

    public void setParent(Container container) {
        this.parent = container;
    }

    public AbstractDisplay getLogPanel() {
        return this.getTarget("logCommand");
    }

    public AbstractDisplay getStatusPanel() {
        return this.getTarget("statusCommand");
    }

    public void showStatusPanel(boolean bl) {
        AbstractDisplay abstractDisplay = this.getStatusPanel();
        if (abstractDisplay != null) {
            abstractDisplay.setVisible(bl);
        }
    }

    protected void initPanels() {
        this.scrContext.getLogger().logTextInfo(" Invoked at " + Calendar.getInstance().getTime());
        this.initContainerPanels();
        this.initDialogPanels();
        this.scrContext.getLogger().logTextInfo(" Finished at " + Calendar.getInstance().getTime());
    }

    protected void initContainerPanels() {
        this.panelClasses.put("logCommand", new AbstractDisplayStrategy(LogPanel.class, false, this.scrContext));
        this.panelClasses.put("statusCommand", new AbstractDisplayStrategy(RaritanStatusBarPanel.class, false, this.scrContext));
    }

    protected abstract void initDialogPanels();

    public AbstractDisplay getTarget(String string) {
        AbstractDisplay abstractDisplay = null;
        AbstractDisplayStrategy abstractDisplayStrategy = (AbstractDisplayStrategy)this.panelClasses.get(string);
        if (abstractDisplayStrategy != null) {
            Frame frame;
            String string2 = abstractDisplayStrategy.getAbstractDisplayClass().getName();
            PanelKey panelKey = new PanelKey(string2, frame = JOptionPane.getFrameForComponent(this.scrContext.getApplication().getContentPane()));
            abstractDisplay = (AbstractDisplay)this.panels.get(panelKey);
            if (abstractDisplay == null) {
                abstractDisplay = (AbstractDisplay)this.panels.get(string2);
            }
            if (abstractDisplay == null) {
                try {
                    abstractDisplay = abstractDisplayStrategy.create();
                    if (!abstractDisplay.isNewPanel()) {
                        this.panels.put(abstractDisplay.isDialog() ? panelKey : string2, abstractDisplay);
                    }
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    RRCLogger.logException(noSuchMethodException);
                }
                catch (InstantiationException instantiationException) {
                    RRCLogger.logException(instantiationException);
                }
                catch (IllegalAccessException illegalAccessException) {
                    RRCLogger.logException(illegalAccessException);
                }
                catch (InvocationTargetException invocationTargetException) {
                    RRCLogger.logException(invocationTargetException);
                }
            }
        }
        return abstractDisplay;
    }

    public Object showDialogOnTop(JOptionPane jOptionPane, String string, AbstractDisplay abstractDisplay) {
        JDialog jDialog = jOptionPane.createDialog(abstractDisplay == null ? this.getParent() : abstractDisplay, string);
        if (abstractDisplay != null) {
            jDialog.addWindowFocusListener(abstractDisplay);
            jDialog.setLocationRelativeTo(abstractDisplay);
        }
        Util.jre17WorkaroundInheritAlwaysOnTop(jDialog);
        jDialog.setModal(true);
        ShellFocusObserver.addShellVisible(jDialog, this.scrContext);
        jDialog.setVisible(true);
        ShellFocusObserver.removeShellVisible(jDialog, this.scrContext);
        jDialog.dispose();
        return jOptionPane.getValue();
    }

    public Object showDialogOnTop(JOptionPane jOptionPane, String string, Component component) {
        JDialog jDialog = jOptionPane.createDialog(component == null ? this.getParent() : component, string);
        if (component != null) {
            jDialog.setLocationRelativeTo(component);
        }
        Util.jre17WorkaroundInheritAlwaysOnTop(jDialog);
        jDialog.setModal(true);
        ShellFocusObserver.addShellVisible(jDialog, this.scrContext);
        jDialog.setVisible(true);
        ShellFocusObserver.removeShellVisible(jDialog, this.scrContext);
        jDialog.dispose();
        return jOptionPane.getValue();
    }

    public void destroy() {
        this.panelClasses.clear();
        this.panelClasses.clear();
        this.parent = null;
    }

    private class PanelKey {
        private String name;
        private Frame frame;

        public PanelKey(String string, Frame frame) {
            this.name = string;
            this.frame = frame;
        }

        public boolean equals(Object object) {
            if (!(object instanceof PanelKey)) {
                return false;
            }
            PanelKey panelKey = (PanelKey)object;
            return this.name.equals(panelKey.name) && this.frame == panelKey.frame;
        }

        public int hashCode() {
            return this.name.hashCode() + this.frame.hashCode();
        }
    }
}

