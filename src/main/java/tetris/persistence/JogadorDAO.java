package tetris.persistence;

import tetris.domain.Jogador;
import java.sql.*;
import java.util.*;

public class JogadorDAO {
    public void criarTabelaSeNaoExistir() {
        String sql = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Jogador' and xtype='U') CREATE TABLE Jogador (id UNIQUEIDENTIFIER PRIMARY KEY, nome NVARCHAR(100) NOT NULL);";
        try (Connection conn = ConexaoSQL.getConexao(); Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    public void inserir(Jogador jogador) {
        String sql = "INSERT INTO Jogador (id, nome) VALUES (?, ?)";
        try (Connection conn = ConexaoSQL.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            // Use string representation for UUID to ensure proper handling by the JDBC driver
            ps.setString(1, jogador.getId().toString());
            ps.setString(2, jogador.getNome());
            int updated = ps.executeUpdate();
            System.out.println("[DB] Jogador inserido, linhas afetadas: " + updated);
        } catch (SQLException e) {
            System.err.println("Erro ao inserir jogador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Jogador> listarTodos() {
        List<Jogador> jogadores = new ArrayList<>();
        String sql = "SELECT id, nome FROM Jogador";
        try (Connection conn = ConexaoSQL.getConexao(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                java.util.UUID id = (java.util.UUID) rs.getObject("id");
                String nome = rs.getString("nome");
                Jogador j = new Jogador(nome);
                // Reflection of id not set on constructor; in production you'd map properly
                jogadores.add(j);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar: " + e.getMessage());
        }
        return jogadores;
    }
}
