package com.apostassa.infra.servlet.jogo.mapa;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastrarMapaJogoDTO {

    @NotBlank(message = "Você precisa informar o nome da role")
    private String nome;

    private String imagem;

    @NotBlank(message = "Você precisa informar o ID do jogo")
    private String jogoId;
}
