package com.apostassa.aplicacao.gateway.jogo.jogojuncaorole;

import com.apostassa.dominio.jogo.jogojuncaorole.AdicionarJogoJuncaoRoleException;
import com.apostassa.dominio.jogo.jogojuncaorole.JogoJuncaoRole;
import com.apostassa.dominio.jogo.jogojuncaorole.RemoverJogoJuncaoRoleException;

import java.util.List;

public interface RepositorioDeJogoJuncaoRoleAdmin {

    public void adicionarRoleAUmJogo(JogoJuncaoRole jogoJuncaoRole) throws AdicionarJogoJuncaoRoleException;

    public void removerRoleDeUmJogo(String roleJogoId, String jogoId) throws RemoverJogoJuncaoRoleException;

    public List<String> pegarTodosJogoJuncaoRolePorJogoId(String jogoId);
}
