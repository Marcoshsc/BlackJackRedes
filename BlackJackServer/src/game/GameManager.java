package game;

import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Game;
import domain.LobbyUser;
import domain.Player;
import lobby.LobbyManager;
import server.ConnectionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameManager implements Runnable {

    private final LobbyManager lobbyManager;
    private final ConnectionHandler connectionHandler;
    private LobbyUser lobbyUser;

    private GameManager(LobbyManager lobbyManager, ConnectionHandler connectionHandler) {
        this.lobbyManager = lobbyManager;
        this.connectionHandler = connectionHandler;
    }

    public static GameManager gameFinder(LobbyManager lobbyManager, ConnectionHandler connectionHandler) {
        return new GameManager(lobbyManager, connectionHandler);
    }

    @Override
    public void run() {
        findGame();
    }

    private void findGame() {
        try {
            lobbyUser = (LobbyUser) CommunicationHandler.of(connectionHandler).getMessage(
                    Collections.singletonList(CommunicationTypes.INFORMATION),
                    Collections.singletonList(LobbyUser.networkTransferable())
            ).getValue();
            List<LobbyUser> opponents = findOpponents();
            if(opponents.size() == 3) {
                for (LobbyUser lobbyUser : opponents) {
                    lobbyManager.removeFromLobby(lobbyUser);
                }
                runGameBetweenPlayers(opponents);
                return;
            }
            lobbyManager.addToLobby(lobbyUser);
            CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.LOBBY);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void communicateUsersAndStartGame(List<Player> players) throws IOException {
        Game game = new Game(players);
        for (Player player : players) {
            CommunicationHandler.of(player.getConnectionHandler()).sendMessage(
                    CommunicationTypes.GAME_FOUND,
                    Game.networkTransferable(),
                    player.getUsername(),
                    game
            );
        }
        GameInstance gameInstance = new GameInstance(game);
        Thread thread = new Thread(gameInstance);
        thread.start();
    }

    private void runGameBetweenPlayers(List<LobbyUser> opponents) {
        try {
            List<Player> players = new ArrayList<>();
            Player mainPlayer = new Player(lobbyUser.getConnectionHandler(), lobbyUser.getUsername());
            players.add(mainPlayer);
            for (LobbyUser lobbyUser : opponents) {
                Player player = new Player(lobbyUser.getConnectionHandler(), lobbyUser.getUsername());
                players.add(player);
            }
            communicateUsersAndStartGame(players);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private List<LobbyUser> findOpponents() {
        List<LobbyUser> lobbyUsers = new ArrayList<>();
        for (LobbyUser lobbyUser : lobbyManager.getLobbyUsers()) {
            lobbyUsers.add(lobbyUser);
            if(lobbyUsers.size() == 3) {
                return lobbyUsers;
            }
        }
        return lobbyUsers;
    }

}
