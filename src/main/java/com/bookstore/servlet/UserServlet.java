package com.bookstore.servlet;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;
import com.bookstore.util.PasswordUtil;

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
        dao = new UserDAO();   // uses DataPaths.dataDir()
    }

    /** Logged-in user from the session, or null for guests. */
    private User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        Object o = (s == null) ? null : s.getAttribute("loggedUser");
        return (o instanceof User) ? (User) o : null;
    }

    private boolean isAdmin(User u) {
        return u != null && "Admin".equalsIgnoreCase(u.getRole());
    }

    private int countAdmins() throws IOException {
        int n = 0;
        for (User u : dao.findAll()) {
            if ("Admin".equalsIgnoreCase(u.getRole())) n++;
        }
        return n;
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

        // everything below is user management: Admin only
        // (AccessFilter also blocks this, this is a second safety check)
        if (!isAdmin(currentUser(req))) {
            resp.sendRedirect("login.jsp?msg=Admin+access+required");
            return;
        }

        if ("edit".equals(action)) {
            User u = dao.findByUsername(req.getParameter("username"));
            if (u == null) {
                resp.sendRedirect("user?msg=Username+not+found");
                return;
            }
            req.setAttribute("user", u);
            req.getRequestDispatcher("/edit.jsp").forward(req, resp);
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

        // ---------- LOGIN ----------
        if ("/login".equals(path)) {
            String username = req.getParameter("username");
            String pass = req.getParameter("password");
            User u = dao.authenticate(username == null ? "" : username.trim(),
                    pass == null ? "" : pass);
            if (u != null) {
                HttpSession old = req.getSession(false);
                if (old != null) old.invalidate();          // avoid session fixation
                req.getSession(true).setAttribute("loggedUser", u);
                if (isAdmin(u)) {
                    resp.sendRedirect("user");
                } else {
                    resp.sendRedirect("welcome.jsp");
                }
            } else {
                resp.sendRedirect("login.jsp?msg=Invalid+username+or+password");
            }
            return;
        }

        User current = currentUser(req);
        boolean adminCreating = isAdmin(current);

        String username = req.getParameter("username");
        String pass = req.getParameter("password");
        String role = req.getParameter("role");

        // ---------- REGISTER / ADD ----------
        if ("add".equals(action)) {
            if (username == null || username.trim().isEmpty()
                    || pass == null || pass.isEmpty()
                    || username.contains(",")) {
                resp.sendRedirect("register.jsp?msg=Invalid+input");
                return;
            }
            // only an Admin can create another Admin; public sign-ups are Customers
            String roleToSave = (adminCreating && "Admin".equalsIgnoreCase(role)) ? "Admin" : "Customer";
            boolean ok = dao.add(new User(username.trim(), PasswordUtil.hash(pass), roleToSave));
            if (!ok) {
                resp.sendRedirect("register.jsp?msg=Username+already+exists");
            } else if (adminCreating) {
                resp.sendRedirect("user?msg=User+added+successfully");
            } else {
                resp.sendRedirect("login.jsp?msg=Account+created.+Please+login");
            }
            return;
        }

        // ---------- update / delete: Admin only ----------
        if (!adminCreating) {
            resp.sendRedirect("login.jsp?msg=Admin+access+required");
            return;
        }

        User target = dao.findByUsername(username);
        if (target == null) {
            resp.sendRedirect("user?msg=Username+not+found");
            return;
        }
        boolean targetIsAdmin = "Admin".equalsIgnoreCase(target.getRole());

        if ("update".equals(action)) {
            String newRole = "Admin".equalsIgnoreCase(role) ? "Admin" : "Customer";
            if (targetIsAdmin && "Customer".equals(newRole) && countAdmins() <= 1) {
                resp.sendRedirect("user?msg=Cannot+demote+the+last+admin");
                return;
            }
            String newHash = (pass == null || pass.isEmpty()) ? null : PasswordUtil.hash(pass);
            dao.update(username, newHash, newRole);
            resp.sendRedirect("user?msg=User+updated+successfully");

        } else if ("delete".equals(action)) {
            if (username.equalsIgnoreCase(current.getUsername())) {
                resp.sendRedirect("user?msg=You+cannot+delete+your+own+account");
                return;
            }
            if (targetIsAdmin && countAdmins() <= 1) {
                resp.sendRedirect("user?msg=Cannot+delete+the+last+admin");
                return;
            }
            dao.delete(username);
            resp.sendRedirect("user?msg=User+deleted+successfully");

        } else {
            resp.sendRedirect("user");
        }
    }
}