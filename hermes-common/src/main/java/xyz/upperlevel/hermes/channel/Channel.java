package xyz.upperlevel.hermes.channel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import xyz.upperlevel.event.EventManager;
import xyz.upperlevel.hermes.*;
import xyz.upperlevel.hermes.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.hermes.reflect.PacketHandler;
import xyz.upperlevel.hermes.reflect.PacketListener;
import xyz.upperlevel.hermes.reflect.ReflectedPacketListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Accessors(chain = true)
public class Channel {

    @Getter
    private final String name;
    @Getter
    private final EventManager eventManager;
    @Getter
    @Setter
    private int id = BaseChannelSystem.UNASSIGNED;
    private Map<Class<? extends Packet>, List<SinglePacketListener<?>>> listeners = new HashMap<>();
    @Getter
    @Setter
    private PacketConverter protocol;

    public Channel(String name, EventManager eventManager) {
        if (!isNameLegal(name))
            throw new IllegalArgumentException("Invalid name! it can only contain chars: \"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_\"");
        this.name = name;
        this.eventManager = eventManager;

        //Sub-channels support
        register(ChannelMessagePacket.class, (connection, packet) -> connection.getChannelSystemChild().onReceive(packet));
    }

    public Channel(String name) {
        this(name, new EventManager());
    }

    public Channel setProtocol(Protocol protocol, PacketSide side) {
        setProtocol(protocol.compile(side));
        return this;
    }

    protected boolean isNameLegal(String name) {
        return name.chars().allMatch(i ->
                (i >= 'a' && i <= 'z') ||
                        (i >= 'A' && i <= 'Z') ||
                        (i >= '0' && i <= '9') ||
                        i == '_' ||
                        i == '-');
    }


    public <T extends Packet> void register(Class<T> clazz, SinglePacketListener<T> listener) {
        listeners.computeIfAbsent(clazz, c -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public void register(PacketListener listener) {
        for(Method m : listener.getClass().getDeclaredMethods()) {
            PacketHandler handler = m.getAnnotation(PacketHandler.class);
            if(handler != null) {
                m.setAccessible(true);
                ReflectedPacketListener l = new ReflectedPacketListener(m, listener);
                register(l.getPacketClass(), l);
            }
        }
    }

    public <T extends Packet> boolean unregister(Class<T> clazz, SinglePacketListener<T> listener) {
        List<SinglePacketListener<?>> registry = listeners.get(clazz);
        return registry.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public void unregister(PacketListener listener) {
        for(Method m : listener.getClass().getDeclaredMethods()) {
            PacketHandler handler = m.getAnnotation(PacketHandler.class);
            if(handler != null) {
                m.setAccessible(true);
                ReflectedPacketListener l = new ReflectedPacketListener(m, listener);
                unregister(l.getPacketClass(), l);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void receive(Connection conn, Packet msg) {
        List<SinglePacketListener<?>> receivers = listeners.get(msg.getClass());
        if (receivers != null) {
            for (SinglePacketListener listener : receivers) {
                listener.onPacket(conn, msg);
            }
        }
    }
}
