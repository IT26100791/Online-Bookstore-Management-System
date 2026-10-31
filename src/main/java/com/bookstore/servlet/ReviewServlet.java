package com.bookstore.servlet;

import com.bookstore.dao.ReviewDAO;
import com.bookstore.model.Review;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {
    private ReviewDAO dao;

    @Override
    public void init() {
        String dir = getServletContext().getInitParameter("dataDir");
        if (dir == null || dir.trim().isEmpty() || !new File(dir).exists()) {
            File homeDir = new File(System.getProperty("user.home"), "bookstore-data");
            if (!homeDir.exists()) homeDir.mkdirs();
            dir = homeDir.getAbsolutePath();
        }
        dao = new ReviewDAO(dir);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        req.setAttribute("msg", req.getParameter("msg"));

        switch (action) {
            case "new":
                req.getRequestDispatcher("/review-form.jsp").forward(req, resp);
                break;
            case "edit":
                Review r = dao.findById(req.getParameter("id"));
                if (r == null) {
                    resp.sendRedirect("review?msg=Review+not+found");
                    return;
                }
                req.setAttribute("review", r);
                req.getRequestDispatcher("/review-form.jsp").forward(req, resp);
                break;
            default:
                req.setAttribute("reviews", dao.findAll());
                req.getRequestDispatcher("/review-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String id = trim(req.getParameter("id"));
        String bookTitle = trim(req.getParameter("bookTitle"));
        String rating = trim(req.getParameter("rating"));
        String comment = trim(req.getParameter("comment"));

        switch (action == null ? "" : action) {
            case "add":
            case "update": {
                boolean isUpdate = "update".equals(action);
                String back = isUpdate ? "review?action=edit&id=" + enc(id) : "review?action=new";

                if (id.isEmpty() || bookTitle.isEmpty() || rating.isEmpty() || comment.isEmpty()) {
                    resp.sendRedirect(back + "&msg=All+fields+are+required");
                    return;
                }
                if (id.contains(",") || bookTitle.contains(",") || rating.contains(",") || comment.contains(",")) {
                    resp.sendRedirect(back + "&msg=Commas+are+not+allowed");
                    return;
                }

                Review review = new Review(id, bookTitle, rating, comment);
                if (isUpdate) {
                    boolean ok = dao.update(review);
                    resp.sendRedirect("review?msg=" + (ok ? "Review+updated" : "Review+not+found"));
                } else {
                    boolean ok = dao.add(review);
                    resp.sendRedirect(ok ? "review?msg=Review+added" : back + "&msg=Review+ID+already+exists");
                }
                break;
            }
            case "delete":
                boolean deleted = dao.delete(id);
                resp.sendRedirect("review?msg=" + (deleted ? "Review+deleted" : "Review+not+found"));
                break;
            default:
                resp.sendRedirect("review");
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String enc(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }
}
