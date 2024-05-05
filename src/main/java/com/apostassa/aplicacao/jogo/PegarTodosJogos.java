package com.apostassa.aplicacao.jogo;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.jogo.Jogo;
import com.apostassa.dominio.jogo.RepositorioDeJogoAdmin;
import com.apostassa.dominio.jogo.jogojuncaorole.RepositorioDeJogoJuncaoRoleAdmin;
import com.apostassa.dominio.jogo.mapa.MapaJogo;
import com.apostassa.dominio.jogo.mapa.RepositorioDeMapaJogoAdmin;
import com.apostassa.dominio.jogo.role.RepositorioDeRoleJogoAdmin;
import com.apostassa.dominio.jogo.role.RoleJogo;

import java.util.List;

public class PegarTodosJogos {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeJogoAdmin repositorioDeJogoAdmin;

    private final JogoAdminPresenter jogoPresenter;

    private final RepositorioDeRoleJogoAdmin repositorioRoleJogo;

    private final RepositorioDeJogoJuncaoRoleAdmin repositorioJogoJuncaoRole;

    private final RepositorioDeMapaJogoAdmin repositorioMapaJogo;

    public PegarTodosJogos(ProvedorConexao provedorConexao, RepositorioDeJogoAdmin repositorioDeJogoAdmin, JogoAdminPresenter jogoPresenter, RepositorioDeRoleJogoAdmin repositorioRoleJogo, RepositorioDeJogoJuncaoRoleAdmin repositorioJogoJuncaoRole, RepositorioDeMapaJogoAdmin repositorioMapaJogo) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeJogoAdmin = repositorioDeJogoAdmin;
        this.jogoPresenter = jogoPresenter;
        this.repositorioRoleJogo = repositorioRoleJogo;
        this.repositorioJogoJuncaoRole = repositorioJogoJuncaoRole;
        this.repositorioMapaJogo = repositorioMapaJogo;
    }

    public String executa() {
        try {
            List<Jogo> jogos = repositorioDeJogoAdmin.pegarTodosJogos();

            jogos.forEach(jogo -> {
                List<String> listaRolesJogoId = repositorioJogoJuncaoRole.pegarTodosJogoJuncaoRolePorJogoId(jogo.getId().toString());

                if (!listaRolesJogoId.isEmpty()) {
                    List<RoleJogo> rolesJogos = repositorioRoleJogo.pegarTodasRolesJogoPorListaDeRoleJogoId(listaRolesJogoId);
                    rolesJogos.forEach(jogo::adicionarRole);
                }

                List<MapaJogo> mapasJogo = repositorioMapaJogo.pegarTodosMapasJogoPorJogoId(jogo.getId().toString());
                mapasJogo.forEach(jogo::adicionarMapa);
            });

            provedorConexao.commitarTransacao();

            return jogoPresenter.respostaPegarTodosJogos(jogos);
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
