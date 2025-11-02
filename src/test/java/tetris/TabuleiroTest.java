package tetris;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import tetris.domain.*;
import java.awt.Color;

public class TabuleiroTest {
    private Tabuleiro tabuleiro;

    @BeforeEach
    void setUp() {
        tabuleiro = new Tabuleiro();
    }

    @Test
    void devePermitirPosicaoValida() {
        Tetromino peca = new Tetromino() {
            {
                forma = new boolean[][]{{true}};
                cor = Color.BLUE;
                posicao = new Posicao(5,5);
            }
            @Override
            public void rotacionar() {
                // método vazio para teste
            }
        }; 
        assertTrue(tabuleiro.posicaoValida(peca));
    }

    @Test
    void deveDetectarPosicaoInvalidaForaDoTabuleiro() {
        Tetromino peca = new Tetromino() {
            {
                forma = new boolean[][]{{true}};
                cor = Color.BLUE;
            }
            @Override
            public void rotacionar() {
                // método vazio para teste
            }
        }; 
        assertFalse(tabuleiro.posicaoValida(peca));
    }

    @Test
    void deveEliminarLinhaCompleta() {
        boolean[][] grid = tabuleiro.getGrid();
        for (int x = 0; x < Tabuleiro.LARGURA; x++) grid[Tabuleiro.ALTURA - 1][x] = true;
        int eliminadas = tabuleiro.eliminarLinhasCompletas();
        assertEquals(1, eliminadas);
    }
}


