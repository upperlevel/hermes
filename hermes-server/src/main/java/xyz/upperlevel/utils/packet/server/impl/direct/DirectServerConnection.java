package xyz.upperlevel.utils.packet.server.impl.direct;

import lombok.Getter;
import xyz.upperlevel.utils.event.impl.def.EventManager;
import xyz.upperlevel.utils.packet.channel.Channel;
import xyz.upperlevel.utils.packet.channel.ChannelSystemChild;
import xyz.upperlevel.utils.packet.impl.direct.DirectConnection;
import xyz.upperlevel.utils.packet.server.ServerConnection;

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
