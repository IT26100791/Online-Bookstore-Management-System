<%@ page contentType="text/html;charset=UTF-8" %>
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
  <title>Aura Books - Place New Order</title>
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
    .form-control, .form-select {
      background: rgba(255, 255, 255, 0.08);
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: #f8fafc;
    }
    .form-control:focus, .form-select:focus {
      background: rgba(255, 255, 255, 0.12);
      border-color: #818cf8;
      box-shadow: 0 0 0 0.2rem rgba(99, 102, 241, 0.25);
      color: #fff;
    }
    .form-select option { color: #0f172a; }
    .form-control::placeholder { color: rgba(248, 250, 252, 0.5); }
  </style>
</head>
<body>

<div class="container" style="max-width: 640px;">
  <div class="glass-container">
    <h2 class="fw-bold mb-1"><i class="fa-solid fa-cart-plus me-2" style="color: #818cf8;"></i>Place New Order</h2>
    <p class="text-light opacity-75 mb-4">Enter the order details below</p>

    <% String msg = (String) request.getAttribute("msg");
      if (msg != null && !msg.isEmpty()) { %>
    <div class="alert alert-warning py-2"><%= esc(msg) %></div>
    <% } %>

    <form method="post" action="order">
      <input type="hidden" name="action" value="add">
      <div class="mb-3">
        <label class="form-label" for="customerName">Customer name</label>
        <input type="text" class="form-control" id="customerName" name="customerName" required>
      </div>
      <div class="mb-3">
        <label class="form-label" for="bookTitle">Book title</label>
        <input type="text" class="form-control" id="bookTitle" name="bookTitle" required>
      </div>
      <div class="row">
        <div class="col-md-6 mb-3">
          <label class="form-label" for="quantity">Quantity</label>
          <input type="number" class="form-control" id="quantity" name="quantity" min="1" value="1" required>
        </div>
        <div class="col-md-6 mb-3">
          <label class="form-label" for="totalAmount">Total amount (LKR)</label>
          <input type="number" class="form-control" id="totalAmount" name="totalAmount" min="0" step="0.01" required>
        </div>
      </div>
      <div class="mb-4">
        <label class="form-label" for="status">Status</label>
        <select class="form-select" id="status" name="status">
          <option value="Pending" selected>Pending</option>
          <option value="Processing">Processing</option>
          <option value="Shipped">Shipped</option>
          <option value="Delivered">Delivered</option>
          <option value="Cancelled">Cancelled</option>
        </select>
      </div>

      <div class="d-flex justify-content-between">
        <a href="order" class="btn btn-outline-light"><i class="fa-solid fa-arrow-left me-2"></i>Back to orders</a>
        <button type="submit" class="btn btn-primary" style="background: #6366f1; border: none;">
          <i class="fa-solid fa-check me-2"></i>Place order</button>
      </div>
    </form>
  </div>
</div>

</body>
</html>
