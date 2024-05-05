package com.apostassa.aplicacao.jogo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlterarJogoDTO {

    @NotBlank(message = "Você deve informar o ID do jogo que será alterado")
    private String jogoId;

    private String nome;

    private String icone;

    private String descricao;

    private String imagem;

    private String ativo;

    private String subCategoriaId;
}
