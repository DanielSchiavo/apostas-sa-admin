package com.apostassa.infra.categoria;

import com.apostassa.aplicacao.categoria.*;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.AlterarCategoriaException;
import com.apostassa.infra.categoria.subcategoria.RepositorioDeSubCategoriaAdminComJdbcPostgres;
import com.apostassa.infra.db.InicializadorConexao;
import com.apostassa.infra.db.ProvedorConexaoJDBC;
import com.apostassa.infra.util.GeradorUUIDImpl;
import com.apostassa.infra.util.JacksonUtil;
import com.apostassa.infra.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class CategoriaAdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ProvedorConexaoJDBC provedorConexaoJDBC;
	
	private RepositorioDeCategoriaAdminComJdbcPostgres repositorio;

	private CategoriaAdminWebAdapter adapter;
	
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.provedorConexaoJDBC = InicializadorConexao.executa(request);
		this.repositorio = new RepositorioDeCategoriaAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
		this.adapter = new CategoriaAdminWebAdapter();
		super.service(request, response);
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo != null) {
			String categoriaId = pathInfo.replace("/", "");
			PegarCategoriaPorId pegarCategoria = new PegarCategoriaPorId(provedorConexaoJDBC, repositorio, adapter, new RepositorioDeSubCategoriaAdminComJdbcPostgres(provedorConexaoJDBC.getConexao()));
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
			PegarTodasCategorias pegarTodasCategorias = new PegarTodasCategorias(provedorConexaoJDBC, repositorio, adapter, new RepositorioDeSubCategoriaAdminComJdbcPostgres(provedorConexaoJDBC.getConexao()));
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
	    
	    try {
	    	String usuarioId = req.getAttribute("usuarioId").toString();
			CadastrarCategoria cadastrarCategoria = new CadastrarCategoria(provedorConexaoJDBC, repositorio, adapter, new GeradorUUIDImpl());

			CadastrarCategoriaDTO cadastrarCategoriaDTO = (CadastrarCategoriaDTO) JacksonUtil.deserializar(requestBody, CadastrarCategoriaDTO.class);
			Map<String, String> respostaCadastrarCategoria = cadastrarCategoria.executa(cadastrarCategoriaDTO, usuarioId);
	    	
	    	StringBuffer urlAtual = req.getRequestURL();
	    	urlAtual.append("/" + respostaCadastrarCategoria.get("categoriaId"));
	    	
	    	resp.setHeader("Location", urlAtual.toString());
			resp.getWriter().write(respostaCadastrarCategoria.get("mensagem"));
			resp.setStatus(HttpServletResponse.SC_CREATED);
		} catch (ValidacaoException e) {
			e.printStackTrace();
			resp.getWriter().write(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
	    
	    try {
	    	String usuarioId = req.getAttribute("usuarioId").toString();
	    	AlterarCategoria alterarCategoria = new AlterarCategoria(provedorConexaoJDBC, repositorio, adapter);

			AlterarCategoriaDTO alterarCategoriaDTO = (AlterarCategoriaDTO) JacksonUtil.deserializar(requestBody, AlterarCategoriaDTO.class);
			String respostaAlterarCategoria = alterarCategoria.executa(alterarCategoriaDTO, usuarioId);

			resp.getWriter().write(respostaAlterarCategoria);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (ValidacaoException | AlterarCategoriaException e) {
			e.printStackTrace();
			resp.getWriter().write(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
    }
}
