package com.apostassa.aplicacao.jogo.mapa;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.jogo.mapa.MapaJogo;
import com.apostassa.dominio.jogo.mapa.RepositorioDeMapaJogoAdmin;

import java.util.List;

public class PegarTodosMapasJogoPorJogoId {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin;

    private final MapaJogoAdminPresenter presenter;

    public PegarTodosMapasJogoPorJogoId(ProvedorConexao provedorConexao, RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin, MapaJogoAdminPresenter presenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeMapaJogoAdmin = repositorioDeMapaJogoAdmin;
        this.presenter = presenter;
    }

    public String executa(String jogoId) {
        try {
            List<MapaJogo> mapasJogo = repositorioDeMapaJogoAdmin.pegarTodosMapasJogoPorJogoId(jogoId);

            provedorConexao.commitarTransacao();

            return presenter.respostaPegarTodosMapasJogo(mapasJogo);
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
