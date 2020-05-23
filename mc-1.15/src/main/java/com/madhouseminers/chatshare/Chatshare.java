package com.madhouseminers.chatshare;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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

import com.madhouseminers.chatshareCore.Websocket;
import com.madhouseminers.chatshareCore.ChatShareMod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("chatshare")
public class Chatshare implements ChatShareMod {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public MinecraftServer server;
    private Websocket ws;
    private Config config;

    public Chatshare() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        this.config = new Config();
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("chatshare-common.toml"));
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        this.server = event.getServer();
        this.ws = new Websocket(this, this.config);
        this.ws.start();
    }

    @SubscribeEvent
    public void gotChatEvent(ServerChatEvent event) {
        this.ws.chatMessage(event.getUsername(), event.getMessage());
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        this.ws.playerJoined(event.getPlayer().getDisplayName().getString());
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        this.ws.playerLeft(event.getPlayer().getDisplayName().getString());
    }

    public void broadcast(String message) {
        this.server.getPlayerList().sendMessage(new StringTextComponent(message));
    }
}
