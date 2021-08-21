package domain;

import domain.enums.Shape;
import main.ConnectionHandler;

public class Player {

    private final Shape shape;
    private final String username;
    private final boolean computer;

    public Player(Shape shape, String username, boolean computer) {
        this.shape = shape;
        this.username = username;
        this.computer = computer;
    }

    public static NetworkTransferable<Player> networkTransferable() {
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(Player value) {
                return String.format("%s/%s/%s", value.shape, value.username, value.computer);
            }

            @Override
            public Player fromTransferString(String transferString) {
                String[] values = transferString.split("/");
                return new Player(
                        Shape.valueOf(values[0]),
                        values[1],
                        Boolean.parseBoolean(values[2])
                );
            }
        };
    }

    public Shape getShape() {
        return shape;
    }

    public String getUsername() {
        return username;
    }

    public boolean isComputer() {
        return computer;
    }
}
