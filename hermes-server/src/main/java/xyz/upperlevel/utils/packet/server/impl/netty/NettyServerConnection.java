package xyz.upperlevel.utils.packet.server.impl.netty;

import lombok.Getter;
import xyz.upperlevel.utils.packet.event.impl.ConnectionCloseEvent;
import xyz.upperlevel.utils.packet.event.impl.ConnectionOpenEvent;
import xyz.upperlevel.utils.packet.impl.netty.NettyConnection;
import xyz.upperlevel.utils.packet.server.ServerConnection;
import xyz.upperlevel.utils.packet.server.channel.ServerChannelSystemChild;

public class NettyServerConnection extends NettyConnection implements ServerConnection {
    @Getter
    private final NettyServer parent;


    public NettyServerConnection(NettyServer server, ServerChannelSystemChild channelSystemChild) {
        super(channelSystemChild);
        this.parent = server;
        setDefaultChannel(server.getDefaultChannel());
    }

    @Override
    public boolean onOpen() {
        ConnectionOpenEvent event = new ConnectionOpenEvent(this);
        parent.getEventManager().call(event);
        if(event.isCancelled())
            return false;
        return super.onOpen();
    }

    @Override
    public void onClose() {
        super.onClose();
        getEventManager().call(new ConnectionCloseEvent(this));
    }
}
