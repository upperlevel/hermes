package xyz.upperlevel.hermes.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.hermes.Connection;

@AllArgsConstructor
public class ConnectionCloseEvent implements Event {
    @Getter
    private final Connection connection;
}
