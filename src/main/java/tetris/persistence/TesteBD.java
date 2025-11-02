package tetris.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TesteBD {
    public static void main(String[] args) {
        System.out.println("Testando conexão...");
        ConexaoSQL.testarConexao();

        // Ensure tables exist
        JogadorDAO jdao = new JogadorDAO();
        PartidaDAO pdao = new PartidaDAO();
        jdao.criarTabelaSeNaoExistir();
        pdao.criarTabelaSeNaoExistir();

        // List recent partidas
        pdao.listarUltimas(10);
    }
}