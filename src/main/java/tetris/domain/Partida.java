package tetris.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Partida implements Serializable {
    private final Jogador jogador;
    private final Tabuleiro tabuleiro;
    private final SistemaPontuacao sistemaPontuacao;
    private Tetromino pecaAtual;
    private Tetromino heldPiece = null;
    private final Queue<Tetromino> proximasPecas;
    private final List<TipoTetromino> historicoPecas;
    private boolean gameOver = false;
    private long tempoInicio; // Tempo de início da partida em milissegundos
    private static final int NUM_PROXIMAS_PECAS = 5;

    public Partida(Jogador jogador) {
        this.jogador = jogador;
        this.tabuleiro = new Tabuleiro();
        this.sistemaPontuacao = new SistemaPontuacao();
        this.proximasPecas = new LinkedList<>();
        this.historicoPecas = new ArrayList<>();
        this.tempoInicio = System.currentTimeMillis();
        
        // Inicializa a fila de próximas peças
        for (int i = 0; i < NUM_PROXIMAS_PECAS; i++) {
            proximasPecas.offer(TetrominoFactory.gerarAleatorio());
        }
        
        // Pega a primeira peça
        this.pecaAtual = proximasPecas.poll();
        proximasPecas.offer(TetrominoFactory.gerarAleatorio());
    }
    
    public long getTempoDecorrido() {
        if (gameOver) {
            // Retorna o tempo final quando o jogo termina
            return System.currentTimeMillis() - tempoInicio;
        }
        return System.currentTimeMillis() - tempoInicio;
    }
    
    public String getTempoFormatado() {
        long tempoMs = getTempoDecorrido();
        long segundos = tempoMs / 1000;
        long minutos = segundos / 60;
        long horas = minutos / 60;
        
        segundos = segundos % 60;
        minutos = minutos % 60;
        
        if (horas > 0) {
            return String.format("%02d:%02d:%02d", horas, minutos, segundos);
        }
        return String.format("%02d:%02d", minutos, segundos);
    }

    public void tick() {
        if (gameOver) return;
        
        // Move a peça para baixo
        Posicao posicaoAnterior = pecaAtual.getPosicao();
        pecaAtual.moverBaixo();

        // Se a nova posição é inválida, volta para a posição anterior e fixa a peça
        if (!tabuleiro.posicaoValida(pecaAtual)) {
            // Volta para a posição válida anterior
            pecaAtual.setPosicao(posicaoAnterior);
            
            // Fixa a peça no tabuleiro
            tabuleiro.fixarPeca(pecaAtual);
            
            // Adiciona ao histórico
            TipoTetromino tipo = obterTipo(pecaAtual);
            if (tipo != null) {
                historicoPecas.add(tipo);
            }
            
            // Elimina linhas completas e atualiza pontuação
            int linhasEliminadas = tabuleiro.eliminarLinhasCompletas();
            if (linhasEliminadas > 0) {
                sistemaPontuacao.adicionarLinhas(linhasEliminadas);
            }
            
            // Pega a próxima peça da fila
            pecaAtual = proximasPecas.poll();
            if (pecaAtual == null) {
                pecaAtual = TetrominoFactory.gerarAleatorio();
            }
            pecaAtual.setPosicao(new Posicao(4, 0));
            
            // Adiciona uma nova peça à fila
            proximasPecas.offer(TetrominoFactory.gerarAleatorio());
            
            // Verifica game over
            if (!tabuleiro.posicaoValida(pecaAtual)) {
                gameOver = true;
            }
        }
    }
    
    private TipoTetromino obterTipo(Tetromino tetromino) {
        // Identifica o tipo pela cor RGB
        java.awt.Color cor = tetromino.getCor();
        int rgb = cor.getRGB();
        
        // Cores atualizadas do TetrominoFactory
        if (rgb == new java.awt.Color(64, 224, 208).getRGB()) return TipoTetromino.I;  // Ciano
        if (rgb == new java.awt.Color(255, 215, 0).getRGB()) return TipoTetromino.O;      // Amarelo dourado
        if (rgb == new java.awt.Color(255, 20, 147).getRGB()) return TipoTetromino.T;    // Magenta/rosa
        if (rgb == new java.awt.Color(50, 205, 50).getRGB()) return TipoTetromino.S;     // Verde menta
        if (rgb == new java.awt.Color(255, 69, 0).getRGB()) return TipoTetromino.Z;      // Vermelho-laranja
        if (rgb == new java.awt.Color(30, 144, 255).getRGB()) return TipoTetromino.J;     // Azul royal
        if (rgb == new java.awt.Color(255, 140, 0).getRGB()) return TipoTetromino.L;    // Laranja
        
        return null;
    }

    public void hardDrop() {
        if (gameOver || pecaAtual == null) return;
        
        // Move a peça para baixo até colidir
        while (tabuleiro.posicaoValida(pecaAtual)) {
            pecaAtual.moverBaixo();
        }
        
        // Volta uma posição para cima (última posição válida)
        Posicao posAnterior = pecaAtual.getPosicao().moverParaCima();
        pecaAtual.setPosicao(posAnterior);
        
        // Fixa a peça e processa normalmente
        tabuleiro.fixarPeca(pecaAtual);
        TipoTetromino tipo = obterTipo(pecaAtual);
        if (tipo != null) {
            historicoPecas.add(tipo);
        }
        
        int linhasEliminadas = tabuleiro.eliminarLinhasCompletas();
        if (linhasEliminadas > 0) {
            sistemaPontuacao.adicionarLinhas(linhasEliminadas);
        }
        
        pecaAtual = proximasPecas.poll();
        if (pecaAtual == null) {
            pecaAtual = TetrominoFactory.gerarAleatorio();
        }
        pecaAtual.setPosicao(new Posicao(4, 0));
        proximasPecas.offer(TetrominoFactory.gerarAleatorio());
        
        if (!tabuleiro.posicaoValida(pecaAtual)) {
            gameOver = true;
        }
    }

    /**
     * Swap current piece with held piece. If no held piece, current piece is stored and next piece is spawned.
     * After swapping, the new current piece is placed at the default spawn position.
     */
    public void swapHold() {
        if (pecaAtual == null) return;
        if (heldPiece == null) {
            // store current, spawn next
            heldPiece = pecaAtual;
            pecaAtual = proximasPecas.poll();
            if (pecaAtual == null) pecaAtual = TetrominoFactory.gerarAleatorio();
            pecaAtual.setPosicao(new Posicao(4, 0));
        } else {
            // swap current and held
            Tetromino temp = pecaAtual;
            pecaAtual = heldPiece;
            heldPiece = temp;
            // reset position of current
            pecaAtual.setPosicao(new Posicao(4, 0));
        }
    }

    public Tetromino getHeldPiece() { return heldPiece; }

    public Jogador getJogador() { return jogador; }
    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public SistemaPontuacao getSistemaPontuacao() { return sistemaPontuacao; }
    public Tetromino getPecaAtual() { return pecaAtual; }
    public boolean isGameOver() { return gameOver; }
    public Queue<Tetromino> getProximasPecas() { return proximasPecas; }
    public List<TipoTetromino> getHistoricoPecas() { return new ArrayList<>(historicoPecas); }
}
