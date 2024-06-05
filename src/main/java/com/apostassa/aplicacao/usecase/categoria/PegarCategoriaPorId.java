package com.apostassa.aplicacao.usecase.categoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.categoria.CategoriaAdminPresenter;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.aplicacao.gateway.categoria.RepositorioDeCategoriaAdmin;
import com.apostassa.aplicacao.gateway.usuario.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class PegarCategoriaPorId {

    private final ProvedorConexao provedorConexao;

    private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;

    private final CategoriaAdminPresenter presenter;

    private final RepositorioDeCategoriaAdmin repositorioDeCategoria;

    public PegarCategoriaPorId(ProvedorConexao provedorConexao, RepositorioDeCategoriaAdmin repositorioDeCategoria, CategoriaAdminPresenter presenter, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeCategoria = repositorioDeCategoria;
        this.presenter = presenter;
        this.repositorioDeSubCategoria = repositorioDeSubCategoria;
    }

    public String executa(String categoriaId) throws JsonProcessingException, ValidacaoException {
        try {
            Categoria categoria = repositorioDeCategoria.pegarCategoriaPorId(categoriaId);

            List<SubCategoria> subCategorias = repositorioDeSubCategoria.pegarTodasSubCategoriasPorCategoriaId(categoria.getId().toString());

            subCategorias.forEach(categoria::adicionarSubCategoria);

            provedorConexao.commitarTransacao();

            return presenter.respostaPegarCategoriaPorId(categoria);
        } catch (ValidacaoException e) {
            provedorConexao.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
