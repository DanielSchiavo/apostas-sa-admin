package com.apostassa.aplicacao.categoria;

import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.dominio.categoria.RepositorioDeCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.RepositorioDeSubCategoriaAdmin;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.List;

public class PegarCategoriaPorId {

    private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;

    private final RepositorioDeCategoriaAdmin repositorioDeCategoria;

    public PegarCategoriaPorId(RepositorioDeCategoriaAdmin repositorioDeCategoria, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria) {
        this.repositorioDeCategoria = repositorioDeCategoria;
        this.repositorioDeSubCategoria = repositorioDeSubCategoria;
    }

    public String executa(String categoriaId) throws JsonProcessingException, ValidacaoException {
        try {
            Categoria categoria = repositorioDeCategoria.pegarCategoriaPorId(categoriaId);

            List<SubCategoria> subCategorias = repositorioDeSubCategoria.pegarTodasSubCategoriasPorCategoriaId(categoria.getId().toString());

            subCategorias.forEach(categoria::adicionarSubCategoria);

            repositorioDeCategoria.commitarTransacao();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configOverride(LocalDateTime.class).setFormat(JsonFormat.Value.forPattern("dd/MM/yyyy hh:MM:ss"));

            return objectMapper.writeValueAsString(categoria);
        } catch (ValidacaoException e) {
            repositorioDeCategoria.rollbackTransacao();
            throw new ValidacaoException(e.getMessage());
        }
    }
}
