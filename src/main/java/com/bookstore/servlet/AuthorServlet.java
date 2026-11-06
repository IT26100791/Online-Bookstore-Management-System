package com.bookstore.servlet;

import com.bookstore.dao.AuthorDAO;
import com.bookstore.model.Author;
import com.bookstore.util.DataPaths;
import com.bookstore.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@WebServlet("/author")
@MultipartConfig(maxFileSize = 3 * 1024 * 1024, maxRequestSize = 4 * 1024 * 1024)
public class AuthorServlet extends HttpServlet {
    private static final List<String> IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final int MAX_BIO = 1000;

    private AuthorDAO dao;
    private File imageDir;   // author photos live next to authors.txt, so redeploying never wipes them

    @Override
    public void init() {
        // Using the updated default constructor
        dao = new AuthorDAO();
        imageDir = new File(DataPaths.dataDir(), "author-images");
        if (!imageDir.exists()) imageDir.mkdirs();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        req.setAttribute("msg", req.getParameter("msg"));

        // customers can only view the list; add/edit pages are Admin only
        if (("new".equals(action) || "edit".equals(action)) && !SessionUtil.isAdmin(req)) {
            resp.sendRedirect("author?msg=Access+denied");
            return;
        }

        switch (action) {
            case "photo":
                servePhoto(req, resp);
                break;
            case "new":
                req.getRequestDispatcher("/author-form.jsp").forward(req, resp);
                break;
            case "edit":
                Author a = dao.findById(req.getParameter("id"));
                if (a == null) {
                    resp.sendRedirect("author?msg=Author+not+found");
                    return;
                }
                req.setAttribute("author", a);
                req.getRequestDispatcher("/author-form.jsp").forward(req, resp);
                break;
            case "cards":
                // card view of the authors (same data as the table list)
                req.setAttribute("authors", dao.findAll());
                req.getRequestDispatcher("/author-cards.jsp").forward(req, resp);
                break;
            default:
                req.setAttribute("authors", dao.findAll());
                req.getRequestDispatcher("/author-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        // add / update / delete are Admin only
        if (!SessionUtil.isAdmin(req)) {
            resp.sendRedirect("author?msg=Access+denied");
            return;
        }

        boolean multipart = req.getContentType() != null
                && req.getContentType().toLowerCase(Locale.ROOT).startsWith("multipart/");
        if (multipart) {
            try {
                req.getParts();   // parse the upload now, so a too-big photo is caught here
            } catch (IllegalStateException e) {
                resp.sendRedirect("author?msg=Photo+is+too+large+%28max+3+MB%29");
                return;
            }
        }

        String action = req.getParameter("action");
        String id = trim(req.getParameter("id"));
        String name = trim(req.getParameter("name"));
        String email = trim(req.getParameter("email"));          // optional
        String bio = trim(req.getParameter("bio"));              // optional
        boolean removePhoto = "1".equals(req.getParameter("removePhoto"));
        String type = "International".equalsIgnoreCase(trim(req.getParameter("authorType")))
                ? "International" : "Local";

        switch (action == null ? "" : action) {
            case "add":
            case "update": {
                boolean isUpdate = "update".equals(action);
                String back = isUpdate ? "author?action=edit&id=" + enc(id) : "author?action=new";

                if (id.isEmpty() || name.isEmpty()) {
                    resp.sendRedirect(back + "&msg=Author+ID+and+name+are+required");
                    return;
                }
                if (id.contains(",") || name.contains(",") || email.contains(",")) {
                    resp.sendRedirect(back + "&msg=Commas+are+not+allowed+in+ID%2C+name+or+email");
                    return;
                }
                if (!email.isEmpty() && (!email.contains("@") || email.contains(" "))) {
                    resp.sendRedirect(back + "&msg=Email+is+not+valid");
                    return;
                }
                if (bio.length() > MAX_BIO) {
                    resp.sendRedirect(back + "&msg=Description+is+too+long+%28max+1000+characters%29");
                    return;
                }

                Author existing = dao.findById(id);
                if (!isUpdate && existing != null) {
                    resp.sendRedirect(back + "&msg=Author+ID+already+exists");
                    return;
                }

                // keep the current photo unless a new one was chosen or "remove" was ticked
                String oldPhoto = existing != null ? existing.getPhoto() : "";
                String photo = removePhoto ? "" : oldPhoto;
                Part part = multipart ? req.getPart("photo") : null;
                if (part != null && part.getSize() > 0) {
                    String saved = savePhoto(part, id);
                    if (saved == null) {
                        resp.sendRedirect(back + "&msg=Photo+must+be+a+JPG%2C+PNG%2C+WebP+or+GIF+image");
                        return;
                    }
                    photo = saved;
                }
                boolean photoChanged = !photo.equals(oldPhoto);

                Author author = dao.createAuthor(id, name, email, type, photo, bio);
                if (isUpdate) {
                    boolean ok = dao.update(author);
                    if (photoChanged) deleteImage(ok ? oldPhoto : photo);
                    resp.sendRedirect("author?msg=" + (ok ? "Author+updated" : "Author+not+found"));
                } else {
                    boolean ok = dao.add(author);
                    if (!ok && photoChanged) deleteImage(photo);
                    resp.sendRedirect(ok ? "author?msg=Author+added" : back + "&msg=Author+ID+already+exists");
                }
                break;
            }
            case "delete": {
                Author toDelete = dao.findById(id);
                boolean deleted = dao.delete(id);
                if (deleted && toDelete != null) deleteImage(toDelete.getPhoto());
                resp.sendRedirect("author?msg=" + (deleted ? "Author+deleted" : "Author+not+found"));
                break;
            }
            default:
                resp.sendRedirect("author");
        }
    }

    // ---- photo helpers ----

    // streams a saved photo to the browser: author?action=photo&name=<file>
    private void servePhoto(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Cache-Control", "public, max-age=86400");
        Files.copy(f.toPath(), resp.getOutputStream());
    }

    // saves the upload as <authorId>-<time>.<ext>; returns the file name, or null if it is not a real image
    private String savePhoto(Part part, String authorId) throws IOException {
        String submitted = fileNameOf(part);
        if (submitted == null) return null;
        String ext = extensionOf(new File(submitted).getName());
        String type = part.getContentType();
        if (!IMAGE_TYPES.contains(ext) || type == null
                || !type.toLowerCase(Locale.ROOT).startsWith("image/")) {
            return null;
        }
        String fileName = authorId.replaceAll("[^A-Za-z0-9_-]", "_") + "-" + System.currentTimeMillis() + "." + ext;
        File target = new File(imageDir, fileName);
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        if (!looksLikeImage(target)) {      // the file content must really be an image
            target.delete();
            return null;
        }
        return fileName;
    }

    // checks the first bytes of the file: JPEG, PNG, GIF or WebP
    private boolean looksLikeImage(File f) throws IOException {
        byte[] b = new byte[12];
        try (RandomAccessFile r = new RandomAccessFile(f, "r")) {
            if (r.read(b) < 12) return false;
        }
        boolean jpeg = (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
        boolean png = (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G';
        boolean gif = b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8';
        boolean webp = b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
        return jpeg || png || gif || webp;
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