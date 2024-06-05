package com.apostassa.infra.gateway.categoria;

import com.apostassa.aplicacao.gateway.categoria.CategoriaAdminPresenter;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.infra.util.JacksonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriaAdminWebPresenter implements CategoriaAdminPresenter {
    @Override
    public Map<String, String> respostaCadastrarCategoria(Categoria categoria) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("categoriaId", categoria.getId().toString());
        resposta.put("mensagem", "Categoria cadastrada com sucesso!");
        return resposta;
    }

    @Override
    public String respostaDeletarCategoria() {
        return "Categoria deletada com sucesso!";
    }

    @Override
    public String respostaAlterarCategoria(Categoria categoria) {
        return "Categoria alterada com sucesso!";
    }

    @Override
    public String respostaPegarCategoriaPorId(Categoria categoria) {
        return JacksonUtil.serializador(categoria);
    }

    @Override
    public String respostaPegarTodasCategoria(List<Categoria> categorias) {
        return JacksonUtil.serializador(categorias);
    }
}
