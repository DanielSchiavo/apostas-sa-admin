package com.apostassa.aplicacao.jogo.jogojuncaorole;

import com.apostassa.dominio.jogo.jogojuncaorole.JogoJuncaoRole;

public interface JogoJuncaoRoleAdminPresenter {

    public String respostaAdicionarRoleAUmJogo(JogoJuncaoRole jogoJuncaoRole);

    public String respostaRemoverRoleDeJogo(String roleJogoId, String jogoId);
}
