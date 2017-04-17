package xyz.upperlevel.utils.packet;

import lombok.Data;
import xyz.upperlevel.utils.packet.impl.IdProtocol;

import java.util.List;

public interface Protocol {

    <T extends Packet> PacketConverter<T> getConverter(Class<T> packet);

    default boolean isRegistered(Class<? extends Packet> packet) {
        return getConverter(packet) != null;
    }

    <T extends Packet> byte[] convert(T o, Class<T> clazz);

    Packet convert(byte[] data);

    @SuppressWarnings("unchecked")
    default byte[] convert(Packet packet) {
        return convert(packet, (Class<Packet>)packet.getClass());
    }

    //----------------HELPER CLASSES

    @Data
    class ConverterData<T extends Packet> {
        public final Class<T> clazz;
        public final PacketConverter<T> converter;
    }

    //----------------HELPER METHODS

    static Protocol build(List<ConverterData> converters) {
        return new IdProtocol(converters);
    }

    static ProtocolBuilder builder() {
        return new ProtocolBuilder();
    }
}

