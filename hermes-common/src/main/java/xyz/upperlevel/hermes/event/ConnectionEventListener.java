package xyz.upperlevel.hermes.event;

import xyz.upperlevel.event.Event;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.GeneralEventListener;
import xyz.upperlevel.event.impl.BaseGeneralEventListener;
import xyz.upperlevel.hermes.Connection;

import java.util.function.Consumer;

public abstract class ConnectionEventListener extends BaseGeneralEventListener<ConnectionEvent> implements GeneralEventListener<ConnectionEvent> {

    public ConnectionEventListener(Class<?> clazz, byte priority) {
        super(clazz, priority);
    }

    public ConnectionEventListener(Class<?> clazz) {
        super(clazz, EventPriority.NORMAL);
    }

    public abstract void call(ConnectionEvent<?> event);

    public static <E extends Event> ConnectionEventListener listener(Class<E> clazz, Listener<E> consumer, byte priority) {
        return new SimpleConnectionEventListener(clazz, priority, consumer);
    }

    public static <E extends Event> ConnectionEventListener listener(Class<E> clazz, Listener<E> consumer) {
        return new SimpleConnectionEventListener(clazz, consumer);
    }

    public static <E extends Event> ConnectionEventListener listener(Class<E> clazz, Consumer<E> consumer, byte priority) {
        return new SimpleConnectionEventListener(clazz, priority, (c, e) -> consumer.accept(e));
    }

    public static <E extends Event> ConnectionEventListener listener(Class<E> clazz, Consumer<E> consumer) {
        return new SimpleConnectionEventListener(clazz, (c, e) -> consumer.accept(e));
    }

    public interface Listener<E extends Event> {
        void onCall(Connection connection, E event);
    }
}
