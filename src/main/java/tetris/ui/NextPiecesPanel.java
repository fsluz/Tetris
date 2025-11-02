package tetris.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Queue;
import tetris.domain.*;

public class NextPiecesPanel extends JPanel {
    private static final int TAM_CELULA = 20;
    private Partida partida;
    // Cache de recursos para evitar criação a cada frame
    private final Font fontHistorico = new Font("Courier New", Font.BOLD, 11);
    private static final Color COR_BORDA_PECA = new Color(0, 255, 0);
    
    public NextPiecesPanel(Partida partida) {
        this.partida = partida;
        setPreferredSize(new Dimension(200, 600));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 0), 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                "PRÓXIMAS PEÇAS",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("Courier New", Font.BOLD, 11),
                new Color(0, 255, 0)
            )
        ));
    }
    
    public void atualizar(Partida partida) {
        this.partida = partida;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Fundo preto terminal
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Scanlines CRT mais visíveis (como na segunda foto)
        g2d.setColor(new Color(0, 40, 0, 80));
        for (int y = 0; y < getHeight(); y += 2) {
            g2d.drawLine(0, y, getWidth(), y);
        }
        // Scanlines duplas
        g2d.setColor(new Color(0, 60, 0, 40));
        for (int y = 1; y < getHeight(); y += 2) {
            g2d.drawLine(0, y, getWidth(), y);
        }
        
        // Vignette
        RadialGradientPaint vignette = new RadialGradientPaint(
            getWidth() / 2f, getHeight() / 2f, Math.max(getWidth(), getHeight()) / 2f,
            new float[]{0f, 1f},
            new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 100)}
        );
        g2d.setPaint(vignette);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        if (partida == null) return;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desenha as próximas peças
        Queue<Tetromino> proximasPecas = partida.getProximasPecas();
        if (proximasPecas != null) {
            int yOffset = 40;
            int contador = 0;
            
            for (Tetromino peca : proximasPecas) {
                if (contador >= 5) break; // Mostra apenas as 5 primeiras
                
                drawTetromino(g2d, peca, 20, yOffset);
                yOffset += 80;
                contador++;
            }
        }
        
        // Desenha o histórico de peças
        List<TipoTetromino> historico = partida.getHistoricoPecas();
        if (historico != null && !historico.isEmpty()) {
            g2d.setFont(fontHistorico);
            g2d.setColor(new Color(0, 255, 0));
            
            int histY = 480;
            g2d.drawString("Histórico:", 10, histY);
            histY += 20;
            
            // Mostra as últimas 10 peças do histórico
            int inicio = Math.max(0, historico.size() - 10);
            for (int i = inicio; i < historico.size(); i++) {
                g2d.drawString((i + 1) + ". " + historico.get(i).name(), 15, histY);
                histY += 18;
                if (histY > getHeight() - 20) break;
            }
        }
    }
    
    private void drawTetromino(Graphics2D g, Tetromino tetromino, int x, int y) {
        if (tetromino == null) return;
        
        boolean[][] forma = tetromino.getForma();
        Color corPeca = tetromino.getCor(); // Usa a cor original da peça
        
        for (int i = 0; i < forma.length; i++) {
            for (int j = 0; j < forma[i].length; j++) {
                if (forma[i][j]) {
                    int px = x + j * TAM_CELULA;
                    int py = y + i * TAM_CELULA;
                    desenharBlocoTerminal(g, px, py, TAM_CELULA, corPeca, corPeca);
                }
            }
        }
    }
    
    private void desenharBlocoTerminal(Graphics2D g, int x, int y, int tamanho, Color corClara, Color corEscura) {
        // Estilo ASCII puro - apenas o caractere [] como na foto
        // Sem preenchimento, sem bordas complexas
        
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
        // Base com gradiente
        GradientPaint gradiente = new GradientPaint(
            x, y, corClara,
            x, y + tamanho, corEscura
        );
        g.setPaint(gradiente);
        g.fillRect(x, y, tamanho, tamanho);
        
        // Efeito diamante/piramidal
        int meio = tamanho / 2;
        g.setColor(new Color(corClara.getRed(), corClara.getGreen(), corClara.getBlue(), 150));
        g.fillPolygon(
            new int[]{x, x + meio, x},
            new int[]{y, y + meio, y},
            3
        );
        
        // Brilho radial no canto superior esquerdo
        int brilhoSize = tamanho / 3;
        RadialGradientPaint brilhoGrad = new RadialGradientPaint(
            x + 2, y + 2, brilhoSize,
            new float[]{0f, 1f},
            new Color[]{new Color(255, 255, 255, 180), new Color(255, 255, 255, 0)}
        );
        g.setPaint(brilhoGrad);
        g.fillOval(x, y, brilhoSize + 1, brilhoSize + 1);
        
        // Bordas
        g.setColor(new Color(0, 0, 0, 120));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRect(x, y, tamanho - 1, tamanho - 1);
        
        // Linhas diagonais para profundidade
        g.setColor(new Color(0, 0, 0, 50));
        g.drawLine(x, y, x + tamanho, y + tamanho);
        
        // Borda superior clara
        g.setColor(new Color(255, 255, 255, 90));
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

