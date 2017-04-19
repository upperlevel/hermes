package xyz.upperlevel.hermes.server.channel;

import xyz.upperlevel.hermes.channel.ChannelSystemChild;
import xyz.upperlevel.hermes.server.ServerConnection;

public interface ServerChannelSystemChild extends ChannelSystemChild{
    ServerChannelSystem getParent();

    ServerConnection getConnection();

    void init(ServerConnection connection);
}
