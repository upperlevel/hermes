package xyz.upperlevel.utils.packet.client.channel;

import xyz.upperlevel.utils.packet.client.ClientConnection;
import xyz.upperlevel.utils.packet.channel.ChannelSystemChild;

public interface ClientChannelSystemChild extends ChannelSystemChild {
    ClientChannelSystem getParent();

    ClientConnection getConnection();
}
