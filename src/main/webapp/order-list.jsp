<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.*" %>
<%! private static String esc(String s) {
    return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
}
    private static String money(double v) {
        return String.format(Locale.US, "Rs. %,.2f", v);
    }
    private static String badgeClass(String status) {
        if (status == null) return "bg-secondary";
        switch (status.toLowerCase()) {
            case "pending":    return "bg-warning text-dark";
            case "processing": return "bg-info text-dark";
            case "shipped":    return "bg-primary";
            case "delivered":  return "bg-success";
            case "cancelled":  return "bg-danger";
            default:           return "bg-secondary";
        }
    } %>
<%
    User me = (User) session.getAttribute("loggedUser");
    boolean admin = me != null && "Admin".equalsIgnoreCase(me.getRole());

    List<Order> orders = (List<Order>) request.getAttribute("orders");
    if (orders == null) orders = new ArrayList<>();

    int pending = 0, delivered = 0;
    double revenue = 0;
    for (Order o : orders) {
        if ("Pending".equalsIgnoreCase(o.getStatus())) pending++;
        if ("Delivered".equalsIgnoreCase(o.getStatus())) delivered++;
        if (!"Cancelled".equalsIgnoreCase(o.getStatus())) revenue += o.calculateTotal();   // polymorphic call
    }
    int colCount = admin ? 9 : 8;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - Orders</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
    <style>
        .ob-stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(170px, 1fr)); gap: .9rem; margin-bottom: 1.5rem; }
        .ob-stat { display: flex; align-items: center; gap: .9rem; padding: .9rem 1.1rem; border-radius: 14px;
            background: rgba(255,255,255,.07); border: 1px solid rgba(255,255,255,.16); }
        .ob-stat .ob-icon { width: 2.6rem; height: 2.6rem; border-radius: 50%; display: grid; place-items: center;
            background: rgba(255,255,255,.14); font-size: 1.1rem; flex: none; }
        .ob-stat .ob-num { font-size: 1.35rem; font-weight: 700; line-height: 1.1; }
        .ob-stat .ob-label { font-size: .82rem; opacity: .75; }

        .ob-toolbar { display: flex; flex-wrap: wrap; gap: .75rem; align-items: center; margin-bottom: 1rem; }
        .ob-search { position: relative; flex: 1 1 240px; max-width: 380px; }
        .ob-search i { position: absolute; left: .85rem; top: 50%; transform: translateY(-50%); opacity: .6; }
        .ob-search input { padding-left: 2.4rem; }
        .ob-filters { display: flex; flex-wrap: wrap; gap: .4rem; }
        .ob-filter { border: 1px solid rgba(255,255,255,.3); background: rgba(255,255,255,.06); color: inherit;
            border-radius: 999px; padding: .25rem .85rem; font-size: .85rem; cursor: pointer; transition: background .2s; }
        .ob-filter:hover { background: rgba(255,255,255,.16); }
        .ob-filter.active { background: rgba(255,255,255,.25); border-color: rgba(255,255,255,.8); font-weight: 700; }

        .ob-type-chip { display: inline-flex; align-items: center; gap: .4rem; font-size: .85rem; white-space: nowrap; }
        .ob-empty { text-align: center; padding: 2.5rem 1rem; }
        .ob-empty i { font-size: 2.5rem; opacity: .6; margin-bottom: .75rem; }
        .ob-total { font-weight: 700; white-space: nowrap; }
        #noMatch { display: none; }
    </style>
