package com.apostassa.aplicacao.jogo;


import com.apostassa.aplicacao.CriptografiaSenha;
import com.apostassa.dominio.GeradorUUID;
import com.apostassa.dominio.usuario.RepositorioDeUsuarioUser;

public class CadastrarJogo {

	private RepositorioDeUsuarioUser repositorioDeUsuario;
	
	private CriptografiaSenha criptografiaSenha;
	
	private GeradorUUID geradorUuid;
	
	public CadastrarJogo(RepositorioDeUsuarioUser repositorioDeUsuario, CriptografiaSenha criptografiaSenha, GeradorUUID geradorUuid) {
		this.repositorioDeUsuario = repositorioDeUsuario;
		this.criptografiaSenha = criptografiaSenha;
		this.geradorUuid = geradorUuid;
	}
	
	public void executa(CadastrarJogoDTO cadastrarJogoDTO) {
		
	}
	
}
