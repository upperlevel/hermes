package xyz.upperlevel.hermes.impl.id;

import lombok.Getter;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.PacketSide;
import xyz.upperlevel.hermes.Protocol;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IdProtocol implements Protocol {
    @Getter
    protected Map<Class<?>, IdPacketData<?>> registry;

    public IdProtocol(Map<Class<? extends Packet>, PacketSide> packets) {
        registry = packets.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new IdPacketData<>(e.getKey(), e.getValue())
                ));
    }

    @Override
    public boolean isRegistered(Class<? extends Packet> packet) {
        return registry.containsKey(packet);
    }

    @Override
    public Set<Class<?>> getRegistered() {
        return registry.keySet();
    }

    public IdPacketData<?> getData(Class<?> clazz) {
        return registry.get(clazz);
    }

    @Override
    public IdPacketConverter compile(PacketSide side) {
        return new IdPacketConverter(this, side);
    }
}
