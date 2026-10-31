package com.bookstore.servlet;

import com.bookstore.dao.OrderDAO;
import com.bookstore.model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    private OrderDAO dao;

    @Override
    public void init() {
        String dir = getServletContext().getInitParameter("dataDir");
        if (dir == null || dir.trim().isEmpty() || !new File(dir).exists()) {
            File homeDir = new File(System.getProperty("user.home"), "bookstore-data");
            if (!homeDir.exists()) homeDir.mkdirs();
            dir = homeDir.getAbsolutePath();
        }
        dao = new OrderDAO(dir);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        req.setAttribute("msg", req.getParameter("msg"));

        switch (action) {
            case "new":
                req.getRequestDispatcher("/order-form.jsp").forward(req, resp);
                break;
            case "edit":
                Order o = dao.findById(req.getParameter("id"));
                if (o == null) {
                    resp.sendRedirect("order?msg=Order+not+found");
                    return;
                }
                req.setAttribute("order", o);
                req.getRequestDispatcher("/order-form.jsp").forward(req, resp);
                break;
            default:
                req.setAttribute("orders", dao.findAll());
                req.getRequestDispatcher("/order-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String id = trim(req.getParameter("id"));
        String customerName = trim(req.getParameter("customerName"));
        String bookTitle = trim(req.getParameter("bookTitle"));
        String status = trim(req.getParameter("status"));

        switch (action == null ? "" : action) {
            case "add":
            case "update": {
                boolean isUpdate = "update".equals(action);
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
            case "delete":
                boolean deleted = dao.delete(id);
                resp.sendRedirect("order?msg=" + (deleted ? "Order+deleted" : "Order+not+found"));
                break;
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
