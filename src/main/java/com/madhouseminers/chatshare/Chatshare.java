package com.madhouseminers.chatshare;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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

        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] pwdArray = "password".toCharArray();
            ks.load(null, pwdArray);
            ks.setCertificateEntry("chatshare.madhouseminers.com", CertificateFactory.getInstance("X.509").generateCertificate(new FileInputStream("/home/steve/GolandProjects/chatshare-server/ca.cer")));
            try (FileOutputStream fos = new FileOutputStream("newKeyStoreFileName.jks")) {
                ks.store(fos, pwdArray);
            }
        } catch (Exception e) {
            LOGGER.error("Error loading keystore");
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
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
