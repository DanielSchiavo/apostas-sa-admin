package com.apostassa.infra.gateway.jogo;

import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.AlterarJogoException;
import com.apostassa.dominio.jogo.DeletarJogoException;
import com.apostassa.dominio.jogo.Jogo;
import com.apostassa.aplicacao.gateway.jogo.RepositorioDeJogoAdmin;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RepositorioDeJogoAdminComJdbcPostgres implements RepositorioDeJogoAdmin {

    private final Connection connection;

    public RepositorioDeJogoAdminComJdbcPostgres(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void cadastrarJogo(Jogo jogo) throws ValidacaoException {
        String sql = """
                INSERT INTO jogos
                (id, nome, icone, descricao, imagem, data_e_hora_criacao, criado_por_usuario_id, ativo, sub_categoria_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, jogo.getId().toString());
            ps.setString(2, jogo.getNome());
            ps.setString(3, jogo.getIcone());
            ps.setString(4, jogo.getDescricao());
            ps.setString(5, jogo.getImagem());
            ps.setTimestamp(6, Timestamp.valueOf(jogo.getDataEHoraCriacao()));
            ps.setString(7, jogo.getCriadoPor().toString());
            ps.setBoolean(8, jogo.getAtivo());
            ps.setString(9, jogo.getSubCategoriaId());
            ps.execute();
        } catch (SQLException e) {
            throw new ValidacaoException("Erro ao cadastrar jogo" + e.getMessage());
        }
    }

    @Override
    public void deletarJogo(String jogoId) throws DeletarJogoException, ValidacaoException {
        String sql = "DELETE FROM jogos WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, jogoId);
            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new DeletarJogoException("Não foi possível remover essa sub-categoria");
            }
        } catch (SQLException e) {
            String mensagem = "Erro ao deletar sub-categoria";
            throw new ValidacaoException(mensagem + e.getMessage());
        }
    }

    @Override
    public void alterarJogo(Jogo jogo) throws AlterarJogoException {
        String sql = gerarSqlAlterarJogo(jogo);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            if (jogo.getNome() != null) {
                ps.setString(index, jogo.getNome());
                index++;
            }
            if (jogo.getIcone() != null) {
                ps.setString(index, jogo.getIcone());
                index++;
            }
            if (jogo.getAtivo() != null) {
                ps.setBoolean(index, jogo.getAtivo());
                index++;
            }
            ps.setTimestamp(index, Timestamp.valueOf(jogo.getDataEHoraUltimaAlteracao()));
            index++;

            ps.setString(index, jogo.getAlteradoPor().toString());
            index++;

            ps.setString(index, jogo.getId().toString());
            int executeUpdate = ps.executeUpdate();

            if (executeUpdate == 0) {
                throw new AlterarJogoException("Não foi possivel alterar os dados do jogo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String mensagem = "Erro ao alterar categoria";
            throw new AlterarJogoException(mensagem);
        }
    }

    @Override
    public Jogo pegarJogoPorId(String jogoId) throws ValidacaoException {
        String sql = "SELECT * FROM jogos WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, jogoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String nome = rs.getString("nome");
                    String icone = rs.getString("icone");
                    String descricao = rs.getString("descricao");
                    String imagem = rs.getString("imagem");
                    Timestamp dataEHoraCriacaoTimestamp = rs.getTimestamp("data_e_hora_criacao");
                    String criadoPor = rs.getString("criado_por_usuario_id");
                    Timestamp dataEHoraUltimaAlteracaoTimestamp = rs.getTimestamp("data_e_hora_ultima_alteracao");
                    String alteradoPor = rs.getString("alterado_por_usuario_id");
                    Long numeroApostas = rs.getLong("numero_apostas");
                    Long numeroEventos = rs.getLong("numero_eventos");
                    BigDecimal totalReaisMovimentado = rs.getBigDecimal("total_reais_movimentado");
                    BigDecimal totalDolarMovimentado = rs.getBigDecimal("total_dolar_movimentado");
                    BigDecimal totalEuroMovimentado = rs.getBigDecimal("total_euro_movimentado");
                    BigDecimal totalBitcoinMovimentado = rs.getBigDecimal("total_bitcoin_movimentado");
                    boolean ativo = rs.getBoolean("ativo");
                    String subCategoriaId = rs.getString("sub_categoria_id");

                    Jogo.JogoBuilder jogoBuilder = Jogo.builder()
                                                        .id(UUID.fromString(id)).nome(nome).icone(icone)
                                                        .descricao(descricao)
                                                        .imagem(imagem)
                                                        .dataEHoraCriacao(dataEHoraCriacaoTimestamp.toLocalDateTime())
                                                        .criadoPor(UUID.fromString(criadoPor))
                                                        .numeroApostas(numeroApostas)
                                                        .numeroEventos(numeroEventos)
                                                        .ativo(ativo);

                    if (dataEHoraUltimaAlteracaoTimestamp != null) {
                        jogoBuilder.dataEHoraUltimaAlteracao(dataEHoraUltimaAlteracaoTimestamp.toLocalDateTime());
                    }
                    if (alteradoPor != null) {
                        jogoBuilder.alteradoPor(UUID.fromString(alteradoPor));
                    }
                    if (totalReaisMovimentado != null) {
                        jogoBuilder.totalReaisMovimentado(totalReaisMovimentado);
                    }
                    if (totalDolarMovimentado != null) {
                        jogoBuilder.totalDolarMovimentado(totalDolarMovimentado);
                    }
                    if (totalEuroMovimentado != null) {
                        jogoBuilder.totalEuroMovimentado(totalEuroMovimentado);
                    }
                    if (totalBitcoinMovimentado != null) {
                        jogoBuilder.totalBitcoinMovimentado(totalBitcoinMovimentado);
                    }

                    return jogoBuilder.build();

                } else {
                    throw new ValidacaoException("Não existe jogo com esse id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Jogo> pegarTodosJogos() {
        String sql = "SELECT * FROM jogos";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Jogo> listaJogos = new ArrayList<>();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String nome = rs.getString("nome");
                    String icone = rs.getString("icone");
                    String descricao = rs.getString("descricao");
                    String imagem = rs.getString("imagem");
                    Timestamp dataEHoraCriacaoTimestamp = rs.getTimestamp("data_e_hora_criacao");
                    String criadoPor = rs.getString("criado_por_usuario_id");
                    Timestamp dataEHoraUltimaAlteracaoTimestamp = rs.getTimestamp("data_e_hora_ultima_alteracao");
                    String alteradoPor = rs.getString("alterado_por_usuario_id");
                    Long numeroApostas = rs.getLong("numero_apostas");
                    Long numeroEventos = rs.getLong("numero_eventos");
                    BigDecimal totalReaisMovimentado = rs.getBigDecimal("total_reais_movimentado");
                    BigDecimal totalDolarMovimentado = rs.getBigDecimal("total_dolar_movimentado");
                    BigDecimal totalEuroMovimentado = rs.getBigDecimal("total_euro_movimentado");
                    BigDecimal totalBitcoinMovimentado = rs.getBigDecimal("total_bitcoin_movimentado");
                    boolean ativo = rs.getBoolean("ativo");
                    String subCategoriaId = rs.getString("sub_categoria_id");

                    Jogo.JogoBuilder jogoBuilder = Jogo.builder()
                            .id(UUID.fromString(id)).nome(nome).icone(icone)
                            .descricao(descricao)
                            .imagem(imagem)
                            .dataEHoraCriacao(dataEHoraCriacaoTimestamp.toLocalDateTime())
                            .criadoPor(UUID.fromString(criadoPor))
                            .numeroApostas(numeroApostas)
                            .numeroEventos(numeroEventos)
                            .ativo(ativo)
                            .subCategoriaId(subCategoriaId);

                    if (dataEHoraUltimaAlteracaoTimestamp != null) {
                        jogoBuilder.dataEHoraUltimaAlteracao(dataEHoraUltimaAlteracaoTimestamp.toLocalDateTime());
                    }
                    if (alteradoPor != null) {
                        jogoBuilder.alteradoPor(UUID.fromString(alteradoPor));
                    }
                    if (totalReaisMovimentado != null) {
                        jogoBuilder.totalReaisMovimentado(totalReaisMovimentado);
                    }
                    if (totalDolarMovimentado != null) {
                        jogoBuilder.totalDolarMovimentado(totalDolarMovimentado);
                    }
                    if (totalEuroMovimentado != null) {
                        jogoBuilder.totalEuroMovimentado(totalEuroMovimentado);
                    }
                    if (totalBitcoinMovimentado != null) {
                        jogoBuilder.totalBitcoinMovimentado(totalBitcoinMovimentado);
                    }

                    listaJogos.add(jogoBuilder.build());
                }

                return listaJogos;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void verificarSeNomeJogoJaExiste(String nomeJogo) throws ValidacaoException {
        String sql = "SELECT EXISTS (SELECT nome FROM jogos j WHERE LOWER(j.nome) = LOWER(?)) AS existe";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nomeJogo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean existe = rs.getBoolean("existe");

                    if (existe) {
                        throw new ValidacaoException("Já existe um jogo cadastrado com esse nome");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verificarSeJogoExistePorId(String jogoId) {
        String sql = "SELECT EXISTS (SELECT nome FROM jogos WHERE id = ?) AS existe";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, jogoId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBoolean("existe");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String gerarSqlAlterarJogo(Jogo jogo) {
        StringBuilder sql = new StringBuilder("UPDATE jogos SET ");
        boolean first = true;
        if (jogo.getNome() != null) {
            sql.append("nome = ?");
            first = false;
        }
        if (jogo.getIcone() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("icone = ?");
            first = false;
        }
        if (jogo.getDescricao() != null) {
            sql.append("descricao = ?");
            first = false;
        }
        if (jogo.getImagem() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("imagem = ?");
            first = false;
        }
        if (jogo.getSubCategoriaId() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("sub_categoria_id = ?");
            first = false;
        }
        if (jogo.getAtivo() != null) {
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
