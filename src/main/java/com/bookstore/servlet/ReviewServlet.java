package com.bookstore.servlet;

import com.bookstore.dao.ReviewDAO;
import com.bookstore.model.PublicReview;
import com.bookstore.model.Review;
import com.bookstore.model.VerifiedReview;
import com.bookstore.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {
    // sensible upper limits so one review can never make a huge line in reviews.txt
    private static final int MAX_ID = 30;
    private static final int MAX_TITLE = 200;
    private static final int MAX_COMMENT = 1000;

    private ReviewDAO dao;

    @Override
    public void init() {
        // Using the updated default constructor
        dao = new ReviewDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        req.setAttribute("msg", req.getParameter("msg"));

        // customers can read reviews and write new ones; only Admin can edit
        if ("edit".equals(action) && !SessionUtil.isAdmin(req)) {
            resp.sendRedirect("review?msg=Access+denied");
            return;
        }

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
                req.setAttribute("reviews", dao.getAll());
                req.getRequestDispatcher("/review-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        // customers may only add; update / delete are Admin only
        if (!SessionUtil.isAdmin(req) && !"add".equals(action)) {
            resp.sendRedirect("review?msg=Access+denied");
            return;
        }

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

                int ratingValue;
                try {
                    ratingValue = Integer.parseInt(rating);
                } catch (NumberFormatException e) {
                    resp.sendRedirect(back + "&msg=Rating+must+be+a+number+1-5");
                    return;
                }

                // new review: written by the logged-in user.
                // edit: keep the original reviewer and review type
                String reviewer = SessionUtil.getUsername(req);
                boolean verified = false;
                if (isUpdate) {
                    Review old = dao.findById(id);
                    if (old != null) {
                        reviewer = old.getReviewer();
                        verified = "VERIFIED".equalsIgnoreCase(old.getType());
                    }
                }

                Review review;
                try {
                    review = verified
                            ? new VerifiedReview(id, bookTitle, ratingValue, reviewer, comment)
                            : new PublicReview(id, bookTitle, ratingValue, reviewer, comment);
                } catch (IllegalArgumentException e) {
                    resp.sendRedirect(back + "&msg=" + enc(e.getMessage()));
                    return;
                }

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