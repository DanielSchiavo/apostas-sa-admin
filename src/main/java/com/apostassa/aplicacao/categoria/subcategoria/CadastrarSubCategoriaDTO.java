package com.apostassa.aplicacao.categoria.subcategoria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastrarSubCategoriaDTO {

	private String nome;
	
	private String icone;
	
	private Boolean ativo;
	
	private String categoriaId;
}
