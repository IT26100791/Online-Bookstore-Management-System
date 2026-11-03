package com.bookstore.util;

import com.bookstore.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AccessFilter implements Filter {

    // anyone (even not logged in) may open these
    private static final List<String> PUBLIC = Arrays.asList(
            "/login", "/login.jsp", "/logout", "/register.jsp", "/index.jsp", "/");

    // customers may only READ these modules
    private static final List<String> READ_ONLY_FOR_CUSTOMER = Arrays.asList(
            "/book", "/author", "/publisher");

    // customers may read AND add (but not edit/delete)
    private static final List<String> CUSTOMER_CAN_ADD = Arrays.asList(
            "/order", "/review");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();

        // static resources and public pages
        if (PUBLIC.contains(path) || path.startsWith("/css") || path.startsWith("/js")
                || path.startsWith("/images")) {
            chain.doFilter(request, response);
            return;
        }

        // registering a new account is public (UserServlet forces the role to Customer)
        if ("/user".equals(path) && isRegister(req)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loggedUser");

        // 1. not logged in
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=Please+login+first");
            return;
        }

        // 2. admin can do everything
        if ("Admin".equalsIgnoreCase(user.getRole())) {
            chain.doFilter(request, response);
            return;
        }

        // 3. customer rules
        if (READ_ONLY_FOR_CUSTOMER.contains(path) && !isWrite(req)) {
            chain.doFilter(request, response);
            return;
        }
        if (CUSTOMER_CAN_ADD.contains(path) && (!isWrite(req) || isAdd(req))) {
            chain.doFilter(request, response);
            return;
        }
        if ("/welcome.jsp".equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 4. everything else is denied for customers
        resp.sendRedirect(req.getContextPath() + "/welcome.jsp?msg=Access+denied");
    }

    private boolean isRegister(HttpServletRequest req) {
        String action = req.getParameter("action");
        return "new".equals(action) || "add".equals(action);
    }

    /** edit / update / delete / add / new = write operations */
    private boolean isWrite(HttpServletRequest req) {
        String a = req.getParameter("action");
        return a != null && Arrays.asList("new", "add", "edit", "update", "delete").contains(a)
                || "POST".equalsIgnoreCase(req.getMethod());
    }

    private boolean isAdd(HttpServletRequest req) {
        String a = req.getParameter("action");
        return "new".equals(a) || "add".equals(a);
    }
}