package com.apostassa.infra.jogo.mapa;

import com.apostassa.aplicacao.jogo.mapa.*;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.mapa.AlterarMapaJogoException;
import com.apostassa.dominio.jogo.mapa.DeletarMapaJogoException;
import com.apostassa.infra.db.InicializadorConexao;
import com.apostassa.infra.db.ProvedorConexaoJDBC;
import com.apostassa.infra.util.GeradorUUIDImpl;
import com.apostassa.infra.util.JacksonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class MapaJogoAdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProvedorConexaoJDBC provedorConexaoJDBC;

    private RepositorioDeMapaJogoAdminComJdbcPostgres repositorio;

    private MapaJogoWebAdapter adapter;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.provedorConexaoJDBC = InicializadorConexao.executa(request);
        this.repositorio = new RepositorioDeMapaJogoAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
        this.adapter = new MapaJogoWebAdapter();
        super.service(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String originalPath = req.getAttribute("originalPath").toString();
        String[] splitOriginalPath = originalPath.split("/");

        // /jogo/{jogoId}/mapa/{mapaId}
        // quando se dá split em uma string por exemplo /jogo se tem dois elementos no array, um string vazio e o outro contendo "jogo"
        // esse primeiro if está verificando se o "mapa" está no lugar do {jogoId} da url de exemplo mostrada acima
        // porque se o requisitante quiser pegar um mapa especifico ele não precisa informar o {jogoId}
        // e se ele quiser pegar todos os mapas de um jogo ele não precisa informar o {mapaId}, portanto ficará: /jogo/{jogoId}/mapa
        if (splitOriginalPath[1].contains("mapa")) {
            String mapaJogoId = splitOriginalPath[2];
            PegarMapaJogoPorId pegarMapaJogoPorId = new PegarMapaJogoPorId(provedorConexaoJDBC, repositorio, adapter);
            try {
                String respostaPegarMapaJogoPorId = pegarMapaJogoPorId.executa(mapaJogoId);

                resp.setContentType("application/json");
                resp.getWriter().write(respostaPegarMapaJogoPorId);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (ValidacaoException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(e.getMessage());

            }
        }
        if (splitOriginalPath[2].contains("mapa") && splitOriginalPath.length == 3) {
            String jogoId = splitOriginalPath[0];
            PegarTodosMapasJogoPorJogoId pegarTodosMapasJogo = new PegarTodosMapasJogoPorJogoId(provedorConexaoJDBC, repositorio, adapter);
            try {
                String respostaPegarTodosMapasJogo = pegarTodosMapasJogo.executa(jogoId);

                resp.setContentType("application/json");
                resp.getWriter().write(respostaPegarTodosMapasJogo);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (JsonProcessingException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(e.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String corpoDaRequisicao = req.getAttribute("corpoDaRequisicao").toString();
        String usuarioId = req.getAttribute("usuarioId").toString();
        try {
            CadastrarMapaJogo cadastrarMapaJogo = new CadastrarMapaJogo(provedorConexaoJDBC, repositorio, adapter, new GeradorUUIDImpl());

            CadastrarMapaJogoDTO cadastrarMapaJogoDTO = (CadastrarMapaJogoDTO) JacksonUtil.deserializar(corpoDaRequisicao, CadastrarMapaJogoDTO.class);
            Map<String, String> respostaCadastrarMapaJogo = cadastrarMapaJogo.executa(cadastrarMapaJogoDTO, usuarioId);

            StringBuffer urlAtual = req.getRequestURL();
            urlAtual.append("/jogo/mapa/" + respostaCadastrarMapaJogo.get("mapaJogoId"));
            resp.setHeader("Location", urlAtual.toString());

            resp.setContentType("text/plain");
            resp.getWriter().write(respostaCadastrarMapaJogo.get("mensagem"));
            resp.setStatus(HttpServletResponse.SC_CREATED);

        } catch (ValidacaoException e) {
            e.printStackTrace();
            resp.getWriter().write(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String corpoDaRequisicao = req.getAttribute("corpoDaRequisicao").toString();
        try {
            String usuarioId = req.getAttribute("usuarioId").toString();
            AlterarMapaJogo alterarMapaJogo = new AlterarMapaJogo(provedorConexaoJDBC, repositorio, adapter);

            AlterarMapaJogoDTO alterarMapaJogoDTO = (AlterarMapaJogoDTO) JacksonUtil.deserializar(corpoDaRequisicao, AlterarMapaJogoDTO.class);
            String respostaAlterarMapaJogo = alterarMapaJogo.executa(alterarMapaJogoDTO, usuarioId);

            resp.setContentType("text/plain");
            resp.getWriter().write(respostaAlterarMapaJogo);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (ValidacaoException | AlterarMapaJogoException e) {
            e.printStackTrace();
            resp.getWriter().write(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] splitOriginalPath = req.getAttribute("originalPath").toString().split("/");
        if (splitOriginalPath[1].contains("mapa")) {
            String mapaJogoId = splitOriginalPath[2];
            try {
                DeletarMapaJogo deletarMapaJogo = new DeletarMapaJogo(provedorConexaoJDBC, repositorio, adapter);
                String respostaDeletarRoleJogo = deletarMapaJogo.executa(mapaJogoId);

                resp.setContentType("text/plain");
                resp.getWriter().write(respostaDeletarRoleJogo);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (DeletarMapaJogoException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(e.getMessage());

            }
        }
    }
}
