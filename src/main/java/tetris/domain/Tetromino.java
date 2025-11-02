package tetris.domain;

import java.awt.Color;
import java.io.Serializable;
import java.util.Random;

public abstract class Tetromino implements Serializable {
    private static final long serialVersionUID = 1L;
    protected boolean[][] forma;
    protected Color cor;
    protected Posicao posicao;

    public abstract void rotacionar();

    public void moverBaixo() {
        posicao = posicao.moverParaBaixo();
    }

    public void moverEsquerda() {
        posicao = posicao.moverParaEsquerda();
    }

    public void moverDireita() {
        posicao = posicao.moverParaDireita();
    }

    public boolean[][] getForma() { return forma; }
    public Color getCor() { return cor; }
    public Posicao getPosicao() { return posicao; }
    public void setPosicao(Posicao p) { this.posicao = p; }

    public static Tetromino gerarAleatorio() {
        return TetrominoFactory.gerarAleatorio();
    }
}
