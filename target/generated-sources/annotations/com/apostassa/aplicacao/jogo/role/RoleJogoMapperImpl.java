package com.apostassa.aplicacao.jogo.role;

import com.apostassa.dominio.jogo.role.RoleJogo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-01T04:35:22-0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
public class RoleJogoMapperImpl implements RoleJogoMapper {

    @Override
    public RoleJogo formatarCadastrarRoleJogoDTOParaRoleJogo(CadastrarRoleJogoDTO cadastrarRoleJogoDTO) {
        if ( cadastrarRoleJogoDTO == null ) {
            return null;
        }

        RoleJogo roleJogo = new RoleJogo();

        roleJogo.setNome( cadastrarRoleJogoDTO.getNome() );
        roleJogo.setDescricao( cadastrarRoleJogoDTO.getDescricao() );
        roleJogo.setIcone( cadastrarRoleJogoDTO.getIcone() );

        return roleJogo;
    }

    @Override
    public RoleJogo formatarAlterarRoleJogoDTOParaRoleJogo(AlterarRoleJogoDTO alterarRoleJogoDTO) {
        if ( alterarRoleJogoDTO == null ) {
            return null;
        }

        RoleJogo roleJogo = new RoleJogo();

        roleJogo.setNome( alterarRoleJogoDTO.getNome() );
        roleJogo.setDescricao( alterarRoleJogoDTO.getDescricao() );
        roleJogo.setIcone( alterarRoleJogoDTO.getIcone() );
        if ( alterarRoleJogoDTO.getAtivo() != null ) {
            roleJogo.setAtivo( Boolean.parseBoolean( alterarRoleJogoDTO.getAtivo() ) );
        }

        return roleJogo;
    }
}
