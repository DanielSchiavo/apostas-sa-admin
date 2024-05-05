package com.apostassa.infra.categoria.subcategoria;

import com.apostassa.aplicacao.categoria.subcategoria.*;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.AlterarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.DeletarSubCategoriaException;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;
import com.apostassa.infra.categoria.RepositorioDeCategoriaAdminComJdbcPostgres;
import com.apostassa.infra.db.InicializadorConexao;
import com.apostassa.infra.db.ProvedorConexaoJDBC;
import com.apostassa.infra.util.GeradorUUIDImpl;
import com.apostassa.infra.util.JacksonUtil;
import com.apostassa.infra.util.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class SubCategoriaAdminServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private ProvedorConexaoJDBC provedorConexaoJDBC;
	
	private RepositorioDeSubCategoriaAdminComJdbcPostgres repositorio;

	private SubCategoriaAdminWebAdapter adapter;
	
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.provedorConexaoJDBC = InicializadorConexao.executa(request);
		this.repositorio = new RepositorioDeSubCategoriaAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
		this.adapter = new SubCategoriaAdminWebAdapter();
		super.service(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
		if (pathInfo != null) {
			String idSubCategoria = pathInfo.replace("/", "");
			PegarSubCategoriaPorId pegarSubCategoria = new PegarSubCategoriaPorId(provedorConexaoJDBC, repositorio, adapter, new RepositorioDeCategoriaAdminComJdbcPostgres(provedorConexaoJDBC.getConexao()));
			try {
				String respostaPegarSubCategoriaPorId = pegarSubCategoria.executa(idSubCategoria);
				
				resp.getWriter().write(respostaPegarSubCategoriaPorId);
				resp.setStatus(HttpServletResponse.SC_OK);
			} catch (ValidacaoException e) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(e.getMessage());

			}
        }
		else {
			PegarTodasSubCategorias pegarTodasSubCategorias = new PegarTodasSubCategorias(provedorConexaoJDBC, repositorio, adapter, new RepositorioDeCategoriaAdminComJdbcPostgres(provedorConexaoJDBC.getConexao()));
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
    	String usuarioId = req.getAttribute("usuarioId").toString();
	    try {
			CadastrarSubCategoria cadastrarSubCategoria = new CadastrarSubCategoria(provedorConexaoJDBC, repositorio, adapter, new GeradorUUIDImpl());

			CadastrarSubCategoriaDTO cadastrarSubCategoriaDTO = (CadastrarSubCategoriaDTO) JacksonUtil.deserializar(requestBody, CadastrarSubCategoriaDTO.class);
			Map<String, String> respostaCadastrarSubCategoria = cadastrarSubCategoria.executa(cadastrarSubCategoriaDTO, usuarioId);
	    	
	    	StringBuffer urlAtual = req.getRequestURL();
	    	urlAtual.append("/" + respostaCadastrarSubCategoria.get("subCategoriaId"));
	    	
	    	resp.setHeader("Location", urlAtual.toString());
			resp.getWriter().write(respostaCadastrarSubCategoria.get("mensagem"));
			resp.setStatus(HttpServletResponse.SC_CREATED);
		} catch (AutenticacaoException | ValidacaoException e) {
			e.printStackTrace();
			resp.getWriter().write(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
		String usuarioId = req.getAttribute("usuarioId").toString();

		try {
	    	AlterarSubCategoria alterarSubCategoria = new AlterarSubCategoria(provedorConexaoJDBC, repositorio, adapter);

			AlterarSubCategoriaDTO alterarSubCategoriaDTO = (AlterarSubCategoriaDTO) JacksonUtil.deserializar(requestBody, AlterarSubCategoriaDTO.class);
			String respostaAlterarSubCategoria = alterarSubCategoria.executa(alterarSubCategoriaDTO, usuarioId);

			resp.getWriter().write(respostaAlterarSubCategoria);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (AutenticacaoException | ValidacaoException | AlterarSubCategoriaException e) {
			e.printStackTrace();
			resp.getWriter().write(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
		if (pathInfo != null) {
			String idCategoria = pathInfo.replace("/", "");
			try {
				DeletarSubCategoria deletarSubCategoria = new DeletarSubCategoria(provedorConexaoJDBC, repositorio, adapter);
				String respostaDeletarSubCategoria = deletarSubCategoria.executa(idCategoria);

				resp.getWriter().write(respostaDeletarSubCategoria);
				resp.setStatus(HttpServletResponse.SC_OK);
			} catch (AutenticacaoException | DeletarSubCategoriaException | ValidacaoException e) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(e.getMessage());

			}
        }
    }
}
