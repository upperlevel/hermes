package xyz.upperlevel.hermes.client;

import xyz.upperlevel.hermes.Endpoint;

public interface Client extends Endpoint {
    ClientConnection getConnection();
}
