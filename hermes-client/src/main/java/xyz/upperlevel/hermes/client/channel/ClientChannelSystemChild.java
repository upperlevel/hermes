package xyz.upperlevel.hermes.client.channel;

import xyz.upperlevel.hermes.client.ClientConnection;
import xyz.upperlevel.hermes.channel.ChannelSystemChild;

public interface ClientChannelSystemChild extends ChannelSystemChild {
    ClientChannelSystem getParent();

    ClientConnection getConnection();
}
