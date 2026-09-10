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
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import nn.pp.audiocore.AudioException;
import nn.pp.audiocore.AudioFormat;
import nn.pp.audiocore.impl.ThreadMonitor;
import nn.pp.audiocore.impl.audio.AudioConsumer;
import nn.pp.audiocore.impl.audio.CircularBufferStream;
import nn.pp.core.T;

public class Capture
extends Thread {
    private static final boolean OVERRIDE_DURATION = true;
    private static final int RETRY_TIMEOUT = 1000;
    private static final int DEFAULT_BLOCK_DURATION_CAPTURE = 40;
    private boolean shouldRun = false;
    private ThreadMonitor captureMonitor;
    private ThreadMonitor consumeMonitor;
    private Thread consumeThread;
    private CircularBufferStream circularStream;
    private static final boolean TRACE_BUFFERS = false;
    private int overruns = 0;
    private Timer traceTimer;
    private int traceCount = 0;
    private TargetDataLine targetLine;
    private LineListener targetListener;
    private Mixer.Info mixer;
    private AudioFormat format;
    private Logger logger;
    private byte[] readBuffer = null;
    private byte[] consumeBuffer = null;
    private int blockDuration = 40;
    private int packetSize;
    private AudioConsumer consumer;

    public Capture(Logger logger, Mixer.Info info, AudioFormat audioFormat, AudioConsumer audioConsumer) {
        this.logger = logger;
        this.mixer = info;
        this.format = audioFormat;
        this.consumer = audioConsumer;
        this.circularStream = new CircularBufferStream(audioFormat.getBytesPerSecond());
        this.captureMonitor = new ThreadMonitor(0);
        this.consumeMonitor = new ThreadMonitor(0);
        this.consumeThread = new Thread(){

            @Override
            public void run() {
                Capture.this.consumeCapture();
            }
        };
    }

    public void activate() {
        this.packetSize = this.format.getBytesPerSecond() * this.blockDuration / 1000;
        if (!this.shouldRun) {
            this.shouldRun = true;
            this.start();
            this.consumeThread.start();
        }
    }

    public void close() {
        this.shouldRun = false;
        this.wakeupCapture();
        this.wakeupConsumer();
        try {
            this.join(1000L);
            if (this.isAlive()) {
                this.logger.log(Level.WARNING, T._("Capture thread still alive, killing forcefully!"));
                this.stop();
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        try {
            this.consumeThread.join(1000L);
            if (this.consumeThread.isAlive()) {
                this.logger.log(Level.WARNING, T._("Consumer thread still alive, killing forcefully!"));
                this.consumeThread.stop();
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        this.readBuffer = null;
        this.consumeBuffer = null;
    }

    public void setBlockDuration(int n) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void pauseCapture() {
        ThreadMonitor threadMonitor = this.captureMonitor;
        synchronized (threadMonitor) {
            this.captureMonitor.setValue(0);
        }
    }

    public void resumeCapture() {
        this.wakeupCapture();
    }

    private void checkConsumeBufSize(int n) {
        if (this.consumeBuffer == null || this.consumeBuffer.length < n) {
            this.consumeBuffer = new byte[n];
        }
    }

    private void deactivate() throws IOException {
        this.logger.log(Level.INFO, T._("cleaning up targetLine"));
        if (this.targetLine != null) {
            this.targetLine.flush();
            this.targetLine.removeLineListener(this.targetListener);
            this.targetLine.stop();
            this.targetLine.close();
            this.targetLine = null;
        }
    }

    private void initCapture() throws AudioException {
        this.logger.log(Level.INFO, T._("trying to acquire a TargetDataLine"));
        try {
            this.targetLine = AudioSystem.getTargetDataLine(this.format, this.mixer);
            this.targetLine.open(this.format, this.format.getBytesPerSecond());
        }
        catch (Exception exception) {
            throw new AudioException(T._("failure during targetline aquisition"));
        }
        this.logger.log(Level.INFO, T._("TargetDataLine has been acquired"));
        this.targetListener = new LineListener(){

            @Override
            public void update(LineEvent lineEvent) {
                if (lineEvent.getType() == LineEvent.Type.START) {
                    Capture.this.logger.log(Level.INFO, T._("target line start event"));
                } else if (lineEvent.getType() == LineEvent.Type.STOP) {
                    Capture.this.logger.log(Level.INFO, T._("target line stop event"));
                } else if (lineEvent.getType() == LineEvent.Type.OPEN) {
                    Capture.this.logger.log(Level.INFO, T._("target line open event"));
                } else if (lineEvent.getType() == LineEvent.Type.CLOSE) {
                    Capture.this.logger.log(Level.INFO, T._("target line close event"));
                }
            }
        };
        this.targetLine.addLineListener(this.targetListener);
        this.targetLine.start();
        this.logger.log(Level.INFO, T._("capture interface initialized"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void wakeupCapture() {
        ThreadMonitor threadMonitor = this.captureMonitor;
        synchronized (threadMonitor) {
            this.captureMonitor.setValue(1);
            this.captureMonitor.notify();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void wakeupConsumer() {
        ThreadMonitor threadMonitor = this.consumeMonitor;
        synchronized (threadMonitor) {
            this.consumeMonitor.setValue(1);
            this.consumeMonitor.notify();
        }
    }

    @Override
    public void run() {
        this.logger.log(Level.INFO, T._("capture thread started"));
        try {
            this.processCapture();
        }
        catch (Exception exception) {
            this.logger.log(Level.SEVERE, T._("AudioHandler: processCapture exception"));
            exception.printStackTrace();
        }
        try {
            this.deactivate();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.logger.log(Level.INFO, T._("capture thread finished"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processCapture() throws IOException, AudioException {
        int n = 10;
        int n2 = 0;
        boolean bl = false;
        int n3 = this.format.getBytesPerSecond() * n / 1000;
        int n4 = this.format.getFrameSize();
        n3 = n3 / n4 * n4;
        this.readBuffer = new byte[n3];
        while (this.shouldRun) {
            while (this.targetLine == null && this.shouldRun) {
                try {
                    this.initCapture();
                }
                catch (AudioException audioException) {
                    this.logger.log(Level.WARNING, T._("targetline currently unavailable"));
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (Exception exception) {}
                }
            }
            Object object = this.captureMonitor;
            synchronized (object) {
                while (this.captureMonitor.getValue() == 0) {
                    this.logger.log(Level.INFO, "Capture is paused");
                    this.deactivate();
                    bl = false;
                    try {
                        this.captureMonitor.wait();
                    }
                    catch (Exception exception) {}
                }
                if (!this.shouldRun) {
                    break;
                }
            }
            if (!bl) {
                this.logger.log(Level.INFO, "Capture resumed");
                object = this.circularStream;
                synchronized (object) {
                    this.circularStream.bufReset();
                }
                this.initCapture();
                bl = true;
            }
            if (this.targetLine.available() < n3) {
                try {
                    Thread.sleep(n / 2);
                }
                catch (InterruptedException interruptedException) {}
                continue;
            }
            try {
                n2 = this.targetLine.read(this.readBuffer, 0, n3);
            }
            catch (Exception exception) {
                throw new AudioException(T._("targetLine read failed"));
            }
            if (n2 == 0) continue;
            if (n2 > 0) {
                object = this.circularStream;
                synchronized (object) {
                    if (n2 > this.circularStream.getWritableBytes()) {
                        this.logger.log(Level.INFO, T._("Buffer overrun detected, expect choppyness"));
                        this.trace("Buffer overrun detected, expect choppyness");
                        ++this.overruns;
                    }
                    this.circularStream.write(this.readBuffer, 0, n2);
                }
                this.wakeupConsumer();
                continue;
            }
            this.deactivate();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void consumeCapture() {
        while (this.shouldRun) {
            int n;
            int n2;
            ThreadMonitor threadMonitor = this.consumeMonitor;
            synchronized (threadMonitor) {
                while (this.consumeMonitor.getValue() == 0) {
                    try {
                        this.consumeMonitor.wait();
                    }
                    catch (Exception exception) {}
                }
                this.consumeMonitor.setValue(0);
                if (!this.shouldRun) {
                    break;
                }
            }
            Object object = this;
            synchronized (object) {
                n2 = this.packetSize;
            }
            object = this.circularStream;
            synchronized (object) {
                n = this.circularStream.getAvailableBytes();
                if (n < n2) {
                    continue;
                }
                try {
                    this.checkConsumeBufSize(n2);
                    int n3 = this.circularStream.read(this.consumeBuffer, n2);
                    assert (n2 == n3);
                }
                catch (IOException iOException) {
                    this.logger.log(Level.SEVERE, T._("Exception handled when reading capture data from buffer"), iOException);
                    break;
                }
            }
            try {
                this.consumer.consumeAudioData(this.format, this.consumeBuffer, 0, n2);
            }
            catch (IOException iOException) {
                this.logger.log(Level.SEVERE, T._("Exception handled when sending capture data to network"), iOException);
                break;
            }
            if (n - n2 < n2) continue;
            this.wakeupConsumer();
        }
        this.logger.log(Level.INFO, T._("capture consumer thread finished"));
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
        this.trace("Capture: " + this.traceCount + " -> Overruns: " + this.overruns + ", buffer: " + n2 + " bytes, " + n + " ms");
    }

    private void trace(String string) {
    }
}

