package xyz.upperlevel.hermes.event;

import xyz.upperlevel.event.Event;
import xyz.upperlevel.event.EventPriority;

public class SimpleConnectionEventListener extends ConnectionEventListener {
    private final Listener<Event> consumer;

    @SuppressWarnings("unchecked")
    public <E extends Event> SimpleConnectionEventListener(Class<E> clazz, byte priority, Listener<E> consumer) {
        super(clazz, priority);
        this.consumer = (Listener<Event>) consumer;
    }

    public <E extends Event> SimpleConnectionEventListener(Class<E> clazz, Listener<E> consumer) {
        this(clazz, EventPriority.NORMAL, consumer);
    }

    public void call(ConnectionEvent<?> event) {
        consumer.onCall(event.getConnection(), event.getEvent());
    }
}
