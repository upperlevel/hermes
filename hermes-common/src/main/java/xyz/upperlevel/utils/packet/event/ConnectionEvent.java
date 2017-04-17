package xyz.upperlevel.utils.packet.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.utils.event.Event;
import xyz.upperlevel.utils.packet.Connection;

@RequiredArgsConstructor
public class ConnectionEvent<E> implements Event {
    @Getter
    private final Connection connection;
    @Getter
    private final E event;
}
