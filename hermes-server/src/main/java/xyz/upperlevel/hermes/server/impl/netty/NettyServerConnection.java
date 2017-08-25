package xyz.upperlevel.hermes.server.impl.netty;

import lombok.Getter;
import xyz.upperlevel.hermes.event.impl.ConnectionCloseEvent;
import xyz.upperlevel.hermes.event.impl.ConnectionOpenEvent;
import xyz.upperlevel.hermes.impl.netty.NettyConnection;
import xyz.upperlevel.hermes.server.ServerConnection;
import xyz.upperlevel.hermes.server.channel.ServerChannelSystemChild;

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
        if (event.isCancelled())
            return false;
        return super.onOpen();
    }

    @Override
    public void onClose() {
        super.onClose();
        getEventManager().call(new ConnectionCloseEvent(this));
    }
}
