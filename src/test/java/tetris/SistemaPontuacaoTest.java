package tetris;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import tetris.domain.SistemaPontuacao;

public class SistemaPontuacaoTest {
    private SistemaPontuacao sistema;

    @BeforeEach
    void setUp() { sistema = new SistemaPontuacao(); }

    @Test
    void deveSomarPontosCorretamente() {
        sistema.adicionarLinhas(2);
        assertEquals(100, sistema.getPontos());
    }

    @Test
    void deveAumentarNivelAoUltrapassar1000() {
        for (int i = 0; i < 20; i++) sistema.adicionarLinhas(4);
        assertTrue(sistema.getNivel() > 1);
    }
}
