package xyz.upperlevel.utils.packet.client.impl.netty;

import lombok.Getter;
import xyz.upperlevel.utils.packet.client.Client;
import xyz.upperlevel.utils.packet.client.channel.ClientChannelSystem;
import xyz.upperlevel.utils.packet.client.channel.impl.SimpleClientChannelSystem;

import java.util.function.BiConsumer;

public class NettyClient implements Client {

    @Getter
    private final ClientChannelSystem channelSystem = new SimpleClientChannelSystem(this);

    @Getter
    private final NettyClientConnection connection;

    public NettyClient() {
        this.connection = new NettyClientConnection(this, channelSystem.getChild());
    }

    @Override
    public void stop() throws InterruptedException {
        connection.close();
    }

    public static void open(String ip, int port, BiConsumer<NettyClient, Exception> callback) {

    }
}
