package com.apostassa.infra.jogo.jogojuncaorole;

import com.apostassa.dominio.jogo.jogojuncaorole.AdicionarJogoJuncaoRoleException;
import com.apostassa.dominio.jogo.jogojuncaorole.JogoJuncaoRole;
import com.apostassa.dominio.jogo.jogojuncaorole.RemoverJogoJuncaoRoleException;
import com.apostassa.dominio.jogo.jogojuncaorole.RepositorioDeJogoJuncaoRoleAdmin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeJogoJuncaoRoleAdminComJdbcPostgres implements RepositorioDeJogoJuncaoRoleAdmin {

    private Connection connection;

    public RepositorioDeJogoJuncaoRoleAdminComJdbcPostgres(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void adicionarRoleAUmJogo(JogoJuncaoRole jogoJuncaoRole) throws AdicionarJogoJuncaoRoleException {
        String sql = """
                INSERT INTO jogos_juncao_roles 
                (jogo_id, role_jogo_id, data_e_hora_atribuicao, atribuido_por_usuario_id)
                VALUES (?, ?, ?, ?)""";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, jogoJuncaoRole.getJogoId());
            ps.setString(2, jogoJuncaoRole.getRoleJogoId());
            ps.setTimestamp(3, Timestamp.valueOf(jogoJuncaoRole.getDataEHoraAtribuicao()));
            ps.setString(4, jogoJuncaoRole.getAtribuidoPor());
            ps.execute();
        } catch (SQLException e) {
            throw new AdicionarJogoJuncaoRoleException("Erro ao cadastrar role para o jogo" + e.getMessage());
        }
    }

    @Override
    public void removerRoleDeUmJogo(String roleJogoId, String jogoId) throws RemoverJogoJuncaoRoleException {
        String sql = "DELETE FROM jogos_juncao_roles WHERE role_jogo_id = ? AND jogo_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roleJogoId);
            ps.setString(2, jogoId);
            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new RemoverJogoJuncaoRoleException("Não foi possível remover essa role desse jogo");
            }
        } catch (SQLException e) {
            String mensagem = "Erro ao deletar role de jogo";
            throw new RemoverJogoJuncaoRoleException(mensagem + e.getMessage());
        }
    }

    @Override
    public List<String> pegarTodosJogoJuncaoRolePorJogoId(String jogoId) {
        String sql = "SELECT role_jogo_id FROM jogos_juncao_roles WHERE jogo_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, jogoId);
            List<String> listaJogoJuncaoRoles = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String roleJogoId = rs.getString("role_jogo_id");

                    listaJogoJuncaoRoles.add(roleJogoId);
                }
                return listaJogoJuncaoRoles;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
