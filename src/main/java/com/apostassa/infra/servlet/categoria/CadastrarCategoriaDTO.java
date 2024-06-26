package com.apostassa.infra.servlet.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastrarCategoriaDTO {

	@NotBlank(message = "Você deve informar o nome da categoria")
	private String nome;
	
	private String icone;
	
	@NotNull(message = "Você deve informar se essa categoria deve estar ativa ou não")
	private Boolean ativo;
}
