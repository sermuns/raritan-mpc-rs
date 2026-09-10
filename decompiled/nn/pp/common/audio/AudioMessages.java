/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.audio;

import nn.pp.core.T;

public class AudioMessages {
    public static final String audioConnectionInProgressMessage = T._("Audio connection in progress. Please wait.");
    public static final String audioSuccessfulConnectionMessage = T._("Audio Connection was established successfully.");
    public static final String audioSharingHintMessage = T._("An audio connection is already in place to a target from this client PC.\nSuccessful connection of audio to multiple targets from a single client PC is \ndependent on a number of factors; OS, Java version and drivers that are in use. \nIf necessary disconnect existing audio sessions and reconnect audio to this target.");
    public static final String audioConfirmDisconnectMessage = T._("Disconnect Audio device: Are you sure?");
    public static final String audioSuccessfulDisconnectMessage = T._("Audio Device disconnected successfully.");
    public static final String audioUsbnotconnConnectionMessage = T._("The audio capability is set up but will not be available until \nthe USB cable is connected or the target is powered on. \nPlease check your USB connectivity or see if the target is powered on.");

    private AudioMessages() {
    }
}

