package lobby;

import domain.LobbyUser;

import java.util.ArrayList;
import java.util.List;

public class LobbyManager {

    private final List<LobbyUser> lobbyUsers = new ArrayList<>();

    public synchronized void addToLobby(LobbyUser lobbyUser) {
        lobbyUsers.add(lobbyUser);
        System.out.println("Added to lobby");
        notifyAll();
    }

    public synchronized void removeFromLobby(LobbyUser lobbyUser) {
        lobbyUsers.remove(lobbyUser);
        notifyAll();
    }

    public List<LobbyUser> getLobbyUsers() {
        return lobbyUsers;
    }
}
