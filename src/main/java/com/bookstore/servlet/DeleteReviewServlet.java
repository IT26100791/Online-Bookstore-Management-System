package com.bookstore.servlet;

import com.bookstore.dao.ReviewDAO;
import com.bookstore.model.Review;
import com.bookstore.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** DELETE: removes a review. Admins can delete any review; other users only their own. */
@WebServlet("/deleteReview")
public class DeleteReviewServlet extends HttpServlet {

    private final ReviewDAO dao = new ReviewDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Review review = dao.findById(request.getParameter("id"));
        String result;

        if (review == null) {
            result = "notfound";
        } else if (!SessionUtil.isAdmin(request)
                && !review.getReviewer().equalsIgnoreCase(SessionUtil.getUsername(request))) {
            result = "denied";
        } else {
            result = dao.delete(review.getReviewId()) ? "deleted" : "error";
        }
        response.sendRedirect(request.getContextPath() + "/viewReviews?msg=" + result);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/viewReviews");
    }
}
