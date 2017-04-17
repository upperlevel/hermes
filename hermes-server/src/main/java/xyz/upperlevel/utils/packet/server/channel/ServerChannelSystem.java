package xyz.upperlevel.utils.packet.server.channel;

import xyz.upperlevel.utils.packet.channel.ChannelSystem;
import xyz.upperlevel.utils.packet.server.Server;

public interface ServerChannelSystem extends ChannelSystem{
    @Override
    Server getParent();

    ServerChannelSystemChild createChild();
}
