package xyz.upperlevel.hermes.channel.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.event.Event;
import xyz.upperlevel.hermes.Connection;

/**
 * Called when a connection begins to use the channel.
 * After this event is called you can begin to use the channel with that connection, no packet will be discarded
 */
@RequiredArgsConstructor
public class ChannelActiveEvent implements Event {
    @Getter
    private final Connection connection;
}
