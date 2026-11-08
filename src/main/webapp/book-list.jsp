<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,com.bookstore.model.Book" %>
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
    <title>Aura Books - Book Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@500;600&family=Great+Vibes&family=Lato:wght@400;700&family=Playfair+Display:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/aura.css">
</head>
<body class="aura">
<% boolean canEdit = com.bookstore.util.SessionUtil.isAdmin(request); %>
<% request.setAttribute("navActive", "book"); %>
<%@ include file="/WEB-INF/navbar.jspf" %>
<div class="container">
    <section class="panel mb-4" aria-label="Sample books">
        <%
            List<Book> shelfBooks = (List<Book>) request.getAttribute("books");
            if (shelfBooks != null && !shelfBooks.isEmpty()) {
        %>
        <h2 class="shelf-title">Our catalog</h2>
        <p class="shelf-note">Click a book to see its description, author and cover.</p>
        <ul class="shelf-grid">
            <% for (Book sb : shelfBooks) { %>
            <li class="book">
                <a href="book?action=view&id=<%= java.net.URLEncoder.encode(sb.getId(), "UTF-8") %>"
                   style="color:inherit;text-decoration:none;display:block;" title="View details">
                <% if (sb.hasImage()) { %>
                <img class="cover-img" alt="Cover of <%= esc(sb.getTitle()) %>" loading="lazy"
                     src="book?action=image&name=<%= java.net.URLEncoder.encode(sb.getImage(), "UTF-8") %>">
                <% } else { %>
                <div class="cover-empty c<%= Math.abs(sb.getId().hashCode()) % 4 %>" aria-hidden="true"><%= esc(sb.getTitle()) %></div>
                <% } %>
                <p class="book-title"><%= esc(sb.getTitle()) %></p>
                <p class="book-author"><%= esc(sb.getAuthorName().isEmpty() ? sb.getGenre() : sb.getAuthorName()) %></p>
                <p class="book-price">Rs. <%= esc(sb.getPrice()) %></p>
                </a>
            </li>
            <% } %>
        </ul>
        <% } %>
        <h2 class="shelf-title">Popular books</h2>
        <p class="shelf-note">Sample titles and prices shown for preview.</p>
        <ul class="shelf-grid">
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/salt-road.jpg" alt="Cover of The Salt Road" loading="lazy"><p class="book-title">The Salt Road</p><p class="book-author">Jane Johnson</p><p class="book-price">Rs. 2,450</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/shuggie-bain.jpg" alt="Cover of Shuggie Bain" loading="lazy"><p class="book-title">Shuggie Bain</p><p class="book-author">Douglas Stuart</p><p class="book-price">Rs. 2,890</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/a-little-life.jpg" alt="Cover of A Little Life" loading="lazy"><p class="book-title">A Little Life</p><p class="book-author">Hanya Yanagihara</p><p class="book-price">Rs. 3,250</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/james.jpg" alt="Cover of James" loading="lazy"><p class="book-title">James</p><p class="book-author">Percival Everett</p><p class="book-price">Rs. 2,780</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/seven-sisters.jpg" alt="Cover of The Seven Sisters" loading="lazy"><p class="book-title">The Seven Sisters</p><p class="book-author">Lucinda Riley</p><p class="book-price">Rs. 2,590</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/atlas-six.jpg" alt="Cover of The Atlas Six" loading="lazy"><p class="book-title">The Atlas Six</p><p class="book-author">Olivie Blake</p><p class="book-price">Rs. 2,990</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/before-the-coffee-gets-cold.jpg" alt="Cover of Before the Coffee Gets Cold" loading="lazy"><p class="book-title">Before the Coffee Gets Cold</p><p class="book-author">Toshikazu Kawaguchi</p><p class="book-price">Rs. 2,150</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/this-is-going-to-hurt.jpg" alt="Cover of This Is Going to Hurt" loading="lazy"><p class="book-title">This Is Going to Hurt</p><p class="book-author">Adam Kay</p><p class="book-price">Rs. 2,350</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/dead-simple.jpg" alt="Cover of Dead Simple" loading="lazy"><p class="book-title">Dead Simple</p><p class="book-author">Peter James</p><p class="book-price">Rs. 2,250</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/children-of-strife.jpg" alt="Cover of Children of Strife" loading="lazy"><p class="book-title">Children of Strife</p><p class="book-author">Adrian Tchaikovsky</p><p class="book-price">Rs. 3,100</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/queen-of-our-times.jpg" alt="Cover of Queen of Our Times" loading="lazy"><p class="book-title">Queen of Our Times</p><p class="book-author">Robert Hardman</p><p class="book-price">Rs. 3,400</p></li>
            </ul>
        <h2 class="shelf-title">Sherlock Holmes collection</h2>
        <p class="shelf-note">The complete set by Sir Arthur Conan Doyle.</p>
        <ul class="shelf-grid">
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/study-in-scarlet.jpg" alt="Cover of A Study in Scarlet" loading="lazy"><p class="book-title">A Study in Scarlet</p><p class="book-author">Sir Arthur Conan Doyle</p><p class="book-price">Rs. 1,450</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/sign-of-four.jpg" alt="Cover of The Sign of Four" loading="lazy"><p class="book-title">The Sign of Four</p><p class="book-author">Sir Arthur Conan Doyle</p><p class="book-price">Rs. 1,450</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/hound-of-the-baskervilles.jpg" alt="Cover of The Hound of the Baskervilles" loading="lazy"><p class="book-title">The Hound of the Baskervilles</p><p class="book-author">Sir Arthur Conan Doyle</p><p class="book-price">Rs. 1,650</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/valley-of-fear.jpg" alt="Cover of The Valley of Fear" loading="lazy"><p class="book-title">The Valley of Fear</p><p class="book-author">Sir Arthur Conan Doyle</p><p class="book-price">Rs. 1,550</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/adventures-of-sherlock-holmes.jpg" alt="Cover of The Adventures of Sherlock Holmes" loading="lazy"><p class="book-title">The Adventures of Sherlock Holmes</p><p class="book-author">Sir Arthur Conan Doyle</p><p class="book-price">Rs. 1,750</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/memoirs-of-sherlock-holmes.jpg" alt="Cover of The Memoirs of Sherlock Holmes" loading="lazy"><p class="book-title">The Memoirs of Sherlock Holmes</p><p class="book-author">Sir Arthur Conan Doyle</p><p class="book-price">Rs. 1,850</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/return-of-sherlock-holmes.jpg" alt="Cover of The Return of Sherlock Holmes" loading="lazy"><p class="book-title">The Return of Sherlock Holmes</p><p class="book-author">Sir Arthur Conan Doyle</p><p class="book-price">Rs. 1,750</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/his-last-bow.jpg" alt="Cover of His Last Bow" loading="lazy"><p class="book-title">His Last Bow</p><p class="book-author">Sir Arthur Conan Doyle</p><p class="book-price">Rs. 1,550</p></li>
                <li class="book"><img class="cover-img" src="<%= request.getContextPath() %>/images/books/case-book-of-sherlock-holmes.jpg" alt="Cover of The Case-Book of Sherlock Holmes" loading="lazy"><p class="book-title">The Case-Book of Sherlock Holmes</p><p class="book-author">Sir Arthur Conan Doyle</p><p class="book-price">Rs. 1,650</p></li>
            </ul>
    </section>

    <div class="panel">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold"><i class="fa-solid fa-book me-2"></i>Book Management</h2>
                <p class="text-light opacity-75 mb-0">Manage the bookstore catalog</p>
            </div>
            <div>
                <a href="index.jsp" class="btn btn-outline-light me-2"><i class="fa-solid fa-house me-1"></i>Home</a>
                <% if (canEdit) { %>
                <a href="book?action=new" class="btn btn-primary"><i class="fa-solid fa-plus me-2"></i>Add New Book</a>
                <% } %>
            </div>
        </div>

        <form method="get" action="book" class="d-flex mb-3">
            <% String q = (String) request.getAttribute("query"); %>
            <input type="text" name="q" class="form-control me-2" placeholder="Search by title, author, genre or ID"
                   value="<%= esc(q) %>">
            <button type="submit" class="btn btn-primary me-2"><i class="fa-solid fa-magnifying-glass"></i></button>
            <% if (q != null && !q.isEmpty()) { %><a href="book" class="btn btn-outline-light">Clear</a><% } %>
        </form>

        <% String msg = (String) request.getAttribute("msg");
           if (msg != null && !msg.isEmpty()) { %>
          <div class="alert alert-info py-2"><%= esc(msg) %></div>
        <% } %>

        <div class="table-responsive">
            <table class="table table-dark-glass table-hover rounded overflow-hidden">
                <thead>
                <tr>
                    <th>#</th><th>Cover</th><th>Book ID</th><th>Title</th><th>Author</th><th>Type</th><th>Genre</th><th>Price</th>
                    <% if (canEdit) { %><th class="text-center">Actions</th><% } %>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Book> books = (List<Book>) request.getAttribute("books");
                    int n = 1;
                    if (books != null && !books.isEmpty()) {
                        for (Book b : books) {
                %>
                <tr>
                    <td><%= String.format("%02d", n++) %></td>
                    <td>
                        <% if (b.hasImage()) { %>
                        <img class="thumb" alt="Cover of <%= esc(b.getTitle()) %>"
                             src="book?action=image&name=<%= java.net.URLEncoder.encode(b.getImage(), "UTF-8") %>">
                        <% } else { %>
                        <span class="thumb thumb-empty"><i class="fa-solid fa-book"></i></span>
                        <% } %>
                    </td>
                    <td><%= esc(b.getId()) %></td>
                    <td><a href="book?action=view&id=<%= java.net.URLEncoder.encode(b.getId(), "UTF-8") %>"
                           style="color:inherit;font-weight:600;"><%= esc(b.getTitle()) %></a></td>
                    <td>
                        <%= esc(b.getAuthorName().isEmpty() ? "Unassigned" : b.getAuthorName()) %>
                        <% if (canEdit && b.isAuthorUnresolved()) { %>
                            <span class="badge bg-warning text-dark ms-1" title="Legacy unlinked author">Unlinked</span>
                        <% } %>
                    </td>
                    <td><%= esc(b.getTypeLabel()) %></td>
                    <td><%= esc(b.getGenre()) %></td>
                    <td><%= esc(b.getPrice()) %></td>
                    <% if (canEdit) { %>
                    <td class="text-center">
                        <a class="btn btn-sm btn-info btn-action me-1"
                           href="book?action=view&id=<%= java.net.URLEncoder.encode(b.getId(), "UTF-8") %>" title="View details">
                            <i class="fa-solid fa-eye"></i></a>
                        <a class="btn btn-sm btn-warning btn-action me-1"
                           href="book?action=edit&id=<%= java.net.URLEncoder.encode(b.getId(), "UTF-8") %>" title="Edit book">
                            <i class="fa-solid fa-pen-to-square"></i></a>
                        <form method="post" action="book" style="display:inline"
                              onsubmit="return confirm('Delete this book?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= esc(b.getId()) %>">
                            <button type="submit" class="btn btn-sm btn-danger btn-action" title="Delete book"><i class="fa-solid fa-trash"></i></button>
                        </form>
                    </td>
                    <% } %>
                </tr>
                <%      }
                    } else { %>
                <tr><td colspan="<%= canEdit ? 9 : 8 %>" class="text-center opacity-75">No books yet.</td></tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
