package com.apostassa.aplicacao.jogo.role;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.GeradorUUID;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.role.RepositorioDeRoleJogoAdmin;
import com.apostassa.dominio.jogo.role.RoleJogo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CadastrarRoleJogo {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeRoleJogoAdmin repositorioDeRoleJogoAdmin;

    private final RoleJogoAdminPresenter presenter;

    private final GeradorUUID geradorUuid;

    private final RoleJogoMapper roleJogoMapper;

    public CadastrarRoleJogo(ProvedorConexao provedorConexao, RepositorioDeRoleJogoAdmin repositorioDeRoleJogoAdmin, RoleJogoAdminPresenter presenter, GeradorUUID geradorUuid) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeRoleJogoAdmin = repositorioDeRoleJogoAdmin;
        this.presenter = presenter;
        this.geradorUuid = geradorUuid;
        this.roleJogoMapper = Mappers.getMapper(RoleJogoMapper.class);
    }

    public Map<String, String> executa(CadastrarRoleJogoDTO cadastrarRoleJogoDTO, String usuarioId) throws ValidacaoException {
        Set<ConstraintViolation<CadastrarRoleJogoDTO>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(cadastrarRoleJogoDTO);
        for (ConstraintViolation<CadastrarRoleJogoDTO> violation : violations) {
            throw new ValidacaoException(violation.getMessage());
        }

        try {
            RoleJogo roleJogo = roleJogoMapper.formatarCadastrarRoleJogoDTOParaRoleJogo(cadastrarRoleJogoDTO);
            roleJogo.setId(geradorUuid.gerarUUID());
            roleJogo.setDataEHoraCriacao(LocalDateTime.now());
            roleJogo.setCriadoPor(UUID.fromString(usuarioId));

            boolean nomeRoleJogoJaExiste = repositorioDeRoleJogoAdmin.verificarSeNomeRoleJogoJaExiste(roleJogo.getNome());
            if (nomeRoleJogoJaExiste) {
                throw new ValidacaoException("Já existe uma role com esse nome!");
            }

            repositorioDeRoleJogoAdmin.cadastrarRoleJogo(roleJogo);

            provedorConexao.commitarTransacao();

            return presenter.respostaCadastrarRoleJogo(roleJogo);
        } catch (ValidacaoException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
