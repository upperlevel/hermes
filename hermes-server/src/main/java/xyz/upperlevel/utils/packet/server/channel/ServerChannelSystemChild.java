package xyz.upperlevel.utils.packet.server.channel;

import xyz.upperlevel.utils.packet.channel.ChannelSystemChild;
import xyz.upperlevel.utils.packet.server.ServerConnection;

public interface ServerChannelSystemChild extends ChannelSystemChild{
    ServerChannelSystem getParent();

    ServerConnection getConnection();

    void init(ServerConnection connection);
}
