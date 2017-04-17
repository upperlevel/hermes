package xyz.upperlevel.utils.packet.event;

import xyz.upperlevel.utils.event.GeneralEventManager;
import xyz.upperlevel.utils.packet.Connection;

import java.util.function.BiConsumer;

import static xyz.upperlevel.utils.packet.event.ConnectionEventListener.listener;


public class ConnectionEventManager extends GeneralEventManager<ConnectionEvent, ConnectionEventListener> {
    @Override
    @SuppressWarnings("unchecked")
    public ConnectionEventListener[] newListenerArray(int size) {
        return new ConnectionEventListener[size];
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void execute(ConnectionEventListener listener, ConnectionEvent event) {
        listener.call(event);
    }

    public <E> void register(Class<E> clazz, BiConsumer<Connection, E> consumer) {
        register(listener(clazz, consumer));
    }

    public <E> void register(Class<E> clazz, BiConsumer<Connection, E> consumer, byte priority) {
        register(listener(clazz, consumer, priority));
    }
}