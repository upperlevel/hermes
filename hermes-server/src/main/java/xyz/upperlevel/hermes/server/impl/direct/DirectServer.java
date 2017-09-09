package xyz.upperlevel.hermes.server.impl.direct;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.event.EventManager;
import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.event.ConnectionOpenEvent;
import xyz.upperlevel.hermes.impl.direct.DirectConnection;
import xyz.upperlevel.hermes.server.Server;
import xyz.upperlevel.hermes.server.channel.ServerChannelSystem;
import xyz.upperlevel.hermes.server.channel.ServerChannelSystemChild;
import xyz.upperlevel.hermes.server.channel.impl.SimpleServerChannelSystem;

import java.util.HashSet;
import java.util.Set;

public class DirectServer implements Server {

    @Getter
    private final ServerChannelSystem channelSystem;
    @Getter
    private final EventManager eventManager;
    @Getter
    @Setter
    private Channel defaultChannel;
    private Set<DirectServerConnection> connections = new HashSet<>();

    public DirectServer(Channel defaultChannel, ServerChannelSystem channelSystem, EventManager eventManager) {
        this.channelSystem = channelSystem;
        this.defaultChannel = defaultChannel;
        this.eventManager = eventManager;
    }

    public DirectServer() {
        channelSystem = new SimpleServerChannelSystem(this);
        defaultChannel = null;
        eventManager = new EventManager();
    }

    public DirectServer(Channel defaultChannel) {
        this.channelSystem = new SimpleServerChannelSystem(this);
        this.defaultChannel = defaultChannel;
        this.eventManager = new EventManager();
    }

    public DirectServer(ServerChannelSystem channelSystem) {
        this.channelSystem = channelSystem;
        this.defaultChannel = null;
        this.eventManager = new EventManager();
    }

    public DirectServerConnection newConnection(DirectConnection connection, boolean copy) {
        ServerChannelSystemChild child = channelSystem.createChild();
        DirectServerConnection conn = new DirectServerConnection(this, child);
        conn.setDefaultChannel(defaultChannel);
        conn.setCopy(copy);
        conn.setOther(connection);
        connection.setOther(conn);
        child.init(conn);
        ConnectionOpenEvent event = new ConnectionOpenEvent(conn);
        if (getEventManager().call(event)) {
            connections.add(conn);
            return conn;
        } else
            return null;
    }

    public DirectServerConnection newConnection(DirectConnection connection) {
        return newConnection(connection, true);
    }

    @Override
    public void stop() {
        throw new IllegalStateException("Cannot stop a direct server");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Connection> getConnections() {
        return (Set) connections;
    }
}
