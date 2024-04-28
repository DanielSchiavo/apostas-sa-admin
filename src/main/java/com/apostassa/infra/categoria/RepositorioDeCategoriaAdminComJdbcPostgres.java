package com.apostassa.infra.categoria;


import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.AlterarCategoriaException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.dominio.categoria.RemoverCategoriaException;
import com.apostassa.dominio.categoria.RepositorioDeCategoriaAdmin;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class RepositorioDeCategoriaAdminComJdbcPostgres implements RepositorioDeCategoriaAdmin {

    private final Connection connection;

    public RepositorioDeCategoriaAdminComJdbcPostgres(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void cadastrarCategoria(Categoria categoria) throws ValidacaoException {
        String sql = """
                INSERT INTO categorias
                (id, nome, icone, data_criacao, criado_por_usuario_id, ativo)
                VALUES (?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categoria.getId().toString());
            ps.setString(2, categoria.getNome());
            ps.setString(3, categoria.getIcone());
            ps.setTimestamp(4, Timestamp.valueOf(categoria.getDataCriacao()));
            ps.setString(5, categoria.getCriadoPor().toString());
            ps.setBoolean(6, categoria.getAtivo());
            ps.execute();
        } catch (SQLException e) {
            throw new ValidacaoException("Erro ao cadastrar categoria");
        }
    }

    @Override
    public void alterarCategoria(Categoria categoria) throws AlterarCategoriaException, ValidacaoException {
        String sql = gerarSqlAlterarCategoria(categoria);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            if (categoria.getNome() != null) {
                ps.setString(index, categoria.getNome());
                index++;
            }
            if (categoria.getIcone() != null) {
                ps.setString(index, categoria.getIcone());
                index++;
            }
            if (categoria.getAtivo() != null) {
                ps.setBoolean(index, categoria.getAtivo());
                index++;
            }
            if (categoria.getDataUltimaAlteracao() != null) {
                ps.setTimestamp(index, Timestamp.valueOf(categoria.getDataUltimaAlteracao()));
                index++;
            }
            if (categoria.getAlteradorPor() != null) {
                ps.setString(index, categoria.getAlteradorPor().toString());
                index++;
            }
            ps.setString(index, categoria.getId().toString());
            int executeUpdate = ps.executeUpdate();

            if (executeUpdate == 0) {
                throw new AlterarCategoriaException("Não foi possivel alterar os dados da categoria");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String mensagem = "Erro ao alterar categoria";
            throw new AlterarCategoriaException(mensagem);
        }
    }

    @Override
    public void removerCategoria(Categoria categoria) throws RemoverCategoriaException {
        // TODO Auto-generated method stub

    }

    @Override
    public void verificarSeNomeCategoriaJaExiste(String nomeCategoria) throws ValidacaoException {
        String sql = "SELECT EXISTS (SELECT nome FROM categorias c WHERE LOWER(c.nome) = LOWER(?)) AS existe";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nomeCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean existe = rs.getBoolean("existe");

                    if (existe) {
                        throw new ValidacaoException("Já existe uma categoria cadastrada com esse nome");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Categoria pegarCategoriaPorId(String categoriaId) throws ValidacaoException {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String nome = rs.getString("nome");
                    String icone = rs.getString("icone");
                    LocalDateTime dataCriacao = rs.getTimestamp("data_criacao").toLocalDateTime();
                    String criadoPor = rs.getString("criado_por_usuario_id");
                    Timestamp dataUltimaAlteracaoTimestamp = rs.getTimestamp("data_ultima_alteracao");
                    String alteradoPor = rs.getString("alterado_por_usuario_id");
                    Long numeroApostas = rs.getLong("numero_apostas");
                    Long numeroEventos = rs.getLong("numero_eventos");
                    BigDecimal totalReaisMovimentado = rs.getBigDecimal("total_reais_movimentado");
                    BigDecimal totalDolarMovimentado = rs.getBigDecimal("total_dolar_movimentado");
                    BigDecimal totalEuroMovimentado = rs.getBigDecimal("total_euro_movimentado");
                    BigDecimal totalBitcoinMovimentado = rs.getBigDecimal("total_bitcoin_movimentado");
                    boolean ativo = rs.getBoolean("ativo");


                    Categoria.CategoriaBuilder categoriaBuilder = Categoria.builder().id(UUID.fromString(id))
                            .nome(nome)
                            .icone(icone)
                            .dataCriacao(dataCriacao)
                            .criadoPor(UUID.fromString(criadoPor))
                            .numeroApostas(numeroApostas)
                            .numeroEventos(numeroEventos)
                            .ativo(ativo);

                    if (dataUltimaAlteracaoTimestamp != null) {
                        categoriaBuilder.dataUltimaAlteracao(dataUltimaAlteracaoTimestamp.toLocalDateTime());
                    }
                    if (alteradoPor != null) {
                        categoriaBuilder.alteradorPor(UUID.fromString(alteradoPor));
                    }
                    if (totalReaisMovimentado != null) {
                        categoriaBuilder.totalReaisMovimentado(totalReaisMovimentado);
                    }
                    if (totalDolarMovimentado != null) {
                        categoriaBuilder.totalDolarMovimentado(totalDolarMovimentado);
                    }
                    if (totalEuroMovimentado != null) {
                        categoriaBuilder.totalEuroMovimentado(totalEuroMovimentado);
                    }
                    if (totalBitcoinMovimentado != null) {
                        categoriaBuilder.totalBitcoinMovimentado(totalBitcoinMovimentado);
                    }

                    return categoriaBuilder.build();

                } else {
                    throw new ValidacaoException("Não existe categoria com esse id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Categoria> pegarTodasCategorias() {
        String sql = "SELECT * FROM categorias";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Categoria> listaCategorias = new ArrayList<>();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String nome = rs.getString("nome");
                    String icone = rs.getString("icone");
                    LocalDateTime dataCriacao = rs.getTimestamp("data_criacao").toLocalDateTime();
                    String criadoPor = rs.getString("criado_por_usuario_id");
                    Timestamp dataUltimaAlteracaoTimestamp = rs.getTimestamp("data_ultima_alteracao");
                    String alteradoPor = rs.getString("alterado_por_usuario_id");
                    Long numeroApostas = rs.getLong("numero_apostas");
                    Long numeroEventos = rs.getLong("numero_eventos");
                    BigDecimal totalReaisMovimentado = rs.getBigDecimal("total_reais_movimentado");
                    BigDecimal totalDolarMovimentado = rs.getBigDecimal("total_dolar_movimentado");
                    BigDecimal totalEuroMovimentado = rs.getBigDecimal("total_euro_movimentado");
                    BigDecimal totalBitcoinMovimentado = rs.getBigDecimal("total_bitcoin_movimentado");
                    boolean ativo = rs.getBoolean("ativo");
                    String categoriaId = rs.getString("categoria_id");

                    Categoria.CategoriaBuilder categoriaBuilder =
                            Categoria.builder()
                                    .id(UUID.fromString(id))
                                    .nome(nome)
                                    .icone(icone)
                                    .dataCriacao(dataCriacao)
                                    .criadoPor(UUID.fromString(criadoPor))
                                    .numeroApostas(numeroApostas)
                                    .numeroEventos(numeroEventos)
                                    .ativo(ativo);

                    if (dataUltimaAlteracaoTimestamp != null) {
                        categoriaBuilder.dataUltimaAlteracao(dataUltimaAlteracaoTimestamp.toLocalDateTime());
                    }
                    if (alteradoPor != null) {
                        categoriaBuilder.alteradorPor(UUID.fromString(alteradoPor));
                    }
                    if (totalReaisMovimentado != null) {
                        categoriaBuilder.totalReaisMovimentado(totalReaisMovimentado);
                    }
                    if (totalDolarMovimentado != null) {
                        categoriaBuilder.totalDolarMovimentado(totalDolarMovimentado);
                    }
                    if (totalEuroMovimentado != null) {
                        categoriaBuilder.totalEuroMovimentado(totalEuroMovimentado);
                    }
                    if (totalBitcoinMovimentado != null) {
                        categoriaBuilder.totalBitcoinMovimentado(totalBitcoinMovimentado);
                    }

                    listaCategorias.add(categoriaBuilder.build());
                }

                return listaCategorias;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String gerarSqlAlterarCategoria(Categoria categoria) throws ValidacaoException {
        StringBuilder sql = new StringBuilder("UPDATE categorias SET ");
        boolean first = true;
        if (categoria.getNome() != null) {
            sql.append("nome = ?");
            first = false;
        }
        if (categoria.getIcone() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("icone = ?");
            first = false;
        }
        if (categoria.getAtivo() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("ativo = ?");
            first = false;
        }

        if (first) {
            throw new ValidacaoException("Para alterar uma categoria você deve enviar alguma alteração seja em nome, icone ou ativo");
        }
        sql.append(", data_ultima_alteracao = ?, alterado_por_usuario_id = ? WHERE id = ?");

        return sql.toString();
    }

    @Override
    public void commitarTransacao() {
        try {
            getConnection().commit();
            getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollbackTransacao() {
        try {
            getConnection().rollback();
            getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
