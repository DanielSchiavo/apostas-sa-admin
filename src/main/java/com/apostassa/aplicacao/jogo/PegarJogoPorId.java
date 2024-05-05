package com.apostassa.aplicacao.jogo;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.Jogo;
import com.apostassa.dominio.jogo.RepositorioDeJogoAdmin;
import com.apostassa.dominio.jogo.jogojuncaorole.RepositorioDeJogoJuncaoRoleAdmin;
import com.apostassa.dominio.jogo.mapa.MapaJogo;
import com.apostassa.dominio.jogo.mapa.RepositorioDeMapaJogoAdmin;
import com.apostassa.dominio.jogo.role.RepositorioDeRoleJogoAdmin;
import com.apostassa.dominio.jogo.role.RoleJogo;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class PegarJogoPorId {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeJogoAdmin repositorioDeJogoAdmin;

    private final JogoAdminPresenter jogoPresenter;

    private final RepositorioDeRoleJogoAdmin repositorioRoleJogo;

    private final RepositorioDeJogoJuncaoRoleAdmin repositorioJogoJuncaoRole;

    private final RepositorioDeMapaJogoAdmin repositorioMapaJogo;

    public PegarJogoPorId(ProvedorConexao provedorConexao, RepositorioDeJogoAdmin repositorioDeJogoAdmin, JogoAdminPresenter jogoPresenter, RepositorioDeRoleJogoAdmin repositorioRoleJogo, RepositorioDeJogoJuncaoRoleAdmin repositorioJogoJuncaoRole, RepositorioDeMapaJogoAdmin repositorioMapaJogo) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeJogoAdmin = repositorioDeJogoAdmin;
        this.jogoPresenter = jogoPresenter;
        this.repositorioRoleJogo = repositorioRoleJogo;
        this.repositorioJogoJuncaoRole = repositorioJogoJuncaoRole;
        this.repositorioMapaJogo = repositorioMapaJogo;
    }

    public String executa(String jogoId) throws JsonProcessingException, ValidacaoException {
        try {
            Jogo jogo = repositorioDeJogoAdmin.pegarJogoPorId(jogoId);

            List<String> listaRolesJogoId = repositorioJogoJuncaoRole.pegarTodosJogoJuncaoRolePorJogoId(jogoId);

            List<RoleJogo> rolesJogos = repositorioRoleJogo.pegarTodasRolesJogoPorListaDeRoleJogoId(listaRolesJogoId);
            rolesJogos.forEach(jogo::adicionarRole);

            List<MapaJogo> mapasJogo = repositorioMapaJogo.pegarTodosMapasJogoPorJogoId(jogoId);
            mapasJogo.forEach(jogo::adicionarMapa);

            provedorConexao.commitarTransacao();

            return jogoPresenter.respostaPegarJogoPorId(jogo);
        } catch (ValidacaoException e) {
            provedorConexao.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
