package com.madhouseminers.chatshare;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private Chatshare cs;
    private static final Logger LOGGER = LogManager.getLogger();

    public WebsocketHandler(Chatshare cs) {
        this.cs = cs;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        LOGGER.info("Received a message from the ChatShare service: " + msg.text());
        this.cs.server.getPlayerList().sendMessage(new StringTextComponent(msg.text()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOGGER.info("Channel Inactive");
        ctx.channel().close();
        ctx.close();
        this.cs.reconnectWebsocket();
    }
}
