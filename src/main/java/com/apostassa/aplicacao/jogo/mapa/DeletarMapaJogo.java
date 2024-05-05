package com.apostassa.aplicacao.jogo.mapa;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.jogo.mapa.DeletarMapaJogoException;
import com.apostassa.dominio.jogo.mapa.RepositorioDeMapaJogoAdmin;

public class DeletarMapaJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin;

    private final MapaJogoAdminPresenter mapaJogoAdminPresenter;

    public DeletarMapaJogo(ProvedorConexao provedorConexao, RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin, MapaJogoAdminPresenter mapaJogoAdminPresenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeMapaJogoAdmin = repositorioDeMapaJogoAdmin;
        this.mapaJogoAdminPresenter = mapaJogoAdminPresenter;
    }

    public String executa(String mapaJogoId) throws DeletarMapaJogoException {
        try {
            repositorioDeMapaJogoAdmin.deletarMapaJogo(mapaJogoId);

            provedorConexao.commitarTransacao();

            return mapaJogoAdminPresenter.respostaDeletarMapaJogo();
        } catch (DeletarMapaJogoException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new DeletarMapaJogoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
