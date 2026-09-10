/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.applet.Applet
 *  javax.swing.JApplet
 *  netscape.javascript.JSException
 *  netscape.javascript.JSObject
 *  sun.applet.AppletViewer
 *  sun.plugin.javascript.JSObject
 */
package com.raritan.tools.commands;

import com.raritan.tools.commands.AbstractCommand;
import com.raritan.tools.commands.CommandResult;
import com.raritan.tools.ui.ScreenContext;
import java.applet.Applet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JApplet;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import sun.applet.AppletViewer;

public class CloseBrowserCommand
extends AbstractCommand {
    public static final String COMMAND_KEY = "closeBrowserCommand";

    public CloseBrowserCommand(ScreenContext screenContext) {
        super(screenContext);
    }

    @Override
    public CommandResult execute() {
        CommandResult commandResult = new CommandResult();
        try {
            this.invokeWindowClose();
            commandResult.setIsSuccess(true);
            ((JApplet)this.scrContext.getApplication()).stop();
        }
        catch (JSException jSException) {
            if (((JApplet)this.scrContext.getApplication()).getAppletContext() instanceof AppletViewer) {
                // empty if block
            }
            commandResult.setIsSuccess(true);
        }
        catch (Exception exception) {
            this.scrContext.getLogger().logTextError(exception.getMessage());
            commandResult.setIsSuccess(false);
        }
        return commandResult;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public String getKey() {
        return COMMAND_KEY;
    }

    private void invokeWindowClose() {
        JSObject jSObject = sun.plugin.javascript.JSObject.getWindow((Applet)((JApplet)this.scrContext.getApplication()));
        if (jSObject == null) {
            return;
        }
        try {
            Method method = jSObject.getClass().getMethod("call", String.class, Object[].class);
            if (method != null) {
                method.invoke((Object)jSObject, "close", new Object[0]);
            }
        }
        catch (SecurityException securityException) {
            securityException.printStackTrace();
        }
        catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
        }
        catch (NoSuchMethodException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
        }
        catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
        catch (InvocationTargetException invocationTargetException) {
            invocationTargetException.printStackTrace();
        }
    }
}

