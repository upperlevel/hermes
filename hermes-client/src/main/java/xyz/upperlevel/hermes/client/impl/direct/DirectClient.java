package xyz.upperlevel.hermes.client.impl.direct;

import lombok.Getter;
import xyz.upperlevel.event.EventManager;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.client.Client;
import xyz.upperlevel.hermes.client.channel.ClientChannelSystem;
import xyz.upperlevel.hermes.client.channel.impl.SimpleClientChannelSystem;

public class DirectClient implements Client {

    @Getter
    private final ClientChannelSystem channelSystem;

    @Getter
    private final DirectClientConnection connection;

    public DirectClient(ClientChannelSystem channelSystem, DirectClientConnection connection) {
        this.channelSystem = channelSystem;
        this.connection = connection;
    }

    public DirectClient(Channel defaultChannel, ClientChannelSystem channelSystem, EventManager eventManager) {
        this.channelSystem = channelSystem;
        this.connection = new DirectClientConnection(this, defaultChannel, eventManager, channelSystem.getChild());
    }

    public DirectClient() {
        this(null);
    }

    public DirectClient(Channel defaultChannel) {
        this.channelSystem = new SimpleClientChannelSystem(this);
        this.connection = new DirectClientConnection(this, defaultChannel, new EventManager(), channelSystem.getChild());
        channelSystem.init();
    }

    @Override
    public void stop() {
        throw new IllegalStateException("Cannot stop a direct client");
    }
}
