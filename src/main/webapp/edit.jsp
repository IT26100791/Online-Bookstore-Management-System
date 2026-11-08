<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User,com.bookstore.servlet.UserServlet" %>
<%
    User user = (User) request.getAttribute("user");
    if (user == null) { response.sendRedirect(request.getContextPath() + "/user"); return; }
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - Edit User</title>
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
<% request.setAttribute("navActive", "user"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<main class="auth-wrap">
<div class="auth-card">
  <div class="text-center mb-4">
    <h2 class="fw-bold">Edit User</h2>
    <p class="text-light opacity-75">Update account details for <b><%= UserServlet.esc(user.getUsername()) %></b>
      <span class="role-badge <%= user.getRoleBadgeClass() %> ms-1"><%= user.getRole() %></span></p>
  </div>

  <% String msg = request.getParameter("msg");
     if (msg != null && !msg.isEmpty()) { %>
      <div class="alert <%= UserServlet.alertClass(request.getParameter("t")) %> py-2" role="alert"><%= UserServlet.esc(msg) %></div>
    <% } %>

  <form action="<%= ctx %>/user" method="POST" enctype="multipart/form-data">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="_csrf" value="<%= UserServlet.csrfToken(session) %>">
    <input type="hidden" name="username" value="<%= UserServlet.esc(user.getUsername()) %>">
    <div class="mb-3 photo-field">
      <%= UserServlet.avatar(ctx, user, "avatar-lg") %>
      <div>
        <label class="form-label mb-1" for="photo">Profile photo</label>
        <input type="file" id="photo" name="photo" class="form-control form-control-sm" accept="image/jpeg,image/png,image/gif,image/webp" data-photo-input>
        <% if (user.hasAvatar()) { %>
        <label class="check-row"><input type="checkbox" name="removePhoto" value="1"> Remove current photo</label>
        <% } else { %>
        <small class="pw-hint">JPG, PNG, GIF or WEBP, up to 2 MB.</small>
        <% } %>
      </div>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-user me-2"></i>Username</label>
      <input type="text" class="form-control" value="<%= UserServlet.esc(user.getUsername()) %>" disabled>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-envelope me-2"></i>E-mail <span class="opt">(optional)</span></label>
      <input type="email" name="email" class="form-control" value="<%= UserServlet.esc(user.getEmail()) %>" placeholder="name@example.com" maxlength="100">
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-lock me-2"></i>New Password</label>
      <input type="password" name="password" class="form-control" placeholder="Leave blank to keep current password" autocomplete="new-password" minlength="8" data-strength>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-check me-2"></i>Confirm new password</label>
      <input type="password" name="confirm" class="form-control" placeholder="Re-enter the password" autocomplete="new-password" minlength="8" data-confirm>
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
      <p class="small text-light opacity-75"><a href="<%= ctx %>/user" class="text-decoration-none">Cancel</a></p>
    </div>
  </form>
</div>
</main>
</body>
</html>

