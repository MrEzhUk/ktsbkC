package kts.dev.ktsbk.common.db.bridge;

import kts.dev.ktsbk.common.db.users.KtsPermission;
import kts.dev.ktsbk.common.db.users.KtsUser;

import java.io.Serializable;

public class KtsUserPermission implements Serializable {
    public KtsUserPermission() {}
    private long id;

    private KtsUser user;

    private KtsPermission permission;

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

    public KtsPermission getPermission() {
        return permission;
    }

    public void setPermission(KtsPermission permission) {
        this.permission = permission;
    }
}
