package com.madhouseminers.chatshare;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

import com.madhouseminers.chatshareCore.ModConfig;

public class Config implements ModConfig {

    public static final String CATEGORY_SERVER = "chatshare";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<String> NAME;
    public static ForgeConfigSpec.ConfigValue<String> SERVER;
    public static ForgeConfigSpec.IntValue PORT;
    public static ForgeConfigSpec.ConfigValue<String> PASSWORD;

    static {
        NAME = COMMON_BUILDER.comment("Minecraft Instance Name").define("name", "NEW_SERVER");

        COMMON_BUILDER.comment("Chatshare settings").push(CATEGORY_SERVER);
        SERVER = COMMON_BUILDER.comment("Server").define("server", "localhost");
        PORT = COMMON_BUILDER.comment("Port").defineInRange("port", 8080, 1000, 65535);
        PASSWORD = COMMON_BUILDER.comment("Pre-shared Key").define("psk", "");
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        CommentedFileConfig config = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

        config.load();
        spec.setConfig(config);
    }

    public String getName() {
        return Config.NAME.get();
    }

    public String getServer() {
        return Config.SERVER.get();
    }

    public String getPassword() {
        return Config.PASSWORD.get();
    }

    public int getPort() {
        return Config.PORT.get();
    }
}
