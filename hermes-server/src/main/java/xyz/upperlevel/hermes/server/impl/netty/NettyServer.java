package xyz.upperlevel.hermes.server.impl.netty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.event.impl.ConnectionOpenEvent;
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
    @Setter
    private Channel defaultChannel;

    private Set<NettyServerConnection> connections = new HashSet<>();

    @Getter
    private final NettyServerInitializer initializer = new NettyServerInitializer(this);

    @Getter
    private final ServerChannelSystem channelSystem = new SimpleServerChannelSystem(this);

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

    public NettyServerConnection newConnection() {
        ServerChannelSystemChild child = channelSystem.createChild();
        NettyServerConnection conn = new NettyServerConnection(this, child);
        child.init(conn);
        ConnectionOpenEvent event = new ConnectionOpenEvent(conn);
        if(getEventManager().call(event)) {
            connections.add(conn);
            return conn;
        } else
            return null;
    }
}
