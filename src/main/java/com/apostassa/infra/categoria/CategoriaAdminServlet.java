package com.apostassa.infra.categoria;

import com.apostassa.aplicacao.categoria.*;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.AlterarCategoriaException;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;
import com.apostassa.infra.categoria.subcategoria.RepositorioDeSubCategoriaAdminComJdbcPostgres;
import com.apostassa.infra.util.GeradorUUIDImpl;
import com.apostassa.infra.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

public class CategoriaAdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private RepositorioDeCategoriaAdminComJdbcPostgres repositorio;
	
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		DataSource pool = (DataSource) getServletContext().getAttribute("my-pool");
            this.repositorio = new RepositorioDeCategoriaAdminComJdbcPostgres(pool.getConnection());
            super.service(request, response);
        } catch (SQLException e) {
        	e.printStackTrace();
            throw new ServletException("Erro ao inicializar implementação do repositorio de usuario");
        }
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo != null) {
			String categoriaId = pathInfo.replace("/", "");
			PegarCategoriaPorId pegarCategoria = new PegarCategoriaPorId(repositorio, new RepositorioDeSubCategoriaAdminComJdbcPostgres(repositorio.getConnection()));
			try {
				String jsonCategoria = pegarCategoria.executa(categoriaId);

				resp.getWriter().write(jsonCategoria);
				resp.setStatus(HttpServletResponse.SC_OK);
			} catch (ValidacaoException e) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(e.getMessage());

			}
		}
		else {
			PegarTodasCategorias pegarTodasCategorias = new PegarTodasCategorias(repositorio, new RepositorioDeSubCategoriaAdminComJdbcPostgres(repositorio.getConnection()));
			try {
				String jsonTodasSubCategorias = pegarTodasCategorias.executa();

				resp.getWriter().write(jsonTodasSubCategorias);
				resp.setStatus(HttpServletResponse.SC_OK);
			} catch (JsonProcessingException e) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(e.getMessage());
			}
		}
	}

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
	    
	    try {
	    	String usuarioId = (String) req.getAttribute("usuarioId");
			CadastrarCategoria cadastrarCategoria = new CadastrarCategoria(repositorio, new GeradorUUIDImpl());
			CadastrarCategoriaDTO cadastrarCategoriaDTO = deserializarCadastrarCategoriaDTO(requestBody);
			
			String categoriaId = cadastrarCategoria.executa(cadastrarCategoriaDTO, usuarioId);
	    	
	    	StringBuffer urlAtual = req.getRequestURL();
	    	urlAtual.append("/" + categoriaId);
	    	
	    	resp.setHeader("Location", urlAtual.toString());
			resp.getWriter().write("Categoria cadastrada com sucesso!");
			resp.setStatus(HttpServletResponse.SC_CREATED);
			return;
		} catch (AutenticacaoException | ValidacaoException e) {
			e.printStackTrace();
			resp.getWriter().write(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
	    
	    try {
	    	String usuarioId = (String) req.getAttribute("usuarioId");
	    	AlterarCategoria alterarCategoria = new AlterarCategoria(repositorio);
			AlterarCategoriaDTO alterarCategoriaDTO = deserializarAlterarCategoriaDTO(requestBody);
			
			alterarCategoria.executa(alterarCategoriaDTO, usuarioId);
	    	
			resp.getWriter().write("Categoria alterada com sucesso!");
			resp.setStatus(HttpServletResponse.SC_OK);
			return;
		} catch (AutenticacaoException | ValidacaoException | AlterarCategoriaException e) {
			e.printStackTrace();
			resp.getWriter().write(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
    }

	private AlterarCategoriaDTO deserializarAlterarCategoriaDTO(String requestBody) throws ValidacaoException {
		try {
		    ObjectMapper objectMapper = new ObjectMapper();
		    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    
			return objectMapper.readValue(requestBody, AlterarCategoriaDTO.class);
		} catch (JsonMappingException e) {
			throw new ValidacaoException(e.getCause().getMessage());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao processar json de resposta");
		}
	}

	private CadastrarCategoriaDTO deserializarCadastrarCategoriaDTO(String requestBody) throws ValidacaoException {
		try {
		    ObjectMapper objectMapper = new ObjectMapper();
		    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    
			return objectMapper.readValue(requestBody, CadastrarCategoriaDTO.class);
		} catch (JsonMappingException e) {
			throw new ValidacaoException(e.getCause().getMessage());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao processar json de resposta");
		}
	}
	
}
