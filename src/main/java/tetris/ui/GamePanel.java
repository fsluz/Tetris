package tetris.ui;

import javax.swing.*;
import java.awt.*;
import tetris.domain.*;

public class GamePanel extends JPanel {
    private static final int TAM_CELULA = 30;
    private Tabuleiro tabuleiro;
    private Tetromino pecaAtual;

    public GamePanel(Tabuleiro tabuleiro, Tetromino pecaAtual) {
        this.tabuleiro = tabuleiro;
        this.pecaAtual = pecaAtual;
        setPreferredSize(new Dimension(Tabuleiro.LARGURA * TAM_CELULA, Tabuleiro.ALTURA * TAM_CELULA));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        // Sem borda - estilo ASCII puro
    }

    public void atualizar(Tabuleiro tabuleiro, Tetromino pecaAtual) {
        this.tabuleiro = tabuleiro;
        this.pecaAtual = pecaAtual;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        // Fundo preto
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // No grid background per user request — keep clean background (scanlines + vignette below provide retro look)
        
        boolean[][] grid = tabuleiro.getGrid();
        Color[][] coresGrid = tabuleiro.getCoresGrid();
        
        // Desenha blocos fixados no tabuleiro com suas cores originais
        for (int y = 0; y < Tabuleiro.ALTURA; y++) {
            for (int x = 0; x < Tabuleiro.LARGURA; x++) {
                if (grid[y][x]) {
                    int px = x * TAM_CELULA;
                    int py = y * TAM_CELULA;
                    Color corBloco = coresGrid[y][x];
                    if (corBloco != null) {
                        desenharBlocoTerminal(g2d, px, py, TAM_CELULA, corBloco, corBloco);
                    }
                }
            }
        }
        
        // Desenha peça atual com sua cor original
        if (pecaAtual != null) {
            boolean[][] forma = pecaAtual.getForma();
            Color corPeca = pecaAtual.getCor();
            
            for (int i = 0; i < forma.length; i++) {
                for (int j = 0; j < forma[i].length; j++) {
                    if (forma[i][j]) {
                        int px = (pecaAtual.getPosicao().getX() + j) * TAM_CELULA;
                        int py = (pecaAtual.getPosicao().getY() + i) * TAM_CELULA;
                        desenharBlocoTerminal(g2d, px, py, TAM_CELULA, corPeca, corPeca);
                    }
                }
            }
        }
        
        // Sem grade visível - estilo ASCII puro
        
        // Efeito scanlines CRT mais visível (como na segunda foto)
        g2d.setColor(new Color(0, 40, 0, 80));
        for (int y = 0; y < getHeight(); y += 2) {
            g2d.drawLine(0, y, getWidth(), y);
        }
        // Scanlines duplas para efeito mais intenso
        g2d.setColor(new Color(0, 60, 0, 40));
        for (int y = 1; y < getHeight(); y += 2) {
            g2d.drawLine(0, y, getWidth(), y);
        }
        
        // Efeito vignette CRT
        RadialGradientPaint vignette = new RadialGradientPaint(
            getWidth() / 2f, getHeight() / 2f, Math.max(getWidth(), getHeight()) / 1.5f,
            new float[]{0f, 1f},
            new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 120)}
        );
        g2d.setPaint(vignette);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    private void desenharBlocoTerminal(Graphics2D g, int x, int y, int tamanho, Color corClara, Color corEscura) {
        // Estilo ASCII puro - apenas o caractere [] como na foto
        // Sem preenchimento, sem bordas complexas - apenas o texto
        
        // Caractere ASCII [] simples e direto
        g.setColor(corClara);
        g.setFont(new Font("Courier New", Font.BOLD, tamanho - 2));
        FontMetrics fm = g.getFontMetrics();
        String blockChar = "[]";
        int charWidth = fm.stringWidth(blockChar);
        int charHeight = fm.getHeight();
        
        // Desenha o caractere centralizado
        int textX = x + (tamanho - charWidth) / 2;
        int textY = y + (tamanho + charHeight) / 2 - fm.getDescent();
        
        g.drawString(blockChar, textX, textY);
    }
    
    private void desenharBloco3D(Graphics2D g, int x, int y, int tamanho, Color corClara, Color corEscura) {
        // Base com gradiente do topo (claro) para baixo (escuro)
        GradientPaint gradiente = new GradientPaint(
            x, y, corClara,
            x, y + tamanho, corEscura
        );
        
        g.setPaint(gradiente);
        g.fillRect(x, y, tamanho, tamanho);
        
        // Efeito diamante/piramidal - linhas diagonais que criam 4 triângulos
        int meio = tamanho / 2;
        Color corMeio = new Color(
            (corClara.getRed() + corEscura.getRed()) / 2,
            (corClara.getGreen() + corEscura.getGreen()) / 2,
            (corClara.getBlue() + corEscura.getBlue()) / 2
        );
        
        // Triângulo superior esquerdo (mais claro)
        g.setColor(new Color(corClara.getRed(), corClara.getGreen(), corClara.getBlue(), 180));
        g.fillPolygon(
            new int[]{x, x + meio, x},
            new int[]{y, y + meio, y},
            3
        );
        
        // Triângulo inferior direito (mais escuro)
        g.setColor(new Color(corEscura.getRed(), corEscura.getGreen(), corEscura.getBlue(), 180));
        g.fillPolygon(
            new int[]{x + meio, x + tamanho, x + tamanho},
            new int[]{y + meio, y, y + tamanho},
            3
        );
        
        // Brilho intenso no canto superior esquerdo (como na foto)
        int brilhoSize = tamanho / 3;
        RadialGradientPaint brilhoGrad = new RadialGradientPaint(
            x + 3, y + 3, brilhoSize,
            new float[]{0f, 1f},
            new Color[]{new Color(255, 255, 255, 200), new Color(255, 255, 255, 0)}
        );
        g.setPaint(brilhoGrad);
        g.fillOval(x + 1, y + 1, brilhoSize + 2, brilhoSize + 2);
        
        // Bordas escuras para definição
        g.setColor(new Color(0, 0, 0, 150));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRect(x, y, tamanho - 1, tamanho - 1);
        
        // Linha diagonal central (cria efeito de profundidade)
        g.setColor(new Color(0, 0, 0, 60));
        g.drawLine(x, y, x + tamanho, y + tamanho);
        g.drawLine(x, y + tamanho, x + tamanho, y);
        
        // Borda superior e esquerda clara (realce 3D)
        g.setColor(new Color(255, 255, 255, 100));
        g.setStroke(new BasicStroke(1.0f));
        g.drawLine(x, y, x + tamanho - 1, y);
        g.drawLine(x, y, x, y + tamanho - 1);
    }
    
    private Color obterCorClara(Color corBase) {
        int r = Math.min(255, corBase.getRed() + 60);
        int g = Math.min(255, corBase.getGreen() + 60);
        int b = Math.min(255, corBase.getBlue() + 60);
        return new Color(r, g, b);
    }
    
    private Color obterCorEscura(Color corBase) {
        int r = Math.max(0, corBase.getRed() - 60);
        int g = Math.max(0, corBase.getGreen() - 60);
        int b = Math.max(0, corBase.getBlue() - 60);
        return new Color(r, g, b);
    }
}

