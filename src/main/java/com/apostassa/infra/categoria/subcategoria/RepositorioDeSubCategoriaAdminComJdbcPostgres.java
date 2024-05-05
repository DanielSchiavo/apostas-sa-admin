package com.apostassa.infra.categoria.subcategoria;

import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.dominio.categoria.subcategoria.AlterarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.DeletarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RepositorioDeSubCategoriaAdminComJdbcPostgres implements RepositorioDeSubCategoriaAdmin {

	private final Connection connection;

	public RepositorioDeSubCategoriaAdminComJdbcPostgres(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void cadastrarSubCategoria(SubCategoria subCategoria) throws ValidacaoException {
		String sql = """
				INSERT INTO sub_categorias
				(id, nome, icone, data_e_hora_criacao, criado_por_usuario_id, ativo, categoria_id)
				VALUES (?, ?, ?, ?, ?, ?, ?)""";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, subCategoria.getId().toString());
			ps.setString(2, subCategoria.getNome());
			ps.setString(3, subCategoria.getIcone());
			ps.setTimestamp(4, Timestamp.valueOf(subCategoria.getDataCriacao()));
			ps.setString(5, subCategoria.getCriadoPor().toString());
			ps.setBoolean(6, subCategoria.getAtivo());
			ps.setString(7, subCategoria.getCategoria().getId().toString());
			ps.execute();
		} catch (SQLException e) {
			String mensagem = "Erro ao cadastrar sub-categoria";
			if (e.getMessage().contains("sub_categorias_categoria_id_fkey")) {
				mensagem = "Esse categoria_id não existe";
			}
			throw new ValidacaoException(mensagem);
		}
	}

	@Override
	public void alterarSubCategoria(SubCategoria subCategoria) throws AlterarSubCategoriaException, ValidacaoException {
		String sql = gerarSqlAlterarSubCategoria(subCategoria);

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			int index = 1;
			if (subCategoria.getNome() != null) {
				ps.setString(index, subCategoria.getNome());
				index++;
			}
			if (subCategoria.getIcone() != null) {
				ps.setString(index, subCategoria.getIcone());
				index++;
			}
			if (subCategoria.getAtivo() != null) {
				ps.setBoolean(index, subCategoria.getAtivo());
				index++;
			}
			if (subCategoria.getCategoria() != null) {
				ps.setString(index, subCategoria.getCategoria().getId().toString());
				index++;
			}
			if (subCategoria.getDataUltimaAlteracao() != null) {
				ps.setTimestamp(index, Timestamp.valueOf(subCategoria.getDataUltimaAlteracao()));
				index++;
			}
			if (subCategoria.getAlteradorPor() != null) {
				ps.setString(index, subCategoria.getAlteradorPor().toString());
				index++;
			}
			ps.setString(index, subCategoria.getId().toString());
			int executeUpdate = ps.executeUpdate();

			if (executeUpdate == 0) {
				throw new AlterarSubCategoriaException("Não foi possivel alterar os dados da sub-categoria");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			String mensagem = "Erro ao alterar sub-categoria";
			if (e.getMessage().contains("sub_categorias_categoria_id_fkey")) {
				mensagem = "Insira um ID de categoria válido";
			}
			
		    throw new AlterarSubCategoriaException(mensagem);
		}
	}

	@Override
	public void deletarSubCategoria(String subCategoriaId) throws DeletarSubCategoriaException, ValidacaoException {
		String sql = "DELETE FROM sub_categorias WHERE id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, subCategoriaId);
	        int linhasAfetadas = ps.executeUpdate();

	        if (linhasAfetadas == 0) {
	            throw new DeletarSubCategoriaException("Não foi possível remover essa sub-categoria");
	        }
		} catch (SQLException e) {
			String mensagem = "Erro ao deletar sub-categoria";
			throw new ValidacaoException(mensagem);
		}
	}

	@Override
	public boolean verificarSeNomeSubCategoriaJaExiste(String nomeSubCategoria) throws ValidacaoException {
		String sql = "SELECT EXISTS (SELECT nome FROM sub_categorias sc WHERE LOWER(sc.nome) = LOWER(?)) AS existe";
		
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, nomeSubCategoria);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				boolean existe = rs.getBoolean("existe");
				return existe;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public SubCategoria pegarSubCategoriaPorId(String subCategoriaId) throws ValidacaoException {
		String sql = "SELECT * FROM sub_categorias WHERE id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, subCategoriaId);
			try (ResultSet rs = ps.executeQuery()) {
				boolean encontrou = rs.next();
				if (!encontrou) {
					throw new ValidacaoException("Não existe sub-categoria com esse id");
				}
				String id = rs.getString("id");
				String nome = rs.getString("nome");
				String icone = rs.getString("icone");
				LocalDateTime dataCriacao = rs.getTimestamp("data_e_hora_criacao").toLocalDateTime();
				String criadoPor = rs.getString("criado_por_usuario_id");
				Timestamp dataUltimaAlteracaoTimestamp = rs.getTimestamp("data_e_hora_ultima_alteracao");
				String alteradoPor = rs.getString("alterado_por_usuario_id");
				Long numeroApostas = rs.getLong("numero_apostas");
				Long numeroEventos = rs.getLong("numero_eventos");
				BigDecimal totalReaisMovimentado = rs.getBigDecimal("total_reais_movimentado");
				BigDecimal totalDolarMovimentado = rs.getBigDecimal("total_dolar_movimentado");
				BigDecimal totalEuroMovimentado = rs.getBigDecimal("total_euro_movimentado");
				BigDecimal totalBitcoinMovimentado = rs.getBigDecimal("total_bitcoin_movimentado");
				boolean ativo = rs.getBoolean("ativo");
				String categoriaId = rs.getString("categoria_id");

				SubCategoria.SubCategoriaBuilder subCategoriaBuilder =
												SubCategoria.builder()
												.id(UUID.fromString(id))
												.nome(nome)
												.icone(icone)
												.dataCriacao(dataCriacao)
												.criadoPor(UUID.fromString(criadoPor))
												.ativo(ativo)
												.categoria(Categoria.builder()
												.id(UUID.fromString(categoriaId)).build());

				if (dataUltimaAlteracaoTimestamp != null) {
					subCategoriaBuilder.dataUltimaAlteracao(dataUltimaAlteracaoTimestamp.toLocalDateTime());
				}
				if (alteradoPor != null) {
					subCategoriaBuilder.alteradorPor(UUID.fromString(alteradoPor));
				}
				if (numeroApostas != null) {
					subCategoriaBuilder.numeroApostas(numeroApostas);
				}
				if (numeroEventos != null) {
					subCategoriaBuilder.numeroEventos(numeroEventos);
				}
				if (totalReaisMovimentado != null) {
					subCategoriaBuilder.totalReaisMovimentado(totalReaisMovimentado);
				}
				if (totalDolarMovimentado != null) {
					subCategoriaBuilder.totalDolarMovimentado(totalDolarMovimentado);
				}
				if (totalEuroMovimentado != null) {
					subCategoriaBuilder.totalEuroMovimentado(totalEuroMovimentado);
				}
				if (totalBitcoinMovimentado != null) {
					subCategoriaBuilder.totalBitcoinMovimentado(totalBitcoinMovimentado);
				}

				return subCategoriaBuilder.build();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<SubCategoria> pegarTodasSubCategorias() throws ValidacaoException {
		String sql = "SELECT * FROM sub_categorias";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				
				List<SubCategoria> listaSubCategorias = new ArrayList<>();
				while (rs.next()) {
					String id = rs.getString("id");
					String nome = rs.getString("nome");
					String icone = rs.getString("icone");
					LocalDateTime dataCriacao = rs.getTimestamp("data_e_hora_criacao").toLocalDateTime();
					String criadoPor = rs.getString("criado_por_usuario_id");
					Timestamp dataUltimaAlteracaoTimestamp = rs.getTimestamp("data_e_hora_ultima_alteracao");
					String alteradoPor = rs.getString("alterado_por_usuario_id");
					Long numeroApostas = rs.getLong("numero_apostas");
					Long numeroEventos = rs.getLong("numero_eventos");
					BigDecimal totalReaisMovimentado = rs.getBigDecimal("total_reais_movimentado");
					BigDecimal totalDolarMovimentado = rs.getBigDecimal("total_dolar_movimentado");
					BigDecimal totalEuroMovimentado = rs.getBigDecimal("total_euro_movimentado");
					BigDecimal totalBitcoinMovimentado = rs.getBigDecimal("total_bitcoin_movimentado");
					boolean ativo = rs.getBoolean("ativo");
					String categoriaId = rs.getString("categoria_id");

					SubCategoria.SubCategoriaBuilder subCategoriaBuilder =
							SubCategoria.builder()
							.id(UUID.fromString(id))
							.nome(nome)
							.icone(icone)
							.dataCriacao(dataCriacao)
							.criadoPor(UUID.fromString(criadoPor))
							.ativo(ativo)
							.categoria(Categoria.builder().id(UUID.fromString(categoriaId)).build());
					
					if (dataUltimaAlteracaoTimestamp != null) {
						subCategoriaBuilder.dataUltimaAlteracao(dataUltimaAlteracaoTimestamp.toLocalDateTime());
					}
					if (alteradoPor != null) {
						subCategoriaBuilder.alteradorPor(UUID.fromString(alteradoPor));
					}
					if (numeroApostas != null) {
						subCategoriaBuilder.numeroApostas(numeroApostas);
					}
					if (numeroEventos != null) {
						subCategoriaBuilder.numeroEventos(numeroEventos);
					}
					if (totalReaisMovimentado != null) {
						subCategoriaBuilder.totalReaisMovimentado(totalReaisMovimentado);
					}
					if (totalDolarMovimentado != null) {
						subCategoriaBuilder.totalDolarMovimentado(totalDolarMovimentado);
					}
					if (totalEuroMovimentado != null) {
						subCategoriaBuilder.totalEuroMovimentado(totalEuroMovimentado);
					}
					if (totalBitcoinMovimentado != null) {
						subCategoriaBuilder.totalBitcoinMovimentado(totalBitcoinMovimentado);
					}
					
					listaSubCategorias.add(subCategoriaBuilder.build());
				}

				return listaSubCategorias;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<SubCategoria> pegarTodasSubCategoriasPorCategoriaId(String categoriaId) {
		String sql = "SELECT * FROM sub_categorias WHERE categoria_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, categoriaId);
			try (ResultSet rs = ps.executeQuery()) {
				List<SubCategoria> listaSubCategorias = new ArrayList<>();
				while (rs.next()) {
					String id = rs.getString("id");
					String nome = rs.getString("nome");
					String icone = rs.getString("icone");
					LocalDateTime dataCriacao = rs.getTimestamp("data_e_hora_criacao").toLocalDateTime();
					String criadoPor = rs.getString("criado_por_usuario_id");
					Timestamp dataUltimaAlteracaoTimestamp = rs.getTimestamp("data_e_hora_ultima_alteracao");
					String alteradoPor = rs.getString("alterado_por_usuario_id");
					Long numeroApostas = rs.getLong("numero_apostas");
					Long numeroEventos = rs.getLong("numero_eventos");
					BigDecimal totalReaisMovimentado = rs.getBigDecimal("total_reais_movimentado");
					BigDecimal totalDolarMovimentado = rs.getBigDecimal("total_dolar_movimentado");
					BigDecimal totalEuroMovimentado = rs.getBigDecimal("total_euro_movimentado");
					BigDecimal totalBitcoinMovimentado = rs.getBigDecimal("total_bitcoin_movimentado");
					boolean ativo = rs.getBoolean("ativo");

					SubCategoria.SubCategoriaBuilder subCategoriaBuilder =
							SubCategoria.builder()
									.id(UUID.fromString(id))
									.nome(nome)
									.icone(icone)
									.dataCriacao(dataCriacao)
									.criadoPor(UUID.fromString(criadoPor))
									.ativo(ativo)
									.categoria(Categoria.builder().id(UUID.fromString(categoriaId)).build());

					if (dataUltimaAlteracaoTimestamp != null) {
						subCategoriaBuilder.dataUltimaAlteracao(dataUltimaAlteracaoTimestamp.toLocalDateTime());
					}
					if (alteradoPor != null) {
						subCategoriaBuilder.alteradorPor(UUID.fromString(alteradoPor));
					}
					if (numeroApostas != null) {
						subCategoriaBuilder.numeroApostas(numeroApostas);
					}
					if (numeroEventos != null) {
						subCategoriaBuilder.numeroEventos(numeroEventos);
					}
					if (totalReaisMovimentado != null) {
						subCategoriaBuilder.totalReaisMovimentado(totalReaisMovimentado);
					}
					if (totalDolarMovimentado != null) {
						subCategoriaBuilder.totalDolarMovimentado(totalDolarMovimentado);
					}
					if (totalEuroMovimentado != null) {
						subCategoriaBuilder.totalEuroMovimentado(totalEuroMovimentado);
					}
					if (totalBitcoinMovimentado != null) {
						subCategoriaBuilder.totalBitcoinMovimentado(totalBitcoinMovimentado);
					}

					listaSubCategorias.add(subCategoriaBuilder.build());
				}

				return listaSubCategorias;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean verificarSeSubCategoriaIdExiste(String subCategoriaId) throws ValidacaoException {
		String sql = "SELECT EXISTS (SELECT id FROM sub_categorias sc WHERE id = ?) AS existe";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, subCategoriaId);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				boolean existe = rs.getBoolean("existe");
				return existe;
			}
		} catch (SQLException e) {
			throw new ValidacaoException(e.getMessage());
		}
	}

	private String gerarSqlAlterarSubCategoria(SubCategoria subCategoria) throws ValidacaoException {
		StringBuilder sql = new StringBuilder("UPDATE sub_categorias SET ");
        boolean first = true;
		if (subCategoria.getNome() != null) {
            sql.append("nome = ?");
            first = false;
		}
		if (subCategoria.getIcone() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("icone = ?");
            first = false;
		}
        if (subCategoria.getAtivo() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("ativo = ?");
            first = false;
        }
        if (subCategoria.getCategoria() != null) {
            if (!first) {
                sql.append(", ");
            }
            sql.append("categoria_id = ?");
            first = false;
        }
        
        if (first == true) {
        	throw new ValidacaoException("Para alterar uma sub-categoria você deve enviar alguma alteração seja em nome, icone ou ativo");
        }
        sql.append(", data_e_hora_ultima_alteracao = ?, alterado_por_usuario_id = ? WHERE id = ?");
        
        return sql.toString();
	}
}
