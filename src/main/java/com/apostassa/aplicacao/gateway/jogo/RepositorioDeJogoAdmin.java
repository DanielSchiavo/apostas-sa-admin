package com.apostassa.aplicacao.gateway.jogo;

import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.AlterarJogoException;
import com.apostassa.dominio.jogo.DeletarJogoException;
import com.apostassa.dominio.jogo.Jogo;

import java.util.List;

public interface RepositorioDeJogoAdmin {

	public void cadastrarJogo(Jogo jogo) throws ValidacaoException;
	
	public void deletarJogo(String jogoId) throws ValidacaoException, DeletarJogoException;

	public void alterarJogo(Jogo jogo) throws AlterarJogoException;



	public Jogo pegarJogoPorId(String jogoId) throws ValidacaoException;

	public List<Jogo> pegarTodosJogos();



	public void verificarSeNomeJogoJaExiste(String nome) throws ValidacaoException;

	boolean verificarSeJogoExistePorId(String jogoId);
}
