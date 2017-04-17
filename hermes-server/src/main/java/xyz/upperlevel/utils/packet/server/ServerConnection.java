package xyz.upperlevel.utils.packet.server;

import xyz.upperlevel.utils.packet.Connection;

public interface ServerConnection extends Connection {
    @Override
    Server getParent();
}
