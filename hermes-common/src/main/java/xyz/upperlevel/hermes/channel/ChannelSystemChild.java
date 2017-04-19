package xyz.upperlevel.hermes.channel;

import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.hermes.channel.packets.ChannelMessagePacket;

import java.util.Set;

public interface ChannelSystemChild {
    ChannelSystem getParent();

    void onReceive(ChannelMessagePacket packet);

    Set<String> getPending();

    //TODO: Collection<Channel> get();

    Connection getConnection();
}
