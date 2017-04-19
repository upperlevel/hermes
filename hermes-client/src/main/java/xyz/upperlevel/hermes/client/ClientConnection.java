package xyz.upperlevel.hermes.client;

import xyz.upperlevel.hermes.Connection;

public interface ClientConnection extends Connection {
    @Override
    Client getParent();
}