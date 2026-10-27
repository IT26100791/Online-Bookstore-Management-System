<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.User" %>
<html>
<head><title>User List</title></head>
<body>
<h2>User Directory</h2>
<% String msg = (String) request.getAttribute("msg"); if (msg != null) { %>
  <p style="color:green"><%= msg.replace("<", "&lt;") %></p>
<% } %>
<a href="user?action=new">+ Register new user</a>
<table border="1" cellpadding="6">
  <tr><th>Username</th><th>Role</th><th>Description</th><th>Actions</th></tr>
<%
  List<User> users = (List<User>) request.getAttribute("users");
  for (User u : users) {
%>
  <tr>
    <td><%= u.getUsername().replace("<", "&lt;") %></td>
    <td><%= u.getRole() %></td>
    <td><%= u.getRoleDescription() %></td>
    <td>
      <a href="user?action=edit&username=<%= java.net.URLEncoder.encode(u.getUsername(), "UTF-8") %>">Edit</a>
      <form method="post" action="user" style="display:inline"
            onsubmit="return confirm('Delete this user?');">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="username" value="<%= u.getUsername().replace("\"", "&quot;") %>">
        <button type="submit">Delete</button>
      </form>
    </td>
  </tr>
<% } %>
</table>
</body>
</html>