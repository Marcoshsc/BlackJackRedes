package main;

public class Main {

    public static void main(String[] args) {
        Thread thread = new Thread(new BlackJackTCPClient("localhost", 3002));
        thread.start();
    }

}
