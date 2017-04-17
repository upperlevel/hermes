package xyz.upperlevel.utils.packet.channel;

import xyz.upperlevel.utils.packet.Connection;
import xyz.upperlevel.utils.packet.channel.packets.ChannelMessagePacket;

import java.util.Set;

public interface ChannelSystemChild {
    ChannelSystem getParent();

    void onReceive(ChannelMessagePacket packet);

    Set<String> getPending();

    //TODO: Collection<Channel> get();

    Connection getConnection();
}
