package xyz.upperlevel.utils.packet.client.channel.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.utils.packet.channel.BaseChannelSystemChild;
import xyz.upperlevel.utils.packet.channel.Channel;
import xyz.upperlevel.utils.packet.channel.ChannelSystem;
import xyz.upperlevel.utils.packet.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.utils.packet.client.ClientConnection;
import xyz.upperlevel.utils.packet.client.channel.ClientChannelSystemChild;
import xyz.upperlevel.utils.packet.event.impl.ConnectionCloseEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class SimpleClientChannelSystemChild extends BaseChannelSystemChild implements ClientChannelSystemChild {
    @Getter
    private final SimpleClientChannelSystem parent;

    private final Map<String, Integer> pending = new HashMap<>();

    @Override
    public Set<String> getPending() {
        return Collections.unmodifiableSet(pending.keySet());
    }

    @Override
    public ClientConnection getConnection() {
        return parent.getParent().getConnection();
    }

    public void onChannelRegister(Channel channel) {
        Integer stored_id = pending.remove(channel.getName());

        getConnection().send(
                getConnection().getDefaultChannel(),
                ChannelMessagePacket.newWakeupPacket((short) ChannelSystem.LAST_ID, channel.getName())
        );

        if(stored_id != null) {
            channel.setId(stored_id.shortValue());
            used[channel.getId() & 0xffff] = channel;
            onChannelActive(channel);//Call the event
        }
    }

    @Override
    protected void onWakeup(short id, String name) {
        //System.out.println("Client: wake up! (" + id + " -> " + name + ")");
        Channel channel = getParent().get(name);
        if(channel == null) {
            pending.put(name, (int) id);
        } else {
            channel.setId(id);
            used[channel.getId() & 0xffff] = channel;
            onChannelActive(channel);//Call the event
        }
    }

    @Override
    protected void onConnectionClose(ConnectionCloseEvent event) {}
}
