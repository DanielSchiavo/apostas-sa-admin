package com.apostassa.infra.categoria.subcategoria;

import com.apostassa.aplicacao.categoria.subcategoria.*;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.AlterarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.DeletarSubCategoriaException;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;
import com.apostassa.infra.categoria.RepositorioDeCategoriaAdminComJdbcPostgres;
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

public class SubCategoriaAdminServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private RepositorioDeSubCategoriaAdminComJdbcPostgres repositorio;
	
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		DataSource pool = (DataSource) getServletContext().getAttribute("my-pool");
            this.repositorio = new RepositorioDeSubCategoriaAdminComJdbcPostgres(pool.getConnection());
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
			String idSubCategoria = pathInfo.replace("/", "");
			PegarSubCategoriaPorId pegarSubCategoria = new PegarSubCategoriaPorId(repositorio, new RepositorioDeCategoriaAdminComJdbcPostgres(repositorio.getConnection()));
			try {
				String jsonSubCategoria = pegarSubCategoria.executa(idSubCategoria);
				
				resp.getWriter().write(jsonSubCategoria);
				resp.setStatus(HttpServletResponse.SC_OK);
			} catch (ValidacaoException e) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(e.getMessage());

			}
        }
		else {
			PegarTodasSubCategorias pegarTodasSubCategorias = new PegarTodasSubCategorias(repositorio, new RepositorioDeCategoriaAdminComJdbcPostgres(repositorio.getConnection()));
			try {
				String jsonTodasSubCategorias = pegarTodasSubCategorias.executa();
				
				resp.getWriter().write(jsonTodasSubCategorias);
				resp.setStatus(HttpServletResponse.SC_OK);
			} catch (ValidacaoException e) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(e.getMessage());

			}
		}
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
    	String usuarioId = (String) req.getAttribute("usuarioId");
	    try {
			CadastrarSubCategoria cadastrarSubCategoria = new CadastrarSubCategoria(repositorio, new GeradorUUIDImpl());
			CadastrarSubCategoriaDTO cadastrarSubCategoriaDTO = deserializarCadastrarSubCategoriaDTO(requestBody);
			
			String subCategoriaId = cadastrarSubCategoria.executa(cadastrarSubCategoriaDTO, usuarioId);
	    	
	    	StringBuffer urlAtual = req.getRequestURL();
	    	urlAtual.append("/" + subCategoriaId);
	    	
	    	resp.setHeader("Location", urlAtual.toString());
			resp.getWriter().write("Sub-Categoria cadastrada com sucesso!");
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
	    	AlterarSubCategoria alterarCategoria = new AlterarSubCategoria(repositorio);
	    	AlterarSubCategoriaDTO alterarSubCategoriaDTO = deserializarAlterarSubCategoriaDTO(requestBody);
			
			alterarCategoria.executa(alterarSubCategoriaDTO, usuarioId);
	    	
			resp.getWriter().write("Sub-Categoria alterada com sucesso!");
			resp.setStatus(HttpServletResponse.SC_OK);
			return;
		} catch (AutenticacaoException | ValidacaoException | AlterarSubCategoriaException e) {
			e.printStackTrace();
			resp.getWriter().write(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
		if (pathInfo != null) {
			String idCategoria = pathInfo.replace("/", "");
			try {
				DeletarSubCategoria deletarSubCategoria = new DeletarSubCategoria(repositorio);
				deletarSubCategoria.executa(idCategoria);
				
				resp.getWriter().write("Sub-Categoria deletada com sucesso!");
				resp.setStatus(HttpServletResponse.SC_OK);
			} catch (AutenticacaoException | DeletarSubCategoriaException | ValidacaoException e) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(e.getMessage());

			}
        }
    }
    
	private AlterarSubCategoriaDTO deserializarAlterarSubCategoriaDTO(String requestBody) throws ValidacaoException {
		try {
		    ObjectMapper objectMapper = new ObjectMapper();
		    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    
			return objectMapper.readValue(requestBody, AlterarSubCategoriaDTO.class);
		} catch (JsonMappingException e) {
			throw new ValidacaoException(e.getCause().getMessage());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao processar json de resposta");
		}
	}

	private CadastrarSubCategoriaDTO deserializarCadastrarSubCategoriaDTO(String requestBody) throws ValidacaoException {
		try {
		    ObjectMapper objectMapper = new ObjectMapper();
		    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    
			return objectMapper.readValue(requestBody, CadastrarSubCategoriaDTO.class);
		} catch (JsonMappingException e) {
			throw new ValidacaoException(e.getCause().getMessage());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao processar json de resposta");
		}
	}
}
