package xyz.upperlevel.hermes;

import io.netty.buffer.ByteBuf;

public interface Packet {
    void toData(ByteBuf out);

    void fromData(ByteBuf in);
}
