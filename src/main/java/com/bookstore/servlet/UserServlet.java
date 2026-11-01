package com.bookstore.servlet;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/user", "/login", "/logout"})
public class UserServlet extends HttpServlet {
    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        super.init();
        // same data folder as the other modules (project root)
        dao = new UserDAO(System.getProperty("user.dir"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        String action = req.getParameter("action");

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            resp.sendRedirect("login.jsp?msg=Logged+out+successfully");
            return;
        }
        if ("/login".equals(path)) {
            resp.sendRedirect("login.jsp");
            return;
        }

        if ("new".equals(action)) {
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }
        if ("edit".equals(action)) {
            User u = dao.findByUsername(req.getParameter("username"));
            if (u == null) {
                resp.sendRedirect("user?msg=Username+not+found");
                return;
            }
            req.setAttribute("user", u);
            req.getRequestDispatcher("/edit.jsp").forward(req, resp);   // use your edit page's real name
            return;
        }

        req.setAttribute("users", dao.findAll());
        req.setAttribute("msg", req.getParameter("msg"));
        req.getRequestDispatcher("/users-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();
        String action = req.getParameter("action");

        if ("/login".equals(path)) {
            String username = req.getParameter("username");
            String pass = req.getParameter("password");
            User u = dao.authenticate(username == null ? "" : username.trim(), pass == null ? "" : pass);
            if (u != null) {
                req.getSession(true).setAttribute("loggedUser", u);   // SessionUtil reads this name
                if ("Admin".equalsIgnoreCase(u.getRole())) {
                    resp.sendRedirect("user");
                } else {
                    resp.sendRedirect("welcome.jsp");
                }
            } else {
                resp.sendRedirect("login.jsp?msg=Invalid+username+or+password");
            }
            return;
        }

        String username = req.getParameter("username");
        String pass = req.getParameter("password");
        String role = req.getParameter("role");

        if ("add".equals(action)) {
            boolean ok = dao.add(new User(username, pass, role));
            resp.sendRedirect(ok ? "user?msg=User+added+successfully"
                    : "register.jsp?msg=Username+already+exists");
        } else if ("update".equals(action)) {
            dao.update(username, pass, role);
            resp.sendRedirect("user?msg=User+updated+successfully");
        } else if ("delete".equals(action)) {
            dao.delete(username);
            resp.sendRedirect("user?msg=User+deleted+successfully");
        } else {
            resp.sendRedirect("user");
        }
    }
}
