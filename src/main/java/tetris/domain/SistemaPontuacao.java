package tetris.domain;

import java.io.Serializable;

public class SistemaPontuacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private int pontos = 0;
    private int nivel = 1;

    public void adicionarLinhas(int linhas) {
        int ganho = switch (linhas) {
            case 1 -> 40 * nivel;
            case 2 -> 100 * nivel;
            case 3 -> 300 * nivel;
            case 4 -> 1200 * nivel;
            default -> 0;
        };
        pontos += ganho;
        if (pontos / 1000 > nivel) nivel++;
    }

    public int getPontos() { return pontos; }
    public int getNivel() { return nivel; }
}
