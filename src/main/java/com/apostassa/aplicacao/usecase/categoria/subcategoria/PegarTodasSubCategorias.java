package com.apostassa.aplicacao.usecase.categoria.subcategoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.categoria.subcategoria.SubCategoriaAdminPresenter;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.aplicacao.gateway.categoria.RepositorioDeCategoriaAdmin;
import com.apostassa.aplicacao.gateway.usuario.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PegarTodasSubCategorias {

	private final ProvedorConexao provedorConexao;

	private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;

	private final SubCategoriaAdminPresenter presenter;
	
	private final RepositorioDeCategoriaAdmin repositorioDeCategoria;
	
	public PegarTodasSubCategorias(ProvedorConexao provedorConexao, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria, SubCategoriaAdminPresenter presenter, RepositorioDeCategoriaAdmin repositorioDeCategoria) {
		this.provedorConexao = provedorConexao;
		this.repositorioDeSubCategoria = repositorioDeSubCategoria;
		this.presenter = presenter;
		this.repositorioDeCategoria = repositorioDeCategoria;
	}
	
	public String executa() throws ValidacaoException, JsonProcessingException{
		try {
			List<SubCategoria> subCategorias = repositorioDeSubCategoria.pegarTodasSubCategorias();

			Map<String, Categoria> map = new HashMap<>();
			subCategorias.forEach(subCategoria -> {
				String categoriaId = subCategoria.getCategoria().getId().toString();
				boolean containsKey = map.containsKey(categoriaId);
				Categoria categoria = null;
				if (containsKey) {
					categoria = map.get(categoriaId);
				} else {
					try {
						categoria = repositorioDeCategoria.pegarCategoriaPorId(categoriaId);
					} catch (ValidacaoException e) {
						e.printStackTrace();
					}
					map.put(categoriaId, categoria);
				}
				subCategoria.setCategoria(categoria);
			});

			provedorConexao.commitarTransacao();

			return presenter.respostaPegarTodasSubCategorias(subCategorias);
		} finally {
				provedorConexao.fecharConexao();
			}
	}
	
}
