package xyz.upperlevel.hermes.exceptions;

import xyz.upperlevel.hermes.Packet;

public class UnregisteredPacketException extends RuntimeException {
    public UnregisteredPacketException(Packet packet) {
        super("Cannot solve the converter for " + packet.getClass().getName());
    }
}
