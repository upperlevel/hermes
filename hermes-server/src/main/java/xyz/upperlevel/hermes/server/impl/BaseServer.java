package xyz.upperlevel.hermes.server.impl;

import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.event.impl.def.EventManager;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.server.Server;

public abstract class BaseServer implements Server {
    @Getter
    private final EventManager eventManager;

    @Getter
    @Setter
    private Channel defaultChannel;

    public BaseServer(EventManager eventManager, Channel defaultChannel) {
        this.eventManager = eventManager;
        this.defaultChannel = defaultChannel;
    }

    public BaseServer(EventManager eventManager) {
        this(eventManager, null);
    }

    public BaseServer(Channel defaultChannel) {
        this(new EventManager(), defaultChannel);
    }

    public BaseServer() {
        this(new EventManager(), null);
    }
}
