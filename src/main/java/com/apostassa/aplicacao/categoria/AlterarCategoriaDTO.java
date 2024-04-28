package com.apostassa.aplicacao.categoria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlterarCategoriaDTO {
	
	private String categoriaId;
	
	private String nome;
	
	private String icone;
	
	private String ativo;

}
