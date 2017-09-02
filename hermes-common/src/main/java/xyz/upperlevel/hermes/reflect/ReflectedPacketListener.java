package xyz.upperlevel.hermes.reflect;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.SinglePacketListener;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

@EqualsAndHashCode(exclude = "method")
public class ReflectedPacketListener implements SinglePacketListener {
    @Getter
    private final Class<? extends Packet> packetClass;
    private final Method listener;
    private final Object instance;
    private final MethodHandle method;

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

        MethodHandle rawMethod;
        try {
            rawMethod = MethodHandles.lookup().unreflect(listener);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        rawMethod = rawMethod.bindTo(instance);
        this.method = rawMethod.asType(rawMethod.type().changeParameterType(0, Connection.class).changeParameterType(1, Packet.class));
    }

    @Override
    public void onPacket(Connection connection, Packet packet) {
        try {
            method.invokeExact(connection, packet);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException("Code not reachable, listener cannot throw any non/runtime exception", e);
        }
    }
}
