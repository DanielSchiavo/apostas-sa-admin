package com.apostassa.infra.jogo.role;

import com.apostassa.aplicacao.jogo.role.*;
import com.apostassa.dominio.ValidacaoException;
import com.apostassa.dominio.jogo.role.AlterarRoleJogoException;
import com.apostassa.dominio.jogo.role.DeletarRoleJogoException;
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
import java.io.Serial;
import java.util.Map;

public class RoleJogoAdminServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private ProvedorConexaoJDBC provedorConexaoJDBC;

    private RepositorioDeRoleJogoAdminComJdbcPostgres repositorio;

    private RoleJogoWebAdapter adapter;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.provedorConexaoJDBC = InicializadorConexao.executa(request);
        this.repositorio = new RepositorioDeRoleJogoAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
        this.adapter = new RoleJogoWebAdapter();
        super.service(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String originalPath = req.getAttribute("originalPath").toString();
        String[] splitOriginalPath = originalPath.split("/");

        // /jogo/{jogoId}/role/{roleId}
        // quando se dá split em uma string por exemplo: "/jogo" se tem dois elementos no array, um string vazio e o outro contendo "jogo"
        // esse primeiro if está verificando se o "role" está no lugar do {jogoId} da url de exemplo mostrada acima
        // porque se o requisitante quiser pegar um "role" especifico ele não precisa informar o {jogoId}
        // e se ele quiser pegar todas as "roles" de um jogo ele não precisa informar o {roleId}, portanto ficará: /jogo/{jogoId}/role
        if (splitOriginalPath[1].contains("role")) {
            String roleJogoId = splitOriginalPath[2];
            PegarRoleJogoPorId pegarRoleJogoPorId = new PegarRoleJogoPorId(provedorConexaoJDBC, repositorio, adapter);
            try {
                String respostaPegarRoleJogoPorId = pegarRoleJogoPorId.executa(roleJogoId);

                resp.setContentType("application/json");
                resp.getWriter().write(respostaPegarRoleJogoPorId);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (ValidacaoException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(e.getMessage());

            }
        }

        if (splitOriginalPath[1].contains("role") && splitOriginalPath.length == 2) {
            PegarTodasRolesJogo pegarTodasRolesJogo = new PegarTodasRolesJogo(provedorConexaoJDBC, repositorio, adapter);
            try {
                String respostaPegarTodasRolesJogo = pegarTodasRolesJogo.executa();

                resp.setContentType("application/json");
                resp.getWriter().write(respostaPegarTodasRolesJogo);
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
            CadastrarRoleJogo cadastrarRoleJogo = new CadastrarRoleJogo(provedorConexaoJDBC, repositorio, adapter, new GeradorUUIDImpl());

            CadastrarRoleJogoDTO cadastrarRoleJogoDTO = (CadastrarRoleJogoDTO) JacksonUtil.deserializar(corpoDaRequisicao, CadastrarRoleJogoDTO.class);
            Map<String, String> respostaCadastrarRoleJogo = cadastrarRoleJogo.executa(cadastrarRoleJogoDTO, usuarioId);

            StringBuffer urlAtual = req.getRequestURL();
            urlAtual.append("/jogo/role/" + respostaCadastrarRoleJogo.get("roleJogoId"));
            resp.setHeader("Location", urlAtual.toString());

            resp.setContentType("text/plain");
            resp.getWriter().write(respostaCadastrarRoleJogo.get("mensagem"));
            resp.setStatus(HttpServletResponse.SC_CREATED);

        } catch (ValidacaoException e) {
            e.printStackTrace();
            resp.getWriter().write(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String corpoDaRequisicao = req.getAttribute("corpoDaRequisicao").toString();
        try {
            String usuarioId = req.getAttribute("usuarioId").toString();
            System.out.println("USUARIO " + usuarioId);
            AlterarRoleJogo alterarRoleJogo = new AlterarRoleJogo(provedorConexaoJDBC, repositorio, adapter);

            AlterarRoleJogoDTO alterarRoleJogoDTO = (AlterarRoleJogoDTO) JacksonUtil.deserializar(corpoDaRequisicao, AlterarRoleJogoDTO.class);
            String respostaAlterarRoleJogo = alterarRoleJogo.executa(alterarRoleJogoDTO, usuarioId);

            resp.setContentType("text/plain");
            resp.getWriter().write(respostaAlterarRoleJogo);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (ValidacaoException | AlterarRoleJogoException e) {
            e.printStackTrace();
            resp.getWriter().write(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] splitOriginalPath = req.getAttribute("originalPath").toString().split("/");
        if (splitOriginalPath[1].contains("role")) {
            String roleJogoId = splitOriginalPath[2];
            try {
                DeletarRoleJogo deletarRoleJogo = new DeletarRoleJogo(provedorConexaoJDBC, repositorio, adapter);
                String respostaDeletarRoleJogo = deletarRoleJogo.executa(roleJogoId);

                resp.setContentType("text/plain");
                resp.getWriter().write(respostaDeletarRoleJogo);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (ValidacaoException | DeletarRoleJogoException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(e.getMessage());

            }
        }
    }
}
