package xyz.upperlevel.hermes.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.hermes.Protocol;
import xyz.upperlevel.hermes.exceptions.UnregisteredPacketException;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.PacketConverter;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdProtocol implements Protocol {

    protected Map<Class, IdConverterData> handlers;
    protected IdConverterData[] byId;

    public IdProtocol(List<ConverterData> converters) {
        handlers = new HashMap<>(converters.size());
        byId = new IdConverterData[converters.size()];

        int nextId = Short.MIN_VALUE;

        for(ConverterData e : converters) {
            IdConverterData data = new IdConverterData(e.converter, nextId);
            handlers.put(e.clazz, data);
            byId[nextId & 0xFF] = data;
            nextId++;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Packet> PacketConverter<T> getConverter(Class<T> packet) {
        return (PacketConverter<T>) handlers.get(packet).converter;
    }

    @Override
    public boolean isRegistered(Class<? extends  Packet> packet) {
        return handlers.containsKey(packet);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Packet> byte[] convert(T o, Class<T> clazz) {
        IdConverterData converter = handlers.get(clazz);
        if(converter == null) throw new UnregisteredPacketException(o);
        byte[] data = converter.converter.toData(o);

        return ByteBuffer.allocate(data.length + Short.BYTES)
                .putShort((short) converter.id)
                .put(data)
                .array();
    }

    @Override
    public Packet convert(byte[] data) {
        if(data.length < Short.BYTES) throw new IllegalArgumentException("The data passed isn't legal");
        int id = ByteBuffer.wrap(data, 0, Short.BYTES).getShort() & 0xFF;

        if(id > byId.length)
            throw new IllegalArgumentException("Illegal data received! id:" + id + " data " + DatatypeConverter.printHexBinary(data));

        IdConverterData c = byId[id];
        if(c == null)
            return null;
        return c.converter.toPacket(Arrays.copyOfRange(data, Short.BYTES, data.length));
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    public class IdConverterData {
        public final PacketConverter converter;
        public int id = -1;
    }
}
