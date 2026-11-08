<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.Author" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - Author Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
    <style>
        .au-thumb { display: block; width: 44px; height: 44px; object-fit: cover; border-radius: 50%; border: 1px solid var(--line); }
        .au-initial { display: inline-grid; place-items: center; width: 44px; height: 44px; border-radius: 50%; background: hsl(var(--h, 30) 45% 32%); border: 1px solid rgba(255, 255, 255, .22); color: #fff; font-family: var(--display); font-weight: 600; }
        .au-about { max-width: 280px; color: var(--muted); font-size: .9rem; }
    </style>
</head>
<body class="aura">
<% boolean canEdit = com.bookstore.util.SessionUtil.isAdmin(request); %>
<% request.setAttribute("navActive", "author"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>
<div class="container">
    <div class="panel">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-pen-nib me-2"></i>Author Management</h2>
                <p class="text-light opacity-75 mb-0">Manage registered authors in Regent Books</p>
            </div>
            <div>
                <a href="index.jsp" class="btn btn-outline-light me-2"><i class="fa-solid fa-house me-1"></i>Home</a>
                <a href="author?action=cards" class="btn btn-outline-light me-2"><i class="fa-solid fa-table-cells-large me-1"></i>Cards view</a>
                <% if (canEdit) { %>
<a href="author?action=new" class="btn btn-primary">
                    <i class="fa-solid fa-plus me-2"></i>Add New Author
                </a>
<% } %>
            </div>
        </div>

        <% String msg = (String) request.getAttribute("msg");
           if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-info py-2"><%= esc(msg) %></div>
        <% } %>

        <div class="table-responsive">
            <table class="table table-dark-glass table-hover rounded overflow-hidden">
                <thead>
                <tr>
                    <th>#</th><th>Photo</th><th>Author ID</th><th>Name</th><th>Email</th><th>Type</th><th>Description</th><th>About</th>
                    <% if (canEdit) { %><th class="text-center">Actions</th><% } %>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Author> authors = (List<Author>) request.getAttribute("authors");
                    int n = 1;
                    if (authors != null && !authors.isEmpty()) {
                        for (Author a : authors) {
                %>
                <tr>
                    <td><%= String.format("%02d", n++) %></td>
                    <td>
                        <% if (a.hasPhoto()) { %>
                        <img class="au-thumb" alt="Photo of <%= esc(a.getName()) %>" loading="lazy"
                             src="author?action=photo&name=<%= java.net.URLEncoder.encode(a.getPhoto(), "UTF-8") %>">
                        <% } else { %>
                        <span class="au-initial" style="--h:<%= Math.abs(a.getName().hashCode()) % 360 %>"><%= a.getName().isEmpty() ? "?" : esc(a.getName().substring(0, 1).toUpperCase()) %></span>
                        <% } %>
                    </td>
                    <td><%= esc(a.getId()) %></td>
                    <td><%= esc(a.getName()) %></td>
                    <td><%= a.getEmail().isEmpty() ? "<span class=\"opacity-50\">&mdash;</span>" : esc(a.getEmail()) %></td>
                    <td><span class="badge <%= "International".equalsIgnoreCase(a.getAuthorType()) ? "bg-success" : "bg-secondary" %>"><%= esc(a.getAuthorType()) %></span></td>
                    <td><%= esc(a.getTypeDescription()) %></td>
                    <%
                        String auBio = a.getBio().replace("\r", " ").replace("\n", " ");
                        String auShort = auBio.length() > 90 ? auBio.substring(0, 90) + "\u2026" : auBio;
                    %>
                    <td class="au-about" title="<%= esc(auBio) %>"><%= auBio.isEmpty() ? "<span class=\"opacity-50\">&mdash;</span>" : esc(auShort) %></td>
                    <% if (canEdit) { %>
<td class="text-center">
                        <a class="btn btn-sm btn-warning btn-action me-1"
                           href="author?action=edit&id=<%= java.net.URLEncoder.encode(a.getId(), "UTF-8") %>">
                            <i class="fa-solid fa-pen-to-square"></i></a>
                        <form method="post" action="author" style="display:inline"
                              onsubmit="return confirm('Delete this author?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= esc(a.getId()) %>">
                            <button type="submit" class="btn btn-sm btn-danger btn-action"><i class="fa-solid fa-trash"></i></button>
                        </form>
                    </td>
<% } %>
                </tr>
                <%      }
                    } else { %>
                <tr><td colspan="<%= canEdit ? 9 : 8 %>" class="text-center opacity-75">No authors yet.</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script>
document.querySelectorAll('img.au-thumb').forEach(function (img) {
    img.addEventListener('error', function () {
        var s = document.createElement('span');
        s.className = 'au-initial';
        s.textContent = (img.alt.replace('Photo of ', '').charAt(0) || '?').toUpperCase();
        img.replaceWith(s);
    });
});
</script>
</body>
</html>

