package com.bookstore.servlet;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/book")
public class BookServlet extends HttpServlet {
    private BookDAO dao;

    @Override
    public void init() {
        String dir = getServletContext().getInitParameter("dataDir");
        if (dir == null || dir.trim().isEmpty() || !new java.io.File(dir).exists()) {
            java.io.File homeDir = new java.io.File(System.getProperty("user.home"), "bookstore-data");
            if (!homeDir.exists()) homeDir.mkdirs();
            dir = homeDir.getAbsolutePath();
        }
        dao = new BookDAO(dir);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        req.setAttribute("msg", req.getParameter("msg"));

        switch (action) {
            case "new":
                req.getRequestDispatcher("/book-form.jsp").forward(req, resp);
                break;
            case "edit":
                Book b = dao.findById(req.getParameter("id"));
                if (b == null) {
                    resp.sendRedirect("book?msg=Book+not+found");
                    return;
                }
                req.setAttribute("book", b);
                req.getRequestDispatcher("/book-form.jsp").forward(req, resp);
                break;
            default:
                req.setAttribute("books", dao.findAll());
                req.getRequestDispatcher("/book-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String id = trim(req.getParameter("id"));
        String title = trim(req.getParameter("title"));
        String genre = trim(req.getParameter("genre"));
        String price = trim(req.getParameter("price"));

        switch (action == null ? "" : action) {
            case "add":
            case "update": {
                boolean isUpdate = "update".equals(action);
                String back = isUpdate ? "book?action=edit&id=" + enc(id) : "book?action=new";

                if (id.isEmpty() || title.isEmpty() || genre.isEmpty() || price.isEmpty()) {
                    resp.sendRedirect(back + "&msg=All+fields+are+required");
                    return;
                }
                if (id.contains(",") || title.contains(",") || genre.contains(",") || price.contains(",")) {
                    resp.sendRedirect(back + "&msg=Commas+are+not+allowed");
                    return;
                }
                try {
                    if (Double.parseDouble(price) < 0) {
                        resp.sendRedirect(back + "&msg=Price+cannot+be+negative");
                        return;
                    }
                } catch (NumberFormatException e) {
                    resp.sendRedirect(back + "&msg=Price+must+be+a+number");
                    return;
                }

                Book book = new Book(id, title, genre, price);
                if (isUpdate) {
                    boolean ok = dao.update(book);
                    resp.sendRedirect("book?msg=" + (ok ? "Book+updated" : "Book+not+found"));
                } else {
                    boolean ok = dao.add(book);
                    resp.sendRedirect(ok ? "book?msg=Book+added" : back + "&msg=Book+ID+already+exists");
                }
                break;
            }
            case "delete":
                boolean deleted = dao.delete(id);
                resp.sendRedirect("book?msg=" + (deleted ? "Book+deleted" : "Book+not+found"));
                break;
            default:
                resp.sendRedirect("book");
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String enc(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }
}
