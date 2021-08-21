package main;

import game.GameManager;

import java.io.IOException;
import java.net.Socket;

public class TicTacToeTCPClient implements Runnable {

    private final String host;
    private final int port;

    public TicTacToeTCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        connect();
    }

    private void connect() {
        try {
            Socket socket = new Socket(host, port);
            GameManager gameManager = new GameManager(new ConnectionHandler(socket));
            gameManager.manageGame();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
