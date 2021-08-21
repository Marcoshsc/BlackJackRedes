package main;

import server.TicTacToeTCPServer;

public class Main {

    public static void main(String[] args) {
        Thread thread = new Thread(new TicTacToeTCPServer());
        thread.start();
    }

}
