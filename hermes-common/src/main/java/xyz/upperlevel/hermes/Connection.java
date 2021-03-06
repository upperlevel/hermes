package xyz.upperlevel.hermes;

import xyz.upperlevel.event.EventManager;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.channel.ChannelSystemChild;

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