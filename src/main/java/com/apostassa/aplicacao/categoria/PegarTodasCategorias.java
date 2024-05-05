package com.apostassa.aplicacao.categoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.dominio.categoria.RepositorioDeCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;

import java.util.List;

public class PegarTodasCategorias {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;

    private final CategoriaAdminPresenter presenter;

    private final RepositorioDeCategoriaAdmin repositorioDeCategoria;

    public PegarTodasCategorias(ProvedorConexao provedorConexao, RepositorioDeCategoriaAdmin repositorioDeCategoria, CategoriaAdminPresenter presenter, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeCategoria = repositorioDeCategoria;
        this.presenter = presenter;
        this.repositorioDeSubCategoria = repositorioDeSubCategoria;
    }

    public String executa() {
        try {
            List<Categoria> categorias = repositorioDeCategoria.pegarTodasCategorias();

            categorias.forEach(c -> {
                String categoriaId = c.getId().toString();
                List<SubCategoria> subCategorias = repositorioDeSubCategoria.pegarTodasSubCategoriasPorCategoriaId(categoriaId);
                subCategorias.forEach(c::adicionarSubCategoria);
            });

            provedorConexao.commitarTransacao();

            return presenter.respostaPegarTodasCategoria(categorias);
        } finally {
            provedorConexao.fecharConexao();
        }

    }
}
