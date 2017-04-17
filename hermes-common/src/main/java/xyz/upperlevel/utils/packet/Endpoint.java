package xyz.upperlevel.utils.packet;

import xyz.upperlevel.utils.packet.channel.ChannelSystem;

public interface Endpoint {
    ChannelSystem getChannelSystem();

    void stop() throws InterruptedException;
}
