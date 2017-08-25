package xyz.upperlevel.hermes.server.impl.netty;

import io.netty.channel.ChannelHandler;
import lombok.AllArgsConstructor;
import xyz.upperlevel.hermes.PacketConverter;
import xyz.upperlevel.hermes.impl.netty.NettyChannelInitializer;

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
            public PacketConverter getProtocol() {
                return connection.getDefaultChannel().getProtocol();
            }
        };
    }
}
