package com.madhouseminers.chatshare;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.nio.file.Path;
import com.madhouseminers.chatshareCore.ModConfig;

public class Config implements ModConfig {
    private String NAME;
    private String SERVER;
    private int PORT;
    private String PASSWORD;

    public void loadConfig(File file) {
        Configuration config = new Configuration(file);
        config.load();

        this.NAME = config.getString("name", Configuration.CATEGORY_GENERAL, "", "");
        this.PASSWORD = config.getString("psk", "CHATSHARE", "", "");
        this.SERVER = config.getString("server", "CHATSHARE", "", "");
        this.PORT = config.getInt("port", "CHATSHARE", 8080, 1000, 65535, "");
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
}
