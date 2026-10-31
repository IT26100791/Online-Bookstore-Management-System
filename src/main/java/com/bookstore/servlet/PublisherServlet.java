package com.bookstore.servlet;

import com.bookstore.dao.PublisherDAO;
import com.bookstore.model.Publisher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/publisher")
public class PublisherServlet extends HttpServlet {
    private PublisherDAO dao;

    @Override
    public void init() {
        String dir = getServletContext().getInitParameter("dataDir");
        if (dir == null || dir.trim().isEmpty() || !new File(dir).exists()) {
            File homeDir = new File(System.getProperty("user.home"), "bookstore-data");
            if (!homeDir.exists()) homeDir.mkdirs();
            dir = homeDir.getAbsolutePath();
        }
        dao = new PublisherDAO(dir);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        req.setAttribute("msg", req.getParameter("msg"));

        switch (action) {
            case "new":
                req.getRequestDispatcher("/publisher-form.jsp").forward(req, resp);
                break;
            case "edit":
                Publisher p = dao.findById(req.getParameter("id"));
                if (p == null) {
                    resp.sendRedirect("publisher?msg=Publisher+not+found");
                    return;
                }
                req.setAttribute("publisher", p);
                req.getRequestDispatcher("/publisher-form.jsp").forward(req, resp);
                break;
            default:
                req.setAttribute("publishers", dao.findAll());
                req.getRequestDispatcher("/publisher-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String id = trim(req.getParameter("id"));
        String name = trim(req.getParameter("name"));
        String phone = trim(req.getParameter("phone"));

        switch (action == null ? "" : action) {
            case "add":
            case "update": {
                boolean isUpdate = "update".equals(action);
                String back = isUpdate ? "publisher?action=edit&id=" + enc(id) : "publisher?action=new";

                if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                    resp.sendRedirect(back + "&msg=All+fields+are+required");
                    return;
                }
                if (id.contains(",") || name.contains(",") || phone.contains(",")) {
                    resp.sendRedirect(back + "&msg=Commas+are+not+allowed");
                    return;
                }

                Publisher publisher = new Publisher(id, name, phone);
                if (isUpdate) {
                    boolean ok = dao.update(publisher);
                    resp.sendRedirect("publisher?msg=" + (ok ? "Publisher+updated" : "Publisher+not+found"));
                } else {
                    boolean ok = dao.add(publisher);
                    resp.sendRedirect(ok ? "publisher?msg=Publisher+added" : back + "&msg=Publisher+ID+already+exists");
                }
                break;
            }
            case "delete":
                boolean deleted = dao.delete(id);
                resp.sendRedirect("publisher?msg=" + (deleted ? "Publisher+deleted" : "Publisher+not+found"));
                break;
            default:
                resp.sendRedirect("publisher");
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String enc(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }
}
