package domain;

import server.ConnectionHandler;

import java.util.Objects;

public class LobbyUser {

    private final String username;
    private final ConnectionHandler connectionHandler;

    public LobbyUser(String username, ConnectionHandler connectionHandler) {
        this.username = username;
        this.connectionHandler = connectionHandler;
    }

    public static NetworkTransferable<LobbyUser> networkTransferable() {
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(LobbyUser value) {
                return String.format("%s", value.username);
            }

            @Override
            public LobbyUser fromTransferString(String transferString, ConnectionHandler connectionHandler) {
                String[] values = transferString.split("/");
                return new LobbyUser(
                        values[0],
                        connectionHandler
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
