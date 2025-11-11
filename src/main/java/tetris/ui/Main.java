package tetris.ui;

import javax.swing.SwingUtilities;

/**
 * Entrypoint: abre o menu principal (nome, jogar, rankings).
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
        });
    }
}
