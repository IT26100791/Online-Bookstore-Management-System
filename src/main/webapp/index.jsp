<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(30, 27, 75, 0.85)),
            url('https://images.unsplash.com/photo-1457369804613-52c61a468e7d?q=80&w=1920&auto=format&fit=crop');
            background-size: cover !important;
            background-position: center !important;
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
            padding: 40px;
            box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
        }
        .module-card {
            display: block;
            background: rgba(255, 255, 255, 0.07);
            border: 1px solid rgba(255, 255, 255, 0.12);
            border-radius: 12px;
            padding: 24px;
            text-align: center;
            color: #f8fafc;
            text-decoration: none;
            transition: all 0.3s ease;
            height: 100%;
        }
        .module-card:hover {
            background: rgba(99, 102, 241, 0.25);
            transform: translateY(-4px);
            color: #fff;
        }
        .module-card i { font-size: 2rem; color: #818cf8; margin-bottom: 12px; }
    </style>
</head>
<body>
<div class="container">
    <div class="glass-container">
        <div class="text-center mb-5">
            <i class="fa-solid fa-book-open-reader fa-3x mb-3" style="color: #818cf8;"></i>
            <h1 class="fw-bold">Aura Books</h1>
            <p class="opacity-75">Online Bookstore Management System</p>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <a class="module-card" href="login">
                    <i class="fa-solid fa-right-to-bracket"></i>
                    <h5>Login</h5>
                </a>
            </div>
            <div class="col-md-4">
                <a class="module-card" href="user?action=new">
                    <i class="fa-solid fa-user-plus"></i>
                    <h5>Register</h5>
                </a>
            </div>
            <div class="col-md-4">
                <a class="module-card" href="user">
                    <i class="fa-solid fa-users-gear"></i>
                    <h5>User Management</h5>
                </a>
            </div>

            <!-- අනිත් modules සහ Review Management එක මෙහි එකතු කර ඇත -->
            <!--
            <div class="col-md-4"><a class="module-card" href="book"><i class="fa-solid fa-book"></i><h5>Book Management</h5></a></div>
            <div class="col-md-4"><a class="module-card" href="author"><i class="fa-solid fa-pen-nib"></i><h5>Author Management</h5></a></div>
            <div class="col-md-4"><a class="module-card" href="order"><i class="fa-solid fa-cart-shopping"></i><h5>Order Management</h5></a></div>
            <div class="col-md-4"><a class="module-card" href="publisher"><i class="fa-solid fa-building"></i><h5>Publisher Management</h5></a></div>
            -->

            <div class="col-md-4">
                <a class="module-card" href="ReviewList.jsp">
                    <i class="fa-solid fa-star"></i>
                    <h5>Review Management</h5>
                </a>
            </div>
        </div>
    </div>
</div>
</body>
</html>