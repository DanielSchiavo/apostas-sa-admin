package com.apostassa.aplicacao.categoria.subcategoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.dominio.categoria.RepositorioDeCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.RepositorioDeSubCategoriaAdmin;
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
