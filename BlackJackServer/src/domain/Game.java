package domain;

import domain.enums.Faces;
import domain.enums.PlayerStatus;
import domain.enums.Suit;
import server.ConnectionHandler;

import java.util.*;

public class Game {

    private Deck deck = new Deck();
    private final List<Player> players;
    private String turn = null;
    private String stage;
    private double currentBet = 0d;
    private boolean croupietWon = false;
    private List<Card> croupietCards = new ArrayList<>();
    private Card croupietCard;
    private Card croupietSecondCard;

    public Game(List<Player> players) {
        this.players = players;
        for (Player player : getPlayers()) {
            drawCard(player);
            drawCard(player);
        }
        this.croupietCard = deck.getCard();
        this.croupietSecondCard = deck.getCard();
        this.croupietCards.add(croupietCard);
        this.croupietCards.add(croupietSecondCard);
    }

    public void reset() {
        this.deck = new Deck();
        for (Player player : getPlayers()) {
            player.reset();
            player.getCards().clear();
            drawCard(player);
            drawCard(player);
        }
        this.croupietCard = deck.getCard();
        this.croupietSecondCard = deck.getCard();
        this.croupietCards.clear();
        this.croupietCards.add(croupietCard);
        this.croupietCards.add(croupietSecondCard);
        this.stage = null;
        this.currentBet = 0d;
        this.croupietWon = false;
        this.turn = null;
    }

    public void drawCard(Player player) {
        Card card = deck.getCard();
        player.addCard(card);
        player.checkBlown();
    }

    public int getValidPlayers() {
        int number = 0;
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getStatus() == PlayerStatus.PLAYING)
                number++;
        }
        return number;
    }

    public List<Card> getCroupietCards() {
        return croupietCards;
    }

    public boolean isCroupietWon() {
        return croupietWon;
    }

    public boolean everyoneBetted() {
        for (Player player : players) {
            System.out.println(player.getUsername() + " " + player.getStatus() + " " + player.isBetted());
            if(player.getStatus() != PlayerStatus.PLAYING)
                continue;
            if(!player.isBetted())
                return false;
        }
        return true;
    }

    public void unbet(double currentBet) {
        for (Player player : players) {
            if(player.getBet() < currentBet)
                player.setBetted(false);
        }
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

    public boolean croupietBlackJack() {
        return (croupietCard.getFaces() == Faces.ACE && croupietCard.getSuit() == Suit.SPADES &&
                croupietSecondCard.getFaces() == Faces.JACK && croupietSecondCard.getSuit() == Suit.SPADES) ||
                        (croupietSecondCard.getFaces() == Faces.ACE && croupietSecondCard.getSuit() == Suit.SPADES &&
                                croupietCard.getFaces() == Faces.JACK && croupietCard.getSuit() == Suit.SPADES);
    }

    public int croupietValue(List<Card> cards) {
        int value1 = 0;
        int value2 = 0;
        for (Card card: cards) {
            value1 += card.getFaces().getValue1();
            value2 += card.getFaces().getValue2();
        }
        if(value1 <= 21 || value2 <= 21) {
            return Math.max(value2, value1);
        }
        return Math.min(value1, value2);
    }

    public void gameWon(List<Player> winners) {
        List<Player> losers = new ArrayList<>();
        for (Player player : players) {
            if(winners.contains(player))
                continue;
            losers.add(player);
        }
        double totalMoney = 0d;
        double greaterBet = -1;
        for (Player player : losers) {
            totalMoney += player.getBet();
            if(greaterBet < player.getBet()) {
                greaterBet = player.getBet();
            }
        }
        totalMoney += greaterBet;

        if(!winners.isEmpty()) {
            int size = croupietWon ? winners.size() + 1 : winners.size();
            double forEach = totalMoney / size;
            for (Player player : winners) {
                player.increaseBalance(forEach);
            }
        }

        // somente ao final
        for (Player player : players) {
            player.reset();
        }
    }

    public List<Player> getWinners(boolean start) {
        croupietWon = croupietBlackJack();
        int croupietPoints = croupietValue(Arrays.asList(croupietCard, croupietSecondCard));
        croupietWon = croupietWon || croupietPoints == 21;
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
                if(croupietPoints == 21)
                    croupietWon = true;
            }
        }
        if(start) {
            return winners;
        }
        if(winners.isEmpty() && !croupietWon) {
            int greater = -1;
            for(Player player : players) {
                int score = player.getValue();
                if(greater < score && score < 21) {
                    greater = score;
                }
            }
            int newCroupietPoints = croupietPoints;
            while(newCroupietPoints < greater) {
                System.out.println(newCroupietPoints);
                Card card = deck.getCard();
                croupietCards.add(card);
                if(newCroupietPoints + card.getFaces().getValue2() > 21) {
                    newCroupietPoints += card.getFaces().getValue1();
                }
                else {
                    newCroupietPoints += card.getFaces().getValue2();
                }
            }
            if(newCroupietPoints > 21 || newCroupietPoints == greater) {
                croupietWon = croupietWon || (newCroupietPoints == greater) || (newCroupietPoints > greater && newCroupietPoints <= 21);
                for (Player player : players) {
                    if (player.getValue() == greater) {
                        winners.add(player);
                    }
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
                return String.format("%s@%s@%s@%s@%s@%s@%f", playerNetworkTransferable.toTransferString(value.players.get(0)),
                        playerNetworkTransferable.toTransferString(value.players.get(1)),
                        playerNetworkTransferable.toTransferString(value.players.get(2)),
                        playerNetworkTransferable.toTransferString(value.players.get(3)),
                        value.getTurn(), value.getStage(), value.currentBet);
            }

            @Override
            public String toTransferString(Game value, String context) {
                return String.format("%s@%s@%s@%s@%s@%s@%f@%s", playerNetworkTransferable.toTransferString(value.players.get(0), context),
                        playerNetworkTransferable.toTransferString(value.players.get(1), context),
                        playerNetworkTransferable.toTransferString(value.players.get(2), context),
                        playerNetworkTransferable.toTransferString(value.players.get(3), context),
                        value.getTurn(), value.getStage(), value.currentBet, String.format("%s*%s",
                                value.croupietCard.getFaces(), value.croupietCard.getSuit()));
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
