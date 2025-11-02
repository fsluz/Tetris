package tetris;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import tetris.domain.*;

public class PartidaTest {
    private Partida partida;

    @BeforeEach
    void setUp() {
        Jogador jogador = new Jogador("Tester");
        partida = new Partida(jogador);
    }

    @Test
    void deveInicializarComJogadorEPartidaValida() {
        assertNotNull(partida.getJogador());
        assertNotNull(partida.getTabuleiro());
        assertNotNull(partida.getSistemaPontuacao());
        assertNotNull(partida.getPecaAtual());
    }

    @Test
    void deveExecutarTickSemGameOver() {
        partida.tick();
        assertFalse(partida.isGameOver());
    }

    @Test
    void deveGerarGameOverQuandoPosicaoInvalida() {
        boolean[][] grid = partida.getTabuleiro().getGrid();
        for (int y = 0; y < Tabuleiro.ALTURA; y++)
            for (int x = 0; x < Tabuleiro.LARGURA; x++)
                grid[y][x] = true;
        partida.tick();
        assertTrue(partida.isGameOver());
    }
}
