/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore;

public class VirtualMediaConfig {
    private Config[] configs;

    public VirtualMediaConfig(int n) {
        this.configs = new Config[n];
        for (int i = 0; i < n; ++i) {
            this.configs[i] = new Config();
        }
    }

    public VirtualMediaConfig(VirtualMediaConfig virtualMediaConfig) {
        this.configs = new Config[virtualMediaConfig.configs.length];
        for (int i = 0; i < this.configs.length; ++i) {
            this.configs[i] = new Config(virtualMediaConfig.configs[i]);
        }
    }

    public void setCdrom(int n, boolean bl) {
        if (n < this.configs.length) {
            this.configs[n].cdrom = bl;
        }
    }

    public void setFloppy(int n, boolean bl) {
        if (n < this.configs.length) {
            this.configs[n].floppy = bl;
        }
    }

    public void setRemovable(int n, boolean bl) {
        if (n < this.configs.length) {
            this.configs[n].removable = bl;
        }
    }

    public int getNoMassStorage() {
        return this.configs.length;
    }

    public boolean getCdrom(int n) {
        if (n < this.configs.length) {
            return this.configs[n].cdrom;
        }
        return false;
    }

    public boolean getFloppy(int n) {
        if (n < this.configs.length) {
            return this.configs[n].floppy;
        }
        return false;
    }

    public boolean getRemovable(int n) {
        if (n < this.configs.length) {
            return this.configs[n].removable;
        }
        return false;
    }

    private class Config {
        boolean cdrom;
        boolean floppy;
        boolean removable;

        Config() {
            this.removable = false;
            this.floppy = false;
            this.cdrom = false;
        }

        Config(Config config) {
            this.cdrom = config.cdrom;
            this.floppy = config.floppy;
            this.removable = config.removable;
        }
    }
}

