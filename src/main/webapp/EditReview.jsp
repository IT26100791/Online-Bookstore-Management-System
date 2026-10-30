<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Edit Review</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(30, 27, 75, 0.85)),
            url('https://images.unsplash.com/photo-1457369804613-52c61a468e7d?q=80&w=1920&auto=format&fit=crop') !important;
            background-size: cover !important; background-position: center !important;
            min-height: 100vh; display: flex; align-items: center; justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #f8fafc;
        }
        .glass-card {
            background: rgba(15, 23, 42, 0.75); backdrop-filter: blur(16px);
            border: 1px solid rgba(255, 255, 255, 0.12); border-radius: 1rem; box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
        }
        .form-control, .form-select { background: rgba(255, 255, 255, 0.07); border: 1px solid rgba(255, 255, 255, 0.15); color: #f8fafc; }
        .form-control:focus, .form-select:focus { background: rgba(255, 255, 255, 0.12); border-color: #6366f1; color: #f8fafc; }
        .form-select option { background: #0f172a; color: #f8fafc; }
        .form-label { color: #cbd5e1; font-weight: 500; }
    </style>
</head>
<body class="py-5">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="glass-card p-4 p-md-5">
                <h3 class="text-white fw-bold mb-1 text-center"><i class="fa-solid fa-pen-to-square me-2 text-info"></i>Edit Review</h3>
                <p class="text-white-50 text-small text-center mb-4">Update your review details.</p>

                <% String editId = request.getParameter("id"); %>
                <form action="UpdateReviewServlet" method="POST">
                    <div class="mb-3">
                        <label for="reviewId" class="form-label">Review ID</label>
                        <input type="text" class="form-control" id="reviewId" name="reviewId" value="<%= editId != null ? editId : "" %>" readonly>
                    </div>
                    <div class="mb-3">
                        <label for="bookTitle" class="form-label">New Book Title</label>
                        <input type="text" class="form-control" id="bookTitle" name="bookTitle" required>
                    </div>
                    <div class="mb-3">
                        <label for="rating" class="form-label">New Rating (1 to 5)</label>
                        <select class="form-select" id="rating" name="rating" required>
                            <option value="" selected disabled>Select rating</option>
                            <option value="5">5 Excellent</option>
                            <option value="4">4 Very Good</option>
                            <option value="3">3 Good</option>
                            <option value="2">2 Fair</option>
                            <option value="1">1 Poor</option>
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="comment" class="form-label">New Comment</label>
                        <textarea class="form-control" id="comment" name="comment" rows="3" required></textarea>
                    </div>
                    <div class="d-flex justify-content-between align-items-center">
                        <!-- මෙහි Back ලින්ක් එක ReviewList.jsp ලෙස නිවැරදි කර ඇත -->
                        <a href="ReviewList.jsp" class="btn btn-outline-light"><i class="fa-solid fa-arrow-left me-1"></i>Back</a>
                        <button type="submit" class="btn btn-info fw-semibold text-white"><i class="fa-solid fa-check me-1"></i>Update Review</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>