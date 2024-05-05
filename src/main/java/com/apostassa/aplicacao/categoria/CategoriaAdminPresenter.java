package com.apostassa.aplicacao.categoria;

import com.apostassa.dominio.categoria.Categoria;

import java.util.List;
import java.util.Map;

public interface CategoriaAdminPresenter {

    public Map<String, String> respostaCadastrarCategoria(Categoria categoria);

    public String respostaDeletarCategoria();

    public String respostaAlterarCategoria(Categoria categoria);

    public String respostaPegarCategoriaPorId(Categoria categoria);

    public String respostaPegarTodasCategoria(List<Categoria> categorias);

}
