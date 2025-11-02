package tetris.io;

import tetris.domain.Partida;
import tetris.persistence.JogadorDAO;
import tetris.persistence.PartidaDAO;
import java.io.*;

public class SaveManager {
    private static final String SAVE_FILE = "partida_salva.dat";

    public static void salvar(Partida partida) {
        // Save to local file (existing behavior)
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(partida);
            System.out.println("[SAVE] Sucesso em " + SAVE_FILE);
        } catch (IOException e) {
            System.err.println("[SAVE] Erro ao gravar arquivo: " + e.getMessage());
        }

        // Also persist to database
        try {
            JogadorDAO jogadorDAO = new JogadorDAO();
            PartidaDAO partidaDAO = new PartidaDAO();

            // Ensure tables exist
            jogadorDAO.criarTabelaSeNaoExistir();
            partidaDAO.criarTabelaSeNaoExistir();

            // Ensure jogador is present (inserir will quietly fail if duplicate)
            jogadorDAO.inserir(partida.getJogador());

            // Save partida blob
            partidaDAO.salvar(partida);
            System.out.println("[SAVE] Salvo no DB");
        } catch (Exception e) {
            System.err.println("[SAVE] Erro DB: " + e.getMessage());
        }
    }

    public static Partida carregar() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("[LOAD] Nenhum save");
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Partida p = (Partida) ois.readObject();
            System.out.println("[LOAD] Carregado");
            return p;
        } catch (Exception e) {
            System.err.println("[LOAD] Erro: " + e.getMessage());
            return null;
        }
    }
}
