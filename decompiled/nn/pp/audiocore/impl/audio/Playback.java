/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.audiocore.impl.audio;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import nn.pp.audiocore.AudioException;
import nn.pp.audiocore.AudioFormat;
import nn.pp.audiocore.impl.ThreadMonitor;
import nn.pp.audiocore.impl.audio.AudioConsumer;
import nn.pp.audiocore.impl.audio.CircularBufferStream;
import nn.pp.core.Platform;
import nn.pp.core.T;

public class Playback
extends Thread
implements AudioConsumer {
    private static final int RETRY_TIMEOUT = 1000;
    private static final int DEFAULT_BLOCK_DURATION_PLAYBACK = 120;
    private boolean shouldRun = false;
    private ThreadMonitor monitor;
    private static final boolean TRACE_BUFFERS = false;
    private int overruns = 0;
    private int underruns = 0;
    private Timer traceTimer;
    private int traceCount = 0;
    private Mixer.Info mixer;
    private AudioFormat format;
    private CircularBufferStream circularStream;
    private SourceDataLine sourceLine;
    private LineListener sourceListener;
    private Logger logger;
    private byte[] writeBuffer = null;
    private int blockDuration = 120;

    public Playback(Logger logger, Mixer.Info info, AudioFormat audioFormat) {
        this.logger = logger;
        this.mixer = info;
        this.format = audioFormat;
        this.monitor = new ThreadMonitor(0);
        this.circularStream = new CircularBufferStream(audioFormat.getBytesPerSecond());
    }

    public void activate() {
        if (!this.shouldRun) {
            this.shouldRun = true;
            this.start();
        }
    }

    public void close() {
        this.shouldRun = false;
        this.wakeup();
        try {
            this.join(1000L);
            if (this.isAlive()) {
                this.logger.log(Level.WARNING, T._("Playback thread still alive, killing forcefully!"));
                this.stop();
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        if (this.traceTimer != null) {
            this.traceTimer.cancel();
            this.traceTimer = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setBlockDuration(int n) {
        Playback playback = this;
        synchronized (playback) {
            this.blockDuration = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void consumeAudioData(AudioFormat audioFormat, byte[] byArray, int n, int n2) throws IOException {
        int n3;
        int n4;
        if (!this.format.matches(audioFormat)) {
            return;
        }
        CircularBufferStream circularBufferStream = this.circularStream;
        synchronized (circularBufferStream) {
            if (n2 > this.circularStream.getWritableBytes()) {
                this.logger.log(Level.INFO, T._("Buffer overrun detected, expect choppyness"));
                this.trace("Buffer overrun detected, expect choppyness");
                ++this.overruns;
            }
            this.circularStream.write(byArray, n, n2);
            n4 = this.circularStream.getAvailableBytes();
        }
        this.logger.log(Level.FINER, "Received " + n2 + " bytes");
        Playback playback = this;
        synchronized (playback) {
            n3 = this.blockDuration;
        }
        int n5 = audioFormat.getBytesPerSecond();
        if (n4 > n5 * n3 / 1000 || n2 == 0) {
            this.wakeup();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void flushBuffers() {
        CircularBufferStream circularBufferStream = this.circularStream;
        synchronized (circularBufferStream) {
            this.circularStream.bufReset();
        }
    }

    private void reset() {
        this.logger.log(Level.INFO, T._("cleaning up sourceLine"));
        if (this.sourceLine != null) {
            this.sourceLine.flush();
            this.sourceLine.stop();
            this.sourceLine.removeLineListener(this.sourceListener);
            this.sourceLine.close();
            this.sourceLine = null;
        }
    }

    private void checkWriteBufSize(int n) {
        if (this.writeBuffer == null || this.writeBuffer.length < n) {
            this.writeBuffer = new byte[n];
        }
    }

    private void initPlayback() throws AudioException {
        this.logger.log(Level.INFO, T._("trying to acquire a SourceDataLine"));
        try {
            this.sourceLine = AudioSystem.getSourceDataLine(this.format, this.mixer);
            this.sourceLine.open(this.format, this.format.getBytesPerSecond());
        }
        catch (LineUnavailableException lineUnavailableException) {
            throw new AudioException(T._("failure during sourceline aquisition"));
        }
        this.logger.log(Level.INFO, T._("SourceDataLine has been acquired"));
        this.sourceListener = new LineListener(){

            @Override
            public void update(LineEvent lineEvent) {
                if (lineEvent.getType() == LineEvent.Type.START) {
                    Playback.this.logger.log(Level.INFO, T._("source line start event"));
                } else if (lineEvent.getType() == LineEvent.Type.STOP) {
                    Playback.this.logger.log(Level.INFO, T._("source line stop event"));
                } else if (lineEvent.getType() == LineEvent.Type.OPEN) {
                    Playback.this.logger.log(Level.INFO, T._("source line open event"));
                } else if (lineEvent.getType() == LineEvent.Type.CLOSE) {
                    Playback.this.logger.log(Level.INFO, T._("source line close event"));
                }
            }
        };
        this.sourceLine.addLineListener(this.sourceListener);
        this.sourceLine.start();
        this.logger.log(Level.INFO, T._("playback interface initialized"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void wakeup() {
        ThreadMonitor threadMonitor = this.monitor;
        synchronized (threadMonitor) {
            this.monitor.setValue(1);
            this.monitor.notify();
        }
    }

    @Override
    public void run() {
        this.logger.log(Level.INFO, T._("playback thread started"));
        try {
            this.processPlayback();
        }
        catch (Exception exception) {
            this.logger.log(Level.SEVERE, T._("AudioHandler: processPlayback exception"));
            exception.printStackTrace();
        }
        try {
            this.reset();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.logger.log(Level.INFO, T._("playback thread finished"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processPlayback() throws IOException, AudioException {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        boolean bl = false;
        Mixer mixer = AudioSystem.getMixer(this.mixer);
        if (mixer != null && mixer.getClass().getName().equals("com.sun.media.sound.DirectAudioDevice") && Platform.isWindows()) {
            bl = true;
        }
        while (this.shouldRun) {
            while (this.sourceLine == null && this.shouldRun) {
                try {
                    this.initPlayback();
                }
                catch (AudioException audioException) {
                    this.logger.log(Level.WARNING, T._("line currently unavailable"));
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                CircularBufferStream circularBufferStream = this.circularStream;
                synchronized (circularBufferStream) {
                    this.circularStream.bufReset();
                }
            }
            int n4 = this.format.getFrameSize();
            CircularBufferStream circularBufferStream = this.circularStream;
            synchronized (circularBufferStream) {
                n = this.circularStream.getAvailableBytes() / n4 * n4;
                this.checkWriteBufSize(n);
                n2 = this.circularStream.read(this.writeBuffer, n);
            }
            if (n2 > 0) {
                try {
                    n3 = this.sourceLine.write(this.writeBuffer, 0, n2);
                }
                catch (Exception exception) {
                    throw new IOException(T._("sourceLine transfer error"));
                }
            } else {
                n3 = 0;
            }
            if (n3 < 0) {
                this.logger.log(Level.INFO, T._("bogus sourceline state"));
                this.reset();
                continue;
            }
            if (n2 == 0) {
                boolean bl2 = false;
                ThreadMonitor threadMonitor = this.monitor;
                synchronized (threadMonitor) {
                    while (this.monitor.getValue() == 0) {
                        if (!bl2 && this.sourceLine.isActive() && this.sourceLine.available() == this.sourceLine.getBufferSize()) {
                            this.logger.log(Level.INFO, T._("Buffer underrun detected, re-syncing ..."));
                            this.trace("Buffer underrun detected, re-syncing ...");
                            ++this.underruns;
                            if (bl) {
                                this.sourceLine.stop();
                                this.sourceLine.flush();
                            }
                            bl2 = true;
                        }
                        try {
                            this.monitor.wait(100L);
                        }
                        catch (Exception exception) {}
                    }
                    if (bl2) {
                        this.logger.log(Level.INFO, T._("... re-syncing done"));
                        this.trace("... re-syncing done");
                        if (bl && !this.sourceLine.isActive()) {
                            this.sourceLine.start();
                        }
                        bl2 = false;
                    }
                    this.monitor.setValue(0);
                    continue;
                }
            }
            this.logger.log(Level.FINER, "Played " + n2 + " bytes of data");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void traceBuffers() {
        int n;
        int n2;
        ++this.traceCount;
        CircularBufferStream circularBufferStream = this.circularStream;
        synchronized (circularBufferStream) {
            n2 = this.circularStream.getAvailableBytes();
            n = n2 * 1000 / this.format.getBytesPerSecond();
        }
        this.trace("Playback: " + this.traceCount + " -> Overruns: " + this.overruns + ", underruns: " + this.underruns + ", buffer: " + n2 + " bytes, " + n + " ms");
    }

    private void trace(String string) {
    }
}

