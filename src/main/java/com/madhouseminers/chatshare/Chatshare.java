package com.madhouseminers.chatshare;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
    }

    public void connectWebsocket() {
        this.ws = new Websocket(this);
    }

    public void reconnectWebsocket() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                this.ws = new Websocket(this);
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        LOGGER.info("HELLO from server starting");
        this.connectWebsocket();
    }

    @SubscribeEvent
    public void gotChatEvent(ServerChatEvent event) {
        this.ws.sendMessage("<" + event.getUsername() + "> " + event.getMessage());
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
