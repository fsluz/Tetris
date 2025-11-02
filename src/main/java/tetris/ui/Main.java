package tetris.ui;

import tetris.domain.Jogador;
import tetris.domain.Partida;

public class Main {
    public static void main(String[] args) {
        Jogador jogador = new Jogador("Jogador 1");
        Partida partida = new Partida(jogador);
        TelaPrincipal tela = new TelaPrincipal(partida);
        tela.iniciar();
    }
}
