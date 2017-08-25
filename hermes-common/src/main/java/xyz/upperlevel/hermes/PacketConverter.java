package xyz.upperlevel.hermes;

import io.netty.buffer.ByteBuf;

public interface PacketConverter {

    void toData(Packet packet, ByteBuf out);

    Packet fromData(ByteBuf in);
}
