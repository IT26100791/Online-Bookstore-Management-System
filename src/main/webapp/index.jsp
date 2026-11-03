<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User" %>
<%
    User u0 = (User) session.getAttribute("loggedUser");
    boolean loggedIn = u0 != null;
    boolean admin0 = loggedIn && "Admin".equalsIgnoreCase(u0.getRole());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Bookstore Portal</title>
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

<section class="hero">
    <div class="hero-inner">
        <div>
            <h1>Your bookstore, beautifully kept</h1>
            <div class="ornament" aria-hidden="true"><span></span></div>
            <p class="hero-lead">
                <%= admin0 ? "Manage books, authors, publishers, orders and reviews from one place."
                           : "Browse our books, place orders and share your reviews." %>
            </p>
            <div class="hero-actions">
                <% if (loggedIn) { %>
                <a class="btn btn-primary" href="book">Browse books</a>
                <% } else { %>
                <a class="btn btn-primary" href="login">Sign in</a>
                <a class="btn btn-outline-light" href="user?action=new">Register</a>
                <% } %>
            </div>
        </div>
        <img class="hero-book" src="<%= request.getContextPath() %>/images/salt-road.jpeg" alt="The Salt Road by Jane Johnson">
    </div>
</section>

<% if (loggedIn) { %>
<section class="modules">
    <div class="modules-inner">
        <h2><%= admin0 ? "Manage the store" : "Explore the store" %></h2>
        <div class="module-grid">
            <a class="module" href="book">
                <i class="fa-solid fa-book"></i>
                <h3>Book Catalog</h3>
                <p><%= admin0 ? "Manage books, titles, genres &amp; prices" : "Browse books, genres &amp; prices" %></p>
            </a>
            <a class="module" href="author">
                <i class="fa-solid fa-pen-nib"></i>
                <h3>Author Directory</h3>
                <p><%= admin0 ? "Manage author profiles and contacts" : "Discover the authors" %></p>
            </a>
            <a class="module" href="publisher">
                <i class="fa-solid fa-building"></i>
                <h3>Publishers</h3>
                <p><%= admin0 ? "Publishing houses and phone lines" : "See our publishing houses" %></p>
            </a>
            <a class="module" href="order">
                <i class="fa-solid fa-cart-shopping"></i>
                <h3><%= admin0 ? "Order Management" : "My Orders" %></h3>
                <p><%= admin0 ? "Track order statuses &amp; customers" : "Place and track your orders" %></p>
            </a>
            <a class="module" href="review">
                <i class="fa-solid fa-star"></i>
                <h3>Reviews &amp; Ratings</h3>
                <p>Customer feedback and star ratings</p>
            </a>
            <% if (admin0) { %>
            <a class="module" href="user">
                <i class="fa-solid fa-users-gear"></i>
                <h3>User Management</h3>
                <p>Administer users, roles &amp; security</p>
            </a>
            <% } %>
        </div>
    </div>
</section>
<% } else { %>
<section class="modules">
    <div class="modules-inner">
        <h2>Account &amp; access</h2>
        <div class="module-grid">
            <a class="module" href="login">
                <i class="fa-solid fa-right-to-bracket"></i>
                <h3>User Login</h3>
                <p>Sign in to your customer or admin portal</p>
            </a>
            <a class="module" href="user?action=new">
                <i class="fa-solid fa-user-plus"></i>
                <h3>Register</h3>
                <p>Create a new customer account</p>
            </a>
        </div>
    </div>
</section>
<% } %>
</body>
</html>

