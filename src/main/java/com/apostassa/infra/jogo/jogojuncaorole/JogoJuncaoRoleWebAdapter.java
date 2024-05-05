package com.apostassa.infra.jogo.jogojuncaorole;

import com.apostassa.aplicacao.jogo.jogojuncaorole.JogoJuncaoRoleAdminPresenter;
import com.apostassa.dominio.jogo.jogojuncaorole.JogoJuncaoRole;

public class JogoJuncaoRoleWebAdapter implements JogoJuncaoRoleAdminPresenter {

    @Override
    public String respostaAdicionarRoleAUmJogo(JogoJuncaoRole jogoJuncaoRole) {
        return "Role adicionada ao jogo com sucesso!";
    }

    @Override
    public String respostaRemoverRoleDeJogo(String roleJogoId, String jogoId) {
        return "Role removida com sucesso!";
    }
}
