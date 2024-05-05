package com.apostassa.aplicacao.categoria.subcategoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.categoria.subcategoria.mapper.SubCategoriaMapper;
import com.apostassa.dominio.GeradorUUID;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CadastrarSubCategoria {

	private final ProvedorConexao provedorConexao;

	private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;

	private final SubCategoriaAdminPresenter presenter;

	private final GeradorUUID geradorUuid;

	private final SubCategoriaMapper subCategoriaMapper;

	public CadastrarSubCategoria(ProvedorConexao provedorConexao, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria, SubCategoriaAdminPresenter presenter, GeradorUUID geradorUuid) {
		this.provedorConexao = provedorConexao;
		this.repositorioDeSubCategoria = repositorioDeSubCategoria;
		this.presenter = presenter;
		this.geradorUuid = geradorUuid;
		this.subCategoriaMapper = Mappers.getMapper(SubCategoriaMapper.class);
	}
	
	public Map<String, String> executa(CadastrarSubCategoriaDTO cadastrarSubCategoriaDTO, String usuarioId) throws AutenticacaoException, ValidacaoException {
		try {
			SubCategoria subcategoria = subCategoriaMapper.formatarCadastrarSubCategoriaDTOParaSubCategoria(cadastrarSubCategoriaDTO);
			subcategoria.setId(geradorUuid.gerarUUID());
			subcategoria.setDataCriacao(LocalDateTime.now());
			subcategoria.setCriadoPor(UUID.fromString(usuarioId));

			boolean existe = repositorioDeSubCategoria.verificarSeNomeSubCategoriaJaExiste(subcategoria.getNome());
			if (existe == true) {
				throw new ValidacaoException("Já existe uma Subcategoria com o nome " + subcategoria.getNome());
			}

			repositorioDeSubCategoria.cadastrarSubCategoria(subcategoria);
			
			provedorConexao.commitarTransacao();
			
			return presenter.respostaCadastrarSubCategoria(subcategoria);
		} catch (ValidacaoException e) {
			e.printStackTrace();
			provedorConexao.rollbackTransacao();
			throw new ValidacaoException(e.getMessage());
		} finally {
			provedorConexao.fecharConexao();
		}
	}
	
}
