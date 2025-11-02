package tetris.ui;

import javax.swing.*;
import java.awt.*;
import tetris.domain.Partida;
import tetris.engine.*;
import tetris.domain.Tetromino;
import tetris.domain.Tabuleiro;

public class TelaPrincipal extends JFrame {
    private final Partida partida;
    private final GamePanel gamePanel;
    private final ScorePanel scorePanel;
    private final NextPiecesPanel nextPiecesPanel;
    private final GameEngine engine;
    private Timer timerUI;

    public TelaPrincipal(Partida partida) {
        super("TETRIS");
        this.partida = partida;
        engine = new GameEngine(partida);
        gamePanel = new GamePanel(partida.getTabuleiro(), partida.getPecaAtual());
        scorePanel = new ScorePanel();
        nextPiecesPanel = new NextPiecesPanel(partida);
        
        // UI Terminal Retro
        setLayout(new BorderLayout(5, 5));
        setBackground(Color.BLACK);
        
        // Adiciona painéis com espaçamento
        add(nextPiecesPanel, BorderLayout.WEST);
        add(gamePanel, BorderLayout.CENTER);
        add(scorePanel, BorderLayout.EAST);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setFocusable(true);
        requestFocus();
        addKeyListener(new InputHandler(partida));
        
        timerUI = new Timer(50, e -> atualizarTela());
    }

    public void iniciar() {
        engine.iniciar();
        timerUI.start();
        setVisible(true);
    }

    private void atualizarTela() {
        if (partida.isGameOver()) {
            timerUI.stop();
            engine.parar();
            JOptionPane.showMessageDialog(this, "Game Over!\nPontuação final: " + partida.getSistemaPontuacao().getPontos());
            dispose();
            return;
        }
        
        gamePanel.atualizar(partida.getTabuleiro(), partida.getPecaAtual());
        scorePanel.atualizar(partida.getSistemaPontuacao(), partida.getTempoFormatado());
        nextPiecesPanel.atualizar(partida);
        
        // Ajusta velocidade baseado no nível (mais rápido - começa com 200ms e aumenta velocidade)
        ThreadLoop threadLoop = engine.getThreadLoop();
        if (threadLoop != null) {
            int novoDelay = Math.max(30, 250 - (partida.getSistemaPontuacao().getNivel() * 20));
            threadLoop.setDelay(novoDelay);
        }
    }
}
