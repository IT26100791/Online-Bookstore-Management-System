<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.Publisher" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    Publisher p = (Publisher) request.getAttribute("publisher");
    if (p == null) { response.sendRedirect("publisher"); return; }
    boolean intl = "INTERNATIONAL".equals(p.getType());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Publisher Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(30, 27, 75, 0.85)),
            url('https://images.unsplash.com/photo-1457369804613-52c61a468e7d?q=80&w=1920&auto=format&fit=crop');
            background-size: cover !important;
            background-attachment: fixed !important;
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: #f8fafc;
            padding: 40px 0;
        }
        .glass-container {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 16px;
            padding: 30px;
            max-width: 520px;
            margin: 0 auto;
            box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
        }
        .detail-row { display: flex; justify-content: space-between; padding: 10px 0;
                      border-bottom: 1px solid rgba(255, 255, 255, 0.08); }
        .detail-label { color: #94a3b8; }
    </style>
</head>
<body>
<div class="container">
    <div class="glass-container">
        <h2 class="fw-bold mb-4">
            <i class="fa-solid fa-building me-2" style="color: #818cf8;"></i>Publisher Details
        </h2>

        <div class="detail-row"><span class="detail-label">Publisher ID</span><span><%= esc(p.getId()) %></span></div>
        <div class="detail-row"><span class="detail-label">Company Name</span><span><%= esc(p.getName()) %></span></div>
        <div class="detail-row"><span class="detail-label">Phone</span><span><%= esc(p.getPhone()) %></span></div>
        <div class="detail-row">
            <span class="detail-label">Type</span>
            <span class="badge <%= intl ? "bg-info text-dark" : "bg-success" %>">
                <i class="fa-solid <%= intl ? "fa-globe" : "fa-location-dot" %> me-1"></i><%= intl ? "International" : "Local" %>
            </span>
        </div>
        <div class="detail-row"><span class="detail-label"><%= esc(p.getExtraLabel()) %></span><span><%= esc(p.getExtra()) %></span></div>

        <div class="mt-4">
            <a href="publisher?action=edit&id=<%= java.net.URLEncoder.encode(p.getId(), "UTF-8") %>"
               class="btn btn-primary" style="background: #6366f1; border: none;">
                <i class="fa-solid fa-pen-to-square me-1"></i>Edit</a>
            <a href="publisher" class="btn btn-outline-light ms-2">Back</a>
        </div>
    </div>
</div>
</body>
</html>
