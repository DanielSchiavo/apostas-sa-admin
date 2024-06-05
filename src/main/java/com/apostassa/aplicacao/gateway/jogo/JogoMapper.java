package com.apostassa.aplicacao.gateway.jogo;

import com.apostassa.infra.servlet.jogo.AlterarJogoDTO;
import com.apostassa.infra.servlet.jogo.CadastrarJogoDTO;
import com.apostassa.dominio.jogo.Jogo;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JogoMapper {

    Jogo cadastrarJogoDTOParaJogo(CadastrarJogoDTO cadastrarJogoDTO);

    @Mapping(source = "jogoId", target = "id", qualifiedByName = "converterStringEmUUID")
    Jogo alterarJogoDTOParaJogo(AlterarJogoDTO alterarJogoDTO);

    @Named(value = "converterStringEmUUID")
    default UUID converterStringEmUUID(String value) {
        return (value == null) ? null : UUID.fromString(value);
    }

}
