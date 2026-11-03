<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.Order,com.bookstore.model.User" %>
<%! private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    } %>
<%
    User me = (User) session.getAttribute("loggedUser");
    boolean admin = me != null && "Admin".equalsIgnoreCase(me.getRole());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Orders</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<% request.setAttribute("navActive", "order"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">
    <div class="panel">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-cart-shopping me-2"></i><%= admin ? "Order Management" : "My Orders" %></h2>
                <p class="text-light opacity-75 mb-0"><%= admin ? "Track and manage all customer orders" : "Your placed orders" %></p>
            </div>
            <a href="order?action=new" class="btn btn-primary"><i class="fa-solid fa-plus me-2"></i>Place New Order</a>
        </div>

        <% String msg = (String) request.getAttribute("msg");
           if (msg != null && !msg.isEmpty()) { %>
        <div class="alert alert-info py-2"><%= esc(msg) %></div>
        <% } %>

        <div class="table-responsive">
            <table class="table table-dark-glass table-hover rounded overflow-hidden">
                <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Customer</th>
                    <th>Book</th>
                    <th>Status</th>
                    <% if (admin) { %><th class="text-center">Actions</th><% } %>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Order> orders = (List<Order>) request.getAttribute("orders");
                    if (orders == null || orders.isEmpty()) {
                %>
                <tr><td colspan="<%= admin ? 5 : 4 %>" class="text-center opacity-75">No orders found</td></tr>
                <%
                    } else {
                        for (Order o : orders) {
                %>
                <tr>
                    <td><%= esc(o.getId()) %></td>
                    <td><%= esc(o.getCustomerName()) %></td>
                    <td><%= esc(o.getBookTitle()) %></td>
                    <td><span class="badge bg-secondary"><%= esc(o.getStatus()) %></span></td>
                    <% if (admin) { %>
                    <td class="text-center">
                        <a class="btn btn-sm btn-warning btn-action me-1"
                           href="order?action=edit&id=<%= java.net.URLEncoder.encode(o.getId(), "UTF-8") %>">
                            <i class="fa-solid fa-pen-to-square"></i></a>
                        <form method="post" action="order" style="display:inline"
                              onsubmit="return confirm('Delete this order?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= esc(o.getId()) %>">
                            <button type="submit" class="btn btn-sm btn-danger btn-action"><i class="fa-solid fa-trash"></i></button>
                        </form>
                    </td>
                    <% } %>
                </tr>
                <%      }
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>