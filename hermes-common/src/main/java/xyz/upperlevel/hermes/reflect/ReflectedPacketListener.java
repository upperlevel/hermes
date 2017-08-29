package xyz.upperlevel.hermes.reflect;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.SinglePacketListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@EqualsAndHashCode
public class ReflectedPacketListener implements SinglePacketListener {
    private final Method listener;
    @Getter
    private final Class<? extends Packet> packetClass;
    private final Object instance;

    @SuppressWarnings("unchecked")
    public ReflectedPacketListener(Method listener, Object instance) {
        this.listener = listener;

        Class<?>[] args = listener.getParameterTypes();
        if(args.length != 2)
            throw new IllegalArgumentException("The listener must have only 2 arguments");

        if(!Packet.class.isAssignableFrom(args[1]) || args[0] != Connection.class)
            throw new IllegalArgumentException("The listener must have 2 arguments: Connection and packet");
        packetClass = (Class<? extends Packet>) args[1];

        if(listener.getExceptionTypes().length > 0)
            throw new IllegalArgumentException("The listeners cannot throw any exception!");

        this.instance = instance;
    }

    @Override
    public void onPacket(Connection connection, Packet packet) {
        try {
            listener.invoke(instance, connection, packet);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot invoke reflected listener", e);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        }
    }
}
