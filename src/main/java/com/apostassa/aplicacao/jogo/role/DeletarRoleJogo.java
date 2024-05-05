package com.apostassa.aplicacao.jogo.role;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.role.DeletarRoleJogoException;
import com.apostassa.dominio.jogo.role.RepositorioDeRoleJogoAdmin;

public class DeletarRoleJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeRoleJogoAdmin repositorioDeRoleJogo;

    private final RoleJogoAdminPresenter roleJogoAdminPresenter;

    public DeletarRoleJogo(ProvedorConexao provedorConexao, RepositorioDeRoleJogoAdmin repositorioDeRoleJogo, RoleJogoAdminPresenter roleJogoAdminPresenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeRoleJogo = repositorioDeRoleJogo;
        this.roleJogoAdminPresenter = roleJogoAdminPresenter;
    }

    public String executa(String roleJogoId) throws ValidacaoException, DeletarRoleJogoException {
        try {
            repositorioDeRoleJogo.deletarRoleJogo(roleJogoId);

            provedorConexao.commitarTransacao();

            return roleJogoAdminPresenter.respostaDeletarRoleJogo();
        } catch (DeletarRoleJogoException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new DeletarRoleJogoException(e.getMessage());
        } catch (ValidacaoException e) {
            provedorConexao.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
