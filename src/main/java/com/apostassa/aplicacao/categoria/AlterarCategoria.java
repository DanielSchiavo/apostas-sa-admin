package com.apostassa.aplicacao.categoria;

import com.apostassa.aplicacao.ProvedorConexao;
import com.apostassa.aplicacao.categoria.mapper.CategoriaMapper;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.AlterarCategoriaException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.dominio.categoria.RepositorioDeCategoriaAdmin;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public String executa(AlterarCategoriaDTO alterarCategoriaDTO, String usuarioId) throws AlterarCategoriaException, ValidacaoException {
        try {
            Categoria categoria = categoriaMapper.formatarAlterarCategoriaDTOParaCategoria(alterarCategoriaDTO);
            categoria.setDataEHoraUltimaAlteracao(LocalDateTime.now());
            categoria.setAlteradorPor(UUID.fromString(usuarioId));

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
