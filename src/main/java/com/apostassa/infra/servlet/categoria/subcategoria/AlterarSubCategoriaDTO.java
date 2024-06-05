package com.apostassa.infra.servlet.categoria.subcategoria;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlterarSubCategoriaDTO {

	@NotNull(message = "Você deve informar o id da sub-categoria que você está querendo alterar")
	private String subCategoriaId;
	
	private String nome;
	
	private String icone;
	
	private String ativo;
	
	private String categoriaId;
}
