package xyz.upperlevel.utils.packet.client.channel;

import xyz.upperlevel.utils.packet.client.Client;
import xyz.upperlevel.utils.packet.channel.ChannelSystem;

public interface ClientChannelSystem extends ChannelSystem {
    Client getParent();

    ClientChannelSystemChild getChild();
}
