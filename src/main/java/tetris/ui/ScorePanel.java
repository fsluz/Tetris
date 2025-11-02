package tetris.ui;

import javax.swing.*;
import java.awt.*;
import tetris.domain.SistemaPontuacao;

public class ScorePanel extends JPanel {
    private JLabelWithGlow lblPontuacao;
    private JLabelWithGlow lblNivel;
    private JLabelWithGlow lblTempo;
    private JLabelWithGlow lblTituloPontuacao;
    private JLabelWithGlow lblTituloNivel;
    private JLabelWithGlow lblTituloTempo;

    public ScorePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Tema Terminal Retro - Fundo preto
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Fonte monoespaçada estilo terminal - aumentada
        Font tituloFont = new Font("Courier New", Font.BOLD, 12);
        Font valorFont = new Font("Courier New", Font.BOLD, 26); // Aumentado de 18 para 26
        
        // Tema Terminal - Verde ultra brilhante com glow
        Color verdeTerminal = new Color(0, 255, 0);
        Color amareloTerminal = new Color(255, 255, 0);
        
        // Título Pontuação - JLabel customizado com glow
        JLabelWithGlow lblPontuacaoGlow = new JLabelWithGlow("0", verdeTerminal, valorFont);
        JLabelWithGlow lblNivelGlow = new JLabelWithGlow("1", amareloTerminal, valorFont);
        JLabelWithGlow lblTempoGlow = new JLabelWithGlow("00:00", amareloTerminal, valorFont);
        JLabelWithGlow lblTituloPontuacaoGlow = new JLabelWithGlow("PONTUAÇÃO", verdeTerminal, tituloFont);
        JLabelWithGlow lblTituloNivelGlow = new JLabelWithGlow("NÍVEL", verdeTerminal, tituloFont);
        JLabelWithGlow lblTituloTempoGlow = new JLabelWithGlow("TEMPO", verdeTerminal, tituloFont);
        
        lblTituloPontuacao = lblTituloPontuacaoGlow;
        lblPontuacao = lblPontuacaoGlow;
        lblTituloNivel = lblTituloNivelGlow;
        lblNivel = lblNivelGlow;
        lblTituloTempo = lblTituloTempoGlow;
        lblTempo = lblTempoGlow;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        add(lblTituloPontuacao, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        add(lblPontuacao, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        add(lblTituloNivel, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        add(lblNivel, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        add(lblTituloTempo, gbc);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(lblTempo, gbc);
        
        setPreferredSize(new Dimension(200, 220));
        setDoubleBuffered(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        // Fundo preto estilo terminal
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
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
        
        // Efeito vignette (escurecimento nas bordas)
        RadialGradientPaint vignette = new RadialGradientPaint(
            getWidth() / 2f, getHeight() / 2f, Math.max(getWidth(), getHeight()) / 2f,
            new float[]{0f, 1f},
            new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 100)}
        );
        g2d.setPaint(vignette);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public void atualizar(SistemaPontuacao sistema, String tempoFormatado) {
        lblPontuacao.setText(String.format("%,d", sistema.getPontos()));
        lblNivel.setText(String.valueOf(sistema.getNivel()));
        lblTempo.setText(tempoFormatado);
    }
    
    // JLabel customizado com efeito glow brilhante
    private static class JLabelWithGlow extends JLabel {
        private final Color glowColor;
        
        public JLabelWithGlow(String text, Color color, Font font) {
            super(text, JLabel.CENTER);
            this.glowColor = color;
            setFont(font);
            setForeground(color);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            
            String text = getText();
            FontMetrics fm = g2d.getFontMetrics(getFont());
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            
            // Múltiplas camadas para efeito glow intenso
            for (int i = 3; i >= 1; i--) {
                g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 80 / i));
                g2d.setFont(getFont());
                for (int dx = -i; dx <= i; dx++) {
                    for (int dy = -i; dy <= i; dy++) {
                        if (dx * dx + dy * dy <= i * i) {
                            g2d.drawString(text, x + dx, y + dy);
                        }
                    }
                }
            }
            
            // Texto principal ultra brilhante
            g2d.setColor(glowColor);
            g2d.drawString(text, x, y);
            
            g2d.dispose();
        }
    }
}
