package xyz.upperlevel.hermes.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import xyz.upperlevel.hermes.channel.packets.ChannelMessagePacket;

import static org.junit.Assert.assertEquals;

public class ChannelMessagePacketTest {

    @Test
    public void test() {
        test((short) 123, "testing".getBytes());
        test(Short.MAX_VALUE, "a super long string is quite easy to create using a simple human and a keyboard".getBytes());
        test(Short.MIN_VALUE, "".getBytes());
        test((short) 0, "it really works?".getBytes());
        test((short) -123, "still not sure".getBytes());
    }

    public void test(short id, byte[] message) {
        ChannelMessagePacket original = new ChannelMessagePacket(id, message);
        ByteBuf buffer = Unpooled.buffer();
        original.toData(buffer);


        ChannelMessagePacket packet = new ChannelMessagePacket();
        packet.fromData(buffer);

        assertEquals(
                original,
                packet
        );
    }
}
