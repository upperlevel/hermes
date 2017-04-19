package xyz.upperlevel.hermes;

import xyz.upperlevel.hermes.Protocol.ConverterData;
import xyz.upperlevel.hermes.channel.packets.ChannelMessagePacket;
import xyz.upperlevel.hermes.impl.IdProtocol;

import java.util.ArrayList;
import java.util.List;

public class ProtocolBuilder {
    private final List<ConverterData> converters;

    public ProtocolBuilder(List<ConverterData> converters) {
        assert converters != null;
        this.converters = converters;
    }

    public ProtocolBuilder() {
        this(new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> ProtocolBuilder register(Class<T> clazz, PacketConverter<T> converter) {
        converters.add(new ConverterData(clazz, converter));
        return this;
    }

    public ProtocolBuilder subChannels() {
        register(ChannelMessagePacket.class, ChannelMessagePacket.CONVERTER);
        return this;
    }

    public Protocol build() {
        return new IdProtocol(converters);
    }

    public List<ConverterData> buildList() {
        return converters;
    }
}
