<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.*" %>
<%! private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    } %>
<%
    User me = (User) session.getAttribute("loggedUser");
    boolean admin = me != null && "Admin".equalsIgnoreCase(me.getRole());
    Order order = (Order) request.getAttribute("order");
    boolean editing = order != null;

    String curType = editing ? order.getTypeCode() : PhysicalOrder.TYPE_CODE;
    String curExtra = editing ? order.getExtraField() : "";
    int curQty = editing ? order.getQuantity() : 1;
    String curPayment = editing ? order.getPaymentMethod() : "COD";

    // TODO: replace with the shop's real bank details
    final String BANK_NAME = "Your Bank Name - Branch";
    final String BANK_ACCOUNT_NAME = "Regent Books (Pvt) Ltd";
    final String BANK_ACCOUNT_NO = "0000 0000 0000";

    List<String> statuses = new ArrayList<>(Arrays.asList("Pending", "Processing", "Shipped", "Delivered", "Cancelled"));
    if (editing && order.getStatus() != null && !statuses.contains(order.getStatus())) {
        statuses.add(order.getStatus());
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - <%= editing ? "Edit Order" : "Place Order" %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
    <style>
        /* Only neutral see-through layers, so the Aura colour scheme still shows through */
        .ob-types { display: grid; grid-template-columns: 1fr 1fr; gap: .75rem; }
        .ob-type { cursor: pointer; margin: 0; }
        .ob-type input { position: absolute; opacity: 0; pointer-events: none; }
        .ob-type .ob-type-body {
            display: flex; flex-direction: column; align-items: center; gap: .25rem;
            padding: .9rem .5rem; text-align: center; border-radius: 12px;
            background: rgba(255,255,255,.06); border: 1.5px solid rgba(255,255,255,.18);
            transition: background .2s, border-color .2s;
        }
        .ob-type .ob-type-body i { font-size: 1.4rem; }
        .ob-type .ob-type-body small { opacity: .7; }
        .ob-type input:checked + .ob-type-body { background: rgba(255,255,255,.16); border-color: rgba(255,255,255,.8); }
        .ob-type input:focus-visible + .ob-type-body { outline: 2px solid #fff; outline-offset: 2px; }
        .ob-fixed-type { display: inline-flex; align-items: center; gap: .5rem; padding: .45rem .9rem;
            border-radius: 999px; background: rgba(255,255,255,.12); border: 1px solid rgba(255,255,255,.25); }
        .ob-estimate { border-radius: 12px; padding: .9rem 1rem; background: rgba(255,255,255,.07);
            border: 1px dashed rgba(255,255,255,.3); }
        .ob-estimate .row-line { display: flex; justify-content: space-between; font-size: .92rem; padding: .1rem 0; }
        .ob-estimate .total-line { font-weight: 700; font-size: 1.05rem; border-top: 1px solid rgba(255,255,255,.25);
            margin-top: .4rem; padding-top: .5rem; }
        .ob-hint { font-size: .8rem; opacity: .7; }
        .ob-locked { pointer-events: none; }
        .ob-pay { grid-template-columns: repeat(auto-fit, minmax(120px, 1fr)); }
        .ob-note { border-radius: 12px; padding: .9rem 1rem; background: rgba(255,255,255,.07);
            border: 1px solid rgba(255,255,255,.2); font-size: .92rem; }
        .ob-kv-mini { display: flex; justify-content: space-between; gap: 1rem; padding: .2rem 0; }
        .ob-kv-mini span:first-child { opacity: .7; }
    </style>
</head>
<body class="aura">
<% request.setAttribute("navActive", "order"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<main class="auth-wrap">
<div class="auth-card">
    <div class="text-center mb-4">
        <i class="fa-solid fa-cart-shopping fa-3x mb-3"></i>
        <h2 class="fw-bold"><%= editing ? "Edit Order" : "Place Order" %></h2>
        <p class="text-light opacity-75"><%= editing ? "Update order details" : "Order a book from Regent Books" %></p>
    </div>

    <% String msg = (String) request.getAttribute("msg");
       if (msg != null && !msg.isEmpty()) { %>
    <div class="alert alert-danger py-2" role="alert"><i class="fa-solid fa-circle-exclamation me-2"></i><%= esc(msg) %></div>
    <% } %>

    <form action="order" method="POST" id="orderForm" enctype="multipart/form-data">
        <input type="hidden" name="action" value="<%= editing ? "update" : "add" %>">

        <!-- Order type: selectable on new orders, fixed when editing -->
        <div class="mb-3">
            <label class="form-label"><i class="fa-solid fa-layer-group me-2"></i>Order Type</label>
            <% if (editing) { %>
            <div>
                <span class="ob-fixed-type">
                    <i class="fa-solid <%= "EBOOK".equals(curType) ? "fa-tablet-screen-button" : "fa-book" %>"></i>
                    <%= esc(order.getOrderType()) %>
                </span>
                <input type="hidden" name="orderType" id="orderTypeFixed" value="<%= esc(curType) %>">
            </div>
            <% } else { %>
            <div class="ob-types ob-locked">
                <label class="ob-type">
                    <input type="radio" name="orderType" value="PHYSICAL" checked>
                    <span class="ob-type-body"><i class="fa-solid fa-book"></i><strong>Printed Book</strong><small>Delivered to your address</small></span>
                </label>
                <label class="ob-type">
                    <input type="radio" name="orderType" value="EBOOK">
                    <span class="ob-type-body"><i class="fa-solid fa-tablet-screen-button"></i><strong>E-Book</strong><small>10% off, instant download</small></span>
                </label>
            </div>
            <div class="ob-hint mt-2">Set automatically from the book you choose.</div>
            <% } %>
        </div>

        <% if (admin) { %>
        <div class="mb-3">
            <label class="form-label" for="id"><i class="fa-solid fa-hashtag me-2"></i>Order ID</label>
            <input type="text" id="id" name="id" class="form-control" placeholder="e.g. ORD001"
                   value="<%= editing ? esc(order.getId()) : "" %>" <%= editing ? "readonly" : "" %> required>
        </div>
        <div class="mb-3">
            <label class="form-label" for="customerName"><i class="fa-solid fa-user me-2"></i>Customer Name</label>
            <input type="text" id="customerName" name="customerName" class="form-control" placeholder="Customer username"
                   value="<%= editing ? esc(order.getCustomerName()) : "" %>" required>
        </div>
        <% } %>

        <div class="mb-3">
            <label class="form-label" for="bookTitle"><i class="fa-solid fa-book-open me-2"></i>Book Title</label>
            <select id="bookTitle" name="bookTitle" class="form-select" required>
                <option value="" data-price="" data-type="">Choose a book</option>
                <% List<Book> books = (List<Book>) request.getAttribute("books");
                   boolean listed = false;
                   if (books != null) for (Book b : books) {
                       String p = b.getPrice() == null ? "" : b.getPrice().replaceAll("[^0-9.]", "");
                       boolean sel = editing && b.getTitle().equalsIgnoreCase(order.getBookTitle());
                       listed |= sel; %>
                <option value="<%= esc(b.getTitle()) %>" data-price="<%= p %>" data-type="<%= "EBOOK".equals(b.getType()) ? "EBOOK" : "PHYSICAL" %>" <%= sel ? "selected" : "" %>>
                    <%= esc(b.getTitle()) %> - Rs. <%= p %> (<%= esc(b.getTypeLabel()) %>)</option>
                <% }
                   if (editing && !listed) { %>
                <option value="<%= esc(order.getBookTitle()) %>" data-price="<%= String.format(Locale.US, "%.2f", order.getUnitPrice()) %>" data-type="<%= curType %>" selected><%= esc(order.getBookTitle()) %></option>
                <% } %>
            </select>
        </div>

        <div class="row g-3 mb-3">
            <div class="col-6">
                <label class="form-label" for="quantity"><i class="fa-solid fa-boxes-stacked me-2"></i>Quantity</label>
                <input type="number" id="quantity" name="quantity" class="form-control" min="1" max="100"
                       value="<%= curQty %>" required>
            </div>
            <% if (admin) { %>
            <div class="col-6">
                <label class="form-label" for="unitPrice"><i class="fa-solid fa-tag me-2"></i>Unit Price (Rs.)</label>
                <input type="number" id="unitPrice" name="unitPrice" class="form-control" min="0" step="0.01"
                       placeholder="Default price"
                       value="<%= editing ? String.format(Locale.US, "%.2f", order.getUnitPrice()) : "" %>">
            </div>
            <% } %>
        </div>

        <!-- Subclass-specific field: only one of these is sent (the other is disabled by the script) -->
        <div class="mb-3" id="grpPhysical">
            <label class="form-label" for="extraAddress"><i class="fa-solid fa-location-dot me-2"></i>Shipping Address</label>
            <input type="text" id="extraAddress" name="extra" class="form-control" placeholder="No. 12, Galle Road, Colombo"
                   value="<%= "PHYSICAL".equals(curType) ? esc(curExtra) : "" %>">
            <div class="ob-hint mt-1">Commas are not allowed. Use spaces or dashes instead.</div>
        </div>
        <div class="mb-3" id="grpEbook">
            <label class="form-label" for="extraFormat"><i class="fa-solid fa-file-lines me-2"></i>File Format</label>
            <select id="extraFormat" name="extra" class="form-select">
                <% for (String f : EbookOrder.FORMATS) { %>
                <option value="<%= f %>" <%= "EBOOK".equals(curType) && f.equals(curExtra) ? "selected" : "" %>><%= f %></option>
                <% } %>
            </select>
        </div>

        <!-- Payment method -->
        <div class="mb-3">
            <label class="form-label"><i class="fa-solid fa-credit-card me-2"></i>Payment Method</label>
            <div class="ob-types ob-pay">
                <label class="ob-type" id="payCod">
                    <input type="radio" name="paymentMethod" value="COD" <%= "COD".equals(curPayment) ? "checked" : "" %>>
                    <span class="ob-type-body"><i class="fa-solid fa-money-bill-wave"></i><strong>Cash on Delivery</strong><small>Pay when it arrives</small></span>
                </label>
                <label class="ob-type">
                    <input type="radio" name="paymentMethod" value="CARD" <%= "CARD".equals(curPayment) ? "checked" : "" %>>
                    <span class="ob-type-body"><i class="fa-solid fa-credit-card"></i><strong>Online Card</strong><small>Pay by card</small></span>
                </label>
                <label class="ob-type">
                    <input type="radio" name="paymentMethod" value="BANK" <%= "BANK".equals(curPayment) ? "checked" : "" %>>
                    <span class="ob-type-body"><i class="fa-solid fa-building-columns"></i><strong>Bank Deposit</strong><small>Upload your receipt</small></span>
                </label>
            </div>

            <div class="ob-note mt-3" id="panelCard">
                <i class="fa-solid fa-lock me-2"></i>No card details are stored on this site.
            </div>

            <div class="ob-note mt-3" id="panelBank">
                <div class="fw-bold mb-2"><i class="fa-solid fa-building-columns me-2"></i>Deposit to this account</div>
                <div class="ob-kv-mini"><span>Bank</span><span><%= esc(BANK_NAME) %></span></div>
                <div class="ob-kv-mini"><span>Account name</span><span><%= esc(BANK_ACCOUNT_NAME) %></span></div>
                <div class="ob-kv-mini"><span>Account number</span><span><%= esc(BANK_ACCOUNT_NO) %></span></div>
                <label class="form-label mt-3" for="receipt"><i class="fa-solid fa-file-arrow-up me-2"></i>Deposit receipt</label>
                <input type="file" id="receipt" name="receipt" class="form-control"
                       accept=".jpg,.jpeg,.png,.pdf,image/jpeg,image/png,application/pdf">
                <div class="ob-hint mt-1">JPG, PNG or PDF, up to 2 MB.</div>
                <% if (editing && order.hasReceipt()) { %>
                <div class="ob-hint mt-1">
                    Current receipt:
                    <a href="order?action=receipt&id=<%= java.net.URLEncoder.encode(order.getId(), "UTF-8") %>" target="_blank" rel="noopener">view</a>.
                    Choose a new file only if you want to replace it.
                </div>
                <% } %>
            </div>
        </div>

        <% if (admin) { %>
        <div class="mb-3">
            <label class="form-label" for="status"><i class="fa-solid fa-truck me-2"></i>Status</label>
            <select id="status" name="status" class="form-select">
                <% for (String s : statuses) { %>
                <option <%= editing && s.equals(order.getStatus()) ? "selected" : "" %>><%= esc(s) %></option>
                <% } %>
            </select>
        </div>
        <% } else { %>
        <p class="small text-light opacity-75 mb-3">
            This order will be placed under your account (<strong><%= esc(me.getUsername()) %></strong>) with status <strong>Pending</strong>.
        </p>
        <% } %>

        <!-- Live price estimate -->
        <div class="ob-estimate mb-4" aria-live="polite">
            <div class="row-line"><span>Subtotal</span><span id="estSub">Rs. 0.00</span></div>
            <div class="row-line"><span id="estAdjLabel">Shipping fee</span><span id="estAdj">Rs. 0.00</span></div>
            <div class="row-line total-line"><span>Estimated total</span><span id="estTotal">Rs. 0.00</span></div>
        </div>

        <button type="submit" class="btn btn-custom mb-3">
            <i class="fa-solid <%= editing ? "fa-floppy-disk" : "fa-bag-shopping" %> me-2"></i><%= editing ? "Save changes" : "Place Order" %>
        </button>
        <div class="text-center">
            <p class="small text-light opacity-75"><a href="order" class="text-decoration-none">Cancel</a></p>
        </div>
    </form>
</div>
</main>

<script>
    // Pricing rules mirror the Java classes (PhysicalOrder / EbookOrder). The server always recalculates the real total.
    var RULES = {
        PHYSICAL: { price: <%= PhysicalOrder.DEFAULT_UNIT_PRICE %>, shipping: <%= PhysicalOrder.SHIPPING_FEE %>, freeOver: <%= PhysicalOrder.FREE_SHIPPING_THRESHOLD %> },
        EBOOK:    { price: <%= EbookOrder.DEFAULT_UNIT_PRICE %>, discount: <%= EbookOrder.DISCOUNT_RATE %> }
    };
    var IS_ADMIN = <%= admin %>;

    var form = document.getElementById('orderForm');
    var grpPhysical = document.getElementById('grpPhysical');
    var grpEbook = document.getElementById('grpEbook');
    var addressInput = document.getElementById('extraAddress');
    var formatSelect = document.getElementById('extraFormat');
    var qtyInput = document.getElementById('quantity');
    var priceInput = document.getElementById('unitPrice');
    var bookSelect = document.getElementById('bookTitle');
    var codCard = document.getElementById('payCod');
    var receiptInput = document.getElementById('receipt');
    var HAS_RECEIPT = <%= editing && order.hasReceipt() %>;
    var MAX_RECEIPT = 2 * 1024 * 1024;

    function currentPayment() {
        return form.querySelector('input[name="paymentMethod"]:checked').value;
    }

    // E-books cannot be paid on delivery; the Bank panel and receipt field only show for bank deposits
    function refreshPayment(physical) {
        var codInput = codCard.querySelector('input');
        codCard.style.display = physical ? '' : 'none';
        codInput.disabled = !physical;
        if (!physical && codInput.checked) {
            form.querySelector('input[name="paymentMethod"][value="CARD"]').checked = true;
        }
        var pay = currentPayment();
        document.getElementById('panelBank').style.display = pay === 'BANK' ? '' : 'none';
        document.getElementById('panelCard').style.display = pay === 'CARD' ? '' : 'none';
        receiptInput.disabled = pay !== 'BANK';
        receiptInput.required = pay === 'BANK' && !HAS_RECEIPT;
    }

    receiptInput.addEventListener('change', function () {
        var f = receiptInput.files[0];
        if (f && f.size > MAX_RECEIPT) {
            alert('The receipt is larger than 2 MB. Please choose a smaller file.');
            receiptInput.value = '';
        }
    });

    function currentType() {
        var fixed = document.getElementById('orderTypeFixed');
        if (fixed) return fixed.value;
        return form.querySelector('input[name="orderType"]:checked').value;
    }

    function money(v) {
        return 'Rs. ' + v.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function refresh() {
        var opt = bookSelect.options[bookSelect.selectedIndex];
        var typeRadio = opt && opt.dataset.type ? form.querySelector('input[name="orderType"][value="' + opt.dataset.type + '"]') : null;
        if (typeRadio) typeRadio.checked = true;          // the book decides Printed or E-Book

        var type = currentType();
        var physical = type === 'PHYSICAL';
        refreshPayment(physical);

        grpPhysical.style.display = physical ? '' : 'none';
        grpEbook.style.display = physical ? 'none' : '';
        addressInput.disabled = !physical;
        addressInput.required = physical;
        formatSelect.disabled = physical;

        var qty = Math.max(1, parseInt(qtyInput.value, 10) || 1);
        var base = (opt && opt.dataset.price) ? (parseFloat(opt.dataset.price) || 0) : RULES[type].price;
        var unit = base;
        if (IS_ADMIN && priceInput && priceInput.value !== '') unit = parseFloat(priceInput.value) || 0;
        if (priceInput) priceInput.placeholder = 'Book price ' + money(base);

        var sub = unit * qty, adj, label;
        if (physical) {
            var free = sub >= RULES.PHYSICAL.freeOver;
            adj = free ? 0 : RULES.PHYSICAL.shipping;
            label = free ? 'Shipping (free over Rs. 10,000)' : 'Shipping fee';
        } else {
            adj = -(sub * RULES.EBOOK.discount);
            label = 'Digital discount (10%)';
        }
        document.getElementById('estSub').textContent = money(sub);
        document.getElementById('estAdjLabel').textContent = label;
        document.getElementById('estAdj').textContent = (adj < 0 ? '- ' : '') + money(Math.abs(adj));
        document.getElementById('estTotal').textContent = money(Math.max(0, sub + adj));
    }

    form.addEventListener('input', refresh);
    form.addEventListener('change', refresh);
    refresh();
</script>
</body>
</html>
