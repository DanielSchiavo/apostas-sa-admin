package com.apostassa.aplicacao.categoria.subcategoria;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PegarTodasSubCategorias {

	private RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria;
	
	private RepositorioDeCategoriaAdmin repositorioDeCategoria;
	
	public PegarTodasSubCategorias(RepositorioDeSubCategoriaAdmin repositorioDeSubCategoria, RepositorioDeCategoriaAdmin repositorioDeCategoria) {
		this.repositorioDeSubCategoria = repositorioDeSubCategoria;
		this.repositorioDeCategoria = repositorioDeCategoria;
	}
	
	public String executa() throws ValidacaoException, JsonProcessingException{
		List<SubCategoria> subCategorias = repositorioDeSubCategoria.pegarTodasSubCategorias();
		
		Map<String, Categoria> map = new HashMap<>();
		subCategorias.forEach(subCategoria -> {
			String categoriaId = subCategoria.getCategoria().getId().toString();
			boolean containsKey = map.containsKey(categoriaId);
			Categoria categoria = null;
			if (containsKey) {
				categoria = map.get(categoriaId);
			}
			else {
				try {
					categoria = repositorioDeCategoria.pegarCategoriaPorId(categoriaId);
				} catch (ValidacaoException e) {
					e.printStackTrace();
				}
				map.put(categoriaId, categoria);
			}
			subCategoria.setCategoria(categoria);
		});
		
		repositorioDeSubCategoria.commitarTransacao();
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configOverride(LocalDateTime.class).setFormat(JsonFormat.Value.forPattern("dd/MM/yyyy hh:MM:ss"));
		return objectMapper.writeValueAsString(subCategorias);
	}
	
}
