<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Register User</title></head>
<body>
<h2>Register User</h2>
<% String msg = request.getParameter("msg"); if (msg != null) { %>
  <p style="color:red"><%= msg.replace("<", "&lt;") %></p>
<% } %>
<form method="post" action="user">
  <input type="hidden" name="action" value="add">
  Username: <input type="text" name="username" required><br><br>
  Password: <input type="password" name="password" required><br><br>
  Role:
  <select name="role">
    <option>Customer</option>
    <option>Admin</option>
  </select><br><br>
  <button type="submit">Register</button>
  <a href="user">Back to list</a>
</form>
</body>
</html>