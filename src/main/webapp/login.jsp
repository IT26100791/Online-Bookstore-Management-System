<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - User Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<% request.setAttribute("navActive", "login"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<main class="auth-wrap">
<div class="auth-card">
    <div class="text-center mb-4">
        <i class="fa-solid fa-book-open-reader fa-3x mb-3"></i>
        <h2 class="fw-bold">Welcome Back</h2>
        <p class="text-light opacity-75">Login to Aura Books User Portal</p>
    </div>

    <% String msg = request.getParameter("msg");
       if (msg != null) { %>
      <div class="alert alert-warning py-2"><%= msg.replace("<", "&lt;") %></div>
    <% } %>

    <form action="${pageContext.request.contextPath}/login" method="POST">
        <div class="mb-3">
            <label class="form-label"><i class="fa-solid fa-user me-2"></i>Username</label>
            <input type="text" name="username" class="form-control" placeholder="Enter your username" required>
        </div>
        <div class="mb-4">
            <label class="form-label"><i class="fa-solid fa-lock me-2"></i>Password</label>
            <input type="password" name="password" class="form-control" placeholder="Enter your password" required>
        </div>
        <button type="submit" class="btn btn-custom mb-3">Login</button>
        <div class="text-center">
            <p class="small text-light opacity-75">Don't have an account?
                <a href="user?action=new" class="text-decoration-none">Register here</a></p>
        </div>
    </form>
</div>
</main>
</body>
</html>