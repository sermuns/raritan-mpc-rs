/*
 * Decompiled with CFR 0.152.
 */
package com.raritan.tools.ui.screens;

import com.raritan.tools.ui.AbstractUIManager;
import com.raritan.tools.ui.ScreenContext;
import com.raritan.tools.ui.screens.BaseScreen;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

public abstract class ScreenManager
implements Observer {
    protected Map screens;
    protected AbstractUIManager application;
    protected JPanel contentPane;
    protected ScreenContext scrContext;

    protected ScreenManager(ScreenContext screenContext) {
        this.scrContext = screenContext;
        this.scrContext.getLogger().logTextDebug(" Invoked");
        this.contentPane = new JPanel(new BorderLayout());
        this.contentPane.setVisible(true);
        this.screens = new Hashtable();
        this.scrContext.getLogger().logTextDebug(" Finished");
    }

    public synchronized void setApplication(AbstractUIManager abstractUIManager) {
        this.application = abstractUIManager;
        this.application.getContentPane().add(this.contentPane);
    }

    public boolean show() {
        this.scrContext.getLogger().logTextDebug(" Invoked for state " + this.scrContext.getState().toString() + " at " + Calendar.getInstance().getTime());
        this.contentPane.removeAll();
        BaseScreen baseScreen = (BaseScreen)this.screens.get(this.scrContext.getState().toString());
        this.contentPane.add((Component)baseScreen, "Center");
        if (this.contentPane.getParent() != null) {
            this.contentPane.getParent().validate();
            this.contentPane.getParent().repaint();
        }
        this.scrContext.getLogger().logTextDebug(" Finished for state " + this.scrContext.getState().toString() + " at " + Calendar.getInstance().getTime());
        return true;
    }

    public int getScreensCount() {
        return this.screens.size();
    }

    @Override
    public void update(Observable observable, Object object) {
        this.show();
    }

    public void destroy() {
        this.screens.clear();
        this.screens = null;
        this.application = null;
        this.scrContext = null;
    }
}

