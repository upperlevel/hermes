package xyz.upperlevel.utils.packet.client;

import xyz.upperlevel.utils.packet.Endpoint;

public interface Client extends Endpoint {
    public ClientConnection getConnection();
}
