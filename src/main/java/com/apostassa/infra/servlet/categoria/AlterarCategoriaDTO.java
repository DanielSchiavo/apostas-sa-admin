package com.apostassa.infra.servlet.categoria;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlterarCategoriaDTO {

	@NotBlank(message = "Você deve informar o ID da categoria que será alterada")
	private String categoriaId;
	
	private String nome;
	
	private String icone;
	
	private String ativo;

}
