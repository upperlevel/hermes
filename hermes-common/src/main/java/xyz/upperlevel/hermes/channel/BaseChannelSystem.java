package xyz.upperlevel.hermes.channel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseChannelSystem implements ChannelSystem {
    private int nextId = FIRST_ID;

    protected final Map<String, Channel> channels = new HashMap<>();

    protected Channel[] id_map = new Channel[MAX_IDS];

    @Override
    public void register(Channel ch) {
        if (ch.getId() == UNASSIGNED) {
            if (isOutOfIds())
                throw new IllegalStateException("Out of ids!");
            Channel present = channels.computeIfAbsent(ch.getName(), k -> ch);

            if (present != ch)
                throw new IllegalArgumentException("Name already in use");

            register0(ch);
        }
    }

    protected abstract void register0(Channel channel);

    protected void updateNextId() {
        if(isOutOfIds())
            return;
        while(id_map[nextId & 0xffff] != null)
            nextId++;
    }

    public int getNextId() {
        updateNextId();
        return nextId;
    }

    public int useNextId() {
        updateNextId();
        return nextId++;
    }

    @Override
    public boolean isOutOfIds() {
        return channels.size() >= MAX_IDS;
    }

    @Override
    public Channel get(int id) {
        if(id >= FIRST_ID && id <= LAST_ID)
            return id_map[id & 0xffff];
        return null;
    }

    @Override
    public Channel get(String name) {
        return channels.get(name);
    }

    @Override
    public Collection<Channel> get() {
        return Collections.unmodifiableCollection(channels.values());
    }
}
