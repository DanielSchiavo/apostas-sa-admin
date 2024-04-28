package com.apostassa.aplicacao.categoria.mapper;

import java.util.UUID;

import com.apostassa.aplicacao.categoria.AlterarCategoriaDTO;
import com.apostassa.aplicacao.categoria.CadastrarCategoriaDTO;
import com.apostassa.dominio.categoria.Categoria;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoriaMapper {
	
	Categoria formatarCadastrarCategoriaDTOParaCategoria(CadastrarCategoriaDTO cadastrarCategoriaDTO);

	@Mapping(source = "categoriaId", target = "id", qualifiedByName = "converterStringEmUUID")
	Categoria formatarAlterarCategoriaDTOParaCategoria(AlterarCategoriaDTO alterarCategoriaDTO);

	@Named(value = "converterStringEmUUID")
    default UUID converterStringEmUUID(String value) {
        return (value == null) ? null : UUID.fromString(value);
    }
}
