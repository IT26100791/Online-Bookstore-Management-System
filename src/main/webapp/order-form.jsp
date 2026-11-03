<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.Order,com.bookstore.model.User" %>
<%! private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    } %>
<%
    User me = (User) session.getAttribute("loggedUser");
    boolean admin = me != null && "Admin".equalsIgnoreCase(me.getRole());
    Order order = (Order) request.getAttribute("order");
    boolean editing = order != null;

    List<String> statuses = new ArrayList<>(Arrays.asList("Pending", "Processing", "Shipped", "Delivered", "Cancelled"));
    if (editing && order.getStatus() != null && !statuses.contains(order.getStatus())) {
        statuses.add(order.getStatus());
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - <%= editing ? "Edit Order" : "Place Order" %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<% request.setAttribute("navActive", "order"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<main class="auth-wrap">
<div class="auth-card">
    <div class="text-center mb-4">
        <i class="fa-solid fa-cart-shopping fa-3x mb-3"></i>
        <h2 class="fw-bold"><%= editing ? "Edit Order" : "Place Order" %></h2>
        <p class="text-light opacity-75"><%= editing ? "Update order details" : "Order a book from Aura Books" %></p>
    </div>

    <% String msg = (String) request.getAttribute("msg");
       if (msg != null && !msg.isEmpty()) { %>
    <div class="alert alert-danger py-2"><%= esc(msg) %></div>
    <% } %>

    <form action="order" method="POST">
        <input type="hidden" name="action" value="<%= editing ? "update" : "add" %>">

        <% if (admin) { %>
        <div class="mb-3">
            <label class="form-label"><i class="fa-solid fa-hashtag me-2"></i>Order ID</label>
            <input type="text" name="id" class="form-control" placeholder="e.g. ORD001"
                   value="<%= editing ? esc(order.getId()) : "" %>" <%= editing ? "readonly" : "" %> required>
        </div>
        <div class="mb-3">
            <label class="form-label"><i class="fa-solid fa-user me-2"></i>Customer Name</label>
            <input type="text" name="customerName" class="form-control" placeholder="Customer username"
                   value="<%= editing ? esc(order.getCustomerName()) : "" %>" required>
        </div>
        <% } %>

        <div class="mb-3">
            <label class="form-label"><i class="fa-solid fa-book me-2"></i>Book Title</label>
            <input type="text" name="bookTitle" class="form-control" placeholder="Enter the book title"
                   value="<%= editing ? esc(order.getBookTitle()) : "" %>" required>
        </div>

        <% if (admin) { %>
        <div class="mb-4">
            <label class="form-label"><i class="fa-solid fa-truck me-2"></i>Status</label>
            <select name="status" class="form-select">
                <% for (String s : statuses) { %>
                <option <%= editing && s.equals(order.getStatus()) ? "selected" : "" %>><%= esc(s) %></option>
                <% } %>
            </select>
        </div>
        <% } else { %>
        <p class="small text-light opacity-75 mb-4">
            This order will be placed under your account (<strong><%= esc(me.getUsername()) %></strong>) with status <strong>Pending</strong>.
        </p>
        <% } %>

        <button type="submit" class="btn btn-custom mb-3"><%= editing ? "Update Order" : "Place Order" %></button>
        <div class="text-center">
            <p class="small text-light opacity-75"><a href="order" class="text-decoration-none">Cancel</a></p>
        </div>
    </form>
</div>
</main>
</body>
</html>