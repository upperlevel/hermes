package xyz.upperlevel.hermes.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.upperlevel.hermes.Connection;
import xyz.upperlevel.event.CancellableEvent;

@AllArgsConstructor
public class ConnectionOpenEvent extends CancellableEvent{
    @Getter
    private final Connection connection;
}
