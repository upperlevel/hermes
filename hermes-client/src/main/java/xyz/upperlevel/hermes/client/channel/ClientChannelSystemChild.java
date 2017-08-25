package xyz.upperlevel.hermes.client.channel;

import xyz.upperlevel.hermes.channel.ChannelSystemChild;
import xyz.upperlevel.hermes.client.ClientConnection;

public interface ClientChannelSystemChild extends ChannelSystemChild {
    ClientChannelSystem getParent();

    ClientConnection getConnection();
}
