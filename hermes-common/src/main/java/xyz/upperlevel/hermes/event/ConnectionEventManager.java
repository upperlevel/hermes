package xyz.upperlevel.hermes.event;

import xyz.upperlevel.event.Event;
import xyz.upperlevel.event.GeneralEventManager;
import xyz.upperlevel.hermes.Connection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static xyz.upperlevel.hermes.event.ConnectionEventListener.listener;


public class ConnectionEventManager extends GeneralEventManager<ConnectionEvent, ConnectionEventListener> {

    @Override
    @SuppressWarnings("unchecked")
    protected ConnectionEventListener eventHandlerToListener(Object listener, Method method, byte priority) throws Exception {
        Class<?> argument;
        int paramCount = method.getParameterCount();
        if (paramCount < 1 && paramCount > 2)
            throw new IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument number");

        Class<?>[] parameters = method.getParameterTypes();
        argument = parameters[paramCount - 1];//paramCount == 1 ? parameters[0] : parameters[1];

        if(!Event.class.isAssignableFrom(argument))
            throw new IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument type, it should be an Event (" + argument.getName());
        if(paramCount == 2 && (Connection.class != parameters[0]))
            throw new IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument type, only (Event) or (Connection, Event) accepted");

        method.setAccessible(true);

        return ConnectionEventListener.listener(
                (Class<Event>) argument,
                (Connection conn, Event e) -> {
                    try {
                        if (paramCount == 1)
                            method.invoke(listener, e);
                        else
                            method.invoke(listener, conn, e);
                    } catch (IllegalAccessException e1) {
                        throw new RuntimeException("Error accessing " + method.getDeclaringClass().getSimpleName() + ":" + method.getName());
                    } catch (InvocationTargetException e1) {
                        log("Error while executing event in " + method.getDeclaringClass().getSimpleName() + ":" + method.getName(), e1);
                    }
                },
                priority
        );
    }

    protected void log(String error, Exception e) {
        System.err.println(error);
        e.printStackTrace();
    }

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

    public <E extends Event> void register(Class<E> clazz, ConnectionEventListener.Listener<E> consumer) {
        register(listener(clazz, consumer));
    }

    public <E extends Event> void register(Class<E> clazz, ConnectionEventListener.Listener<E> consumer, byte priority) {
        register(listener(clazz, consumer, priority));
    }
}
