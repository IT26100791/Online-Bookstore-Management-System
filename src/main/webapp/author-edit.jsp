<%@ page contentType="text/html;charset=UTF-8" %>
<%! private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    } %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: linear-gradient(135deg, #0f172a, #1e1b4b); min-height: 100vh; color: #f8fafc; padding: 40px 0; }
        .glass { background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.12); border-radius: 16px; padding: 30px; }
        .table { color: #f8fafc; --bs-table-bg: transparent; --bs-table-color: #f8fafc; --bs-table-hover-bg: rgba(255,255,255,0.06); --bs-table-hover-color: #fff; }
        .form-control { background: rgba(255,255,255,0.08); color: #fff; border-color: rgba(255,255,255,0.2); }
        .form-control:focus { background: rgba(255,255,255,0.12); color: #fff; }
        .form-control:read-only { opacity: .7; }
    </style>
    <title>Edit Author</title>
</head>
<body>
<%@ page import="com.bookstore.model.Author" %>
<% Author a = (Author) request.getAttribute("author"); %>
<div class="container" style="max-width:600px">
  <div class="glass">
    <h2 class="fw-bold mb-4">Edit Author</h2>
    <form method="post" action="author">
      <input type="hidden" name="action" value="update">
      <div class="mb-3">
        <label class="form-label">Author ID (cannot be changed)</label>
        <input type="text" name="authorId" class="form-control" value="<%= esc(a.getAuthorId()) %>" readonly>
      </div>
      <div class="mb-3">
        <label class="form-label">Name</label>
        <input type="text" name="name" class="form-control" value="<%= esc(a.getName()) %>" required>
      </div>
      <div class="mb-3">
        <label class="form-label">Country</label>
        <input type="text" name="country" class="form-control" value="<%= esc(a.getCountry()) %>" required>
      </div>
      <button type="submit" class="btn btn-primary">Update Author</button>
      <a href="author" class="btn btn-outline-light">Cancel</a>
    </form>
  </div>
</div>
</body>
</html>
