package xyz.upperlevel.utils.packet.impl;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.utils.event.impl.def.EventManager;
import xyz.upperlevel.utils.packet.Connection;
import xyz.upperlevel.utils.packet.Packet;
import xyz.upperlevel.utils.packet.channel.Channel;
import xyz.upperlevel.utils.packet.channel.ChannelSystemChild;
import xyz.upperlevel.utils.packet.channel.packets.ChannelMessagePacket;

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
