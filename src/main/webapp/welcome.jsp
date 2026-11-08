<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User,com.bookstore.servlet.UserServlet" %>
<%
    User wu = (User) session.getAttribute("loggedUser");
    boolean wAdmin = wu != null && wu.canManageUsers();
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - Welcome</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/user-ui.css">
    <script defer src="<%= request.getContextPath() %>/js/user-ui.js"></script>
</head>
<body class="aura">
<% request.setAttribute("navActive", ""); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<section class="modules">
    <div class="modules-inner">
        <div class="welcome-head">
            <% if (wu != null) { %><%= UserServlet.avatar(ctx, wu, "avatar-lg") %><% } %>
            <div>
                <h2>Welcome, <%= wu == null ? "guest" : UserServlet.esc(wu.getUsername()) %></h2>
                <p><%= wu == null ? "" : wu.getRoleDescription() %></p>
            </div>
        </div>

        <% String msg = request.getParameter("msg");
           if (msg != null && !msg.isEmpty()) { %>
        <div class="alert <%= UserServlet.alertClass(request.getParameter("t")) %> py-2" role="alert" data-toast="<%= "ok".equals(request.getParameter("t")) ? "yes" : "no" %>"><%= UserServlet.esc(msg) %></div>
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
            <% if (wu != null) { %>
            <a class="module" href="profile">
                <i class="fa-solid fa-id-card"></i>
                <h3>My Profile</h3>
                <p>Photo, password &amp; security question</p>
            </a>
            <% } %>
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

