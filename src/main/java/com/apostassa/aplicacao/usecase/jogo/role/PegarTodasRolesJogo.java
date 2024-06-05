package com.apostassa.aplicacao.usecase.jogo.role;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.jogo.role.RoleJogoAdminPresenter;
import com.apostassa.aplicacao.gateway.jogo.role.RepositorioDeRoleJogoAdmin;
import com.apostassa.dominio.jogo.role.RoleJogo;

import java.util.List;

public class PegarTodasRolesJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeRoleJogoAdmin repositorioDeRoleJogoAdmin;

    private final RoleJogoAdminPresenter presenter;

    public PegarTodasRolesJogo(ProvedorConexao provedorConexao, RepositorioDeRoleJogoAdmin repositorioDeRoleJogoAdmin, RoleJogoAdminPresenter presenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeRoleJogoAdmin = repositorioDeRoleJogoAdmin;
        this.presenter = presenter;
    }

    public String executa() {
        try {
            List<RoleJogo> rolesJogo = repositorioDeRoleJogoAdmin.pegarTodasRolesJogo();

            provedorConexao.commitarTransacao();

            return presenter.respostaPegarTodasRolesJogo(rolesJogo);
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
