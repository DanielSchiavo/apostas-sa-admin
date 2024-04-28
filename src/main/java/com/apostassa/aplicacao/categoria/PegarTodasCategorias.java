package com.apostassa.aplicacao.categoria;

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

public class PegarTodasCategorias {

    private final RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;

    private final RepositorioDeCategoriaAdmin repositorioDeCategoria;

    public PegarTodasCategorias(RepositorioDeCategoriaAdmin repositorioDeCategoria, RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria) {
        this.repositorioDeCategoria = repositorioDeCategoria;
        this.repositorioDeSubCategoria = repositorioDeSubCategoria;
    }

    public String executa() throws JsonProcessingException {
        List<Categoria> categorias = repositorioDeCategoria.pegarTodasCategorias();

        categorias.forEach(c -> {
            String categoriaId = c.getId().toString();
            List<SubCategoria> subCategorias = repositorioDeSubCategoria.pegarTodasSubCategoriasPorCategoriaId(categoriaId);
            subCategorias.forEach(c::adicionarSubCategoria);
        });

        repositorioDeCategoria.commitarTransacao();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configOverride(LocalDateTime.class).setFormat(JsonFormat.Value.forPattern("dd/MM/yyyy hh:MM:ss"));
        return objectMapper.writeValueAsString(categorias);
    }
}
