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

/** UPDATE: loads a review into the edit form (GET) and saves the changes (POST). */
@WebServlet("/updateReview")
public class UpdateReviewServlet extends HttpServlet {

    private final ReviewDAO dao = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Review review = dao.findById(request.getParameter("id"));
        if (review == null) {
            response.sendRedirect(request.getContextPath() + "/viewReviews?msg=notfound");
            return;
        }
        if (!canModify(request, review)) {
            response.sendRedirect(request.getContextPath() + "/viewReviews?msg=denied");
            return;
        }
        request.setAttribute("review", review);
        request.getRequestDispatcher("/EditReview.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Review review = dao.findById(request.getParameter("id"));
        if (review == null) {
            response.sendRedirect(request.getContextPath() + "/viewReviews?msg=notfound");
            return;
        }
        if (!canModify(request, review)) {
            response.sendRedirect(request.getContextPath() + "/viewReviews?msg=denied");
            return;
        }

        try {
            // The setters validate the new values (encapsulation).
            review.setBookTitle(request.getParameter("bookTitle"));
            review.setRating(Integer.parseInt(request.getParameter("rating")));
            review.setComment(request.getParameter("comment"));

            if (dao.update(review)) {
                response.sendRedirect(request.getContextPath() + "/viewReviews?msg=updated");
            } else {
                showError(request, response, review, "The changes could not be saved. Please try again.");
            }
        } catch (NumberFormatException e) {
            showError(request, response, dao.findById(review.getReviewId()), "Choose a rating from 1 to 5.");
        } catch (IllegalArgumentException e) {
            showError(request, response, dao.findById(review.getReviewId()), e.getMessage());
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, Review review, String message)
            throws ServletException, IOException {
        request.setAttribute("review", review);
        request.setAttribute("error", message);
        request.getRequestDispatcher("/EditReview.jsp").forward(request, response);
    }

    /** Admins can change any review; other users only their own. */
    private boolean canModify(HttpServletRequest request, Review review) {
        return SessionUtil.isAdmin(request)
                || review.getReviewer().equalsIgnoreCase(SessionUtil.getUsername(request));
    }
}

