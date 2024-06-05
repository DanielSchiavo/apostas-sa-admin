package com.apostassa.infra.servlet.jogo.jogojuncaorole;

import com.apostassa.aplicacao.usecase.jogo.jogojuncaorole.AdicionarRoleAUmJogo;
import com.apostassa.aplicacao.usecase.jogo.jogojuncaorole.RemoverRoleDeUmJogo;
import com.apostassa.dominio.jogo.jogojuncaorole.AdicionarJogoJuncaoRoleException;
import com.apostassa.dominio.jogo.jogojuncaorole.RemoverJogoJuncaoRoleException;
import com.apostassa.infra.db.InicializadorConexao;
import com.apostassa.infra.db.ProvedorConexaoJDBC;
import com.apostassa.infra.gateway.jogo.jogojuncaorole.JogoJuncaoRoleWebPresenter;
import com.apostassa.infra.gateway.jogo.jogojuncaorole.RepositorioDeJogoJuncaoRoleAdminComJdbcPostgres;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/jogo-juncao-role")
public class JogoJuncaoRoleAdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProvedorConexaoJDBC provedorConexaoJDBC;

    private RepositorioDeJogoJuncaoRoleAdminComJdbcPostgres repositorio;

    private JogoJuncaoRoleWebPresenter adapter;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.provedorConexaoJDBC = InicializadorConexao.executa(request);
        this.repositorio = new RepositorioDeJogoJuncaoRoleAdminComJdbcPostgres(provedorConexaoJDBC.getConexao());
        this.adapter = new JogoJuncaoRoleWebPresenter();
        super.service(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String usuarioId = req.getAttribute("usuarioId").toString();
            String[] originalPath = req.getAttribute("originalPath").toString().split("/");
            String roleJogoId = originalPath[3];
            String jogoId = originalPath[1];

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
        try {
            String usuarioId = req.getAttribute("usuarioId").toString();
            String[] originalPath = req.getAttribute("originalPath").toString().split("/");
            String roleJogoId = originalPath[3];
            String jogoId = originalPath[1];

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
