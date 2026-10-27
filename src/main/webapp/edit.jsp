<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User" %>
<% User user = (User) request.getAttribute("user"); %>
<html>
<head><title>Edit User</title></head>
<body>
<h2>Edit User: <%= user.getUsername().replace("<", "&lt;") %></h2>
<form method="post" action="user">
  <input type="hidden" name="action" value="update">
  <input type="hidden" name="username" value="<%= user.getUsername().replace("\"", "&quot;") %>">
  New Password: <input type="password" name="password" required><br><br>
  Role:
  <select name="role">
    <option <%= "Customer".equals(user.getRole()) ? "selected" : "" %>>Customer</option>
    <option <%= "Admin".equals(user.getRole()) ? "selected" : "" %>>Admin</option>
  </select><br><br>
  <button type="submit">Update</button>
  <a href="user">Cancel</a>
</form>
</body>
</html>