package com.madhouseminers.chatshare;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private Chatshare cs;
    private static final Logger LOGGER = LogManager.getLogger();

    public WebsocketHandler(Chatshare cs) {
        this.cs = cs;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        LOGGER.info("Received a message from the ChatShare service: " + msg.text());
        this.cs.server.getPlayerList().sendMessage(new StringTextComponent(msg.text()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.cs.reconnectWebsocket();
    }
}
