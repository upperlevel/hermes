package xyz.upperlevel.hermes.channel;

import io.netty.buffer.Unpooled;
import xyz.upperlevel.hermes.channel.events.ChannelActiveEvent;
import xyz.upperlevel.hermes.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.hermes.event.impl.ConnectionCloseEvent;

import javax.xml.bind.DatatypeConverter;

public abstract class BaseChannelSystemChild implements ChannelSystemChild {

    //TODO: Reduce space usage
    protected final Channel[] used = new Channel[ChannelSystem.MAX_IDS];

    public BaseChannelSystemChild() {
        getConnection().getEventManager().register(ConnectionCloseEvent.class, this::onConnectionClose);
    }

    @Override
    public void onReceive(ChannelMessagePacket packet) {
        int id = packet.id & 0xffff;
        Channel ch = id == ChannelSystem.LAST_ID ? null : used[id & 0xffff];
        if (ch == null) {//Wakeup
            System.out.println("WAKEUP! " + new String(packet.message, ChannelMessagePacket.CHARSET));
            onWakeup(packet.id, new String(packet.message, ChannelMessagePacket.CHARSET));
        } else {//Message
            onMessage(ch, packet.message);
        }
    }

    protected void onMessage(Channel channel, byte[] message) {
        System.out.println("CHANNEL SYSTEM: " + DatatypeConverter.printHexBinary(message));
        channel.receive(getConnection(), channel.getProtocol().fromData(Unpooled.wrappedBuffer(message)));
    }

    protected abstract void onWakeup(short id, String name);

    protected abstract void onConnectionClose(ConnectionCloseEvent event);

    protected void onChannelActive(Channel channel) {
        channel.getEventManager().call(new ChannelActiveEvent(getConnection()));
    }
}
