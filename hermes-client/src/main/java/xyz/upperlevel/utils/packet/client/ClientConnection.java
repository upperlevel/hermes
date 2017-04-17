package xyz.upperlevel.utils.packet.client;

import xyz.upperlevel.utils.packet.Connection;

public interface ClientConnection extends Connection {
    @Override
    Client getParent();
}