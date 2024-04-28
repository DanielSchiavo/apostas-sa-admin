package com.apostassa.aplicacao.categoria.mapper;

import com.apostassa.aplicacao.categoria.AlterarCategoriaDTO;
import com.apostassa.aplicacao.categoria.CadastrarCategoriaDTO;
import com.apostassa.dominio.categoria.Categoria;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-27T21:14:18-0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
public class CategoriaMapperImpl implements CategoriaMapper {

    @Override
    public Categoria formatarCadastrarCategoriaDTOParaCategoria(CadastrarCategoriaDTO cadastrarCategoriaDTO) {
        if ( cadastrarCategoriaDTO == null ) {
            return null;
        }

        Categoria categoria = new Categoria();

        categoria.setNome( cadastrarCategoriaDTO.getNome() );
        categoria.setIcone( cadastrarCategoriaDTO.getIcone() );
        categoria.setAtivo( cadastrarCategoriaDTO.getAtivo() );

        return categoria;
    }

    @Override
    public Categoria formatarAlterarCategoriaDTOParaCategoria(AlterarCategoriaDTO alterarCategoriaDTO) {
        if ( alterarCategoriaDTO == null ) {
            return null;
        }

        Categoria categoria = new Categoria();

        categoria.setId( converterStringEmUUID( alterarCategoriaDTO.getCategoriaId() ) );
        categoria.setNome( alterarCategoriaDTO.getNome() );
        categoria.setIcone( alterarCategoriaDTO.getIcone() );
        if ( alterarCategoriaDTO.getAtivo() != null ) {
            categoria.setAtivo( Boolean.parseBoolean( alterarCategoriaDTO.getAtivo() ) );
        }

        return categoria;
    }
}
