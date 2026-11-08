<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.Book" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    Book book = (Book) request.getAttribute("book");
    boolean canEdit = com.bookstore.util.SessionUtil.isAdmin(request);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - <%= esc(book.getTitle()) %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<% request.setAttribute("navActive", "book"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>
<div class="container">
    <div class="panel">
        <div class="row g-4">
            <div class="col-md-4 text-center">
                <% if (book.hasImage()) { %>
                <img class="cover-img" style="max-width:100%;width:260px;height:auto;"
                     alt="Cover of <%= esc(book.getTitle()) %>"
                     src="book?action=image&name=<%= java.net.URLEncoder.encode(book.getImage(), "UTF-8") %>">
                <% } else { %>
                <div class="cover-empty c<%= Math.abs(book.getId().hashCode()) % 4 %>"
                     style="width:260px;max-width:100%;margin:0 auto;"><%= esc(book.getTitle()) %></div>
                <% } %>
            </div>
            <div class="col-md-8">
                <h2 class="fw-bold mb-1"><%= esc(book.getTitle()) %></h2>
                <p class="fs-5 mb-3"><i class="fa-solid fa-pen-nib me-2"></i>by <strong><%= esc(book.getAuthor().isEmpty() ? "Unknown author" : book.getAuthor()) %></strong></p>

                <p class="mb-1"><span class="badge bg-secondary me-2"><%= esc(book.getTypeLabel()) %></span>
                    <span class="badge bg-secondary"><%= esc(book.getGenre()) %></span></p>
                <p class="mb-1"><%= esc(book.getFormatLabel()) %>: <strong><%= esc(book.getFormatValue()) %></strong></p>
                <p class="mb-3 fs-4">Rs. <%= esc(book.getPrice()) %></p>

                <h5 class="fw-bold">About this book</h5>
                <% if (book.hasDescription()) { %>
                <p style="white-space:pre-wrap;"><%= esc(book.getDescription()) %></p>
                <% } else { %>
                <p class="opacity-75">No description has been added for this book yet.</p>
                <% } %>

                <p class="opacity-75"><i class="fa-solid fa-truck me-2"></i><%= esc(book.getDeliveryNote()) %></p>

                <a href="book" class="btn btn-outline-light"><i class="fa-solid fa-arrow-left me-1"></i>Back to books</a>
                <% if (canEdit) { %>
                <a class="btn btn-warning ms-2"
                   href="book?action=edit&id=<%= java.net.URLEncoder.encode(book.getId(), "UTF-8") %>">
                    <i class="fa-solid fa-pen-to-square me-1"></i>Edit</a>
                <% } %>
            </div>
        </div>
    </div>
</div>
</body>
</html>
