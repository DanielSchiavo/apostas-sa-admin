package com.apostassa.aplicacao.gateway.jogo.mapa;

import com.apostassa.dominio.jogo.mapa.MapaJogo;

import java.util.List;
import java.util.Map;

public interface MapaJogoAdminPresenter {

    public Map<String, String> respostaCadastrarMapaJogo(MapaJogo mapaJogo);

    public String respostaDeletarMapaJogo();

    public String respostaAlterarMapaJogo(MapaJogo mapaJogo);

    public String respostaPegarMapaJogoPorId(MapaJogo mapaJogo);

    public String respostaPegarTodosMapasJogo(List<MapaJogo> mapas);

}
