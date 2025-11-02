package tetris.domain;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;

public class Tabuleiro implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int LARGURA = 10;
    public static final int ALTURA = 20;

    private final boolean[][] grid = new boolean[ALTURA][LARGURA];
    private final Color[][] coresGrid = new Color[ALTURA][LARGURA]; // Armazena cores dos blocos fixados

    public boolean[][] getGrid() { return grid; }
    public Color[][] getCoresGrid() { return coresGrid; }

    public boolean posicaoValida(Tetromino t) {
        boolean[][] forma = t.getForma();
        Posicao p = t.getPosicao();

        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j]) {
                    int x = p.getX() + j;
                    int y = p.getY() + i;

                    if (x < 0 || x >= LARGURA || y >= ALTURA || y < 0)
                        return false;

                    if (y >= 0 && grid[y][x])
                        return false;
                }
            }
        }
        return true;
    }

    public void fixarPeca(Tetromino t) {
        boolean[][] forma = t.getForma();
        Posicao p = t.getPosicao();
        Color corPeca = t.getCor();
        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j]) {
                    int x = p.getX() + j;
                    int y = p.getY() + i;
                    if (y >= 0 && y < ALTURA && x >= 0 && x < LARGURA) {
                        grid[y][x] = true;
                        coresGrid[y][x] = corPeca; // Armazena a cor do bloco
                    }
                }
            }
        }
    }

    public int eliminarLinhasCompletas() {
        int eliminadas = 0;
        for (int y = ALTURA - 1; y >= 0; y--) {
            if (linhaCompleta(y)) {
                eliminarLinha(y);
                eliminadas++;
                y++; // recheck same line after shift
            }
        }
        return eliminadas;
    }

    private boolean linhaCompleta(int y) {
        for (boolean b : grid[y]) if (!b) return false;
        return true;
    }

    private void eliminarLinha(int y) {
        for (int i = y; i > 0; i--) {
            System.arraycopy(grid[i-1], 0, grid[i], 0, LARGURA);
            System.arraycopy(coresGrid[i-1], 0, coresGrid[i], 0, LARGURA);
        }
        Arrays.fill(grid[0], false);
        Arrays.fill(coresGrid[0], null);
    }
}
