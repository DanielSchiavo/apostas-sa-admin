package com.apostassa.aplicacao.jogo;


import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.GeradorUUID;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.jogo.Jogo;
import com.apostassa.dominio.jogo.RepositorioDeJogoAdmin;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CadastrarJogo {

	private final ProvedorConexao provedorConexao;

	private final RepositorioDeJogoAdmin repositorioDeJogoAdmin;

	private final JogoAdminPresenter jogoPresenter;

	private final GeradorUUID geradorUuid;

	private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoriaAdmin;

	private final JogoMapper jogoMapper;

	public CadastrarJogo(ProvedorConexao provedorConexao, RepositorioDeJogoAdmin repositorioDeJogoAdmin, JogoAdminPresenter jogoPresenter, GeradorUUID geradorUuid, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoriaAdmin) {
		this.provedorConexao = provedorConexao;
		this.repositorioDeJogoAdmin = repositorioDeJogoAdmin;
		this.jogoPresenter = jogoPresenter;
		this.geradorUuid = geradorUuid;
		this.repositorioDeSubCategoriaAdmin = repositorioDeSubCategoriaAdmin;
		this.jogoMapper = Mappers.getMapper(JogoMapper.class);
	}
	
	public Map<String, String> executa(CadastrarJogoDTO cadastrarJogoDTO, String usuarioId) throws ValidacaoException {
		Set<ConstraintViolation<CadastrarJogoDTO>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(cadastrarJogoDTO);
		for (ConstraintViolation<CadastrarJogoDTO> violation : violations) {
			throw new ValidacaoException(violation.getMessage());
		}

		try {
			Jogo jogo = jogoMapper.formatarCadastrarJogoDTOParaJogo(cadastrarJogoDTO);
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
