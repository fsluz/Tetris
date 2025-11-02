package tetris.domain;

import java.awt.Color;
import java.io.Serializable;
import java.util.Random;

public class TetrominoFactory {
    private static final Random random = new Random();
    
    public static Tetromino criar(TipoTetromino tipo) {
        return switch (tipo) {
            case I -> new TetrominoI();
            case O -> new TetrominoO();
            case T -> new TetrominoT();
            case S -> new TetrominoS();
            case Z -> new TetrominoZ();
            case J -> new TetrominoJ();
            case L -> new TetrominoL();
        };
    }
    
    public static Tetromino gerarAleatorio() {
        TipoTetromino[] tipos = TipoTetromino.values();
        TipoTetromino tipo = tipos[random.nextInt(tipos.length)];
        Tetromino tetromino = criar(tipo);
        tetromino.setPosicao(new Posicao(4, 0));
        return tetromino;
    }
    
    // Tetromino I
    private static class TetrominoI extends Tetromino implements Serializable {
        private static final long serialVersionUID = 1L;
        private int rotacao = 0;
        
        public TetrominoI() {
            forma = new boolean[][]{
                {true, true, true, true}
            };
            cor = new Color(64, 224, 208); // Ciano vibrante (teal/ciano)
            posicao = new Posicao(4, 0);
        }
        
        @Override
        public void rotacionar() {
            rotacao = (rotacao + 1) % 2;
            if (rotacao == 0) {
                forma = new boolean[][]{
                    {true, true, true, true}
                };
            } else {
                forma = new boolean[][]{
                    {true},
                    {true},
                    {true},
                    {true}
                };
            }
        }
    }
    
    // Tetromino O
    private static class TetrominoO extends Tetromino implements Serializable {
        private static final long serialVersionUID = 2L;
        public TetrominoO() {
            forma = new boolean[][]{
                {true, true},
                {true, true}
            };
            cor = new Color(255, 215, 0); // Amarelo dourado
            posicao = new Posicao(4, 0);
        }
        
        @Override
        public void rotacionar() {
            // O não precisa rotacionar
        }
    }
    
    // Tetromino T
    private static class TetrominoT extends Tetromino implements Serializable {
        private static final long serialVersionUID = 3L;
        private int rotacao = 0;
        
        public TetrominoT() {
            forma = new boolean[][]{
                {false, true, false},
                {true, true, true}
            };
            cor = new Color(255, 20, 147); // Magenta vibrante/rosa
            posicao = new Posicao(4, 0);
        }
        
        @Override
        public void rotacionar() {
            rotacao = (rotacao + 1) % 4;
            switch (rotacao) {
                case 0 -> forma = new boolean[][]{
                    {false, true, false},
                    {true, true, true}
                };
                case 1 -> forma = new boolean[][]{
                    {true, false},
                    {true, true},
                    {true, false}
                };
                case 2 -> forma = new boolean[][]{
                    {true, true, true},
                    {false, true, false}
                };
                case 3 -> forma = new boolean[][]{
                    {false, true},
                    {true, true},
                    {false, true}
                };
            }
        }
    }
    
    // Tetromino S
    private static class TetrominoS extends Tetromino implements Serializable {
        private static final long serialVersionUID = 4L;
        private int rotacao = 0;
        
        public TetrominoS() {
            forma = new boolean[][]{
                {false, true, true},
                {true, true, false}
            };
            cor = new Color(50, 205, 50); // Verde menta
            posicao = new Posicao(4, 0);
        }
        
        @Override
        public void rotacionar() {
            rotacao = (rotacao + 1) % 2;
            if (rotacao == 0) {
                forma = new boolean[][]{
                    {false, true, true},
                    {true, true, false}
                };
            } else {
                forma = new boolean[][]{
                    {true, false},
                    {true, true},
                    {false, true}
                };
            }
        }
    }
    
    // Tetromino Z
    private static class TetrominoZ extends Tetromino implements Serializable {
        private static final long serialVersionUID = 5L;
        private int rotacao = 0;
        
        public TetrominoZ() {
            forma = new boolean[][]{
                {true, true, false},
                {false, true, true}
            };
            cor = new Color(255, 69, 0); // Vermelho-laranja vibrante
            posicao = new Posicao(4, 0);
        }
        
        @Override
        public void rotacionar() {
            rotacao = (rotacao + 1) % 2;
            if (rotacao == 0) {
                forma = new boolean[][]{
                    {true, true, false},
                    {false, true, true}
                };
            } else {
                forma = new boolean[][]{
                    {false, true},
                    {true, true},
                    {true, false}
                };
            }
        }
    }
    
    // Tetromino J
    private static class TetrominoJ extends Tetromino implements Serializable {
        private static final long serialVersionUID = 6L;
        private int rotacao = 0;
        
        public TetrominoJ() {
            forma = new boolean[][]{
                {true, false, false},
                {true, true, true}
            };
            cor = new Color(30, 144, 255); // Azul royal
            posicao = new Posicao(4, 0);
        }
        
        @Override
        public void rotacionar() {
            rotacao = (rotacao + 1) % 4;
            switch (rotacao) {
                case 0 -> forma = new boolean[][]{
                    {true, false, false},
                    {true, true, true}
                };
                case 1 -> forma = new boolean[][]{
                    {true, true},
                    {true, false},
                    {true, false}
                };
                case 2 -> forma = new boolean[][]{
                    {true, true, true},
                    {false, false, true}
                };
                case 3 -> forma = new boolean[][]{
                    {false, true},
                    {false, true},
                    {true, true}
                };
            }
        }
    }
    
    // Tetromino L
    private static class TetrominoL extends Tetromino implements Serializable {
        private static final long serialVersionUID = 7L;
        private int rotacao = 0;
        
        public TetrominoL() {
            forma = new boolean[][]{
                {false, false, true},
                {true, true, true}
            };
            cor = new Color(255, 140, 0); // Laranja vibrante
            posicao = new Posicao(4, 0);
        }
        
        @Override
        public void rotacionar() {
            rotacao = (rotacao + 1) % 4;
            switch (rotacao) {
                case 0 -> forma = new boolean[][]{
                    {false, false, true},
                    {true, true, true}
                };
                case 1 -> forma = new boolean[][]{
                    {true, false},
                    {true, false},
                    {true, true}
                };
                case 2 -> forma = new boolean[][]{
                    {true, true, true},
                    {true, false, false}
                };
                case 3 -> forma = new boolean[][]{
                    {true, true},
                    {false, true},
                    {false, true}
                };
            }
        }
    }
}

