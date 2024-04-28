package com.apostassa.aplicacao.categoria;

import com.apostassa.aplicacao.categoria.mapper.CategoriaMapper;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.AlterarCategoriaException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.dominio.categoria.RepositorioDeCategoriaAdmin;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlterarCategoria {

    private final RepositorioDeCategoriaAdmin repositorioDeCategoria;

    private final CategoriaMapper categoriaMapper;

    public AlterarCategoria(RepositorioDeCategoriaAdmin repositorioDeCategoria) {
        this.repositorioDeCategoria = repositorioDeCategoria;
        this.categoriaMapper = Mappers.getMapper(CategoriaMapper.class);
    }

    public void executa(AlterarCategoriaDTO alterarCategoriaDTO, String usuarioId) throws AlterarCategoriaException, ValidacaoException, AutenticacaoException {
        try {
            Categoria categoria = categoriaMapper.formatarAlterarCategoriaDTOParaCategoria(alterarCategoriaDTO);
            categoria.setDataUltimaAlteracao(LocalDateTime.now());
            categoria.setAlteradorPor(UUID.fromString(usuarioId));

            repositorioDeCategoria.verificarSeNomeCategoriaJaExiste(categoria.getNome());

            repositorioDeCategoria.alterarCategoria(categoria);

            repositorioDeCategoria.commitarTransacao();

        } catch (ValidacaoException e) {
            e.printStackTrace();
            repositorioDeCategoria.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        } catch (AlterarCategoriaException e) {
            e.printStackTrace();
            repositorioDeCategoria.rollbackTransacao();
            throw new AlterarCategoriaException(e.getMessage());
        }
    }
}
