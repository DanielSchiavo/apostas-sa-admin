package com.apostassa.aplicacao.usecase.jogo;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.jogo.JogoAdminPresenter;
import com.apostassa.aplicacao.gateway.jogo.RepositorioDeJogoAdmin;
import com.apostassa.aplicacao.gateway.usuario.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.AlterarJogoException;
import com.apostassa.dominio.jogo.Jogo;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlterarJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeJogoAdmin repositorioDeJogoAdmin;

    private final JogoAdminPresenter jogoPresenter;

    private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoriaAdmin;

    public AlterarJogo(ProvedorConexao provedorConexao, RepositorioDeJogoAdmin repositorioDeJogoAdmin, JogoAdminPresenter jogoPresenter, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoriaAdmin) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeJogoAdmin = repositorioDeJogoAdmin;
        this.jogoPresenter = jogoPresenter;
        this. repositorioDeSubCategoriaAdmin = repositorioDeSubCategoriaAdmin;
    }

    public String executa(Jogo jogo, String usuarioId) throws AlterarJogoException, ValidacaoException {
        try {
            jogo.setDataEHoraUltimaAlteracao(LocalDateTime.now());
            jogo.setAlteradoPor(UUID.fromString(usuarioId));

            repositorioDeJogoAdmin.verificarSeNomeJogoJaExiste(jogo.getNome());

            if (jogo.getSubCategoriaId() != null) {
                boolean existe = repositorioDeSubCategoriaAdmin.verificarSeSubCategoriaIdExiste(jogo.getSubCategoriaId());
                if (!existe) {
                    throw new ValidacaoException("Não existe sub-categoria com esse ID!");
                }
            }

            repositorioDeJogoAdmin.alterarJogo(jogo);

            provedorConexao.commitarTransacao();

            return jogoPresenter.respostaAlterarJogo(jogo);
        } catch (ValidacaoException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } catch (AlterarJogoException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new AlterarJogoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
