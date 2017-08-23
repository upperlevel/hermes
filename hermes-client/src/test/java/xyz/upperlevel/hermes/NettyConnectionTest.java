package xyz.upperlevel.hermes;

import org.junit.Test;
import xyz.upperlevel.event.EventHandler;
import xyz.upperlevel.event.EventPriority;
import xyz.upperlevel.event.Listener;
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
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NettyConnectionTest {

    static final String HOST = "localhost";
    static final int PORT = 25346;

    @Test
    public void test() throws InterruptedException, IOException, ExecutionException, TimeoutException {
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

        PacketListener listener = new PacketListener();

        clChannel.register(listener);


        listener.reset();
        serverConn.send(seChannel, new TestPacket("price", 100));
        listener.await();
        assertEquals(2, listener.called);
        assertEquals("price", listener.lastString);
        assertEquals(100, listener.lastInt);


        listener.reset();
        serverConn.send(seChannel, new TestPacket("new price", 90));
        listener.await();
        assertEquals(2, listener.called);
        assertEquals("new price", listener.lastString);
        assertEquals(90, listener.lastInt);


        {
            Protocol subProto = Protocol.builder()
                    .register(TestPacket.class, TestPacket.CONVERTER)
                    .build();

            Channel subClCh = new Channel("sub").setProtocol(subProto);
            Channel subSeCh = new Channel("sub").setProtocol(subProto);

            PacketListener subListener = new PacketListener();

            subClCh.register(subListener);
            listener.reset();
            subListener.reset();


            //We need wo wait for the channel to be activated
            subClCh.register(
                    ChannelActiveEvent.class,
                    (Connection conn, ChannelActiveEvent e) -> serverConn.send(subSeCh, new TestPacket("sub channels working", 194))
            );

            server.getChannelSystem().register(subSeCh);
            client.getChannelSystem().register(subClCh);

            subListener.await();
            assertEquals(2, subListener.called);
            assertEquals("sub channels working", subListener.lastString);
            assertEquals(194, subListener.lastInt);

            assertEquals(0, listener.called);
            assertEquals(null, listener.lastString);
            assertEquals(0, listener.lastInt);
        }


        System.out.println("Closing");

        clientConn.close();
        serverConn.close();
        server.stop();
        System.out.println("Closed");
    }

    public class PacketListener implements Listener {
        public CountDownLatch latch;
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
            latch.countDown();
        }

        public void reset() {
            lastString = null;
            lastInt = 0;
            called = 0;
            latch = new CountDownLatch(1);
        }

        public void await() throws TimeoutException, InterruptedException {
            if(!latch.await(2000, TimeUnit.MILLISECONDS))
                throw new TimeoutException("Cannot send packet!");
        }
    }
}
