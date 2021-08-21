package main;

import server.BlackJackTCPServer;

public class Main {

    public static void main(String[] args) {
        Thread thread = new Thread(new BlackJackTCPServer());
        thread.start();
    }

}
