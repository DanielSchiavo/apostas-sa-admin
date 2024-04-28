package com.apostassa.aplicacao.jogo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastrarJogoDTO {

	private String nome;
	
	private String icone;
	
	private String descricao;
	
	private String imagem;
	
	private Boolean ativo;
	
	private String idSubCategoria;
}
