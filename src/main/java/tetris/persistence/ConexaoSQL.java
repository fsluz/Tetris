package tetris.persistence;

import java.sql.*;

public class ConexaoSQL {
    private static final String URL = 
        "jdbc:sqlserver://localhost:1433;databaseName=tetris;encrypt=true;trustServerCertificate=true";
    private static final String USUARIO = "sa";
    private static final String SENHA = "12345678"; // troque pela sua senha
    
    private static Connection conexao;

    public static Connection getConexao() throws SQLException {
        try {
            if (conexao == null || conexao.isClosed()) {
                System.out.println("Conectando ao SQL Server...");
                conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                System.out.println("Conectado com sucesso!");
            }
            return conexao;
        } catch (SQLException e) {
            System.err.println("Erro de conexão: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void testarConexao() {
        try {
            Connection conn = getConexao();
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT DB_NAME()");
                if (rs.next()) {
                    System.out.println("Conectado ao banco: " + rs.getString(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Teste falhou: " + e.getMessage());
        }
    }
}