package xyz.upperlevel.hermes.exceptions;

import lombok.Getter;
import xyz.upperlevel.hermes.Packet;

public class IllegalPacketException extends RuntimeException {
    @Getter
    private final Packet packet;

    public IllegalPacketException(Packet packet, String reason) {
        super("Received illegal packet: " + packet.toString() + ", reason:" + reason);
        this.packet = packet;
    }
}
