<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User" %>
<%
    User wu = (User) session.getAttribute("loggedUser");
    boolean wAdmin = wu != null && "Admin".equalsIgnoreCase(wu.getRole());
    String wmsg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Welcome</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<% request.setAttribute("navActive", ""); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<section class="modules">
    <div class="modules-inner">
        <h2>Welcome, <%= wu == null ? "guest" : wu.getUsername().replace("<", "&lt;") %></h2>

        <% if (wmsg != null && !wmsg.isEmpty()) { %>
        <div class="alert alert-warning py-2"><%= wmsg.replace("<", "&lt;") %></div>
        <% } %>

        <div class="module-grid">
            <a class="module" href="book">
                <i class="fa-solid fa-book"></i>
                <h3>Book Catalog</h3>
                <p>Browse books, genres &amp; prices</p>
            </a>
            <a class="module" href="order">
                <i class="fa-solid fa-cart-shopping"></i>
                <h3><%= wAdmin ? "Order Management" : "My Orders" %></h3>
                <p><%= wAdmin ? "Track order statuses &amp; customers" : "Place and track your orders" %></p>
            </a>
            <a class="module" href="review">
                <i class="fa-solid fa-star"></i>
                <h3>Reviews</h3>
                <p>Read and write book reviews</p>
            </a>
            <% if (wAdmin) { %>
            <a class="module" href="user">
                <i class="fa-solid fa-users-gear"></i>
                <h3>User Management</h3>
                <p>Administer users, roles &amp; security</p>
            </a>
            <% } %>
        </div>
    </div>
</section>
</body>
</html>