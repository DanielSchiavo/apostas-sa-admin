package com.apostassa.aplicacao.usecase.categoria.subcategoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.categoria.subcategoria.SubCategoriaAdminPresenter;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.aplicacao.gateway.categoria.RepositorioDeCategoriaAdmin;
import com.apostassa.aplicacao.gateway.usuario.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.fasterxml.jackson.core.JsonProcessingException;


public class PegarSubCategoriaPorId {

	private final ProvedorConexao provedorConexao;

	private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;
	
	private final RepositorioDeCategoriaAdmin repositorioDeCategoria;

	private final SubCategoriaAdminPresenter presenter;
	
	public PegarSubCategoriaPorId(ProvedorConexao provedorConexao, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria, SubCategoriaAdminPresenter presenter, RepositorioDeCategoriaAdmin repositorioDeCategoria) {
		this.provedorConexao = provedorConexao;
		this.repositorioDeSubCategoria = repositorioDeSubCategoria;
		this.presenter = presenter;
		this.repositorioDeCategoria = repositorioDeCategoria;
	}
	
	public String executa(String subCategoriaId) throws ValidacaoException, JsonProcessingException {
		try {
			SubCategoria subCategoria = repositorioDeSubCategoria.pegarSubCategoriaPorId(subCategoriaId);

			Categoria categoria = repositorioDeCategoria.pegarCategoriaPorId(subCategoria.getCategoria().getId().toString());

			provedorConexao.commitarTransacao();

			subCategoria.setCategoria(categoria);

			return presenter.respostaPegarSubCategoriaPorId(subCategoria);
		} finally {
			provedorConexao.fecharConexao();
		}
	}
}
