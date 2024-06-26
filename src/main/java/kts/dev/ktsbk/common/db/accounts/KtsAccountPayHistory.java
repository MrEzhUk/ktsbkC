package kts.dev.ktsbk.common.db.accounts;

import kts.dev.ktsbk.common.db.users.KtsUser;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

public class KtsAccountPayHistory implements Serializable {
    private long id;
    private Timestamp createdTime = Timestamp.from(Instant.now());
    KtsUser doer;
    KtsAccount fromAccount;
    KtsAccount toAccount;
    long count;
    String msg;
    boolean rollback = false;

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

    public KtsAccount getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(KtsAccount fromAccount) {
        this.fromAccount = fromAccount;
    }

    public KtsAccount getToAccount() {
        return toAccount;
    }

    public void setToAccount(KtsAccount toAccount) {
        this.toAccount = toAccount;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public boolean isRollback() {
        return rollback;
    }

    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
