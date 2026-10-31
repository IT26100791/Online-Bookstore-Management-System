package com.bookstore.servlet;

import com.bookstore.dao.OrderDAO;
import com.bookstore.model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    private OrderDAO dao;

    @Override
    public void init() {
        String dir = getServletContext().getInitParameter("dataDir");
        if (dir == null || dir.isEmpty()) {
            dir = System.getProperty("user.home") + "/bookstore-data";
        }
        dao = new OrderDAO(dir);
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
                    req.getRequestDispatcher("/order-add.jsp").forward(req, resp);
                    break;
                case "edit":
                    Order o = null;
                    try {
                        o = dao.findById(Integer.parseInt(req.getParameter("id")));
                    } catch (NumberFormatException ignored) {
                    }
                    if (o == null) {
                        resp.sendRedirect("order?msg=Order+not+found");
                        return;
                    }
                    req.setAttribute("order", o);
                    req.setAttribute("msg", req.getParameter("msg"));
                    req.getRequestDispatcher("/order-edit.jsp").forward(req, resp);
                    break;
                default:
                    req.setAttribute("orders", dao.findAll());
                    req.setAttribute("msg", req.getParameter("msg"));
                    req.getRequestDispatcher("/order-list.jsp").forward(req, resp);
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
        String customerName = trim(req.getParameter("customerName"));
        String bookTitle = trim(req.getParameter("bookTitle"));
        String status = trim(req.getParameter("status"));
        if (status.isEmpty()) status = "Pending";

        try {
            switch (action == null ? "" : action) {
                case "add": {
                    int quantity;
                    double total;
                    try {
                        quantity = Integer.parseInt(trim(req.getParameter("quantity")));
                        total = Double.parseDouble(trim(req.getParameter("totalAmount")));
                    } catch (NumberFormatException e) {
                        resp.sendRedirect("order?action=new&msg=Invalid+quantity+or+amount");
                        return;
                    }
                    if (!isValid(customerName, bookTitle, quantity, total)) {
                        resp.sendRedirect("order?action=new&msg=Invalid+input");
                        return;
                    }
                    dao.add(customerName, bookTitle, quantity, total, status);
                    resp.sendRedirect("order?msg=Order+placed");
                    break;
                }
                case "update": {
                    int id;
                    int quantity;
                    double total;
                    try {
                        id = Integer.parseInt(trim(req.getParameter("orderId")));
                        quantity = Integer.parseInt(trim(req.getParameter("quantity")));
                        total = Double.parseDouble(trim(req.getParameter("totalAmount")));
                    } catch (NumberFormatException e) {
                        resp.sendRedirect("order?msg=Invalid+input");
                        return;
                    }
                    if (!isValid(customerName, bookTitle, quantity, total)) {
                        resp.sendRedirect("order?action=edit&id=" + id + "&msg=Invalid+input");
                        return;
                    }
                    boolean updated = dao.update(id, customerName, bookTitle, quantity, total, status);
                    resp.sendRedirect("order?msg=" + (updated ? "Order+updated" : "Order+not+found"));
                    break;
                }
                case "delete": {
                    int id;
                    try {
                        id = Integer.parseInt(trim(req.getParameter("orderId")));
                    } catch (NumberFormatException e) {
                        resp.sendRedirect("order?msg=Invalid+input");
                        return;
                    }
                    boolean deleted = dao.delete(id);
                    resp.sendRedirect("order?msg=" + (deleted ? "Order+deleted" : "Order+not+found"));
                    break;
                }
                default:
                    resp.sendRedirect("order");
            }
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    // commas are rejected because orders.txt is comma-separated
    private boolean isValid(String customerName, String bookTitle, int quantity, double total) {
        return !customerName.isEmpty() && !bookTitle.isEmpty()
                && !customerName.contains(",") && !bookTitle.contains(",")
                && quantity > 0 && total >= 0;
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}