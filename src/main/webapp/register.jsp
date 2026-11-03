<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User" %>
<%
    User cu = (User) session.getAttribute("loggedUser");
    boolean adminCreating = cu != null && "Admin".equalsIgnoreCase(cu.getRole());
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Aura Books - User Register</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<% request.setAttribute("navActive", adminCreating ? "user" : "register"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<main class="auth-wrap">
<div class="auth-card">
  <div class="text-center mb-4">
    <i class="fa-solid fa-user-plus fa-3x mb-3"></i>
    <h2 class="fw-bold"><%= adminCreating ? "Add New User" : "Create Account" %></h2>
    <p class="text-light opacity-75"><%= adminCreating ? "Create a customer or admin account" : "Join Aura Books community today" %></p>
  </div>

  <% String msg = request.getParameter("msg");
     if (msg != null) { %>
    <div class="alert alert-danger py-2"><%= msg.replace("<", "&lt;") %></div>
  <% } %>

  <form action="${pageContext.request.contextPath}/user" method="POST">
    <input type="hidden" name="action" value="add">
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-user me-2"></i>Username</label>
      <input type="text" name="username" class="form-control" placeholder="Enter a username" required>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-lock me-2"></i>Password</label>
      <input type="password" name="password" class="form-control" placeholder="Create a password" required>
    </div>
    <% if (adminCreating) { %>
    <div class="mb-4">
      <label class="form-label"><i class="fa-solid fa-id-badge me-2"></i>Role</label>
      <select name="role" class="form-select">
        <option>Customer</option>
        <option>Admin</option>
      </select>
    </div>
    <% } %>
    <button type="submit" class="btn btn-custom mb-3"><%= adminCreating ? "Add User" : "Register" %></button>
    <div class="text-center">
      <p class="small text-light opacity-75">
        <a href="<%= adminCreating ? "user" : "login" %>" class="text-decoration-none">
          <%= adminCreating ? "Back to user list" : "Back to login" %></a></p>
    </div>
  </form>
</div>
</main>
</body>
</html>