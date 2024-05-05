package com.apostassa.aplicacao.jogo.mapa;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.GeradorUUID;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.mapa.MapaJogo;
import com.apostassa.dominio.jogo.mapa.RepositorioDeMapaJogoAdmin;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CadastrarMapaJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin;

    private final MapaJogoAdminPresenter presenter;

    private final GeradorUUID geradorUuid;

    private final MapaJogoMapper mapaJogoMapper;

    public CadastrarMapaJogo(ProvedorConexao provedorConexao, RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin, MapaJogoAdminPresenter presenter, GeradorUUID geradorUuid) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeMapaJogoAdmin = repositorioDeMapaJogoAdmin;
        this.presenter = presenter;
        this.geradorUuid = geradorUuid;
        this.mapaJogoMapper = Mappers.getMapper(MapaJogoMapper.class);
    }

    public Map<String, String> executa(CadastrarMapaJogoDTO cadastrarMapaJogoDTO, String usuarioId) throws ValidacaoException {
        Set<ConstraintViolation<CadastrarMapaJogoDTO>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(cadastrarMapaJogoDTO);
        for (ConstraintViolation<CadastrarMapaJogoDTO> violation : violations) {
            throw new ValidacaoException(violation.getMessage());
        }

        try {
            MapaJogo mapaJogo = mapaJogoMapper.formatarCadastrarMapaJogoDTOParaMapaJogo(cadastrarMapaJogoDTO);
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
