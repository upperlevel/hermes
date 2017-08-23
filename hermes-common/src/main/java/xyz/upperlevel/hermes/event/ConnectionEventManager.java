package xyz.upperlevel.hermes.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.GeneralEventManager;
import xyz.upperlevel.event.impl.def.EventManager;
import xyz.upperlevel.hermes.Connection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static xyz.upperlevel.hermes.event.ConnectionEventListener.listener;


public class ConnectionEventManager extends GeneralEventManager<ConnectionEvent, ConnectionEventListener> {

    @Override
    @SuppressWarnings("unchecked")
    protected ConnectionEventListener eventHandlerToListener(Object instance, Method listener, byte priority) throws Exception {
        Class<?> argument;
        int paramCount = listener.getParameterCount();
        if (paramCount < 1 || paramCount > 2)
            throw new IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument number");

        Class<?>[] parameters = listener.getParameterTypes();
        argument = parameters[paramCount - 1];//paramCount == 1 ? parameters[0] : parameters[1];

        if(!Event.class.isAssignableFrom(argument))
            throw new IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument type, it should be an Event (" + argument.getName());
        if(paramCount == 2 && (Connection.class != parameters[0]))
            throw new IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument type, only (Event) or (Connection, Event) accepted");

        listener.setAccessible(true);

        return new ReflectionConnectionEventListener(argument, priority, listener, instance, paramCount == 2);
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


    @Getter
    @EqualsAndHashCode(callSuper = true, exclude = "hasConnectionArgument")
    public class ReflectionConnectionEventListener extends ConnectionEventListener {
        private final Method listener;
        private final Object instance;
        private final boolean hasConnectionArgument;

        public ReflectionConnectionEventListener(Class<?> clazz, byte priority, Method listener, Object instance, boolean hasConnectionArgument) {
            super(clazz, priority);
            this.listener = listener;
            this.instance = instance;
            this.hasConnectionArgument = hasConnectionArgument;
        }

        @Override
        public void call(ConnectionEvent<?> event) {
            try {
                if(hasConnectionArgument)
                    listener.invoke(instance, event.getConnection(), event.getEvent());
                else
                    listener.invoke(instance, event.getEvent());
            } catch (IllegalAccessException var3) {
                throw new RuntimeException("Error accessing " + this.listener.getDeclaringClass().getSimpleName() + ":" + this.listener.getName());
            } catch (InvocationTargetException var4) {
                log("Error while executing event in " + this.listener.getDeclaringClass().getSimpleName() + ":" + this.listener.getName(), var4);
            }
        }
    }
}
