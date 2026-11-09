package com.bookstore.servlet;

import com.bookstore.dao.PublisherDAO;
import com.bookstore.model.InternationalPublisher;
import com.bookstore.model.LocalPublisher;
import com.bookstore.model.Publisher;
import com.bookstore.model.PublisherFactory;
import com.bookstore.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;


@WebServlet("/publisher")
public class PublisherServlet extends HttpServlet {

    private static final String LIST_JSP = "/publisher-list.jsp";
    private static final String FORM_JSP = "/publisher-form.jsp";
    private static final String VIEW_JSP = "/publisher-view.jsp";

    private PublisherDAO dao;

    @Override
    public void init() {
        dao = new PublisherDAO();
    }

    // ================================================================ GET

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = trim(req.getParameter("action"));
        if (action.isEmpty()) {
            action = "list";
        }
        req.setAttribute("msg", req.getParameter("msg"));
        req.setAttribute("status", req.getParameter("status"));

        if (("new".equals(action) || "edit".equals(action)) && !SessionUtil.isAdmin(req)) {
            redirectWithMessage(resp, "Only admins can add or edit publishers.", false);
            return;
        }

        switch (action) {
            case "new":
                req.setAttribute("editing", false);
                req.getRequestDispatcher(FORM_JSP).forward(req, resp);
                break;
            case "edit":
                showEditForm(req, resp);
                break;
            case "view":
                showDetails(req, resp);
                break;
            default:
                showList(req, resp);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = trim(req.getParameter("q"));
        String type = trim(req.getParameter("type")).toUpperCase();
        if (!LocalPublisher.TYPE.equals(type) && !InternationalPublisher.TYPE.equals(type)) {
            type = "";
        }

        List<Publisher> all = dao.findAll();
        int localCount = 0;
        int internationalCount = 0;
        for (Publisher p : all) {
            if (LocalPublisher.TYPE.equals(p.getType())) {
                localCount++;
            } else if (InternationalPublisher.TYPE.equals(p.getType())) {
                internationalCount++;
            }
        }

        req.setAttribute("publishers", dao.search(keyword, type));
        req.setAttribute("searchText", keyword);
        req.setAttribute("typeFilter", type);
        req.setAttribute("totalCount", all.size());
        req.setAttribute("localCount", localCount);
        req.setAttribute("internationalCount", internationalCount);
        req.getRequestDispatcher(LIST_JSP).forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Publisher publisher = dao.findById(req.getParameter("id"));
        if (publisher == null) {
            redirectWithMessage(resp, "Publisher not found.", false);
            return;
        }
        req.setAttribute("publisher", publisher);
        req.setAttribute("editing", true);
        req.getRequestDispatcher(FORM_JSP).forward(req, resp);
    }

    private void showDetails(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Publisher publisher = dao.findById(req.getParameter("id"));
        if (publisher == null) {
            redirectWithMessage(resp, "Publisher not found.", false);
            return;
        }
        req.setAttribute("publisher", publisher);
        req.getRequestDispatcher(VIEW_JSP).forward(req, resp);
    }

    // ================================================================ POST

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if (!SessionUtil.isAdmin(req)) {
            redirectWithMessage(resp, "Only admins can change publishers.", false);
            return;
        }

        switch (trim(req.getParameter("action"))) {
            case "add":
                save(req, resp, false);
                break;
            case "update":
                save(req, resp, true);
                break;
            case "delete":
                boolean deleted = dao.delete(trim(req.getParameter("id")));
                redirectWithMessage(resp, deleted ? "Publisher deleted." : "Publisher not found.", deleted);
                break;
            default:
                resp.sendRedirect("publisher");
        }
    }

    /** Validation lives in the model; this method only reports the outcome. */
    private void save(HttpServletRequest req, HttpServletResponse resp, boolean isUpdate)
            throws ServletException, IOException {
        String id = trim(req.getParameter("id"));
        String name = trim(req.getParameter("name"));
        String phone = trim(req.getParameter("phone"));
        String type = trim(req.getParameter("type"));
        String extra = trim(req.getParameter("extra"));

        try {
            Publisher publisher = PublisherFactory.create(type, id, name, phone, extra);
            if (isUpdate) {
                boolean updated = dao.update(publisher);
                redirectWithMessage(resp, updated ? "Publisher updated." : "Publisher not found.", updated);
            } else if (dao.add(publisher)) {
                redirectWithMessage(resp, "Publisher added.", true);
            } else {
                showFormError(req, resp, isUpdate, "Publisher ID " + id + " is already in use. Choose a different ID.");
            }
        } catch (IllegalArgumentException e) {
            showFormError(req, resp, isUpdate, e.getMessage());
        }
    }

    /** Shows the form again with the error and everything the admin already typed. */
    private void showFormError(HttpServletRequest req, HttpServletResponse resp,
                               boolean isUpdate, String error) throws ServletException, IOException {
        req.setAttribute("editing", isUpdate);
        req.setAttribute("formError", error);
        req.setAttribute("formId", trim(req.getParameter("id")));
        req.setAttribute("formName", trim(req.getParameter("name")));
        req.setAttribute("formPhone", trim(req.getParameter("phone")));
        req.setAttribute("formType", trim(req.getParameter("type")).toUpperCase());
        req.setAttribute("formExtra", trim(req.getParameter("extra")));
        req.getRequestDispatcher(FORM_JSP).forward(req, resp);
    }

    // ================================================================ helpers

    private void redirectWithMessage(HttpServletResponse resp, String message, boolean success)
            throws IOException {
        resp.sendRedirect("publisher?msg=" + URLEncoder.encode(message, "UTF-8")
                + "&status=" + (success ? "success" : "error"));
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}