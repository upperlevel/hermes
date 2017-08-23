package xyz.upperlevel.hermes;

public interface PacketConverter<T extends Packet> {
    byte[] toData(T packet);

    T toPacket(byte[] data);
}
