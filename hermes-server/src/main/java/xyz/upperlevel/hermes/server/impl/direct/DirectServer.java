package xyz.upperlevel.hermes.server.impl.direct;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.hermes.server.Server;
import xyz.upperlevel.hermes.server.channel.ServerChannelSystem;
import xyz.upperlevel.hermes.server.channel.ServerChannelSystemChild;
import xyz.upperlevel.hermes.server.channel.impl.SimpleServerChannelSystem;
import xyz.upperlevel.event.impl.def.EventManager;
import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.hermes.channel.Channel;

public class DirectServer implements Server {

    @Getter
    private final ServerChannelSystem channelSystem;

    @Getter
    @Setter
    private Channel defaultChannel;

    @Getter
    private final EventManager eventManager;

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

    public DirectServerConnection newConnection(Connection connection) {
        ServerChannelSystemChild child = channelSystem.createChild();
        DirectServerConnection conn =  new DirectServerConnection(this, child);
        conn.setDefaultChannel(defaultChannel);
        conn.setOther(connection);
        child.init(conn);
        return conn;
    }

    @Override
    public void stop() {
        throw new IllegalStateException("Cannot stop a direct server");
    }
}
