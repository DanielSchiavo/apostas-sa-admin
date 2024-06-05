package com.apostassa.aplicacao.usecase.jogo.mapa;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.jogo.mapa.MapaJogoAdminPresenter;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.mapa.MapaJogo;
import com.apostassa.aplicacao.gateway.jogo.mapa.RepositorioDeMapaJogoAdmin;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;

public class PegarMapaJogoPorId {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin;

    private final MapaJogoAdminPresenter presenter;

    public PegarMapaJogoPorId(ProvedorConexao provedorConexao, RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin, MapaJogoAdminPresenter presenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeMapaJogoAdmin = repositorioDeMapaJogoAdmin;
        this.presenter = presenter;
    }

    public String executa(String mapaJogoId) throws ValidacaoException {
        try {
            MapaJogo mapaJogo = repositorioDeMapaJogoAdmin.pegarMapaJogoPorId(mapaJogoId);

            provedorConexao.commitarTransacao();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configOverride(LocalDateTime.class).setFormat(JsonFormat.Value.forPattern("dd/MM/yyyy hh:MM:ss"));

            return presenter.respostaPegarMapaJogoPorId(mapaJogo);
        } catch (ValidacaoException e) {
            throw new ValidacaoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
