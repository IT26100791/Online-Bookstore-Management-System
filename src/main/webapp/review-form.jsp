<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.Review" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    Review review = (Review) request.getAttribute("review");
    boolean editing = (review != null);
    String msg = (String) request.getAttribute("msg");
    String[] rvLabels = { "", "Poor", "Below Average", "Average", "Very Good", "Outstanding" };

    // When the servlet rejects the form it sends back what the user typed, so nothing is lost
    String formId = (String) request.getAttribute("formId");
    String formBook = (String) request.getAttribute("formBook");
    String formRating = (String) request.getAttribute("formRating");
    String formComment = (String) request.getAttribute("formComment");

    String fvId = editing ? review.getReviewId() : (formId != null ? formId : "");
    String fvBook = formBook != null ? formBook : (editing ? review.getBookTitle() : "");
    String fvComment = formComment != null ? formComment : (editing ? review.getComment() : "");
    String formType = (String) request.getAttribute("formType");
    String formReviewer = (String) request.getAttribute("formReviewer");
    String fvType = formType != null ? formType.toUpperCase() : (editing ? review.getType() : "PUBLIC");
    if (!"VERIFIED".equals(fvType)) fvType = "PUBLIC";
    String fvReviewer = formReviewer != null ? formReviewer : (editing ? review.getReviewer() : "");
    String currentRating = editing ? String.valueOf(review.getRating()) : "5";
    if (formRating != null && formRating.matches("[1-5]")) currentRating = formRating;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - <%= editing ? "Edit" : "Add" %> Review</title>
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

        .rv-head { display: flex; align-items: center; gap: 14px; margin-bottom: 24px; }
        .rv-head-icon { flex: 0 0 52px; width: 52px; height: 52px; display: grid; place-items: center;
                        border-radius: 14px; font-size: 1.3rem;
                        background: rgba(255,255,255,.08); border: 1px solid rgba(255,255,255,.1); }
        .rv-head h2 { margin: 0; }
        .rv-head p { margin: 0; opacity: .7; font-size: .9rem; }

        /* fields with a leading icon (no new colours) */
        .rv-field { position: relative; }
        .rv-field > i { position: absolute; left: 14px; top: 50%; transform: translateY(-50%); opacity: .6; pointer-events: none; }
        .rv-field.rv-top > i { top: 18px; transform: none; }
        .rv-field .form-control { padding-left: 2.5rem; }
        .rv-hint { font-size: .8rem; opacity: .65; margin-top: 4px; }

        /* clickable star picker (radio buttons; reversed so "~" selects lower stars) */
        .rv-stars { display: inline-flex; flex-direction: row-reverse; gap: 6px; font-size: 1.9rem; position: relative; }
        .rv-stars input { position: absolute; opacity: 0; width: 1px; height: 1px; pointer-events: none; }
        .rv-stars label { cursor: pointer; margin: 0; opacity: .45; transition: transform .15s ease, opacity .15s ease; }
        .rv-stars label:hover { transform: scale(1.15); }
        .rv-stars input:checked ~ label,
        .rv-stars label:hover,
        .rv-stars label:hover ~ label { opacity: 1; }
        .rv-stars input:checked ~ label i,
        .rv-stars label:hover i,
        .rv-stars label:hover ~ label i { font-weight: 900; }   /* regular star -> solid star */
        .rv-stars input:focus-visible + label { outline: 2px solid currentColor; outline-offset: 3px; border-radius: 6px; }
        .rv-rating-row { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; }
        .rv-rating-text { font-weight: 600; opacity: .85; }

        /* review type switch */
        .rv-type { display: flex; flex-wrap: wrap; gap: 8px; }
        .rv-type .btn { display: inline-flex; align-items: center; gap: 8px; }

        .rv-counter { text-align: right; font-size: .8rem; opacity: .6; margin-top: 4px; }

        @media (prefers-reduced-motion: reduce) {
            .rv-fade { animation: none; }
            .rv-stars label { transition: none; }
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
                <h2 class="fw-bold"><%= editing ? "Edit Review" : "Add New Review" %></h2>
                <p><%= editing ? "Update the details of this review" : "Share a rating and a comment for a catalog book" %></p>
            </div>
        </div>

        <% if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-warning py-2" role="alert">
              <i class="fa-solid fa-triangle-exclamation me-2"></i><%= esc(msg) %>
          </div>
        <% } %>

        <form method="post" action="review" id="rvForm" novalidate>
            <input type="hidden" name="action" value="<%= editing ? "update" : "add" %>">

            <div class="mb-3">
                <label class="form-label" for="rvId">Review ID</label>
                <div class="rv-field">
                    <i class="fa-solid <%= editing ? "fa-lock" : "fa-hashtag" %>"></i>
                    <input type="text" name="id" id="rvId" class="form-control" required maxlength="30" pattern="[^,]*"
                           value="<%= esc(fvId) %>" <%= editing ? "readonly" : "" %>>
                    <div class="invalid-feedback">Required. Commas (,) are not allowed.</div>
                </div>
                <div class="rv-hint"><%= editing ? "The Review ID cannot be changed." : "Choose a unique ID for this review (no commas)." %></div>
            </div>

            <div class="mb-3">
                <label class="form-label" for="rvBook">Book Title</label>
                <div class="rv-field">
                    <i class="fa-solid fa-book"></i>
                    <input type="text" name="bookTitle" id="rvBook" class="form-control" required maxlength="200" pattern="[^,]*"
                           value="<%= esc(fvBook) %>">
                    <div class="invalid-feedback">Required. Commas (,) are not allowed.</div>
                </div>
            </div>

            <% if (canEdit) { %>
            <!-- Admin only: review type + reviewer name -->
            <div class="mb-3">
                <label class="form-label d-block">Review Type</label>
                <div class="rv-type" role="radiogroup" aria-label="Review type">
                    <input type="radio" class="btn-check" name="type" id="typePublic" value="PUBLIC" <%= "PUBLIC".equals(fvType) ? "checked" : "" %>>
                    <label class="btn btn-outline-light" for="typePublic"><i class="fa-solid fa-users"></i>Public</label>

                    <input type="radio" class="btn-check" name="type" id="typeVerified" value="VERIFIED" <%= "VERIFIED".equals(fvType) ? "checked" : "" %>>
                    <label class="btn btn-outline-light" for="typeVerified"><i class="fa-solid fa-circle-check"></i>Verified purchase</label>
                </div>
                <div class="rv-hint">Verified = a reader who bought this book. Public = anyone.</div>
            </div>

            <div class="mb-3">
                <label class="form-label" for="rvReviewer">Reviewer Name</label>
                <div class="rv-field">
                    <i class="fa-solid fa-circle-user"></i>
                    <input type="text" name="reviewer" id="rvReviewer" class="form-control" maxlength="50"
                           placeholder="Leave empty to use your own username" value="<%= esc(fvReviewer) %>">
                </div>
            </div>
            <% } else { %>
            <div class="rv-hint mb-3"><i class="fa-solid fa-circle-info me-1"></i>Your review will be added as a Public review under your username.</div>
            <% } %>

            <div class="mb-3">
                <label class="form-label d-block">Rating</label>
                <div class="rv-rating-row">
                    <div class="rv-stars star-rating" role="radiogroup" aria-label="Rating">
                        <% for (int starVal = 5; starVal >= 1; starVal--) { %>
                        <input type="radio" name="rating" id="rate<%= starVal %>" value="<%= starVal %>"
                               <%= String.valueOf(starVal).equals(currentRating) ? "checked" : "" %> required>
                        <label for="rate<%= starVal %>" title="<%= starVal %> star<%= starVal > 1 ? "s" : "" %> - <%= rvLabels[starVal] %>">
                            <i class="fa-regular fa-star"></i>
                        </label>
                        <% } %>
                    </div>
                    <span class="rv-rating-text" id="rvRatingText" aria-live="polite"></span>
                </div>
            </div>

            <div class="mb-4">
                <label class="form-label" for="rvComment">Comment</label>
                <div class="rv-field rv-top">
                    <i class="fa-solid fa-comment-dots"></i>
                    <textarea name="comment" id="rvComment" rows="4" class="form-control" required maxlength="1000"
                              placeholder="What did you think about this book?"><%= esc(fvComment) %></textarea>
                    <div class="invalid-feedback">Please write a short comment.</div>
                </div>
                <div class="rv-counter" id="rvChars">0 / 1000</div>
            </div>

            <button type="submit" class="btn btn-primary" id="rvSave">
                <i class="fa-solid fa-floppy-disk me-1"></i>Save</button>
            <a href="review" class="btn btn-outline-light ms-2">
                <i class="fa-solid fa-xmark me-1"></i>Cancel</a>
        </form>
    </div>
