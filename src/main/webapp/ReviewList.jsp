<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.io.File, java.io.BufferedReader, java.io.FileReader, java.io.IOException, java.util.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Review List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(30, 27, 75, 0.85)),
            url('https://images.unsplash.com/photo-1457369804613-52c61a468e7d?q=80&w=1920&auto=format&fit=crop') !important;
            background-size: cover !important; background-position: center !important;
            min-height: 100vh; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #f8fafc;
        }
        .glass-card {
            background: rgba(15, 23, 42, 0.75); backdrop-filter: blur(16px);
            border: 1px solid rgba(255, 255, 255, 0.12); border-radius: 1rem; box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
        }
        .table-glass th, .table-glass td { background: transparent !important; color: #f8fafc !important; border-color: rgba(255, 255, 255, 0.1) !important; }
    </style>
</head>
<body class="py-5">
<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold text-white mb-0"><i class="fa-solid fa-star-half-stroke me-2 text-warning"></i>Review Management</h2>
        </div>
        <!-- මෙහි නම AddReview.jsp ලෙස නිවැරදි කර ඇත -->
        <a href="AddReview.jsp" class="btn btn-primary btn-sm"><i class="fa-solid fa-plus me-1"></i>Add New Review</a>
    </div>

    <div class="glass-card p-4">
        <div class="table-responsive">
            <table class="table table-glass align-middle mb-0">
                <thead>
                <tr>
                    <th>Review ID</th>
                    <th>Book Title</th>
                    <th>Rating</th>
                    <th>Comment</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>
                <tbody>
                <%
                    File file = new File("reviews.txt");
                    boolean hasData = false;
                    if (file.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] data = line.split(",");
                                if (data.length >= 4) {
                                    hasData = true;
                %>
                <tr>
                    <td><%= data[0] %></td>
                    <td><%= data[1] %></td>
                    <td><span class="text-warning"><%= data[2] %>/5 ⭐</span></td>
                    <td><%= data[3] %></td>
                    <td class="text-center">
                        <!-- මෙහි නම EditReview.jsp ලෙස නිවැරදි කර ඇත -->
                        <a href="EditReview.jsp?id=<%= data[0] %>" class="btn btn-sm btn-outline-info me-1"><i class="fa-solid fa-pen-to-square"></i></a>
                        <form action="DeleteReviewServlet" method="POST" style="display:inline;">
                            <input type="hidden" name="reviewId" value="<%= data[0] %>">
                            <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Delete this review?');"><i class="fa-solid fa-trash"></i></button>
                        </form>
                    </td>
                </tr>
                <%
                                }
                            }
                        } catch (IOException e) {
                            out.println("<tr><td colspan='5' class='text-danger'>Error reading file.</td></tr>");
                        }
                    }
                    if (!hasData) {
                %>
                <tr><td colspan="5" class="text-center text-white-50 py-3">No reviews found.</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>