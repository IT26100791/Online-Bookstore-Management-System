package com.bookstore.servlet;

import com.bookstore.dao.OrderDAO;
import com.bookstore.model.Order;
import com.bookstore.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    private OrderDAO dao;

    @Override
    public void init() {
        dao = new OrderDAO();
    }

    private User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        Object o = (s == null) ? null : s.getAttribute("loggedUser");
        return (o instanceof User) ? (User) o : null;
    }

    private boolean isAdmin(User u) {
        return u != null && "Admin".equalsIgnoreCase(u.getRole());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = currentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=Please+login+first");
            return;
        }
        boolean admin = isAdmin(user);

        String action = req.getParameter("action");
        if (action == null) action = "list";
        req.setAttribute("msg", req.getParameter("msg"));

        switch (action) {
            case "new":
                req.getRequestDispatcher("/order-form.jsp").forward(req, resp);
                break;
            case "edit":
                if (!admin) {                       // customers cannot edit orders
                    resp.sendRedirect("order?msg=Access+denied");
                    return;
                }
                Order o = dao.findById(req.getParameter("id"));
                if (o == null) {
                    resp.sendRedirect("order?msg=Order+not+found");
                    return;
                }
                req.setAttribute("order", o);
                req.getRequestDispatcher("/order-form.jsp").forward(req, resp);
                break;
            default:
                List<Order> all = dao.findAll();
                if (!admin) {                       // customers see only their own orders
                    List<Order> mine = new ArrayList<>();
                    for (Order x : all) {
                        if (x.getCustomerName().equalsIgnoreCase(user.getUsername())) mine.add(x);
                    }
                    all = mine;
                }
                req.setAttribute("orders", all);
                req.getRequestDispatcher("/order-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        User user = currentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=Please+login+first");
            return;
        }
        boolean admin = isAdmin(user);

        String action = req.getParameter("action");
        String id = trim(req.getParameter("id"));
        String customerName = trim(req.getParameter("customerName"));
        String bookTitle = trim(req.getParameter("bookTitle"));
        String status = trim(req.getParameter("status"));

        switch (action == null ? "" : action) {
            case "add":
            case "update": {
                boolean isUpdate = "update".equals(action);

                if (isUpdate && !admin) {           // customers can only place orders
                    resp.sendRedirect("order?msg=Access+denied");
                    return;
                }
                if (!admin) {
                    // customer: name and status come from the server, not from the form
                    customerName = user.getUsername();
                    status = "Pending";
                    id = "ORD" + System.currentTimeMillis();
                }

                String back = isUpdate ? "order?action=edit&id=" + enc(id) : "order?action=new";

                if (id.isEmpty() || customerName.isEmpty() || bookTitle.isEmpty() || status.isEmpty()) {
                    resp.sendRedirect(back + "&msg=All+fields+are+required");
                    return;
                }
                if (id.contains(",") || customerName.contains(",") || bookTitle.contains(",") || status.contains(",")) {
                    resp.sendRedirect(back + "&msg=Commas+are+not+allowed");
                    return;
                }

                Order order = new Order(id, customerName, bookTitle, status);
                if (isUpdate) {
                    boolean ok = dao.update(order);
                    resp.sendRedirect("order?msg=" + (ok ? "Order+updated" : "Order+not+found"));
                } else {
                    boolean ok = dao.add(order);
                    resp.sendRedirect(ok ? "order?msg=Order+placed" : back + "&msg=Order+ID+already+exists");
                }
                break;
            }
            case "delete": {
                if (!admin) {
                    resp.sendRedirect("order?msg=Access+denied");
                    return;
                }
                boolean deleted = dao.delete(id);
                resp.sendRedirect("order?msg=" + (deleted ? "Order+deleted" : "Order+not+found"));
                break;
            }
            default:
                resp.sendRedirect("order");
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String enc(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }
}

