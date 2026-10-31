package com.bookstore.servlet;

import com.bookstore.dao.AuthorDAO;
import com.bookstore.model.Author;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/author")
public class AuthorServlet extends HttpServlet {
    private AuthorDAO dao;

    @Override
    public void init() {
        String dir = getServletContext().getInitParameter("dataDir");
        if (dir == null || dir.trim().isEmpty() || !new File(dir).exists()) {
            File homeDir = new File(System.getProperty("user.home"), "bookstore-data");
            if (!homeDir.exists()) homeDir.mkdirs();
            dir = homeDir.getAbsolutePath();
        }
        dao = new AuthorDAO(dir);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        req.setAttribute("msg", req.getParameter("msg"));

        switch (action) {
            case "new":
                req.getRequestDispatcher("/author-form.jsp").forward(req, resp);
                break;
            case "edit":
                Author a = dao.findById(req.getParameter("id"));
                if (a == null) {
                    resp.sendRedirect("author?msg=Author+not+found");
                    return;
                }
                req.setAttribute("author", a);
                req.getRequestDispatcher("/author-form.jsp").forward(req, resp);
                break;
            default:
                req.setAttribute("authors", dao.findAll());
                req.getRequestDispatcher("/author-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String id = trim(req.getParameter("id"));
        String name = trim(req.getParameter("name"));
        String email = trim(req.getParameter("email"));

        switch (action == null ? "" : action) {
            case "add":
            case "update": {
                boolean isUpdate = "update".equals(action);
                String back = isUpdate ? "author?action=edit&id=" + enc(id) : "author?action=new";

                if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
                    resp.sendRedirect(back + "&msg=All+fields+are+required");
                    return;
                }
                if (id.contains(",") || name.contains(",") || email.contains(",")) {
                    resp.sendRedirect(back + "&msg=Commas+are+not+allowed");
                    return;
                }

                Author author = new Author(id, name, email);
                if (isUpdate) {
                    boolean ok = dao.update(author);
                    resp.sendRedirect("author?msg=" + (ok ? "Author+updated" : "Author+not+found"));
                } else {
                    boolean ok = dao.add(author);
                    resp.sendRedirect(ok ? "author?msg=Author+added" : back + "&msg=Author+ID+already+exists");
                }
                break;
            }
            case "delete":
                boolean deleted = dao.delete(id);
                resp.sendRedirect("author?msg=" + (deleted ? "Author+deleted" : "Author+not+found"));
                break;
            default:
                resp.sendRedirect("author");
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String enc(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }
}
