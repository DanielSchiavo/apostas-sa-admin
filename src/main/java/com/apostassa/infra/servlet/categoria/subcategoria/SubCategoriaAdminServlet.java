package com.apostassa.infra.servlet.categoria.subcategoria;

import com.apostassa.aplicacao.gateway.categoria.subcategoria.SubCategoriaMapper;
import com.apostassa.aplicacao.usecase.categoria.subcategoria.*;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.categoria.subcategoria.AlterarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.DeletarSubCategoriaException;
import com.apostassa.dominio.categoria.subcategoria.SubCategoria;
import com.apostassa.dominio.usuario.exceptions.AutenticacaoException;
import com.apostassa.infra.db.InicializadorConexao;
import com.apostassa.infra.db.ProvedorConexaoJDBC;
import com.apostassa.infra.gateway.categoria.RepositorioDeCategoriaAdminComJdbcPostgres;
import com.apostassa.infra.gateway.categoria.subcategoria.RepositorioDeSubCategoriaAdminComJdbcPostgres;
import com.apostassa.infra.gateway.categoria.subcategoria.SubCategoriaAdminWebPresenter;
import com.apostassa.infra.util.JacksonUtil;
import com.apostassa.infra.util.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.util.Map;

@WebServlet("/sub-categoria/*")
public class SubCategoriaAdminServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private ProvedorConexaoJDBC provedorConexaoJDBC;
	
	private RepositorioDeSubCategoriaAdminComJdbcPostgres repositorio;

	private SubCategoriaAdminWebPresenter adapter;
	
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.provedorConexaoJDBC = InicializadorConexao.executa(request);
		this.repositorio = new RepositorioDeSubCategoriaAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
		this.adapter = new SubCategoriaAdminWebPresenter();
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
	    try {
			SubCategoriaMapper subCategoriaMapper = Mappers.getMapper(SubCategoriaMapper.class);
			String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
			String usuarioId = req.getAttribute("usuarioId").toString();

			CadastrarSubCategoriaDTO cadastrarSubCategoriaDTO = (CadastrarSubCategoriaDTO) JacksonUtil.deserializar(requestBody, CadastrarSubCategoriaDTO.class);
			Util.validar(cadastrarSubCategoriaDTO);
			SubCategoria subCategoria = subCategoriaMapper.cadastrarSubCategoriaDTOParaSubCategoria(cadastrarSubCategoriaDTO);

			CadastrarSubCategoria cadastrarSubCategoria = new CadastrarSubCategoria(provedorConexaoJDBC, repositorio, adapter);
			Map<String, String> respostaCadastrarSubCategoria = cadastrarSubCategoria.executa(subCategoria, usuarioId);
	    	
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
		try {
			SubCategoriaMapper subCategoriaMapper = Mappers.getMapper(SubCategoriaMapper.class);
			String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
			String usuarioId = req.getAttribute("usuarioId").toString();

			AlterarSubCategoriaDTO alterarSubCategoriaDTO = (AlterarSubCategoriaDTO) JacksonUtil.deserializar(requestBody, AlterarSubCategoriaDTO.class);
			Util.validar(alterarSubCategoriaDTO);
			SubCategoria subCategoria = subCategoriaMapper.alterarSubCategoriaDTOParaCategoria(alterarSubCategoriaDTO);

			AlterarSubCategoria alterarSubCategoria = new AlterarSubCategoria(provedorConexaoJDBC, repositorio, adapter);
			String respostaAlterarSubCategoria = alterarSubCategoria.executa(subCategoria, usuarioId);

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
