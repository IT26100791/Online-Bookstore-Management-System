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
    <title>Regent Books - <%= editing ? "Edit" : "Add" %> Author</title>
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
    <style>
        .au-photo { display: block; width: 120px; height: 120px; object-fit: cover; border-radius: 50%; border: 1px solid var(--line); box-shadow: 0 8px 18px rgba(0, 0, 0, .4); }
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

        <form method="post" action="author" enctype="multipart/form-data">
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
                <label class="form-label">Email Address <span class="opacity-75">(optional)</span></label>
                <input type="email" name="email" class="form-control"
                       placeholder="Leave empty if the author has no email"
                       value="<%= editing ? esc(author.getEmail()) : "" %>">
            </div>
            <div class="mb-3">
                <label class="form-label">Author Type</label>
                <select name="authorType" class="form-select">
                    <option value="Local" <%= isIntl ? "" : "selected" %>>Local</option>
                    <option value="International" <%= isIntl ? "selected" : "" %>>International</option>
                </select>
            </div>

            <div class="mb-3">
                <label class="form-label">Description <span class="opacity-75">(optional)</span></label>
                <textarea name="bio" id="bioInput" class="form-control" rows="4" maxlength="1000"
                          placeholder="A short note about the author, their life and their writing"><%= editing ? esc(author.getBio()) : "" %></textarea>
                <div class="form-text text-end"><span id="bioCount">0</span> / 1000</div>
            </div>
            <div class="mb-4">
                <label class="form-label">Photo <span class="opacity-75">(optional)</span></label>
                <% if (editing && author.hasPhoto()) { %>
                <div class="mb-2" id="currentPhotoBox">
                    <img class="au-photo" id="currentPhoto" alt="Current photo"
                         src="author?action=photo&name=<%= java.net.URLEncoder.encode(author.getPhoto(), "UTF-8") %>">
                    <div class="form-check mt-2">
                        <input class="form-check-input" type="checkbox" name="removePhoto" value="1" id="removePhoto">
                        <label class="form-check-label" for="removePhoto">Remove the current photo</label>
                    </div>
                </div>
                <% } %>
                <input type="file" name="photo" id="photoInput" class="form-control"
                       accept="image/png,image/jpeg,image/webp,image/gif">
                <div class="form-text">JPG, PNG, WebP or GIF, up to 3 MB.<%= editing && author.hasPhoto() ? " Choose a file only if you want to replace the current photo." : "" %></div>
                <img id="photoPreview" class="au-photo mt-2" alt="Selected photo" hidden>
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-floppy-disk me-1"></i>Save</button>
            <a href="author" class="btn btn-outline-light ms-2">Cancel</a>
        </form>
    </div>
</div>
<script>
(function () {
    var bio = document.getElementById('bioInput'), count = document.getElementById('bioCount');
    function upd() { count.textContent = bio.value.length; }
    bio.addEventListener('input', upd); upd();

    var input = document.getElementById('photoInput'), preview = document.getElementById('photoPreview');
    var current = document.getElementById('currentPhoto'), remove = document.getElementById('removePhoto');
    input.addEventListener('change', function () {
        var f = input.files && input.files[0];
        if (f && f.size > 3 * 1024 * 1024) {
            alert('The photo must be 3 MB or smaller.');
            input.value = ''; preview.hidden = true; return;
        }
        if (f && f.type.indexOf('image/') === 0) { preview.src = URL.createObjectURL(f); preview.hidden = false; }
        else { preview.hidden = true; }
    });
    if (remove && current) {
        remove.addEventListener('change', function () { current.style.opacity = remove.checked ? '.35' : '1'; });
    }
})();
</script>
</body>
</html>



