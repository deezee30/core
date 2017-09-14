/*
 * Part of core.
 * 
 * Created on 14 July 2017 at 1:17 AM.
 */

package com.maulss.core.net.communication.netty;

import com.maulss.core.net.communication.command.CommandRegistry;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.lang3.Validate;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final ChannelGroup channels;
    private final CommandRegistry cmdReg;

    public NettyServerInitializer(final SslContext sslCtx,
                                  final ChannelGroup channels,
                                  final CommandRegistry cmdReg) {
        this.sslCtx = Validate.notNull(sslCtx, "sslCtx");
        this.channels = Validate.notNull(channels, "channels");
        this.cmdReg = Validate.notNull(cmdReg, "cmdReg");
    }

    @Override
    public void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        pipeline.addLast(sslCtx.newHandler(channel.alloc()));

        // On top of the SSL handler, add the text line codec.
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        // and then business logic.
        pipeline.addLast(new NettyServerHandler(channels, cmdReg));
    }
}