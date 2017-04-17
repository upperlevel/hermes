package xyz.upperlevel.utils.packet.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.upperlevel.utils.event.CancellableEvent;
import xyz.upperlevel.utils.packet.Connection;

@AllArgsConstructor
public class ConnectionOpenEvent extends CancellableEvent{
    @Getter
    private final Connection connection;
}
