package com.apostassa.aplicacao.categoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.categoria.mapper.CategoriaMapper;
import com.apostassa.dominio.GeradorUUID;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.dominio.categoria.RepositorioDeCategoriaAdmin;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CadastrarCategoria {

	private final ProvedorConexao provedorConexao;

	private final RepositorioDeCategoriaAdmin repositorioDeCategoria;
	
	private final CategoriaMapper categoriaMapper;

	private final CategoriaAdminPresenter presenter;

	private final GeradorUUID geradorUuid;

	public CadastrarCategoria(ProvedorConexao provedorConexao, RepositorioDeCategoriaAdmin repositorioDeCategoria, CategoriaAdminPresenter presenter, GeradorUUID geradorUuid) {
		this.provedorConexao = provedorConexao;
		this.repositorioDeCategoria = repositorioDeCategoria;
		this.presenter = presenter;
		this.geradorUuid = geradorUuid;
		this.categoriaMapper = Mappers.getMapper(CategoriaMapper.class);
	}
	
	public Map<String, String> executa(CadastrarCategoriaDTO cadastrarCategoriaDTO, String usuarioId) throws ValidacaoException {
		Set<ConstraintViolation<CadastrarCategoriaDTO>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(cadastrarCategoriaDTO);
        for (ConstraintViolation<CadastrarCategoriaDTO> violation : violations) {
        	throw new ValidacaoException(violation.getMessage());
        }
		
		try {
			Categoria categoria = categoriaMapper.formatarCadastrarCategoriaDTOParaCategoria(cadastrarCategoriaDTO);
			categoria.setId(geradorUuid.gerarUUID());
			categoria.setDataEHoraCriacao(LocalDateTime.now());
			categoria.setCriadoPor(UUID.fromString(usuarioId));
			
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
