package xyz.upperlevel.hermes.client.impl.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

@Data
@Builder
public class NettyClientOptions {
    public static final String DEFAULT_THREAD_NAME = "Netty Client Manager";

    private final int threadNumber;
    private final String host;
    private final int port;
    private final boolean tcpNoDelay;

    //default values
    public static class NettyClientOptionsBuilder {
        private int threadNumber = 0; //0 means default
        private boolean tcpNoDelay = true;
    }

    public void openSync(BiConsumer<NettyClient, Exception> callback) {
        EventLoopGroup group = new NioEventLoopGroup(threadNumber);
        NettyClient client = new NettyClient();
        NettyClientConnection connection = client.getConnection();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, tcpNoDelay)
                    .handler(new NettyClientChannelInitializer(connection));

            // Start the client.
            ChannelFuture f = b.connect(host, port);

            try {
                f.sync();
            } catch (Exception e) {
                callback.accept(null, e);
                return;
            }

            Channel channel = f.channel();

            callback.accept(client, null);

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public void openAsync(BiConsumer<NettyClient, Exception> callback, String name) {
        new Thread(() -> openSync(callback), name).start();
    }

    public void openAsync(BiConsumer<NettyClient, Exception> callback) {
        openAsync(callback, DEFAULT_THREAD_NAME);
    }

    public Future<NettyClient> openAsync(String threadName) {
        CompletableFuture<NettyClient> future = new CompletableFuture<>();
        openAsync((s, e) -> {
            if(e != null)
                future.completeExceptionally(e);
            else
                future.complete(s);
        }, threadName);
        return future;
    }

    public Future<NettyClient> openAsync() {
        return openAsync(DEFAULT_THREAD_NAME);
    }
}
