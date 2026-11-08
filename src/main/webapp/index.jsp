<%@ page contentType="text/html;charset=UTF-8" import="com.bookstore.model.User" %>
<%
    User u0 = (User) session.getAttribute("loggedUser");
    boolean loggedIn = u0 != null;
    boolean admin0 = loggedIn && u0.canManageUsers();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Regent Books - Bookstore Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/book3d.css">
    <script>document.documentElement.classList.add('js');</script>
</head>
<body class="aura">
<% request.setAttribute("navActive", ""); %>
<%@ include file="/WEB-INF/navbar.jspf" %>

<!-- ===== Hero ===== -->
<section class="hero">
    <div class="hero-inner">
        <div class="hero-copy">
            <span class="hero-eyebrow">Welcome to Regent Books</span>
            <h1>Your bookstore, beautifully kept</h1>
            <div class="ornament" aria-hidden="true"><span></span></div>
            <p class="hero-lead">
                <%= admin0 ? "Manage books, authors, publishers, orders and reviews from one place."
                           : "Browse our books, place orders and share your reviews." %>
            </p>
            <div class="hero-actions">
                <% if (loggedIn) { %>
                <a class="btn btn-primary" href="book"><i class="fa-solid fa-book-open me-2"></i>Browse books</a>
                <a class="btn btn-outline-light" href="#explore">Explore the store</a>
                <% } else { %>
                <a class="btn btn-primary" href="login"><i class="fa-solid fa-right-to-bracket me-2"></i>Sign in</a>
                <a class="btn btn-outline-light" href="user?action=new">Register</a>
                <% } %>
            </div>
        </div>
        <div class="hero-art">
            <!-- animated 3D book (css/book3d.css): cover opens, pages turn, then it closes. Hover pauses it. -->
            <div class="book3d" role="img" aria-label="An animated book opening and turning its pages">
                <div class="book3d__shadow"></div>
                <div class="book3d__float">
                    <div class="book3d__body">

                        <!-- last page -->
                        <div class="bk-back bk-paper">
                            <i class="bk-ribbon"></i>
                            <div class="bk-page bk-end">
                                <p class="bk-kicker">Happy reading</p>
                                <h4 class="bk-h bk-h--lg">The End</h4>
                                <i class="bk-orn"></i>
                                <p class="bk-sub">Regent Books<br>Online bookstore</p>
                                <span class="bk-folio">12</span>
                            </div>
                        </div>

                        <!-- leaf 4: front = Chapter II + verse, back = bookshelf -->
                        <div class="bk-leaf bk-leaf--4">
                            <div class="bk-face bk-face--front bk-paper"><div class="bk-page">
                                <p class="bk-kicker">Chapter II</p>
                                <h4 class="bk-h">Salt &amp; Stars</h4>
                                <i class="bk-orn"></i>
                                <p class="bk-verse">Count the stars like pages,<br>one for every mile;<br>the road forgets our footsteps,<br>the story stays a while.</p>
                                <span class="bk-folio">11</span>
                            </div></div>
                            <div class="bk-face bk-face--back bk-paper"><div class="bk-page">
                                <p class="bk-kicker">The shelf</p>
                                <h4 class="bk-h">Find your next book</h4>
                                <div class="bk-shelf"><i></i><i></i><i></i><i></i><i></i><i></i><i></i><i></i></div>
                                <p class="bk-cap">Browse &middot; Order &middot; Review</p>
                                <span class="bk-folio">10</span>
                            </div></div>
                        </div>

                        <!-- leaf 3: front = picture, back = quote -->
                        <div class="bk-leaf bk-leaf--3">
                            <div class="bk-face bk-face--front bk-paper"><div class="bk-page">
                                <p class="bk-kicker">Plate I</p>
                                <div class="bk-scene"><b></b><i></i></div>
                                <p class="bk-cap">The salt road at dusk</p>
                                <span class="bk-folio">9</span>
                            </div></div>
                            <div class="bk-face bk-face--back bk-paper"><div class="bk-page">
                                <blockquote class="bk-quote" style="margin:auto 0">Every book is a door. Open it gently, and see who is waiting inside.<small>Regent Books</small></blockquote>
                                <span class="bk-folio">8</span>
                            </div></div>
                        </div>

                        <!-- leaf 2: front = Chapter I, back = story continues -->
                        <div class="bk-leaf bk-leaf--2">
                            <div class="bk-face bk-face--front bk-paper"><div class="bk-page">
                                <p class="bk-kicker">Chapter I</p>
                                <h4 class="bk-h">The Road Begins</h4>
                                <i class="bk-orn"></i>
                                <p class="bk-dc">The road ran white with salt beneath a sky the colour of old paper, and every traveller carried one book for the long walk home.</p>
                                <p>Salt crunched underfoot like turning pages.</p>
                                <span class="bk-folio">7</span>
                            </div></div>
                            <div class="bk-face bk-face--back bk-paper"><div class="bk-page">
                                <p>By evening the pages had grown soft as cloth, and the stories inside them had learned the shape of the wind.</p>
                                <p>Some read aloud to the dusk. Others read in silence, and the silence listened back.</p>
                                <p>Far ahead, a lantern swung from a cart.</p>
                                <span class="bk-folio">6</span>
                            </div></div>
                        </div>

                        <!-- leaf 1: front = title page, back = contents -->
                        <div class="bk-leaf bk-leaf--1">
                            <div class="bk-face bk-face--front bk-paper"><div class="bk-page bk-end">
                                <p class="bk-kicker">A Reader&rsquo;s Companion</p>
                                <h4 class="bk-h bk-h--lg">Regent Books</h4>
                                <i class="bk-orn"></i>
                                <p class="bk-sub">Stories &middot; Ideas &middot; Worlds</p>
                                <span class="bk-folio">3</span>
                            </div></div>
                            <div class="bk-face bk-face--back bk-paper"><div class="bk-page">
                                <p class="bk-kicker">Contents</p>
                                <i class="bk-orn" style="margin-top:.2em"></i>
                                <ul class="bk-toc">
                                    <li><b>I</b> The Road Begins <i></i> 7</li>
                                    <li><b>II</b> Salt &amp; Stars <i></i> 11</li>
                                    <li><b>III</b> The Shelf <i></i> 10</li>
                                    <li><b>IV</b> The End <i></i> 12</li>
                                </ul>
                                <span class="bk-folio">2</span>
                            </div></div>
                        </div>

                        <!-- cover: front = picture, back = bookplate -->
                        <div class="bk-cover">
                            <div class="bk-face bk-face--front"><img src="<%= request.getContextPath() %>/images/salt-road.jpeg" alt=""></div>
                            <div class="bk-face bk-face--back"><div class="bk-plate"><b>Ex Libris</b><span>This book belongs to a curious reader</span></div></div>
                        </div>
                    </div>
                </div>
                <i class="bk-spark s1"></i><i class="bk-spark s2"></i><i class="bk-spark s3"></i>
                <i class="bk-spark s4"></i><i class="bk-spark s5"></i><i class="bk-spark s6"></i>
            </div>
        </div>
    </div>
    <a class="scroll-cue" href="#explore" aria-label="Scroll to explore"><i class="fa-solid fa-chevron-down"></i></a>
