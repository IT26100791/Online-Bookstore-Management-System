<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User,com.bookstore.servlet.UserServlet" %>
<%
    User me = (User) request.getAttribute("me");
    if (me == null) { response.sendRedirect(request.getContextPath() + "/profile"); return; }
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - My Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/user-ui.css">
    <script defer src="<%= request.getContextPath() %>/js/user-ui.js"></script>
</head>
<body class="aura">
<% request.setAttribute("navActive", "profile"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
        <div>
            <h2 class="fw-bold"><i class="fa-solid fa-id-card me-2"></i>My Profile</h2>
            <p class="text-light opacity-75 mb-0">Update your photo, details and security settings</p>
        </div>
        <a href="<%= me.getHomePage() %>" class="btn btn-outline-light"><i class="fa-solid fa-arrow-left me-2"></i>Back</a>
    </div>

        <% String msg = request.getParameter("msg");
           if (msg != null && !msg.isEmpty()) { %>
        <div class="alert <%= UserServlet.alertClass(request.getParameter("t")) %> py-2" role="alert" data-toast="<%= "ok".equals(request.getParameter("t")) ? "yes" : "no" %>"><%= UserServlet.esc(msg) %></div>
        <% } %>

    <div class="profile-grid">
        <aside class="panel profile-side">
            <%= UserServlet.avatar(ctx, me, "avatar-xl") %>
            <h2><%= UserServlet.esc(me.getUsername()) %></h2>
            <span class="role-badge <%= me.getRoleBadgeClass() %>"><%= me.getRole() %></span>
            <p><i class="fa-solid fa-envelope fa-fw me-2"></i><%= me.getEmail().isEmpty() ? "No e-mail added" : UserServlet.esc(me.getEmail()) %></p>
            <p><i class="fa-solid fa-shield-halved fa-fw me-2"></i><%= me.getRoleDescription() %></p>
            <p><i class="fa-solid fa-circle-question fa-fw me-2"></i><%= me.hasSecurityQuestion() ? "Security question set" : "No security question yet" %></p>
        </aside>

        <div class="profile-sections">
            <section class="panel">
                <h3><i class="fa-solid fa-camera"></i>Profile photo</h3>
                <form action="<%= ctx %>/profile" method="POST" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="photo">
                    <input type="hidden" name="_csrf" value="<%= UserServlet.csrfToken(session) %>">
                    <div class="photo-field">
                        <%= UserServlet.avatar(ctx, me, "avatar-lg") %>
                        <div>
                            <input type="file" name="photo" class="form-control" accept="image/jpeg,image/png,image/gif,image/webp" data-photo-input required>
                            <small class="pw-hint">JPG, PNG, GIF or WEBP, up to 2 MB. Square photos look best.</small>
                        </div>
                    </div>
                    <div class="btn-row">
                        <button type="submit" class="btn btn-primary"><i class="fa-solid fa-upload me-2"></i>Upload photo</button>
                        <% if (me.hasAvatar()) { %>
                        <button type="submit" name="removePhoto" value="1" formnovalidate class="btn btn-danger"><i class="fa-solid fa-trash me-2"></i>Remove photo</button>
                        <% } %>
                    </div>
                </form>
            </section>

            <section class="panel">
                <h3><i class="fa-solid fa-user"></i>Account details</h3>
                <form action="<%= ctx %>/profile" method="POST">
                    <input type="hidden" name="action" value="info">
                    <input type="hidden" name="_csrf" value="<%= UserServlet.csrfToken(session) %>">
                    <div class="mb-3">
                        <label class="form-label">Username</label>
                        <input type="text" class="form-control" value="<%= UserServlet.esc(me.getUsername()) %>" disabled>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="email">E-mail <span class="opt">(optional)</span></label>
                        <input type="email" id="email" name="email" class="form-control" value="<%= UserServlet.esc(me.getEmail()) %>" placeholder="name@example.com" maxlength="100">
                    </div>
                    <button type="submit" class="btn btn-primary">Save details</button>
                </form>
            </section>

            <section class="panel">
                <h3><i class="fa-solid fa-lock"></i>Change password</h3>
                <form action="<%= ctx %>/profile" method="POST">
                    <input type="hidden" name="action" value="password">
                    <input type="hidden" name="_csrf" value="<%= UserServlet.csrfToken(session) %>">
                    <div class="mb-3">
                        <label class="form-label">Current password</label>
                        <input type="password" name="current" class="form-control" autocomplete="current-password" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">New password</label>
                        <input type="password" name="password" class="form-control" autocomplete="new-password" minlength="8" data-strength required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Confirm new password</label>
                        <input type="password" name="confirm" class="form-control" autocomplete="new-password" minlength="8" data-confirm required>
                    </div>
                    <button type="submit" class="btn btn-primary">Change password</button>
                </form>
            </section>

            <section class="panel">
                <h3><i class="fa-solid fa-circle-question"></i>Security question</h3>
                <p class="text-light opacity-75">Used by <b>Forgot password</b> if you ever lose your password.</p>
                <form action="<%= ctx %>/profile" method="POST">
                    <input type="hidden" name="action" value="security">
                    <input type="hidden" name="_csrf" value="<%= UserServlet.csrfToken(session) %>">
                    <div class="mb-3">
                        <label class="form-label">Question</label>
                        <select name="question" class="form-select" required>
                            <option value="" disabled<%= (me.getSecurityQuestion() < 0 ? " selected" : "") %>>Choose a question&hellip;</option>
        <% for (int qi = 0; qi < UserServlet.QUESTIONS.length; qi++) { %>
        <option value="<%= qi %>"<%= (me.getSecurityQuestion() == qi ? " selected" : "") %>><%= UserServlet.esc(UserServlet.QUESTIONS[qi]) %></option>
        <% } %>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Answer</label>
                        <input type="text" name="answer" class="form-control" minlength="2" maxlength="60" autocomplete="off" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Current password <span class="opt">(to confirm it is you)</span></label>
                        <input type="password" name="current" class="form-control" autocomplete="current-password" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Save security question</button>
                </form>
            </section>
        </div>
    </div>
</div>
</body>
</html>

