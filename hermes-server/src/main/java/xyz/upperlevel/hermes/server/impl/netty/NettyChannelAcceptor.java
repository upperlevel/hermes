package xyz.upperlevel.hermes.server.impl.netty;

import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NettyChannelAcceptor implements Runnable{
    private final ChannelFuture channelAcceptor;

    @Override
    public void run() {
        while(true) {
            try {
                channelAcceptor.sync();
            } catch (InterruptedException e) {
                System.out.println("Closing the Netty Server");
                e.printStackTrace();
                break;
            }
        }
    }
}