</section>

<!-- ===== Highlights strip ===== -->
<section class="perks" aria-label="Why Regent Books">
    <div class="perks-inner">
        <div class="perk"><i class="fa-solid fa-book-open-reader"></i><div><b>Curated catalogue</b><span>Titles across every genre</span></div></div>
        <div class="perk"><i class="fa-solid fa-building-columns"></i><div><b>Trusted publishers</b><span>Local and international houses</span></div></div>
        <div class="perk"><i class="fa-solid fa-box-open"></i><div><b>Easy ordering</b><span>Place and track in a few clicks</span></div></div>
        <div class="perk"><i class="fa-solid fa-star"></i><div><b>Honest reviews</b><span>Ratings from real readers</span></div></div>
    </div>
</section>

<!-- ===== Modules ===== -->
<% if (loggedIn) { %>
<section class="modules" id="explore">
    <div class="modules-inner">
        <span class="section-eyebrow">Regent Books</span>
        <h2><%= admin0 ? "Manage the store" : "Explore the store" %></h2>
        <div class="module-grid">
            <a class="module reveal" href="book">
                <i class="fa-solid fa-book"></i>
                <h3>Book Catalog</h3>
                <p><%= admin0 ? "Manage books, titles, genres &amp; prices" : "Browse books, genres &amp; prices" %></p>
                <span class="module-go" aria-hidden="true"><i class="fa-solid fa-arrow-right"></i></span>
            </a>
            <a class="module reveal" href="author">
                <i class="fa-solid fa-pen-nib"></i>
                <h3>Author Directory</h3>
                <p><%= admin0 ? "Manage author profiles and contacts" : "Discover the authors" %></p>
                <span class="module-go" aria-hidden="true"><i class="fa-solid fa-arrow-right"></i></span>
            </a>
            <a class="module reveal" href="publisher">
                <i class="fa-solid fa-building"></i>
                <h3>Publishers</h3>
                <p><%= admin0 ? "Publishing houses and phone lines" : "See our publishing houses" %></p>
                <span class="module-go" aria-hidden="true"><i class="fa-solid fa-arrow-right"></i></span>
            </a>
            <a class="module reveal" href="order">
                <i class="fa-solid fa-cart-shopping"></i>
                <h3><%= admin0 ? "Order Management" : "My Orders" %></h3>
                <p><%= admin0 ? "Track order statuses &amp; customers" : "Place and track your orders" %></p>
                <span class="module-go" aria-hidden="true"><i class="fa-solid fa-arrow-right"></i></span>
            </a>
            <a class="module reveal" href="review">
                <i class="fa-solid fa-star"></i>
                <h3>Reviews &amp; Ratings</h3>
                <p>Customer feedback and star ratings</p>
                <span class="module-go" aria-hidden="true"><i class="fa-solid fa-arrow-right"></i></span>
            </a>
            <% if (admin0) { %>
            <a class="module reveal" href="user">
                <i class="fa-solid fa-users-gear"></i>
                <h3>User Management</h3>
                <p>Administer users, roles &amp; security</p>
                <span class="module-go" aria-hidden="true"><i class="fa-solid fa-arrow-right"></i></span>
            </a>
            <% } %>
        </div>
    </div>
</section>
<% } else { %>
<section class="modules" id="explore">
    <div class="modules-inner">
        <span class="section-eyebrow">Regent Books</span>
        <h2>Account &amp; access</h2>
        <div class="module-grid">
            <a class="module reveal" href="login">
                <i class="fa-solid fa-right-to-bracket"></i>
                <h3>User Login</h3>
                <p>Sign in to your customer or admin portal</p>
                <span class="module-go" aria-hidden="true"><i class="fa-solid fa-arrow-right"></i></span>
            </a>
            <a class="module reveal" href="user?action=new">
                <i class="fa-solid fa-user-plus"></i>
                <h3>Register</h3>
                <p>Create a new customer account</p>
                <span class="module-go" aria-hidden="true"><i class="fa-solid fa-arrow-right"></i></span>
            </a>
        </div>
    </div>
</section>
<% } %>

<!-- ===== Footer ===== -->
<footer class="site-foot">
    <div class="site-foot-inner">
        <span class="foot-brand">Regent Books</span>
        <span class="foot-note">&copy; <%= java.time.Year.now() %> Regent Books &middot; Bookstore Portal</span>
    </div>
</footer>

<script>
    (function () {
        var items = document.querySelectorAll('.reveal');
        if (!('IntersectionObserver' in window)) {
            items.forEach(function (el) { el.classList.add('in'); });
            return;
        }
        var io = new IntersectionObserver(function (entries) {
            entries.forEach(function (e) {
                if (e.isIntersecting) { e.target.classList.add('in'); io.unobserve(e.target); }
            });
        }, { threshold: .15 });
        items.forEach(function (el, i) {
            el.style.transitionDelay = (i % 3) * 90 + 'ms';
            io.observe(el);
        });
    })();
</script>
</body>
</html>








