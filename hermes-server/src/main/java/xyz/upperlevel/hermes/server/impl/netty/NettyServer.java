package xyz.upperlevel.hermes.server.impl.netty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.event.ConnectionOpenEvent;
import xyz.upperlevel.hermes.server.channel.ServerChannelSystem;
import xyz.upperlevel.hermes.server.channel.ServerChannelSystemChild;
import xyz.upperlevel.hermes.server.channel.impl.SimpleServerChannelSystem;
import xyz.upperlevel.hermes.server.impl.BaseServer;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class NettyServer extends BaseServer {
    private final Runnable stop;
    @Getter
    private final NettyServerInitializer initializer = new NettyServerInitializer(this);
    @Getter
    private final ServerChannelSystem channelSystem = new SimpleServerChannelSystem(this);
    @Getter
    @Setter
    private Channel defaultChannel;
    private Set<NettyServerConnection> connections = new HashSet<>();

    public void onOpen(NettyServerConnection connection) {
        assert connection.getParent() == this;
        connections.add(connection);
    }

    public void onClose(NettyServerConnection connection) {
        assert connection.getParent() == this;
        connections.remove(connection);
    }

    @Override
    public void stop() {
        stop.run();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Connection> getConnections() {
        return (Set) connections;
    }

    public NettyServerConnection newConnection() {
        ServerChannelSystemChild child = channelSystem.createChild();
        NettyServerConnection conn = new NettyServerConnection(this, child);
        child.init(conn);
        if (conn.onOpen()) {
            connections.add(conn);
            return conn;
        } else
            return null;
    }
}
