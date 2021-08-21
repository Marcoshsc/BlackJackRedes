package domain;

public class Move {

    private final int row;
    private final int column;

    public Move(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public static NetworkTransferable<Move> networkTransferable() {
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(Move value) {
                return String.format("%d/%d", value.row, value.column);
            }

            @Override
            public Move fromTransferString(String transferString) {
                String[] parts = transferString.split("/");
                return new Move(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
        };
    }

}
