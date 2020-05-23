package com.madhouseminers.chatshareCore;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
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
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class Websocket extends Thread {

    private static final Logger LOGGER = LogManager.getLogger();
    private ChatShareMod cs;
    private Channel ch;
    private EventLoopGroup loop;
    private Bootstrap b;
    private ModConfig config;

    public Websocket(ChatShareMod cs, ModConfig config) {
        super("Chatshare");

        this.config = config;
        this.cs = cs;
    }

    public void sendMessage(String message) {
        LOGGER.info("Sending a plain message to ChatShare service");
        this.ch.writeAndFlush(new TextWebSocketFrame(message));
    }

    public void sendMessage(Message message) {
        LOGGER.info("Sending a packaged message to ChatShare service");
        this.ch.writeAndFlush(new TextWebSocketFrame(message.toJSON()));
    }

    public void run() {
        this.connect();
    }

    public void connect() {
        LOGGER.info("Trying to connect to ChatShare service.");

        URI uri = URI.create("wss://" + config.getServer() + ":" + config.getPort() + "/ws");
        this.loop = new NioEventLoopGroup();

        final WebSocketClientProtocolHandler handler = new WebSocketClientProtocolHandler(
                WebSocketClientHandshakerFactory.newHandshaker(
                        uri, WebSocketVersion.V13, null, false, EmptyHttpHeaders.INSTANCE
                )
        );
        WebsocketHandler wsh = new WebsocketHandler(this, this.cs, this.config);

        this.b = new Bootstrap();
        this.b.group(loop).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                try {
                    ch.pipeline().addLast(
//                            SslContextBuilder.forClient().build().newHandler(ch.alloc(), uri.getHost(), uri.getPort()),
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

    /**
     * Sends the chat message to the chatshare network
     *
     * @param String playerName The display name of the player
     * @param String message The message that the player sent
     */
    public void chatMessage(String playerName, String message) {
        Message msg = new Message(MessageType.MESSAGE);
        msg.setName(playerName).setMessage(message);
        this.sendMessage(msg);
    }

    /**
     * Informs the chatshare network that a player has joined this server
     *
     * @param String playerName The display name of the player
     */
    public void playerJoined(String playerName) {
        Message msg = new Message(MessageType.JOIN);
        msg.setName(playerName);
        this.sendMessage(msg);
    }

    /**
     * Informs the chatshare network that a player has left this server
     *
     * @param String playerName The display name of the player
     */
    public void playerLeft(String playerName) {
        Message msg = new Message(MessageType.LEAVE);
        msg.setName(playerName);
        this.sendMessage(msg);
    }
}
