package com.apostassa.aplicacao.gateway.jogo.role;

import com.apostassa.infra.servlet.jogo.role.AlterarRoleJogoDTO;
import com.apostassa.infra.servlet.jogo.role.CadastrarRoleJogoDTO;
import com.apostassa.dominio.jogo.role.RoleJogo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleJogoMapper {

    RoleJogo formatarCadastrarRoleJogoDTOParaRoleJogo(CadastrarRoleJogoDTO cadastrarRoleJogoDTO);

    RoleJogo formatarAlterarRoleJogoDTOParaRoleJogo(AlterarRoleJogoDTO alterarRoleJogoDTO);
}
