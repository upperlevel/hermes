package xyz.upperlevel.utils.packet;

public interface PacketConverter<T extends Packet> {

    byte[] toData(T packet);

    T toPacket(byte[] data);
}
