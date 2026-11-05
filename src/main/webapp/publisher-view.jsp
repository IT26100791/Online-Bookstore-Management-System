<%@ page contentType="text/html;charset=UTF-8"
         import="java.net.URLEncoder,com.bookstore.model.Publisher,com.bookstore.model.InternationalPublisher" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    String initial(String s) {
        return (s == null || s.isEmpty()) ? "?" : new String(Character.toChars(s.codePointAt(0))).toUpperCase();
    }
%>
<%
    Publisher p = (Publisher) request.getAttribute("publisher");
    if (p == null) { response.sendRedirect("publisher"); return; }
    boolean intl = InternationalPublisher.TYPE.equals(p.getType());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - <%= esc(p.getName()) %></title>
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
<div class="container">
    <div class="panel panel-narrow">

        <div class="pub-profile">
            <span class="pub-avatar pub-avatar-lg" aria-hidden="true"><%= esc(initial(p.getName())) %></span>
            <div>
                <h2 class="fw-bold"><%= esc(p.getName()) %></h2>
                <span class="badge <%= intl ? "bg-info text-dark" : "bg-success" %>">
                    <i class="fa-solid <%= intl ? "fa-globe" : "fa-location-dot" %> me-1"></i><%= intl ? "International" : "Local" %>
                </span>
            </div>
        </div>

        <dl class="pub-detail-list">
            <div class="pub-detail-row"><dt>Publisher ID</dt><dd><%= esc(p.getId()) %></dd></div>
            <div class="pub-detail-row"><dt>Company name</dt><dd><%= esc(p.getName()) %></dd></div>
            <div class="pub-detail-row">
                <dt>Phone</dt>
                <dd><a class="pub-tel" href="tel:<%= esc(p.getPhone().replaceAll("[^0-9+]", "")) %>"><%= esc(p.getPhone()) %></a></dd>
            </div>
            <div class="pub-detail-row"><dt><%= esc(p.getExtraLabel()) %></dt><dd><%= esc(p.getExtra()) %></dd></div>
        </dl>

        <% if (canEdit) { %>
        <a href="publisher?action=edit&id=<%= URLEncoder.encode(p.getId(), "UTF-8") %>" class="btn btn-primary">
            <i class="fa-solid fa-pen-to-square me-1"></i>Edit publisher</a>
        <% } %>
        <a href="publisher" class="btn btn-outline-light <%= canEdit ? "ms-2" : "" %>">
            <i class="fa-solid fa-arrow-left me-1"></i>Back to publishers</a>
    </div>
</div>
</body>
</html>