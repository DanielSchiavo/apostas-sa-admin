package com.apostassa.infra.gateway.jogo.mapa;

import com.apostassa.aplicacao.gateway.jogo.mapa.MapaJogoAdminPresenter;
import com.apostassa.dominio.jogo.mapa.MapaJogo;
import com.apostassa.infra.util.JacksonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapaJogoWebPresenter implements MapaJogoAdminPresenter {

    @Override
    public Map<String, String> respostaCadastrarMapaJogo(MapaJogo mapaJogo) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("mapaJogoId", mapaJogo.getId().toString());
        resposta.put("mensagem", "Mapa jogo cadastrado com sucesso!");
        return resposta;
    }

    @Override
    public String respostaDeletarMapaJogo() {
        return "Mapa jogo deletado com sucesso!";
    }

    @Override
    public String respostaAlterarMapaJogo(MapaJogo mapaJogo) {
        return "Mapa jogo alterado com sucesso!";
    }

    @Override
    public String respostaPegarMapaJogoPorId(MapaJogo mapaJogo) {
        return JacksonUtil.serializador(mapaJogo);
    }

    @Override
    public String respostaPegarTodosMapasJogo(List<MapaJogo> mapas) {
        return JacksonUtil.serializador(mapas);
    }
}
