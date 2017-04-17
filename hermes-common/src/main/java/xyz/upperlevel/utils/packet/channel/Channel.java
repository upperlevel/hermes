package xyz.upperlevel.utils.packet.channel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import xyz.upperlevel.utils.event.EventPriority;
import xyz.upperlevel.utils.packet.Connection;
import xyz.upperlevel.utils.packet.Packet;
import xyz.upperlevel.utils.packet.Protocol;
import xyz.upperlevel.utils.packet.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.utils.packet.event.ConnectionEvent;
import xyz.upperlevel.utils.packet.event.ConnectionEventManager;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
@Accessors(chain = true)
public class Channel {

    @Getter
    @Setter
    private int id = BaseChannelSystem.UNASSIGNED;

    @Getter
    private final String name;
    @Getter
    private final ConnectionEventManager eventManager;
    @Getter
    @Setter
    private Protocol protocol;

    public Channel(String name, ConnectionEventManager eventManager) {
        if(!isNameLegal(name))
            throw new IllegalArgumentException("Invalid name! it can only contain chars: \"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_\"");
        this.name = name;
        this.eventManager = eventManager;

        //Sub-channels support
        eventManager.register(ChannelMessagePacket.class, (Connection conn, ChannelMessagePacket pkt) -> conn.getChannelSystemChild().onReceive(pkt));
    }

    public Channel(String name) {
        this(name, new ConnectionEventManager());
    }

    protected boolean isNameLegal(String name) {
        return name.chars().allMatch(i ->
                        (i >= 'a' && i <= 'z') ||
                        (i >= 'A' && i <= 'Z') ||
                        (i >= '0' && i <= '9') ||
                        i == '_' ||
                        i == '-');
    }


    public <T extends Packet> void register(Class<T> clazz, byte priority, BiConsumer<Connection, T> listener) {
        getEventManager().register(clazz, listener, priority);
    }

    public <T extends Packet> void register(Class<T> clazz, BiConsumer<Connection, T> listener) {
        register(clazz, EventPriority.NORMAL, listener);
    }

    public <T extends Packet> void register(Class<T> clazz, byte priority, Consumer<T> listener) {
        register(clazz, priority, (c, p) -> listener.accept(p));
    }

    public <T extends Packet> void register(Class<T> clazz, Consumer<T> listener) {
        register(clazz, EventPriority.NORMAL, listener);
    }

    public void receive(Connection conn, Packet msg) {
        getEventManager().call(new ConnectionEvent<>(conn, msg), msg.getClass());
    }
}
