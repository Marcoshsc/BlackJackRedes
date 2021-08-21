package domain;

import domain.enums.Shape;
import server.ConnectionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {

    private final Player player1;
    private final Player player2;
    private final Shape[][] board;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new Shape[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = null;
            }
        }
    }

    public void makeMove(Move move, Shape shape) {
        int row = move.getRow();
        int column = move.getColumn();
        if(board[row][column] != null)
            throw new IllegalArgumentException("Invalid move.");
        board[row][column] = shape;
    }

    public Move getRandomValidMove() {
        List<Move> validMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j] == null)
                    validMoves.add(new Move(i, j));
            }
        }
        int total = validMoves.size();
        int index = new Random().nextInt(total);
        return validMoves.get(index);
    }

    public boolean isFinished() {
        if(draw())
            return true;
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

    private boolean draw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(board[i][j] == null)
                    return false;
            }
        }
        return true;
    }

    public static NetworkTransferable<Game> networkTransferable() {
        NetworkTransferable<Player> playerNetworkTransferable = Player.networkTransferable();
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(Game value) {
                return String.format("%s@%s@%s", playerNetworkTransferable.toTransferString(value.player1),
                        playerNetworkTransferable.toTransferString(value.player2), value.getBoardString());
            }

            @Override
            public Game fromTransferString(String transferString, ConnectionHandler connectionHandler) {
                throw new IllegalArgumentException("Server cannot read games.");
            }
        };
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    private String getBoardString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                stringBuilder.append(board[i][j] != null ? board[i][j].toString() : "null");
                if(i != 2 || j != 2)
                    stringBuilder.append("|");
            }
        }
        return stringBuilder.toString();
    }

}
