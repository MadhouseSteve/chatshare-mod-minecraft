package com.madhouseminers.chatshareCore;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;

public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private ChatShareMod cs;
    private Websocket ws;
    private ModConfig config;
    private static final Logger LOGGER = LogManager.getLogger();

    public WebsocketHandler(Websocket ws, ChatShareMod cs, ModConfig config) {
        this.ws = ws;
        this.cs = cs;
        this.config = config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        LOGGER.info("Received a message from the ChatShare service: " + msg.text());
        if (msg.text().equals("HELLO")) {
            this.ws.sendMessage(this.config.getName() + "::" + new String(Base64.getDecoder().decode(this.config.getPassword())), true);
//            this.ws.sendMessage("version::" + new String(Base64.getDecoder().decode(this.config.getVersion)), true);
        } else {
            this.cs.broadcast(msg.text());
            // server.getPlayerList().sendMessage new TextComponentString(msg.text())
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOGGER.info("Connection to ChatShare lost.");
        ctx.channel().close();
        ctx.close();
        this.ws.reconnect();
    }
}
