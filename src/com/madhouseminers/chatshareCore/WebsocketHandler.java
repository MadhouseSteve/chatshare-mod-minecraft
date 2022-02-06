package com.madhouseminers.chatshareCore;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;

class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private ChatShareMod cs;
    private Websocket ws;
    private ModConfig config;
    private static final Logger LOGGER = LogManager.getLogger();
    private Pinger pinger;

    public WebsocketHandler(Websocket ws, ChatShareMod cs, ModConfig config) {
        this.ws = ws;
        this.cs = cs;
        this.config = config;
        this.pinger = new Pinger(ws);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        LOGGER.debug("Received a message from the ChatShare service: " + msg.text());
        if (msg.text().equals("HELLO")) {
            LOGGER.debug(this.config.getName() + "::" + new String(Base64.getDecoder().decode(this.config.getPassword())));
            this.ws.sendMessage(this.config.getName() + "::" + new String(Base64.getDecoder().decode(this.config.getPassword())));
        } else if (msg.text().equals("WELCOME")) {
            this.ws.sendMessage("VERSION::2.1");
            this.pinger.start();
        } else {
            Message message = Message.fromJSON(msg.text());
            if (message.getType() == MessageType.MESSAGE) {
                this.cs.broadcast("[" + message.getSender() + "] <" + message.getName() + "> " + message.getMessage());
            } else if (message.getType() == MessageType.PING) {
                this.ws.sendMessage(new Message(MessageType.PONG));
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOGGER.info("Connection to ChatShare lost.");
        this.pinger.end();
        ctx.channel().close();
        ctx.close();
        this.ws.reconnect();
    }
}
