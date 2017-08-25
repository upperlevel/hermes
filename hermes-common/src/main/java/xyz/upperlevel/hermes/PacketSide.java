package xyz.upperlevel.hermes;

public enum PacketSide {
    CLIENT {
        @Override
        public boolean isSideCorrect(PacketSide side) {
            return side == CLIENT;
        }
    },
    SERVER {
        @Override
        public boolean isSideCorrect(PacketSide side) {
            return side == SERVER;
        }
    },
    SHARED {
        @Override
        public boolean isSideCorrect(PacketSide side) {
            return true;
        }
    };

    public abstract boolean isSideCorrect(PacketSide side);
}