</head>
<body class="aura">
<% request.setAttribute("navActive", "order"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">
    <div class="panel">
        <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-cart-shopping me-2"></i><%= admin ? "Order Management" : "My Orders" %></h2>
                <p class="text-light opacity-75 mb-0"><%= admin ? "Track and manage all customer orders" : "Your placed orders" %></p>
            </div>
            <a href="order?action=new" class="btn btn-primary"><i class="fa-solid fa-plus me-2"></i>Place New Order</a>
        </div>

        <% String msg = (String) request.getAttribute("msg");
            if (msg != null && !msg.isEmpty()) { %>
        <div class="alert alert-info alert-dismissible fade show py-2" role="alert">
            <i class="fa-solid fa-circle-info me-2"></i><%= esc(msg) %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <% } %>

        <!-- Summary cards -->
        <div class="ob-stats">
            <div class="ob-stat"><span class="ob-icon"><i class="fa-solid fa-receipt"></i></span>
                <div><div class="ob-num"><%= orders.size() %></div><div class="ob-label"><%= admin ? "Total orders" : "Your orders" %></div></div></div>
            <div class="ob-stat"><span class="ob-icon"><i class="fa-solid fa-hourglass-half"></i></span>
                <div><div class="ob-num"><%= pending %></div><div class="ob-label">Pending</div></div></div>
            <div class="ob-stat"><span class="ob-icon"><i class="fa-solid fa-circle-check"></i></span>
                <div><div class="ob-num"><%= delivered %></div><div class="ob-label">Delivered</div></div></div>
            <div class="ob-stat"><span class="ob-icon"><i class="fa-solid fa-coins"></i></span>
                <div><div class="ob-num"><%= money(revenue) %></div><div class="ob-label"><%= admin ? "Order value (excl. cancelled)" : "Total spent (excl. cancelled)" %></div></div></div>
        </div>

        <% if (orders.isEmpty()) { %>
        <div class="ob-empty">
            <i class="fa-solid fa-book-open"></i>
            <h5 class="fw-bold">No orders yet</h5>
            <p class="opacity-75 mb-3">Pick a book and place your first order.</p>
            <a href="order?action=new" class="btn btn-primary"><i class="fa-solid fa-plus me-2"></i>Place New Order</a>
        </div>
        <% } else { %>

        <!-- Search and status filter -->
        <div class="ob-toolbar">
            <div class="ob-search">
                <i class="fa-solid fa-magnifying-glass"></i>
                <input type="search" id="orderSearch" class="form-control" placeholder="Search by ID, customer or book" aria-label="Search orders">
            </div>
            <div class="ob-filters" id="statusFilters" role="group" aria-label="Filter by status">
                <button type="button" class="ob-filter active" data-status="">All</button>
                <button type="button" class="ob-filter" data-status="pending">Pending</button>
                <button type="button" class="ob-filter" data-status="processing">Processing</button>
                <button type="button" class="ob-filter" data-status="shipped">Shipped</button>
                <button type="button" class="ob-filter" data-status="delivered">Delivered</button>
                <button type="button" class="ob-filter" data-status="cancelled">Cancelled</button>
            </div>
        </div>

        <div class="table-responsive">
            <table class="table table-dark-glass table-hover rounded overflow-hidden align-middle" id="orderTable">
                <thead>
                <tr>
                    <th>Order ID</th>
                    <% if (admin) { %><th>Customer</th><% } %>
                    <th>Book</th>
                    <th>Type</th>
                    <th>Payment</th>
                    <th class="text-center">Qty</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>
                <tbody>
                <% for (Order o : orders) { %>
                <tr data-status="<%= esc(o.getStatus().toLowerCase()) %>"
                    data-search="<%= esc((o.getId() + " " + o.getCustomerName() + " " + o.getBookTitle()).toLowerCase()) %>">
                    <td><%= esc(o.getId()) %></td>
                    <% if (admin) { %><td><%= esc(o.getCustomerName()) %></td><% } %>
                    <td><%= esc(o.getBookTitle()) %></td>
                    <td><span class="ob-type-chip"><i class="fa-solid <%= "EBOOK".equals(o.getTypeCode()) ? "fa-tablet-screen-button" : "fa-book" %>"></i><%= esc(o.getOrderType()) %></span></td>
                    <td><span class="ob-type-chip"><i class="fa-solid <%= "BANK".equals(o.getPaymentMethod()) ? "fa-building-columns" : "CARD".equals(o.getPaymentMethod()) ? "fa-credit-card" : "fa-money-bill-wave" %>"></i><%= esc(o.getPaymentLabel()) %></span>
                        <% if (o.hasReceipt()) { %><a href="order?action=receipt&id=<%= java.net.URLEncoder.encode(o.getId(), "UTF-8") %>" target="_blank" rel="noopener" class="ms-1" title="View receipt" aria-label="View receipt for order <%= esc(o.getId()) %>"><i class="fa-solid fa-paperclip"></i></a><% } %></td>
                    <td class="text-center"><%= o.getQuantity() %></td>
                    <td class="ob-total"><%= money(o.calculateTotal()) %></td>
                    <td><span class="badge <%= badgeClass(o.getStatus()) %>"><%= esc(o.getStatus()) %></span></td>
                    <td class="text-center text-nowrap">
                        <a class="btn btn-sm btn-info btn-action me-1" title="View details" aria-label="View order <%= esc(o.getId()) %>"
                           href="order?action=view&id=<%= java.net.URLEncoder.encode(o.getId(), "UTF-8") %>">
                            <i class="fa-solid fa-eye"></i></a>
                        <% if (admin) { %>
                        <a class="btn btn-sm btn-warning btn-action me-1" title="Edit" aria-label="Edit order <%= esc(o.getId()) %>"
                           href="order?action=edit&id=<%= java.net.URLEncoder.encode(o.getId(), "UTF-8") %>">
                            <i class="fa-solid fa-pen-to-square"></i></a>
                        <form method="post" action="order" style="display:inline"
                              onsubmit="return confirm('Delete this order? This cannot be undone.');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= esc(o.getId()) %>">
                            <button type="submit" class="btn btn-sm btn-danger btn-action" title="Delete" aria-label="Delete order <%= esc(o.getId()) %>"><i class="fa-solid fa-trash"></i></button>
                        </form>
                        <% } %>
                    </td>
                </tr>
                <% } %>
                <tr id="noMatch"><td colspan="<%= colCount %>" class="text-center opacity-75 py-4">No orders match your search.</td></tr>
                </tbody>
            </table>
        </div>
        <p class="small opacity-75 mb-0" id="resultCount"></p>
        <% } %>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    (function () {
        var search = document.getElementById('orderSearch');
        var table = document.getElementById('orderTable');
        if (!search || !table) return;

        var rows = Array.prototype.slice.call(table.querySelectorAll('tbody tr[data-status]'));
        var filters = document.querySelectorAll('#statusFilters .ob-filter');
        var noMatch = document.getElementById('noMatch');
        var counter = document.getElementById('resultCount');
        var activeStatus = '';

        function apply() {
            var q = search.value.trim().toLowerCase();
            var shown = 0;
            rows.forEach(function (r) {
                var ok = (!activeStatus || r.dataset.status === activeStatus) &&
                    (!q || r.dataset.search.indexOf(q) !== -1);
                r.style.display = ok ? '' : 'none';
                if (ok) shown++;
            });
            noMatch.style.display = shown === 0 ? 'table-row' : 'none';
            counter.textContent = 'Showing ' + shown + ' of ' + rows.length + ' orders';
        }

        search.addEventListener('input', apply);
        filters.forEach(function (btn) {
            btn.addEventListener('click', function () {
                filters.forEach(function (b) { b.classList.remove('active'); });
                btn.classList.add('active');
                activeStatus = btn.dataset.status;
                apply();
            });
        });
        apply();
    })();
</script>
</body>
</html>
