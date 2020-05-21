package com.madhouseminers.chatshare;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class Websocket extends Thread {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final URI uri = URI.create("wss://" + Config.SERVER.get() + ":" + Config.PORT.get() + "/ws");
    private Chatshare cs;
    private Channel ch;
    private EventLoopGroup loop;
    private Bootstrap b;

    public Websocket(Chatshare cs) {
        super("Chatshare");

        this.cs = cs;
    }

    public void sendMessage(String message) {
        LOGGER.info("Sending a message to ChatShare service: " + message);
        this.ch.writeAndFlush(new TextWebSocketFrame(message));
    }

    public void run() {
        this.connect();
    }

    public void connect() {
        LOGGER.info("Trying to connect to ChatShare service.");
        this.loop = new NioEventLoopGroup();

        final WebSocketClientProtocolHandler handler = new WebSocketClientProtocolHandler(
                WebSocketClientHandshakerFactory.newHandshaker(
                        uri, WebSocketVersion.V13, null, false, EmptyHttpHeaders.INSTANCE
                )
        );
        WebsocketHandler wsh = new WebsocketHandler(this, this.cs);

        this.b = new Bootstrap();
        this.b.group(loop).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                try {
                    ch.pipeline().addLast(
                            new HttpClientCodec(),
                            new HttpObjectAggregator(8192),
                            handler,
                            wsh
                    );
                } catch (Exception e) {
                    LOGGER.error("Failed to create SSL context: " + e.getMessage());
                }
            }
        });

        ChannelFuture bf = this.b.connect(uri.getHost(), uri.getPort());
        this.ch = bf.channel();
        bf.addListener((ChannelFuture f) -> {
            if (!f.isSuccess()) {
                LOGGER.warn("Unable to connect to ChatShare service. Retrying in 10 seconds.");
                f.channel().eventLoop().schedule(this::reconnect, 10, TimeUnit.SECONDS);
            } else {
                LOGGER.info("Connected to ChatShare");
            }
        });
    }

    public void reconnect() {
        this.close();
        try {
            sleep(1000);
        } catch (Exception e) {
            LOGGER.info("Sleep interrupted");
        }
        this.connect();
    }

    public void close() {
        try {
            this.ch.close();
            this.loop.shutdownGracefully();
        } catch (Exception e) {
            LOGGER.error("Unable to close ChatShare connection: " + e.getMessage());
        }
    }
}
