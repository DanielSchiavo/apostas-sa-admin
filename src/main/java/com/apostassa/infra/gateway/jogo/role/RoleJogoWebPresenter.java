package com.apostassa.infra.gateway.jogo.role;

import com.apostassa.aplicacao.gateway.jogo.role.RoleJogoAdminPresenter;
import com.apostassa.dominio.jogo.role.RoleJogo;
import com.apostassa.infra.util.JacksonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleJogoWebPresenter implements RoleJogoAdminPresenter {

    @Override
    public Map<String, String> respostaCadastrarRoleJogo(RoleJogo roleJogo) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("roleJogoId", roleJogo.getId().toString());
        resposta.put("mensagem", "Role jogo cadastrado com sucesso!");
        return resposta;
    }

    @Override
    public String respostaDeletarRoleJogo() {
        return "Role jogo deletado com sucesso!";
    }

    @Override
    public String respostaAlterarRoleJogo(RoleJogo roleJogo) {
        return "Role jogo alterado com sucesso!";
    }

    @Override
    public String respostaPegarRoleJogoPorId(RoleJogo roleJogo) {
        return JacksonUtil.serializador(roleJogo);
    }

    @Override
    public String respostaPegarTodasRolesJogo(List<RoleJogo> roles) {
        return JacksonUtil.serializador(roles);
    }
}
