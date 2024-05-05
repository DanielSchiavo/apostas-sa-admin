package com.apostassa.aplicacao.jogo;

import com.apostassa.dominio.jogo.Jogo;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JogoMapper {

    Jogo formatarCadastrarJogoDTOParaJogo(CadastrarJogoDTO cadastrarJogoDTO);

    @Mapping(source = "jogoId", target = "id", qualifiedByName = "converterStringEmUUID")
    Jogo formatarAlterarJogoDTOParaJogo(AlterarJogoDTO alterarJogoDTO);

    @Named(value = "converterStringEmUUID")
    default UUID converterStringEmUUID(String value) {
        return (value == null) ? null : UUID.fromString(value);
    }

}
