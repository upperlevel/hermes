package xyz.upperlevel.hermes.client.channel.impl;

import lombok.Getter;
import xyz.upperlevel.hermes.channel.BaseChannelSystemChild;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.channel.ChannelSystem;
import xyz.upperlevel.hermes.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.hermes.client.ClientConnection;
import xyz.upperlevel.hermes.client.channel.ClientChannelSystemChild;
import xyz.upperlevel.hermes.event.ConnectionCloseEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleClientChannelSystemChild extends BaseChannelSystemChild implements ClientChannelSystemChild {
    @Getter
    private final SimpleClientChannelSystem parent;

    private final Map<String, Integer> pending = new HashMap<>();

    public SimpleClientChannelSystemChild(SimpleClientChannelSystem parent) {
        this.parent = parent;
    }

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

        if (stored_id != null) {
            channel.setId(stored_id.shortValue());
            used.set(channel.getId() & 0xffff, channel);
            onChannelActive(channel);//Call the event
        }
    }

    @Override
    protected void onWakeup(short id, String name) {
        //System.out.println("Client: wake up! (" + id + " -> " + name + ")");
        Channel channel = getParent().get(name);
        if (channel == null) {
            pending.put(name, (int) id);
        } else {
            channel.setId(id);
            used.set(channel.getId() & 0xffff, channel);
            onChannelActive(channel);//Call the event
        }
    }

    @Override
    protected void onConnectionClose(ConnectionCloseEvent event) {
    }
}
