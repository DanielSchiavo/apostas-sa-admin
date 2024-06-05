package com.apostassa.infra.servlet.jogo;

import com.apostassa.aplicacao.gateway.jogo.JogoMapper;
import com.apostassa.aplicacao.usecase.jogo.*;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.AlterarJogoException;
import com.apostassa.dominio.jogo.DeletarJogoException;
import com.apostassa.dominio.jogo.Jogo;
import com.apostassa.infra.gateway.categoria.subcategoria.RepositorioDeSubCategoriaAdminComJdbcPostgres;
import com.apostassa.infra.db.InicializadorConexao;
import com.apostassa.infra.db.ProvedorConexaoJDBC;
import com.apostassa.infra.gateway.jogo.JogoAdminWebPresenter;
import com.apostassa.infra.gateway.jogo.RepositorioDeJogoAdminComJdbcPostgres;
import com.apostassa.infra.gateway.jogo.jogojuncaorole.RepositorioDeJogoJuncaoRoleAdminComJdbcPostgres;
import com.apostassa.infra.gateway.jogo.mapa.RepositorioDeMapaJogoAdminComJdbcPostgres;
import com.apostassa.infra.gateway.jogo.role.RepositorioDeRoleJogoAdminComJdbcPostgres;
import com.apostassa.infra.util.GeradorUUIDImpl;
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

@WebServlet("/end-jogo")
public class JogoAdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ProvedorConexaoJDBC provedorConexaoJDBC;
	
	private RepositorioDeJogoAdminComJdbcPostgres repositorio;

	private JogoAdminWebPresenter adapter;

	private JogoMapper jogoMapper;
	
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.provedorConexaoJDBC = InicializadorConexao.executa(request);
		this.repositorio = new RepositorioDeJogoAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
		this.adapter = new JogoAdminWebPresenter();
		this.jogoMapper = Mappers.getMapper(JogoMapper.class);
		super.service(request, response);
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		var repositorioRoleJogo = new RepositorioDeRoleJogoAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
		var repositorioJogoJuncaoRole = new RepositorioDeJogoJuncaoRoleAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
		var repositorioMapaJogo = new RepositorioDeMapaJogoAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
		if (pathInfo != null) {
			String[] pathInfoSplit = pathInfo.split("/");
			String jogoId = pathInfoSplit[1];
			PegarJogoPorId pegarJogoPorId = new PegarJogoPorId(provedorConexaoJDBC, repositorio, adapter, repositorioRoleJogo, repositorioJogoJuncaoRole, repositorioMapaJogo);
			try {
				String respostaPegarJogoPorId = pegarJogoPorId.executa(jogoId);

				resp.setContentType("application/json");
				resp.getWriter().write(respostaPegarJogoPorId);
				resp.setStatus(HttpServletResponse.SC_OK);
			} catch (ValidacaoException e) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(e.getMessage());

			}
		}
		else {
			PegarTodosJogos pegarTodosJogos = new PegarTodosJogos(provedorConexaoJDBC, repositorio, adapter, repositorioRoleJogo, repositorioJogoJuncaoRole, repositorioMapaJogo);
			try {
				String respostaPegarTodosJogos = pegarTodosJogos.executa();

				resp.setContentType("application/json");
				resp.getWriter().write(respostaPegarTodosJogos);
				resp.setStatus(HttpServletResponse.SC_OK);
			} catch (JsonProcessingException e) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(e.getMessage());
			}
		}
	}

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String corpoDaRequisicao = req.getAttribute("corpoDaRequisicao").toString();

	    try {
	    	String usuarioId = req.getAttribute("usuarioId").toString();

			CadastrarJogoDTO cadastrarJogoDTO = (CadastrarJogoDTO) JacksonUtil.deserializar(corpoDaRequisicao, CadastrarJogoDTO.class);
			Util.validar(cadastrarJogoDTO);
			Jogo jogo = jogoMapper.cadastrarJogoDTOParaJogo(cadastrarJogoDTO);

			CadastrarJogo cadastrarJogo = new CadastrarJogo(provedorConexaoJDBC, repositorio, adapter, new GeradorUUIDImpl(), new RepositorioDeSubCategoriaAdminComJdbcPostgres(provedorConexaoJDBC.getConexao()));
			Map<String, String> respostaCadastrarJogo = cadastrarJogo.executa(jogo, usuarioId);

			String urlAtual = req.getRequestURL().toString().replace("end-", "");
			String urlAtualComJogoId = urlAtual.concat("/" + respostaCadastrarJogo.get("jogoId"));
			resp.setHeader("Location", urlAtualComJogoId);

			resp.setContentType("text/plain");
			resp.getWriter().write(respostaCadastrarJogo.get("mensagem"));
			resp.setStatus(HttpServletResponse.SC_CREATED);
		} catch (ValidacaoException e) {
			e.printStackTrace();
			resp.getWriter().write(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestBody = Util.pegarJsonCorpoDaRequisicao(req);
	    
	    try {
	    	String usuarioId = req.getAttribute("usuarioId").toString();

			AlterarJogoDTO alterarJogoDTO = (AlterarJogoDTO) JacksonUtil.deserializar(requestBody, AlterarJogoDTO.class);
			Util.validar(alterarJogoDTO);
			Jogo jogo = jogoMapper.alterarJogoDTOParaJogo(alterarJogoDTO);

			AlterarJogo alterarJogo = new AlterarJogo(provedorConexaoJDBC, repositorio, adapter, new RepositorioDeSubCategoriaAdminComJdbcPostgres(provedorConexaoJDBC.getConexao()));
			String respostaAlterarJogo = alterarJogo.executa(jogo, usuarioId);

			resp.setContentType("text/plain");
			resp.getWriter().write(respostaAlterarJogo);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (ValidacaoException | AlterarJogoException e) {
			e.printStackTrace();
			resp.getWriter().write(e.getMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
    }

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo != null) {
			String jogoId = pathInfo.replace("/", "");
			try {
				DeletarJogo deletarJogo = new DeletarJogo(provedorConexaoJDBC, repositorio, adapter);
				String respostaDeletarJogo = deletarJogo.executa(jogoId);

				resp.setContentType("text/plain");
				resp.getWriter().write(respostaDeletarJogo);
				resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} catch (DeletarJogoException | ValidacaoException e) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write(e.getMessage());

			}
		}
	}
}
