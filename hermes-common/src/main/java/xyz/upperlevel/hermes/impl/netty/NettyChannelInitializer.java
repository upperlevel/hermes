package xyz.upperlevel.hermes.impl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.PacketConverter;

import java.util.List;

@ChannelHandler.Sharable
public abstract class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        ConnectionHandler handler = newConnectionHandler();

        p.addLast("length-decoder", new LengthDecoder());
        p.addLast("packet-decoder", handler.new PacketDecoder());


        p.addLast("length-encoder", new LengthEncoder());
        p.addLast("packet-encoder", handler.new PacketEncoder());


        p.addLast("executor", handler.getChannelHandler());
    }

    protected abstract ConnectionHandler newConnectionHandler();


    public static abstract class ConnectionHandler {
        public abstract ChannelHandler getChannelHandler();

        public abstract PacketConverter getProtocol();

        public class PacketDecoder extends ByteToMessageDecoder {
            @Override
            protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                out.add(getProtocol().fromData(in));
            }
        }

        private class PacketEncoder extends MessageToByteEncoder<Packet> {
            @SuppressWarnings("unchecked")
            @Override
            protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
                getProtocol().toData(msg, out);
            }
        }
    }

    public static class LengthEncoder extends MessageToMessageEncoder<ByteBuf> {

        @Override
        protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            out.add(ctx.alloc().buffer(2).writeShort((short) in.readableBytes()));
            out.add(in.retain());
        }
    }

    public static class LengthDecoder extends ByteToMessageDecoder {
        private int length = -1;

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (length == -1) {
                if (in.readableBytes() < Short.BYTES)
                    return;
                length = in.readUnsignedShort();
            } else {
                if (in.readableBytes() < length)
                    return;
                out.add(in.readBytes(length));
                length = -1;
            }
        }
    }
}
