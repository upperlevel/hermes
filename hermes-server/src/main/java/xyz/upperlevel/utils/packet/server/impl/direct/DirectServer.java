package xyz.upperlevel.utils.packet.server.impl.direct;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.utils.event.impl.def.EventManager;
import xyz.upperlevel.utils.packet.Connection;
import xyz.upperlevel.utils.packet.channel.Channel;
import xyz.upperlevel.utils.packet.server.Server;
import xyz.upperlevel.utils.packet.server.channel.ServerChannelSystem;
import xyz.upperlevel.utils.packet.server.channel.ServerChannelSystemChild;
import xyz.upperlevel.utils.packet.server.channel.impl.SimpleServerChannelSystem;

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
