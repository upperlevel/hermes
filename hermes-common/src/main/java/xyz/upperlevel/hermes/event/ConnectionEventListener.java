package xyz.upperlevel.hermes.event;

import xyz.upperlevel.event.Event;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.GeneralEventListener;
import xyz.upperlevel.event.impl.BaseGeneralEventListener;
import xyz.upperlevel.hermes.Connection;

import java.util.function.Consumer;

public class ConnectionEventListener extends BaseGeneralEventListener<ConnectionEvent> implements GeneralEventListener<ConnectionEvent> {
    private final Listener<Event> consumer;

    @SuppressWarnings("unchecked")
    public <E extends Event> ConnectionEventListener(Class<E> clazz, byte priority, Listener<E> consumer) {
        super(clazz, priority);
        this.consumer = (Listener<Event>) consumer;
    }

    public <E extends Event> ConnectionEventListener(Class<E> clazz, Listener<E> consumer) {
        this(clazz, EventPriority.NORMAL, consumer);
    }

    public void call(ConnectionEvent<?> event) {
        consumer.onCall(event.getConnection(), event.getEvent());
    }

    public static <E extends Event> ConnectionEventListener listener(Class<E> clazz, Listener<E> consumer, byte priority) {
        return new ConnectionEventListener(clazz, priority, consumer);
    }

    public static <E extends Event> ConnectionEventListener listener(Class<E> clazz, Listener<E> consumer) {
        return new ConnectionEventListener(clazz, consumer);
    }

    public static <E extends Event> ConnectionEventListener listener(Class<E> clazz, Consumer<E> consumer, byte priority) {
        return new ConnectionEventListener(clazz, priority, (c, e) -> consumer.accept(e));
    }

    public static <E extends Event> ConnectionEventListener listener(Class<E> clazz, Consumer<E> consumer) {
        return new ConnectionEventListener(clazz, (c, e) -> consumer.accept(e));
    }

    public interface Listener<E extends Event> {
        void onCall(Connection connection, E event);
    }
}
