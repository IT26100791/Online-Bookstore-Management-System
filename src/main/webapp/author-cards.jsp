<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.Author" %>
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
    <title>Aura Books - Our Authors</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
    <style>
        /* ---- author cards page (uses the colours already defined in aura.css) ---- */
        @keyframes ac-rise { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: none; } }
        .ac-stats { display: flex; flex-wrap: wrap; gap: 1rem; margin: 0 0 1.5rem; }
        .ac-stat { min-width: 140px; padding: .85rem 1.2rem; border: 1px solid var(--line); border-radius: 4px; background: rgba(244, 237, 226, .04); }
        .ac-stat b { display: block; font-family: var(--display); font-size: 1.7rem; line-height: 1.1; color: var(--gold); }
        .ac-stat span { color: var(--muted); font-size: .88rem; }

        .ac-tools { display: flex; flex-wrap: wrap; align-items: center; gap: .75rem 1rem; margin-bottom: 1rem; }
        .ac-search { position: relative; flex: 1 1 260px; max-width: 380px; margin: 0; }
        .ac-search i { position: absolute; left: .85rem; top: 50%; transform: translateY(-50%); color: var(--muted); pointer-events: none; }
        .ac-search input { width: 100%; padding: .6rem .85rem .6rem 2.4rem; border: 1px solid rgba(244, 237, 226, .24); border-radius: 3px; background: rgba(244, 237, 226, .06); color: var(--paper); }
        .ac-search input:focus { outline: none; border-color: var(--gold); box-shadow: 0 0 0 .2rem rgba(226, 169, 59, .25); }
        .ac-chips { display: flex; gap: .4rem; }
        .ac-chip, .ac-letter { padding: .35rem .85rem; border: 1px solid var(--line); border-radius: 999px; background: transparent; color: var(--paper); font-weight: 700; font-size: .85rem; }
        .ac-chip:hover, .ac-letter:hover { border-color: rgba(226, 169, 59, .6); color: var(--gold-hi); }
        .ac-chip[aria-pressed="true"], .ac-letter[aria-pressed="true"] { border-color: var(--gold); background: rgba(226, 169, 59, .16); color: var(--gold); }
        .ac-select { width: auto; padding: .5rem 2.2rem .5rem .8rem; font-size: .9rem; }
        .ac-count { margin-left: auto; color: var(--muted); font-size: .9rem; }
        .ac-letters { display: flex; flex-wrap: wrap; gap: .35rem; margin-bottom: 1.5rem; }
        .ac-letter { min-width: 2.2rem; padding: .25rem .5rem; border-radius: 4px; text-align: center; }

        .ac-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(270px, 1fr)); gap: 1.25rem; }
        .ac-card { display: flex; flex-direction: column; gap: .9rem; padding: 1.4rem; border: 1px solid var(--line); border-left: 3px solid var(--gold); border-radius: 6px; background: rgba(244, 237, 226, .04); animation: ac-rise .4s ease-out both; }
        .ac-card.is-intl { border-left-color: var(--teal); }
        .ac-card:hover { background: rgba(244, 237, 226, .07); }
        .ac-top { display: flex; align-items: center; gap: .9rem; }
        .ac-avatar { display: grid; place-items: center; flex: none; width: 56px; height: 56px; border-radius: 50%; background: hsl(var(--h, 30) 45% 32%); border: 1px solid rgba(255, 255, 255, .22); color: #fff; font-family: var(--display); font-weight: 600; font-size: 1.5rem; }
        .ac-name { margin: 0 0 .3rem; font-family: var(--display); font-weight: 600; font-size: 1.2rem; line-height: 1.2; overflow-wrap: anywhere; }
        .ac-type { display: inline-block; padding: .1rem .6rem; border: 1px solid; border-radius: 3px; font-weight: 700; font-size: .78rem; }
        .ac-type.t-local { color: var(--gold); border-color: rgba(226, 169, 59, .6); background: rgba(226, 169, 59, .12); }
        .ac-type.t-intl { color: #86dcd7; border-color: rgba(42, 140, 138, .7); background: rgba(42, 140, 138, .16); }
        .ac-desc { margin: 0; color: var(--muted); font-family: var(--serif); font-size: 1.1rem; line-height: 1.35; }
        .ac-meta { margin: 0; display: grid; gap: .4rem; font-size: .9rem; }
        .ac-meta div { display: flex; align-items: baseline; gap: .6rem; }
        .ac-meta dt { flex: none; width: 3.2rem; color: var(--muted); font-weight: 400; }
        .ac-meta dd { margin: 0; min-width: 0; overflow-wrap: anywhere; }
        .ac-copy { min-width: 1.7rem; min-height: 1.7rem; margin-left: .35rem; padding: 0 .35rem; border: 0; border-radius: 3px; background: none; color: var(--muted); }
        .ac-copy:hover { color: var(--gold-hi); }
        .ac-actions { display: flex; flex-wrap: wrap; gap: .5rem; margin-top: auto; padding-top: .3rem; }
        .ac-avatar { overflow: hidden; }
        .ac-photo { width: 100%; height: 100%; object-fit: cover; }
        .ac-bio { margin: 0; color: var(--paper); font-size: .92rem; line-height: 1.5; white-space: pre-line; overflow-wrap: anywhere; display: -webkit-box; -webkit-line-clamp: 4; -webkit-box-orient: vertical; overflow: hidden; }
        .ac-bio.is-open { display: block; -webkit-line-clamp: unset; }
        .ac-more { align-self: flex-start; margin-top: -.5rem; padding: 0; border: 0; background: none; color: var(--gold); font-size: .85rem; font-weight: 700; }
        .ac-more:hover { color: var(--gold-hi); }
        .ac-none { color: rgba(185, 176, 166, .7); }
        .ac-empty { padding: 2.5rem 0; text-align: center; color: var(--muted); }

        .ac-toasts { position: fixed; right: 1rem; bottom: 1rem; z-index: 110; display: grid; gap: .6rem; width: min(360px, calc(100vw - 2rem)); }
        .ac-toast { display: flex; align-items: flex-start; gap: .7rem; padding: .8rem .9rem; border: 1px solid var(--line); border-left: 3px solid var(--teal); border-radius: 4px; background: #211e25; box-shadow: 0 18px 40px rgba(0, 0, 0, .5); animation: ac-rise .3s ease-out both; }
        .ac-toast i { margin-top: .2rem; color: #86dcd7; }
        .ac-toast.is-leaving { opacity: 0; transition: opacity .25s; }
        dialog.ac-dialog { width: min(420px, calc(100vw - 2rem)); padding: 1.75rem; border: 1px solid var(--line); border-radius: 8px; background: #1c1a1f; color: var(--paper); text-align: center; box-shadow: 0 30px 70px rgba(0, 0, 0, .6); }
        dialog.ac-dialog::backdrop { background: rgba(10, 8, 12, .74); }
        .ac-dialog h3 { margin: .5rem 0 .4rem; font-family: var(--display); font-weight: 600; font-size: 1.4rem; }
        .ac-dialog p { margin: 0; color: var(--muted); overflow-wrap: anywhere; }
        .ac-dialog .ac-actions { justify-content: center; margin-top: 1.4rem; }
        @media (prefers-reduced-motion: reduce) { .ac-card, .ac-toast { animation: none; } }
        @media print {
            .site-bar, .ac-tools, .ac-letters, .ac-actions, .ac-copy, .ac-head-actions { display: none !important; }
            body.aura { background: #fff !important; color: #000 !important; }
            .panel, .ac-card { background: #fff !important; border-color: #bbb !important; box-shadow: none !important; color: #000 !important; }
            .ac-stat b, .ac-desc, .ac-meta dt, .ac-stat span { color: #000 !important; }
        }
    </style>
</head>
<body class="aura">
<% boolean canEdit = com.bookstore.util.SessionUtil.isAdmin(request); %>
<% request.setAttribute("navActive", "author"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>
<%
    List<Author> authors = (List<Author>) request.getAttribute("authors");
    int acTotal = authors == null ? 0 : authors.size();
    int acIntl = 0;
    if (authors != null) {
        for (Author acA : authors) {
            if ("International".equalsIgnoreCase(acA.getAuthorType())) acIntl++;
        }
    }
    String msg = (String) request.getAttribute("msg");
%>
<div class="container">
    <div class="panel">
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-pen-nib me-2"></i>Our Authors</h2>
                <p class="text-light opacity-75 mb-0">The writers behind the books at Aura Books</p>
            </div>
            <div class="ac-head-actions">
                <a href="index.jsp" class="btn btn-outline-light me-2"><i class="fa-solid fa-house me-1"></i>Home</a>
                <a href="author" class="btn btn-outline-light me-2"><i class="fa-solid fa-table-list me-1"></i>Table view</a>
                <button type="button" class="btn btn-outline-light me-2" onclick="window.print()"><i class="fa-solid fa-print me-1"></i>Print</button>
                <% if (canEdit) { %>
                <a href="author?action=new" class="btn btn-primary"><i class="fa-solid fa-plus me-2"></i>Add New Author</a>
                <% } %>
            </div>
        </div>

        <% if (msg != null && !msg.isEmpty()) { %>
        <div class="alert alert-info py-2" id="acMsg"><%= esc(msg) %></div>
        <% } %>

        <div class="ac-stats">
            <div class="ac-stat"><b><%= acTotal %></b><span>Total authors</span></div>
            <div class="ac-stat"><b><%= acTotal - acIntl %></b><span>Local</span></div>
            <div class="ac-stat"><b><%= acIntl %></b><span>International</span></div>
        </div>

        <% if (acTotal > 0) { %>
        <div class="ac-tools">
            <label class="ac-search">
                <i class="fa-solid fa-magnifying-glass"></i>
                <input type="search" id="acSearch" placeholder="Search by name, ID, email or description&hellip;" aria-label="Search authors">
            </label>
            <div class="ac-chips" role="group" aria-label="Filter by type">
                <button type="button" class="ac-chip" data-type="" aria-pressed="true">All</button>
                <button type="button" class="ac-chip" data-type="local" aria-pressed="false">Local</button>
                <button type="button" class="ac-chip" data-type="international" aria-pressed="false">International</button>
            </div>
            <select id="acSort" class="form-select ac-select" aria-label="Sort authors">
                <option value="name-asc">Name A&ndash;Z</option>
                <option value="name-desc">Name Z&ndash;A</option>
                <option value="id">Author ID</option>
            </select>
            <span class="ac-count" id="acCount" aria-live="polite"></span>
        </div>
        <div class="ac-letters" id="acLetters" role="group" aria-label="Filter by first letter"></div>
        <% } %>

        <div class="ac-grid" id="acGrid">
            <%-- LOOP-START --%>
            <%
                if (authors != null) {
                    for (Author a : authors) {
                        boolean acIsIntl = "International".equalsIgnoreCase(a.getAuthorType());
                        String acName = a.getName() == null ? "" : a.getName();
            %>
            <article class="ac-card <%= acIsIntl ? "is-intl" : "is-local" %>"
                     data-name="<%= esc(acName.toLowerCase()) %>" data-id="<%= esc(a.getId().toLowerCase()) %>"
                     data-email="<%= esc(a.getEmail().toLowerCase()) %>" data-bio="<%= esc(a.getBio().toLowerCase()) %>" data-type="<%= acIsIntl ? "international" : "local" %>"
                     data-letter="<%= acName.isEmpty() || !Character.isLetter(acName.charAt(0)) ? "#" : acName.substring(0, 1).toUpperCase() %>">
                <div class="ac-top">
                    <span class="ac-avatar" style="--h:<%= Math.abs(acName.hashCode()) % 360 %>" aria-hidden="true"><% if (a.hasPhoto()) { %><img class="ac-photo" alt="" loading="lazy" src="author?action=photo&name=<%= java.net.URLEncoder.encode(a.getPhoto(), "UTF-8") %>"><% } else { %><%= acName.isEmpty() ? "?" : esc(acName.substring(0, 1).toUpperCase()) %><% } %></span>
                    <div>
                        <h3 class="ac-name"><%= esc(acName) %></h3>
                        <span class="ac-type <%= acIsIntl ? "t-intl" : "t-local" %>"><i class="fa-solid <%= acIsIntl ? "fa-earth-americas" : "fa-location-dot" %> me-1"></i><%= esc(a.getAuthorType()) %></span>
                    </div>
                </div>
                <p class="ac-desc"><%= esc(a.getTypeDescription()) %></p>
                <% if (!a.getBio().isEmpty()) { %>
                <p class="ac-bio"><%= esc(a.getBio()) %></p>
                <button type="button" class="ac-more" hidden>Read more</button>
                <% } %>
                <dl class="ac-meta">
                    <div><dt>ID</dt><dd><%= esc(a.getId()) %></dd></div>
                    <% if (!a.getEmail().isEmpty()) { %>
                    <div><dt>Email</dt><dd><a href="mailto:<%= esc(a.getEmail()) %>"><%= esc(a.getEmail()) %></a><button type="button" class="ac-copy" data-copy="<%= esc(a.getEmail()) %>" title="Copy email" aria-label="Copy email"><i class="fa-regular fa-copy"></i></button></dd></div>
                    <% } else { %>
                    <div><dt>Email</dt><dd class="ac-none">Not available</dd></div>
                    <% } %>
                </dl>
                <div class="ac-actions">
                    <% if (!a.getEmail().isEmpty()) { %>
                    <a class="btn btn-sm btn-outline-light" href="mailto:<%= esc(a.getEmail()) %>"><i class="fa-solid fa-envelope me-1"></i>Email</a>
                    <% } %>
                    <% if (canEdit) { %>
                    <a class="btn btn-sm btn-warning btn-action"
                       href="author?action=edit&id=<%= java.net.URLEncoder.encode(a.getId(), "UTF-8") %>"><i class="fa-solid fa-pen-to-square me-1"></i>Edit</a>
                    <form method="post" action="author" class="ac-delete" data-name="<%= esc(acName) %>" style="display:inline">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="<%= esc(a.getId()) %>">
                        <button type="submit" class="btn btn-sm btn-danger btn-action"><i class="fa-solid fa-trash me-1"></i>Delete</button>
                    </form>
                    <% } %>
                </div>
            </article>
            <%
                    }
                }
            %>
            <%-- LOOP-END --%>
        </div>

        <p class="ac-empty" id="acEmpty" <%= acTotal > 0 ? "hidden" : "" %>>
            <%= acTotal > 0 ? "No authors match your filters." : "No authors yet." %>
        </p>
    </div>
</div>

<dialog class="ac-dialog" id="acDialog" aria-labelledby="acDialogTitle">
    <i class="fa-solid fa-trash-can fa-2x" style="color: var(--gold)"></i>
    <h3 id="acDialogTitle">Delete this author?</h3>
    <p id="acDialogText"></p>
    <div class="ac-actions">
        <button type="button" class="btn btn-outline-light" id="acNo">Cancel</button>
        <button type="button" class="btn btn-danger" id="acYes">Yes, delete</button>
    </div>
</dialog>
<div class="ac-toasts" id="acToasts" role="region" aria-label="Notifications"></div>

<script>
(function () {
    var $ = function (s, r) { return (r || document).querySelector(s); };
    var $$ = function (s, r) { return Array.prototype.slice.call((r || document).querySelectorAll(s)); };

    function toast(text) {
        var box = $('#acToasts');
        var t = document.createElement('div');
        t.className = 'ac-toast';
        t.setAttribute('role', 'status');
        t.innerHTML = '<i class="fa-solid fa-circle-check"></i><span></span>';
        t.lastChild.textContent = text;
        box.appendChild(t);
        setTimeout(function () { t.classList.add('is-leaving'); setTimeout(function () { t.remove(); }, 260); }, 4500);
    }

    /* success message from the server becomes a toast (errors stay on the page) */
    var msg = $('#acMsg');
    if (msg && !/not |already|required|denied|invalid|commas/i.test(msg.textContent)) {
        toast(msg.textContent.trim());
        msg.hidden = true;
    }

    /* long descriptions: "Read more" */
    $$('.ac-bio').forEach(function (p) {
        var more = p.nextElementSibling;
        if (!more || !more.classList.contains('ac-more')) return;
        if (p.scrollHeight > p.clientHeight + 1) more.hidden = false;
        more.addEventListener('click', function () {
            var open = p.classList.toggle('is-open');
            more.textContent = open ? 'Show less' : 'Read more';
        });
    });

    /* a missing photo file falls back to the initial letter */
    $$('img.ac-photo').forEach(function (img) {
        img.addEventListener('error', function () {
            var holder = img.parentNode, name = holder.parentNode.querySelector('.ac-name');
            holder.textContent = (name && name.textContent.trim().charAt(0) || '?').toUpperCase();
        });
    });

    /* delete confirmation */
    var dlg = $('#acDialog'), pending = null;
    $$('form.ac-delete').forEach(function (f) {
        f.addEventListener('submit', function (e) {
            if (!dlg || typeof dlg.showModal !== 'function') {
                if (!window.confirm('Delete this author?')) e.preventDefault();
                return;
            }
            if (f.dataset.ok) return;
            e.preventDefault();
            pending = f;
            $('#acDialogText').textContent = f.dataset.name || '';
            dlg.showModal();
        });
    });
    if (dlg) {
        $('#acNo').addEventListener('click', function () { dlg.close(); pending = null; });
        $('#acYes').addEventListener('click', function () { var f = pending; dlg.close(); if (f) { f.dataset.ok = '1'; f.submit(); } });
    }

    /* copy email */
    $$('.ac-copy').forEach(function (b) {
        b.addEventListener('click', function () {
            var v = b.getAttribute('data-copy');
            function done() { toast('Email copied: ' + v); }
            if (navigator.clipboard && navigator.clipboard.writeText) {
                navigator.clipboard.writeText(v).then(done, function () { fallback(v); done(); });
            } else { fallback(v); done(); }
        });
    });
    function fallback(v) {
        var ta = document.createElement('textarea');
        ta.value = v; ta.style.position = 'fixed'; ta.style.opacity = '0';
        document.body.appendChild(ta); ta.select();
        try { document.execCommand('copy'); } catch (e) { /* ignore */ }
        ta.remove();
    }

    /* search, type filter, A-Z filter, sort */
    var grid = $('#acGrid'), search = $('#acSearch');
    if (!grid || !search) return;
    var cards = $$('.ac-card', grid), state = { q: '', type: '', letter: '', sort: 'name-asc' };
    var count = $('#acCount'), empty = $('#acEmpty'), lettersBox = $('#acLetters');

    var letters = [];
    cards.forEach(function (c) { if (letters.indexOf(c.dataset.letter) < 0) letters.push(c.dataset.letter); });
    letters.sort();
    [''].concat(letters).forEach(function (l) {
        var b = document.createElement('button');
        b.type = 'button';
        b.className = 'ac-letter';
        b.dataset.letter = l;
        b.textContent = l || 'All';
        b.setAttribute('aria-pressed', l === '' ? 'true' : 'false');
        lettersBox.appendChild(b);
    });

    function press(group, attr, value) {
        $$('[data-' + attr + ']', group).forEach(function (b) { b.setAttribute('aria-pressed', b.getAttribute('data-' + attr) === value ? 'true' : 'false'); });
    }
    $$('.ac-chip').forEach(function (b) {
        b.addEventListener('click', function () { state.type = b.dataset.type; press($('.ac-chips'), 'type', state.type); apply(); });
    });
    lettersBox.addEventListener('click', function (e) {
        var b = e.target.closest('.ac-letter');
        if (!b) return;
        state.letter = b.dataset.letter; press(lettersBox, 'letter', state.letter); apply();
    });
    search.addEventListener('input', function () { state.q = search.value.trim().toLowerCase(); apply(); });
    $('#acSort').addEventListener('change', function (e) { state.sort = e.target.value; apply(); });

    function apply() {
        var shown = 0;
        cards.sort(function (a, b) {
            if (state.sort === 'id') return a.dataset.id.localeCompare(b.dataset.id, undefined, { numeric: true });
            var r = a.dataset.name.localeCompare(b.dataset.name);
            return state.sort === 'name-desc' ? -r : r;
        }).forEach(function (c) {
            grid.appendChild(c);
            var hay = (c.dataset.name + ' ' + c.dataset.id + ' ' + c.dataset.email + ' ' + (c.dataset.bio || '')).toLowerCase();
            var ok = (!state.q || hay.indexOf(state.q) > -1) &&
                     (!state.type || c.dataset.type === state.type) &&
                     (!state.letter || c.dataset.letter === state.letter);
            c.hidden = !ok;
            if (ok) shown++;
        });
        count.textContent = shown + ' of ' + cards.length + ' authors';
        empty.hidden = shown > 0;
        empty.textContent = 'No authors match your filters.';
    }
    apply();
})();
</script>
</body>
</html>

