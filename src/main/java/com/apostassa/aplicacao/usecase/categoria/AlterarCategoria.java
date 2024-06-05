package com.apostassa.aplicacao.usecase.categoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.gateway.categoria.CategoriaAdminPresenter;
import com.apostassa.aplicacao.gateway.categoria.CategoriaMapper;
import com.apostassa.aplicacao.gateway.categoria.RepositorioDeCategoriaAdmin;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.AlterarCategoriaException;
import com.apostassa.dominio.categoria.Categoria;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

public class AlterarCategoria {

    private ProvedorConexao provedorConexao;

    private final RepositorioDeCategoriaAdmin repositorioDeCategoria;

    private final CategoriaMapper categoriaMapper;

    private final CategoriaAdminPresenter presenter;

    public AlterarCategoria(ProvedorConexao provedorConexao, RepositorioDeCategoriaAdmin repositorioDeCategoria, CategoriaAdminPresenter presenter) {
        this.provedorConexao = provedorConexao;
        this.repositorioDeCategoria = repositorioDeCategoria;
        this.presenter = presenter;
        this.categoriaMapper = Mappers.getMapper(CategoriaMapper.class);
    }

    public String executa(Categoria categoria, String usuarioCpf) throws AlterarCategoriaException, ValidacaoException {
        try {
            categoria.setDataEHoraUltimaAlteracao(LocalDateTime.now());
            categoria.setAlteradorPorUsuarioCpf(usuarioCpf);

            repositorioDeCategoria.verificarSeNomeCategoriaJaExiste(categoria.getNome());

            repositorioDeCategoria.alterarCategoria(categoria);

            provedorConexao.commitarTransacao();

            return presenter.respostaAlterarCategoria(categoria);
        } catch (ValidacaoException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } catch (AlterarCategoriaException e) {
            e.printStackTrace();
            provedorConexao.rollbackTransacao();
            throw new AlterarCategoriaException(e.getMessage());
        } finally {
            provedorConexao.fecharConexao();
        }
    }
}
