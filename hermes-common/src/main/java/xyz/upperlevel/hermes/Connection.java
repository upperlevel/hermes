package xyz.upperlevel.hermes;

import xyz.upperlevel.hermes.channel.ChannelSystemChild;
import xyz.upperlevel.event.impl.def.EventManager;
import xyz.upperlevel.hermes.channel.Channel;

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