package domain;

import server.ConnectionHandler;

public class RaiseDecision {

    private final double raiseValue;

    public RaiseDecision(double raiseValue) {
        this.raiseValue = raiseValue;
    }

    public double getRaiseValue() {
        return raiseValue;
    }

    public static NetworkTransferable<RaiseDecision> networkTransferable() {
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(RaiseDecision value) {
                return String.format("%f", value.raiseValue);
            }

            @Override
            public RaiseDecision fromTransferString(String transferString, ConnectionHandler connectionHandler) {
                return new RaiseDecision(Double.parseDouble(transferString));
            }
        };
    }

}
