package xyz.upperlevel.utils.packet.channel;

import xyz.upperlevel.utils.packet.channel.events.ChannelActiveEvent;
import xyz.upperlevel.utils.packet.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.utils.packet.event.ConnectionEvent;
import xyz.upperlevel.utils.packet.event.impl.ConnectionCloseEvent;

public abstract class BaseChannelSystemChild implements ChannelSystemChild {

    //TODO: Reduce space usage
    protected final Channel[] used = new Channel[ChannelSystem.MAX_IDS];

    protected void init() {
        getConnection().getEventManager().register(ConnectionCloseEvent.class, this::onConnectionClose);
    }

    @Override
    public void onReceive(ChannelMessagePacket packet) {//TODO could be optimized (?)
        Channel ch = used[packet.id & 0xffff];
        if(ch == null) {//Wakeup
            onWakeup(packet.id, new String(packet.message, ChannelMessagePacket.CHARSET));
        } else {//Messae
            onMessage(ch, packet.message);
        }
    }

    protected void onMessage(Channel channel, byte[] message) {
        channel.receive(getConnection(), channel.getProtocol().convert(message));
    }

    protected abstract void onWakeup(short id, String name);

    protected abstract void onConnectionClose(ConnectionCloseEvent event);

    protected void onChannelActive(Channel channel) {
        channel.getEventManager().call(new ConnectionEvent<>(getConnection(), new ChannelActiveEvent()), ChannelActiveEvent.class);
    }
}
