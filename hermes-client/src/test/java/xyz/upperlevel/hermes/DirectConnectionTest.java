package xyz.upperlevel.hermes;

import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.Listener;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.client.impl.direct.DirectClient;
import xyz.upperlevel.hermes.server.impl.direct.DirectServer;
import xyz.upperlevel.hermes.server.impl.direct.DirectServerConnection;

import static xyz.upperlevel.hermes.TestPacket.PROTOCOL;

public class DirectConnectionTest {

    public static void main(String... args) {

        Channel clChannel = new Channel("main").setProtocol(PROTOCOL);
        Channel seChannel = new Channel("main").setProtocol(PROTOCOL);


        DirectServer server = new DirectServer(seChannel);
        DirectClient client = new DirectClient(clChannel);

        DirectServerConnection seConn = server.newConnection(client.getConnection());
        client.getConnection().setOther(seConn);

        clChannel.register(new PacketListener());

        seConn.setCopy(false);
        seConn.send(seChannel, new TestPacket("price", 100));
        seConn.setCopy(true);
        seConn.send(seChannel, new TestPacket("new price", 90));

        {
            Protocol subProto = Protocol.builder()
                    .register(TestPacket.class, TestPacket.CONVERTER)
                    .build();

            Channel subClCh = new Channel("sub").setProtocol(subProto);
            Channel subSeCh = new Channel("sub").setProtocol(subProto);

            server.getChannelSystem().register(subSeCh);
            client.getChannelSystem().register(subClCh);

            subClCh.register(
                TestPacket.class,
                (TestPacket p) -> System.out.println(p.testString + ": " + p.testInt)
            );

            seConn.send(subSeCh, new TestPacket("sub channel working?", 1));
        }
    }

    public static class PacketListener implements Listener {

        @EventHandler
        protected void onTestPacket(TestPacket packet) {
            System.out.println(packet.testString + ": " + packet.testInt);
        }

        @EventHandler(priority = EventPriority.HIGH)
        protected void onTestPacketConn(Connection conn, TestPacket packet) {
            System.out.println("Received new packet from " + conn);
        }
    }

}
