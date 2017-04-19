package xyz.upperlevel.hermes;

import xyz.upperlevel.hermes.channel.ChannelSystem;

public interface Endpoint {
    ChannelSystem getChannelSystem();

    void stop() throws InterruptedException;
}
