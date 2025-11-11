package tetris.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import tetris.persistence.RankingsManager;

public class RankingsDialog extends JDialog {
    public RankingsDialog(Frame owner, RankingsManager manager) {
        super(owner, "Rankings", true);
        setSize(380, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(new Color(8,8,8));
        area.setForeground(new Color(180,255,220));
        add(new JScrollPane(area), BorderLayout.CENTER);

        java.util.List<String> lines = manager.loadRankings(50);
        if (lines.isEmpty()) {
            area.setText("Nenhum ranking encontrado.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-4s %-18s %6s %s\n", "#", "NOME", "PONTOS", "DATA"));
            sb.append("---------------------------------------------------\n");
            int i = 1;
            for (String l : lines) {
                sb.append(String.format("%-4d %s\n", i++, l));
            }
            area.setText(sb.toString());
        }

        JButton fechar = new JButton("Fechar");
        fechar.addActionListener(e -> dispose());
        JPanel p = new JPanel();
        p.add(fechar);
        add(p, BorderLayout.SOUTH);
    }
}
