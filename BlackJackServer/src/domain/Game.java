package domain;

import domain.enums.PlayerStatus;
import server.ConnectionHandler;

import java.util.*;

public class Game {

    private final Deck deck = new Deck();
    private final List<Player> players;
    private String turn = null;
    private String stage;
    private double currentBet = 0d;

    public Game(List<Player> players) {
        this.players = players;
        for (Player player : getPlayers()) {
            drawCard(player);
            drawCard(player);
        }
    }

    public void drawCard(Player player) {
        Card card = deck.getCard();
        player.addCard(card);
    }

    public int getValidPlayers() {
        int number = 0;
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getStatus() == PlayerStatus.PLAYING)
                number++;
        }
        return number;
    }

    public boolean everyoneBetted() {
        for (Player player : players) {
            if(!player.isBetted())
                return false;
        }
        return true;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public double getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(double currentBet) {
        this.currentBet = currentBet;
    }

    public boolean shouldCalculateWinners() {
        boolean should = true;
        for (Player player : players) {
            if(player.getStatus() != PlayerStatus.PLAYING)
                continue;
            if(player.getValue() == 21)
                return true;
            if (player.getLastDecision() == null || player.getLastDecision().equals("draw")) {
                should = false;
                break;
            }
        }
        return should;
    }

    public void gameWon(List<Player> winners) {
        List<Player> losers = new ArrayList<>();
        for (Player player : players) {
            if(winners.contains(player))
                continue;
            losers.add(player);
        }
        double totalMoney = 0d;
        for (Player player : losers) {
            totalMoney += player.getBet();
        }

        if(!winners.isEmpty()) {
            double forEach = totalMoney / winners.size();
            for (Player player : winners) {
                player.increaseBalance(forEach);
            }
        }

        // somente ao final
        for (Player player : players) {
            player.reset();
        }
    }

    public List<Player> getWinners() {
        List<Player> winners = new ArrayList<>();
        boolean blackJack = false;
        for (Player player : players) {
            if(player.blackJack()) {
                winners.add(player);
                blackJack = true;
                continue;
            }
            int points = player.getValue();
            if(points == 21 && !blackJack) {
                winners.add(player);
            }
        }
        if(winners.isEmpty()) {
            int greater = -1;
            for(Player player : players) {
                int score = player.getValue();
                if(greater < score && score < 21) {
                    greater = score;
                }
            }
            for (Player player : players) {
                if(player.getValue() == greater) {
                    winners.add(player);
                }
            }
        }
        return winners;
    }

    public static NetworkTransferable<Game> networkTransferable() {
        NetworkTransferable<Player> playerNetworkTransferable = Player.networkTransferable();
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(Game value) {
                String gameString = String.format("%s@%s@%s@%s@%s@%s@%f", playerNetworkTransferable.toTransferString(value.players.get(0)),
                        playerNetworkTransferable.toTransferString(value.players.get(1)),
                        playerNetworkTransferable.toTransferString(value.players.get(2)),
                        playerNetworkTransferable.toTransferString(value.players.get(3)),
                        value.getTurn(), value.getStage(), value.currentBet);
                System.out.println(gameString);
                return gameString;
            }

            @Override
            public Game fromTransferString(String transferString, ConnectionHandler connectionHandler) {
                throw new IllegalArgumentException("Server cannot read games.");
            }
        };
    }



    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
