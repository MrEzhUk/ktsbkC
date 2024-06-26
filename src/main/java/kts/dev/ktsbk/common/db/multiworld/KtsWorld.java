package kts.dev.ktsbk.common.db.multiworld;


import java.io.Serializable;

public class KtsWorld implements Serializable {
    private long id;
    private KtsServer server;
    private String mcName;
    private String ktsbkName;
    private boolean disabled = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public KtsServer getServer() {
        return server;
    }

    public void setServer(KtsServer serverId) {
        this.server = serverId;
    }

    public String getMcName() {
        return mcName;
    }

    public void setMcName(String mcName) {
        this.mcName = mcName;
    }

    public String getKtsbkName() {
        return ktsbkName;
    }

    public void setKtsbkName(String ktsbkName) {
        this.ktsbkName = ktsbkName;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
