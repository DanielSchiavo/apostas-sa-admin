package com.apostassa.aplicacao.gateway.jogo.role;

import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.role.AlterarRoleJogoException;
import com.apostassa.dominio.jogo.role.DeletarRoleJogoException;
import com.apostassa.dominio.jogo.role.RoleJogo;

import java.util.List;

public interface RepositorioDeRoleJogoAdmin {

    public void cadastrarRoleJogo(RoleJogo role) throws ValidacaoException;

    public void alterarRoleJogo(RoleJogo role) throws AlterarRoleJogoException;

    public void deletarRoleJogo(String roleJogoId) throws DeletarRoleJogoException, ValidacaoException;

    public RoleJogo pegarRoleJogoPorId(String roleJogoId) throws ValidacaoException;

    public List<RoleJogo> pegarTodasRolesJogo();

    public List<RoleJogo> pegarTodasRolesJogoPorListaDeRoleJogoId(List<String> rolesJogoId);

    public boolean verificarSeNomeRoleJogoJaExiste(String nomeRoleJogo) throws ValidacaoException;
}
