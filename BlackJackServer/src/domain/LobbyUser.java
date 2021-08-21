package domain;

import domain.enums.Shape;
import server.ConnectionHandler;

import java.net.Socket;
import java.util.Objects;

public class LobbyUser {

    private final String username;
    private final ConnectionHandler connectionHandler;
    private final Shape shape;
    private final boolean computer;

    public LobbyUser(String username, ConnectionHandler connectionHandler, Shape shape, boolean computer) {
        this.username = username;
        this.connectionHandler = connectionHandler;
        this.shape = shape;
        this.computer = computer;
    }

    public static NetworkTransferable<LobbyUser> networkTransferable() {
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(LobbyUser value) {
                return String.format("%s/%s/%s", value.shape, value.username, value.computer);
            }

            @Override
            public LobbyUser fromTransferString(String transferString, ConnectionHandler connectionHandler) {
                String[] values = transferString.split("/");
                return new LobbyUser(
                        values[1],
                        connectionHandler,
                        Shape.valueOf(values[0]),
                        Boolean.parseBoolean(values[2])
                );
            }
        };
    }

    public String getUsername() {
        return username;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public Shape getShape() {
        return shape;
    }

    public boolean isComputer() {
        return computer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyUser lobbyUser = (LobbyUser) o;
        return username.equals(lobbyUser.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
