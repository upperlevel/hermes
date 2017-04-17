package xyz.upperlevel.utils.packet.server;

import xyz.upperlevel.utils.event.impl.def.EventManager;
import xyz.upperlevel.utils.packet.Endpoint;
import xyz.upperlevel.utils.packet.channel.Channel;

public interface Server extends Endpoint {
    EventManager getEventManager();

    Channel getDefaultChannel();

    void setDefaultChannel(Channel channel);
}
