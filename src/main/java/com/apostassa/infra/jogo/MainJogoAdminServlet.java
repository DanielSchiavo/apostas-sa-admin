package com.apostassa.infra.jogo;

import com.apostassa.infra.util.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MainJogoAdminServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();

            req.setAttribute("originalPath", pathInfo);

            String corpoDaRequisicao = Util.pegarJsonCorpoDaRequisicao(req);
            req.setAttribute("corpoDaRequisicao", corpoDaRequisicao);

            //JOGO - PADRAO = /jogo || /jogo/{jogoId}
            if (pathInfo == null || pathInfo.matches("^/[0-9a-fA-F\\-]+$")) {
                System.out.println("AQUI");
                req.getRequestDispatcher("/end-jogo").forward(req, resp);
            }

            //ROLE JOGO - PADRAO = /jogo/role || /jogo/role/{roleId}
            else if (pathInfo.matches("^/role") || pathInfo.matches("^/role/[0-9a-fA-F\\-]+$")) {
                System.out.println("TRUE");
                req.getRequestDispatcher("/role-jogo").forward(req, resp);
            }

            //JOGO JUNCAO ROLE - PADRAO = /jogo/{id}/role/{id}
            else if (pathInfo.matches("^/[0-9a-fA-F\\-]+/role/[0-9a-fA-F\\-]+$")) {
                System.out.println("TRUE");
                req.getRequestDispatcher("/jogo-juncao-role").forward(req, resp);
            }

            //MAPA JOGO - PADRAO = /jogo/{jogoId}/mapa || /jogo/{jogoId}/mapa/{mapaId} || /jogo/mapa
            else if (pathInfo.matches("^/[0-9a-fA-F\\-]+/mapa/[0-9a-fA-F\\-]+$") || pathInfo.matches("^/[0-9a-fA-F\\-]+/mapa") || pathInfo.matches("^/mapa")) {
                System.out.println("TRUE");
                req.getRequestDispatcher("/mapa-jogo").forward(req, resp);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
