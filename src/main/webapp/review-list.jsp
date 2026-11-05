<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.Review" %>
<%!
    String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aura Books - Review & Rating Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">

    <%-- Page-only styles. Uses neutral white overlays + inherited colours,
         so the shared aura.css colour scheme is not touched. --%>
    <style>
        .rv-fade { animation: rvFade .45s ease both; }
        @keyframes rvFade { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: none; } }

        /* ---- stat cards ---- */
        .rv-stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(190px, 1fr)); gap: 16px; margin-bottom: 24px; }
        .rv-stat { display: flex; align-items: center; gap: 14px; padding: 16px 18px;
                   background: rgba(255,255,255,.05); border: 1px solid rgba(255,255,255,.1);
                   border-radius: 14px; transition: transform .2s ease, background .2s ease; }
        .rv-stat:hover { transform: translateY(-3px); background: rgba(255,255,255,.09); }
        .rv-stat-icon { flex: 0 0 44px; width: 44px; height: 44px; display: grid; place-items: center;
                        border-radius: 12px; background: rgba(255,255,255,.08); font-size: 1.1rem; }
        .rv-stat-value { font-size: 1.6rem; font-weight: 700; line-height: 1.1; }
        .rv-stat-value small { font-size: .85rem; font-weight: 400; opacity: .6; }
        .rv-stat-label { font-size: .75rem; opacity: .7; text-transform: uppercase; letter-spacing: .07em; }
        .rv-stat .star-rating { font-size: .75rem; }

        /* ---- toolbar ---- */
        .rv-toolbar { display: flex; flex-wrap: wrap; gap: 12px; align-items: center; margin-bottom: 16px; }
        .rv-search { position: relative; flex: 1 1 260px; }
        .rv-search i { position: absolute; left: 14px; top: 50%; transform: translateY(-50%); opacity: .6; pointer-events: none; }
        .rv-search .form-control { padding-left: 2.5rem; }
        .rv-toolbar .form-select { width: auto; flex: 0 0 auto; }
        .rv-count { margin-left: auto; font-size: .85rem; opacity: .7; }

        /* ---- table cells ---- */
        .rv-num { opacity: .6; font-variant-numeric: tabular-nums; }
        .rv-id { display: inline-block; padding: 2px 9px; border-radius: 999px; font-size: .8rem;
                 font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
                 background: rgba(255,255,255,.08); border: 1px solid rgba(255,255,255,.1); }
        .rv-book { font-weight: 600; }
        .rv-reviewer { font-weight: 600; display: flex; align-items: center; gap: 6px; flex-wrap: wrap; margin-bottom: 2px; }
        .rv-text { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
                   opacity: .85; cursor: pointer; max-width: 460px; }
        .rv-text.open { display: block; -webkit-line-clamp: unset; }
        .btn-action { transition: transform .15s ease; }
        .btn-action:hover { transform: translateY(-2px) scale(1.08); }

        /* ---- empty state ---- */
        .rv-empty { text-align: center; padding: 40px 10px; }
        .rv-empty i { font-size: 2.6rem; opacity: .4; margin-bottom: 12px; display: block; }
        .rv-empty p { margin-bottom: 16px; opacity: .75; }

        @media (max-width: 576px) { .rv-count { margin-left: 0; width: 100%; } }
        @media (prefers-reduced-motion: reduce) {
            .rv-fade { animation: none; }
            .rv-stat, .btn-action { transition: none; }
        }
    </style>
