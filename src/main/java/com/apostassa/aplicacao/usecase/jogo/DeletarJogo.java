package com.apostassa.aplicacao.usecase.jogo;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.jogo.JogoAdminPresenter;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.DeletarJogoException;
import com.apostassa.aplicacao.gateway.jogo.RepositorioDeJogoAdmin;

public class DeletarJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeJogoAdmin repositorioDeJogo;

    private final JogoAdminPresenter jogoPresenter;

    public DeletarJogo(ProvedorConexao provedorConexao, RepositorioDeJogoAdmin repositorioDeJogo, JogoAdminPresenter jogoPresenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeJogo = repositorioDeJogo;
        this.jogoPresenter = jogoPresenter;
    }

    public String executa(String categoriaId) throws ValidacaoException, DeletarJogoException {
        try {
            repositorioDeJogo.deletarJogo(categoriaId);

            provedorConexao.commitarTransacao();

            return jogoPresenter.respostaDeletarJogo();
        } catch (DeletarJogoException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new DeletarJogoException(e.getMessage());
        } catch (ValidacaoException e) {
            provedorConexao.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }

}
