package lobby;

import domain.LobbyUser;

import java.util.ArrayList;
import java.util.List;

public class LobbyManager {

    private final List<LobbyUser> lobbyUsers = new ArrayList<>();

    public synchronized void addToLobby(LobbyUser lobbyUser) {
        lobbyUsers.add(lobbyUser);
        notifyAll();
    }

    public synchronized void removeFromLobby(LobbyUser lobbyUser) {
        lobbyUsers.remove(lobbyUser);
        notifyAll();
    }

    public synchronized boolean existsOnLobby(LobbyUser user) {
        for (LobbyUser lobbyUser : lobbyUsers) {
            if(user.getUsername().equals(lobbyUser.getUsername()))
                return true;
        }
        return false;
    }

    public List<LobbyUser> getLobbyUsers() {
        return lobbyUsers;
    }
}