</head>
<body class="aura">
<% boolean canEdit = com.bookstore.util.SessionUtil.isAdmin(request); %>
<% request.setAttribute("navActive", "review"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>
<%
    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
    String msg = (String) request.getAttribute("msg");

    int rvTotal = (reviews == null) ? 0 : reviews.size();
    int rvSum = 0, rvFive = 0, rvVerified = 0;
    if (reviews != null) {
        for (Review rv : reviews) {
            rvSum += rv.getRating();
            if (rv.getRating() == 5) rvFive++;
            if ("VERIFIED".equalsIgnoreCase(rv.getType())) rvVerified++;
        }
    }
    double rvAvg = rvTotal == 0 ? 0 : (double) rvSum / rvTotal;
    int rvAvgRounded = (int) Math.round(rvAvg);
%>
<div class="container">
    <div class="panel rv-fade">
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-star me-2"></i>Review Management</h2>
                <p class="text-light opacity-75 mb-0">Customer reviews and ratings for catalog books</p>
            </div>
            <div>
                <a href="index.jsp" class="btn btn-outline-light me-2"><i class="fa-solid fa-house me-1"></i>Home</a>
                <a href="review?action=new" class="btn btn-primary">
                    <i class="fa-solid fa-plus me-2"></i>Add New Review
                </a>
            </div>
        </div>

        <% if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-info py-2 d-flex justify-content-between align-items-center" role="alert">
              <span><i class="fa-solid fa-circle-info me-2"></i><%= esc(msg) %></span>
              <button type="button" class="btn-close" aria-label="Close"
                      onclick="this.parentNode.remove()"></button>
          </div>
        <% } %>

        <% if (rvTotal > 0) { %>
        <!-- summary cards -->
        <div class="rv-stats">
            <div class="rv-stat">
                <div class="rv-stat-icon"><i class="fa-solid fa-comments"></i></div>
                <div>
                    <div class="rv-stat-value"><%= rvTotal %></div>
                    <div class="rv-stat-label">Total reviews</div>
                </div>
            </div>
            <div class="rv-stat">
                <div class="rv-stat-icon"><i class="fa-solid fa-chart-simple"></i></div>
                <div>
                    <div class="rv-stat-value"><%= String.format(Locale.US, "%.1f", rvAvg) %> <small>/ 5</small></div>
                    <span class="star-rating">
                        <% for (int s = 1; s <= 5; s++) { %>
                            <i class="fa-<%= s <= rvAvgRounded ? "solid" : "regular" %> fa-star"></i>
                        <% } %>
                    </span>
                </div>
            </div>
            <div class="rv-stat">
                <div class="rv-stat-icon"><i class="fa-solid fa-award"></i></div>
                <div>
                    <div class="rv-stat-value"><%= rvFive %></div>
                    <div class="rv-stat-label">5-star reviews</div>
                </div>
            </div>
            <div class="rv-stat">
                <div class="rv-stat-icon"><i class="fa-solid fa-circle-check"></i></div>
                <div>
                    <div class="rv-stat-value"><%= rvVerified %></div>
                    <div class="rv-stat-label">Verified purchases</div>
                </div>
            </div>
        </div>

        <!-- search / filter / sort -->
        <div class="rv-toolbar">
            <div class="rv-search">
                <i class="fa-solid fa-magnifying-glass"></i>
                <input type="search" id="rvSearch" class="form-control"
                       placeholder="Search by ID, book, reviewer or comment..." aria-label="Search reviews">
            </div>
            <select id="rvFilter" class="form-select" aria-label="Filter by rating">
                <option value="all">All ratings</option>
                <option value="5">5 stars</option>
                <option value="4">4 stars</option>
                <option value="3">3 stars</option>
                <option value="2">2 stars</option>
                <option value="1">1 star</option>
            </select>
            <select id="rvType" class="form-select" aria-label="Filter by review type">
                <option value="all">All types</option>
                <option value="VERIFIED">Verified purchase</option>
                <option value="PUBLIC">Public</option>
            </select>
            <select id="rvSort" class="form-select" aria-label="Sort reviews">
                <option value="default">Default order</option>
                <option value="high">Highest rated</option>
                <option value="low">Lowest rated</option>
                <option value="title">Book title A-Z</option>
            </select>
            <div class="rv-count" id="rvCount" aria-live="polite"></div>
        </div>
        <% } %>

        <div class="table-responsive">
            <table class="table table-dark-glass table-hover rounded overflow-hidden align-middle">
                <thead>
                <tr>
                    <th>#</th><th>Review ID</th><th>Book Title</th><th>Rating</th><th>Comment</th>
                    <% if (canEdit) { %><th class="text-center">Actions</th><% } %>
                </tr>
                </thead>
                <tbody id="rvBody">
                <%
                    int n = 1;
                    if (reviews != null && !reviews.isEmpty()) {
                        for (Review r : reviews) {
                            int stars = r.getRating();
                            String rvBadge = r.getBadge();
                            boolean rvIsVerified = "VERIFIED".equalsIgnoreCase(r.getType());
                %>
                <tr data-rating="<%= stars %>" data-type="<%= esc(r.getType()) %>">
                    <td class="rv-num"><%= String.format("%02d", n++) %></td>
                    <td><span class="rv-id"><%= esc(r.getReviewId()) %></span></td>
                    <td><span class="rv-book"><i class="fa-solid fa-book me-2 opacity-50"></i><%= esc(r.getBookTitle()) %></span></td>
                    <td class="text-nowrap">
                        <span class="star-rating">
                            <% for (int s = 1; s <= 5; s++) { %>
                                <i class="fa-<%= s <= stars ? "solid" : "regular" %> fa-star"></i>
                            <% } %>
                        </span>
                        <span class="ms-1 small text-light opacity-75">(<%= r.getRating() %>/5)</span>
                    </td>
                    <td>
                        <div class="rv-reviewer">
                            <i class="fa-solid fa-circle-user opacity-50"></i><%= esc(r.getReviewer()) %>
                            <% if (rvBadge != null && !rvBadge.isEmpty()) { %>
                                <span class="badge bg-secondary">
                                    <% if (rvIsVerified) { %><i class="fa-solid fa-circle-check me-1"></i><% } %><%= esc(rvBadge) %>
                                </span>
                            <% } %>
                        </div>
                        <div class="rv-text" title="Click to expand / collapse"><%= esc(r.getComment()) %></div>
                    </td>
                    <% if (canEdit) { %>
                    <td class="text-center text-nowrap">
                        <a class="btn btn-sm btn-warning btn-action me-1" title="Edit review" aria-label="Edit review"
                           href="review?action=edit&id=<%= java.net.URLEncoder.encode(r.getReviewId(), "UTF-8") %>">
                            <i class="fa-solid fa-pen-to-square"></i></a>
                        <form method="post" action="review" style="display:inline"
                              data-title="<%= esc(r.getBookTitle()) %>"
                              onsubmit="return rvConfirmDelete(this);">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= esc(r.getReviewId()) %>">
                            <button type="submit" class="btn btn-sm btn-danger btn-action" title="Delete review" aria-label="Delete review"><i class="fa-solid fa-trash"></i></button>
                        </form>
                    </td>
                    <% } %>
                </tr>
                <%      }
                %>
                <tr id="rvNoMatch" hidden>
                    <td colspan="<%= canEdit ? 6 : 5 %>">
                        <div class="rv-empty">
                            <i class="fa-solid fa-magnifying-glass"></i>
                            <p class="mb-0">No reviews match your search.</p>
                        </div>
                    </td>
                </tr>
                <%  } else { %>
                <tr>
                    <td colspan="<%= canEdit ? 6 : 5 %>">
                        <div class="rv-empty">
                            <i class="fa-regular fa-comment-dots"></i>
                            <p>No reviews yet.</p>
                            <a href="review?action=new" class="btn btn-primary btn-sm">
                                <i class="fa-solid fa-plus me-1"></i>Add the first review</a>
                        </div>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    function rvConfirmDelete(form) {
        var title = form.getAttribute('data-title') || 'this book';
        return confirm('Delete the review for "' + title + '"?');
    }

    (function () {
        var tbody = document.getElementById('rvBody');
        var none = document.getElementById('rvNoMatch');
        var q = document.getElementById('rvSearch');
        if (!tbody || !none || !q) return;

        var f = document.getElementById('rvFilter');
        var t = document.getElementById('rvType');
        var s = document.getElementById('rvSort');
        var c = document.getElementById('rvCount');
        var rows = Array.prototype.slice.call(tbody.querySelectorAll('tr[data-rating]'));
        rows.forEach(function (r, i) { r.setAttribute('data-index', i); });

        function num(r, key) { return Number(r.getAttribute(key)); }
        function titleOf(r) { return r.cells[2].textContent.trim().toLowerCase(); }
        function pad(v) { return v < 10 ? '0' + v : String(v); }

        function refresh() {
            var term = q.value.trim().toLowerCase();
            var rating = f.value;
            var type = t.value;
            var mode = s.value;

            var list = rows.slice();
            list.sort(function (a, b) {
                if (mode === 'high') return (num(b, 'data-rating') - num(a, 'data-rating')) || (num(a, 'data-index') - num(b, 'data-index'));
                if (mode === 'low')  return (num(a, 'data-rating') - num(b, 'data-rating')) || (num(a, 'data-index') - num(b, 'data-index'));
                if (mode === 'title') return titleOf(a).localeCompare(titleOf(b));
                return num(a, 'data-index') - num(b, 'data-index');
            });

            var shown = 0;
            list.forEach(function (r) {
                tbody.insertBefore(r, none);
                var okRating = (rating === 'all') || (r.getAttribute('data-rating') === rating);
                var okType = (type === 'all') || (r.getAttribute('data-type') === type);
                var okText = !term || r.textContent.toLowerCase().indexOf(term) !== -1;
                var visible = okRating && okType && okText;
                r.hidden = !visible;
                if (visible) { shown++; r.cells[0].textContent = pad(shown); }
            });

            none.hidden = shown !== 0;
            c.textContent = 'Showing ' + shown + ' of ' + rows.length + ' reviews';
        }

        q.addEventListener('input', refresh);
        f.addEventListener('change', refresh);
        t.addEventListener('change', refresh);
        s.addEventListener('change', refresh);

        // click a comment to expand / collapse it
        tbody.addEventListener('click', function (e) {
            var el = e.target.closest ? e.target.closest('.rv-text') : null;
            if (el) el.classList.toggle('open');
        });

        refresh();
    })();
</script>
</body>
</html>
