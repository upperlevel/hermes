package xyz.upperlevel.utils.packet.impl.direct;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.utils.event.impl.def.EventManager;
import xyz.upperlevel.utils.packet.Connection;
import xyz.upperlevel.utils.packet.Packet;
import xyz.upperlevel.utils.packet.channel.Channel;
import xyz.upperlevel.utils.packet.channel.ChannelSystemChild;
import xyz.upperlevel.utils.packet.impl.BaseConnection;

public abstract class DirectConnection extends BaseConnection {
    @Getter
    @Setter
    private Connection other;

    @Getter
    @Setter
    private boolean copy = true;


    public DirectConnection(Channel defaultChannel, EventManager eventManager, ChannelSystemChild channelSystemChild) {
        super(defaultChannel, eventManager, channelSystemChild);
    }

    public DirectConnection(Channel defaultChannel, ChannelSystemChild channelSystemChild) {
        super(defaultChannel, channelSystemChild);
    }

    public DirectConnection(EventManager eventManager, ChannelSystemChild channelSystemChild) {
        super(eventManager, channelSystemChild);
    }

    public DirectConnection(ChannelSystemChild channelSystemChild) {
        super(channelSystemChild);
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() {
        throw new IllegalStateException("Cannot close a direct connection!");
    }

    @Override
    protected void send(Packet packet) {
        if(getDefaultChannel() == null)
            throw new IllegalStateException("Default channel not set!");

        final Channel otherCh = other.getDefaultChannel();
        if(copy) {
            byte[] msg = getDefaultChannel().getProtocol().convert(packet);
            packet = otherCh.getProtocol().convert(msg);
        }
        otherCh.receive(other, packet);
    }
}
