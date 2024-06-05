package com.apostassa.aplicacao.usecase.categoria.subcategoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.categoria.subcategoria.SubCategoriaAdminPresenter;
import com.apostassa.aplicacao.gateway.usuario.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;

import java.time.LocalDateTime;
import java.util.Map;

public class CadastrarSubCategoria {

	private final ProvedorConexao provedorConexao;

	private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;

	private final SubCategoriaAdminPresenter presenter;

	public CadastrarSubCategoria(ProvedorConexao provedorConexao, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria, SubCategoriaAdminPresenter presenter) {
		this.provedorConexao = provedorConexao;
		this.repositorioDeSubCategoria = repositorioDeSubCategoria;
		this.presenter = presenter;
	}
	
	public Map<String, String> executa(SubCategoria subCategoria, String usuarioCpf) throws AutenticacaoException, ValidacaoException {
		try {
			subCategoria.setDataCriacao(LocalDateTime.now());
			subCategoria.setCriadoPorUsuarioCpf(usuarioCpf);

			boolean existe = repositorioDeSubCategoria.verificarSeNomeSubCategoriaJaExiste(subCategoria.getNome());
			if (existe == true) {
				throw new ValidacaoException("Já existe uma Subcategoria com o nome " + subCategoria.getNome());
			}

			repositorioDeSubCategoria.cadastrarSubCategoria(subCategoria);
			
			provedorConexao.commitarTransacao();
			
			return presenter.respostaCadastrarSubCategoria(subCategoria);
		} catch (ValidacaoException e) {
			e.printStackTrace();
			provedorConexao.rollbackTransacao();
			throw new ValidacaoException(e.getMessage());
		} finally {
			provedorConexao.fecharConexao();
		}
	}
	
}
