package dao;

import core.IDAO;
import dominio.EntidadeDominio;
import dominio.Quarto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuartoDAO implements IDAO {

    @Override
    public void salvar(EntidadeDominio entidade) {
        Quarto q = (Quarto) entidade;
        String sql = "INSERT INTO quartos (numero, tipo, cap_adultos, cap_criancas, preco_base, data_cadastro) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, q.getNumero());
            stmt.setString(2, q.getTipo());
            stmt.setInt(3, q.getCapAdultos());
            stmt.setInt(4, q.getCapCriancas());
            stmt.setDouble(5, q.getPrecoBase());
            stmt.setLong(6, System.currentTimeMillis());
            
            stmt.executeUpdate();
            System.out.println("DAO: Quarto salvo com sucesso!");
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar quarto: " + e.getMessage());
        }
    }

    @Override
    public void alterar(EntidadeDominio entidade) { }

    @Override
    public void excluir(EntidadeDominio entidade) { }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) {
        List<EntidadeDominio> lista = new ArrayList<>();
        String sql = "SELECT * FROM quartos ORDER BY numero ASC";

        try (Connection conn = ConexaoFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Quarto q = new Quarto();
                q.setId(rs.getInt("id"));
                q.setNumero(rs.getInt("numero"));
                q.setTipo(rs.getString("tipo"));
                q.setCapAdultos(rs.getInt("cap_adultos"));
                q.setCapCriancas(rs.getInt("cap_criancas"));
                q.setPrecoBase(rs.getDouble("preco_base"));
                lista.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}