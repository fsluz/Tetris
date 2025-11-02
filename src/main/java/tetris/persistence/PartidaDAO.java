package tetris.persistence;

import tetris.domain.Partida;
import java.sql.*;
import java.io.*;

public class PartidaDAO {
    public void salvar(Partida partida) {
        String sql = "INSERT INTO Partida (jogador_id, pontuacao, tempo_ms, dados) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoSQL.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(partida);
            }
            // Use string for UUID to be compatible with SQL Server driver
            ps.setString(1, partida.getJogador().getId().toString());
            // pontuação e tempo em ms
            int pontos = partida.getSistemaPontuacao().getPontos();
            long tempoMs = partida.getTempoDecorrido();
            ps.setInt(2, pontos);
            ps.setLong(3, tempoMs);
            ps.setBytes(4, baos.toByteArray());
            int updated = ps.executeUpdate();
            System.out.println("[DB] Partida inserida, linhas afetadas: " + updated + " (pontos=" + pontos + ", tempo_ms=" + tempoMs + ")");
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao salvar partida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void criarTabelaSeNaoExistir() {
        // Try to create table if it doesn't exist using new schema
        String createSql = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Partida' and xtype='U') "
                   + "CREATE TABLE Partida (" 
                   + "id INT IDENTITY(1,1) PRIMARY KEY, "
                   + "jogador_id NVARCHAR(36) NOT NULL, "
                   + "pontuacao INT NOT NULL, "
                   + "tempo_ms BIGINT NOT NULL, "
                   + "dados VARBINARY(MAX) NOT NULL, "
                   + "data_criacao DATETIME DEFAULT GETDATE()" 
                   + ");";

        // If table already exists but missing columns, add them
        String addPontuacao = "IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'pontuacao' AND Object_ID = Object_ID(N'Partida')) "
                            + "ALTER TABLE Partida ADD pontuacao INT NOT NULL DEFAULT 0;";
        String addTempoMs = "IF NOT EXISTS (SELECT * FROM sys.columns WHERE Name = N'tempo_ms' AND Object_ID = Object_ID(N'Partida')) "
                          + "ALTER TABLE Partida ADD tempo_ms BIGINT NOT NULL DEFAULT 0;";
        try (Connection conn = ConexaoSQL.getConexao(); Statement st = conn.createStatement()) {
            st.execute(createSql);
            st.execute(addPontuacao);
            st.execute(addTempoMs);
        } catch (SQLException e) {
            System.err.println("Erro ao criar/atualizar tabela Partida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void listarUltimas(int limite) {
        String sql = "SELECT TOP (" + limite + ") id, jogador_id, pontuacao, tempo_ms, DATALENGTH(dados) AS tamanho, data_criacao FROM Partida ORDER BY data_criacao DESC";
        try (Connection conn = ConexaoSQL.getConexao(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("[DB] Últimas partidas:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String jogadorId = rs.getString("jogador_id");
                int pontuacao = rs.getInt("pontuacao");
                long tempoMs = rs.getLong("tempo_ms");
                int tamanho = rs.getInt("tamanho");
                java.sql.Timestamp ts = rs.getTimestamp("data_criacao");
                System.out.println(String.format("  id=%d jogador_id=%s pontuacao=%d tempo_ms=%d tamanho=%d data=%s", id, jogadorId, pontuacao, tempoMs, tamanho, ts));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar partidas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Partida carregarUltima(java.util.UUID jogadorId) {
        String sql = "SELECT TOP 1 dados FROM Partida WHERE jogador_id = ? ORDER BY data_criacao DESC";
        try (Connection conn = ConexaoSQL.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, jogadorId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    byte[] blob = rs.getBytes("dados");
                    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(blob))) {
                        return (Partida) ois.readObject();
                    }
                }
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
