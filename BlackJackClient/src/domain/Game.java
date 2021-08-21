package domain;

import domain.enums.Shape;

import java.lang.invoke.StringConcatFactory;

public class Game {

    private final Player player1;
    private final Player player2;
    private final Shape[][] board;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new Shape[3][3];
    }

    public Game(Player player1, Player player2, Shape[][] board) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = board;
    }

    public boolean someoneWon() {
        return areShapesEqual(board[0][0], board[0][1], board[0][2]) ||
                areShapesEqual(board[1][0], board[1][1], board[1][2]) ||
                areShapesEqual(board[2][0], board[2][1], board[2][2]) ||
                areShapesEqual(board[0][0], board[1][0], board[2][0]) ||
                areShapesEqual(board[0][1], board[1][1], board[2][1]) ||
                areShapesEqual(board[0][2], board[1][2], board[2][2]) ||
                areShapesEqual(board[0][0], board[1][1], board[2][2]) ||
                areShapesEqual(board[0][2], board[1][1], board[2][0]);
    }

    private boolean areShapesEqual(Shape... shapes) {
        Shape first = shapes[0];
        if(first == null)
            return false;
        for (int i = 1; i < shapes.length; i++) {
            if(shapes[i] != first)
                return false;
        }
        return true;
    }

    public String getBoardString() {
        return String.format("%s | %s | %s\n---------\n%s | %s | %s\n---------\n%s | %s | %s\n---------\n",
                Shape.toPresentation(board[0][0]), Shape.toPresentation(board[0][1]), Shape.toPresentation(board[0][2]),
                Shape.toPresentation(board[1][0]), Shape.toPresentation(board[1][1]), Shape.toPresentation(board[1][2]),
                Shape.toPresentation(board[2][0]), Shape.toPresentation(board[2][1]), Shape.toPresentation(board[2][2]));
    }

    public static Shape[][] getBoardFromString(String str) {
        String[] values = str.split("\\|");
        return new Shape[][] {
                { Shape.valueOfNullable(values[0]), Shape.valueOfNullable(values[1]), Shape.valueOfNullable(values[2]) },
                { Shape.valueOfNullable(values[3]), Shape.valueOfNullable(values[4]), Shape.valueOfNullable(values[5]) },
                { Shape.valueOfNullable(values[6]), Shape.valueOfNullable(values[7]), Shape.valueOfNullable(values[8]) }
        };
    }

    public static NetworkTransferable<Game> networkTransferable() {
        NetworkTransferable<Player> playerNetworkTransferable = Player.networkTransferable();
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(Game value) {
                return String.format("%s@%s", playerNetworkTransferable.toTransferString(value.player1),
                        playerNetworkTransferable.toTransferString(value.player2));
            }

            @Override
            public Game fromTransferString(String transferString) {
                String[] players = transferString.split("@");
                return new Game(
                        playerNetworkTransferable.fromTransferString(players[0]),
                        playerNetworkTransferable.fromTransferString(players[1]),
                        Game.getBoardFromString(players[2])
                );
            }
        };
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
}
