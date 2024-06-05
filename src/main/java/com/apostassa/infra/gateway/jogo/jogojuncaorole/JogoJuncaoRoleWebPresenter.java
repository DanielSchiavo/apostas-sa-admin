package com.apostassa.infra.gateway.jogo.jogojuncaorole;

import com.apostassa.aplicacao.gateway.jogo.jogojuncaorole.JogoJuncaoRoleAdminPresenter;
import com.apostassa.dominio.jogo.jogojuncaorole.JogoJuncaoRole;

public class JogoJuncaoRoleWebPresenter implements JogoJuncaoRoleAdminPresenter {

    @Override
    public String respostaAdicionarRoleAUmJogo(JogoJuncaoRole jogoJuncaoRole) {
        return "Role adicionada ao jogo com sucesso!";
    }

    @Override
    public String respostaRemoverRoleDeJogo(String roleJogoId, String jogoId) {
        return "Role removida com sucesso!";
    }
}
