package xyz.upperlevel.hermes.channel;

import io.netty.buffer.Unpooled;
import xyz.upperlevel.hermes.channel.events.ChannelActiveEvent;
import xyz.upperlevel.hermes.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.hermes.event.ConnectionCloseEvent;
import xyz.upperlevel.hermes.util.DynamicArray;

/**
 * The ChannelSystem & ChannelSystemChild classes help with the channel protocol:
 * the protocol is set-up in a way that with one packet it can send specific sub-channel messages and register the sub channels
 * if the id inside the received packet isn't registered (that's why ChannelSystem.LAST_ID isn't a valid id) the packet is recognized
 * as a wakeup packet and the contents of the packet signals the channel's name
 *
 * when a wakeup packet is received it means that the other endpoint has registered a sub-channel, in order for the
 * sub-channel to be activated both endpoints must've sent and received the wakeup packet containing the channel's name
 *
 * the server is the endpoint that decides the channel's id so the server's wakeup packets are the only ones whose id is
 * meaningful: it indicates the new channel's id to the server
 */
public abstract class BaseChannelSystemChild implements ChannelSystemChild {
    
    protected final DynamicArray<Channel> used = new DynamicArray<>(4, ChannelSystem.MAX_IDS);

    public void init() {
        getConnection()
                .getEventManager()
                .register(ConnectionCloseEvent.class, this::onConnectionClose);
    }

    @Override
    public void onReceive(ChannelMessagePacket packet) {
        int id = packet.id & 0xffff;
        Channel ch = id == ChannelSystem.LAST_ID ? null : used.get(id & 0xffff);
        if (ch != null) {//normal message
            onMessage(ch, packet.message);
        } else {//Wakeup message
            onWakeup(packet.id, new String(packet.message, ChannelMessagePacket.CHARSET));
        }
    }

    protected void onMessage(Channel channel, byte[] message) {
        channel.receive(getConnection(), channel.getProtocol().fromData(Unpooled.wrappedBuffer(message)));
    }

    protected abstract void onWakeup(short id, String name);

    protected abstract void onConnectionClose(ConnectionCloseEvent event);

    protected void onChannelActive(Channel channel) {
        channel.getEventManager().call(new ChannelActiveEvent(getConnection()));
    }
}
