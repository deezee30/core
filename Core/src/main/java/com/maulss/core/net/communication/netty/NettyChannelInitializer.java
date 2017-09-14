/*
 * Part of core.
 * 
 * Created on 11 July 2017 at 12:09 AM.
 */

package com.maulss.core.net.communication.netty;

import com.maulss.core.net.communication.CoreServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.lang3.Validate;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final CoreServer server;

    public NettyChannelInitializer(final SslContext sslCtx,
                                   final CoreServer server) {
        this.sslCtx = Validate.notNull(sslCtx, "sslCtx");
        this.server = Validate.notNull(server, "server");
    }

    @Override
    public void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        pipeline.addLast(sslCtx.newHandler(channel.alloc(), server.getAddress(), server.getPort()));

        // On top of the SSL handler, add the text line codec.
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        // and then business logic.
        pipeline.addLast(new NettyClientHandler());
    }
}