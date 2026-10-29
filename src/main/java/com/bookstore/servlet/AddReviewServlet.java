package com.bookstore.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AddReviewServlet")
public class AddReviewServlet extends HttpServlet {

    // Servlet තුනම එකම file එක පාවිච්චි කරනවා (Tomcat working directory ප්‍රශ්නය මගහැරෙන්න)
    private static final String FILE_NAME =
            System.getProperty("user.home") + File.separator + "reviews.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String id = clean(request.getParameter("reviewId"));
        String book = clean(request.getParameter("bookTitle"));
        String rating = clean(request.getParameter("rating"));
        String comment = clean(request.getParameter("comment"));

        if (id.isEmpty() || book.isEmpty() || rating.isEmpty()) {
            response.sendRedirect("AddReview.jsp?error=missing");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(id + "," + book + "," + rating + "," + comment);
            writer.newLine();
        } catch (IOException e) {
            response.sendRedirect("AddReview.jsp?error=save");
            return;
        }

        response.sendRedirect("ViewReviewServlet");
    }

    // කොමා සහ අලුත් පේළි අයින් කරනවා (නැත්නම් file format එක කැඩෙනවා)
    private String clean(String s) {
        if (s == null) return "";
        return s.replace(",", " ").replace("\r", " ").replace("\n", " ").trim();
    }
}
