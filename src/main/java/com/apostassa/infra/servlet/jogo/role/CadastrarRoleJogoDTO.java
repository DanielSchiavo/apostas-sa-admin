package com.apostassa.infra.servlet.jogo.role;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastrarRoleJogoDTO {

    @NotBlank(message = "Você precisa informar o nome da role")
    private String nome;

    private String descricao;

    private String icone;
}
