<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - User List Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(30, 27, 75, 0.85)),
            url('https://images.unsplash.com/photo-1457369804613-52c61a468e7d?q=80&w=1920&auto=format&fit=crop');
            background-size: cover !important;
            background-position: center !important;
            background-repeat: no-repeat !important;
            background-attachment: fixed !important;
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: #f8fafc;
            padding: 40px 0;
        }
        .glass-container {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 16px;
            padding: 30px;
            box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
        }
        .table { color: #f8fafc; vertical-align: middle; }
        .table-dark-glass { background: rgba(255, 255, 255, 0.03); }
        .table-dark-glass th {
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
            border-bottom: 2px solid rgba(255, 255, 255, 0.2);
        }
        .table-dark-glass td {
            border-color: rgba(255, 255, 255, 0.08);
            background: transparent;
            color: #e2e8f0;
        }
        .btn-action { padding: 6px 12px; font-size: 0.875rem; border-radius: 6px; }
    </style>
</head>
<body>

<div class="container">
    <div class="glass-container">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-users-gear me-2" style="color: #818cf8;"></i>User Management Dashboard</h2>
                <p class="text-light opacity-75 mb-0">Manage registered bookstore system users</p>
            </div>
            <a href="user?action=new" class="btn btn-primary" style="background: #6366f1; border: none;"><i class="fa-solid fa-user-plus me-2"></i>Add New User</a>
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