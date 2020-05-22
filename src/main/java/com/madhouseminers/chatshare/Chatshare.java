package com.madhouseminers.chatshare;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("chatshare")
public class Chatshare {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public MinecraftServer server;
    private Websocket ws;

    public Chatshare() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("chatshare-common.toml"));
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        this.server = event.getServer();
        this.ws = new Websocket(this);
        this.ws.start();
    }

    @SubscribeEvent
    public void gotChatEvent(ServerChatEvent event) {
        this.ws.sendMessage("<" + event.getUsername() + "> " + event.getMessage());
    }
}
