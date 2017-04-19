package xyz.upperlevel.hermes.impl;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.hermes.channel.ChannelSystemChild;
import xyz.upperlevel.hermes.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.utils.event.impl.def.EventManager;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.channel.Channel;

public abstract class BaseConnection implements Connection {
    @Getter
    @Setter
    private Channel defaultChannel;

    @Getter
    private final EventManager eventManager;

    @Getter
    @Setter
    private ChannelSystemChild channelSystemChild;

    public BaseConnection(Channel defaultChannel, EventManager eventManager, ChannelSystemChild channelSystemChild) {
        this.defaultChannel = defaultChannel;
        this.eventManager = eventManager;
        this.channelSystemChild = channelSystemChild;
    }

    public BaseConnection(Channel defaultChannel, ChannelSystemChild channelSystemChild) {
        this(defaultChannel, new EventManager(), channelSystemChild);
    }

    public BaseConnection(EventManager eventManager, ChannelSystemChild channelSystem) {
        this(null, eventManager, channelSystem);
    }

    public BaseConnection(ChannelSystemChild channelSystemChild) {
        this(null, new EventManager(), channelSystemChild);
    }

    @Override
    public void send(Channel channel, Packet message) {
        if(channel == getDefaultChannel())
            send(message);
        else
            send(ChannelMessagePacket.newMessage(channel, message));
    }

    protected abstract void send(Packet packet);
}
