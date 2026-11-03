<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User" %>
<% User user = (User) request.getAttribute("user"); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Aura Books - Edit User</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<% request.setAttribute("navActive", "user"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<main class="auth-wrap">
<div class="auth-card">
  <div class="text-center mb-4">
    <i class="fa-solid fa-user-pen fa-3x mb-3"></i>
    <h2 class="fw-bold">Edit User</h2>
    <p class="text-light opacity-75">Update account details</p>
  </div>

  <form action="user" method="POST">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="username" value="<%= user.getUsername().replace("\"", "&quot;") %>">
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-user me-2"></i>Username</label>
      <input type="text" class="form-control" value="<%= user.getUsername().replace("\"", "&quot;") %>" disabled>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-lock me-2"></i>New Password</label>
      <input type="password" name="password" class="form-control" placeholder="Leave blank to keep current password">
    </div>
    <div class="mb-4">
      <label class="form-label"><i class="fa-solid fa-id-badge me-2"></i>Role</label>
      <select name="role" class="form-select">
        <option <%= "Customer".equalsIgnoreCase(user.getRole()) ? "selected" : "" %>>Customer</option>
        <option <%= "Admin".equalsIgnoreCase(user.getRole()) ? "selected" : "" %>>Admin</option>
      </select>
    </div>
    <button type="submit" class="btn btn-custom mb-3">Update</button>
    <div class="text-center">
      <p class="small text-light opacity-75"><a href="user" class="text-decoration-none">Cancel</a></p>
    </div>
  </form>
</div>
</main>
</body>
</html>