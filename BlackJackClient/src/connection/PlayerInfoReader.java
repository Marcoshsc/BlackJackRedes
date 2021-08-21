package connection;

import domain.Player;
import domain.enums.Shape;
import main.ConnectionHandler;

import java.util.Scanner;

public class PlayerInfoReader {

    public Player readInfo() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem vindo ao jogo da velha! Qual seu nome de usuário?");
        String username = scanner.nextLine().replace("/", "");
        System.out.printf("Bom te conhecer %s! Agora diz ai: quer jogar com bolinha (0) ou com X (1)?\n", username);
        int value = scanner.nextInt();
        Shape shape = value == 0 ? Shape.CIRCLE : Shape.X;
        System.out.println("Boa escolha! Última coisa: quer jogar contra um player de verdade (0) ou contra o computador (1)?");
        boolean computer = scanner.nextInt() == 1;
        System.out.printf("Massa demais %s! Pra mim isso já é o suficiente! Bom jogo!\n", username);
        return new Player(shape, username, computer);
    }

}
