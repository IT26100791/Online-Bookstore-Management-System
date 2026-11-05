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
    private static final int MAX_REVIEWER = 50;

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

            case "view": {
                // anyone can open the details page of a single review
                Review found = dao.findById(trim(req.getParameter("id")));
                if (found == null) {
                    resp.sendRedirect("review?msg=Review+not+found");
                    return;
                }
                req.setAttribute("review", found);
                req.getRequestDispatcher("/review-view.jsp").forward(req, resp);
                break;
            }

            case "edit": {
                Review r = dao.findById(trim(req.getParameter("id")));
                if (r == null) {
                    resp.sendRedirect("review?msg=Review+not+found");
                    return;
                }
                req.setAttribute("review", r);
                req.getRequestDispatcher("/review-form.jsp").forward(req, resp);
                break;
            }

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

                if (id.isEmpty() || bookTitle.isEmpty() || rating.isEmpty() || comment.isEmpty()) {
                    showForm(req, resp, isUpdate, id, bookTitle, rating, comment,
                            "All fields are required.");
                    return;
                }
                // The file is comma separated. The review id and book title sit in the
                // middle of each line, so commas are not allowed there. The comment is
                // the last column (ReviewDAO splits with a limit), so commas are fine in it.
                if (id.contains(",") || bookTitle.contains(",")) {
                    showForm(req, resp, isUpdate, id, bookTitle, rating, comment,
                            "Commas are not allowed in the Review ID or the Book Title.");
                    return;
                }
                if (id.length() > MAX_ID || bookTitle.length() > MAX_TITLE || comment.length() > MAX_COMMENT) {
                    showForm(req, resp, isUpdate, id, bookTitle, rating, comment,
                            "Text is too long (ID max " + MAX_ID + ", title max " + MAX_TITLE
                                    + ", comment max " + MAX_COMMENT + " characters).");
                    return;
                }

                int ratingValue;
                try {
                    ratingValue = Integer.parseInt(rating);
                } catch (NumberFormatException e) {
                    showForm(req, resp, isUpdate, id, bookTitle, rating, comment,
                            "Rating must be a number from 1 to 5.");
                    return;
                }

                // new review: written by the logged-in user, always a PUBLIC review.
                // edit: keep the original reviewer and review type.
                String reviewer = SessionUtil.getUsername(req);
                boolean verified = false;
                if (isUpdate) {
                    Review old = dao.findById(id);
                    if (old != null) {
                        reviewer = old.getReviewer();
                        verified = "VERIFIED".equalsIgnoreCase(old.getType());
                    }
                }

                // Only the Admin can choose the review type and the reviewer name.
                // (A customer can never mark their own review as "verified".)
                if (SessionUtil.isAdmin(req)) {
                    String typeParam = trim(req.getParameter("type"));
                    String reviewerParam = trim(req.getParameter("reviewer"));
                    if ("VERIFIED".equalsIgnoreCase(typeParam)) verified = true;
                    else if ("PUBLIC".equalsIgnoreCase(typeParam)) verified = false;
                    if (reviewerParam.length() > MAX_REVIEWER) {
                        showForm(req, resp, isUpdate, id, bookTitle, rating, comment,
                                "Reviewer name is too long (max " + MAX_REVIEWER + " characters).");
                        return;
                    }
                    if (!reviewerParam.isEmpty()) reviewer = reviewerParam;
                }
                // never write "null" or a comma into the file
                if (reviewer == null || reviewer.trim().isEmpty()) reviewer = "Guest";
                reviewer = reviewer.trim().replace(",", " ");

                Review review;
                try {
                    review = verified
                            ? new VerifiedReview(id, bookTitle, ratingValue, reviewer, comment)
                            : new PublicReview(id, bookTitle, ratingValue, reviewer, comment);
                } catch (IllegalArgumentException e) {
                    showForm(req, resp, isUpdate, id, bookTitle, rating, comment, e.getMessage());
                    return;
                }

                if (isUpdate) {
                    boolean ok = dao.update(review);
                    resp.sendRedirect("review?msg=" + (ok ? "Review+updated" : "Review+not+found"));
                } else {
                    if (dao.add(review)) {
                        resp.sendRedirect("review?msg=Review+added");
                    } else {
                        showForm(req, resp, false, id, bookTitle, rating, comment,
                                "Review ID already exists. Please choose another one.");
                    }
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

    /**
     * Shows the form again with an error message and with everything the user already typed,
     * so they do not have to write the whole review again.
     */
    private void showForm(HttpServletRequest req, HttpServletResponse resp, boolean isUpdate,
                          String id, String bookTitle, String rating, String comment, String message)
            throws ServletException, IOException {
        if (isUpdate) {
            Review existing = dao.findById(id);
            if (existing == null) {
                resp.sendRedirect("review?msg=Review+not+found");
                return;
            }
            req.setAttribute("review", existing);   // keeps the form in "edit" mode
        }
        req.setAttribute("msg", message);
        req.setAttribute("formId", id);
        req.setAttribute("formBook", bookTitle);
        req.setAttribute("formRating", rating);
        req.setAttribute("formComment", comment);
        req.setAttribute("formType", req.getParameter("type"));
        req.setAttribute("formReviewer", req.getParameter("reviewer"));
        req.getRequestDispatcher("/review-form.jsp").forward(req, resp);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
