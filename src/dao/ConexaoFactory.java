package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoFactory {

    // Se quiser usar um serviço como Turso (libsql), defina as variáveis de ambiente:
    // - LIBSQL_URL (libsql://crud-sniperalex.aws-us-west-2.turso.io)
    // - LIBSQL_TOKEN (eyJhbGciOiJFZERTQSIsInR5cCI6IkpXVCJ9.eyJhIjoicnciLCJpYXQiOjE3NjM5MzYwNDgsImlkIjoiYmRlOTdmZWYtZDk4Yi00OWUzLTkwNmYtNWI3N2MzM2FiOGNmIiwicmlkIjoiMTNjZGJlMDYtZTA1Yi00YTA0LWFmY2QtZGNjMTkwMDkyMDQ4In0.Biq2bhke5KAJnjZw97VDQbd9IqYUXGWBHeuGnGJMGI8QrBYJ9wcNG2dLVGVAdXUyne3IoJyBrbamErhM6gbGCA)
    // Caso contrário, o fallback é usar SQLite local via SQLITE_PATH

    private static final String DEFAULT_SQLITE_PATH = "hotel_db.sqlite";

    public static Connection getConnection() {
        try {
            // 1) Verifica se há libsql (Turso) configurado
            String libsqlUrl = System.getenv("LIBSQL_URL");
            if (libsqlUrl != null && !libsqlUrl.isBlank()) {
                // Converte libsql://host... para jdbc:libsql://host...
                String jdbcUrl = libsqlUrl.startsWith("jdbc:") ? libsqlUrl : libsqlUrl.replaceFirst("^libsql:", "jdbc:libsql:");

                // Opcional: se for necessário carregar driver específico, informe via LIBSQL_DRIVER
                String libDriver = System.getenv("LIBSQL_DRIVER");
                if (libDriver != null && !libDriver.isBlank()) {
                    Class.forName(libDriver);
                }

                java.util.Properties props = new java.util.Properties();
                String token = System.getenv("LIBSQL_TOKEN");
                if (token != null && !token.isBlank()) {
                    // A chave exata esperada pelo driver pode variar; muitos drivers aceitam uma propriedade 'authToken' ou específica.
                    // Para o driver libsql use a propriedade apropriada (ex.: 'libsql.token' ou 'authToken') conforme a documentação do driver.
                    props.setProperty("authToken", token);
                }

                return DriverManager.getConnection(jdbcUrl, props);
            }

            // 2) Fallback: usa SQLite local (arquivo). Path pode ser configurado via SQLITE_PATH
            String dbPath = System.getenv().getOrDefault("SQLITE_PATH", DEFAULT_SQLITE_PATH);
            String url = "jdbc:sqlite:" + dbPath;

            // Carrega driver SQLite (Xerial)
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(url);

            // Melhora concorrência local com WAL
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA journal_mode = WAL;");
                stmt.execute("PRAGMA synchronous = NORMAL;");
            } catch (SQLException ex) {
                // não impede a conexão, apenas loga
                ex.printStackTrace();
            }

            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Erro ao conectar no banco: " + e.getMessage(), e);
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