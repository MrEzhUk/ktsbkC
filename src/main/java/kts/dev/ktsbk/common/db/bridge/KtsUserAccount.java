package kts.dev.ktsbk.common.db.bridge;


import kts.dev.ktsbk.common.db.accounts.KtsAccount;
import kts.dev.ktsbk.common.db.users.KtsUser;

public class KtsUserAccount {
    private long id;

    private KtsUser user;

    private KtsAccount account;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public KtsUser getUser() {
        return user;
    }

    public void setUser(KtsUser user) {
        this.user = user;
    }

    public KtsAccount getAccount() {
        return account;
    }

    public void setAccount(KtsAccount account) {
        this.account = account;
    }
}
