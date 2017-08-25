package xyz.upperlevel.hermes.impl.id;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.PacketConverter;
import xyz.upperlevel.hermes.PacketSide;
import xyz.upperlevel.hermes.exceptions.UnregisteredPacketException;

import javax.xml.bind.DatatypeConverter;
import java.util.*;
import java.util.stream.Collectors;

public class IdPacketConverter implements PacketConverter {
    @Getter
    protected final IdProtocol parent;
    @Getter
    private final PacketSide side;

    protected Map<Class<? extends Packet>, IdPacketData<?>> registry = new HashMap<>();
    protected IdPacketData<?>[] sided;

    public IdPacketConverter(IdProtocol parent, PacketSide side) {
        this.parent = parent;
        this.side = side;

        if (side == PacketSide.SHARED)
            throw new IllegalArgumentException("Cannot build non-sided protocol!");

        int len = (int) parent.getRegistry().entrySet().stream().map(h -> h.getValue().isSideCorrect(side)).count();
        sided = new IdPacketData<?>[len];
        int nextId = 0;
        for (Map.Entry<Class<? extends Packet>, PacketSide> entry : parent.getRegistry().entrySet()) {
            if (entry.getValue().isSideCorrect(side)) {
                IdPacketData<?> data = new IdPacketData<>(entry.getKey(), entry.getValue());
                sided[nextId & 0xffff] = data;
                data.id = nextId & 0xffff;
                registry.put(data.packet, data);
                nextId++;
            }
        }
    }

    @Override
    public void toData(Packet packet, ByteBuf out) {
        IdPacketData<?> converter = registry.get(packet.getClass());
        if (converter == null) throw new UnregisteredPacketException(packet);
        out.writeShort((short) converter.id);
        packet.toData(out);
    }

    @Override
    public Packet fromData(ByteBuf in) {
        if (in.readableBytes() < Short.BYTES)
            throw new IllegalArgumentException("The data passed isn't legal");
        int id = in.readUnsignedShort();

        if (id > sided.length)
            throw new IllegalArgumentException("Illegal data received! id:" + id + " data " + DatatypeConverter.printHexBinary(getArray(in)));

        IdPacketData<?> c = sided[id];
        if (c == null)
            return null;
        Packet packet;
        try {
            packet = c.packet.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot initialize packet", e);
        }
        packet.fromData(in);
        return packet;
    }

    @Override
    public Set<Class<?>> getRegistered() {
        return Arrays.stream(sided)
                .filter(Objects::nonNull)
                .map(e -> e.packet)
                .collect(Collectors.toSet());
    }

    private byte[] getArray(ByteBuf buffer) {
        byte[] array = new byte[buffer.readableBytes()];
        buffer.readBytes(array);
        return array;
    }
}
