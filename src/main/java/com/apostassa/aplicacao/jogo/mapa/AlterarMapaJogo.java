package com.apostassa.aplicacao.jogo.mapa;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.mapa.AlterarMapaJogoException;
import com.apostassa.dominio.jogo.mapa.MapaJogo;
import com.apostassa.dominio.jogo.mapa.RepositorioDeMapaJogoAdmin;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class AlterarMapaJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin;

    private final MapaJogoAdminPresenter presenter;

    private final MapaJogoMapper mapaJogoMapper;

    public AlterarMapaJogo(ProvedorConexao provedorConexao, RepositorioDeMapaJogoAdmin repositorioDeMapaJogoAdmin, MapaJogoAdminPresenter presenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeMapaJogoAdmin = repositorioDeMapaJogoAdmin;
        this.presenter = presenter;
        this.mapaJogoMapper = Mappers.getMapper(MapaJogoMapper.class);
    }

    public String executa(AlterarMapaJogoDTO alterarMapaJogoDTO, String usuarioId) throws ValidacaoException, AlterarMapaJogoException {
        Set<ConstraintViolation<AlterarMapaJogoDTO>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(alterarMapaJogoDTO);
        for (ConstraintViolation<AlterarMapaJogoDTO> violation : violations) {
            throw new ValidacaoException(violation.getMessage());
        }

        try {
            MapaJogo mapaJogo = mapaJogoMapper.formatarAlterarMapaJogoDTOParaMapaJogo(alterarMapaJogoDTO);
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
