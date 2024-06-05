package com.apostassa.infra.gateway.jogo.mapa;

import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.mapa.AlterarMapaJogoException;
import com.apostassa.dominio.jogo.mapa.DeletarMapaJogoException;
import com.apostassa.dominio.jogo.mapa.MapaJogo;
import com.apostassa.aplicacao.gateway.jogo.mapa.RepositorioDeMapaJogoAdmin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RepositorioDeMapaJogoAdminComJdbcPostgres implements RepositorioDeMapaJogoAdmin {

    private Connection connection;

    public RepositorioDeMapaJogoAdminComJdbcPostgres(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void cadastrarMapaJogo(MapaJogo mapaJogo) throws ValidacaoException {
        String sql = """
                INSERT INTO jogos_mapas 
                (id, nome, imagem, jogo_id, data_e_hora_criacao, criado_por_usuario_id)
                VALUES (?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, mapaJogo.getId().toString());
            ps.setString(2, mapaJogo.getNome());
            ps.setString(3, mapaJogo.getImagem());
            ps.setString(4, mapaJogo.getJogoId().toString());
            ps.setTimestamp(5, Timestamp.valueOf(mapaJogo.getDataEHoraCriacao()));
            ps.setString(6, mapaJogo.getCriadoPor().toString());
            ps.execute();
        } catch (SQLException e) {
            throw new ValidacaoException("Erro ao cadastrar mapa para o jogo" + e.getMessage());
        }
    }

    @Override
    public void alterarMapaJogo(MapaJogo mapaJogo) throws AlterarMapaJogoException {
        String sql = gerarSqlAlterarMapaJogo(mapaJogo);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            if (mapaJogo.getNome() != null) {
                ps.setString(index, mapaJogo.getNome());
                index++;
            }
            if (mapaJogo.getImagem() != null) {
                ps.setString(index, mapaJogo.getImagem());
                index++;
            }
            if (mapaJogo.getJogoId() != null) {
                ps.setString(index, mapaJogo.getImagem());
                index++;
            }
            if (mapaJogo.getAtivo() != null) {
                ps.setBoolean(index, mapaJogo.getAtivo());
                index++;
            }
            ps.setTimestamp(index, Timestamp.valueOf(mapaJogo.getDataEHoraUltimaAlteracao()));
            index++;

            ps.setString(index, mapaJogo.getAlteradoPor().toString());
            index++;

            ps.setString(index, mapaJogo.getId().toString());
            int executeUpdate = ps.executeUpdate();

            if (executeUpdate == 0) {
                throw new AlterarMapaJogoException("Não foi possivel alterar os dados desse mapa");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String mensagem = "Erro ao alterar mapa";
            throw new AlterarMapaJogoException(mensagem);
        }
    }

    @Override
    public void deletarMapaJogo(String mapaJogoId) throws DeletarMapaJogoException {
        String sql = "DELETE FROM jogos_mapas WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, mapaJogoId);
            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new DeletarMapaJogoException("Não foi possível deletar esse mapa");
            }
        } catch (SQLException e) {
            String mensagem = "Erro ao deletar role";
            throw new DeletarMapaJogoException(mensagem + e.getMessage());
        }
    }

    @Override
    public MapaJogo pegarMapaJogoPorId(String mapaJogoId) throws ValidacaoException {
        String sql = "SELECT * FROM jogos_mapas WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, mapaJogoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String nome = rs.getString("nome");
                    String imagem = rs.getString("imagem");
                    String jogoId = rs.getString("jogo_id");
                    Timestamp dataEHoraCriacaoTimestamp = rs.getTimestamp("data_e_hora_criacao");
                    String criadoPor = rs.getString("criado_por_usuario_id");
                    Timestamp dataEHoraUltimaAlteracaoTimestamp = rs.getTimestamp("data_e_hora_ultima_alteracao");
                    String alteradoPor = rs.getString("alterado_por_usuario_id");
                    boolean ativo = rs.getBoolean("ativo");

                    MapaJogo.MapaJogoBuilder mapaJogoBuilder = MapaJogo.builder()
                            .id(UUID.fromString(id))
                            .nome(nome)
                            .imagem(imagem)
                            .jogoId(UUID.fromString(jogoId))
                            .dataEHoraCriacao(dataEHoraCriacaoTimestamp.toLocalDateTime())
                            .criadoPor(UUID.fromString(criadoPor))
                            .ativo(ativo);

                    if (dataEHoraUltimaAlteracaoTimestamp != null) {
                        mapaJogoBuilder.dataEHoraUltimaAlteracao(dataEHoraUltimaAlteracaoTimestamp.toLocalDateTime());
                    }
                    if (alteradoPor != null) {
                        mapaJogoBuilder.alteradoPor(UUID.fromString(alteradoPor));
                    }

                    return mapaJogoBuilder.build();
                } else {
                    throw new ValidacaoException("Não existe mapa de jogo com esse ID!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MapaJogo> pegarTodosMapasJogoPorJogoId(String jogoId) {
        List<MapaJogo> listaMapasJogo = new ArrayList<>();
        String sql = "SELECT * FROM jogos_mapas WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, jogoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String nome = rs.getString("nome");
                    String imagem = rs.getString("imagem");
                    Timestamp dataEHoraCriacaoTimestamp = rs.getTimestamp("data_e_hora_criacao");
                    String criadoPor = rs.getString("criado_por_usuario_id");
                    Timestamp dataEHoraUltimaAlteracaoTimestamp = rs.getTimestamp("data_e_hora_ultima_alteracao");
                    String alteradoPor = rs.getString("alterado_por_usuario_id");
                    boolean ativo = rs.getBoolean("ativo");

                    MapaJogo.MapaJogoBuilder mapaJogoBuilder = MapaJogo.builder()
                                        .id(UUID.fromString(id))
                                        .nome(nome)
                                        .imagem(imagem)
                                        .jogoId(UUID.fromString(jogoId))
                                        .dataEHoraCriacao(dataEHoraCriacaoTimestamp.toLocalDateTime())
                                        .criadoPor(UUID.fromString(criadoPor))
                                        .ativo(ativo);

                    if (dataEHoraUltimaAlteracaoTimestamp != null) {
                        mapaJogoBuilder.dataEHoraUltimaAlteracao(dataEHoraUltimaAlteracaoTimestamp.toLocalDateTime());
                    }
                    if (alteradoPor != null) {
                        mapaJogoBuilder.alteradoPor(UUID.fromString(alteradoPor));
                    }
                    listaMapasJogo.add(mapaJogoBuilder.build());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listaMapasJogo;
    }

    @Override
    public boolean verificarSeNomeMapaJogoJaExiste(String nomeMapaJogo) throws ValidacaoException {
        String sql = "SELECT EXISTS (SELECT nome FROM jogos_mapas jm WHERE LOWER(jm.nome) = LOWER(?)) AS existe";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nomeMapaJogo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBoolean("existe");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String gerarSqlAlterarMapaJogo(MapaJogo mapaJogo) {
        StringBuilder sql = new StringBuilder("UPDATE jogos_roles SET ");
        boolean first = true;
        if (mapaJogo.getNome() != null) {
            sql.append("nome = ?");
            first = false;
        }
        if (mapaJogo.getImagem() != null) {
            sql.append("imagem = ?");
            first = false;
        }
        if (mapaJogo.getJogoId() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("jogo_id = ?");
            first = false;
        }
        if (mapaJogo.getAtivo() != null) {
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
