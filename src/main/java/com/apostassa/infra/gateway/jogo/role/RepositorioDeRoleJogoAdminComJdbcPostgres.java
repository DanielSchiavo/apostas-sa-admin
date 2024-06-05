package com.apostassa.infra.gateway.jogo.role;

import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.role.AlterarRoleJogoException;
import com.apostassa.dominio.jogo.role.DeletarRoleJogoException;
import com.apostassa.aplicacao.gateway.jogo.role.RepositorioDeRoleJogoAdmin;
import com.apostassa.dominio.jogo.role.RoleJogo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RepositorioDeRoleJogoAdminComJdbcPostgres implements RepositorioDeRoleJogoAdmin {

    private Connection connection;

    public RepositorioDeRoleJogoAdminComJdbcPostgres(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void cadastrarRoleJogo(RoleJogo role) throws ValidacaoException {
        String sql = """
                INSERT INTO jogos_roles 
                (id, nome, icone, descricao, data_e_hora_criacao, criado_por_usuario_id)
                VALUES (?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, role.getId().toString());
            ps.setString(2, role.getNome());
            ps.setString(3, role.getIcone());
            ps.setString(4, role.getDescricao());
            ps.setTimestamp(5, Timestamp.valueOf(role.getDataEHoraCriacao()));
            ps.setString(6, role.getCriadoPor().toString());
            ps.execute();
        } catch (SQLException e) {
            throw new ValidacaoException("Erro ao cadastrar role para o jogo" + e.getMessage());
        }
    }

    @Override
    public void alterarRoleJogo(RoleJogo roleJogo) throws AlterarRoleJogoException {
        String sql = gerarSqlAlterarRoleJogo(roleJogo);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            if (roleJogo.getNome() != null) {
                ps.setString(index, roleJogo.getNome());
                index++;
            }
            if (roleJogo.getIcone() != null) {
                ps.setString(index, roleJogo.getIcone());
                index++;
            }
            if (roleJogo.getAtivo() != null) {
                ps.setBoolean(index, roleJogo.getAtivo());
                index++;
            }
            ps.setTimestamp(index, Timestamp.valueOf(roleJogo.getDataEHoraUltimaAlteracao()));
            index++;

            ps.setString(index, roleJogo.getAlteradoPor().toString());
            index++;

            ps.setString(index, roleJogo.getId().toString());
            int executeUpdate = ps.executeUpdate();

            if (executeUpdate == 0) {
                throw new AlterarRoleJogoException("Não foi possivel alterar os dados dessa role jogo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String mensagem = "Erro ao alterar role";
            throw new AlterarRoleJogoException(mensagem);
        }
    }

    @Override
    public void deletarRoleJogo(String roleJogoId) throws DeletarRoleJogoException, ValidacaoException {
        String sql = "DELETE FROM jogos_roles WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roleJogoId);
            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new DeletarRoleJogoException("Não foi possível remover essa role");
            }
        } catch (SQLException e) {
            String mensagem = "Erro ao deletar role";
            throw new ValidacaoException(mensagem + e.getMessage());
        }
    }

    @Override
    public RoleJogo pegarRoleJogoPorId(String roleJogoId) throws ValidacaoException {
        String sql = "SELECT * FROM jogos_roles WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, roleJogoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String nome = rs.getString("nome");
                    String descricao = rs.getString("descricao");
                    String icone = rs.getString("icone");
                    Timestamp dataEHoraCriacaoTimestamp = rs.getTimestamp("data_e_hora_criacao");
                    String criadoPor = rs.getString("criado_por_usuario_id");
                    Timestamp dataEHoraUltimaAlteracaoTimestamp = rs.getTimestamp("data_e_hora_ultima_alteracao");
                    String alteradoPor = rs.getString("alterado_por_usuario_id");
                    boolean ativo = rs.getBoolean("ativo");

                    RoleJogo.RoleJogoBuilder roleJogoBuilder = RoleJogo.builder()
                            .id(UUID.fromString(id))
                            .nome(nome)
                            .icone(icone)
                            .descricao(descricao)
                            .dataEHoraCriacao(dataEHoraCriacaoTimestamp.toLocalDateTime())
                            .criadoPor(UUID.fromString(criadoPor))
                            .ativo(ativo);

                    if (dataEHoraUltimaAlteracaoTimestamp != null) {
                        roleJogoBuilder.dataEHoraUltimaAlteracao(dataEHoraUltimaAlteracaoTimestamp.toLocalDateTime());
                    }
                    if (alteradoPor != null) {
                        roleJogoBuilder.alteradoPor(UUID.fromString(alteradoPor));
                    }

                    return roleJogoBuilder.build();
                } else {
                    throw new ValidacaoException("Não existe role de jogo com esse ID!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RoleJogo> pegarTodasRolesJogo() {
        String sql = "SELECT * FROM jogos_roles";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<RoleJogo> listaRolesJogo = new ArrayList<>();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String nome = rs.getString("nome");
                    String descricao = rs.getString("descricao");
                    String icone = rs.getString("icone");
                    Timestamp dataEHoraCriacaoTimestamp = rs.getTimestamp("data_e_hora_criacao");
                    String criadoPor = rs.getString("criado_por_usuario_id");
                    Timestamp dataEHoraUltimaAlteracaoTimestamp = rs.getTimestamp("data_e_hora_ultima_alteracao");
                    String alteradoPor = rs.getString("alterado_por_usuario_id");
                    boolean ativo = rs.getBoolean("ativo");

                    RoleJogo.RoleJogoBuilder roleJogoBuilder = RoleJogo.builder()
                            .id(UUID.fromString(id))
                            .nome(nome)
                            .icone(icone)
                            .descricao(descricao)
                            .dataEHoraCriacao(dataEHoraCriacaoTimestamp.toLocalDateTime())
                            .criadoPor(UUID.fromString(criadoPor))
                            .ativo(ativo);

                    if (dataEHoraUltimaAlteracaoTimestamp != null) {
                        roleJogoBuilder.dataEHoraUltimaAlteracao(dataEHoraUltimaAlteracaoTimestamp.toLocalDateTime());
                    }
                    if (alteradoPor != null) {
                        roleJogoBuilder.alteradoPor(UUID.fromString(alteradoPor));
                    }

                    listaRolesJogo.add(roleJogoBuilder.build());
                }
                return listaRolesJogo;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RoleJogo> pegarTodasRolesJogoPorListaDeRoleJogoId(List<String> listaRoleJogoId) {
        System.out.println("LISTA" + listaRoleJogoId.size());

        List<RoleJogo> listaRolesJogo = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM jogos_roles WHERE id IN (");
        for (int i = 0; i < listaRoleJogoId.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        sql.append(")");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < listaRoleJogoId.size(); i++) {
                ps.setString(i + 1, listaRoleJogoId.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String nome = rs.getString("nome");
                    String descricao = rs.getString("descricao");
                    String icone = rs.getString("icone");
                    Timestamp dataEHoraCriacaoTimestamp = rs.getTimestamp("data_e_hora_criacao");
                    String criadoPor = rs.getString("criado_por_usuario_id");
                    Timestamp dataEHoraUltimaAlteracaoTimestamp = rs.getTimestamp("data_e_hora_ultima_alteracao");
                    String alteradoPor = rs.getString("alterado_por_usuario_id");
                    boolean ativo = rs.getBoolean("ativo");

                    RoleJogo.RoleJogoBuilder roleJogoBuilder = RoleJogo.builder()
                            .id(UUID.fromString(id))
                            .nome(nome)
                            .icone(icone)
                            .descricao(descricao)
                            .dataEHoraCriacao(dataEHoraCriacaoTimestamp.toLocalDateTime())
                            .criadoPor(UUID.fromString(criadoPor))
                            .ativo(ativo);

                    if (dataEHoraUltimaAlteracaoTimestamp != null) {
                        roleJogoBuilder.dataEHoraUltimaAlteracao(dataEHoraUltimaAlteracaoTimestamp.toLocalDateTime());
                    }
                    if (alteradoPor != null) {
                        roleJogoBuilder.alteradoPor(UUID.fromString(alteradoPor));
                    }

                    listaRolesJogo.add(roleJogoBuilder.build());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listaRolesJogo;
    }

    @Override
    public boolean verificarSeNomeRoleJogoJaExiste(String nomeRoleJogo) throws ValidacaoException {
        String sql = "SELECT EXISTS (SELECT nome FROM jogos_roles jr WHERE LOWER(jr.nome) = LOWER(?)) AS existe";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nomeRoleJogo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBoolean("existe");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String gerarSqlAlterarRoleJogo(RoleJogo roleJogo) {
        StringBuilder sql = new StringBuilder("UPDATE jogos_roles SET ");
        boolean first = true;
        if (roleJogo.getNome() != null) {
            sql.append("nome = ?");
            first = false;
        }
        if (roleJogo.getDescricao() != null) {
            sql.append("descricao = ?");
            first = false;
        }
        if (roleJogo.getIcone() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("icone = ?");
            first = false;
        }
        if (roleJogo.getAtivo() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("ativo = ?");
            first = false;
        }

        sql.append(", data_e_hora_ultima_alteracao = ?, alterado_por_usuario_id = ? WHERE id = ?");

        return sql.toString();
    }
}
