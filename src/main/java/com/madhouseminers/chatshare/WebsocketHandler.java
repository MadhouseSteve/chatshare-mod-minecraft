package com.madhouseminers.chatshare;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private Chatshare cs;
    private Websocket ws;
    private static final Logger LOGGER = LogManager.getLogger();

    public WebsocketHandler(Websocket ws, Chatshare cs) {
        this.ws = ws;
        this.cs = cs;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        LOGGER.info("Received a message from the ChatShare service: " + msg.text());
        if (msg.text().equals("HELLO")) {
            this.ws.sendMessage(Config.NAME.get());
        } else {
            this.cs.server.getPlayerList().sendMessage(new StringTextComponent(msg.text()));
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
