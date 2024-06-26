package kts.dev.ktsbk.common.db.currencies;


import kts.dev.ktsbk.common.db.multiworld.KtsServer;

import java.io.Serializable;

public class KtsCurrency implements Serializable {
    private long id;
    private KtsServer server;
    private String name;
    private String shortName;
    private long boxRent = 0;
    private long transactionPercent = 0;
    private long maxAccounts = 16;
    private int boxTypeSupport = 0;
    private int currencyPermission = 0;
    private int day3Acquire = 0;
    private boolean blocked = false;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public long getBoxRent() {
        return boxRent;
    }

    public void setBoxRent(long boxRent) {
        this.boxRent = boxRent;
    }

    public long getTransactionPercent() {
        return transactionPercent;
    }

    public void setTransactionPercent(long transactionPercent) {
        this.transactionPercent = transactionPercent;
    }

    public long getMaxAccounts() {
        return maxAccounts;
    }

    public void setMaxAccounts(long maxAccounts) {
        this.maxAccounts = maxAccounts;
    }

    public boolean isBlocked() {
        return blocked;
    }
    public boolean climbBlocked() {
        return isBlocked() || server.isBlocked();
    }
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isDisabled() {
        return disabled;
    }
    public boolean climbDisabled() {
        return isDisabled() || server.isDisabled();
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int getBoxTypeSupport() {
        return boxTypeSupport;
    }

    public void setBoxTypeSupport(int boxTypeSupport) {
        this.boxTypeSupport = boxTypeSupport;
    }

    public int getCurrencyPermission() {
        return currencyPermission;
    }

    public void setCurrencyPermission(int currencyPermission) {
        this.currencyPermission = currencyPermission;
    }

    public int getDay3Acquire() {
        return day3Acquire;
    }

    public void setDay3Acquire(int day3Acquire) {
        this.day3Acquire = day3Acquire;
    }
}
