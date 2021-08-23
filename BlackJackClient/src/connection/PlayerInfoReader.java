package connection;

import domain.Player;
import domain.enums.Shape;
import main.ConnectionHandler;

import java.util.Scanner;

public class PlayerInfoReader {

    public Player readInfo() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem vindo ao jogo de BlackJack! Qual seu nome de usuário?");
        String username = scanner.nextLine().replace("/", "");
        System.out.printf("Massa demais %s! Pra mim isso já é o suficiente! Bom jogo!\n", username);
        return new Player(username);
    }

}
