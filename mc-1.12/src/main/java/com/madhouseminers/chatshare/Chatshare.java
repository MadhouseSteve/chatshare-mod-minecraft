package com.madhouseminers.chatshare;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.madhouseminers.chatshareCore.Websocket;
import com.madhouseminers.chatshareCore.ChatShareMod;

@Mod(
        modid = Chatshare.MOD_ID,
        name = Chatshare.MOD_NAME,
        version = Chatshare.VERSION,
        acceptableRemoteVersions = "*"
)
public class Chatshare implements ChatShareMod {

    public static final String MOD_ID = "chatshare";
    public static final String MOD_NAME = "Chatshare";
    public static final String VERSION = "2.0.0-1.12.2";

    private static final Logger LOGGER = LogManager.getLogger();
    public MinecraftServer server;
    private Websocket ws;
    private Config config;

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static Chatshare INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        this.config = new Config();
        this.config.loadConfig(event.getSuggestedConfigurationFile());
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        this.server = FMLCommonHandler.instance().getMinecraftServerInstance();
        this.ws = new Websocket(this, this.config);
        this.ws.start();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        this.ws.sendMessage("<" + event.getUsername() + "> " + event.getMessage());
    }

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        this.ws.sendMessage(event.player.getDisplayNameString() + " has joined " + this.config.getName());
    }

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        this.ws.sendMessage(event.player.getDisplayNameString() + " has left " + this.config.getName());
    }

    public void broadcast(String message) {
        this.server.getPlayerList().sendMessage(new TextComponentString(message));
    }
}
