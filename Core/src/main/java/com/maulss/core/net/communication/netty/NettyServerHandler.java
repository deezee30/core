/*
 * Part of core.
 * 
 * Created on 15 July 2017 at 2:48 PM.
 */

package com.maulss.core.net.communication.netty;

import com.maulss.core.net.communication.command.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslHandler;

import java.util.Optional;

/**
 * Handles a server-side channel.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    private final ChannelGroup channels;
    private final CommandRegistry registry;

    public NettyServerHandler(final ChannelGroup channels,
                              final CommandRegistry registry) {
        this.channels = channels;
        this.registry = registry;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Once session is secured, register the channel to the global channel
        // list so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                future -> channels.add(ctx.channel()));
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final String msg) throws Exception {
        Command cmd = CommandParser.parse(msg);
        Optional<Command> registeredCmd = registry.getCommand(cmd.getName());
        if (registeredCmd.isPresent()) {
            cmd = registeredCmd.get();
            CommandProcess process = registry.getProcess(cmd).get();
            Optional<ReturnCommand> returnCmd = process.process();
            if (returnCmd.isPresent()) {
                if (!cmd.getType().equals(CommandType.QUERY)) {
                    throw new CommandException(
                            "Return is present for expected QUERY command `%s` but got `%s` instead",
                            cmd.getName(), cmd.getType()
                    );
                }

                ctx.writeAndFlush(registeredCmd.get().toJson());
            }
        } else {
            throw new CommandException("Received command `%s` that hasn't been registered");
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}