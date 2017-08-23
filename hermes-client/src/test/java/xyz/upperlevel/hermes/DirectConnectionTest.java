package xyz.upperlevel.hermes;

import org.junit.Test;
import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.Listener;
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
        Channel clChannel = new Channel("main").setProtocol(PROTOCOL);
        Channel seChannel = new Channel("main").setProtocol(PROTOCOL);


        DirectServer server = new DirectServer(seChannel);
        DirectClient client = new DirectClient(clChannel);

        DirectServerConnection seConn = server.newConnection(client.getConnection());
        client.getConnection().setOther(seConn);

        PacketListener listener = new PacketListener();
        clChannel.register(listener);

        seConn.setCopy(false);
        listener.reset();

        seConn.send(seChannel, new TestPacket("price", 100));
        assertEquals(2, listener.called);
        assertEquals("price", listener.lastString);
        assertEquals(100, listener.lastInt);


        seConn.setCopy(true);
        listener.reset();

        seConn.send(seChannel, new TestPacket("new price", 90));

        assertEquals(2, listener.called);
        assertEquals("new price", listener.lastString);
        assertEquals(90, listener.lastInt);

        {
            Protocol subProto = Protocol.builder()
                    .register(TestPacket.class, TestPacket.CONVERTER)
                    .build();

            Channel subClCh = new Channel("sub").setProtocol(subProto);
            Channel subSeCh = new Channel("sub").setProtocol(subProto);

            server.getChannelSystem().register(subSeCh);
            client.getChannelSystem().register(subClCh);

            PacketListener subListener = new PacketListener();

            subClCh.register(subListener);

            listener.reset();
            subListener.reset();
            seConn.send(subSeCh, new TestPacket("sub channels working", 194));

            assertEquals(2, subListener.called);
            assertEquals("sub channels working", subListener.lastString);
            assertEquals(194, subListener.lastInt);

            assertEquals(0, listener.called);
            assertEquals(null, listener.lastString);
            assertEquals(0, listener.lastInt);
        }
    }

    public class PacketListener implements Listener {
        public String lastString;
        public int lastInt;
        public int called;

        @EventHandler
        protected void onTestPacket(TestPacket packet) {
            assertEquals(0, called);
            called++;
        }

        @EventHandler(priority = EventPriority.HIGH)
        protected void onTestPacketConn(Connection conn, TestPacket packet) {
            assertNotNull(conn);
            assertEquals(1, called);
            called++;
            lastString = packet.testString;
            lastInt = packet.testInt;
        }

        public void reset() {
            lastString = null;
            lastInt = 0;
            called = 0;
        }
    }

}
