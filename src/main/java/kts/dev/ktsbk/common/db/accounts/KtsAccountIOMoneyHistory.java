package kts.dev.ktsbk.common.db.accounts;

import kts.dev.ktsbk.common.db.users.KtsUser;

import java.sql.Timestamp;
import java.time.Instant;

public class KtsAccountIOMoneyHistory {
    private long id;
    private Timestamp createdTime = Timestamp.from(Instant.now());
    KtsUser doer;
    private KtsAccount account;
    long count;
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

    public KtsAccount getAccount() {
        return account;
    }

    public void setAccount(KtsAccount account) {
        this.account = account;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
