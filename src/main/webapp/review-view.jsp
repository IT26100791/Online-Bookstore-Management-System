<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.Review" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    String orDash(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : esc(s);
    }
%>
<%
    Review review = (Review) request.getAttribute("review");
    if (review == null) { response.sendRedirect("review"); return; }
    int stars = review.getRating();
    String[] rvLabels = { "", "Poor", "Below Average", "Average", "Very Good", "Outstanding" };
    String rvLabel = (stars >= 1 && stars <= 5) ? rvLabels[stars] : "";
    String rvBadge = review.getBadge();
    boolean rvIsVerified = "VERIFIED".equalsIgnoreCase(review.getType());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Review Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">

    <%-- Page-only styles. No new colours: neutral overlays + inherited colours only. --%>
    <style>
        .rv-fade { animation: rvFade .45s ease both; }
        @keyframes rvFade { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: none; } }

        .rv-head { display: flex; align-items: center; gap: 14px; margin-bottom: 20px; }
        .rv-head-icon { flex: 0 0 52px; width: 52px; height: 52px; display: grid; place-items: center;
                        border-radius: 14px; font-size: 1.3rem;
                        background: rgba(255,255,255,.08); border: 1px solid rgba(255,255,255,.1); }
        .rv-head h2 { margin: 0; }
        .rv-head p { margin: 0; opacity: .7; font-size: .9rem; }

        .rv-id { display: inline-block; padding: 2px 10px; border-radius: 999px; font-size: .8rem;
                 font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
                 background: rgba(255,255,255,.08); border: 1px solid rgba(255,255,255,.1); }

        /* big rating block */
        .rv-score { display: flex; align-items: center; gap: 18px; flex-wrap: wrap; padding: 18px 20px; margin-bottom: 20px;
                    background: rgba(255,255,255,.05); border: 1px solid rgba(255,255,255,.1); border-radius: 14px; }
        .rv-score-num { font-size: 2.6rem; font-weight: 700; line-height: 1; }
        .rv-score-num small { font-size: 1rem; font-weight: 400; opacity: .6; }
        .rv-score .star-rating { font-size: 1.4rem; }
        .rv-score-label { opacity: .75; font-weight: 600; margin-top: 4px; }

        /* detail rows */
        .rv-row { display: flex; justify-content: space-between; align-items: center; gap: 12px; padding: 12px 0;
                  border-bottom: 1px solid rgba(255,255,255,.08); }
        .rv-row-label { opacity: .7; display: flex; align-items: center; gap: 10px; }
        .rv-row-label i { width: 18px; text-align: center; }
        .rv-row-value { text-align: right; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; justify-content: flex-end; }

        /* comment as a quote block */
        .rv-quote { position: relative; margin: 20px 0 0; padding: 18px 20px 18px 52px; border-radius: 14px;
                    background: rgba(255,255,255,.05); border: 1px solid rgba(255,255,255,.1);
                    white-space: pre-wrap; word-break: break-word; line-height: 1.6; }
        .rv-quote > i { position: absolute; left: 18px; top: 18px; font-size: 1.3rem; opacity: .4; }

        .rv-actions { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 24px; }
        .rv-actions form { display: inline; margin: 0; }
        .rv-actions .btn { transition: transform .15s ease; }
        .rv-actions .btn:hover { transform: translateY(-2px); }

        @media (max-width: 480px) {
            .rv-row { flex-direction: column; align-items: flex-start; gap: 4px; }
            .rv-row-value { text-align: left; justify-content: flex-start; }
        }
        @media (prefers-reduced-motion: reduce) {
            .rv-fade { animation: none; }
            .rv-actions .btn { transition: none; }
        }
    </style>
</head>
<body class="aura">
<% boolean canEdit = com.bookstore.util.SessionUtil.isAdmin(request); %>
<% request.setAttribute("navActive", "review"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>
<div class="container">
    <div class="panel panel-narrow rv-fade">
        <div class="rv-head">
            <div class="rv-head-icon"><i class="fa-solid fa-star"></i></div>
            <div>
                <h2 class="fw-bold">Review Details</h2>
                <p><span class="rv-id"><%= orDash(review.getReviewId()) %></span></p>
            </div>
        </div>

        <!-- rating -->
        <div class="rv-score">
            <div class="rv-score-num"><%= stars %><small> / 5</small></div>
            <div>
                <span class="star-rating">
                    <% for (int s = 1; s <= 5; s++) { %>
                        <i class="fa-<%= s <= stars ? "solid" : "regular" %> fa-star"></i>
                    <% } %>
                </span>
                <% if (!rvLabel.isEmpty()) { %><div class="rv-score-label"><%= rvLabel %></div><% } %>
            </div>
        </div>

        <!-- details -->
        <div class="rv-row">
            <span class="rv-row-label"><i class="fa-solid fa-book"></i>Book Title</span>
            <span class="rv-row-value fw-semibold"><%= orDash(review.getBookTitle()) %></span>
        </div>
        <div class="rv-row">
            <span class="rv-row-label"><i class="fa-solid fa-circle-user"></i>Reviewer</span>
            <span class="rv-row-value">
                <%= orDash(review.getReviewer()) %>
                <% if (rvBadge != null && !rvBadge.isEmpty()) { %>
                    <span class="badge bg-secondary">
                        <% if (rvIsVerified) { %><i class="fa-solid fa-circle-check me-1"></i><% } %><%= esc(rvBadge) %>
                    </span>
                <% } %>
            </span>
        </div>

        <!-- comment -->
        <div class="rv-quote">
            <i class="fa-solid fa-quote-left"></i>
            <%= orDash(review.getComment()) %>
        </div>

        <!-- actions -->
        <div class="rv-actions">
            <% if (canEdit) { %>
            <a class="btn btn-warning"
               href="review?action=edit&id=<%= java.net.URLEncoder.encode(review.getReviewId(), "UTF-8") %>">
                <i class="fa-solid fa-pen-to-square me-1"></i>Edit</a>
            <form method="post" action="review"
                  data-title="<%= esc(review.getBookTitle()) %>"
                  onsubmit="return rvConfirmDelete(this);">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="<%= esc(review.getReviewId()) %>">
                <button type="submit" class="btn btn-danger">
                    <i class="fa-solid fa-trash me-1"></i>Delete</button>
            </form>
            <% } %>
            <a href="review" class="btn btn-outline-light">
                <i class="fa-solid fa-arrow-left me-1"></i>Back to list</a>
        </div>
    </div>
</div>

<script>
    function rvConfirmDelete(form) {
        var title = form.getAttribute('data-title') || 'this book';
        return confirm('Delete the review for "' + title + '"?');
    }
</script>
</body>
</html>
