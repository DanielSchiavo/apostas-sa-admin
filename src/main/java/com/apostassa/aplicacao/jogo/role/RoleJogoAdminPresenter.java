package com.apostassa.aplicacao.jogo.role;

import com.apostassa.dominio.jogo.role.RoleJogo;

import java.util.List;
import java.util.Map;

public interface RoleJogoAdminPresenter {

    public Map<String, String> respostaCadastrarRoleJogo(RoleJogo roleJogo);

    public String respostaDeletarRoleJogo();

    public String respostaAlterarRoleJogo(RoleJogo roleJogo);

    public String respostaPegarRoleJogoPorId(RoleJogo roleJogo);

    public String respostaPegarTodasRolesJogo(List<RoleJogo> roles);

}
