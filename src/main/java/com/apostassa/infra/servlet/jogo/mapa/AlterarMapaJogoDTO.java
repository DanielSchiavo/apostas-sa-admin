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
public class AlterarMapaJogoDTO {

    @NotBlank(message = "Você deve informar o ID da role que será alterada")
    private String mapaJogoId;

    private String nome;

    private String imagem;

    private String jogoId;

    private Boolean ativo;
}
