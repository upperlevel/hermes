package xyz.upperlevel.utils.packet.server.impl.netty;

import io.netty.channel.ChannelHandler;
import lombok.AllArgsConstructor;
import xyz.upperlevel.utils.packet.Protocol;
import xyz.upperlevel.utils.packet.impl.netty.NettyChannelInitializer;

@AllArgsConstructor
public class NettyServerInitializer extends NettyChannelInitializer {
    private final NettyServer server;

    @Override
    protected ConnectionHandler newConnectionHandler() {
        final NettyServerConnection connection = server.newConnection();
        return new ConnectionHandler() {
            @Override
            public ChannelHandler getChannelHandler() {
                return connection.adapter();
            }

            @Override
            public Protocol getProtocol() {
                return connection.getDefaultChannel().getProtocol();
            }
        };
    }
}
