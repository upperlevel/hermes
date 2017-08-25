package xyz.upperlevel.hermes.impl.id;

import lombok.Getter;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.PacketSide;
import xyz.upperlevel.hermes.Protocol;

import java.util.Map;
import java.util.Set;

public class IdProtocol implements Protocol {
    @Getter
    protected Map<Class<? extends Packet>, PacketSide> registry;

    public IdProtocol(Map<Class<? extends Packet>, PacketSide> packets) {
        registry = packets;
    }

    @Override
    public boolean isRegistered(Class<? extends Packet> packet) {
        return registry.containsKey(packet);
    }

    @Override
    public Set<Class<? extends Packet>> getRegistered() {
        return registry.keySet();
    }

    @Override
    public IdPacketConverter compile(PacketSide side) {
        return new IdPacketConverter(this, side);
    }
}
