package xyz.upperlevel.hermes.server;

import xyz.upperlevel.hermes.Connection;

public interface ServerConnection extends Connection {
    @Override
    Server getParent();
}
