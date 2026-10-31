<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User" %>
<% User user = (User) request.getAttribute("user"); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Aura Books - Edit User</title>
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
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 20px 0;
    }
    .glass-card {
      background: rgba(255, 255, 255, 0.05);
      backdrop-filter: blur(12px);
      -webkit-backdrop-filter: blur(12px);
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: 16px;
      padding: 40px;
      box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
      width: 100%;
      max-width: 480px;
    }
    .form-control, .form-select {
      background: rgba(255, 255, 255, 0.07);
      border: 1px solid rgba(255, 255, 255, 0.15);
      color: #fff;
      border-radius: 8px;
      padding: 10px 12px;
    }
    .form-control:focus, .form-select:focus {
      background: rgba(255, 255, 255, 0.12);
      border-color: #6366f1;
      color: #fff;
      box-shadow: 0 0 0 0.25rem rgba(99, 102, 241, 0.25);
    }
    .form-control:disabled {
      background: rgba(255, 255, 255, 0.04);
      color: rgba(255, 255, 255, 0.6);
    }
    .form-control::placeholder { color: rgba(255, 255, 255, 0.5); }
    .form-select option { color: #0f172a; }
    .btn-custom {
      background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
      border: none;
      color: white;
      padding: 12px;
      border-radius: 8px;
      font-weight: 600;
      width: 100%;
      transition: all 0.3s ease;
    }
    .btn-custom:hover {
      background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);
      color: white;
    }
  </style>
</head>
<body>

<div class="glass-card">
  <div class="text-center mb-4">
    <i class="fa-solid fa-user-pen fa-3x mb-3" style="color: #818cf8;"></i>
    <h2 class="fw-bold">Edit User</h2>
    <p class="text-light opacity-75">Update account details</p>
  </div>

  <form action="user" method="POST">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="username" value="<%= user.getUsername().replace("\"", "&quot;") %>">
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-user me-2"></i>Username</label>
      <input type="text" class="form-control" value="<%= user.getUsername().replace("\"", "&quot;") %>" disabled>
    </div>
    <div class="mb-3">
      <label class="form-label"><i class="fa-solid fa-lock me-2"></i>New Password</label>
      <input type="password" name="password" class="form-control" placeholder="Enter new password" required>
    </div>
    <div class="mb-4">
      <label class="form-label"><i class="fa-solid fa-id-badge me-2"></i>Role</label>
      <select name="role" class="form-select">
        <option <%= "Customer".equalsIgnoreCase(user.getRole()) ? "selected" : "" %>>Customer</option>
        <option <%= "Admin".equalsIgnoreCase(user.getRole()) ? "selected" : "" %>>Admin</option>
      </select>
    </div>
    <button type="submit" class="btn btn-custom mb-3">Update</button>
    <div class="text-center">
      <p class="small text-light opacity-75"><a href="user" class="text-decoration-none" style="color: #818cf8;">Cancel</a></p>
    </div>
  </form>
</div>

</body>
</html>