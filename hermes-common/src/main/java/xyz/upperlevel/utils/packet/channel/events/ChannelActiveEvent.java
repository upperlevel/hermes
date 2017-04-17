package xyz.upperlevel.utils.packet.channel.events;

import lombok.RequiredArgsConstructor;
import xyz.upperlevel.utils.event.Event;
import xyz.upperlevel.utils.packet.Packet;

/**
 * Called when a connection begins to use the channel.
 * After this event is called you can begin to use the channel with that connection, no packet will be discarded
 * This is NOT a packet, but it implements it because I need to throw it as an event in ConnectionEventHandler
 */
@RequiredArgsConstructor
public class ChannelActiveEvent implements Event, Packet {
}
