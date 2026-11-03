<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.Review" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    Review review = (Review) request.getAttribute("review");
    boolean editing = (review != null);
    String currentRating = editing ? String.valueOf(review.getRating()) : "5";
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - <%= editing ? "Edit" : "Add" %> Review</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<% boolean canEdit = com.bookstore.util.SessionUtil.isAdmin(request); %>
<% request.setAttribute("navActive", "review"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>
<div class="container">
    <div class="panel panel-narrow">
        <h2 class="fw-bold mb-4">
            <i class="fa-solid fa-star me-2"></i><%= editing ? "Edit Review" : "Add New Review" %>
        </h2>

        <% if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-warning py-2"><%= esc(msg) %></div>
        <% } %>

        <form method="post" action="review">
            <input type="hidden" name="action" value="<%= editing ? "update" : "add" %>">

            <div class="mb-3">
                <label class="form-label">Review ID</label>
                <input type="text" name="id" class="form-control" required
                       value="<%= editing ? esc(review.getReviewId()) : "" %>" <%= editing ? "readonly" : "" %>>
            </div>
            <div class="mb-3">
                <label class="form-label">Book Title</label>
                <input type="text" name="bookTitle" class="form-control" required
                       value="<%= editing ? esc(review.getBookTitle()) : "" %>">
            </div>
            <div class="mb-3">
                <label class="form-label">Rating</label>
                <select name="rating" class="form-select" required>
                    <option value="5" <%= "5".equals(currentRating) ? "selected" : "" %>>5 Stars - Outstanding</option>
                    <option value="4" <%= "4".equals(currentRating) ? "selected" : "" %>>4 Stars - Very Good</option>
                    <option value="3" <%= "3".equals(currentRating) ? "selected" : "" %>>3 Stars - Average</option>
                    <option value="2" <%= "2".equals(currentRating) ? "selected" : "" %>>2 Stars - Below Average</option>
                    <option value="1" <%= "1".equals(currentRating) ? "selected" : "" %>>1 Star - Poor</option>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label">Comment</label>
                <textarea name="comment" rows="3" class="form-control" required><%= editing ? esc(review.getComment()) : "" %></textarea>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-floppy-disk me-1"></i>Save</button>
            <a href="review" class="btn btn-outline-light ms-2">Cancel</a>
        </form>
    </div>
</div>
</body>
</html>