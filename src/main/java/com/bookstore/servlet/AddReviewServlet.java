package com.bookstore.servlet;

import com.bookstore.dao.ReviewDAO;
import com.bookstore.model.PublicReview;
import com.bookstore.model.Review;
import com.bookstore.model.VerifiedReview;
import com.bookstore.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** CREATE: saves a new review through ReviewDAO. */
@WebServlet("/AddReviewServlet")
public class AddReviewServlet extends HttpServlet {

    private final ReviewDAO dao = new ReviewDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String result;
        try {
            String id = request.getParameter("reviewId");
            String title = request.getParameter("bookTitle");
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");
            String reviewer = SessionUtil.getUsername(request);

            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Review ID cannot be empty.");
            }
            Review review = "VERIFIED".equalsIgnoreCase(request.getParameter("type"))
                    ? new VerifiedReview(id.trim(), title, rating, reviewer, comment)
                    : new PublicReview(id.trim(), title, rating, reviewer, comment);

            result = dao.add(review) ? "added" : "duplicate";
        } catch (IllegalArgumentException e) {   // includes NumberFormatException
            result = "invalid";
        }
        response.sendRedirect(request.getContextPath() + "/viewReviews?msg=" + result);
    }
}