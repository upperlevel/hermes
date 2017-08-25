package xyz.upperlevel.hermes.client.channel.impl;

import lombok.Getter;
import xyz.upperlevel.hermes.channel.BaseChannelSystem;
import xyz.upperlevel.hermes.channel.Channel;
import xyz.upperlevel.hermes.client.Client;
import xyz.upperlevel.hermes.client.channel.ClientChannelSystem;

public class SimpleClientChannelSystem extends BaseChannelSystem implements ClientChannelSystem {

    @Getter
    private final SimpleClientChannelSystemChild child;

    @Getter
    private final Client parent;

    public SimpleClientChannelSystem(SimpleClientChannelSystemChild child, Client parent) {
        this.child = child;
        this.parent = parent;
    }

    public SimpleClientChannelSystem(Client parent) {
        this.parent = parent;
        this.child = new SimpleClientChannelSystemChild(this);
    }

    @Override
    public void init() {
        child.init();
    }

    @Override
    protected void register0(Channel channel) {
        child.onChannelRegister(channel);
    }
}
