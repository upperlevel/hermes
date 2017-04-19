package xyz.upperlevel.hermes.client.channel;

import xyz.upperlevel.hermes.client.Client;
import xyz.upperlevel.hermes.channel.ChannelSystem;

public interface ClientChannelSystem extends ChannelSystem {
    Client getParent();

    ClientChannelSystemChild getChild();
}
