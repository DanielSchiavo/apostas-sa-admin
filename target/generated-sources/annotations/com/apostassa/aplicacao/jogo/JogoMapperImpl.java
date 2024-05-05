package com.apostassa.aplicacao.jogo;

import com.apostassa.dominio.jogo.Jogo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-01T04:35:22-0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
public class JogoMapperImpl implements JogoMapper {

    @Override
    public Jogo formatarCadastrarJogoDTOParaJogo(CadastrarJogoDTO cadastrarJogoDTO) {
        if ( cadastrarJogoDTO == null ) {
            return null;
        }

        Jogo jogo = new Jogo();

        jogo.setNome( cadastrarJogoDTO.getNome() );
        jogo.setIcone( cadastrarJogoDTO.getIcone() );
        jogo.setDescricao( cadastrarJogoDTO.getDescricao() );
        jogo.setImagem( cadastrarJogoDTO.getImagem() );
        jogo.setAtivo( cadastrarJogoDTO.getAtivo() );
        jogo.setSubCategoriaId( cadastrarJogoDTO.getSubCategoriaId() );

        return jogo;
    }

    @Override
    public Jogo formatarAlterarJogoDTOParaJogo(AlterarJogoDTO alterarJogoDTO) {
        if ( alterarJogoDTO == null ) {
            return null;
        }

        Jogo jogo = new Jogo();

        jogo.setId( converterStringEmUUID( alterarJogoDTO.getJogoId() ) );
        jogo.setNome( alterarJogoDTO.getNome() );
        jogo.setIcone( alterarJogoDTO.getIcone() );
        jogo.setDescricao( alterarJogoDTO.getDescricao() );
        jogo.setImagem( alterarJogoDTO.getImagem() );
        if ( alterarJogoDTO.getAtivo() != null ) {
            jogo.setAtivo( Boolean.parseBoolean( alterarJogoDTO.getAtivo() ) );
        }
        jogo.setSubCategoriaId( alterarJogoDTO.getSubCategoriaId() );

        return jogo;
    }
}
