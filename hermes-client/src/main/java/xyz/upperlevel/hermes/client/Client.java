package xyz.upperlevel.hermes.client;

import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.hermes.Endpoint;

import java.util.Collections;
import java.util.Set;

public interface Client extends Endpoint {
    ClientConnection getConnection();

    @Override
    default Set<Connection> getConnections() {
        return Collections.singleton(getConnection());
    }
}
