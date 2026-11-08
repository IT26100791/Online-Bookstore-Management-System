<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.*" %>
<%! private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
    private static String money(double v) {
        return String.format(Locale.US, "Rs. %,.2f", Math.abs(v));
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
    Order order = (Order) request.getAttribute("order");
    if (order == null) {
        response.sendRedirect("order?msg=Order+not+found");
        return;
    }

    List<String> flow = order.getStatusFlow();                 // differs per subclass
    boolean cancelled = "Cancelled".equalsIgnoreCase(order.getStatus());
    int current = -1;
    for (int i = 0; i < flow.size(); i++) {
        if (flow.get(i).equalsIgnoreCase(order.getStatus())) current = i;
    }
    boolean ebook = "EBOOK".equals(order.getTypeCode());
    double adj = order.getAdjustment();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Order <%= esc(order.getId()) %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
    <style>
        .ob-card { border-radius: 14px; padding: 1.25rem 1.4rem; background: rgba(255,255,255,.07);
            border: 1px solid rgba(255,255,255,.16); height: 100%; }
        .ob-card h5 { font-weight: 700; margin-bottom: 1rem; }
        .ob-kv { display: flex; justify-content: space-between; gap: 1rem; padding: .45rem 0;
            border-bottom: 1px solid rgba(255,255,255,.1); }
        .ob-kv:last-child { border-bottom: 0; }
        .ob-kv span:first-child { opacity: .7; }
        .ob-kv span:last-child { text-align: right; word-break: break-word; }

        /* Progress tracker: the one featured element on this page */
        .ob-track { display: flex; align-items: flex-start; list-style: none; padding: 0; margin: 0; }
        .ob-step { flex: 1; text-align: center; position: relative; }
        .ob-step::before { content: ""; position: absolute; top: 1.15rem; left: -50%; width: 100%; height: 3px;
            background: rgba(255,255,255,.25); z-index: 0; }
        .ob-step:first-child::before { display: none; }
        .ob-step.done::before, .ob-step.now::before { background: rgba(255,255,255,.9); }
        .ob-dot { position: relative; z-index: 1; width: 2.4rem; height: 2.4rem; border-radius: 50%; margin: 0 auto .5rem;
            display: grid; place-items: center; border: 2px dashed rgba(255,255,255,.4); background: transparent; }
        .ob-step.done .ob-dot { background: rgba(255,255,255,.92); border: 2px solid #fff; color: #222; }
        .ob-step.now .ob-dot { background: rgba(255,255,255,.92); border: 2px solid #fff; color: #222;
            box-shadow: 0 0 0 5px rgba(255,255,255,.25); }
        .ob-step .ob-step-name { font-size: .88rem; opacity: .65; }
        .ob-step.done .ob-step-name, .ob-step.now .ob-step-name { opacity: 1; font-weight: 700; }
        .ob-track.is-cancelled { opacity: .45; }

        .ob-invoice td { padding: .55rem 0; }
        .ob-invoice .ob-grand td { border-top: 1px solid rgba(255,255,255,.35); font-weight: 700; font-size: 1.1rem; padding-top: .8rem; }

        @media print {
            nav, .navbar, .no-print { display: none !important; }
            body { background: #fff !important; color: #000 !important; }
            .panel, .ob-card { background: #fff !important; color: #000 !important; border-color: #999 !important; box-shadow: none !important; }
        }
    </style>
</head>
<body class="aura">
<% request.setAttribute("navActive", "order"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<div class="container">
    <div class="panel">
        <div class="d-flex flex-wrap justify-content-between align-items-start gap-3 mb-4">
            <div>
                <h2 class="fw-bold mb-1"><i class="fa-solid fa-file-invoice me-2"></i>Order <%= esc(order.getId()) %></h2>
                <p class="text-light opacity-75 mb-0">
                    <i class="fa-solid <%= ebook ? "fa-tablet-screen-button" : "fa-book" %> me-1"></i><%= esc(order.getOrderType()) %>
                    <span class="badge <%= badgeClass(order.getStatus()) %> ms-2"><%= esc(order.getStatus()) %></span>
                </p>
            </div>
            <div class="d-flex gap-2 no-print">
                <a href="order" class="btn btn-outline-light"><i class="fa-solid fa-arrow-left me-2"></i>Back to orders</a>
                <% if (admin) { %>
                <a href="order?action=edit&id=<%= java.net.URLEncoder.encode(order.getId(), "UTF-8") %>" class="btn btn-warning"><i class="fa-solid fa-pen-to-square me-2"></i>Edit</a>
                <% } %>
                <button type="button" class="btn btn-primary" onclick="window.print()"><i class="fa-solid fa-print me-2"></i>Print</button>
            </div>
        </div>

        <!-- Progress tracker -->
        <div class="ob-card mb-4">
            <h5><i class="fa-solid fa-route me-2"></i>Order progress</h5>
            <% if (cancelled) { %>
            <div class="alert alert-danger py-2 mb-3"><i class="fa-solid fa-ban me-2"></i>This order was cancelled.</div>
            <% } %>
            <ol class="ob-track <%= cancelled ? "is-cancelled" : "" %>">
                <% for (int i = 0; i < flow.size(); i++) {
                       String cls = (!cancelled && i < current) ? "done" : (!cancelled && i == current) ? "now" : ""; %>
                <li class="ob-step <%= cls %>">
                    <div class="ob-dot"><% if ("done".equals(cls) || "now".equals(cls)) { %><i class="fa-solid fa-check"></i><% } %></div>
                    <div class="ob-step-name"><%= esc(flow.get(i)) %></div>
                </li>
                <% } %>
            </ol>
        </div>

        <div class="row g-4">
            <div class="col-lg-6">
                <div class="ob-card">
                    <h5><i class="fa-solid fa-circle-info me-2"></i>Order details</h5>
                    <div class="ob-kv"><span>Order ID</span><span><%= esc(order.getId()) %></span></div>
                    <div class="ob-kv"><span>Customer</span><span><%= esc(order.getCustomerName()) %></span></div>
                    <div class="ob-kv"><span>Book</span><span><%= esc(order.getBookTitle()) %></span></div>
                    <div class="ob-kv"><span>Type</span><span><%= esc(order.getOrderType()) %></span></div>
                    <div class="ob-kv"><span><%= ebook ? "Delivery" : "Shipping" %></span><span><%= esc(order.getDeliveryInfo()) %></span></div>
                    <div class="ob-kv"><span>Payment</span><span><%= esc(order.getPaymentLabel()) %></span></div>
                </div>
            </div>
            <div class="col-lg-6">
                <div class="ob-card">
                    <h5><i class="fa-solid fa-receipt me-2"></i>Invoice</h5>
                    <table class="ob-invoice w-100">
                        <tr><td><%= esc(order.getBookTitle()) %> &times; <%= order.getQuantity() %><br>
                            <small class="opacity-75"><%= money(order.getUnitPrice()) %> each</small></td>
                            <td class="text-end"><%= money(order.getSubtotal()) %></td></tr>
                        <tr><td><%= esc(order.getAdjustmentLabel()) %></td>
                            <td class="text-end"><%= adj < 0 ? "- " : (adj > 0 ? "+ " : "") %><%= money(adj) %></td></tr>
                        <tr class="ob-grand"><td>Total</td><td class="text-end"><%= money(order.calculateTotal()) %></td></tr>
                    </table>
                </div>
            </div>
        </div>

        <% if (order.hasReceipt()) {
               String receiptUrl = "order?action=receipt&id=" + java.net.URLEncoder.encode(order.getId(), "UTF-8");
               boolean pdfReceipt = order.getReceiptFile().toLowerCase().endsWith(".pdf"); %>
        <div class="ob-card mt-4">
            <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
                <h5 class="mb-0"><i class="fa-solid fa-file-invoice-dollar me-2"></i>Bank deposit receipt</h5>
                <a href="<%= receiptUrl %>" target="_blank" rel="noopener" class="btn btn-sm btn-outline-light no-print">
                    <i class="fa-solid fa-up-right-from-square me-2"></i>Open in new tab</a>
            </div>
            <% if (pdfReceipt) { %>
            <p class="mb-0 opacity-75"><i class="fa-solid fa-file-pdf me-2"></i>PDF receipt uploaded. Use "Open in new tab" to view it.</p>
            <% } else { %>
            <img src="<%= receiptUrl %>" alt="Bank deposit receipt for order <%= esc(order.getId()) %>"
                 style="max-width:100%; max-height:420px; border-radius:10px; border:1px solid rgba(255,255,255,.25);">
            <% } %>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>
