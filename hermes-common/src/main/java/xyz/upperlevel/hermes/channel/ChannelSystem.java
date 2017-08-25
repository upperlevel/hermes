package xyz.upperlevel.hermes.channel;

import xyz.upperlevel.hermes.Endpoint;

import java.util.Collection;

public interface ChannelSystem {
    int UNASSIGNED = Integer.MIN_VALUE;
    int FIRST_ID = 0;

    int LAST_ID = Short.MAX_VALUE - Short.MIN_VALUE;//This id is used for channel initialization messages

    int MAX_IDS = LAST_ID - FIRST_ID - 1;//The last channel is already occupied

    Endpoint getParent();

    void register(Channel ch);

    boolean isOutOfIds();

    Channel get(int id);

    Channel get(String name);

    Collection<Channel> get();
}
