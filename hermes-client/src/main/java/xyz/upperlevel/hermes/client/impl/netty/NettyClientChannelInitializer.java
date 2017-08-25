package xyz.upperlevel.hermes.client.impl.netty;

import io.netty.channel.ChannelHandler;
import xyz.upperlevel.hermes.PacketConverter;
import xyz.upperlevel.hermes.impl.netty.NettyChannelInitializer;
import xyz.upperlevel.hermes.impl.netty.NettyConnection;

public class NettyClientChannelInitializer extends NettyChannelInitializer {
    private final NettyConnection connection;

    public NettyClientChannelInitializer(NettyConnection connection) {
        this.connection = connection;
    }

    @Override
    protected ConnectionHandler newConnectionHandler() {
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
