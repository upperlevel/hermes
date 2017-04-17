package xyz.upperlevel.utils.packet.exceptions;

import lombok.Getter;
import xyz.upperlevel.utils.packet.Packet;

public class IllegalPacketException extends RuntimeException {
    @Getter
    private final Packet packet;

    public IllegalPacketException(Packet packet, String reason) {
        super("Received illegal packet: " + packet.toString() + ", reason:" + reason);
        this.packet = packet;
    }
}
