package com.madhouseminers.chatshare;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;

public class Websocket {

    private static final Logger LOGGER = LogManager.getLogger();
    private Chatshare cs;
    private Channel ch;

    public Websocket(Chatshare cs) {
        this.cs = cs;
        EventLoopGroup loop = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        URI uri = URI.create("ws://" + Config.SERVER.get() + ":" + Config.PORT.get() + "/ws");

        final WebSocketClientProtocolHandler handler = new WebSocketClientProtocolHandler(
                WebSocketClientHandshakerFactory.newHandshaker(
                        uri, WebSocketVersion.V13, null, false, EmptyHttpHeaders.INSTANCE
                )
        );

        WebsocketHandler wsh = new WebsocketHandler(this.cs);

        b.group(loop).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(
                        new HttpClientCodec(),
                        new HttpObjectAggregator(8192),
                        handler,
                        wsh
                );
            }
        });
        try {
            LOGGER.info("Attempting to connect to ChatShare service");
            this.ch = b.connect(uri.getHost(), uri.getPort()).sync().channel();
            LOGGER.info("Connected to ChatShare service");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            this.cs.reconnectWebsocket();
        }
    }

    public void sendMessage(String message) {
        LOGGER.info("Sending a message to ChatShare service: " + message);
        this.ch.writeAndFlush(new TextWebSocketFrame(message));
    }
}
