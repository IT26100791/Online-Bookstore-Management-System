<%@ page contentType="text/html;charset=UTF-8"
         import="java.util.*,java.net.URLEncoder,com.bookstore.model.Publisher,com.bookstore.model.LocalPublisher,com.bookstore.model.InternationalPublisher" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    int intAttr(HttpServletRequest r, String name) {
        Object v = r.getAttribute(name);
        return v instanceof Integer ? (Integer) v : 0;
    }

    String textAttr(HttpServletRequest r, String name) {
        Object v = r.getAttribute(name);
        return v == null ? "" : v.toString();
    }

    /** First letter of a name, safe for characters outside the basic plane. */
    String initial(String s) {
        return (s == null || s.isEmpty()) ? "?" : new String(Character.toChars(s.codePointAt(0))).toUpperCase();
    }

    /** Builds a list URL that keeps the search text when the type filter changes. */
    String listUrl(String type, String keyword) throws java.io.UnsupportedEncodingException {
        StringBuilder url = new StringBuilder("publisher");
        String sep = "?";
        if (!type.isEmpty()) { url.append(sep).append("type=").append(URLEncoder.encode(type, "UTF-8")); sep = "&"; }
        if (!keyword.isEmpty()) { url.append(sep).append("q=").append(URLEncoder.encode(keyword, "UTF-8")); }
        return url.toString();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - Publisher Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/publisher.css">
</head>
<body class="aura">
<% boolean canEdit = com.bookstore.util.SessionUtil.isAdmin(request); %>
<% request.setAttribute("navActive", "publisher"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>
<%
    List<Publisher> publishers = (List<Publisher>) request.getAttribute("publishers");
    String flashMessage = textAttr(request, "msg");
    boolean flashSuccess = "success".equals(textAttr(request, "status"));
    String searchText = textAttr(request, "searchText");
    String activeType = textAttr(request, "typeFilter");
    int totalCount = intAttr(request, "totalCount");
    int localCount = intAttr(request, "localCount");
    int internationalCount = intAttr(request, "internationalCount");
    boolean filtered = !searchText.isEmpty() || !activeType.isEmpty();
    int rowNumber = 1;
%>
<div class="container">
    <div class="panel">

        <div class="pub-header">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-building me-2"></i>Publisher Management</h2>
                <p class="text-light opacity-75 mb-0">Manage publishing partners and contact details</p>
            </div>
            <div class="pub-header-actions">
                <a href="index.jsp" class="btn btn-outline-light"><i class="fa-solid fa-house me-1"></i>Home</a>
                <% if (canEdit) { %>
                <a href="publisher?action=new" class="btn btn-primary">
                    <i class="fa-solid fa-plus me-2"></i>Add publisher
                </a>
                <% } %>
            </div>
        </div>

        <% if (!flashMessage.isEmpty()) { %>
        <div class="alert <%= flashSuccess ? "alert-success" : "alert-danger" %> py-2 pub-flash" role="alert" id="flash">
            <span><%= esc(flashMessage) %></span>
            <button type="button" class="btn-close btn-close-white" aria-label="Dismiss message"
                    onclick="this.parentElement.remove()"></button>
        </div>
        <% } %>

        <div class="pub-stats">
            <div class="pub-stat">
                <span class="pub-stat-icon"><i class="fa-solid fa-building"></i></span>
                <div><div class="pub-stat-value"><%= totalCount %></div><div class="pub-stat-label">All publishers</div></div>
            </div>
            <div class="pub-stat">
                <span class="pub-stat-icon"><i class="fa-solid fa-location-dot"></i></span>
                <div><div class="pub-stat-value"><%= localCount %></div><div class="pub-stat-label">Local</div></div>
            </div>
            <div class="pub-stat">
                <span class="pub-stat-icon"><i class="fa-solid fa-globe"></i></span>
                <div><div class="pub-stat-value"><%= internationalCount %></div><div class="pub-stat-label">International</div></div>
            </div>
        </div>

        <div class="pub-toolbar">
            <form method="get" action="publisher" class="pub-search" role="search">
                <% if (!activeType.isEmpty()) { %><input type="hidden" name="type" value="<%= esc(activeType) %>"><% } %>
                <div class="pub-search-field">
                    <i class="fa-solid fa-magnifying-glass"></i>
                    <input type="search" name="q" class="form-control" value="<%= esc(searchText) %>"
                           placeholder="Search by ID, name, phone or location" aria-label="Search publishers">
                </div>
                <button type="submit" class="btn btn-primary">Search</button>
            </form>

            <nav class="pub-chips" aria-label="Filter by type">
                <a class="pub-chip <%= activeType.isEmpty() ? "active" : "" %>" href="<%= listUrl("", searchText) %>">
                    All <span class="pub-chip-count"><%= totalCount %></span></a>
                <a class="pub-chip <%= LocalPublisher.TYPE.equals(activeType) ? "active" : "" %>"
                   href="<%= listUrl(LocalPublisher.TYPE, searchText) %>">
                    <i class="fa-solid fa-location-dot"></i>Local <span class="pub-chip-count"><%= localCount %></span></a>
                <a class="pub-chip <%= InternationalPublisher.TYPE.equals(activeType) ? "active" : "" %>"
                   href="<%= listUrl(InternationalPublisher.TYPE, searchText) %>">
                    <i class="fa-solid fa-globe"></i>International <span class="pub-chip-count"><%= internationalCount %></span></a>
            </nav>
        </div>

        <% if (filtered && publishers != null) { %>
        <p class="pub-result-note">
            Showing <%= publishers.size() %> of <%= totalCount %> publishers.
            <a href="publisher" class="text-light">Clear filters</a>
        </p>
        <% } %>

        <% if (publishers != null && !publishers.isEmpty()) { %>
        <div class="table-responsive">
            <table class="table table-dark-glass table-hover rounded overflow-hidden align-middle">
                <thead>
                <tr>
                    <th>#</th><th>Publisher ID</th><th>Company</th><th>Type</th><th>Location</th><th>Phone</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>
                <tbody>
                <% for (Publisher p : publishers) {
                       boolean intl = InternationalPublisher.TYPE.equals(p.getType());
                       String viewUrl = "publisher?action=view&id=" + URLEncoder.encode(p.getId(), "UTF-8");
                %>
                <tr>
                    <td><%= String.format("%02d", rowNumber++) %></td>
                    <td class="pub-id"><%= esc(p.getId()) %></td>
                    <td>
                        <div class="pub-name-cell">
                            <span class="pub-avatar" aria-hidden="true"><%= esc(initial(p.getName())) %></span>
                            <a class="pub-name-link" href="<%= viewUrl %>"><%= esc(p.getName()) %></a>
                        </div>
                    </td>
                    <td>
                        <span class="badge <%= intl ? "bg-info text-dark" : "bg-success" %>">
                            <i class="fa-solid <%= intl ? "fa-globe" : "fa-location-dot" %> me-1"></i><%= intl ? "International" : "Local" %>
                        </span>
                    </td>
                    <td><%= esc(p.getExtra()) %></td>
                    <td><a class="pub-tel" href="tel:<%= esc(p.getPhone().replaceAll("[^0-9+]", "")) %>"><%= esc(p.getPhone()) %></a></td>
                    <td class="text-center">
                        <div class="pub-actions">
                            <a class="btn btn-sm btn-outline-light btn-action" href="<%= viewUrl %>"
                               title="View details" aria-label="View <%= esc(p.getName()) %>">
                                <i class="fa-solid fa-eye"></i></a>
                            <% if (canEdit) { %>
                            <a class="btn btn-sm btn-warning btn-action"
                               href="publisher?action=edit&id=<%= URLEncoder.encode(p.getId(), "UTF-8") %>"
                               title="Edit" aria-label="Edit <%= esc(p.getName()) %>">
                                <i class="fa-solid fa-pen-to-square"></i></a>
                            <button type="button" class="btn btn-sm btn-danger btn-action"
                                    data-delete-id="<%= esc(p.getId()) %>" data-delete-name="<%= esc(p.getName()) %>"
                                    title="Delete" aria-label="Delete <%= esc(p.getName()) %>">
                                <i class="fa-solid fa-trash"></i></button>
                            <% } %>
                        </div>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <% } else if (filtered) { %>
        <div class="pub-empty">
            <i class="fa-solid fa-magnifying-glass"></i>
            <h3>No publishers match your search</h3>
            <p>Check the spelling or try a different type.</p>
            <a href="publisher" class="btn btn-outline-light">Clear filters</a>
        </div>
        <% } else { %>
        <div class="pub-empty">
            <i class="fa-solid fa-building"></i>
            <h3>No publishers yet</h3>
            <% if (canEdit) { %>
            <p>Add your first publishing partner to get started.</p>
            <a href="publisher?action=new" class="btn btn-primary"><i class="fa-solid fa-plus me-2"></i>Add publisher</a>
            <% } else { %>
            <p>Publishers will appear here once an admin adds them.</p>
            <% } %>
        </div>
        <% } %>
    </div>
</div>

<% if (canEdit) { %>
<dialog id="deleteDialog" class="pub-dialog" aria-labelledby="deleteTitle">
    <form method="post" action="publisher">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="id" id="deleteId">
        <h3 id="deleteTitle">Delete this publisher?</h3>
        <p><strong id="deleteName"></strong> will be removed permanently. This can't be undone.</p>
        <div class="pub-dialog-actions">
            <button type="button" class="btn btn-outline-light" id="deleteCancel">Keep publisher</button>
            <button type="submit" class="btn btn-danger">Delete publisher</button>
        </div>
    </form>
</dialog>
<% } %>

<script>
(function () {
    var flash = document.getElementById('flash');
    if (flash && flash.classList.contains('alert-success')) {
        setTimeout(function () { if (flash.parentNode) { flash.remove(); } }, 5000);
    }

    var dialog = document.getElementById('deleteDialog');
    if (!dialog) { return; }
    var idField = document.getElementById('deleteId');
    var nameLabel = document.getElementById('deleteName');

    document.querySelectorAll('[data-delete-id]').forEach(function (button) {
        button.addEventListener('click', function () {
            var id = button.getAttribute('data-delete-id');
            var name = button.getAttribute('data-delete-name');
            if (typeof dialog.showModal !== 'function') {
                // Very old browsers: fall back to the built-in confirm box.
                if (confirm('Delete ' + name + '? This can\'t be undone.')) {
                    idField.value = id;
                    dialog.querySelector('form').submit();
                }
                return;
            }
            idField.value = id;
            nameLabel.textContent = name;
            dialog.showModal();
        });
    });

    document.getElementById('deleteCancel').addEventListener('click', function () { dialog.close(); });
    dialog.addEventListener('click', function (e) { if (e.target === dialog) { dialog.close(); } });
})();
</script>
</body>
</html>