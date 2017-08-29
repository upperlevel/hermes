package xyz.upperlevel.hermes.impl.id;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.PacketConverter;
import xyz.upperlevel.hermes.PacketSide;
import xyz.upperlevel.hermes.exceptions.UnregisteredPacketException;

import javax.xml.bind.DatatypeConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class IdPacketConverter implements PacketConverter {
    @Getter
    protected final IdProtocol parent;
    @Getter
    private final PacketSide side;

    protected Map<Class<? extends Packet>, Integer> registry = new HashMap<>();
    protected Class<? extends Packet>[] sided;

    @SuppressWarnings("unchecked")
    public IdPacketConverter(IdProtocol parent, PacketSide side) {
        this.parent = parent;
        this.side = side;

        if (side == PacketSide.SHARED)
            throw new IllegalArgumentException("Cannot build non-sided protocol!");

        //Get the other side's PacketSide
        PacketSide other = side == PacketSide.CLIENT ? PacketSide.SERVER : PacketSide.CLIENT;

        //Calculate how many packet types we can receive
        int len = (int) parent.getRegistry().entrySet().stream().map(h -> h.getValue().isSideCorrect(other)).count();
        sided = new Class[len];//And create the id registry with the calculated length


        int nextId = 0, otherNextId = 0;
        for (Map.Entry<Class<? extends Packet>, PacketSide> entry : parent.getRegistry().entrySet()) {
            //If the packet could be sent
            if (entry.getValue().isSideCorrect(side)) {
                //Register its class in the registry
                registry.put(entry.getKey(), nextId);
                //Update this side's id counter
                nextId++;
            }

            //If the packet could be received (or could be sent from the other endpoint)
            if(entry.getValue().isSideCorrect(other)){
                //Register the packet id
                sided[otherNextId & 0xffff] = entry.getKey();
                //Update the other side's id
                otherNextId++;
            }
        }
    }

    @Override
    public void toData(Packet packet, ByteBuf out) {
        Integer converter = registry.get(packet.getClass());
        if (converter == null) throw new UnregisteredPacketException(packet);
        out.writeShort(converter.shortValue());
        packet.toData(out);
    }

    @Override
    public Packet fromData(ByteBuf in) {
        if (in.readableBytes() < Short.BYTES)
            throw new IllegalArgumentException("The data passed isn't legal");
        int id = in.readUnsignedShort();

        if (id > sided.length)
            throw new IllegalArgumentException("Illegal data received! id:" + id + " data " + DatatypeConverter.printHexBinary(getArray(in)));

        Class<? extends Packet> c = sided[id];
        if (c == null)
            throw  new IllegalStateException("Unregistered packet received, id:" + id);
        Packet packet;
        try {
            packet = c.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot initialize packet", e);
        }
        packet.fromData(in);
        return packet;
    }

    @Override
    public Set<Class<?>> getRegistered() {
        return registry.keySet().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private byte[] getArray(ByteBuf buffer) {
        byte[] array = new byte[buffer.readableBytes()];
        buffer.readBytes(array);
        return array;
    }
}
