package com.bookstore.servlet;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;
import com.bookstore.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The ONE servlet of the user module. URLs:
 *   /login /logout            login, logout
 *   /user                     register (Create), list + search (Read), edit (Update), delete (Delete)  [admin]
 *   /profile                  every user manages their own details, photo and password
 *   /forgot-password          reset a forgotten password with the security question
 *   /avatar?u=name            sends a profile photo (logged-in users only)
 * Multipart is on only so the forms can upload a profile photo.
 */
@WebServlet(urlPatterns = {"/user", "/login", "/logout", "/profile", "/forgot-password", "/avatar"})
@MultipartConfig(fileSizeThreshold = 256 * 1024,
        maxFileSize = UserDAO.MAX_AVATAR_BYTES,
        maxRequestSize = UserDAO.MAX_AVATAR_BYTES + 64 * 1024)
public class UserServlet extends HttpServlet {
    public static final String USER_ATTR = "loggedUser";
    public static final int MIN_PASSWORD = 8;
    public static final String[] QUESTIONS = {
            "What was the name of your first pet?",
            "What is the name of your primary school?",
            "In which town were you born?",
            "What is your favourite book?",
            "What was your childhood nickname?",
            "What is your mother's first name?"
    };

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.-]{3,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final String RESET_ATTR = "resetUser";
    private static final SecureRandom RANDOM = new SecureRandom();

    private UserDAO dao;

    @Override
    public void init() throws ServletException {
        super.init();
        dao = new UserDAO();   // uses DataPaths.dataDir()
    }

    // =====================================================================
    //  GET
    // =====================================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        String action = req.getParameter("action");
        User current = currentUser(req);

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            redirect(resp, "login.jsp", "Logged out successfully", true);
            return;
        }
        if ("/login".equals(path)) {
            resp.sendRedirect(current != null ? current.getHomePage() : "login.jsp");
            return;
        }
        if ("/avatar".equals(path)) {
            sendAvatar(req, resp, current);
            return;
        }
        if ("/forgot-password".equals(path)) {
            showForgotPassword(req, resp);
            return;
        }
        if ("/profile".equals(path)) {
            if (current == null) {
                redirect(resp, "login.jsp", "Please login first", false);
                return;
            }
            req.setAttribute("me", current);
            req.getRequestDispatcher("/profile.jsp").forward(req, resp);
            return;
        }

        // ----- /user -----
        if ("new".equals(action)) {
            if (current != null && !current.canManageUsers()) {      // customers cannot open sign-up again
                resp.sendRedirect(current.getHomePage());
                return;
            }
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        // everything below is user management: Admin only
        // (AccessFilter also blocks this, this is a second safety check)
        if (current == null || !current.canManageUsers()) {
            redirect(resp, "login.jsp", "Admin access required", false);
            return;
        }

        if ("edit".equals(action)) {
            User u = dao.findByUsername(req.getParameter("username"));
            if (u == null) {
                redirect(resp, "user", "Username not found", false);
                return;
            }
            req.setAttribute("user", u);
            req.getRequestDispatcher("/edit.jsp").forward(req, resp);
            return;
        }

        // list + search (Read)
        String q = req.getParameter("q");
        List<User> all = dao.findAll();
        req.setAttribute("users", (q == null || q.trim().isEmpty()) ? all : dao.search(q));
        req.setAttribute("totalUsers", all.size());
        req.setAttribute("totalAdmins", dao.countAdmins());
        req.setAttribute("query", q == null ? "" : q.trim());
        req.getRequestDispatcher("/users-list.jsp").forward(req, resp);
    }

    // =====================================================================
    //  POST
    // =====================================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            route(req, resp);
        } catch (IllegalStateException tooBig) {      // the container rejected an upload over the size limit
            User me = currentUser(req);
            redirect(resp, me == null ? "register.jsp" : me.getHomePage(), "Photo must be 2 MB or smaller", false);
        }
    }

    private void route(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/login".equals(path)) {
            login(req, resp);
            return;
        }

        User current = currentUser(req);
        if (!csrfValid(req)) {
            redirect(resp, current == null ? "register.jsp" : "user", "Your session expired. Please try again", false);
            return;
        }
        if ("/forgot-password".equals(path)) {
            resetPassword(req, resp);
            return;
        }
        if ("/profile".equals(path)) {
            profile(req, resp, current);
            return;
        }

        // ----- /user -----
        String action = req.getParameter("action");
        if ("add".equals(action)) {
            add(req, resp, current);
            return;
        }
        // update / delete: Admin only
        if (current == null || !current.canManageUsers()) {
            redirect(resp, "login.jsp", "Admin access required", false);
            return;
        }
        User target = dao.findByUsername(req.getParameter("username"));
        if (target == null) {
            redirect(resp, "user", "Username not found", false);
            return;
        }
        if ("update".equals(action)) {
            update(req, resp, target);
        } else if ("delete".equals(action)) {
            delete(resp, current, target);
        } else {
            resp.sendRedirect("user");
        }
    }

    // =====================================================================
    //  LOGIN
    // =====================================================================
    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = trim(req.getParameter("username"));
        String pass = req.getParameter("password") == null ? "" : req.getParameter("password");
        String key = "login|" + req.getRemoteAddr() + "|" + username.toLowerCase(Locale.ROOT);

        if (isLocked(key)) {
            redirect(resp, "login.jsp", "Too many failed attempts. Try again in " + minutesLeft(key) + " minute(s)", false);
            return;
        }
        User u = dao.authenticate(username, pass);
        if (u == null) {
            int left = fail(key);
            redirect(resp, "login.jsp", "Invalid username or password"
                    + (left > 0 && left < 3 ? " (" + left + " attempts left)" : ""), false);
            return;
        }
        clearFails(key);
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();                  // avoid session fixation
        req.getSession(true).setAttribute(USER_ATTR, u);
        resp.sendRedirect(u.getHomePage());                 // polymorphism: Admin -> user, Customer -> welcome.jsp
    }

    // =====================================================================
    //  CREATE (register / admin adds a user)
    // =====================================================================
    private void add(HttpServletRequest req, HttpServletResponse resp, User current)
            throws ServletException, IOException {
        boolean adminCreating = current != null && current.canManageUsers();
        String back = "register.jsp";

        String username = trim(req.getParameter("username"));
        String pass = req.getParameter("password");
        String email = trim(req.getParameter("email"));
        String answer = req.getParameter("answer");
        int question = parseInt(req.getParameter("question"));

        String err = checkUsername(username);
        if (err == null) err = checkNewPassword(pass, req.getParameter("confirm"));
        if (err == null) err = checkEmail(email);
        if (err == null && (question < 0 || question >= QUESTIONS.length)) err = "Please choose a security question";
        if (err == null) err = checkAnswer(answer);
        if (err == null && dao.findByUsername(username) != null) err = "Username already exists";
        if (err != null) {
            redirect(resp, back, err, false);
            return;
        }

        String photo;
        try {
            photo = readPhoto(req);
        } catch (IllegalArgumentException e) {
            redirect(resp, back, e.getMessage(), false);
            return;
        }

        // only an Admin can create another Admin; public sign-ups are Customers
        String role = (adminCreating && "Admin".equalsIgnoreCase(req.getParameter("role"))) ? "Admin" : "Customer";
        User user = UserDAO.createUser(username, PasswordUtil.hash(pass), role);
        user.setEmail(email);
        user.setAvatarFile(photo);
        user.setSecurity(question, PasswordUtil.hash(normalizeAnswer(answer)));

        if (!dao.add(user)) {
            dao.deleteAvatar(photo);
            redirect(resp, back, "Username already exists", false);
        } else if (adminCreating) {
            redirect(resp, "user", "User added successfully", true);
        } else {
            redirect(resp, "login.jsp", "Account created. Please login", true);
        }
    }

    // =====================================================================
    //  UPDATE (admin edits any user)
    // =====================================================================
    private void update(HttpServletRequest req, HttpServletResponse resp, User target)
            throws ServletException, IOException {
        String back = "user?action=edit&username=" + enc(target.getUsername());
        String newRole = "Admin".equalsIgnoreCase(req.getParameter("role")) ? "Admin" : "Customer";

        if (target.canManageUsers() && "Customer".equals(newRole) && dao.countAdmins() <= 1) {
            redirect(resp, "user", "Cannot demote the last admin", false);
            return;
        }

        String pass = req.getParameter("password");
        boolean changePassword = pass != null && !pass.isEmpty();
        String email = trim(req.getParameter("email"));
        String err = changePassword ? checkNewPassword(pass, req.getParameter("confirm")) : null;
        if (err == null) err = checkEmail(email);
        if (err != null) {
            redirect(resp, back, err, false);
            return;
        }

        String newPhoto;
        try {
            newPhoto = readPhoto(req);
        } catch (IllegalArgumentException e) {
            redirect(resp, back, e.getMessage(), false);
            return;
        }

        User updated = UserDAO.withRole(target, newRole);          // new object of the right subclass
        if (changePassword) updated.setPasswordHash(PasswordUtil.hash(pass));
        updated.setEmail(email);
        if (newPhoto != null) {
            dao.deleteAvatar(target.getAvatarFile());
            updated.setAvatarFile(newPhoto);
        } else if ("1".equals(req.getParameter("removePhoto"))) {
            dao.deleteAvatar(target.getAvatarFile());
            updated.setAvatarFile("");
        }
        dao.update(updated);
        redirect(resp, "user", "User updated successfully", true);
    }

    // =====================================================================
    //  DELETE
    // =====================================================================
    private void delete(HttpServletResponse resp, User current, User target) throws IOException {
        if (target.equals(current)) {
            redirect(resp, "user", "You cannot delete your own account", false);
            return;
        }
        if (target.canManageUsers() && dao.countAdmins() <= 1) {
            redirect(resp, "user", "Cannot delete the last admin", false);
            return;
        }
        dao.delete(target.getUsername());
        dao.deleteAvatar(target.getAvatarFile());
        redirect(resp, "user", "User deleted successfully", true);
    }

    // =====================================================================
    //  MY PROFILE (any logged-in user, only their own account)
    // =====================================================================
    private void profile(HttpServletRequest req, HttpServletResponse resp, User me)
            throws ServletException, IOException {
        if (me == null) {
            redirect(resp, "login.jsp", "Please login first", false);
            return;
        }
        String action = req.getParameter("action");
        String err = null;
        String ok = null;

        if ("info".equals(action)) {
            String email = trim(req.getParameter("email"));
            err = checkEmail(email);
            if (err == null) {
                me.setEmail(email);
                dao.update(me);
                ok = "Profile updated successfully";
            }

        } else if ("photo".equals(action)) {
            if ("1".equals(req.getParameter("removePhoto"))) {
                dao.deleteAvatar(me.getAvatarFile());
                me.setAvatarFile("");
                dao.update(me);
                ok = "Profile photo removed";
            } else {
                String saved = null;
                try {
                    saved = readPhoto(req);
                } catch (IllegalArgumentException e) {
                    err = e.getMessage();
                }
                if (err == null && saved == null) err = "Please choose a photo first";
                if (err == null) {
                    dao.deleteAvatar(me.getAvatarFile());
                    me.setAvatarFile(saved);
                    dao.update(me);
                    ok = "Profile photo updated";
                }
            }

        } else if ("password".equals(action)) {
            String pass = req.getParameter("password");
            if (!currentPasswordOk(req, me)) err = "Current password is incorrect";
            else err = checkNewPassword(pass, req.getParameter("confirm"));
            if (err == null) {
                me.setPasswordHash(PasswordUtil.hash(pass));
                dao.update(me);
                ok = "Password changed successfully";
            }

        } else if ("security".equals(action)) {
            int q = parseInt(req.getParameter("question"));
            String answer = req.getParameter("answer");
            if (!currentPasswordOk(req, me)) err = "Current password is incorrect";
            else if (q < 0 || q >= QUESTIONS.length) err = "Please choose a security question";
            else err = checkAnswer(answer);
            if (err == null) {
                me.setSecurity(q, PasswordUtil.hash(normalizeAnswer(answer)));
                dao.update(me);
                ok = "Security question saved";
            }
        } else {
            resp.sendRedirect("profile");
            return;
        }
        redirect(resp, "profile", err != null ? err : ok, err == null);
    }

    /** Asks for the current password again, with the same guess limit as the login. */
    private boolean currentPasswordOk(HttpServletRequest req, User me) {
        String key = "chg|" + req.getRemoteAddr() + "|" + me.getUsername().toLowerCase(Locale.ROOT);
        if (isLocked(key)) return false;
        if (me.checkPassword(req.getParameter("current"))) {
            clearFails(key);
            return true;
        }
        fail(key);
        return false;
    }

    // =====================================================================
    //  FORGOT PASSWORD  (1: username -> question   2: answer + new password)
    //  Wrong answers are limited, and unknown usernames get a fake question
    //  so nobody can find out which usernames exist.
    // =====================================================================
    private void showForgotPassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (req.getParameter("restart") != null) session.removeAttribute(RESET_ATTR);

        String name = (String) session.getAttribute(RESET_ATTR);
        if (name == null) {
            req.setAttribute("resetStep", "lookup");
        } else {
            User u = dao.findByUsername(name);
            req.setAttribute("resetStep", "answer");
            req.setAttribute("resetUsername", name);
            req.setAttribute("resetQuestion", (u != null && u.hasSecurityQuestion())
                    ? QUESTIONS[u.getSecurityQuestion()]
                    : QUESTIONS[Math.floorMod(name.toLowerCase(Locale.ROOT).hashCode(), QUESTIONS.length)]);
        }
        req.getRequestDispatcher("/forgot-password.jsp").forward(req, resp);
    }

    private void resetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(true);
        String step = req.getParameter("step");

        if ("lookup".equals(step)) {
            String name = trim(req.getParameter("username"));
            if (name.isEmpty()) {
                redirect(resp, "forgot-password", "Please enter your username", false);
                return;
            }
            session.setAttribute(RESET_ATTR, name);
            resp.sendRedirect("forgot-password");
            return;
        }

        String name = (String) session.getAttribute(RESET_ATTR);
        if (!"reset".equals(step) || name == null) {
            resp.sendRedirect("forgot-password");
            return;
        }
        String key = "reset|" + req.getRemoteAddr() + "|" + name.toLowerCase(Locale.ROOT);
        if (isLocked(key)) {
            redirect(resp, "forgot-password", "Too many wrong answers. Try again in " + minutesLeft(key)
                    + " minute(s) or ask an admin to reset your password", false);
            return;
        }
        String pass = req.getParameter("password");
        String err = checkNewPassword(pass, req.getParameter("confirm"));   // typing mistakes do not count as attempts
        if (err != null) {
            redirect(resp, "forgot-password", err, false);
            return;
        }
        User u = dao.findByUsername(name);
        if (u == null || !u.checkSecurityAnswer(normalizeAnswer(req.getParameter("answer")))) {
            int left = fail(key);
            redirect(resp, "forgot-password", "The answer is not correct, or this account has no security question ("
                    + left + " attempts left). An admin can reset your password if you cannot recover it", false);
            return;
        }
        clearFails(key);
        u.setPasswordHash(PasswordUtil.hash(pass));
        dao.update(u);
        session.removeAttribute(RESET_ATTR);
        redirect(resp, "login.jsp", "Password changed. You can login now", true);
    }

    // =====================================================================
    //  AVATAR (the photos are outside the web folder, so they are only reachable here)
    // =====================================================================
    private void sendAvatar(HttpServletRequest req, HttpServletResponse resp, User current) throws IOException {
        if (current == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        User u = dao.findByUsername(req.getParameter("u"));
        Path file = (u != null && u.hasAvatar()) ? dao.findAvatar(u.getAvatarFile()) : null;
        if (file == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        resp.setContentType(UserDAO.avatarContentType(u.getAvatarFile()));
        resp.setContentLengthLong(Files.size(file));
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Cache-Control", "private, max-age=86400");   // page URLs carry &v=<file name>, new photo = new URL
        Files.copy(file, resp.getOutputStream());
    }

    // =====================================================================
    //  helpers
    // =====================================================================
    /** Logged-in user, re-read from users.txt on every request (role changes/deletes apply at once). */
    private User currentUser(HttpServletRequest req) throws IOException {
        HttpSession s = req.getSession(false);
        Object o = (s == null) ? null : s.getAttribute(USER_ATTR);
        if (!(o instanceof User)) return null;
        User fresh = dao.findByUsername(((User) o).getUsername());
        if (fresh == null) {
            s.invalidate();
            return null;
        }
        s.setAttribute(USER_ATTR, fresh);
        return fresh;
    }

    /** Saves the uploaded "photo" part (only if the form is multipart and a file was chosen). */
    private String readPhoto(HttpServletRequest req) throws IOException, ServletException {
        String type = req.getContentType();
        if (type == null || !type.toLowerCase(Locale.ROOT).startsWith("multipart/")) return null;
        Part part = req.getPart("photo");
        if (part == null || part.getSize() == 0) return null;
        return dao.saveAvatar(part.getInputStream(), part.getSize());
    }

    /** Redirect with a message: page?msg=...&t=ok|err (the JSP pages read these two parameters). */
    private static void redirect(HttpServletResponse resp, String page, String msg, boolean ok) throws IOException {
        resp.sendRedirect(page + (page.contains("?") ? "&" : "?") + "msg=" + enc(msg) + "&t=" + (ok ? "ok" : "err"));
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s == null ? "" : s.trim()); } catch (NumberFormatException e) { return -1; }
    }

    // ---------- server side validation: each returns an error message or null ----------
    private static String checkUsername(String u) {
        return (u == null || !USERNAME.matcher(u).matches())
                ? "Username must be 3-30 characters: letters, numbers, dot, dash or underscore" : null;
    }

    private static String checkNewPassword(String p, String confirm) {
        if (p == null || p.length() < MIN_PASSWORD) return "Password must be at least " + MIN_PASSWORD + " characters";
        if (p.length() > 100) return "Password is too long";
        if (p.contains(",") || p.contains("\n") || p.contains("\r")) return "Password cannot contain commas or line breaks";
        if (!p.equals(confirm)) return "Passwords do not match";
        return null;
    }

    private static String checkEmail(String e) {          // e-mail is optional
        if (e == null || e.isEmpty()) return null;
        return (e.length() > 100 || !EMAIL.matcher(e).matches()) ? "Please enter a valid e-mail address" : null;
    }

    private static String checkAnswer(String a) {
        if (a == null || a.trim().length() < 2) return "Security answer must be at least 2 characters";
        return a.length() > 60 ? "Security answer is too long" : null;
    }

    /** "Blue  Cat " and "blue cat" count as the same answer. */
    private static String normalizeAnswer(String a) {
        return a == null ? "" : a.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    // ---------- login / reset attempt limit: 5 wrong tries lock that key for 5 minutes ----------
    private static final Map<String, long[]> FAILS = new HashMap<>();   // key -> {fails, lockedUntil}

    private static synchronized boolean isLocked(String key) {
        long[] s = FAILS.get(key);
        if (s == null || s[1] == 0) return false;
        if (System.currentTimeMillis() >= s[1]) {
            FAILS.remove(key);
            return false;
        }
        return true;
    }

    private static synchronized int minutesLeft(String key) {
        long[] s = FAILS.get(key);
        return (s == null || s[1] == 0) ? 0 : (int) Math.max(1, (s[1] - System.currentTimeMillis() + 59_999) / 60_000);
    }

    private static synchronized int fail(String key) {   // returns the tries left
        if (FAILS.size() > 5000) FAILS.clear();
        long[] s = FAILS.computeIfAbsent(key, k -> new long[2]);
        if (++s[0] >= 5) s[1] = System.currentTimeMillis() + 5 * 60 * 1000L;
        return (int) Math.max(0, 5 - s[0]);
    }

    private static synchronized void clearFails(String key) { FAILS.remove(key); }

    // ---------- CSRF: every POST form has a hidden "_csrf" field equal to the session token ----------
    /** Used inside the JSP forms. */
    public static String csrfToken(HttpSession session) {
        synchronized (session) {
            String t = (String) session.getAttribute("csrfToken");
            if (t == null) {
                byte[] b = new byte[24];
                RANDOM.nextBytes(b);
                t = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
                session.setAttribute("csrfToken", t);
            }
            return t;
        }
    }

    private static boolean csrfValid(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        String expected = s == null ? null : (String) s.getAttribute("csrfToken");
        String given = req.getParameter("_csrf");
        return expected != null && given != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), given.getBytes(StandardCharsets.UTF_8));
    }

    // ---------- small helpers for the JSP pages ----------
    /** Escapes text for HTML (body and attribute values) - stops XSS. */
    public static String esc(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&':  sb.append("&amp;");  break;
                case '<':  sb.append("&lt;");   break;
                case '>':  sb.append("&gt;");   break;
                case '"':  sb.append("&quot;"); break;
                case '\'': sb.append("&#39;");  break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String enc(String s) {
        try {
            return URLEncoder.encode(s == null ? "" : s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Bootstrap alert class for the "t" parameter: ok / err / anything else. */
    public static String alertClass(String type) {
        if ("ok".equals(type)) return "alert-success";
        if ("err".equals(type)) return "alert-danger";
        return "alert-warning";
    }

    /** Round avatar: the letter is always there, the photo (if any) is laid on top; onerror removes a broken photo. */
    public static String avatar(String contextPath, User u, String extraClass) {
        String n = u.getUsername();
        StringBuilder sb = new StringBuilder();
        sb.append("<span class=\"avatar ").append(esc(extraClass)).append("\" style=\"--h:")
                .append(Math.floorMod(n.hashCode(), 360)).append("\"><span class=\"avatar__ph\" aria-hidden=\"true\">")
                .append(esc(n.isEmpty() ? "?" : n.substring(0, 1).toUpperCase(Locale.ROOT))).append("</span>");
        if (u.hasAvatar()) {
            sb.append("<img src=\"").append(esc(contextPath)).append("/avatar?u=").append(enc(n))
                    .append("&amp;v=").append(esc(u.getAvatarFile()))
                    .append("\" alt=\"\" loading=\"lazy\" onerror=\"this.remove()\">");
        }
        return sb.append("</span>").toString();
    }
}
