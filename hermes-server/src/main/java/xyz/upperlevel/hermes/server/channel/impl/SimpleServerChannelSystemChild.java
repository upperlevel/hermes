package xyz.upperlevel.hermes.server.channel.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.hermes.channel.BaseChannelSystemChild;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.hermes.event.ConnectionCloseEvent;
import xyz.upperlevel.hermes.server.ServerConnection;
import xyz.upperlevel.hermes.server.channel.ServerChannelSystemChild;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class SimpleServerChannelSystemChild extends BaseChannelSystemChild implements ServerChannelSystemChild {
    @Getter
    private final SimpleServerChannelSystem parent;
    private final Set<String> pending = new HashSet<>();
    @Getter
    private ServerConnection connection;

    @Override
    public Set<String> getPending() {
        return Collections.unmodifiableSet(pending);
    }

    @Override
    protected void onWakeup(short id, String name) {
        //System.out.println("Server: wake up! (" + id + " -> " + name + ")");
        Channel channel = parent.get(name);
        if (channel == null)
            pending.add(name);
        else
            onChannelActive(channel);//Call the event
    }

    @Override
    protected void onConnectionClose(ConnectionCloseEvent event) {
        parent.onChildClose(this);
    }

    public void onChannelRegister(Channel channel, ChannelMessagePacket packet) {
        boolean wasPending = pending.remove(channel.getName());

        connection.send(connection.getDefaultChannel(), packet);

        if (wasPending)
            onChannelActive(channel);//The client has already sent the wakeup message
    }

    @Override
    public void init(ServerConnection connection) {
        if (this.connection != null)
            throw new IllegalStateException("Class already initialized");
        this.connection = connection;
        super.init();
    }
}
