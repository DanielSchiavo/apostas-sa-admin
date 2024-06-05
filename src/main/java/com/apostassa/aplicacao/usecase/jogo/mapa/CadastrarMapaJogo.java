package com.apostassa.aplicacao.usecase.jogo.mapa;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.jogo.mapa.MapaJogoAdminPresenter;
import com.apostassa.aplicacao.gateway.jogo.mapa.RepositorioDeMapaJogoAdmin;
import com.apostassa.dominio.GeradorUUID;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.mapa.MapaJogo;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CadastrarMapaJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin;

    private final MapaJogoAdminPresenter presenter;

    private final GeradorUUID geradorUuid;

    public CadastrarMapaJogo(ProvedorConexao provedorConexao, RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin, MapaJogoAdminPresenter presenter, GeradorUUID geradorUuid) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeMapaJogoAdmin = repositorioDeMapaJogoAdmin;
        this.presenter = presenter;
        this.geradorUuid = geradorUuid;
    }

    public Map<String, String> executa(MapaJogo mapaJogo, String usuarioId) throws ValidacaoException {
        try {
            mapaJogo.setId(geradorUuid.gerarUUID());
            mapaJogo.setDataEHoraCriacao(LocalDateTime.now());
            mapaJogo.setCriadoPor(UUID.fromString(usuarioId));

            boolean nomeRoleJogoJaExiste = repositorioDeMapaJogoAdmin.verificarSeNomeMapaJogoJaExiste(mapaJogo.getNome());
            if (nomeRoleJogoJaExiste) {
                throw new ValidacaoException("Já existe um mapa com esse nome!");
            }

            repositorioDeMapaJogoAdmin.cadastrarMapaJogo(mapaJogo);

            provedorConexao.commitarTransacao();

            return presenter.respostaCadastrarMapaJogo(mapaJogo);
        } catch (ValidacaoException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
