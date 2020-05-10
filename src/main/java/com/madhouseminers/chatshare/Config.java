package com.madhouseminers.chatshare;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class Config {

    public static final String CATEGORY_SERVER = "chatshare";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<String> SERVER;
    public static ForgeConfigSpec.IntValue PORT;

    static {
        COMMON_BUILDER.comment("Chatshare settings").push(CATEGORY_SERVER);
        SERVER = COMMON_BUILDER.comment("Server").define("chatshareServer", "localhost");
        PORT = COMMON_BUILDER.comment("Port").defineInRange("chatsharePort", 8080, 1000, 65535);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        CommentedFileConfig config = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

        config.load();
        spec.setConfig(config);
    }
}
