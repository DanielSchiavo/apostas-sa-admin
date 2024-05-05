package com.apostassa.infra.jogo;

import com.apostassa.aplicacao.jogo.JogoAdminPresenter;
import com.apostassa.dominio.jogo.Jogo;
import com.apostassa.infra.util.JacksonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JogoAdminWebAdapter implements JogoAdminPresenter {

    public Map<String, String> respostaCadastrarJogo(Jogo jogo) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("jogoId", jogo.getId().toString());
        resposta.put("mensagem", "Jogo cadastrado com sucesso!");
        return resposta;
    }

    public String respostaDeletarJogo() {
        return "Jogo deletado com sucesso!";
    }

    public String respostaAlterarJogo(Jogo jogo) {
        return "Jogo alterado com sucesso!";
    }

    public String respostaPegarJogoPorId(Jogo jogo) {
        return JacksonUtil.serializador(jogo);
    }

    public String respostaPegarTodosJogos(List<Jogo> jogos) {
        return JacksonUtil.serializador(jogos);
    }
}
