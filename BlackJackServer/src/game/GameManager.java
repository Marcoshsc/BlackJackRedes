package game;

import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Game;
import domain.LobbyUser;
import domain.Player;
import domain.enums.Shape;
import lobby.LobbyManager;
import server.ConnectionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

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
            if(lobbyUser.isComputer()) {
                runGameAgainstComputer();
                return;
            }
            LobbyUser opponent = findOpponent();
            if(opponent != null) {
                lobbyManager.removeFromLobby(opponent);
                runGameBetweenPlayers(opponent);
                return;
            }
            lobbyManager.addToLobby(lobbyUser);
            CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.LOBBY);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void runGameAgainstComputer() {
        try {
            Player computer = new Player(
                    lobbyUser.getShape() == Shape.CIRCLE ? Shape.X : Shape.CIRCLE,
                    null,
                    "Computer",
                    true
            );
            Player lobbyUserPlayer = new Player(
                    lobbyUser.getShape(),
                    connectionHandler,
                    lobbyUser.getUsername(),
                    false);
            communicateUsersAndStartGame(lobbyUserPlayer, computer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void communicateUsersAndStartGame(Player player1, Player player2) throws IOException {
        Game game = new Game(player1, player2);
        CommunicationHandler.of(connectionHandler).sendMessage(
                CommunicationTypes.GAME_FOUND,
                Game.networkTransferable(),
                game
        );
        if(!player2.isComputer()) {
            CommunicationHandler.of(player2.getConnectionHandler()).sendMessage(
                    CommunicationTypes.GAME_FOUND,
                    Game.networkTransferable(),
                    game
            );
        }
        GameInstance gameInstance = new GameInstance(game);
        Thread thread = new Thread(gameInstance);
        thread.start();
    }

    private void runGameBetweenPlayers(LobbyUser opponent) {
        try {
            Player opponentPlayer = new Player(
                    lobbyUser.getShape() == Shape.CIRCLE ? Shape.X : Shape.CIRCLE,
                    opponent.getConnectionHandler(),
                    opponent.getUsername(),
                    false
            );
            Player lobbyUserPlayer = new Player(
                    lobbyUser.getShape(),
                    connectionHandler,
                    lobbyUser.getUsername(),
                    false);
            communicateUsersAndStartGame(lobbyUserPlayer, opponentPlayer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private LobbyUser findOpponent() {
        for (LobbyUser user : lobbyManager.getLobbyUsers()) {
            if(!user.getShape().equals(lobbyUser.getShape())) {
                return user;
            }
        }
        return null;
    }

}
