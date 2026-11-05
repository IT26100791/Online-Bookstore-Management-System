<%@ page contentType="text/html;charset=UTF-8"
         import="com.bookstore.model.Publisher,com.bookstore.model.LocalPublisher,com.bookstore.model.InternationalPublisher" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    /** Value re-posted after a validation error, otherwise the fallback (the saved value). */
    String pick(HttpServletRequest r, String name, String fallback) {
        Object v = r.getAttribute(name);
        return v == null ? fallback : v.toString();
    }
%>
<%
    Publisher publisher = (Publisher) request.getAttribute("publisher");
    boolean editing = Boolean.TRUE.equals(request.getAttribute("editing")) || publisher != null;

    String valueId = pick(request, "formId", publisher == null ? "" : publisher.getId());
    String valueName = pick(request, "formName", publisher == null ? "" : publisher.getName());
    String valuePhone = pick(request, "formPhone", publisher == null ? "" : publisher.getPhone());
    String valueExtra = pick(request, "formExtra", publisher == null ? "" : publisher.getExtra());
    String valueType = pick(request, "formType", publisher == null ? LocalPublisher.TYPE : publisher.getType());
    boolean international = InternationalPublisher.TYPE.equals(valueType);

    String formError = (String) request.getAttribute("formError");
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
        <h2 class="fw-bold mb-1">
            <i class="fa-solid fa-building me-2"></i><%= editing ? "Edit publisher" : "Add a new publisher" %>
        </h2>
        <p class="text-light opacity-75 mb-4">
            <%= editing ? "Update the contact details below." : "Fill in the details to add a publishing partner." %>
        </p>

        <% if (formError != null && !formError.isEmpty()) { %>
        <div class="alert alert-danger py-2" role="alert"><%= esc(formError) %></div>
        <% } else if (msg != null && !msg.isEmpty()) { %>
        <div class="alert alert-warning py-2" role="alert"><%= esc(msg) %></div>
        <% } %>

        <form method="post" action="publisher" autocomplete="off">
            <input type="hidden" name="action" value="<%= editing ? "update" : "add" %>">

            <div class="mb-3">
                <span class="form-label d-block" id="typeLabel">Publisher type</span>
                <div class="type-picker" role="radiogroup" aria-labelledby="typeLabel">
                    <label class="type-card">
                        <input type="radio" name="type" value="<%= LocalPublisher.TYPE %>" <%= international ? "" : "checked" %>>
                        <span class="type-card-body">
                            <i class="fa-solid fa-location-dot"></i>
                            <strong>Local</strong>
                            <small>Based in Sri Lanka</small>
                        </span>
                    </label>
                    <label class="type-card">
                        <input type="radio" name="type" value="<%= InternationalPublisher.TYPE %>" <%= international ? "checked" : "" %>>
                        <span class="type-card-body">
                            <i class="fa-solid fa-globe"></i>
                            <strong>International</strong>
                            <small>Based in another country</small>
                        </span>
                    </label>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label" for="id">Publisher ID</label>
                <input type="text" id="id" name="id" class="form-control" required maxlength="20"
                       pattern="[^,]+" title="Use letters, numbers or dashes. Commas are not allowed."
                       placeholder="e.g. PUB001"
                       value="<%= esc(valueId) %>" <%= editing ? "readonly" : "" %>>
                <% if (editing) { %><div class="field-hint">The ID can't be changed after a publisher is created.</div><% } %>
            </div>

            <div class="mb-3">
                <label class="form-label" for="name">Company name</label>
                <input type="text" id="name" name="name" class="form-control" required maxlength="60"
                       pattern="[^,]+" title="Commas are not allowed." placeholder="e.g. Sarasavi Publishers"
                       value="<%= esc(valueName) %>">
            </div>

            <div class="mb-3">
                <label class="form-label" for="phone">Phone number</label>
                <input type="tel" id="phone" name="phone" class="form-control" required maxlength="20"
                       pattern="[0-9+\-\(\) ]{7,20}" title="Use 7 to 20 characters: digits, +, dashes, brackets or spaces."
                       placeholder="e.g. 011 2 123 456"
                       value="<%= esc(valuePhone) %>">
            </div>

            <div class="mb-4">
                <label class="form-label" for="extra" id="extraLabel"><%= international ? "Country" : "District" %></label>
                <input type="text" id="extra" name="extra" class="form-control" required maxlength="40"
                       pattern="[^,]+" title="Commas are not allowed."
                       placeholder="<%= international ? "e.g. United Kingdom" : "e.g. Colombo" %>"
                       value="<%= esc(valueExtra) %>">
            </div>

            <button type="submit" class="btn btn-primary">
                <i class="fa-solid fa-floppy-disk me-1"></i><%= editing ? "Save changes" : "Add publisher" %></button>
            <a href="publisher" class="btn btn-outline-light ms-2">Cancel</a>
        </form>
    </div>
</div>

<script>
(function () {
    var text = {
        LOCAL: { label: 'District', placeholder: 'e.g. Colombo' },
        INTERNATIONAL: { label: 'Country', placeholder: 'e.g. United Kingdom' }
    };
    var label = document.getElementById('extraLabel');
    var input = document.getElementById('extra');

    document.querySelectorAll('input[name="type"]').forEach(function (radio) {
        radio.addEventListener('change', function () {
            var t = text[radio.value];
            if (t) {
                label.textContent = t.label;
                input.placeholder = t.placeholder;
            }
        });
    });
})();
</script>
</body>
</html>