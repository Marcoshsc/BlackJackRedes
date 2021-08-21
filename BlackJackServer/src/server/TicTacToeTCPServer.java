package server;

import game.GameManager;
import lobby.LobbyManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TicTacToeTCPServer implements Runnable {

    private static final int PORT = 3002;
    private final LobbyManager lobbyManager = new LobbyManager();

    @Override
    public void run() {
        runServer();
    }

    private void runServer() {
        try(ServerSocket serverSocket = new ServerSocket(PORT);) {
            while(true) {
                Socket connection = serverSocket.accept();
                ConnectionHandler connectionHandler = new ConnectionHandler(connection);
                GameManager gameManager = GameManager.gameFinder(lobbyManager, connectionHandler);
                Thread thread = new Thread(gameManager);
                thread.start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
