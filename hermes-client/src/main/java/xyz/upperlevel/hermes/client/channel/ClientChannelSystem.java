package xyz.upperlevel.hermes.client.channel;

import xyz.upperlevel.hermes.channel.ChannelSystem;
import xyz.upperlevel.hermes.client.Client;

public interface ClientChannelSystem extends ChannelSystem {
    Client getParent();

    ClientChannelSystemChild getChild();
}
