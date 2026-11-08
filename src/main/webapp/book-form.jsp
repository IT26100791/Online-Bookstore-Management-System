<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.*,com.bookstore.dao.AuthorDAO" %>
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
    String curType = editing ? book.getType() : PrintedBook.TYPE;
    String curFormat = editing ? book.getFormatValue() : "";

    List<Author> authors = (List<Author>) request.getAttribute("authors");
    if (authors == null) {
        try {
            authors = new AuthorDAO().findAll();
        } catch (Exception e) {
            authors = Collections.emptyList();
        }
    }
    String curAuthorId = editing ? (book.getAuthorId() != null ? book.getAuthorId() : "") : "";
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
<% request.setAttribute("navActive", "book"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>
<div class="container">
    <div class="panel panel-narrow">
        <h2 class="fw-bold mb-4">
            <i class="fa-solid fa-book me-2"></i><%= editing ? "Edit Book" : "Add New Book" %>
        </h2>

        <% if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-warning py-2"><%= esc(msg) %></div>
        <% } %>

        <% if (editing && book.isAuthorUnresolved()) { %>
          <div class="alert alert-warning py-2 mb-3">
              <i class="fa-solid fa-triangle-exclamation me-1"></i>
              <strong>Unlinked Legacy Author:</strong>
              This book currently has legacy author data
              (<em><%= esc(book.getAuthorName().isEmpty() ? "Unassigned" : book.getAuthorName()) %></em>)
              that is not linked to a registered author. Please select an existing author from the dropdown below to link it.
          </div>
        <% } %>

        <form method="post" action="book" enctype="multipart/form-data" accept-charset="UTF-8">
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
                <label class="form-label">Author <span class="opacity-75">(select from registered authors)</span></label>
                <% if (authors == null || authors.isEmpty()) { %>
                    <div class="alert alert-danger py-2 mb-2">
                        No registered authors found. Please create an author in the Authors section first.
                    </div>
                    <select name="authorId" id="authorSelect" class="form-select" required disabled>
                        <option value="">-- No authors available --</option>
                    </select>
                <% } else { %>
                    <select name="authorId" id="authorSelect" class="form-select" required>
                        <option value="" disabled <%= curAuthorId.isEmpty() ? "selected" : "" %>>-- Select an Author --</option>
                        <% for (Author a : authors) {
                            boolean selected = a.getId().equalsIgnoreCase(curAuthorId);
                        %>
                        <option value="<%= esc(a.getId()) %>" <%= selected ? "selected" : "" %>>
                            <%= esc(a.getName()) %> (<%= esc(a.getId()) %> - <%= esc(a.getAuthorType()) %>)
                        </option>
                        <% } %>
                    </select>
                <% } %>
            </div>
            <div class="mb-3">
                <label class="form-label">Genre</label>
                <input type="text" name="genre" class="form-control" required
                       value="<%= editing ? esc(book.getGenre()) : "" %>">
            </div>
            <div class="row">
                <div class="col-md-4 mb-3">
                    <label class="form-label">Book type</label>
                    <select name="type" id="typeSelect" class="form-select">
                        <option value="PRINTED" <%= "PRINTED".equals(curType) ? "selected" : "" %>>Printed book</option>
                        <option value="EBOOK" <%= "EBOOK".equals(curType) ? "selected" : "" %>>E-book</option>
                    </select>
                </div>
                <div class="col-md-4 mb-3">
                    <label class="form-label" id="formatLabel">Binding</label>
                    <select name="format" id="formatSelect" class="form-select"></select>
                </div>
                <div class="col-md-4 mb-3">
                    <label class="form-label">Price</label>
                    <input type="number" step="0.01" min="0" name="price" class="form-control" required
                           value="<%= editing ? esc(book.getPrice()) : "" %>">
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">Description <span class="opacity-75">(optional, max 1000 characters)</span></label>
                <textarea name="description" class="form-control" rows="5" maxlength="1000"
                          placeholder="What is this book about?"><%= editing ? esc(book.getDescription()) : "" %></textarea>
            </div>

            <div class="mb-4">
                <label class="form-label">Cover image <span class="opacity-75">(optional)</span></label>
                <% if (editing && book.hasImage()) { %>
                <div class="mb-2"><img class="thumb-lg" alt="Current cover"
                     src="book?action=image&name=<%= java.net.URLEncoder.encode(book.getImage(), "UTF-8") %>"></div>
                <% } %>
                <input type="file" name="image" id="coverInput" class="form-control"
                       accept="image/png,image/jpeg,image/webp,image/gif">
                <div class="form-text">JPG, PNG, WebP or GIF, up to 3 MB.<%= editing ? " Leave empty to keep the current cover." : "" %></div>
                <img id="coverPreview" class="thumb-lg mt-2" alt="Selected cover" hidden>
            </div>

            <button type="submit" class="btn btn-primary" <%= (authors == null || authors.isEmpty()) ? "disabled" : "" %>>
                <i class="fa-solid fa-floppy-disk me-1"></i>Save</button>
            <a href="book" class="btn btn-outline-light ms-2">Cancel</a>
        </form>
    </div>
</div>
<script>
    // format choices depend on the book type
    var FORMATS = {
        PRINTED: {label: 'Binding', options: ['Paperback', 'Hardcover']},
        EBOOK:   {label: 'File format', options: ['PDF', 'EPUB', 'MOBI']}
    };
    var typeSelect = document.getElementById('typeSelect');
    var formatSelect = document.getElementById('formatSelect');

    function fillFormats(selected) {
        var info = FORMATS[typeSelect.value];
        document.getElementById('formatLabel').textContent = info.label;
        formatSelect.innerHTML = '';
        info.options.forEach(function (name) {
            var opt = document.createElement('option');
            opt.value = name;
            opt.textContent = name;
            if (name === selected) opt.selected = true;
            formatSelect.appendChild(opt);
        });
    }
    typeSelect.addEventListener('change', function () { fillFormats(''); });
    fillFormats('<%= esc(curFormat) %>');

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
