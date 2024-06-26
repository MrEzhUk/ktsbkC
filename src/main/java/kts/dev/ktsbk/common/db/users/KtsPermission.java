package kts.dev.ktsbk.common.db.users;

import java.io.Serializable;

public class KtsPermission implements Serializable {
    private long id;
    private String permissionString;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public void setPermissionString(String permissionString) {
        this.permissionString = permissionString;
    }
}
