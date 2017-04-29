package xyz.upperlevel.hermes.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.hermes.Connection;

@RequiredArgsConstructor
public class ConnectionEvent<E> implements Event {
    @Getter
    private final Connection connection;
    @Getter
    private final E event;
}
