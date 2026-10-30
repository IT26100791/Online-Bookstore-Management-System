package com.bookstore.util;

import com.bookstore.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Reads the logged-in user from the session.
 * It expects LoginServlet to store either a User object under "user" or the name under "username".
 * If LoginServlet uses different names, change the two attribute names below.
 */
public final class SessionUtil {

    private SessionUtil() {
    }

    public static String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "guest";
        }
        Object user = session.getAttribute("user");
        if (user instanceof User) {
            return ((User) user).getUsername();
        }
        Object name = session.getAttribute("username");
        return name != null ? name.toString() : "guest";
    }

    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object user = session.getAttribute("user");
        if (user instanceof User) {
            return "admin".equalsIgnoreCase(((User) user).getRole());
        }
        Object role = session.getAttribute("role");
        return role != null && "admin".equalsIgnoreCase(role.toString());
    }
}

