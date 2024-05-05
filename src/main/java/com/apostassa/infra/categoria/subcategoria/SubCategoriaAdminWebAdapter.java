package com.apostassa.infra.categoria.subcategoria;

import com.apostassa.aplicacao.categoria.subcategoria.SubCategoriaAdminPresenter;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.apostassa.infra.util.JacksonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCategoriaAdminWebAdapter implements SubCategoriaAdminPresenter {

    @Override
    public Map<String, String> respostaCadastrarSubCategoria(SubCategoria subCategoria) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("subCategoriaId", subCategoria.getId().toString());
        resposta.put("mensagem", "Sub Categoria cadastrada com sucesso!");
        return resposta;
    }

    @Override
    public String respostaDeletarSubCategoria() {
        return "Sub Categoria deletada com sucesso!";
    }

    @Override
    public String respostaAlterarSubCategoria(SubCategoria subCategoria) {
        return "Sub Categoria alterada com sucesso!";
    }

    @Override
    public String respostaPegarSubCategoriaPorId(SubCategoria subCategoria) {
        return JacksonUtil.serializadorNotEmpty(subCategoria);
    }

    @Override
    public String respostaPegarTodasSubCategorias(List<SubCategoria> subCategorias) {
        return JacksonUtil.serializadorNotNull(subCategorias);
    }
}
