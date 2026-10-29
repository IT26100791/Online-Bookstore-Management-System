package com.bookstore.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ViewReviewServlet")
public class ViewReviewServlet extends HttpServlet {

    private static final String FILE_NAME =
            System.getProperty("user.home") + File.separator + "reviews.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Book Reviews</title></head><body>");
        out.println("<h2>--- BOOK REVIEWS LOG ---</h2>");

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            out.println("<h3>No records found.</h3>");
            out.println("<a href='AddReview.jsp'>Add Review</a></body></html>");
            return;
        }

        out.println("<table border='1' cellpadding='6'>");
        out.println("<tr><th>Review ID</th><th>Book</th><th>Rating</th><th>Comment</th><th>Actions</th></tr>");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", 4);
                if (data.length == 4) {
                    String id = escape(data[0]);
                    out.println("<tr>");
                    out.println("<td>" + id + "</td>");
                    out.println("<td>" + escape(data[1]) + "</td>");
                    out.println("<td>" + escape(data[2]) + "/5</td>");
                    out.println("<td>" + escape(data[3]) + "</td>");
                    out.println("<td>");
                    out.println("<a href='EditReview.jsp?reviewId=" + id + "'>Edit</a> ");
                    out.println("<form action='DeleteReviewServlet' method='post' style='display:inline'"
                            + " onsubmit=\"return confirm('Delete this review?');\">");
                    out.println("<input type='hidden' name='reviewId' value='" + id + "'>");
                    out.println("<button type='submit'>Delete</button>");
                    out.println("</form>");
                    out.println("</td>");
                    out.println("</tr>");
                }
            }
        } catch (IOException e) {
            out.println("<p>Error reading records: " + escape(e.getMessage()) + "</p>");
        }

        out.println("</table>");
        out.println("<p><a href='AddReview.jsp'>Add New Review</a></p>");
        out.println("</body></html>");
    }

    // XSS වලින් ආරක්ෂා වෙන්න HTML escape කරනවා
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
