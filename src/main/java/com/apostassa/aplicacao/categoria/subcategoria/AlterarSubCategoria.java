package com.apostassa.aplicacao.categoria.subcategoria;

import com.apostassa.aplicacao.categoria.subcategoria.mapper.SubCategoriaMapper;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.AlterarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class AlterarSubCategoria {

	private RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;
	
	private SubCategoriaMapper subCategoriaMapper;
	
	public AlterarSubCategoria(RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria) {
		this.repositorioDeSubCategoria = repositorioDeSubCategoria;
		this.subCategoriaMapper = Mappers.getMapper(SubCategoriaMapper.class);
	}
	
	public void executa(AlterarSubCategoriaDTO alterarSubCategoriaDTO, String usuarioId) throws AlterarSubCategoriaException, ValidacaoException, AutenticacaoException {
		Set<ConstraintViolation<AlterarSubCategoriaDTO>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(alterarSubCategoriaDTO);
        for (ConstraintViolation<AlterarSubCategoriaDTO> violation : violations) {
        	throw new ValidacaoException(violation.getMessage());
        }
		
		try {
			SubCategoria subCategoria = subCategoriaMapper.formatarAlterarSubCategoriaDTOParaCategoria(alterarSubCategoriaDTO);
			subCategoria.setDataUltimaAlteracao(LocalDateTime.now());
			subCategoria.setAlteradorPor(UUID.fromString(usuarioId));
			
			repositorioDeSubCategoria.verificarSeNomeSubCategoriaJaExiste(subCategoria.getNome());
			
			repositorioDeSubCategoria.alterarSubCategoria(subCategoria);
			
			repositorioDeSubCategoria.commitarTransacao();
			
		} catch (ValidacaoException e) {
			e.printStackTrace();
			repositorioDeSubCategoria.rollbackTransacao();
			throw new ValidacaoException(e.getMessage());
		} catch (AlterarSubCategoriaException e) {
			e.printStackTrace();
			repositorioDeSubCategoria.rollbackTransacao();
			throw new AlterarSubCategoriaException(e.getMessage());
		}
	}
	
}
