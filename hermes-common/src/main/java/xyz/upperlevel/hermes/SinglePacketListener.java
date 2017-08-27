package xyz.upperlevel.hermes;

public interface SinglePacketListener<P extends Packet> {
    void onPacket(Connection connection, P packet);
}
