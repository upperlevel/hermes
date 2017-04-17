package xyz.upperlevel.utils.packet;

import xyz.upperlevel.utils.event.impl.def.EventManager;
import xyz.upperlevel.utils.packet.channel.Channel;
import xyz.upperlevel.utils.packet.channel.ChannelSystemChild;

public interface Connection {
    Endpoint getParent();

    void send(Channel channel, Packet message);

    Channel getDefaultChannel();

    void setDefaultChannel(Channel channel);

    ChannelSystemChild getChannelSystemChild();

    EventManager getEventManager();

    boolean isOpen();

    void close() throws InterruptedException;
}