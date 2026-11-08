<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.User,com.bookstore.servlet.UserServlet" %>
<%
    String ctx = request.getContextPath();
    List<User> users = (List<User>) request.getAttribute("users");
    User me = (User) session.getAttribute("loggedUser");
    Integer totalAttr = (Integer) request.getAttribute("totalUsers");
    Integer adminAttr = (Integer) request.getAttribute("totalAdmins");
    int total = totalAttr == null ? 0 : totalAttr;
    int admins = adminAttr == null ? 0 : adminAttr;
    String query = request.getAttribute("query") == null ? "" : (String) request.getAttribute("query");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - User List Management</title>
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

<div class="container">
    <div class="panel">
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-users-gear me-2"></i>User Management Dashboard</h2>
                <p class="text-light opacity-75 mb-0">Manage registered bookstore system users</p>
            </div>
            <a href="user?action=new" class="btn btn-primary"><i class="fa-solid fa-user-plus me-2"></i>Add New User</a>
        </div>

        <% String msg = request.getParameter("msg");
           if (msg != null && !msg.isEmpty()) { %>
        <div class="alert <%= UserServlet.alertClass(request.getParameter("t")) %> py-2" role="alert" data-toast="<%= "ok".equals(request.getParameter("t")) ? "yes" : "no" %>"><%= UserServlet.esc(msg) %></div>
        <% } %>

        <div class="stat-row">
            <div class="stat"><b><%= total %></b><span>Total users</span></div>
            <div class="stat"><b><%= admins %></b><span>Admins</span></div>
            <div class="stat"><b><%= total - admins %></b><span>Customers</span></div>
        </div>

        <form class="um-tools" method="get" action="<%= ctx %>/user" role="search">
            <label class="um-search"><i class="fa-solid fa-magnifying-glass"></i>
                <input type="search" name="q" value="<%= UserServlet.esc(query) %>" placeholder="Search by name, e-mail or role&hellip;" aria-label="Search users" data-live-search></label>
            <button type="submit" class="btn btn-outline-light btn-sm">Search</button>
            <% if (!query.isEmpty()) { %><a href="<%= ctx %>/user" class="btn btn-sm btn-outline-light">Clear</a><% } %>
            <span class="um-count" aria-live="polite"></span>
        </form>

        <div class="table-responsive">
            <table class="table table-dark-glass table-hover rounded overflow-hidden">
                <thead>
                <tr>
                    <th>#</th>
                    <th>User</th>
                    <th>Role</th>
                    <th>Description</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>
                <tbody>
                <%
                    int n = 1;
                    if (users != null) {
                        for (User u : users) {
                            boolean isMe = u.equals(me);
                %>
                <tr>
                    <td><%= String.format("%02d", n++) %></td>
                    <td>
                        <span class="user-cell">
                            <%= UserServlet.avatar(ctx, u, "") %>
                            <span><%= UserServlet.esc(u.getUsername()) %><% if (isMe) { %><em class="you">You</em><% } %>
                                <% if (!u.getEmail().isEmpty()) { %><small><%= UserServlet.esc(u.getEmail()) %></small><% } %></span>
                        </span>
                    </td>
                    <td><span class="role-badge <%= u.getRoleBadgeClass() %>"><%= u.getRole() %></span></td>
                    <td><%= u.getRoleDescription() %></td>
                    <td class="text-center">
                        <a class="btn btn-sm btn-warning btn-action me-1" title="Edit"
                           href="user?action=edit&username=<%= UserServlet.enc(u.getUsername()) %>">
                            <i class="fa-solid fa-pen-to-square"></i></a>
                        <% if (isMe) { %>
                        <button type="button" class="btn btn-sm btn-danger btn-action" disabled
                                title="You cannot delete your own account"><i class="fa-solid fa-trash"></i></button>
                        <% } else { %>
                        <form method="post" action="<%= ctx %>/user" style="display:inline"
                              onsubmit="return confirm('Delete this user?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="_csrf" value="<%= UserServlet.csrfToken(session) %>">
                            <input type="hidden" name="username" value="<%= UserServlet.esc(u.getUsername()) %>">
                            <button type="submit" class="btn btn-sm btn-danger btn-action" title="Delete"><i class="fa-solid fa-trash"></i></button>
                        </form>
                        <% } %>
                    </td>
                </tr>
                <%      }
                    }
                    if (users == null || users.isEmpty()) {
                %>
                <tr><td colspan="5" class="text-center text-light opacity-75 py-4">No users found<%= query.isEmpty() ? "" : " for &ldquo;" + UserServlet.esc(query) + "&rdquo;" %></td></tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
