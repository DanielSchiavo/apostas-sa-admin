package com.apostassa.aplicacao.jogo;

import com.apostassa.dominio.jogo.Jogo;

import java.util.List;
import java.util.Map;

public interface JogoAdminPresenter {

    public Map<String, String> respostaCadastrarJogo(Jogo jogo);

    public String respostaDeletarJogo();

    public String respostaAlterarJogo(Jogo jogo);

    public String respostaPegarJogoPorId(Jogo jogo);

    public String respostaPegarTodosJogos(List<Jogo> jogos);

}
