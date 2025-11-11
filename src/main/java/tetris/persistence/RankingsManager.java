package tetris.persistence;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class RankingsManager {
    private final Path arquivo = Paths.get("rankings.txt");

    public RankingsManager() {
        try {
            if (!Files.exists(arquivo)) Files.createFile(arquivo);
        } catch (IOException ignored) {}
    }

    public boolean isDatabaseAvailable() {
        try (Connection c = ConexaoSQL.getConexao()) {
            return c != null && !c.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public java.util.List<String> loadRankings(int limit) {
        if (isDatabaseAvailable()) {
            List<String> out = new ArrayList<>();
            String sql = "SELECT j.nome, p.pontos, p.data_hora FROM Partida p JOIN Jogador j ON p.jogador_id = j.id ORDER BY p.pontos DESC";
            try (Connection con = ConexaoSQL.getConexao();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setMaxRows(limit);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String nome = rs.getString(1);
                    int pts = rs.getInt(2);
                    Timestamp ts = rs.getTimestamp(3);
                    String data = ts != null ? ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
                    out.add(String.format("%-18s %6d %s", nome, pts, data));
                }
                return out;
            } catch (Exception e) {
                // fallback para arquivo
            }
        }
        return loadFromFile(limit);
    }

    private List<String> loadFromFile(int limit) {
        List<String> out = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(arquivo);
            for (String l : lines) {
                if (out.size() >= limit) break;
                String[] parts = l.split(";");
                String nome = parts.length > 0 ? parts[0] : "??";
                String pts = parts.length > 1 ? parts[1] : "0";
                String data = parts.length > 2 ? parts[2] : "";
                out.add(String.format("%-18s %6s %s", nome, pts, data));
            }
        } catch (IOException ignored) {}
        return out;
    }

    public void saveScore(String nome, int pontos) {
        if (isDatabaseAvailable()) {
            try (Connection con = ConexaoSQL.getConexao()) {
                long jogadorId = -1;
                try (PreparedStatement ps = con.prepareStatement("SELECT id FROM Jogador WHERE nome = ?")) {
                    ps.setString(1, nome);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) jogadorId = rs.getLong(1);
                }
                if (jogadorId == -1) {
                    try (PreparedStatement ins = con.prepareStatement("INSERT INTO Jogador(nome) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
                        ins.setString(1, nome);
                        ins.executeUpdate();
                        ResultSet gk = ins.getGeneratedKeys();
                        if (gk.next()) jogadorId = gk.getLong(1);
                    }
                }
                try (PreparedStatement ip = con.prepareStatement("INSERT INTO Partida(jogador_id,pontos,data_hora) VALUES(?,?,?)")) {
                    ip.setLong(1, jogadorId);
                    ip.setInt(2, pontos);
                    ip.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    ip.executeUpdate();
                }
                return;
            } catch (Exception e) {
                // se falhar, fallback para arquivo
            }
        }
        saveToFile(nome, pontos);
    }

    public void saveToFile(String nome, int pontos) {
        String linha = String.format("%s;%d;%s", nome, pontos, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        try {
            Files.write(arquivo, Collections.singletonList(linha), StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }
}
