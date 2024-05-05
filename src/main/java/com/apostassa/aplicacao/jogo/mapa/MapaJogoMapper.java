package com.apostassa.aplicacao.jogo.mapa;

import com.apostassa.dominio.jogo.mapa.MapaJogo;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "default" , builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapaJogoMapper {

    @Mapping(source = "jogoId", target = "jogoId", qualifiedByName = "converterStringEmUUID")
    MapaJogo formatarCadastrarMapaJogoDTOParaMapaJogo(CadastrarMapaJogoDTO cadastrarMapaJogoDTO);

    @Mapping(source = "jogoId", target = "jogoId", qualifiedByName = "converterStringEmUUID")
    MapaJogo formatarAlterarMapaJogoDTOParaMapaJogo(AlterarMapaJogoDTO alterarMapaJogoDTO);

    @Named(value = "converterStringEmUUID")
    default UUID converterStringEmUUID(String value) {
        return (value == null) ? null : UUID.fromString(value);
    }
}
