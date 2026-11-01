package com.bookstore.util;

import com.bookstore.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** Reads the logged-in user (stored by LoginServlet as "loggedUser"). */
public final class SessionUtil {

    private SessionUtil() {
    }

    public static String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "guest";
        }
        Object user = session.getAttribute("loggedUser");
        if (user instanceof User) {
            return ((User) user).getUsername();
        }
        return "guest";
    }

    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object user = session.getAttribute("loggedUser");
        return user instanceof User && "admin".equalsIgnoreCase(((User) user).getRole());
    }
}

