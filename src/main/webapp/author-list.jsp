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
    <title>Author Directory</title>
</head>
<body>
<%@ page import="java.util.*,com.bookstore.model.Author" %>
<div class="container">
  <div class="glass">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2 class="fw-bold">Author Directory</h2>
        <p class="opacity-75 mb-0">Manage the authors in the bookstore</p>
      </div>
      <div>
        <a href="index.jsp" class="btn btn-outline-light me-2">Home</a>
        <a href="author?action=new" class="btn btn-primary">Add New Author</a>
      </div>
    </div>

    <% String msg = (String) request.getAttribute("msg");
       if (msg != null && !msg.isEmpty()) { %>
      <div class="alert alert-info py-2"><%= esc(msg) %></div>
    <% } %>

    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead><tr><th>#</th><th>Author ID</th><th>Name</th><th>Country</th><th class="text-center">Actions</th></tr></thead>
        <tbody>
        <%
          List<Author> authors = (List<Author>) request.getAttribute("authors");
          int n = 1;
          if (authors != null) {
            for (Author a : authors) {
        %>
          <tr>
            <td><%= String.format("%02d", n++) %></td>
            <td><%= esc(a.getAuthorId()) %></td>
            <td><%= esc(a.getName()) %></td>
            <td><%= esc(a.getCountry()) %></td>
            <td class="text-center">
              <a class="btn btn-sm btn-warning me-1"
                 href="author?action=edit&id=<%= java.net.URLEncoder.encode(a.getAuthorId(), "UTF-8") %>">Edit</a>
              <form method="post" action="author" style="display:inline"
                    onsubmit="return confirm('Delete this author?');">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="authorId" value="<%= esc(a.getAuthorId()) %>">
                <button type="submit" class="btn btn-sm btn-danger">Delete</button>
              </form>
            </td>
          </tr>
        <%  }
          } %>
        </tbody>
      </table>
    </div>
  </div>
</div>
</body>
</html>
