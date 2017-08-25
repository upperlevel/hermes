package xyz.upperlevel.hermes.channel.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.EqualsAndHashCode;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.channel.Channel;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@EqualsAndHashCode
public class ChannelMessagePacket implements Packet {
    public static Charset CHARSET = StandardCharsets.UTF_8;

    public short id;
    public byte[] message;

    public ChannelMessagePacket() {
    }

    public ChannelMessagePacket(short id, byte[] message) {
        this.id = id;
        this.message = message;
    }

    @Override
    public void toData(ByteBuf out) {
        out.writeShort(id);
        out.writeBytes(message);
    }

    @Override
    public void fromData(ByteBuf in) {
        id = in.readShort();
        message = new byte[in.readableBytes()];
        in.readBytes(message);
    }

    public static ChannelMessagePacket newMessage(Channel channel, Packet packet) {
        ByteBuf buf = Unpooled.buffer();
        channel.getProtocol().toData(packet, buf);
        byte[] arr = new byte[buf.readableBytes()];
        buf.readBytes(arr);
        return new ChannelMessagePacket((short) channel.getId(), arr);
    }

    public static ChannelMessagePacket newWakeupPacket(short id, String chName) {
        return new ChannelMessagePacket(id, chName.getBytes(CHARSET));
    }
}
