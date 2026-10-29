<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User" %>
<%
    User u = (User) session.getAttribute("loggedUser");
    if (u == null) { response.sendRedirect("login"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Aura Books - Welcome</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(30, 27, 75, 0.85));
            min-height: 100vh; color: #f8fafc;
            display: flex; align-items: center; justify-content: center;
            font-family: 'Segoe UI', Tahoma, sans-serif;
        }
        .glass-card {
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 16px; padding: 40px; text-align: center;
        }
    </style>
</head>
<body>
<div class="glass-card">
    <h2 class="fw-bold">Welcome, <%= u.getUsername().replace("<", "&lt;") %>!</h2>
    <p class="opacity-75"><%= u.getRoleDescription() %></p>
    <a href="logout" class="btn btn-outline-light">Logout</a>
</div>
</body>
</html>