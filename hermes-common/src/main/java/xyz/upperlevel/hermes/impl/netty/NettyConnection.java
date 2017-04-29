package xyz.upperlevel.hermes.impl.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import xyz.upperlevel.hermes.channel.ChannelSystemChild;
import xyz.upperlevel.hermes.event.impl.ConnectionCloseEvent;
import xyz.upperlevel.event.impl.def.EventManager;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.event.impl.ConnectionOpenEvent;
import xyz.upperlevel.hermes.impl.BaseConnection;

public abstract class NettyConnection extends BaseConnection {
    private io.netty.channel.Channel handle = null;
    private NettyAdapter adapter = new NettyAdapter();

    public NettyConnection(Channel defaultChannel, EventManager eventManager, ChannelSystemChild channelSystemChild) {
        super(defaultChannel, eventManager, channelSystemChild);
    }

    public NettyConnection(Channel defaultChannel, ChannelSystemChild channelSystemChild) {
        super(defaultChannel, channelSystemChild);
    }

    public NettyConnection(EventManager eventManager, ChannelSystemChild channelSystemChild) {
        super(eventManager, channelSystemChild);
    }

    public NettyConnection(ChannelSystemChild channelSystemChild) {
        super(channelSystemChild);
    }

    @Override
    protected void send(Packet packet) {
        if(handle == null)
            throw new IllegalStateException("Connection not yet initialized");
        handle.write(packet);
        handle.flush();
    }

    @Override
    public boolean isOpen() {
        return handle != null && handle.isOpen();
    }

    @Override
    public void close() throws InterruptedException {
        if(handle != null)
            handle.close().sync();
    }

    public ChannelInboundHandlerAdapter adapter() {
        return adapter;
    }

    public boolean onOpen() {
        return getEventManager().call(new ConnectionOpenEvent(this));
    }

    public void onClose() {
        getEventManager().call(new ConnectionCloseEvent(this));
    }

    private class NettyAdapter extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(final ChannelHandlerContext ctx) {
            handle = ctx.channel();
            if(!onOpen()) {
                System.out.println("Connection refused");
                ctx.close();
            } else System.out.println("Connection active");
        }

        @Override
        public void channelInactive(final ChannelHandlerContext ctx) {
            onClose();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            assert msg instanceof Packet;

            getDefaultChannel().receive(NettyConnection.this, (Packet)msg);
        }
    }
}
