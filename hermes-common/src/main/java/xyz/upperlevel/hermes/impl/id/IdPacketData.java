package xyz.upperlevel.hermes.impl.id;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.PacketSide;

@AllArgsConstructor
@RequiredArgsConstructor
public class IdPacketData<P extends Packet> {
    public final Class<P> packet;
    public final PacketSide side;
    public int id = -1;
}
