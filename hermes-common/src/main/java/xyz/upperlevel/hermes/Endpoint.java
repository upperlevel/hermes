package xyz.upperlevel.hermes;

import xyz.upperlevel.hermes.channel.ChannelSystem;

import java.util.Set;

public interface Endpoint {
    ChannelSystem getChannelSystem();

    void stop() throws InterruptedException;

    Set<Connection> getConnections();
}
