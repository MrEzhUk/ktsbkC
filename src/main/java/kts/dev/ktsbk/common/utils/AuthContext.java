package kts.dev.ktsbk.common.utils;

import kts.dev.ktsbk.common.db.users.KtsUser;
import org.java_websocket.WebSocket;

import java.io.Serializable;

public class AuthContext implements Serializable {
    private KtsUser user;
    private WebSocket ws;
    public AuthContext() {}
    public KtsUser getUser() {
        return user;
    }
    public void setUser(KtsUser user) {
        this.user = user;
    }
    public WebSocket getWs() {
        return ws;
    }
    public void setWs(WebSocket ws) {
        this.ws = ws;
    }
}
