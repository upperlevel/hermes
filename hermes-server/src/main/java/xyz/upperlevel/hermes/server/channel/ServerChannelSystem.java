package xyz.upperlevel.hermes.server.channel;

import xyz.upperlevel.hermes.channel.ChannelSystem;
import xyz.upperlevel.hermes.server.Server;

public interface ServerChannelSystem extends ChannelSystem{
    @Override
    Server getParent();

    ServerChannelSystemChild createChild();
}
