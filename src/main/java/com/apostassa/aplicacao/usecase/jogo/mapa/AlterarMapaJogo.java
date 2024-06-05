package com.apostassa.aplicacao.usecase.jogo.mapa;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.jogo.mapa.MapaJogoAdminPresenter;
import com.apostassa.aplicacao.gateway.jogo.mapa.RepositorioDeMapaJogoAdmin;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.mapa.AlterarMapaJogoException;
import com.apostassa.dominio.jogo.mapa.MapaJogo;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlterarMapaJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin;

    private final MapaJogoAdminPresenter presenter;

    public AlterarMapaJogo(ProvedorConexao provedorConexao, RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin, MapaJogoAdminPresenter presenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeMapaJogoAdmin = repositorioDeMapaJogoAdmin;
        this.presenter = presenter;
    }

    public String executa(MapaJogo mapaJogo, String usuarioId) throws ValidacaoException, AlterarMapaJogoException {
        try {
            mapaJogo.setDataEHoraUltimaAlteracao(LocalDateTime.now());
            mapaJogo.setAlteradoPor(UUID.fromString(usuarioId));

            if (mapaJogo.getNome() != null) {
                boolean nomeRoleJogoJaExiste = repositorioDeMapaJogoAdmin.verificarSeNomeMapaJogoJaExiste(mapaJogo.getNome());
                if (nomeRoleJogoJaExiste) {
                    throw new ValidacaoException("Nome do mapa já existe!");
                }
            }

            repositorioDeMapaJogoAdmin.alterarMapaJogo(mapaJogo);
            provedorConexao.commitarTransacao();

            return presenter.respostaAlterarMapaJogo(mapaJogo);
        } catch (ValidacaoException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } catch (AlterarMapaJogoException e) {
            provedorConexao.rollbackTransacao();
            throw new AlterarMapaJogoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }

}
