package domain;


public class RaiseDecision {

    private final double raiseValue;
    private final boolean resign;

    public RaiseDecision(double raiseValue, boolean resign) {
        this.raiseValue = raiseValue;
        this.resign = resign;
    }

    public boolean isResign() {
        return resign;
    }

    public double getRaiseValue() {
        return raiseValue;
    }

    public static NetworkTransferable<RaiseDecision> networkTransferable() {
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(RaiseDecision value) {
                return String.format("%f/%s", value.raiseValue, value.resign);
            }

            @Override
            public RaiseDecision fromTransferString(String transferString) {
                String[] splitted = transferString.split("/");
                return new RaiseDecision(Double.parseDouble(splitted[0]), Boolean.parseBoolean(splitted[1]));
            }
        };
    }

}
