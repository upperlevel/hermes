package xyz.upperlevel.hermes;

import java.util.Set;

public interface Protocol {

    boolean isRegistered(Class<? extends Packet> packet);

    Set<Class<?>> getRegistered();

    PacketConverter compile(PacketSide side);

    //----------------HELPER METHODS

    static ProtocolBuilder builder() {
        return new ProtocolBuilder();
    }
}

