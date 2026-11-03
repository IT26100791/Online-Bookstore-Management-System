<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.Review" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Review & Rating Management</title>
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
    <div class="panel">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-star me-2"></i>Review Management</h2>
                <p class="text-light opacity-75 mb-0">Customer reviews and ratings for catalog books</p>
            </div>
            <div>
                <a href="index.jsp" class="btn btn-outline-light me-2"><i class="fa-solid fa-house me-1"></i>Home</a>
                <a href="review?action=new" class="btn btn-primary">
                    <i class="fa-solid fa-plus me-2"></i>Add New Review
                </a>
            </div>
        </div>

        <% String msg = (String) request.getAttribute("msg");
           if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-info py-2"><%= esc(msg) %></div>
        <% } %>

        <div class="table-responsive">
            <table class="table table-dark-glass table-hover rounded overflow-hidden">
                <thead>
                <tr>
                    <th>#</th><th>Review ID</th><th>Book Title</th><th>Rating</th><th>Comment</th>
                    <% if (canEdit) { %><th class="text-center">Actions</th><% } %>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
                    int n = 1;
                    if (reviews != null && !reviews.isEmpty()) {
                        for (Review r : reviews) {
                            int stars = r.getRating();
                %>
                <tr>
                    <td><%= String.format("%02d", n++) %></td>
                    <td><%= esc(r.getReviewId()) %></td>
                    <td><%= esc(r.getBookTitle()) %></td>
                    <td>
                        <span class="star-rating">
                            <% for (int s = 1; s <= 5; s++) { %>
                                <i class="fa-<%= s <= stars ? "solid" : "regular" %> fa-star"></i>
                            <% } %>
                        </span>
                        <span class="ms-1 small text-light opacity-75">(<%= r.getRating() %>/5)</span>
                    </td>
                    <td>
                        <span class="badge bg-secondary me-1"><%= esc(r.getBadge()) %></span>
                        <%= esc(r.getReviewer()) %>: <%= esc(r.getComment()) %>
                    </td>
                    <% if (canEdit) { %>
<td class="text-center">
                        <a class="btn btn-sm btn-warning btn-action me-1"
                           href="review?action=edit&id=<%= java.net.URLEncoder.encode(r.getReviewId(), "UTF-8") %>">
                            <i class="fa-solid fa-pen-to-square"></i></a>
                        <form method="post" action="review" style="display:inline"
                              onsubmit="return confirm('Delete this review?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= esc(r.getReviewId()) %>">
                            <button type="submit" class="btn btn-sm btn-danger btn-action"><i class="fa-solid fa-trash"></i></button>
                        </form>
                    </td>
<% } %>
                </tr>
                <%      }
                    } else { %>
                <tr><td colspan="<%= canEdit ? 6 : 5 %>" class="text-center opacity-75">No reviews yet.</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>