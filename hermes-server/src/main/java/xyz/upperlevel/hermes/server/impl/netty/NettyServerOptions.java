package xyz.upperlevel.hermes.server.impl.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
@Builder
public class NettyServerOptions {
    public static final String DEFAULT_THREAD_NAME = "Netty Server Manager";

    private final int bossThreads, workerThreads;
    private final boolean tcpNoDelay;
    private final int so_backlog;
    private final LogLevel logLevel;
    private int port;

    public void openSync(Consumer<NettyServer> initializer, BiConsumer<NettyServer, Exception> callback) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        CountDownLatch shutdownLatch = new CountDownLatch(1);

        NettyServer server = new NettyServer(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            try {
                shutdownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new NettyServerInitializer(server));


            /*server.getEventManager().register(
                    ConnectionOpenEvent.class,
                    (e) -> {
                        connection = (ServerConnection) e.getConnection();
                        connectLatch.countDown();
                    }
            );*/

            //init
            if (initializer != null)
                initializer.accept(server);

            // Start the client.
            ChannelFuture f = b.bind(port);

            try {
                f.sync();
            } catch (Exception e) {
                callback.accept(null, e);
                return;
            }

            //System.out.println("Server started");
            callback.accept(server, null);

            // Wait until the connection is closed.
            Channel channel = f.channel();
            // startLatch.countDown();

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down the event loop to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            shutdownLatch.countDown();
        }
        //System.out.println("Server closed");
    }

    public void openAsync(Consumer<NettyServer> initializer, BiConsumer<NettyServer, Exception> callback, String threadName) {
        new Thread(() -> openSync(initializer, callback), threadName).start();
    }

    public void openAsync(Consumer<NettyServer> initializer, BiConsumer<NettyServer, Exception> callback) {
        openAsync(initializer, callback, DEFAULT_THREAD_NAME);
    }

    public Future<NettyServer> openAsync(Consumer<NettyServer> initializer, String threadName) {
        CompletableFuture<NettyServer> future = new CompletableFuture<>();
        openAsync(initializer, (s, e) -> {
            if (e != null)
                future.completeExceptionally(e);
            else
                future.complete(s);
        }, threadName);
        return future;
    }

    public Future<NettyServer> openAsync(Consumer<NettyServer> initializer) {
        return openAsync(initializer, DEFAULT_THREAD_NAME);
    }

    //Default values
    public static class NettyServerOptionsBuilder {
        private int bossThreads = 0, workerThreads = 0;
        private boolean tcpNoDelay = true;
        private int soBacklog = 100;
        private LogLevel logLevel = LogLevel.INFO;
    }
}
