package xyz.upperlevel.hermes;

import io.netty.buffer.ByteBuf;

import java.util.Set;

public interface PacketConverter {

    void toData(Packet packet, ByteBuf out);

    Packet fromData(ByteBuf in);

    Set<Class<?>> getRegistered();
}
