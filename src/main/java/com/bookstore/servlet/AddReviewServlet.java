package com.bookstore.servlet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AddReviewServlet")
public class AddReviewServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Form එකෙන් එන දත්ත ලබා ගැනීම
        String reviewId = request.getParameter("reviewId");
        String bookTitle = request.getParameter("bookTitle");
        String rating = request.getParameter("rating");
        String comment = request.getParameter("comment");

        // දත්ත Comma-separated (CSV) විදිහට reviews.txt ෆයිල් එකට ලිවීම
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reviews.txt", true))) {
            writer.write(reviewId + "," + bookTitle + "," + rating + "," + comment);
            writer.newLine(); // අලුත් පේළියකට ය기
        } catch (IOException e) {
            e.printStackTrace();
        }

        // දත්ත save වූ පසු නැවත Review List පිටුවට යැවීම
        response.sendRedirect("ReviewList.jsp");
    }
}