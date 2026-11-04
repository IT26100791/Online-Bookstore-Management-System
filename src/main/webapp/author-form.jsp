<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.Author" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    Author author = (Author) request.getAttribute("author");
    boolean editing = (author != null);
    boolean isIntl = editing && "International".equalsIgnoreCase(author.getAuthorType());
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - <%= editing ? "Edit" : "Add" %> Author</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
    <style>
        .form-select { background-color: rgba(255,255,255,0.08); border: 1px solid rgba(255,255,255,0.2); color: #fff; }
        .form-select:focus { background-color: rgba(255,255,255,0.12); color: #fff; }
        .form-select option { color: #000; }
    </style>
</head>
<body class="aura">
<% boolean canEdit = com.bookstore.util.SessionUtil.isAdmin(request); %>
<% request.setAttribute("navActive", "author"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>
<div class="container">
    <div class="panel panel-narrow">
        <h2 class="fw-bold mb-4">
            <i class="fa-solid fa-pen-nib me-2"></i><%= editing ? "Edit Author" : "Add New Author" %>
        </h2>

        <% if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-warning py-2"><%= esc(msg) %></div>
        <% } %>

        <form method="post" action="author">
            <input type="hidden" name="action" value="<%= editing ? "update" : "add" %>">

            <div class="mb-3">
                <label class="form-label">Author ID</label>
                <input type="text" name="id" class="form-control" required
                       value="<%= editing ? esc(author.getId()) : "" %>" <%= editing ? "readonly" : "" %>>
            </div>
            <div class="mb-3">
                <label class="form-label">Full Name</label>
                <input type="text" name="name" class="form-control" required
                       value="<%= editing ? esc(author.getName()) : "" %>">
            </div>
            <div class="mb-3">
                <label class="form-label">Email Address</label>
                <input type="email" name="email" class="form-control" required
                       value="<%= editing ? esc(author.getEmail()) : "" %>">
            </div>
            <div class="mb-3">
                <label class="form-label">Author Type</label>
                <select name="authorType" class="form-select">
                    <option value="Local" <%= isIntl ? "" : "selected" %>>Local</option>
                    <option value="International" <%= isIntl ? "selected" : "" %>>International</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-floppy-disk me-1"></i>Save</button>
            <a href="author" class="btn btn-outline-light ms-2">Cancel</a>
        </form>
    </div>
</div>
</body>
</html>

