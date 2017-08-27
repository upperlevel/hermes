package xyz.upperlevel.hermes.packet;

import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Test;
import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.reflect.PacketHandler;
import xyz.upperlevel.hermes.reflect.PacketListener;

public class ReflectedListenerTest implements PacketListener {
    private int called;

    @PacketHandler
    public void onTest1(Connection conn, Test1Packet packet) {
        Assert.assertNotNull(packet);
        called |= 1;
    }

    @PacketHandler
    public void onTest2(Connection conn, Test2Packet packet) {
        Assert.assertNotNull(packet);
        called |= 2;
    }

    public void reset() {
        called = 0;
    }

    @Test
    public void test() {
        Channel channel = new Channel("test");
        channel.register(this);

        reset();

        channel.receive(null, new Test1Packet());
        Assert.assertEquals(
                1,
                called
        );

        reset();
        channel.receive(null, new Test2Packet());
        Assert.assertEquals(
                2,
                called
        );
    }




    public static class Test1Packet implements Packet {
        @Override
        public void toData(ByteBuf out) {}

        @Override
        public void fromData(ByteBuf in) {}
    }

    public static class Test2Packet implements Packet {
        @Override
        public void toData(ByteBuf out) {}

        @Override
        public void fromData(ByteBuf in) {}
    }
}
