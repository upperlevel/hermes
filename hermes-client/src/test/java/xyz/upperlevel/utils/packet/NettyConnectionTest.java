package xyz.upperlevel.utils.packet;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import xyz.upperlevel.utils.packet.channel.events.ChannelActiveEvent;
import xyz.upperlevel.utils.packet.client.impl.netty.NettyClient;
import xyz.upperlevel.utils.packet.client.impl.netty.NettyClientChannelInitializer;
import xyz.upperlevel.utils.packet.client.impl.netty.NettyClientConnection;
import xyz.upperlevel.utils.packet.event.impl.ConnectionOpenEvent;
import xyz.upperlevel.utils.packet.server.ServerConnection;
import xyz.upperlevel.utils.packet.server.impl.netty.NettyServer;
import xyz.upperlevel.utils.packet.server.impl.netty.NettyServerInitializer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static xyz.upperlevel.utils.packet.TestPacket.PROTOCOL;

public class NettyConnectionTest {

    static final String HOST = "localhost";
    static final int PORT = 25346;
    static final CountDownLatch latch = new CountDownLatch(2);

    public static void main(String... args) throws InterruptedException, IOException {
        xyz.upperlevel.utils.packet.channel.Channel clChannel = new xyz.upperlevel.utils.packet.channel.Channel("main").setProtocol(PROTOCOL);
        xyz.upperlevel.utils.packet.channel.Channel seChannel = new xyz.upperlevel.utils.packet.channel.Channel("main").setProtocol(PROTOCOL);

        TestServer server = new TestServer();
        new Thread(server::run).start();
        server.startLatch.await();

        TestClient client = new TestClient();
        new Thread(client::run).start();
        client.latch.await();

        server.connectLatch.await();

        Connection clientConn = client.connection;
        assert clientConn != null;
        ServerConnection serverConn = server.connection;
        assert serverConn != null;

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

            server.server.getChannelSystem().register(subSeCh);
            client.client.getChannelSystem().register(subClCh);

            System.out.println("Waiting sub-packets");
            latch.await();
        }


        System.out.println("Closing");

        clientConn.close();
        serverConn.close();
        server.server.stop();
        System.out.println("Closed");
    }



    static class TestClient {
        private NettyClient client;
        private NettyClientConnection connection;
        private CountDownLatch latch = new CountDownLatch(1);

        public void run() {
            EventLoopGroup group = new NioEventLoopGroup();
            client = new NettyClient();
            connection = client.getConnection();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new NettyClientChannelInitializer(connection));

                // Start the client.
                ChannelFuture f = b.connect(HOST, PORT).sync();

                Channel channel = f.channel();
                latch.countDown();

                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Shut down the event loop to terminate all threads.
                group.shutdownGracefully();
            }
            System.out.println("Client closed");
        }
    }

    static class TestServer {
        private NettyServer server = new NettyServer(this::stop);

        private CountDownLatch startLatch = new CountDownLatch(1);
        private CountDownLatch connectLatch = new CountDownLatch(1);

        private ServerConnection connection;

        private EventLoopGroup bossGroup, workerGroup;
        private ChannelFuture f;

        public void run() {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 100)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new NettyServerInitializer(server));


                server.getEventManager().register(
                        ConnectionOpenEvent.class,
                        (e) -> {
                            connection = (ServerConnection) e.getConnection();
                            connectLatch.countDown();
                        }
                );

                // Start the client.
                f = b.bind(PORT).sync();

                System.out.println("Server started");

                // Wait until the connection is closed.
                Channel channel = f.channel();

                startLatch.countDown();

                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Shut down the event loop to terminate all threads.
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
            System.out.println("Server closed");
        }

        public void stop() {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            try {
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
