<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.servlet.UserServlet" %>
<%
    String step = (String) request.getAttribute("resetStep");
    if (step == null) step = "lookup";
    boolean answering = "answer".equals(step);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - Forgot Password</title>
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
<% request.setAttribute("navActive", "login"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<main class="auth-wrap">
<div class="auth-card">
  <div class="text-center mb-4">
    <i class="fa-solid fa-key fa-3x mb-3"></i>
    <h2 class="fw-bold">Forgot Password</h2>
    <p class="text-light opacity-75"><%= answering ? "Answer your security question and choose a new password" : "Enter your username to find your account" %></p>
  </div>

  <ol class="steps" aria-label="Progress">
    <li class="<%= answering ? "done" : "on" %>"><b><%= answering ? "&#10003;" : "1" %></b>Username</li>
    <li class="<%= answering ? "on" : "" %>"><b>2</b>Verify &amp; reset</li>
  </ol>

  <% String msg = request.getParameter("msg");
     if (msg != null && !msg.isEmpty()) { %>
      <div class="alert <%= UserServlet.alertClass(request.getParameter("t")) %> py-2" role="alert"><%= UserServlet.esc(msg) %></div>
    <% } %>

  <% if (!answering) { %>
  <form action="${pageContext.request.contextPath}/forgot-password" method="POST">
    <input type="hidden" name="step" value="lookup">
    <input type="hidden" name="_csrf" value="<%= UserServlet.csrfToken(session) %>">
    <div class="mb-4">
      <label class="form-label"><i class="fa-solid fa-user me-2"></i>Username</label>
      <input type="text" name="username" class="form-control" placeholder="Enter your username" autocomplete="username" autofocus required>
    </div>
    <button type="submit" class="btn btn-custom mb-3">Continue</button>
  </form>
  <% } else { %>
  <form action="${pageContext.request.contextPath}/forgot-password" method="POST">
    <input type="hidden" name="step" value="reset">
    <input type="hidden" name="_csrf" value="<%= UserServlet.csrfToken(session) %>">
    <p class="small text-light opacity-75 mb-2">Account: <b><%= UserServlet.esc((String) request.getAttribute("resetUsername")) %></b>
      &middot; <a href="<%= request.getContextPath() %>/forgot-password?restart=1" class="text-decoration-none">Not you?</a></p>
    <div class="question-box"><%= UserServlet.esc((String) request.getAttribute("resetQuestion")) %></div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-key me-2"></i>Your answer</label>
      <input type="text" name="answer" class="form-control" placeholder="Type your answer" autocomplete="off" autofocus required>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-lock me-2"></i>New password</label>
      <input type="password" name="password" class="form-control" placeholder="Choose a new password" autocomplete="new-password" minlength="8" data-strength required>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-check me-2"></i>Confirm new password</label>
      <input type="password" name="confirm" class="form-control" placeholder="Re-enter the password" autocomplete="new-password" minlength="8" data-confirm required>
    </div>
    <button type="submit" class="btn btn-custom mb-3">Reset password</button>
  </form>
  <% } %>
  <div class="text-center">
    <p class="small text-light opacity-75"><a href="<%= request.getContextPath() %>/login.jsp" class="text-decoration-none">Back to login</a></p>
  </div>
</div>
</main>
</body>
</html>
