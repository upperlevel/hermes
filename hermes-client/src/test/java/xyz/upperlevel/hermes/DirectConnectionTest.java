package xyz.upperlevel.hermes;

import org.junit.Test;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.client.impl.direct.DirectClient;
import xyz.upperlevel.hermes.server.impl.direct.DirectServer;
import xyz.upperlevel.hermes.server.impl.direct.DirectServerConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xyz.upperlevel.hermes.TestPacket.PROTOCOL;

public class DirectConnectionTest {

    @Test
    public void main() {
        Channel clChannel = new Channel("main").setProtocol(PROTOCOL, PacketSide.CLIENT);
        Channel seChannel = new Channel("main").setProtocol(PROTOCOL, PacketSide.SERVER);


        DirectServer server = new DirectServer(seChannel);
        DirectClient client = new DirectClient(clChannel);

        DirectServerConnection seConn = server.newConnection(client.getConnection());

        TestListener listener = new TestListener();
        clChannel.register(TestPacket.class, listener);

        seConn.setCopy(false);
        listener.reset();

        seConn.send(seChannel, new TestPacket("price", 100));
        assertEquals(1, listener.called);
        assertEquals("price", listener.lastString);
        assertEquals(100, listener.lastInt);


        seConn.setCopy(true);
        listener.reset();

        seConn.send(seChannel, new TestPacket("new price", 90));

        assertEquals(1, listener.called);
        assertEquals("new price", listener.lastString);
        assertEquals(90, listener.lastInt);

        {
            Protocol subProto = Protocol.builder()
                    .packet(PacketSide.SHARED, TestPacket.class)
                    .build();

            Channel subClCh = new Channel("sub").setProtocol(subProto, PacketSide.CLIENT);
            Channel subSeCh = new Channel("sub").setProtocol(subProto, PacketSide.SERVER);

            server.getChannelSystem().register(subSeCh);
            client.getChannelSystem().register(subClCh);

            TestListener subListener = new TestListener();

            subClCh.register(TestPacket.class, subListener);

            listener.reset();
            subListener.reset();
            seConn.send(subSeCh, new TestPacket("sub channels working", 194));

            assertEquals(1, subListener.called);
            assertEquals("sub channels working", subListener.lastString);
            assertEquals(194, subListener.lastInt);

            assertEquals(0, listener.called);
            assertEquals(null, listener.lastString);
            assertEquals(0, listener.lastInt);
        }
    }

    public class TestListener implements SinglePacketListener<TestPacket> {
        public String lastString;
        public int lastInt;
        public int called;

        public void reset() {
            lastString = null;
            lastInt = 0;
            called = 0;
        }

        @Override
        public void onPacket(Connection connection, TestPacket packet) {
            assertNotNull(connection);
            called++;
            lastString = packet.testString;
            lastInt = packet.testInt;
        }
    }
}
