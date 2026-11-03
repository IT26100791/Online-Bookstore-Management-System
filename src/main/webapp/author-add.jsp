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
    <title>Add Author</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<header class="site-bar">
    <div class="site-bar__inner">
        <a class="brand" href="index.jsp"><span class="brand-script">Aura Books</span><span class="brand-sub">Online bookstore</span></a>
        <nav class="site-nav" aria-label="Main"><a href="book">Books</a><a href="author" aria-current="page">Authors</a><a href="publisher">Publishers</a><a href="order">Orders</a><a href="review">Reviews</a><a href="user">Users</a></nav>
        <div class="site-bar__end"><a href="login">Login</a><a href="user?action=new">Register</a></div>
    </div>
</header>
<div class="container" style="max-width:600px">
  <div class="panel panel-narrow">
    <h2 class="fw-bold mb-4">Add New Author</h2>

    <% String msg = (String) request.getAttribute("msg");
       if (msg != null && !msg.isEmpty()) { %>
      <div class="alert alert-warning py-2"><%= esc(msg) %></div>
    <% } %>

    <form method="post" action="author">
      <input type="hidden" name="action" value="add">
      <div class="mb-3">
        <label class="form-label">Author ID</label>
        <input type="text" name="authorId" class="form-control" required>
      </div>
      <div class="mb-3">
        <label class="form-label">Name</label>
        <input type="text" name="name" class="form-control" required>
      </div>
      <div class="mb-3">
        <label class="form-label">Country</label>
        <input type="text" name="country" class="form-control" required>
      </div>
      <p class="small opacity-75">Commas are not allowed in any field.</p>
      <button type="submit" class="btn btn-primary">Save Author</button>
      <a href="author" class="btn btn-outline-light">Cancel</a>
    </form>
  </div>
</div>
</body>
</html>