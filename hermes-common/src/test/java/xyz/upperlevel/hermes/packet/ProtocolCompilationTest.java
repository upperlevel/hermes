package xyz.upperlevel.hermes.packet;

import io.netty.buffer.ByteBuf;
import org.junit.Test;
import xyz.upperlevel.hermes.Packet;
import xyz.upperlevel.hermes.PacketSide;
import xyz.upperlevel.hermes.Protocol;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class ProtocolCompilationTest {

    public final Protocol PROTOCOL = Protocol.builder()
            .packet(PacketSide.SHARED, SharedPacket.class)
            .packet(PacketSide.CLIENT, ClientPacket.class)
            .packet(PacketSide.SERVER, ServerPacket.class)
            .packet(PacketSide.SERVER, AnotherServerPacket.class)
            .build();


    @Test(expected = IllegalArgumentException.class)
    public void testShared() {
        PROTOCOL.compile(PacketSide.SHARED);
    }

    @Test
    public void testClient() {
        assertEquals(
                new HashSet<>(Arrays.asList(
                        SharedPacket.class,
                        ClientPacket.class
                )),
                PROTOCOL.compile(PacketSide.CLIENT).getRegistered()
        );
    }

    @Test
    public void testServer() {
        assertEquals(
                new HashSet<>(Arrays.asList(
                        SharedPacket.class,
                        ServerPacket.class,
                        AnotherServerPacket.class
                )),
                PROTOCOL.compile(PacketSide.SERVER).getRegistered()
        );
    }



    public static class SharedPacket implements Packet {

        @Override
        public void toData(ByteBuf out) {
        }

        @Override
        public void fromData(ByteBuf in) {
        }
    }

    public static class ClientPacket implements Packet {

        @Override
        public void toData(ByteBuf out) {
        }

        @Override
        public void fromData(ByteBuf in) {
        }
    }

    public static class ServerPacket implements Packet {

        @Override
        public void toData(ByteBuf out) {
        }

        @Override
        public void fromData(ByteBuf in) {
        }
    }

    public static class AnotherServerPacket implements Packet {

        @Override
        public void toData(ByteBuf out) {
        }

        @Override
        public void fromData(ByteBuf in) {
        }
    }
}
