package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoFactory {

    private static final String URL = "jdbc:sqlite:hotel_db.sqlite";

    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Erro ao conectar no SQLite: " + e.getMessage());
        }
    }

    public static void inicializarBanco() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Tabela Hóspedes (Já existia)
            String sqlHospedes = "CREATE TABLE IF NOT EXISTS hospedes (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "nome TEXT NOT NULL," +
                     "cpf TEXT NOT NULL UNIQUE," +
                     "email TEXT," +
                     "telefone TEXT," +
                     "data_cadastro LONG" +
                     ");";
            stmt.execute(sqlHospedes);

            // Tabela Quartos (NOVA)
            String sqlQuartos = "CREATE TABLE IF NOT EXISTS quartos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "numero INTEGER NOT NULL UNIQUE," +
                    "tipo TEXT NOT NULL," +
                    "cap_adultos INTEGER," +
                    "cap_criancas INTEGER," +
                    "preco_base REAL," +
                    "data_cadastro LONG" +
                    ");";
            stmt.execute(sqlQuartos);

            System.out.println("Banco de dados (Hóspedes e Quartos) inicializado.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}