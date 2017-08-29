package xyz.upperlevel.hermes;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TestPacket implements Packet {
    public static Protocol PROTOCOL = Protocol.builder()
            .enableSubChannels()
            .packet(PacketSide.SERVER, TestPacket.class)
            .build();

    public String testString;
    public int testInt;

    @Override
    public void toData(ByteBuf out) {
        out.writeBytes(testString.getBytes(StandardCharsets.UTF_8));
        out.writeInt(testInt);
    }

    @Override
    public void fromData(ByteBuf in) {
        byte[] rawStr = new byte[in.readableBytes() - 4];
        in.readBytes(rawStr);
        testString = new String(rawStr, StandardCharsets.UTF_8);
        testInt = in.readInt();
    }
}
