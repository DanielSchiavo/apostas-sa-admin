package com.apostassa.infra.servlet.categoria;

import com.apostassa.aplicacao.gateway.categoria.CategoriaMapper;
import com.apostassa.aplicacao.usecase.categoria.AlterarCategoria;
import com.apostassa.aplicacao.usecase.categoria.CadastrarCategoria;
import com.apostassa.aplicacao.usecase.categoria.PegarCategoriaPorId;
import com.apostassa.aplicacao.usecase.categoria.PegarTodasCategorias;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.AlterarCategoriaException;
import com.apostassa.dominio.categoria.Categoria;
import com.apostassa.infra.db.InicializadorConexao;
import com.apostassa.infra.db.ProvedorConexaoJDBC;
import com.apostassa.infra.gateway.categoria.CategoriaAdminWebPresenter;
import com.apostassa.infra.gateway.categoria.RepositorioDeCategoriaAdminComJdbcPostgres;
import com.apostassa.infra.gateway.categoria.subcategoria.RepositorioDeSubCategoriaAdminComJdbcPostgres;
import com.apostassa.infra.util.JacksonUtil;
import com.apostassa.infra.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.util.Map;

@WebServlet("/categoria/*")
public class CategoriaAdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ProvedorConexaoJDBC provedorConexaoJDBC;
	
	private RepositorioDeCategoriaAdminComJdbcPostgres repositorio;

	private CategoriaAdminWebPresenter adapter;
	
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.provedorConexaoJDBC = InicializadorConexao.executa(request);
		this.repositorio = new RepositorioDeCategoriaAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
		this.adapter = new CategoriaAdminWebPresenter();
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
		try {
			CategoriaMapper categoriaMapper = Mappers.getMapper(CategoriaMapper.class);
			String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
			String usuarioId = req.getAttribute("usuarioId").toString();

			CadastrarCategoriaDTO cadastrarCategoriaDTO = (CadastrarCategoriaDTO) JacksonUtil.deserializar(requestBody, CadastrarCategoriaDTO.class);
			Util.validar(cadastrarCategoriaDTO);
			Categoria categoria = categoriaMapper.cadastrarCategoriaDTOParaCategoria(cadastrarCategoriaDTO);

			CadastrarCategoria cadastrarCategoria = new CadastrarCategoria(provedorConexaoJDBC, repositorio, adapter);
			Map<String, String> respostaCadastrarCategoria = cadastrarCategoria.executa(categoria, usuarioId);
	    	
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
		try {
			CategoriaMapper categoriaMapper = Mappers.getMapper(CategoriaMapper.class);
			String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
	    	String usuarioId = req.getAttribute("usuarioId").toString();

			AlterarCategoriaDTO alterarCategoriaDTO = (AlterarCategoriaDTO) JacksonUtil.deserializar(requestBody, AlterarCategoriaDTO.class);
			Util.validar(alterarCategoriaDTO);
			Categoria categoria = categoriaMapper.alterarCategoriaDTOParaCategoria(alterarCategoriaDTO);

			AlterarCategoria alterarCategoria = new AlterarCategoria(provedorConexaoJDBC, repositorio, adapter);
			String respostaAlterarCategoria = alterarCategoria.executa(categoria, usuarioId);

			resp.getWriter().write(respostaAlterarCategoria);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (ValidacaoException | AlterarCategoriaException e) {
			e.printStackTrace();
			resp.getWriter().write(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
    }
}
