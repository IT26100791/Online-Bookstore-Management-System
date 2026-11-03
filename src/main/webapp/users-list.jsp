<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - User List Management</title>
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

<div class="container">
    <div class="panel">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-users-gear me-2"></i>User Management Dashboard</h2>
                <p class="text-light opacity-75 mb-0">Manage registered bookstore system users</p>
            </div>
            <a href="user?action=new" class="btn btn-primary"><i class="fa-solid fa-user-plus me-2"></i>Add New User</a>
        </div>

        <% String msg = (String) request.getAttribute("msg");
           if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-info py-2"><%= msg.replace("<", "&lt;") %></div>
        <% } %>

        <div class="table-responsive">
            <table class="table table-dark-glass table-hover rounded overflow-hidden">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Username</th>
                    <th>Role</th>
                    <th>Description</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>
                <tbody>
                <%
                    List<User> users = (List<User>) request.getAttribute("users");
                    int n = 1;
                    if (users != null) {
                        for (User u : users) {
                            boolean isAdmin = "Admin".equalsIgnoreCase(u.getRole());
                %>
                <tr>
                    <td><%= String.format("%02d", n++) %></td>
                    <td><%= u.getUsername().replace("<", "&lt;") %></td>
                    <td><span class="badge <%= isAdmin ? "bg-success" : "bg-secondary" %>"><%= u.getRole() %></span></td>
                    <td><%= u.getRoleDescription() %></td>
                    <td class="text-center">
                        <a class="btn btn-sm btn-warning btn-action me-1"
                           href="user?action=edit&username=<%= java.net.URLEncoder.encode(u.getUsername(), "UTF-8") %>">
                            <i class="fa-solid fa-pen-to-square"></i></a>
                        <form method="post" action="user" style="display:inline"
                              onsubmit="return confirm('Delete this user?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="username" value="<%= u.getUsername().replace("\"", "&quot;") %>">
                            <button type="submit" class="btn btn-sm btn-danger btn-action"><i class="fa-solid fa-trash"></i></button>
                        </form>
                    </td>
                </tr>
                <%      }
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>