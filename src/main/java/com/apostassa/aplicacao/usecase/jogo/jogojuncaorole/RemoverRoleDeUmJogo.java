package com.apostassa.aplicacao.usecase.jogo.jogojuncaorole;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.jogo.jogojuncaorole.JogoJuncaoRoleAdminPresenter;
import com.apostassa.aplicacao.gateway.jogo.jogojuncaorole.JogoJuncaoRoleMapper;
import com.apostassa.dominio.jogo.jogojuncaorole.RemoverJogoJuncaoRoleException;
import com.apostassa.aplicacao.gateway.jogo.jogojuncaorole.RepositorioDeJogoJuncaoRoleAdmin;
import org.mapstruct.factory.Mappers;

public class RemoverRoleDeUmJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeJogoJuncaoRoleAdmin repositorioDeJogoJuncaoRoleAdmin;

    private final JogoJuncaoRoleAdminPresenter presenter;

    private final JogoJuncaoRoleMapper jogoJuncaoRoleMapper;

    public RemoverRoleDeUmJogo(ProvedorConexao provedorConexao, RepositorioDeJogoJuncaoRoleAdmin repositorioDeJogoJuncaoRoleAdmin, JogoJuncaoRoleAdminPresenter presenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeJogoJuncaoRoleAdmin = repositorioDeJogoJuncaoRoleAdmin;
        this.presenter = presenter;
        this.jogoJuncaoRoleMapper = Mappers.getMapper(JogoJuncaoRoleMapper.class);
    }

    public String executa(String roleJogoId, String jogoId) throws RemoverJogoJuncaoRoleException {
        try {
            repositorioDeJogoJuncaoRoleAdmin.removerRoleDeUmJogo(roleJogoId, jogoId);

            provedorConexao.commitarTransacao();

            return presenter.respostaRemoverRoleDeJogo(roleJogoId, jogoId);
        } catch (RemoverJogoJuncaoRoleException e) {
            provedorConexao.rollbackTransacao();
            throw new RemoverJogoJuncaoRoleException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
