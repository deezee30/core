/*
 * rv_core
 * 
 * Created on 10 July 2017 at 11:15 PM.
 */

package com.maulss.core.net.communication.netty;

import com.maulss.core.net.communication.CoreServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.Future;
import org.apache.commons.lang3.Validate;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

public class NettyClient implements Closeable {

    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;

    private final SslContext sslCtx;
    private final CoreServer server;

    // START // def: 127.0.0.1:8000
    public NettyClient(final SslContext sslCtx,
                       final CoreServer server) {
        this.sslCtx = Validate.notNull(sslCtx, "sslCtx");
        this.server = Validate.notNull(server, "server");
    }

    public void run() throws Exception {
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyChannelInitializer(sslCtx, server))
                    .option(ChannelOption.SO_BACKLOG, 128);

            /* TODO logger
            Core.log(
                    "Opening netty client `%s` on port %s",
                    server.getInternalName(),
                    server.getPort()
            );*/

            channel = bootstrap.connect(server.getAddress(), server.getPort()).sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                channel.write(in.readLine() + "\r\n");
            }

            // Bind and start to accept incoming connections.
            // cf = bootstrap.bind(port).sync();

        } finally {
            close();
        }
    }

    public CoreServer getServer() {
        return server;
    }

    @Override
    public void close() throws IOException {
        // Wait until the server socket is closed.
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        Future<?> f = group.shutdownGracefully();

        /* TODO logger
        f.addListener((GenericFutureListener) future -> Core.log(
                "Closed netty client `%s` on port %s",
                server.getInternalName(),
                server.getPort()
        ));*/
    }
}