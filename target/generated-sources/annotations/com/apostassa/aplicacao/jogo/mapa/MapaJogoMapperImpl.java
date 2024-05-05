package com.apostassa.aplicacao.jogo.mapa;

import com.apostassa.dominio.jogo.mapa.MapaJogo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-01T04:35:22-0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
public class MapaJogoMapperImpl implements MapaJogoMapper {

    @Override
    public MapaJogo formatarCadastrarMapaJogoDTOParaMapaJogo(CadastrarMapaJogoDTO cadastrarMapaJogoDTO) {
        if ( cadastrarMapaJogoDTO == null ) {
            return null;
        }

        MapaJogo mapaJogo = new MapaJogo();

        mapaJogo.setJogoId( converterStringEmUUID( cadastrarMapaJogoDTO.getJogoId() ) );
        mapaJogo.setNome( cadastrarMapaJogoDTO.getNome() );
        mapaJogo.setImagem( cadastrarMapaJogoDTO.getImagem() );

        return mapaJogo;
    }

    @Override
    public MapaJogo formatarAlterarMapaJogoDTOParaMapaJogo(AlterarMapaJogoDTO alterarMapaJogoDTO) {
        if ( alterarMapaJogoDTO == null ) {
            return null;
        }

        MapaJogo mapaJogo = new MapaJogo();

        mapaJogo.setJogoId( converterStringEmUUID( alterarMapaJogoDTO.getJogoId() ) );
        mapaJogo.setNome( alterarMapaJogoDTO.getNome() );
        mapaJogo.setImagem( alterarMapaJogoDTO.getImagem() );
        mapaJogo.setAtivo( alterarMapaJogoDTO.getAtivo() );

        return mapaJogo;
    }
}
