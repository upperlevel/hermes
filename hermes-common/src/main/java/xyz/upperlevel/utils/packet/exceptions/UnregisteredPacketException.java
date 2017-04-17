package xyz.upperlevel.utils.packet.exceptions;

import xyz.upperlevel.utils.packet.Packet;

public class UnregisteredPacketException extends RuntimeException {
    public UnregisteredPacketException(Packet packet) {
        super("Cannot solve the converter for " + packet.getClass().getName());
    }
}
