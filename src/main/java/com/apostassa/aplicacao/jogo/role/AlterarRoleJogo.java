package com.apostassa.aplicacao.jogo.role;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.role.AlterarRoleJogoException;
import com.apostassa.dominio.jogo.role.RepositorioDeRoleJogoAdmin;
import com.apostassa.dominio.jogo.role.RoleJogo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class AlterarRoleJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeRoleJogoAdmin repositorioDeRoleJogoAdmin;

    private final RoleJogoAdminPresenter presenter;

    private final RoleJogoMapper roleJogoMapper;

    public AlterarRoleJogo(ProvedorConexao provedorConexao, RepositorioDeRoleJogoAdmin repositorioDeRoleJogoAdmin, RoleJogoAdminPresenter presenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeRoleJogoAdmin = repositorioDeRoleJogoAdmin;
        this.presenter = presenter;
        this.roleJogoMapper = Mappers.getMapper(RoleJogoMapper.class);
    }

    public String executa(AlterarRoleJogoDTO alterarRoleJogoDTO, String usuarioId) throws ValidacaoException, AlterarRoleJogoException {
        Set<ConstraintViolation<AlterarRoleJogoDTO>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(alterarRoleJogoDTO);
        for (ConstraintViolation<AlterarRoleJogoDTO> violation : violations) {
            throw new ValidacaoException(violation.getMessage());
        }

        try {
            RoleJogo roleJogo = roleJogoMapper.formatarAlterarRoleJogoDTOParaRoleJogo(alterarRoleJogoDTO);
            roleJogo.setDataEHoraUltimaAlteracao(LocalDateTime.now());
            roleJogo.setAlteradoPor(UUID.fromString(usuarioId));

            if (roleJogo.getNome() != null) {
                boolean nomeRoleJogoJaExiste = repositorioDeRoleJogoAdmin.verificarSeNomeRoleJogoJaExiste(roleJogo.getNome());
                if (nomeRoleJogoJaExiste) {
                    throw new ValidacaoException("Nome da role já existe!");
                }
            }

            repositorioDeRoleJogoAdmin.alterarRoleJogo(roleJogo);
            provedorConexao.commitarTransacao();

            return presenter.respostaAlterarRoleJogo(roleJogo);
        } catch (ValidacaoException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } catch (AlterarRoleJogoException e) {
            provedorConexao.rollbackTransacao();
            throw new AlterarRoleJogoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
