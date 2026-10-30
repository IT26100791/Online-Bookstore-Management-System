package com.bookstore.servlet;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private UserDAO dao;

    @Override
    public void init() {
        String dir = getServletContext().getInitParameter("dataDir");
        if (dir == null || dir.isEmpty()) {
            dir = System.getProperty("user.home") + "/bookstore-data";
        }
        dao = new UserDAO(dir);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "new":
                    req.getRequestDispatcher("/register.jsp").forward(req, resp);
                    break;
                case "edit":
                    User u = dao.findByUsername(req.getParameter("username"));
                    if (u == null) {
                        resp.sendRedirect("user?msg=Username+not+found");
                        return;
                    }
                    req.setAttribute("user", u);
                    req.getRequestDispatcher("/edit.jsp").forward(req, resp);
                    break;
                default:
                    req.setAttribute("users", dao.findAll());
                    req.setAttribute("msg", req.getParameter("msg"));
                    req.getRequestDispatcher("/list.jsp").forward(req, resp);
            }
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String username = trim(req.getParameter("username"));
        String password = trim(req.getParameter("password"));
        String role = trim(req.getParameter("role"));

        try {
            switch (action == null ? "" : action) {
                case "add":
                    if (username.isEmpty() || password.isEmpty() || username.contains(",") || password.contains(",")) {
                        resp.sendRedirect("user?action=new&msg=Invalid+input");
                        return;
                    }
                    String roleToSave = "Admin".equalsIgnoreCase(role) ? "Admin" : "Customer";
                    boolean added = dao.add(new com.bookstore.model.User(username, password, roleToSave));
                    resp.sendRedirect("user?msg=" + (added ? "User+registered" : "Username+already+exists"));
                    break;
                case "update":
                    boolean updated = dao.update(username, password, role);
                    resp.sendRedirect("user?msg=" + (updated ? "User+updated" : "Username+not+found"));
                    break;
                case "delete":
                    boolean deleted = dao.delete(username);
                    resp.sendRedirect("user?msg=" + (deleted ? "User+deleted" : "Username+not+found"));
                    break;
                default:
                    resp.sendRedirect("user");
            }
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
