package kts.dev.ktsbk.client;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class KtsbkConfig {
    private static final Gson gson = new Gson();
    private static final Logger log = Logger.getLogger("KtsBkConfig");
    public static KtsbkConfig config = new KtsbkConfig();
    private long serverSelected = 0L;
    private long worldSelected = 0L;
    private int renderMode = 0;
    public int getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(int renderMode) {
        this.renderMode = renderMode;
    }
    private String token = "";
    private KtsbkConfig() {}
    public void save() {
        try(FileWriter f = new FileWriter("config/ktsbk.json")) {
            f.write(gson.toJson(config));
        } catch (IOException e) {
            log.warning("Error write to file: ktsbk.json");
        }
    }

    private void fix() {
        save();
        log.warning("Error read from file: ktsbk.json");
    }

    public void load() {
        try {
            KtsbkConfig tmp_config = gson.fromJson(new FileReader("config/ktsbk.json"), KtsbkConfig.class);
            if(tmp_config == null) {
                fix();
            } else {
                config = tmp_config;
            }
        } catch (IOException e) {
            fix();
        }
    }

    public long getServerSelected() {
        return serverSelected;
    }

    public void setServerSelected(long serverSelected) {
        this.serverSelected = serverSelected;
    }

    public long getWorldSelected() {
        return worldSelected;
    }

    public void setWorldSelected(long worldSelected) {
        this.worldSelected = worldSelected;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
