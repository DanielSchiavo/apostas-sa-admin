package com.apostassa.aplicacao.usecase.categoria.subcategoria;


import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.categoria.subcategoria.SubCategoriaAdminPresenter;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.DeletarSubCategoriaException;
import com.apostassa.aplicacao.gateway.usuario.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;

public class DeletarSubCategoria {

	private final ProvedorConexao provedorConexao;

	private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;

	private final SubCategoriaAdminPresenter presenter;
	
	public DeletarSubCategoria(ProvedorConexao provedorConexao, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria, SubCategoriaAdminPresenter presenter) {
		this.provedorConexao = provedorConexao;
		this.repositorioDeSubCategoria = repositorioDeSubCategoria;
		this.presenter = presenter;
	}

	public String executa(String categoriaId) throws AutenticacaoException, DeletarSubCategoriaException, ValidacaoException {
		try {
			repositorioDeSubCategoria.deletarSubCategoria(categoriaId);
			
			provedorConexao.commitarTransacao();

			return presenter.respostaDeletarSubCategoria();
		} catch (DeletarSubCategoriaException e) {
			e.printStackTrace();
			provedorConexao.rollbackTransacao();
			throw new DeletarSubCategoriaException(e.getMessage());
		} catch (ValidacaoException e) {
			e.printStackTrace();
			provedorConexao.rollbackTransacao();
			throw new ValidacaoException(e.getMessage());
		} finally {
			provedorConexao.fecharConexao();
		}
	}
	
}
