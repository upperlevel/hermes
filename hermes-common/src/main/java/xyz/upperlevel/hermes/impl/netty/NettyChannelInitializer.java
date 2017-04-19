package xyz.upperlevel.hermes.impl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import xyz.upperlevel.hermes.Protocol;
import xyz.upperlevel.hermes.Packet;

import java.util.List;

@ChannelHandler.Sharable
public abstract class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        p.addLast("length-decoder", new LengthDecoder());

        ConnectionHandler handler = newConnectionHandler();

        p.addLast("packet-decoder", handler.new PacketDecoder());

        p.addLast("encoder", handler.new PacketEncoder());

        p.addLast("executor", handler.getChannelHandler());
    }

    protected abstract ConnectionHandler newConnectionHandler();


    public static abstract class ConnectionHandler {
        public abstract ChannelHandler getChannelHandler();

        public abstract Protocol getProtocol();

        public class PacketDecoder extends ByteToMessageDecoder {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                byte[] data = new byte[in.readableBytes()];
                in.readBytes(data);

                Packet packet = getProtocol().convert(data);
                assert packet != null;
                out.add(packet);
            }
        }

        private class PacketEncoder extends MessageToByteEncoder<Packet> {
            @SuppressWarnings("unchecked")
            @Override
            protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
                byte[] bytes = getProtocol().convert(msg, (Class<Packet>)msg.getClass());
                out.writeShort(bytes.length);
                out.writeBytes(bytes);
            }
        }
    }

    public static class LengthDecoder extends ByteToMessageDecoder {
        private int length = -1;

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if(length == -1) {
                if(in.readableBytes() < Short.BYTES)
                    return;
                length = in.readUnsignedShort();
            } else {
                if(in.readableBytes() < length)
                    return;
                out.add(in.readBytes(length));
                length = -1;
            }
        }
    }
}
