package com.apostassa.aplicacao.gateway.categoria;

import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.infra.servlet.categoria.AlterarCategoriaDTO;
import com.apostassa.infra.servlet.categoria.CadastrarCategoriaDTO;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoriaMapper {
	
	Categoria cadastrarCategoriaDTOParaCategoria(CadastrarCategoriaDTO cadastrarCategoriaDTO);

	@Mapping(source = "categoriaId", target = "id", qualifiedByName = "converterStringEmUUID")
	Categoria alterarCategoriaDTOParaCategoria(AlterarCategoriaDTO alterarCategoriaDTO);

	@Named(value = "converterStringEmUUID")
    default UUID converterStringEmUUID(String value) {
        return (value == null) ? null : UUID.fromString(value);
    }
}
