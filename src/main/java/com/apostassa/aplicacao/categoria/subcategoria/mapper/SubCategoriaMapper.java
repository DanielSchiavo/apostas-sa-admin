package com.apostassa.aplicacao.categoria.subcategoria.mapper;

import com.apostassa.aplicacao.categoria.subcategoria.AlterarSubCategoriaDTO;
import com.apostassa.aplicacao.categoria.subcategoria.CadastrarSubCategoriaDTO;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubCategoriaMapper {
	
	@Mapping(source = "categoriaId", target = "categoria", qualifiedByName = "converterStringCategoriaIdEmCategoria")
	SubCategoria formatarCadastrarSubCategoriaDTOParaSubCategoria(CadastrarSubCategoriaDTO cadastrarSubCategoriaDTO);
	
	@Mapping(source = "subCategoriaId", target = "id", qualifiedByName = "converterStringEmUUID")
	@Mapping(source = "categoriaId", target = "categoria", qualifiedByName = "converterStringCategoriaIdEmCategoria")
	SubCategoria formatarAlterarSubCategoriaDTOParaCategoria(AlterarSubCategoriaDTO alterarSubCategoriaDTO);

	@Named(value = "converterStringEmUUID")
    default UUID converterStringEmUUID(String value) {
        return (value == null) ? null : UUID.fromString(value);
    }
	
	@Named(value = "converterStringCategoriaIdEmCategoria")
    default Categoria converterStringCategoriaIdEmCategoria(String value) {
		return Categoria.builder().id(UUID.fromString(value)).build();
	}

}
