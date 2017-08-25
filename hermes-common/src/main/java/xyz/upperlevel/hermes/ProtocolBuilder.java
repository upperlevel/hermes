package xyz.upperlevel.hermes;

import xyz.upperlevel.hermes.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.hermes.impl.id.IdProtocol;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProtocolBuilder {
    private final Map<Class<? extends Packet>, PacketSide> packets;

    public ProtocolBuilder(Map<Class<? extends Packet>, PacketSide> packets) {
        this.packets = packets;
    }

    public ProtocolBuilder() {
        this(new LinkedHashMap<>());
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> ProtocolBuilder packet(PacketSide side, Class<T> clazz) {
        packets.put(clazz, side);
        return this;
    }

    public ProtocolBuilder enableSubChannels() {
        packet(PacketSide.SHARED, ChannelMessagePacket.class);
        return this;
    }

    public Protocol build() {
        return new IdProtocol(packets);
    }

    public Map<Class<? extends Packet>, PacketSide> asMap() {
        return packets;
    }
}
