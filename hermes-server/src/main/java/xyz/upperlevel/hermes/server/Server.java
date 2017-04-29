package xyz.upperlevel.hermes.server;

import xyz.upperlevel.event.impl.def.EventManager;
import xyz.upperlevel.hermes.Endpoint;
import xyz.upperlevel.hermes.channel.Channel;

public interface Server extends Endpoint {
    EventManager getEventManager();

    Channel getDefaultChannel();

    void setDefaultChannel(Channel channel);
}
