package xyz.upperlevel.utils.packet;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class TestPacket implements Packet{
    public static TestPacketConverter CONVERTER = new TestPacketConverter();
    public static Protocol PROTOCOL = Protocol.builder()
            .subChannels()
            .register(TestPacket.class, CONVERTER)
            .build();

    public final String testString;
    public final int testInt;

    public static class TestPacketConverter implements PacketConverter<TestPacket> {

        @Override
        public byte[] toData(TestPacket packet) {
            byte[] rawStr = packet.getTestString().getBytes();
            return ByteBuffer.allocate(rawStr.length + 4)
                    .put(rawStr)
                    .putInt(packet.getTestInt())
                    .array();
        }

        @Override
        public TestPacket toPacket(byte[] data) {
            return new TestPacket(
                    new String(data, 0, data.length - 4),
                    ByteBuffer.wrap(data, data.length - 4, 4).getInt()
            );
        }
    }
}
