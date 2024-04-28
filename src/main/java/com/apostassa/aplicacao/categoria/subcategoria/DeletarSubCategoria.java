package com.apostassa.aplicacao.categoria.subcategoria;


import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.DeletarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;

public class DeletarSubCategoria {
	
	private RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;
	
	public DeletarSubCategoria(RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria) {
		this.repositorioDeSubCategoria = repositorioDeSubCategoria;
	}

	public void executa(String categoriaId) throws AutenticacaoException, DeletarSubCategoriaException, ValidacaoException {
		try {
			repositorioDeSubCategoria.deletarSubCategoria(categoriaId);
			
			repositorioDeSubCategoria.commitarTransacao();
		} catch (DeletarSubCategoriaException e) {
			e.printStackTrace();
			repositorioDeSubCategoria.rollbackTransacao();
			throw new DeletarSubCategoriaException(e.getMessage());
		} catch (ValidacaoException e) {
			e.printStackTrace();
			repositorioDeSubCategoria.rollbackTransacao();
			throw new ValidacaoException(e.getMessage());
		}
	}
	
}
