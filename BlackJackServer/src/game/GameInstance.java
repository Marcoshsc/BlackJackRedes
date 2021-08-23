package game;

import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.*;
import domain.enums.PlayerStatus;
import server.ConnectionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameInstance implements Runnable {

    private final Game game;
    private final PlayerQueue playerQueue;
    private Player currentPlayer;

    public GameInstance(Game game) {
        this.game = game;
        this.playerQueue = new PlayerQueue(game.getPlayers());
    }

    @Override
    public void run() {
        runGame();
    }

    private void runGame() {
        try {
            int i = 0;
            do {
                makeBetPhase();

                if(i == 0) {
                    List<Player> winners = game.getWinners();
                    if(!winners.isEmpty() && winners.get(0).getValue() == 21) {
                        game.gameWon(winners);
                        communicateGameEnd(winners);
                        break;
                    }
                }
                if(game.shouldCalculateWinners()) {
                    List<Player> winners = game.getWinners();
                    System.out.println("O jogo acabou!");
                    game.gameWon(winners);
                    communicateGameEnd(winners);
                    break;
                }
                i++;

                makeDrawPhase();

            } while (true);
            PlayAgainState playAgainState = new PlayAgainState(game);
            for (Player player : game.getPlayers()) {
                PlayerChoiceThread playerChoiceThread = new PlayerChoiceThread(player, playAgainState);
                Thread thread = new Thread(playerChoiceThread);
                thread.start();
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    private void communicateGameEnd(List<Player> winners) throws IOException {
        List<String> winnerUsernames = new ArrayList<>();
        for (Player player : winners) {
            winnerUsernames.add(player.getUsername());
        }
        for (Player player : game.getPlayers()) {
            CommunicationHandler.of(player.getConnectionHandler()).sendMessage(CommunicationTypes.GAME_END,
                    GameEndInfo.gameEndInfoNetworkTransferable(), new GameEndInfo(winnerUsernames,
                            winners.isEmpty() ? -1 : winners.get(0).getValue(), game.croupietValue(game.getCroupietCards())));
        }
    }

    private void makeDrawPhase() throws IOException {
        game.setStage("draw");
        for (int i = 0; i < game.getPlayers().size(); i++) {
            if(currentPlayer.getStatus() != PlayerStatus.PLAYING) {
                currentPlayer = playerQueue.dequeue();
                game.setTurn(currentPlayer.getUsername());
                continue;
            }
            updatePlayerGames();
            DrawDecision drawDecision = (DrawDecision) CommunicationHandler.of(currentPlayer.getConnectionHandler()).getMessage(
                    Collections.singletonList(CommunicationTypes.DRAW_DECISION),
                    Collections.singletonList(DrawDecision.networkTransferable())
            ).getValue();
            if(!drawDecision.isGiveup()) {
                game.drawCard(currentPlayer);
                currentPlayer.setLastDecision("draw");
            }
            else {
                currentPlayer.setLastDecision("keep");
            }
            currentPlayer = playerQueue.dequeue();
            game.setTurn(currentPlayer.getUsername());
        }
    }

    private void makeBetPhase() throws IOException {
        double currentBet = game.getCurrentBet();
        boolean newBet = false;
        game.setStage("bet");
        game.setCurrentBet(currentBet);
        if(currentPlayer == null) {
            currentPlayer = playerQueue.dequeue();
            game.setTurn(currentPlayer.getUsername());
        }
        do {
            newBet = false;
            System.out.printf("Current bet: %f\n", currentBet);
            for (int i = 0; i < game.getValidPlayers(); i++) {
                System.out.printf("%s - %f\n", currentPlayer.getUsername(), currentPlayer.getBet());
                if(currentPlayer.getStatus() != PlayerStatus.PLAYING ||
                        currentPlayer.getBet() == currentBet && game.everyoneBetted()) {
                    currentPlayer = playerQueue.dequeue();
                    game.setTurn(currentPlayer.getUsername());
                    continue;
                }
                updatePlayerGames();
                ConnectionHandler connectionHandler = currentPlayer.getConnectionHandler();
                RaiseDecision raiseDecision = (RaiseDecision) CommunicationHandler.of(connectionHandler).getMessage(
                        Collections.singletonList(CommunicationTypes.RAISE_DECISION),
                        Collections.singletonList(RaiseDecision.networkTransferable())).getValue();
                if(raiseDecision.isResign()) {
                    currentPlayer.giveUp();
                }
                else {
                    currentPlayer.raiseBet(raiseDecision.getRaiseValue());
                    currentPlayer.setBetted(true);
                    if(currentBet < currentPlayer.getBet()) {
                        currentBet = currentPlayer.getBet();
                        game.setCurrentBet(currentBet);
                        newBet = true;
                    }
                }
                if(i != game.getValidPlayers() - 1) {
                    currentPlayer = playerQueue.dequeue();
                    game.setTurn(currentPlayer.getUsername());
                }
            }
            boolean equal = areBetsEqual(currentBet);
            if(!equal) {
                double greater = game.getPlayers().get(0).getBet();
                for (Player player : game.getPlayers()) {
                    if(player.getStatus() != PlayerStatus.PLAYING)
                        continue;
                    if(greater < player.getBet())
                        greater = player.getBet();
                }
                newBet = true;
                currentBet = greater;
            }
        } while(newBet);

        for (Player player : game.getPlayers()) {
            player.setBetted(false);
        }
    }

    private boolean areBetsEqual(double currentBet) {
        boolean equal = true;
        for (int i = 0; i < game.getPlayers().size(); i++) {
            if(game.getPlayers().get(i).getStatus() != PlayerStatus.PLAYING)
                continue;
            if (game.getPlayers().get(i).getBet() != currentBet) {
                equal = false;
                break;
            }
        }
        return equal;
    }

    private void updatePlayerGames() throws IOException {
        for (Player player : game.getPlayers()) {
            CommunicationHandler.of(player.getConnectionHandler()).sendMessage(CommunicationTypes.GAME_INFO,
                    Game.networkTransferable(), player.getUsername(), game);
        }
    }
}
