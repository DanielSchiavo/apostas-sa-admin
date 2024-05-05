package com.apostassa.aplicacao.jogo.role;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlterarRoleJogoDTO {

    @NotBlank(message = "Você deve informar o ID da role que será alterada")
    private String roleJogoId;

    private String nome;

    private String descricao;

    private String icone;

    private String ativo;
}
