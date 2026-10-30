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
import java.util.List;

/** READ: lists all reviews, or only the reviews whose book title matches ?book=... */
@WebServlet("/viewReviews")
public class ViewReviewServlet extends HttpServlet {

    private final ReviewDAO dao = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("book");
        boolean searching = search != null && !search.trim().isEmpty();

        List<Review> reviews = searching ? dao.searchByBook(search) : dao.getAll();

        request.setAttribute("reviews", reviews);
        request.setAttribute("search", searching ? search.trim() : "");
        request.setAttribute("isAdmin", SessionUtil.isAdmin(request));
        request.setAttribute("currentUser", SessionUtil.getUsername(request));
        request.getRequestDispatcher("/ReviewList.jsp").forward(request, response);
    }
}
