package xyz.upperlevel.hermes.server.impl.direct;

import lombok.Getter;
import xyz.upperlevel.utils.event.impl.def.EventManager;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.channel.ChannelSystemChild;
import xyz.upperlevel.hermes.impl.direct.DirectConnection;
import xyz.upperlevel.hermes.server.ServerConnection;

public class DirectServerConnection extends DirectConnection implements ServerConnection{
    @Getter
    private final DirectServer parent;

    public DirectServerConnection(DirectServer client, Channel defaultChannel, EventManager eventManager, ChannelSystemChild channelSystemChild) {
        super(defaultChannel, eventManager, channelSystemChild);
        this.parent = client;
    }

    public DirectServerConnection(DirectServer client, Channel defaultChannel, ChannelSystemChild channelSystemChild) {
        super(defaultChannel, channelSystemChild);
        this.parent = client;
    }

    public DirectServerConnection(DirectServer client, EventManager eventManager, ChannelSystemChild channelSystemChild) {
        super(eventManager, channelSystemChild);
        this.parent = client;
    }

    public DirectServerConnection(DirectServer client, ChannelSystemChild channelSystemChild) {
        super(channelSystemChild);
        this.parent = client;
    }
}
