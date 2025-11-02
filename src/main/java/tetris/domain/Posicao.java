package tetris.domain;

import java.io.Serializable;

public final class Posicao implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int x;
    private final int y;

    public Posicao(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Posicao moverParaBaixo() {
        return new Posicao(x, y + 1);
    }

    public Posicao moverParaCima() {
        return new Posicao(x, y - 1);
    }

    public Posicao moverParaDireita() {
        return new Posicao(x + 1, y);
    }

    public Posicao moverParaEsquerda() {
        return new Posicao(x - 1, y);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
