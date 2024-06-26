package kts.dev.ktsbk.common.db.box;

import kts.dev.ktsbk.common.db.accounts.KtsAccount;
import kts.dev.ktsbk.common.db.multiworld.KtsWorld;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

public class KtsBox implements Serializable {
    private long id;
    private Timestamp createdTime = Timestamp.from(Instant.now());
    private Timestamp lastTaxTime;
    private long x;
    private long y;
    private long z;
    private KtsWorld world;
    private KtsAccount account;
    private String minecraftIdentifier;
    private String minecraftSerializedItem;
    private KtsBoxType boxType;
    private long countNow;
    private long countPerTransaction;
    private long buyCostPerTransaction;
    private long sellCostPerTransaction;
    private boolean blocked = true;
    private boolean disabled = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public long getZ() {
        return z;
    }

    public void setZ(long z) {
        this.z = z;
    }

    public KtsWorld getWorld() {
        return world;
    }

    public void setWorld(KtsWorld world) {
        this.world = world;
    }

    public KtsAccount getAccount() {
        return account;
    }

    public void setAccount(KtsAccount account) {
        this.account = account;
    }

    public String getMinecraftIdentifier() {
        return minecraftIdentifier;
    }

    public void setMinecraftIdentifier(String minecraftIdentifier) {
        this.minecraftIdentifier = minecraftIdentifier;
    }

    public String getMinecraftSerializedItem() {
        return minecraftSerializedItem;
    }

    public void setMinecraftSerializedItem(String minecraftSerializedItem) {
        this.minecraftSerializedItem = minecraftSerializedItem;
    }

    public long getCountPerTransaction() {
        return countPerTransaction;
    }

    public void setCountPerTransaction(long countPerTransaction) {
        this.countPerTransaction = countPerTransaction;
    }

    public long getBuyCostPerTransaction() {
        return buyCostPerTransaction;
    }

    public void setBuyCostPerTransaction(long buyCostPerTransaction) {
        this.buyCostPerTransaction = buyCostPerTransaction;
    }

    public long getSellCostPerTransaction() {
        return sellCostPerTransaction;
    }

    public void setSellCostPerTransaction(long sellCostPerTransaction) {
        this.sellCostPerTransaction = sellCostPerTransaction;
    }

    public long getCountNow() {
        return countNow;
    }

    public void setCountNow(long countNow) {
        this.countNow = countNow;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Timestamp getLastTaxTime() {
        return lastTaxTime;
    }

    public void setLastTaxTime(Timestamp lastTaxTime) {
        this.lastTaxTime = lastTaxTime;
    }
    public KtsBoxType getBoxType() {
        return boxType;
    }

    public void setBoxType(KtsBoxType boxType) {
        this.boxType = boxType;
    }
}
