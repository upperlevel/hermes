package xyz.upperlevel.utils.packet.client.channel.impl;

import lombok.Getter;
import xyz.upperlevel.utils.packet.channel.BaseChannelSystem;
import xyz.upperlevel.utils.packet.channel.Channel;
import xyz.upperlevel.utils.packet.client.Client;
import xyz.upperlevel.utils.packet.client.channel.ClientChannelSystem;

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
    protected void register0(Channel channel) {
        child.onChannelRegister(channel);
    }
}
