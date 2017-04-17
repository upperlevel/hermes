package xyz.upperlevel.utils.packet.channel.packets;

import xyz.upperlevel.utils.packet.Packet;
import xyz.upperlevel.utils.packet.PacketConverter;
import xyz.upperlevel.utils.packet.channel.Channel;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ChannelMessagePacket implements Packet {
    public static final ChannelMessagePacketConverter CONVERTER = new ChannelMessagePacketConverter();
    public static Charset CHARSET = StandardCharsets.UTF_8;

    public final short id;
    public final byte[] message;

    public ChannelMessagePacket(short id, byte[] message) {
        this.id = id;
        this.message = message;
    }

    public static class ChannelMessagePacketConverter implements PacketConverter<ChannelMessagePacket> {
        @Override
        public byte[] toData(ChannelMessagePacket packet) {
            return ByteBuffer.allocate(Short.BYTES + packet.message.length)
                    .putShort(packet.id)
                    .put(packet.message)
                    .array();
        }

        @Override
        public ChannelMessagePacket toPacket(byte[] data) {
            ChannelMessagePacket packet = new ChannelMessagePacket(
                    ByteBuffer.wrap(data, 0, Short.BYTES).getShort(),
                    Arrays.copyOfRange(data, Short.BYTES, data.length)
            );

            return packet;
        }
    }


    public static ChannelMessagePacket newMessage(Channel channel, Packet packet) {
        return new ChannelMessagePacket((short)channel.getId(), channel.getProtocol().convert(packet));
    }

    public static ChannelMessagePacket newWakeupPacket(short id, String chName) {
        return new ChannelMessagePacket(id, chName.getBytes(CHARSET));
    }
}
