<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.Publisher" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    Publisher publisher = (Publisher) request.getAttribute("publisher");
    boolean editing = (publisher != null);
    String currentType = editing ? publisher.getType() : "LOCAL";
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - <%= editing ? "Edit" : "Add" %> Publisher</title>
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
        .form-control, .form-select {
            background: rgba(255,255,255,0.08);
            border: 1px solid rgba(255,255,255,0.2);
            color: #fff;
        }
        .form-select option {
            background: #1e1b4b;
            color: #fff;
        }
        .form-control:focus, .form-select:focus {
            background: rgba(255,255,255,0.12);
            color: #fff;
        }
        .form-control[readonly] { opacity: 0.7; }
    </style>
</head>
<body>
<div class="container">
    <div class="glass-container">
        <h2 class="fw-bold mb-4">
            <i class="fa-solid fa-building me-2" style="color: #818cf8;"></i><%= editing ? "Edit Publisher" : "Add New Publisher" %>
        </h2>

        <% if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-warning py-2"><%= esc(msg) %></div>
        <% } %>

        <form method="post" action="publisher">
            <input type="hidden" name="action" value="<%= editing ? "update" : "add" %>">

            <div class="mb-3">
                <label class="form-label">Publisher ID</label>
                <input type="text" name="id" class="form-control" required
                       value="<%= editing ? esc(publisher.getId()) : "" %>" <%= editing ? "readonly" : "" %>>
            </div>
            <div class="mb-3">
                <label class="form-label">Company Name</label>
                <input type="text" name="name" class="form-control" required
                       value="<%= editing ? esc(publisher.getName()) : "" %>">
            </div>
            <div class="mb-3">
                <label class="form-label">Phone Number</label>
                <input type="text" name="phone" class="form-control" required
                       value="<%= editing ? esc(publisher.getPhone()) : "" %>">
            </div>
            <div class="mb-3">
                <label class="form-label">Publisher Type</label>
                <select name="type" id="type" class="form-select" required>
                    <option value="LOCAL" <%= "LOCAL".equals(currentType) ? "selected" : "" %>>Local Publisher</option>
                    <option value="INTERNATIONAL" <%= "INTERNATIONAL".equals(currentType) ? "selected" : "" %>>International Publisher</option>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label" id="extraLabel">District</label>
                <input type="text" name="extra" id="extra" class="form-control" required
                       value="<%= editing ? esc(publisher.getExtra()) : "" %>">
            </div>
            <p class="small opacity-75">Commas are not allowed in any field.</p>

            <button type="submit" class="btn btn-primary" style="background: #6366f1; border: none;">
                <i class="fa-solid fa-floppy-disk me-1"></i>Save</button>
            <a href="publisher" class="btn btn-outline-light ms-2">Cancel</a>
        </form>
    </div>
</div>

<script>
    // Local -> District, International -> Country
    var typeSelect = document.getElementById('type');
    var extraLabel = document.getElementById('extraLabel');
    function syncLabel() {
        extraLabel.textContent = typeSelect.value === 'INTERNATIONAL' ? 'Country' : 'District';
    }
    typeSelect.addEventListener('change', syncLabel);
    syncLabel();
</script>
</body>
</html>
