package xyz.upperlevel.hermes.event;

import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.GeneralEventListener;
import xyz.upperlevel.event.impl.BaseGeneralEventListener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConnectionEventListener extends BaseGeneralEventListener<ConnectionEvent> implements GeneralEventListener<ConnectionEvent> {
    private final BiConsumer<Connection, Object> consumer;

    @SuppressWarnings("unchecked")
    public <E> ConnectionEventListener(Class<E> clazz, byte priority, BiConsumer<Connection, E> consumer) {
        super(clazz, priority);
        this.consumer = (BiConsumer<Connection, Object>) consumer;
    }

    public <E> ConnectionEventListener(Class<E> clazz, BiConsumer<Connection, E> consumer) {
        this(clazz, EventPriority.NORMAL, consumer);
    }

    public void call(ConnectionEvent<?> event) {
        consumer.accept(event.getConnection(), event.getEvent());
    }

    public static <E> ConnectionEventListener listener(Class<E> clazz, BiConsumer<Connection, E> consumer, byte priority) {
        return new ConnectionEventListener(clazz, priority, consumer);
    }

    public static <E> ConnectionEventListener listener(Class<E> clazz, BiConsumer<Connection, E> consumer) {
        return new ConnectionEventListener(clazz, consumer);
    }

    public static <E> ConnectionEventListener listener(Class<E> clazz, Consumer<E> consumer, byte priority) {
        return new ConnectionEventListener(clazz, priority, (c, e) -> consumer.accept(e));
    }

    public static <E> ConnectionEventListener listener(Class<E> clazz, Consumer<E> consumer) {
        return new ConnectionEventListener(clazz, (c, e) -> consumer.accept(e));
    }
}
