package com.apostassa.aplicacao.usecase.categoria.subcategoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.categoria.subcategoria.SubCategoriaAdminPresenter;
import com.apostassa.aplicacao.gateway.usuario.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.AlterarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;

import java.time.LocalDateTime;

public class AlterarSubCategoria {

	private final ProvedorConexao provedorConexao;

	private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;

	private final SubCategoriaAdminPresenter presenter;

	public AlterarSubCategoria(ProvedorConexao provedorConexao, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria, SubCategoriaAdminPresenter presenter) {
		this.provedorConexao = provedorConexao;
		this.repositorioDeSubCategoria = repositorioDeSubCategoria;
		this.presenter = presenter;
	}
	
	public String executa(SubCategoria subCategoria, String usuarioCpf) throws AlterarSubCategoriaException, ValidacaoException, AutenticacaoException {
		try {
			subCategoria.setDataUltimaAlteracao(LocalDateTime.now());
			subCategoria.setAlteradorPorUsuarioCpf(usuarioCpf);
			
			repositorioDeSubCategoria.verificarSeNomeSubCategoriaJaExiste(subCategoria.getNome());
			
			repositorioDeSubCategoria.alterarSubCategoria(subCategoria);
			
			provedorConexao.commitarTransacao();

			return presenter.respostaAlterarSubCategoria(subCategoria);
		} catch (ValidacaoException e) {
			e.printStackTrace();
			provedorConexao.rollbackTransacao();
			throw new ValidacaoException(e.getMessage());
		} catch (AlterarSubCategoriaException e) {
			e.printStackTrace();
			provedorConexao.rollbackTransacao();
			throw new AlterarSubCategoriaException(e.getMessage());
		} finally {
			provedorConexao.fecharConexao();
		}
	}
	
}
