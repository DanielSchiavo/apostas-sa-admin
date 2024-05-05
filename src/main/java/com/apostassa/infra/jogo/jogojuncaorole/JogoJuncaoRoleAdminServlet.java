package com.apostassa.infra.jogo.jogojuncaorole;

import com.apostassa.aplicacao.jogo.jogojuncaorole.AdicionarRoleAUmJogo;
import com.apostassa.aplicacao.jogo.jogojuncaorole.RemoverRoleDeUmJogo;
import com.apostassa.dominio.jogo.jogojuncaorole.AdicionarJogoJuncaoRoleException;
import com.apostassa.dominio.jogo.jogojuncaorole.RemoverJogoJuncaoRoleException;
import com.apostassa.infra.db.InicializadorConexao;
import com.apostassa.infra.db.ProvedorConexaoJDBC;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JogoJuncaoRoleAdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProvedorConexaoJDBC provedorConexaoJDBC;

    private RepositorioDeJogoJuncaoRoleAdminComJdbcPostgres repositorio;

    private JogoJuncaoRoleWebAdapter adapter;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.provedorConexaoJDBC = InicializadorConexao.executa(request);
        this.repositorio = new RepositorioDeJogoJuncaoRoleAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
        this.adapter = new JogoJuncaoRoleWebAdapter();
        super.service(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] originalPath = req.getAttribute("originalPath").toString().split("/");
        String usuarioId = req.getAttribute("usuarioId").toString();

        String roleJogoId = originalPath[3];
        String jogoId = originalPath[1];

        try {
            AdicionarRoleAUmJogo adicionarRoleAUmJogo = new AdicionarRoleAUmJogo(provedorConexaoJDBC, repositorio, adapter);
            String respostaAdicionarRoleAUmJogo = adicionarRoleAUmJogo.executa(roleJogoId, jogoId, usuarioId);

            resp.setContentType("text/plain");
            resp.getWriter().write(respostaAdicionarRoleAUmJogo);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (AdicionarJogoJuncaoRoleException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] originalPath = req.getAttribute("originalPath").toString().split("/");
        String usuarioId = req.getAttribute("usuarioId").toString();

        String roleJogoId = originalPath[3];
        String jogoId = originalPath[1];

        try {
            RemoverRoleDeUmJogo removerRoleDeUmJogo = new RemoverRoleDeUmJogo(provedorConexaoJDBC, repositorio, adapter);
            String respostaRemoverRoleDeUmJogo = removerRoleDeUmJogo.executa(roleJogoId, jogoId);

            resp.setContentType("text/plain");
            resp.getWriter().write(respostaRemoverRoleDeUmJogo);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (RemoverJogoJuncaoRoleException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());
        }
    }
}
