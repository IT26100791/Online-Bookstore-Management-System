<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.Book" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    Book book = (Book) request.getAttribute("book");
    boolean editing = (book != null);
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - <%= editing ? "Edit" : "Add" %> Book</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<header class="site-bar">
    <div class="site-bar__inner">
        <a class="brand" href="index.jsp"><span class="brand-script">Aura Books</span><span class="brand-sub">Online bookstore</span></a>
        <nav class="site-nav" aria-label="Main"><a href="book" aria-current="page">Books</a><a href="author">Authors</a><a href="publisher">Publishers</a><a href="order">Orders</a><a href="review">Reviews</a><a href="user">Users</a></nav>
        <div class="site-bar__end"><a href="login">Login</a><a href="user?action=new">Register</a></div>
    </div>
</header>
<div class="container">
    <div class="panel panel-narrow">
        <h2 class="fw-bold mb-4">
            <i class="fa-solid fa-book me-2"></i><%= editing ? "Edit Book" : "Add New Book" %>
        </h2>

        <% if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-warning py-2"><%= esc(msg) %></div>
        <% } %>

        <form method="post" action="book" enctype="multipart/form-data">
            <input type="hidden" name="action" value="<%= editing ? "update" : "add" %>">

            <div class="mb-3">
                <label class="form-label">Book ID</label>
                <input type="text" name="id" class="form-control" required
                       value="<%= editing ? esc(book.getId()) : "" %>" <%= editing ? "readonly" : "" %>>
            </div>
            <div class="mb-3">
                <label class="form-label">Title</label>
                <input type="text" name="title" class="form-control" required
                       value="<%= editing ? esc(book.getTitle()) : "" %>">
            </div>
            <div class="mb-3">
                <label class="form-label">Genre</label>
                <input type="text" name="genre" class="form-control" required
                       value="<%= editing ? esc(book.getGenre()) : "" %>">
            </div>
            <div class="mb-3">
                <label class="form-label">Price</label>
                <input type="number" step="0.01" min="0" name="price" class="form-control" required
                       value="<%= editing ? esc(book.getPrice()) : "" %>">
            </div>

            <div class="mb-4">
                <label class="form-label">Cover image <span class="opacity-75">(optional)</span></label>
                <% if (editing && book.getImage() != null && !book.getImage().isEmpty()) { %>
                <div class="mb-2"><img class="thumb-lg" alt="Current cover"
                     src="book?action=image&name=<%= java.net.URLEncoder.encode(book.getImage(), "UTF-8") %>"></div>
                <% } %>
                <input type="file" name="image" id="coverInput" class="form-control"
                       accept="image/png,image/jpeg,image/webp,image/gif">
                <div class="form-text">JPG, PNG, WebP or GIF, up to 3 MB.<%= editing ? " Leave empty to keep the current cover." : "" %></div>
                <img id="coverPreview" class="thumb-lg mt-2" alt="Selected cover" hidden>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-floppy-disk me-1"></i>Save</button>
            <a href="book" class="btn btn-outline-light ms-2">Cancel</a>
        </form>
    </div>
</div>
<script>
    document.getElementById('coverInput').addEventListener('change', function () {
        var preview = document.getElementById('coverPreview');
        var file = this.files && this.files[0];
        if (file && file.size > 3 * 1024 * 1024) {
            alert('Image must be 3 MB or smaller.');
            this.value = '';
            preview.hidden = true;
            return;
        }
        if (file && file.type.indexOf('image/') === 0) {
            preview.src = URL.createObjectURL(file);
            preview.hidden = false;
        } else {
            preview.hidden = true;
        }
    });
</script>
</body>
</html>