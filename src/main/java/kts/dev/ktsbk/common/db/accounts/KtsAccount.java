package kts.dev.ktsbk.common.db.accounts;

import kts.dev.ktsbk.common.db.currencies.KtsCurrency;
import kts.dev.ktsbk.common.db.users.KtsUser;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

public class KtsAccount implements Serializable {
    private long id;
    private Timestamp createdTime = Timestamp.from(Instant.now());
    private KtsUser user;
    private String name;
    private KtsCurrency currency;
    private long balance = 0L;
    boolean blocked = false;
    boolean disabled = false;

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

    public KtsUser getUser() {
        return user;
    }

    public void setUser(KtsUser user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KtsCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(KtsCurrency currency) {
        this.currency = currency;
    }
    public long getBalance() {
        return balance;
    }

    public boolean isBlocked() {
        return blocked;
    }
    public boolean climbBlocked() {
        return isBlocked() || currency.isBlocked();
    }
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    public boolean isDisabled() {
        return disabled;
    }
    public boolean climbDisabled() {
        return isDisabled() || currency.isDisabled();
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
