package com.bookstore.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeleteReviewServlet")
public class DeleteReviewServlet extends HttpServlet {

    private static final String FILE_NAME =
            System.getProperty("user.home") + File.separator + "reviews.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String targetId = request.getParameter("reviewId");

        File originalFile = new File(FILE_NAME);
        File tempFile = new File(System.getProperty("user.home"), "temp_reviews.txt");

        if (targetId == null || !originalFile.exists()) {
            response.sendRedirect("ViewReviewServlet");
            return;
        }

        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(targetId.trim())) {
                    deleted = true;
                    continue; // මේ පේළිය skip කරනවා = delete
                }
                writer.write(line);
                writer.newLine();
            }
        }

        // temp file එකෙන් original එක replace කරනවා
        Files.move(tempFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        response.sendRedirect("ViewReviewServlet" + (deleted ? "" : "?error=notfound"));
    }
}

