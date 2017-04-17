package xyz.upperlevel.utils.packet.server.channel.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.utils.packet.channel.BaseChannelSystem;
import xyz.upperlevel.utils.packet.channel.Channel;
import xyz.upperlevel.utils.packet.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.utils.packet.server.Server;
import xyz.upperlevel.utils.packet.server.channel.ServerChannelSystem;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class SimpleServerChannelSystem extends BaseChannelSystem implements ServerChannelSystem {
    @Getter
    private final Server parent;

    private final Set<SimpleServerChannelSystemChild> childs = new HashSet<>();

    @Override
    public SimpleServerChannelSystemChild createChild() {
        SimpleServerChannelSystemChild child = new SimpleServerChannelSystemChild(this);
        childs.add(child);
        return child;
    }

    @Override
    protected void register0(Channel channel) {
        short id = (short) useNextId();
        channel.setId(id);
        id_map[id & 0xffff] = channel;
        ChannelMessagePacket packet = ChannelMessagePacket.newWakeupPacket(id, channel.getName());
        for(SimpleServerChannelSystemChild ch : childs)
            ch.onChannelRegister(channel, packet);
    }

    public void onChildClose(SimpleServerChannelSystemChild child) {
        childs.remove(child);
    }
}
