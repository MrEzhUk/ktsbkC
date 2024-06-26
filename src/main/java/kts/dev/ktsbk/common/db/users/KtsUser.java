package kts.dev.ktsbk.common.db.users;


import java.io.Serializable;

public class KtsUser implements Serializable {
    long id;
    String disId;
    String nickname;
    transient String token;
    boolean blocked = false;
    boolean disabled = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisId() {
        return disId;
    }

    public void setDisId(String disId) {
        this.disId = disId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    @Override
    public String toString() {
        return "KtsUser{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", blocked=" + blocked +
                ", disabled=" + disabled +
                '}';
    }
}
