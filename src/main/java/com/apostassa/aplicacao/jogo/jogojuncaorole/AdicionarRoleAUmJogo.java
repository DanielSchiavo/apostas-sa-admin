package com.apostassa.aplicacao.jogo.jogojuncaorole;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.jogo.jogojuncaorole.AdicionarJogoJuncaoRoleException;
import com.apostassa.dominio.jogo.jogojuncaorole.JogoJuncaoRole;
import com.apostassa.dominio.jogo.jogojuncaorole.RepositorioDeJogoJuncaoRoleAdmin;

import java.time.LocalDateTime;

public class AdicionarRoleAUmJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeJogoJuncaoRoleAdmin repositorioDeJogoJuncaoRoleAdmin;

    private final JogoJuncaoRoleAdminPresenter presenter;

//    private final JogoJuncaoRoleMapper jogoJuncaoRoleMapper;

    public AdicionarRoleAUmJogo(ProvedorConexao provedorConexao, RepositorioDeJogoJuncaoRoleAdmin repositorioDeJogoJuncaoRoleAdmin, JogoJuncaoRoleAdminPresenter presenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeJogoJuncaoRoleAdmin = repositorioDeJogoJuncaoRoleAdmin;
        this.presenter = presenter;
//        this.jogoJuncaoRoleMapper = Mappers.getMapper(JogoJuncaoRoleMapper.class);
    }

    public String executa(String roleJogoId, String jogoId, String usuarioId) throws AdicionarJogoJuncaoRoleException {
        try {
            JogoJuncaoRole jogoJuncaoRole = JogoJuncaoRole.builder()
                                                        .roleJogoId(roleJogoId)
                                                        .jogoId(jogoId)
                                                        .atribuidoPor(usuarioId)
                                                        .dataEHoraAtribuicao(LocalDateTime.now()).build();
            repositorioDeJogoJuncaoRoleAdmin.adicionarRoleAUmJogo(jogoJuncaoRole);

            provedorConexao.commitarTransacao();

            return presenter.respostaAdicionarRoleAUmJogo(jogoJuncaoRole);
        } catch (AdicionarJogoJuncaoRoleException e) {
            provedorConexao.rollbackTransacao();
            throw new AdicionarJogoJuncaoRoleException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
