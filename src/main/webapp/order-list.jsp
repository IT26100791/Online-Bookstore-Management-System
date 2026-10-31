<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.Order" %>
<%!
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Order Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(30, 27, 75, 0.85)),
            url('https://images.unsplash.com/photo-1457369804613-52c61a468e7d?q=80&w=1920&auto=format&fit=crop');
            background-size: cover !important;
            background-position: center !important;
            background-repeat: no-repeat !important;
            background-attachment: fixed !important;
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: #f8fafc;
            padding: 40px 0;
        }
        .glass-container {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 16px;
            padding: 30px;
            box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
        }
        .table { color: #f8fafc; vertical-align: middle; }
        .table-dark-glass { background: rgba(255, 255, 255, 0.03); }
        .table-dark-glass th {
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
            border-bottom: 2px solid rgba(255, 255, 255, 0.2);
        }
        .table-dark-glass td {
            border-color: rgba(255, 255, 255, 0.08);
            background: transparent;
            color: #e2e8f0;
        }
        .btn-action { padding: 6px 12px; font-size: 0.875rem; border-radius: 6px; }
    </style>
</head>
<body>

<div class="container">
    <div class="glass-container">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-cart-shopping me-2" style="color: #818cf8;"></i>Order Management Dashboard</h2>
                <p class="text-light opacity-75 mb-0">Track and manage bookstore customer orders</p>
            </div>
            <a href="order?action=new" class="btn btn-primary" style="background: #6366f1; border: none;"><i class="fa-solid fa-plus me-2"></i>Place New Order</a>
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
                    <th>Customer Name</th>
                    <th>Book Title</th>
                    <th>Quantity</th>
                    <th>Total Amount (LKR)</th>
                    <th>Status</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Order> orders = (List<Order>) request.getAttribute("orders");
                    if (orders != null) {
                        for (Order o : orders) {
                            String st = o.getStatus();
                            String badge = "bg-secondary";
                            if ("Delivered".equalsIgnoreCase(st)) badge = "bg-success";
                            else if ("Shipped".equalsIgnoreCase(st)) badge = "bg-info";
                            else if ("Processing".equalsIgnoreCase(st)) badge = "bg-primary";
                            else if ("Cancelled".equalsIgnoreCase(st)) badge = "bg-danger";
                            else if ("Pending".equalsIgnoreCase(st)) badge = "bg-warning text-dark";
                %>
                <tr>
                    <td><%= String.format("%03d", o.getOrderId()) %></td>
                    <td><%= esc(o.getCustomerName()) %></td>
                    <td><%= esc(o.getBookTitle()) %></td>
                    <td><%= o.getQuantity() %></td>
                    <td><%= String.format("%.2f", o.getTotalAmount()) %></td>
                    <td><span class="badge <%= badge %>"><%= esc(st) %></span></td>
                    <td class="text-center">
                        <a class="btn btn-sm btn-warning btn-action me-1"
                           href="order?action=edit&id=<%= o.getOrderId() %>">
                            <i class="fa-solid fa-pen-to-square"></i></a>
                        <form method="post" action="order" style="display:inline"
                              onsubmit="return confirm('Delete this order?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="orderId" value="<%= o.getOrderId() %>">
                            <button type="submit" class="btn btn-sm btn-danger btn-action"><i class="fa-solid fa-trash"></i></button>
                        </form>
                    </td>
                </tr>
                <%      }
                }
                %>
                </tbody>
            </table>
        </div>

        <% if (orders == null || orders.isEmpty()) { %>
        <div class="alert alert-secondary py-2 mb-0">No orders yet. Use "Place New Order" to create the first one.</div>
        <% } %>
    </div>
</div>

</body>
</html>
