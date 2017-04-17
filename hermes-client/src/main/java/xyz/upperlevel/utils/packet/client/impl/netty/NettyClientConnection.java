package xyz.upperlevel.utils.packet.client.impl.netty;

import lombok.Getter;
import xyz.upperlevel.utils.event.impl.def.EventManager;
import xyz.upperlevel.utils.packet.channel.Channel;
import xyz.upperlevel.utils.packet.channel.ChannelSystemChild;
import xyz.upperlevel.utils.packet.client.ClientConnection;
import xyz.upperlevel.utils.packet.impl.netty.NettyConnection;

public class NettyClientConnection extends NettyConnection implements ClientConnection {

    @Getter
    private final NettyClient parent;

    public NettyClientConnection(NettyClient parent, Channel defaultChannel, EventManager eventManager, ChannelSystemChild channelSystemChild) {
        super(defaultChannel, eventManager, channelSystemChild);
        this.parent = parent;
    }

    public NettyClientConnection(NettyClient parent, Channel defaultChannel, ChannelSystemChild channelSystemChild) {
        super(defaultChannel, channelSystemChild);
        this.parent = parent;
    }

    public NettyClientConnection(NettyClient parent, EventManager eventManager, ChannelSystemChild channelSystemChild) {
        super(eventManager, channelSystemChild);
        this.parent = parent;
    }

    public NettyClientConnection(NettyClient parent, ChannelSystemChild channelSystemChild) {
        super(channelSystemChild);
        this.parent = parent;
    }
}
