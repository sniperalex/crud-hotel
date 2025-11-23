package dao;

import core.IDAO;
import dominio.EntidadeDominio;
import dominio.Hospede;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HospedeDAO implements IDAO {

    @Override
    public void salvar(EntidadeDominio entidade) {
        Hospede hospede = (Hospede) entidade;
        String sql = "INSERT INTO hospedes (nome, cpf, email, telefone, data_cadastro) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hospede.getNome());
            stmt.setString(2, hospede.getCpf());
            stmt.setString(3, hospede.getEmail());
            stmt.setString(4, hospede.getTelefone());
            stmt.setLong(5, System.currentTimeMillis()); // Data atual
            
            stmt.executeUpdate();
            System.out.println("DAO: Hóspede salvo com sucesso!");
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar hóspede: " + e.getMessage());
        }
    }

    @Override
    public void alterar(EntidadeDominio entidade) {
        // Implementar depois
    }

    @Override
    public void excluir(EntidadeDominio entidade) {
        // Implementar depois
    }

    @Override
    public List<EntidadeDominio> consultar(EntidadeDominio entidade) {
        List<EntidadeDominio> lista = new ArrayList<>();
        String sql = "SELECT * FROM hospedes";

        try (Connection conn = ConexaoFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Hospede h = new Hospede();
                h.setId(rs.getInt("id"));
                h.setNome(rs.getString("nome"));
                h.setCpf(rs.getString("cpf"));
                h.setEmail(rs.getString("email"));
                h.setTelefone(rs.getString("telefone"));
                lista.add(h);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}