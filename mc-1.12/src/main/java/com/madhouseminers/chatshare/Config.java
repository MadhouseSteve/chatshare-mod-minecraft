package com.madhouseminers.chatshare;

import com.madhouseminers.chatshareCore.ModConfig;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

class Config implements ModConfig {
    private String NAME;
    private String SERVER;
    private int PORT;
    private String PASSWORD;
    private String PROTOCOL;

    public void loadConfig(File file) {
        Configuration config = new Configuration(file);
        config.load();

        this.NAME = config.getString("name", Configuration.CATEGORY_GENERAL, "", "NEW_SERVER");
        this.PASSWORD = config.getString("psk", "CHATSHARE", "", "");
        this.SERVER = config.getString("server", "CHATSHARE", "", "localhost");
        this.PORT = config.getInt("port", "CHATSHARE", 8080, 1000, 65535, "");
        this.PROTOCOL = config.getString("protocol", "CHATSHARE", "", "wss");
    }

    public String getName() {
        return this.NAME;
    }

    public String getServer() {
        return this.SERVER;
    }

    public int getPort() {
        return this.PORT;
    }

    public String getPassword() {
        return this.PASSWORD;
    }

    public String getProtocol() { return this.PROTOCOL; }
}
