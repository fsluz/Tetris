package tetris.ui;

import javax.swing.*;
import java.awt.*;
import tetris.domain.Jogador;
import tetris.domain.Partida;
import tetris.persistence.RankingsManager;

public class MenuPrincipal extends JFrame {
    private final JTextField txtNome = new JTextField(18);
    private final RankingsManager rankings = new RankingsManager();

    public MenuPrincipal() {
        super("TETRIS - Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(12, 12, 12));

        JLabel titulo = new JLabel("T E T R I S");
        titulo.setForeground(new Color(0x66FFCC));
        titulo.setFont(new Font("Monospaced", Font.BOLD, 36));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.gridx = 0; c.gridy = 0;
        JLabel lbl = new JLabel("Nome:");
        lbl.setForeground(new Color(0x99FFCC));
        centro.add(lbl, c);

        c.gridx = 1;
        txtNome.setBackground(new Color(0x111111));
        txtNome.setForeground(new Color(0xCCFFEE));
        txtNome.setBorder(BorderFactory.createLineBorder(new Color(0x33AA88)));
        centro.add(txtNome, c);

        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        JPanel botoes = new JPanel();
        botoes.setOpaque(false);
        JButton btnJogar = new JButton("Jogar");
        JButton btnRank = new JButton("Rankings");
        styleButton(btnJogar);
        styleButton(btnRank);
        botoes.add(btnJogar);
        botoes.add(btnRank);
        centro.add(botoes, c);

        add(centro, BorderLayout.CENTER);

        btnJogar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe um nome antes de jogar.", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Jogador j = new Jogador(nome);
            Partida p = new Partida(j);
            try {
                TelaPrincipal tela = new TelaPrincipal(p);
                tela.iniciar();
                this.dispose();
            } catch (Throwable ex) {
                JOptionPane.showMessageDialog(this, "Não foi possível iniciar a interface principal:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRank.addActionListener(e -> {
            RankingsDialog dlg = new RankingsDialog(this, rankings);
            dlg.setVisible(true);
        });
    }

    private void styleButton(JButton b) {
        b.setBackground(new Color(0x224444));
        b.setForeground(new Color(0xEEFFEE));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(0x44CCAA)));
    }
}
