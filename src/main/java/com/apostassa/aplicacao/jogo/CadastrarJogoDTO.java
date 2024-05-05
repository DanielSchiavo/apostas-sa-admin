package com.apostassa.aplicacao.jogo;

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
public class CadastrarJogoDTO {

	@NotBlank(message = "Você deve informar o nome do jogo")
	private String nome;

	@NotBlank(message = "Você deve informar o icone do jogo")
	private String icone;
	
	private String descricao;
	
	private String imagem;

	@NotNull(message = "Você deve informar se o jogo está ativo ou não")
	private Boolean ativo;

	@NotBlank(message = "Você deve informar o ID da sub-categoria")
	private String subCategoriaId;
}
