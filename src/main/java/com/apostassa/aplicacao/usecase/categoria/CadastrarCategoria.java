package com.apostassa.aplicacao.usecase.categoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.categoria.CategoriaAdminPresenter;
import com.apostassa.aplicacao.gateway.categoria.RepositorioDeCategoriaAdmin;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.Categoria;

import java.time.LocalDateTime;
import java.util.Map;

public class CadastrarCategoria {

	private final ProvedorConexao provedorConexao;

	private final RepositorioDeCategoriaAdmin repositorioDeCategoria;

	private final CategoriaAdminPresenter presenter;

	public CadastrarCategoria(ProvedorConexao provedorConexao, RepositorioDeCategoriaAdmin repositorioDeCategoria, CategoriaAdminPresenter presenter) {
		this.provedorConexao = provedorConexao;
		this.repositorioDeCategoria = repositorioDeCategoria;
		this.presenter = presenter;
	}
	
	public Map<String, String> executa(Categoria categoria, String usuarioCpf) throws ValidacaoException {
		try {
			categoria.setDataEHoraCriacao(LocalDateTime.now());
			categoria.setCriadoPorUsuarioCpf(usuarioCpf);
			
			repositorioDeCategoria.verificarSeNomeCategoriaJaExiste(categoria.getNome());
			
			repositorioDeCategoria.cadastrarCategoria(categoria);
			
			provedorConexao.commitarTransacao();
			
			return presenter.respostaCadastrarCategoria(categoria);
		} catch (ValidacaoException e) {
			e.printStackTrace();
			provedorConexao.rollbackTransacao();
			throw new ValidacaoException(e.getMessage());
		} finally {
			provedorConexao.fecharConexao();
		}
	}
	
}
