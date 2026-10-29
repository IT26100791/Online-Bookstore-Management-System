package com.bookstore.servlet;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
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
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username") == null ? "" : req.getParameter("username").trim();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        try {
            User user = dao.authenticate(username, password);
            if (user == null) {
                resp.sendRedirect("login?msg=Invalid+username+or+password");
                return;
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedUser", user);

            if ("Admin".equalsIgnoreCase(user.getRole())) {
                resp.sendRedirect("user");          // Admin → user dashboard
            } else {
                resp.sendRedirect("welcome.jsp");   // Customer → welcome page
            }
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }
}
