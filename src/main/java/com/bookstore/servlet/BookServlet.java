package com.bookstore.servlet;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import com.bookstore.util.DataPaths;
import com.bookstore.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@WebServlet("/book")
@MultipartConfig(maxFileSize = 3 * 1024 * 1024, maxRequestSize = 4 * 1024 * 1024)
public class BookServlet extends HttpServlet {
    private static final List<String> IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    private BookDAO dao;
    private File imageDir;

    @Override
    public void init() {
        // Using the updated default constructor
        dao = new BookDAO();
        // uploaded covers are kept in the data folder (next to books.txt), so redeploying never wipes them
        imageDir = new File(DataPaths.dataDir(), "book-images");
        if (!imageDir.exists()) imageDir.mkdirs();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        req.setAttribute("msg", req.getParameter("msg"));

        // customers can browse and see covers; add/edit pages are Admin only
        if (("new".equals(action) || "edit".equals(action)) && !SessionUtil.isAdmin(req)) {
            resp.sendRedirect("book?msg=Access+denied");
            return;
        }

        switch (action) {
            case "image":
                serveImage(req, resp);
                break;
            case "new":
                req.getRequestDispatcher("/book-form.jsp").forward(req, resp);
                break;
            case "edit":
                Book b = dao.findById(req.getParameter("id"));
                if (b == null) {
                    resp.sendRedirect("book?msg=Book+not+found");
                    return;
                }
                req.setAttribute("book", b);
                req.getRequestDispatcher("/book-form.jsp").forward(req, resp);
                break;
            default:
                req.setAttribute("books", dao.findAll());
                req.getRequestDispatcher("/book-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        // add / update / delete are Admin only
        if (!SessionUtil.isAdmin(req)) {
            resp.sendRedirect("book?msg=Access+denied");
            return;
        }

        boolean multipart = req.getContentType() != null
                && req.getContentType().toLowerCase(Locale.ROOT).startsWith("multipart/");
        if (multipart) {
            try {
                req.getParts();   // parse the upload now, so a too-big file is caught here
            } catch (IllegalStateException e) {
                resp.sendRedirect("book?msg=Image+is+too+large+%28max+3+MB%29");
                return;
            }
        }

        String action = req.getParameter("action");
        String id = trim(req.getParameter("id"));
        String title = trim(req.getParameter("title"));
        String genre = trim(req.getParameter("genre"));
        String price = trim(req.getParameter("price"));

        switch (action == null ? "" : action) {
            case "add":
            case "update": {
                boolean isUpdate = "update".equals(action);
                String back = isUpdate ? "book?action=edit&id=" + enc(id) : "book?action=new";

                if (id.isEmpty() || title.isEmpty() || genre.isEmpty() || price.isEmpty()) {
                    resp.sendRedirect(back + "&msg=All+fields+are+required");
                    return;
                }
                if (id.contains(",") || title.contains(",") || genre.contains(",") || price.contains(",")) {
                    resp.sendRedirect(back + "&msg=Commas+are+not+allowed");
                    return;
                }
                try {
                    if (Double.parseDouble(price) < 0) {
                        resp.sendRedirect(back + "&msg=Price+cannot+be+negative");
                        return;
                    }
                } catch (NumberFormatException e) {
                    resp.sendRedirect(back + "&msg=Price+must+be+a+number");
                    return;
                }

                Book existing = dao.findById(id);
                if (!isUpdate && existing != null) {
                    resp.sendRedirect(back + "&msg=Book+ID+already+exists");
                    return;
                }

                // keep the current cover unless a new one was chosen
                String oldImage = existing != null ? existing.getImage() : "";
                String image = oldImage;
                Part part = multipart ? req.getPart("image") : null;
                if (part != null && part.getSize() > 0) {
                    String saved = saveImage(part, id);
                    if (saved == null) {
                        resp.sendRedirect(back + "&msg=Image+must+be+a+JPG%2C+PNG%2C+WebP+or+GIF");
                        return;
                    }
                    image = saved;
                }
                boolean newImageSaved = !image.equals(oldImage);

                Book book = new Book(id, title, genre, price, image);
                if (isUpdate) {
                    boolean ok = dao.update(book);
                    if (newImageSaved) deleteImage(ok ? oldImage : image);
                    resp.sendRedirect("book?msg=" + (ok ? "Book+updated" : "Book+not+found"));
                } else {
                    boolean ok = dao.add(book);
                    if (!ok && newImageSaved) deleteImage(image);
                    resp.sendRedirect(ok ? "book?msg=Book+added" : back + "&msg=Book+ID+already+exists");
                }
                break;
            }
            case "delete": {
                Book toDelete = dao.findById(id);
                boolean deleted = dao.delete(id);
                if (deleted && toDelete != null) deleteImage(toDelete.getImage());
                resp.sendRedirect("book?msg=" + (deleted ? "Book+deleted" : "Book+not+found"));
                break;
            }
            default:
                resp.sendRedirect("book");
        }
    }

    // ---- cover image helpers ----

    // streams a saved cover to the browser: book?action=image&name=<file>
    private void serveImage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        if (name == null || name.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String safe = new File(name).getName();        // no folder paths allowed
        String ext = extensionOf(safe);
        File f = new File(imageDir, safe);
        if (!IMAGE_TYPES.contains(ext) || !f.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        resp.setContentType(mimeFor(ext));
        resp.setContentLength((int) f.length());
        resp.setHeader("Cache-Control", "public, max-age=86400");
        Files.copy(f.toPath(), resp.getOutputStream());
    }

    // saves the upload as <bookId>-<time>.<ext>; returns the file name, or null if it is not an image
    private String saveImage(Part part, String bookId) throws IOException {
        String submitted = fileNameOf(part);
        if (submitted == null) return null;
        String ext = extensionOf(new File(submitted).getName());
        String type = part.getContentType();
        if (!IMAGE_TYPES.contains(ext) || type == null
                || !type.toLowerCase(Locale.ROOT).startsWith("image/")) {
            return null;
        }
        String fileName = bookId.replaceAll("[^A-Za-z0-9_-]", "_") + "-" + System.currentTimeMillis() + "." + ext;
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, new File(imageDir, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }

    private void deleteImage(String name) {
        if (name == null || name.isEmpty()) return;
        new File(imageDir, new File(name).getName()).delete();
    }

    private String fileNameOf(Part part) {
        String cd = part.getHeader("content-disposition");
        if (cd == null) return null;
        for (String piece : cd.split(";")) {
            piece = piece.trim();
            if (piece.startsWith("filename")) {
                return piece.substring(piece.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private String extensionOf(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? "" : fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private String mimeFor(String ext) {
        switch (ext) {
            case "png":  return "image/png";
            case "gif":  return "image/gif";
            case "webp": return "image/webp";
            default:     return "image/jpeg";
        }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String enc(String s) throws IOException {
        return URLEncoder.encode(s, "UTF-8");
    }
}