package com.apostassa.aplicacao.categoria.subcategoria;

import com.apostassa.aplicacao.categoria.subcategoria.mapper.SubCategoriaMapper;
import com.apostassa.dominio.GeradorUUID;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

public class CadastrarSubCategoria {

	private RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;
	
	private SubCategoriaMapper subCategoriaMapper;
	
	private GeradorUUID geradorUuid;
	
	public CadastrarSubCategoria(RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria, GeradorUUID geradorUuid) {
		this.repositorioDeSubCategoria = repositorioDeSubCategoria;
		this.geradorUuid = geradorUuid;
		this.subCategoriaMapper = Mappers.getMapper(SubCategoriaMapper.class);
	}
	
	public String executa(CadastrarSubCategoriaDTO cadastrarSubCategoriaDTO, String usuarioId) throws AutenticacaoException, ValidacaoException {
		try {
			SubCategoria subcategoria = subCategoriaMapper.formatarCadastrarSubCategoriaDTOParaSubCategoria(cadastrarSubCategoriaDTO);
			subcategoria.setId(geradorUuid.gerarUUID());
			subcategoria.setDataCriacao(LocalDateTime.now());
			subcategoria.setCriadoPor(UUID.fromString(usuarioId));
			
			repositorioDeSubCategoria.verificarSeNomeSubCategoriaJaExiste(subcategoria.getNome());
			
			repositorioDeSubCategoria.cadastrarSubCategoria(subcategoria);
			
			repositorioDeSubCategoria.commitarTransacao();
			
			return subcategoria.getId().toString();
		} catch (ValidacaoException e) {
			e.printStackTrace();
			repositorioDeSubCategoria.rollbackTransacao();
			throw new ValidacaoException(e.getMessage());
		}
	}
	
}
