package com.apostassa.aplicacao.categoria.subcategoria.mapper;

import com.apostassa.aplicacao.categoria.subcategoria.AlterarSubCategoriaDTO;
import com.apostassa.aplicacao.categoria.subcategoria.CadastrarSubCategoriaDTO;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-27T21:14:18-0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
public class SubCategoriaMapperImpl implements SubCategoriaMapper {

    @Override
    public SubCategoria formatarCadastrarSubCategoriaDTOParaSubCategoria(CadastrarSubCategoriaDTO cadastrarSubCategoriaDTO) {
        if ( cadastrarSubCategoriaDTO == null ) {
            return null;
        }

        SubCategoria subCategoria = new SubCategoria();

        subCategoria.setCategoria( converterStringCategoriaIdEmCategoria( cadastrarSubCategoriaDTO.getCategoriaId() ) );
        subCategoria.setNome( cadastrarSubCategoriaDTO.getNome() );
        subCategoria.setIcone( cadastrarSubCategoriaDTO.getIcone() );
        subCategoria.setAtivo( cadastrarSubCategoriaDTO.getAtivo() );

        return subCategoria;
    }

    @Override
    public SubCategoria formatarAlterarSubCategoriaDTOParaCategoria(AlterarSubCategoriaDTO alterarSubCategoriaDTO) {
        if ( alterarSubCategoriaDTO == null ) {
            return null;
        }

        SubCategoria subCategoria = new SubCategoria();

        subCategoria.setId( converterStringEmUUID( alterarSubCategoriaDTO.getSubCategoriaId() ) );
        subCategoria.setCategoria( converterStringCategoriaIdEmCategoria( alterarSubCategoriaDTO.getCategoriaId() ) );
        subCategoria.setNome( alterarSubCategoriaDTO.getNome() );
        subCategoria.setIcone( alterarSubCategoriaDTO.getIcone() );
        if ( alterarSubCategoriaDTO.getAtivo() != null ) {
            subCategoria.setAtivo( Boolean.parseBoolean( alterarSubCategoriaDTO.getAtivo() ) );
        }

        return subCategoria;
    }
}
