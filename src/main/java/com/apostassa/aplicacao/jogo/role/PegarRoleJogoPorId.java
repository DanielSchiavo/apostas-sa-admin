package com.apostassa.aplicacao.jogo.role;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.role.RepositorioDeRoleJogoAdmin;
import com.apostassa.dominio.jogo.role.RoleJogo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;

public class PegarRoleJogoPorId {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeRoleJogoAdmin repositorioDeRoleJogoAdmin;

    private final RoleJogoAdminPresenter presenter;

    public PegarRoleJogoPorId(ProvedorConexao provedorConexao, RepositorioDeRoleJogoAdmin repositorioDeRoleJogoAdmin, RoleJogoAdminPresenter presenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeRoleJogoAdmin = repositorioDeRoleJogoAdmin;
        this.presenter = presenter;
    }

    public String executa(String roleJogoId) throws ValidacaoException {
        try {
            RoleJogo roleJogo = repositorioDeRoleJogoAdmin.pegarRoleJogoPorId(roleJogoId);

            provedorConexao.commitarTransacao();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configOverride(LocalDateTime.class).setFormat(JsonFormat.Value.forPattern("dd/MM/yyyy hh:MM:ss"));

            return presenter.respostaPegarRoleJogoPorId(roleJogo);
        } catch (ValidacaoException e) {
            throw new ValidacaoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