</div>

<script>
    (function () {
        var labels = ['', 'Poor', 'Below Average', 'Average', 'Very Good', 'Outstanding'];
        var form = document.getElementById('rvForm');
        var save = document.getElementById('rvSave');
        var saveHtml = save.innerHTML;
        var text = document.getElementById('rvRatingText');
        var comment = document.getElementById('rvComment');
        var chars = document.getElementById('rvChars');

        // rating label next to the stars
        function showRating() {
            var chosen = form.querySelector('input[name="rating"]:checked');
            text.textContent = chosen ? chosen.value + ' / 5 - ' + labels[Number(chosen.value)] : '';
        }
        form.addEventListener('change', function (e) {
            if (e.target && e.target.name === 'rating') showRating();
        });
        showRating();

        // live character counter
        function count() {
            chars.textContent = comment.value.length + ' / ' + comment.getAttribute('maxlength');
        }
        comment.addEventListener('input', count);
        count();

        // validation + stop double submit
        form.addEventListener('submit', function (e) {
            if (!form.checkValidity()) {
                e.preventDefault();
                form.classList.add('was-validated');
                var bad = form.querySelector(':invalid');
                if (bad && bad.focus) bad.focus();
                return;
            }
            save.disabled = true;
            save.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Saving...';
        });

        // restore the button if the user comes back with the browser's Back button
        window.addEventListener('pageshow', function () {
            save.disabled = false;
            save.innerHTML = saveHtml;
        });
    })();
</script>
</body>
</html>
