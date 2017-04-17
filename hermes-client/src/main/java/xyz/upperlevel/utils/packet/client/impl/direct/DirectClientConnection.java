package xyz.upperlevel.utils.packet.client.impl.direct;

import lombok.Getter;
import xyz.upperlevel.utils.event.impl.def.EventManager;
import xyz.upperlevel.utils.packet.channel.Channel;
import xyz.upperlevel.utils.packet.channel.ChannelSystemChild;
import xyz.upperlevel.utils.packet.client.ClientConnection;
import xyz.upperlevel.utils.packet.impl.direct.DirectConnection;

public class DirectClientConnection extends DirectConnection implements ClientConnection {
    @Getter
    private final DirectClient parent;

    public DirectClientConnection(DirectClient client, Channel defaultChannel, EventManager eventManager, ChannelSystemChild channelSystemChild) {
        super(defaultChannel, eventManager, channelSystemChild);
        this.parent = client;
    }

    public DirectClientConnection(DirectClient client, Channel defaultChannel, ChannelSystemChild channelSystemChild) {
        super(defaultChannel, channelSystemChild);
        this.parent = client;
    }

    public DirectClientConnection(DirectClient client, EventManager eventManager, ChannelSystemChild channelSystemChild) {
        super(eventManager, channelSystemChild);
        this.parent = client;
    }

    public DirectClientConnection(DirectClient client, ChannelSystemChild channelSystemChild) {
        super(channelSystemChild);
        this.parent = client;
    }
}
