package com.apostassa.aplicacao.gateway.categoria.subcategoria;

import com.apostassa.dominio.categoria.subcategoria.SubCategoria;

import java.util.List;
import java.util.Map;

public interface SubCategoriaAdminPresenter {

    public Map<String, String> respostaCadastrarSubCategoria(SubCategoria subCategoria);

    public String respostaDeletarSubCategoria();

    public String respostaAlterarSubCategoria(SubCategoria subCategoria);

    public String respostaPegarSubCategoriaPorId(SubCategoria subCategoria);

    public String respostaPegarTodasSubCategorias(List<SubCategoria> subCategorias);
}
