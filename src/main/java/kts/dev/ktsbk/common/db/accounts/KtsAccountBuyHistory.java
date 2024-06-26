package kts.dev.ktsbk.common.db.accounts;

import kts.dev.ktsbk.common.db.box.KtsBox;
import kts.dev.ktsbk.common.db.users.KtsUser;

import java.sql.Timestamp;
import java.time.Instant;

public class KtsAccountBuyHistory {
    private long id;
    private Timestamp createdTime = Timestamp.from(Instant.now());
    KtsUser doer;
    private KtsAccount account;
    private KtsBox box;
    private long count;
    private long cost;
    private long tax;
    private boolean rollback = false;

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

    public KtsUser getDoer() {
        return doer;
    }

    public void setDoer(KtsUser doer) {
        this.doer = doer;
    }

    public KtsAccount getAccount() {
        return account;
    }

    public void setAccount(KtsAccount account) {
        this.account = account;
    }

    public KtsBox getBox() {
        return box;
    }

    public void setBox(KtsBox box) {
        this.box = box;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public long getTax() {
        return tax;
    }

    public void setTax(long tax) {
        this.tax = tax;
    }

    public boolean isRollback() {
        return rollback;
    }

    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }
}
