package com.apostassa.aplicacao.usecase.jogo;


import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.jogo.JogoAdminPresenter;
import com.apostassa.aplicacao.gateway.jogo.RepositorioDeJogoAdmin;
import com.apostassa.aplicacao.gateway.usuario.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.GeradorUUID;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.Jogo;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CadastrarJogo {

	private final ProvedorConexao provedorConexao;

	private final RepositorioDeJogoAdmin repositorioDeJogoAdmin;

	private final JogoAdminPresenter jogoPresenter;

	private final GeradorUUID geradorUuid;

	private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoriaAdmin;

	public CadastrarJogo(ProvedorConexao provedorConexao, RepositorioDeJogoAdmin repositorioDeJogoAdmin, JogoAdminPresenter jogoPresenter, GeradorUUID geradorUuid, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoriaAdmin) {
		this.provedorConexao = provedorConexao;
		this.repositorioDeJogoAdmin = repositorioDeJogoAdmin;
		this.jogoPresenter = jogoPresenter;
		this.geradorUuid = geradorUuid;
		this.repositorioDeSubCategoriaAdmin = repositorioDeSubCategoriaAdmin;
	}
	
	public Map<String, String> executa(Jogo jogo, String usuarioId) throws ValidacaoException {
		try {
			jogo.setId(geradorUuid.gerarUUID());
			jogo.setDataEHoraCriacao(LocalDateTime.now());
			jogo.setCriadoPor(UUID.fromString(usuarioId));

			repositorioDeJogoAdmin.verificarSeNomeJogoJaExiste(jogo.getNome());

			boolean existe = repositorioDeSubCategoriaAdmin.verificarSeSubCategoriaIdExiste(jogo.getSubCategoriaId());
			if (!existe) {
				throw new ValidacaoException("Não existe sub-categoria com esse ID!");
			}

			repositorioDeJogoAdmin.cadastrarJogo(jogo);

			provedorConexao.commitarTransacao();

			return jogoPresenter.respostaCadastrarJogo(jogo);
		} catch (ValidacaoException e) {
			e.printStackTrace();
			provedorConexao.rollbackTransacao();
			throw new ValidacaoException(e.getMessage());
		} finally {
			provedorConexao.fecharConexao();
		}
	}
	
}
