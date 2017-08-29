package xyz.upperlevel.hermes;

public interface SinglePacketListener<P extends Packet> {
    void onPacket(Connection connection, P packet);

    /**
     * It's important to implement this method if you want to remove the listener!
     * @return {@link java.lang.Object#hashCode hashCode}
     */
    @Override
    int hashCode();

    /**
     * It's important to implement this method if you want to remove the listener!
     * @return {@link java.lang.Object#equals(Object) equals}
     */
    @Override
    boolean equals(Object other);
}
