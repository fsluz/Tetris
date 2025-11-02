package tetris.engine;

import java.awt.event.*;
import tetris.domain.*;

public class InputHandler implements KeyListener {
    private final Partida partida;

    public InputHandler(Partida partida) { this.partida = partida; }

    @Override
    public void keyPressed(KeyEvent e) {
        if (partida.isGameOver()) return;
        
        Tetromino peca = partida.getPecaAtual();
        Tabuleiro tabuleiro = partida.getTabuleiro();
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> {
                Posicao posAnterior = peca.getPosicao();
                peca.moverEsquerda();
                if (!tabuleiro.posicaoValida(peca)) {
                    peca.setPosicao(posAnterior);
                }
            }
            case KeyEvent.VK_RIGHT -> {
                Posicao posAnterior = peca.getPosicao();
                peca.moverDireita();
                if (!tabuleiro.posicaoValida(peca)) {
                    peca.setPosicao(posAnterior);
                }
            }
            case KeyEvent.VK_DOWN -> {
                Posicao posAnterior = peca.getPosicao();
                peca.moverBaixo();
                if (!tabuleiro.posicaoValida(peca)) {
                    peca.setPosicao(posAnterior);
                }
            }
            case KeyEvent.VK_UP -> {
                Posicao posAnterior = peca.getPosicao();
                peca.rotacionar();
                if (!tabuleiro.posicaoValida(peca)) {
                    // Volta para posição anterior se inválida (3 rotações = 1 completa = volta ao original)
                    peca.rotacionar();
                    peca.rotacionar();
                    peca.rotacionar();
                }
            }
            case KeyEvent.VK_SPACE -> partida.hardDrop(); // Hard drop - coloca a peça instantaneamente
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
