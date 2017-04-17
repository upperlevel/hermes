package xyz.upperlevel.utils.packet.client.impl.netty;

import io.netty.channel.ChannelHandler;
import xyz.upperlevel.utils.packet.Protocol;
import xyz.upperlevel.utils.packet.impl.netty.NettyChannelInitializer;
import xyz.upperlevel.utils.packet.impl.netty.NettyConnection;

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
            public Protocol getProtocol() {
                return connection.getDefaultChannel().getProtocol();
            }
        };
    }
}
