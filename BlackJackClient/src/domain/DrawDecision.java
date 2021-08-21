package domain;

public class DrawDecision {

    private final boolean giveup;

    public DrawDecision(boolean giveup) {
        this.giveup = giveup;
    }

    public static NetworkTransferable<DrawDecision> networkTransferable() {
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(DrawDecision value) {
                return String.format("%s", value.giveup);
            }

            @Override
            public DrawDecision fromTransferString(String transferString) {
                return new DrawDecision(Boolean.parseBoolean(transferString));
            }
        };
    }

    public boolean isGiveup() {
        return giveup;
    }
}
