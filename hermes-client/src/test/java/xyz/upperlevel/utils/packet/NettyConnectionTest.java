package xyz.upperlevel.utils.packet;

import xyz.upperlevel.utils.packet.channel.events.ChannelActiveEvent;
import xyz.upperlevel.utils.packet.client.ClientConnection;
import xyz.upperlevel.utils.packet.client.impl.netty.NettyClient;
import xyz.upperlevel.utils.packet.client.impl.netty.NettyClientOptions;
import xyz.upperlevel.utils.packet.event.impl.ConnectionOpenEvent;
import xyz.upperlevel.utils.packet.server.ServerConnection;
import xyz.upperlevel.utils.packet.server.impl.netty.NettyServer;
import xyz.upperlevel.utils.packet.server.impl.netty.NettyServerOptions;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static xyz.upperlevel.utils.packet.TestPacket.PROTOCOL;

public class NettyConnectionTest {

    static final String HOST = "localhosta";
    static final int PORT = 25346;
    static final CountDownLatch latch = new CountDownLatch(2);

    public static void main(String... args) throws InterruptedException, IOException, ExecutionException {
        xyz.upperlevel.utils.packet.channel.Channel clChannel = new xyz.upperlevel.utils.packet.channel.Channel("main").setProtocol(PROTOCOL);
        xyz.upperlevel.utils.packet.channel.Channel seChannel = new xyz.upperlevel.utils.packet.channel.Channel("main").setProtocol(PROTOCOL);


        NettyClient client;
        NettyServer server;

        ServerConnection serverConn;
        ClientConnection clientConn;

        CompletableFuture<ServerConnection> connFuture = new CompletableFuture<>();

        server = NettyServerOptions.builder()
                .port(PORT)
                .build()
                .openAsync(
                        //Init (register events)
                        (s) -> s.getEventManager().register(
                                ConnectionOpenEvent.class,
                                e -> connFuture.complete((ServerConnection) e.getConnection())
                        )
                )
                .get();


        client = NettyClientOptions.builder()
                .host(HOST)
                .port(PORT)
                .build()
                .openAsync()
                .get();

        clientConn = client.getConnection();
        serverConn = connFuture.get();

        clientConn.setDefaultChannel(clChannel);
        serverConn.setDefaultChannel(seChannel);

        clChannel.register(
                TestPacket.class,
                (TestPacket p) -> {
                    System.out.println(p.testString + ": " + p.testInt);
                    latch.countDown();
                }
        );

        serverConn.send(seChannel, new TestPacket("price", 100));
        serverConn.send(seChannel, new TestPacket("new price", 90));

        System.out.println("Waiting for the packets");
        latch.await();


        {
            Protocol subProto = Protocol.builder()
                    .register(TestPacket.class, TestPacket.CONVERTER)
                    .build();

            final CountDownLatch latch = new CountDownLatch(1);

            xyz.upperlevel.utils.packet.channel.Channel subClCh = new xyz.upperlevel.utils.packet.channel.Channel("sub").setProtocol(subProto);
            xyz.upperlevel.utils.packet.channel.Channel subSeCh = new xyz.upperlevel.utils.packet.channel.Channel("sub").setProtocol(subProto);

            subClCh.register(
                    TestPacket.class,
                    (TestPacket p) -> {
                        System.out.println(p.testString + ": " + p.testInt);
                        latch.countDown();
                    }
            );


            subClCh.register(
                    ChannelActiveEvent.class,
                    (Connection conn, ChannelActiveEvent e) -> serverConn.send(subSeCh, new TestPacket("sub channel working", 1))
            );

            server.getChannelSystem().register(subSeCh);
            client.getChannelSystem().register(subClCh);

            System.out.println("Waiting sub-packets");
            latch.await();
        }


        System.out.println("Closing");

        clientConn.close();
        serverConn.close();
        server.stop();
        System.out.println("Closed");
    }
}
