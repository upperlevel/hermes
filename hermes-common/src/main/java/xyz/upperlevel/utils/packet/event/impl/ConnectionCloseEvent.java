package xyz.upperlevel.utils.packet.event.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.upperlevel.utils.event.Event;
import xyz.upperlevel.utils.packet.Connection;

@AllArgsConstructor
public class ConnectionCloseEvent implements Event {
    @Getter
    private final Connection connection;
}
