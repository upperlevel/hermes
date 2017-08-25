package xyz.upperlevel.hermes;

public interface PacketListener<P extends Packet> {
    void onPacket(Connection connection, P packet);
}
