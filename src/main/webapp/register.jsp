<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User,com.bookstore.servlet.UserServlet" %>
<%
    User cu = (User) session.getAttribute("loggedUser");
    boolean adminCreating = cu != null && cu.canManageUsers();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - User Register</title>
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
<% request.setAttribute("navActive", adminCreating ? "user" : "register"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<main class="auth-wrap<%= adminCreating ? "" : " auth-wrap--split" %>">
<% if (!adminCreating) { %>
<aside class="auth-art" aria-hidden="true">
  <img src="<%= request.getContextPath() %>/images/salt-road.jpeg" alt="">
  <p class="auth-art__line">Join the Regent Books community.</p>
  <p class="auth-art__sub">Create a free account to browse books, place orders and write reviews.</p>
</aside>
<% } %>
<div class="auth-card">
  <div class="text-center mb-4">
    <i class="fa-solid fa-user-plus fa-3x mb-3"></i>
    <h2 class="fw-bold"><%= adminCreating ? "Add New User" : "Create Account" %></h2>
    <p class="text-light opacity-75"><%= adminCreating ? "Create a customer or admin account" : "Join Regent Books community today" %></p>
  </div>

  <% String msg = request.getParameter("msg");
     if (msg != null && !msg.isEmpty()) { %>
      <div class="alert <%= UserServlet.alertClass(request.getParameter("t")) %> py-2" role="alert"><%= UserServlet.esc(msg) %></div>
    <% } %>

  <form action="${pageContext.request.contextPath}/user" method="POST" enctype="multipart/form-data">
    <input type="hidden" name="action" value="add">
    <input type="hidden" name="_csrf" value="<%= UserServlet.csrfToken(session) %>">
    <div class="mb-3 photo-field">
      <span class="avatar avatar-lg"><span class="avatar__ph" aria-hidden="true"><i class="fa-solid fa-camera"></i></span></span>
      <div>
        <label class="form-label mb-1" for="photo">Profile photo <span class="opt">(optional)</span></label>
        <input type="file" id="photo" name="photo" class="form-control form-control-sm" accept="image/jpeg,image/png,image/gif,image/webp" data-photo-input>
        <small class="pw-hint">JPG, PNG, GIF or WEBP, up to 2 MB.</small>
      </div>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-user me-2"></i>Username</label>
      <input type="text" name="username" class="form-control" placeholder="Enter a username" autocomplete="username" minlength="3" maxlength="30" pattern="[A-Za-z0-9_.\-]{3,30}" title="3-30 letters, numbers, dot, dash or underscore" autofocus required>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-envelope me-2"></i>E-mail <span class="opt">(optional)</span></label>
      <input type="email" name="email" class="form-control" placeholder="name@example.com" autocomplete="email" maxlength="100">
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-lock me-2"></i>Password</label>
      <input type="password" name="password" class="form-control" placeholder="Create a password" autocomplete="new-password" minlength="8" data-strength required>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-check me-2"></i>Confirm password</label>
      <input type="password" name="confirm" class="form-control" placeholder="Re-enter the password" autocomplete="new-password" minlength="8" data-confirm required>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-circle-question me-2"></i>Security question</label>
      <select name="question" class="form-select" required>
        <option value="" disabled selected>Choose a question&hellip;</option>
        <% for (int qi = 0; qi < UserServlet.QUESTIONS.length; qi++) { %>
        <option value="<%= qi %>"><%= UserServlet.esc(UserServlet.QUESTIONS[qi]) %></option>
        <% } %>
      </select>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-key me-2"></i>Your answer</label>
      <input type="text" name="answer" class="form-control" placeholder="Used if you forget your password" minlength="2" maxlength="60" autocomplete="off" required>
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
