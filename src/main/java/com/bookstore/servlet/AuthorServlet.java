package com.bookstore.servlet;

import com.bookstore.dao.AuthorDAO;
import com.bookstore.model.Author;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/author")
public class AuthorServlet extends HttpServlet {
    private AuthorDAO dao;

    @Override
    public void init() {
        // same data folder logic as the leader's UserServlet
        String dir = getServletContext().getInitParameter("dataDir");
        if (dir == null || dir.isEmpty()) {
            dir = System.getProperty("user.home") + "/bookstore-data";
        }
        dao = new AuthorDAO(dir);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "new":
                    req.setAttribute("msg", req.getParameter("msg"));
                    req.getRequestDispatcher("/author-add.jsp").forward(req, resp);
                    break;
                case "edit":
                    Author a = dao.findById(req.getParameter("id"));
                    if (a == null) {
                        resp.sendRedirect("author?msg=Author+not+found");
                        return;
                    }
                    req.setAttribute("author", a);
                    req.getRequestDispatcher("/author-edit.jsp").forward(req, resp);
                    break;
                default:
                    req.setAttribute("authors", dao.findAll());
                    req.setAttribute("msg", req.getParameter("msg"));
                    req.getRequestDispatcher("/author-list.jsp").forward(req, resp);
            }
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action  = req.getParameter("action");
        String id      = trim(req.getParameter("authorId"));
        String name    = trim(req.getParameter("name"));
        String country = trim(req.getParameter("country"));

        try {
            switch (action == null ? "" : action) {
                case "add":
                    if (invalid(id) || invalid(name) || invalid(country)) {
                        resp.sendRedirect("author?action=new&msg=Invalid+input");
                        return;
                    }
                    boolean added = dao.add(new Author(id, name, country));
                    resp.sendRedirect("author?msg=" + (added ? "Author+added" : "Author+ID+already+exists"));
                    break;
                case "update":
                    if (invalid(name) || invalid(country)) {
                        resp.sendRedirect("author?msg=Invalid+input");
                        return;
                    }
                    boolean updated = dao.update(id, name, country);
                    resp.sendRedirect("author?msg=" + (updated ? "Author+updated" : "Author+not+found"));
                    break;
                case "delete":
                    boolean deleted = dao.delete(id);
                    resp.sendRedirect("author?msg=" + (deleted ? "Author+deleted" : "Author+not+found"));
                    break;
                default:
                    resp.sendRedirect("author");
            }
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    // empty values or commas would break the comma-separated text file
    private boolean invalid(String s) {
        return s.isEmpty() || s.contains(",");
    }
}
