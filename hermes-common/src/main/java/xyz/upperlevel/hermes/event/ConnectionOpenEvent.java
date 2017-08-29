package xyz.upperlevel.hermes.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.upperlevel.event.CancellableEvent;
import xyz.upperlevel.hermes.Connection;

@AllArgsConstructor
public class ConnectionOpenEvent extends CancellableEvent {
    @Getter
    private final Connection connection;
}
