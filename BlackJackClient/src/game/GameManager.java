package game;

import communication.CommunicationAnswer;
import communication.CommunicationHandler;
import communication.CommunicationTypes;
import connection.PlayerInfoReader;
import domain.Game;
import domain.Player;
import main.ConnectionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class GameManager {

    private final ConnectionHandler connectionHandler;

    public GameManager(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public void manageGame() throws IOException {
        registerPlayer();
    }

    private void registerPlayer() throws IOException {
        CommunicationAnswer answer;
        Player player;
        while(true) {
            PlayerInfoReader playerInfoReader = new PlayerInfoReader();
            player = playerInfoReader.readInfo();
            CommunicationHandler.of(connectionHandler).sendMessage(
                    CommunicationTypes.INFORMATION,
                    Player.networkTransferable(),
                    player
            );
            answer = CommunicationHandler.of(connectionHandler).getMessage(
                    Arrays.asList(CommunicationTypes.LOBBY, CommunicationTypes.GAME_FOUND, CommunicationTypes.INVALID_USERNAME),
                    Arrays.asList(null, Game.networkTransferable(), null)
            );
            if(answer.getType() != CommunicationTypes.INVALID_USERNAME)
                break;
            System.out.println("Nome de usuario ja existente! Escolha outro...");
        }
        if(answer.getType() == CommunicationTypes.LOBBY) {
            System.out.println("Você foi colocado no lobby! Lhe avisaremos assim que encontrarmos um oponente para você!");
            CommunicationAnswer gameFoundAnswer = CommunicationHandler.of(connectionHandler).getMessage(
                    Collections.singletonList(CommunicationTypes.GAME_FOUND),
                    Collections.singletonList(Game.networkTransferable())
            );
            startGame(gameFoundAnswer, player);
        }
        else {
            startGame(answer, player);
        }
    }

    private void startGame(CommunicationAnswer answer, Player player) {
        System.out.println("Partida Encontrada!");
        Game game = (Game) answer.getValue();
        GameRunner gameRunner = new GameRunner(game, connectionHandler, player.getUsername());
        Thread thread = new Thread(gameRunner);
        thread.start();
    }
}
