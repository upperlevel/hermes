package xyz.upperlevel.hermes;

import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.channel.events.ChannelActiveEvent;
import xyz.upperlevel.hermes.client.ClientConnection;
import xyz.upperlevel.hermes.client.impl.netty.NettyClient;
import xyz.upperlevel.hermes.client.impl.netty.NettyClientOptions;
import xyz.upperlevel.hermes.event.impl.ConnectionOpenEvent;
import xyz.upperlevel.hermes.server.ServerConnection;
import xyz.upperlevel.hermes.server.impl.netty.NettyServer;
import xyz.upperlevel.hermes.server.impl.netty.NettyServerOptions;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class NettyConnectionTest {

    static final String HOST = "localhost";
    static final int PORT = 25346;
    static final CountDownLatch latch = new CountDownLatch(2);

    public static void main(String... args) throws InterruptedException, IOException, ExecutionException {
        Channel clChannel = new Channel("main").setProtocol(TestPacket.PROTOCOL);
        Channel seChannel = new Channel("main").setProtocol(TestPacket.PROTOCOL);


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

            Channel subClCh = new Channel("sub").setProtocol(subProto);
            Channel subSeCh = new Channel("sub").setProtocol(subProto);

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
