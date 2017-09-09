package xyz.upperlevel.hermes.impl.direct;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.event.EventManager;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.channel.ChannelSystemChild;
import xyz.upperlevel.hermes.impl.BaseConnection;

public abstract class DirectConnection extends BaseConnection {
    @Getter
    @Setter
    private DirectConnection other;

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
        if (getDefaultChannel() == null)
            throw new IllegalStateException("Default channel not set!");

        final Channel otherCh = other.getDefaultChannel();
        if (copy) {
            ByteBuf buffer = Unpooled.buffer();

            getDefaultChannel().getProtocol().toData(packet, buffer);
            packet = otherCh.getProtocol().fromData(buffer);
        }
        otherCh.receive(other, packet);
    }
}
